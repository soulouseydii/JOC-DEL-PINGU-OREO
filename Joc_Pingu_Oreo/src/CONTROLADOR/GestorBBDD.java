package CONTROLADOR;

import MODELO.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GestorBBDD {

    private String urlBBDD;
    private String username;
    private String password;

    public String getUrlBBDD() {
        return urlBBDD;
    }

    public void setUrlBBDD(String urlBBDD) {
        this.urlBBDD = urlBBDD;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void guardarBBDD(Partida p) {
        // Se utiliza try-with-resources para asegurar que la conexion se cierra automaticamente
        try (Connection con = DriverManager.getConnection(this.urlBBDD, this.username, this.password)) {
            
            // Sentencia SQL preparada (Sustituir por los campos y tablas reales de la base de datos)
            String sql = "INSERT INTO PARTIDA (TURNOS, FINALIZADA) VALUES (?, ?)";
            
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, p.getTurnos());
                // En Oracle, los booleanos suelen guardarse como 1/0 o 'Y'/'N'
                pstmt.setInt(2, p.isFinalizada() ? 1 : 0); 
                
                pstmt.executeUpdate();
                System.out.println("Partida guardada correctamente en la base de datos.");
            }
            
        } catch (SQLException e) {
            System.out.println("Error al guardar la partida en BBDD: " + e.getMessage());
        }
    }

    public Partida cargarBBDD(int id) {
        Partida partidaCargada = null;

        try (Connection con = DriverManager.getConnection(this.urlBBDD, this.username, this.password)) {
            
            // Sentencia SQL de busqueda (Sustituir por los nombres reales)
            String sql = "SELECT * FROM PARTIDA WHERE ID_PARTIDA = ?";
            
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        partidaCargada = new Partida();
                        
                        // Configuramos el objeto Partida con los datos devueltos por la base de datos
                        partidaCargada.setTurnos(rs.getInt("TURNOS"));
                        partidaCargada.setFinalizada(rs.getInt("FINALIZADA") == 1);
                        
                        System.out.println("Partida cargada correctamente desde la base de datos.");
                    } else {
                        System.out.println("No se ha encontrado ninguna partida con el ID proporcionado.");
                    }
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error al cargar la partida desde BBDD: " + e.getMessage());
        }

        return partidaCargada;
    }
}