package CONTROLADOR;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CifradoBBDD {

    // Clave secreta fija de 16 bytes (128 bits) para AES
    private static final String CLAVE_SECRETA = "PinguOreoClaveBD"; // 16 caracteres exactos
    // Entero usado como máscara para hacer un XOR a los números
    private static final int MASCARA_NUMERICA = 0x5A5A; // 23130 en decimal

    /**
     * Encripta un texto usando AES.
     * @param textoPlano Texto sin cifrar.
     * @return Texto cifrado en Base64, o el texto original si hay un error.
     */
    public static String encriptarTexto(String textoPlano) {
        if (textoPlano == null) return null;
        try {
            SecretKeySpec secretKey = new SecretKeySpec(CLAVE_SECRETA.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] bytesCifrados = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytesCifrados);
        } catch (Exception e) {
            System.err.println("Error al encriptar: " + e.getMessage());
            return textoPlano; 
        }
    }

    /**
     * Desencripta un texto en Base64 usando AES.
     * Soporta valores no cifrados (los devuelve tal cual) para compatibilidad.
     * @param textoCifrado Texto cifrado en Base64.
     * @return Texto original desencriptado.
     */
    public static String desencriptarTexto(String textoCifrado) {
        if (textoCifrado == null || textoCifrado.trim().isEmpty()) return textoCifrado;
        
        try {
            // Intentamos desencriptar
            SecretKeySpec secretKey = new SecretKeySpec(CLAVE_SECRETA.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            // Si el texto no es Base64 valido, esta linea lanzará excepción y caerá en el catch
            byte[] decodificado = Base64.getDecoder().decode(textoCifrado);
            
            byte[] bytesDesencriptados = cipher.doFinal(decodificado);
            return new String(bytesDesencriptados, StandardCharsets.UTF_8);
            
        } catch (IllegalArgumentException e) {
            // No es Base64, asumimos que es texto plano de una partida antigua guardada sin encriptar
            return textoCifrado;
        } catch (Exception e) {
            // Cualquier otro error de cifrado (por ejemplo Padding incorrecto), también devolvemos el original
            return textoCifrado;
        }
    }

    /**
     * Encripta (ofusca) un número entero usando la operación bit a bit XOR combinada con una suma.
     * Garantiza que los números en BD cambien drásticamente.
     * @param numero Número original
     * @return Número ofuscado
     */
    public static int encriptarNumero(int numero) {
        // Encriptar: (numero XOR mascara)
        return numero ^ MASCARA_NUMERICA;
    }

    /**
     * Desencripta (restaura) un número entero.
     * @param numeroOfuscado Número ofuscado de la BD
     * @return Número original
     */
    public static int desencriptarNumero(int numeroOfuscado) {
        
        // Forma superficial de intentar compatibilidad con partidas antiguas
        // (los números de posiciones reales no suelen pasar del 100, pero los ofuscados superan 20000)
        if (numeroOfuscado < MASCARA_NUMERICA / 2) {
            return numeroOfuscado; // Probablemente no está encriptado (partida muy antigua)
        }
        
        // Desencriptar: (numeroOfuscado XOR mascara)
        return numeroOfuscado ^ MASCARA_NUMERICA;
    }

}
