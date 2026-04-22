package CONTROLADOR;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CifradoBBDD {

    private static final String CLAVE_SECRETA = "PinguOreoClaveBD"; 
    private static final int MASCARA_NUMERICA = 0x5A5A; 

    public static String encriptarTexto(String textoPlano) {
        if (textoPlano == null) return null;
        try {
            SecretKeySpec secretKey = new SecretKeySpec(CLAVE_SECRETA.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] bytesCifrados = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytesCifrados);
        } catch (Exception e) {
            return textoPlano; 
        }
    }

    public static String desencriptarTexto(String textoCifrado) {
        if (textoCifrado == null || textoCifrado.trim().isEmpty()) return textoCifrado;
        try {
            SecretKeySpec secretKey = new SecretKeySpec(CLAVE_SECRETA.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodificado = Base64.getDecoder().decode(textoCifrado);
            byte[] bytesDesencriptados = cipher.doFinal(decodificado);
            return new String(bytesDesencriptados, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return textoCifrado;
        }
    }

    public static int encriptarNumero(int numero) {
        return numero ^ MASCARA_NUMERICA;
    }

    public static int desencriptarNumero(int numeroOfuscado) {
        if (numeroOfuscado < 500) { 
            return numeroOfuscado; 
        }
        return numeroOfuscado ^ MASCARA_NUMERICA;
    }
}
