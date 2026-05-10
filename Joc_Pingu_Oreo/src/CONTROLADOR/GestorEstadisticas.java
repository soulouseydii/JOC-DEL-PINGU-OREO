package CONTROLADOR;

import java.sql.*;
import java.util.ArrayList;

public class GestorEstadisticas {

    private final String urlBBDD;
    private final String username;
    private final String password;

    public GestorEstadisticas() {
        this.urlBBDD = Main.DB_URL;
        this.username = Main.DB_USER;
        this.password = Main.DB_PASS;
    }

    private Connection getConexion() throws SQLException {
        return DriverManager.getConnection(this.urlBBDD, this.username, this.password);
    }

    // Comprueba si la conexion funciona
    public boolean testConexion() {
        try (Connection con = getConexion()) {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Llama a la funcion MAX_PARTIDASGANADAS()
    // Si no existe en Oracle, usa MAX() directamente
    public int obtenerMaxPartidasGanadas() {
        try (Connection con = getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT MAX_PARTIDASGANADAS() FROM DUAL")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("[Estadisticas] Error MAX_PARTIDASGANADAS: " + e.getMessage());
            // Fallback: SQL directo mientras no exista la funcion en Oracle
            try (Connection con = getConexion();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT NVL(MAX(PARTIDAS_GANADAS), 0) FROM USUARIOS")) {
                if (rs.next()) return rs.getInt(1);
            } catch (SQLException ex) {
                System.out.println("[Estadisticas] Error al obtener el record: " + ex.getMessage());
            }
        }
        return -1;
    }

    // Equivalente al procedimiento OBTENIR_JUGADORS_RECORD
    // Busca los usuarios que tienen el maximo de partidas ganadas usando la funcion PL/SQL
    public ArrayList<String[]> obtenerJugadoresConRecord() {
        ArrayList<String[]> lista = new ArrayList<>();
        // Intentamos usar la funcion PL/SQL en la clausula WHERE
        String sql = "SELECT USERNAME, PARTIDAS_GANADAS FROM USUARIOS WHERE PARTIDAS_GANADAS = (SELECT MAX_PARTIDASGANADAS() FROM DUAL)";
        
        try (Connection con = getConexion();
             Statement st = con.createStatement()) {
            
            try (ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    lista.add(new String[]{
                        rs.getString("USERNAME"),
                        String.valueOf(rs.getInt("PARTIDAS_GANADAS"))
                    });
                }
            } catch (SQLException e) {
                System.out.println("[Estadisticas] Error al usar MAX_PARTIDASGANADAS(), usando fallback: " + e.getMessage());
                // Fallback: SQL directo
                String fallback = "SELECT USERNAME, PARTIDAS_GANADAS FROM USUARIOS WHERE PARTIDAS_GANADAS = (SELECT NVL(MAX(PARTIDAS_GANADAS), 0) FROM USUARIOS)";
                try (ResultSet rs = st.executeQuery(fallback)) {
                    while (rs.next()) {
                        lista.add(new String[]{
                            rs.getString("USERNAME"),
                            String.valueOf(rs.getInt("PARTIDAS_GANADAS"))
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("[Estadisticas] Error critico en obtenerJugadoresConRecord: " + e.getMessage());
        }
        return lista;
    }

    // Llama a la funcion avg_partides_guanyades()
    public double obtenerMediaPartidasGanadas() {
        try (Connection con = getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT avg_partides_guanyades() FROM DUAL")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.out.println("[Estadisticas] Error avg_partides_guanyades: " + e.getMessage());
            // Fallback
            try (Connection con = getConexion();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT NVL(AVG(PARTIDAS_GANADAS), 0) FROM USUARIOS")) {
                if (rs.next()) return rs.getDouble(1);
            } catch (SQLException ex) {
                System.out.println("[Estadisticas] Error en fallback de media: " + ex.getMessage());
            }
        }
        return -1;
    }

    // Equivalente al procedimiento LISTA_JGANADORES
    // Devuelve los jugadores con mas victorias que la media usando la funcion PL/SQL
    public ArrayList<String[]> obtenerJugadoresPorEncimaMedia() {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT USERNAME, PARTIDAS_GANADAS FROM USUARIOS " +
                     "WHERE PARTIDAS_GANADAS > (SELECT avg_partides_guanyades() FROM DUAL) " +
                     "ORDER BY PARTIDAS_GANADAS DESC";
        
        try (Connection con = getConexion();
             Statement st = con.createStatement()) {
            
            try (ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    lista.add(new String[]{
                        rs.getString("USERNAME"),
                        String.valueOf(rs.getInt("PARTIDAS_GANADAS"))
                    });
                }
            } catch (SQLException e) {
                System.out.println("[Estadisticas] Error al usar avg_partides_guanyades(), usando fallback: " + e.getMessage());
                // Fallback: SQL directo
                String fallback = "SELECT USERNAME, PARTIDAS_GANADAS FROM USUARIOS " +
                                 "WHERE PARTIDAS_GANADAS > (SELECT NVL(AVG(PARTIDAS_GANADAS), 0) FROM USUARIOS) " +
                                 "ORDER BY PARTIDAS_GANADAS DESC";
                try (ResultSet rs = st.executeQuery(fallback)) {
                    while (rs.next()) {
                        lista.add(new String[]{
                            rs.getString("USERNAME"),
                            String.valueOf(rs.getInt("PARTIDAS_GANADAS"))
                        });
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("[Estadisticas] Error critico en obtenerJugadoresPorEncimaMedia: " + e.getMessage());
        }
        return lista;
    }

    // Ranking completo ordenado por victorias
    public ArrayList<String[]> obtenerRankingCompleto() {
        ArrayList<String[]> lista = new ArrayList<>();
        String sql = "SELECT USERNAME, PARTIDAS_GANADAS FROM USUARIOS ORDER BY PARTIDAS_GANADAS DESC";
        try (Connection con = getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int pos = 1;
            while (rs.next()) {
                lista.add(new String[]{
                    String.valueOf(pos++),
                    rs.getString("USERNAME"),
                    String.valueOf(rs.getInt("PARTIDAS_GANADAS"))
                });
            }
        } catch (SQLException e) {
            System.out.println("[Estadisticas] Error Ranking: " + e.getMessage());
        }
        return lista;
    }
}
