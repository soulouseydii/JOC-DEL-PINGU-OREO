package CONTROLADOR;

import MODELO.*;
import java.sql.*;
import java.util.ArrayList;

public class GestorBBDD {

    private String urlBBDD;
    private String username;
    private String password;

    public GestorBBDD() {
        this.urlBBDD = Main.DB_URL;
        this.username = Main.DB_USER;
        this.password = Main.DB_PASS;
    }

    public String getUrlBBDD() { return urlBBDD; }
    public void setUrlBBDD(String urlBBDD) { this.urlBBDD = urlBBDD; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    private Connection getConexion() throws SQLException {
        return DriverManager.getConnection(this.urlBBDD, this.username, this.password);
    }

    public boolean testConexion() {
        System.out.println("Probando conexion a: " + this.urlBBDD);
        try (Connection con = getConexion()) {
            if (con != null && !con.isClosed()) {
                System.out.println("CONEXION EXITOSA a Oracle! Base de datos lista.");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("ERROR DE CONEXION: " + e.getMessage());
            System.out.println("Codigo de error Oracle: " + e.getErrorCode());
        }
        return false;
    }

    public int guardarBBDD(Partida p) {
        try (Connection con = getConexion()) {
            con.setAutoCommit(false);

            try {
                // ----- 1. INSERT EN PARTIDA -----
                String sqlPartida = "INSERT INTO PARTIDA (TURNOS, JUGADOR_ACTUAL, FINALIZADA) VALUES (?, ?, ?)";
                int idPartida;

                try (PreparedStatement ps = con.prepareStatement(sqlPartida, new String[]{"ID_PARTIDA"})) {
                    ps.setInt(1, p.getTurnos());
                    ps.setInt(2, p.getJugadorActualIndice());
                    ps.setInt(3, p.isFinalizada() ? 1 : 0);
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        idPartida = keys.getInt(1);
                    }
                }

                // ----- 2. INSERT JUGADORES -----
                String sqlJugador = "INSERT INTO JUGADOR (ID_PARTIDA, INDICE_ORDEN, NOMBRE, COLOR, POSICION, TIPO, TURNOS_PERDIDOS, TURNOS_BLOQUEADA) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                for (int i = 0; i < p.getJugadores().size(); i++) {
                    Jugador j = p.getJugadores().get(i);
                    int idJugador;

                    try (PreparedStatement ps = con.prepareStatement(sqlJugador, new String[]{"ID_JUGADOR"})) {
                        ps.setInt(1, idPartida);
                        ps.setInt(2, i);
                        ps.setString(3, j.getNombre());
                        ps.setString(4, j.getColor());
                        ps.setInt(5, j.getPosicion());
                        ps.setString(6, j instanceof Pinguino ? "PINGUINO" : "FOCA");
                        ps.setInt(7, j.getTurnosPerdidos());
                        ps.setInt(8, j instanceof Foca ? ((Foca) j).getTurnosBloqueada() : 0);
                        ps.executeUpdate();

                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            keys.next();
                            idJugador = keys.getInt(1);
                        }
                    }

                    // ----- 3. INSERT ITEMS DEL INVENTARIO (solo Pinguinos) -----
                    if (j instanceof Pinguino) {
                        Pinguino ping = (Pinguino) j;
                        String sqlItem = "INSERT INTO INVENTARIO_ITEM (ID_JUGADOR, TIPO_ITEM, CANTIDAD) VALUES (?, ?, ?)";

                        for (Item item : ping.getInventario().getlista()) {
                            try (PreparedStatement psItem = con.prepareStatement(sqlItem)) {
                                psItem.setInt(1, idJugador);
                                psItem.setString(2, item.getNombre());
                                psItem.setInt(3, item.getCantidad());
                                psItem.executeUpdate();
                            }
                        }
                    }
                }

                // ----- 4. INSERT CASILLAS DEL TABLERO -----
                String sqlCasilla = "INSERT INTO CASILLA_TABLERO (ID_PARTIDA, POSICION, TIPO) VALUES (?, ?, ?)";

                for (Casilla c : p.getTablero().getListaCasillas()) {
                    try (PreparedStatement ps = con.prepareStatement(sqlCasilla)) {
                        ps.setInt(1, idPartida);
                        ps.setInt(2, c.getPosicion());
                        ps.setString(3, c.getClass().getSimpleName());
                        ps.executeUpdate();
                    }
                }

                // ----- COMMIT -----
                con.commit();
                p.setId(idPartida);
                System.out.println("Partida guardada correctamente en la BBDD con ID: " + idPartida);
                return idPartida;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error al guardar la partida en BBDD: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    //  CARGAR PARTIDA COMPLETA

    public Partida cargarBBDD(int id) {
        try (Connection con = getConexion()) {

            // ----- 1. CARGAR DATOS DE LA PARTIDA -----
            int turnos, jugadorActual, finalizada;

            try (PreparedStatement ps = con.prepareStatement("SELECT TURNOS, JUGADOR_ACTUAL, FINALIZADA FROM PARTIDA WHERE ID_PARTIDA = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No se encontro ninguna partida con ID: " + id);
                        return null;
                    }
                    turnos = rs.getInt("TURNOS");
                    jugadorActual = rs.getInt("JUGADOR_ACTUAL");
                    finalizada = rs.getInt("FINALIZADA");
                }
            }

            // ----- 2. CARGAR CASILLAS DEL TABLERO -----
            ArrayList<Casilla> casillas = new ArrayList<>();

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT POSICION, TIPO FROM CASILLA_TABLERO WHERE ID_PARTIDA = ? ORDER BY POSICION")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int pos = rs.getInt("POSICION");
                        String tipo = rs.getString("TIPO");
                        casillas.add(crearCasillaPorTipo(pos, tipo));
                    }
                }
            }

            // ----- 3. CARGAR JUGADORES -----
            ArrayList<Jugador> jugadores = new ArrayList<>();

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM JUGADOR WHERE ID_PARTIDA = ? ORDER BY INDICE_ORDEN")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int idJugador = rs.getInt("ID_JUGADOR");
                        String nombre = rs.getString("NOMBRE");
                        String color = rs.getString("COLOR");
                        int posicion = rs.getInt("POSICION");
                        String tipo = rs.getString("TIPO");
                        int turnosPerdidos = rs.getInt("TURNOS_PERDIDOS");

                        Jugador j;

                        if (tipo.equals("PINGUINO")) {
                            Inventario inv = new Inventario();
                            Pinguino p = new Pinguino(nombre, color, 0, inv);
                            p.setPosicion(posicion);
                            p.setTurnosPerdidos(turnosPerdidos);

                            // Cargar items del inventario
                            try (PreparedStatement psItem = con.prepareStatement(
                                    "SELECT TIPO_ITEM, CANTIDAD FROM INVENTARIO_ITEM WHERE ID_JUGADOR = ?")) {
                                psItem.setInt(1, idJugador);
                                try (ResultSet rsItem = psItem.executeQuery()) {
                                    while (rsItem.next()) {
                                        String tipoItem = rsItem.getString("TIPO_ITEM");
                                        int cantidad = rsItem.getInt("CANTIDAD");
                                        Item item = crearItemPorTipo(tipoItem);
                                        if (item != null) {
                                            item.setCantidad(cantidad);
                                            inv.getlista().add(item);
                                        }
                                    }
                                }
                            }

                            j = p;

                        } else {
                            Foca f = new Foca(nombre, color, 0);
                            f.setPosicion(posicion);
                            f.setTurnosPerdidos(turnosPerdidos);
                            f.setTurnosBloqueada(rs.getInt("TURNOS_BLOQUEADA"));
                            j = f;
                        }

                        jugadores.add(j);
                    }
                }
            }

            // ----- 4. CONSTRUIR OBJETO PARTIDA -----
            Partida partida = new Partida();
            partida.setId(id);
            partida.setTurnos(turnos);
            partida.setJugadorActualIndice(jugadorActual);
            partida.setFinalizada(finalizada == 1);
            partida.getTablero().setListaCasillas(casillas);
            partida.setJugadores(jugadores);

            System.out.println("Partida ID " + id + " cargada correctamente desde la BBDD.");
            return partida;

        } catch (SQLException e) {
            System.out.println("Error al cargar la partida desde BBDD: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    //  LISTAR PARTIDAS GUARDADAS

    public ArrayList<String[]> listarPartidas() {
        ArrayList<String[]> lista = new ArrayList<>();

        try (Connection con = getConexion()) {
            String sql = "SELECT p.ID_PARTIDA, p.TURNOS, p.FINALIZADA, p.FECHA_GUARDADO, " +
                         "(SELECT COUNT(*) FROM JUGADOR j WHERE j.ID_PARTIDA = p.ID_PARTIDA) AS NUM_JUGADORES " +
                         "FROM PARTIDA p ORDER BY p.FECHA_GUARDADO DESC";

            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    String[] info = new String[2];
                    info[0] = String.valueOf(rs.getInt("ID_PARTIDA"));

                    int numTurnos = rs.getInt("TURNOS");
                    int numJugadores = rs.getInt("NUM_JUGADORES");
                    int finalizada = rs.getInt("FINALIZADA");
                    String fecha = rs.getString("FECHA_GUARDADO");

                    // Limpiar la fecha para mostrarla bonita
                    if (fecha != null && fecha.length() > 19) {
                        fecha = fecha.substring(0, 19);
                    }

                    String estado = finalizada == 1 ? "Finalizada" : "En curso";

                    info[1] = "Partida #" + info[0] + "  |  " + numJugadores + " jugadores  |  " +
                              numTurnos + " turnos  |  " + estado + "  |  " + fecha;

                    lista.add(info);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar partidas: " + e.getMessage());
        }

        return lista;
    }

    //  ELIMINAR PARTIDA

    public boolean eliminarPartida(int id) {
        try (Connection con = getConexion()) {
            String sql = "DELETE FROM PARTIDA WHERE ID_PARTIDA = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                int filas = ps.executeUpdate();
                if (filas > 0) {
                    System.out.println("Partida ID " + id + " eliminada de la BBDD.");
                    return true;
                } else {
                    System.out.println("No se encontro partida con ID: " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar partida: " + e.getMessage());
        }
        return false;
    }

    private Casilla crearCasillaPorTipo(int posicion, String tipo) {
        switch (tipo) {
            case "Oso":             return new Oso(posicion);
            case "Trineo":          return new Trineo(posicion);
            case "Agujero":         return new Agujero(posicion);
            case "Evento":          return new Evento(posicion);
            case "SueloQuebradizo": return new SueloQuebradizo(posicion);
            default:                return new Normal(posicion);
        }
    }


    private Item crearItemPorTipo(String tipoItem) {
        switch (tipoItem) {
            case "Dado":            return new Dado("Normal");
            case "Pez":             return new Pez();
            case "Bola de Nieve":   return new bolaDeNieve();
            case "Moto de Nieve":   return new MotoDeNieve();
            default:
                System.out.println("Tipo de item desconocido: " + tipoItem);
                return null;
        }
    }
}