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

    // ============================================================
    //  GUARDAR PARTIDA (INSERT si nueva, UPDATE si ya existe)
    // ============================================================

    public int guardarBBDD(Partida p) {
        if (p.getId() > 0) {
            return actualizarPartida(p);
        } else {
            return insertarPartida(p);
        }
    }

    // ============================================================
    //  INSERT: Crear partida nueva
    // ============================================================

    private int insertarPartida(Partida p) {
        try (Connection con = getConexion()) {
            con.setAutoCommit(false);

            try {
                // 1. INSERT PARTIDA
                String sqlPartida = "INSERT INTO PARTIDA (TURNOS, JUGADOR_ACTUAL, FINALIZADA, NOMBRE_PARTIDA, FECHA_GUARDADO) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
                int idPartida;

                try (PreparedStatement ps = con.prepareStatement(sqlPartida, new String[]{"ID_PARTIDA"})) {
                    ps.setInt(1, CifradoBBDD.encriptarNumero(p.getTurnos()));
                    ps.setInt(2, CifradoBBDD.encriptarNumero(p.getJugadorActualIndice()));
                    ps.setInt(3, p.isFinalizada() ? 1 : 0); // NUMBER(1) - no se encripta
                    ps.setString(4, CifradoBBDD.encriptarTexto(p.getNombrePartida()));
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        idPartida = keys.getInt(1);
                    }
                }

                // 2. INSERT JUGADORES
                insertarJugadores(con, p, idPartida);

                // 3. INSERT CASILLAS
                insertarCasillas(con, p, idPartida);

                // COMMIT
                con.commit();
                p.setId(idPartida);
                System.out.println("Partida NUEVA guardada con ID: " + idPartida);
                return idPartida;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error al guardar partida: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    // ============================================================
    //  UPDATE: Sobrescribir partida existente
    // ============================================================

    private int actualizarPartida(Partida p) {
        try (Connection con = getConexion()) {
            con.setAutoCommit(false);

            try {
                int idPartida = p.getId();

                // 1. UPDATE PARTIDA
                String sqlUpdate = "UPDATE PARTIDA SET TURNOS = ?, JUGADOR_ACTUAL = ?, FINALIZADA = ?, NOMBRE_PARTIDA = ?, FECHA_GUARDADO = CURRENT_TIMESTAMP WHERE ID_PARTIDA = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                    ps.setInt(1, CifradoBBDD.encriptarNumero(p.getTurnos()));
                    ps.setInt(2, CifradoBBDD.encriptarNumero(p.getJugadorActualIndice()));
                    ps.setInt(3, p.isFinalizada() ? 1 : 0);
                    ps.setString(4, CifradoBBDD.encriptarTexto(p.getNombrePartida()));
                    ps.setInt(5, idPartida);
                    ps.executeUpdate();
                }

                // 2. BORRAR jugadores antiguos (CASCADE borra inventarios)
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM JUGADOR WHERE ID_PARTIDA = ?")) {
                    ps.setInt(1, idPartida);
                    ps.executeUpdate();
                }

                // 3. RE-INSERTAR jugadores con estado actual
                insertarJugadores(con, p, idPartida);

                // 4. BORRAR y re-insertar casillas
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM CASILLA_TABLERO WHERE ID_PARTIDA = ?")) {
                    ps.setInt(1, idPartida);
                    ps.executeUpdate();
                }
                insertarCasillas(con, p, idPartida);

                // COMMIT
                con.commit();
                System.out.println("Partida ID " + idPartida + " ACTUALIZADA correctamente.");
                return idPartida;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error al actualizar partida: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    // ============================================================
    //  METODOS AUXILIARES INSERT
    // ============================================================

    private void insertarJugadores(Connection con, Partida p, int idPartida) throws SQLException {
        String sqlJugador = "INSERT INTO JUGADOR (ID_PARTIDA, INDICE_ORDEN, NOMBRE, COLOR, POSICION, TIPO, TURNOS_PERDIDOS, TURNOS_BLOQUEADA) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        for (int i = 0; i < p.getJugadores().size(); i++) {
            Jugador j = p.getJugadores().get(i);
            int idJugador;

            try (PreparedStatement ps = con.prepareStatement(sqlJugador, new String[]{"ID_JUGADOR"})) {
                ps.setInt(1, idPartida);
                ps.setInt(2, CifradoBBDD.encriptarNumero(i));
                ps.setString(3, CifradoBBDD.encriptarTexto(j.getNombre()));
                ps.setString(4, CifradoBBDD.encriptarTexto(j.getColor()));
                ps.setInt(5, CifradoBBDD.encriptarNumero(j.getPosicion()));
                ps.setString(6, CifradoBBDD.encriptarTexto(j instanceof Pinguino ? "PINGUINO" : "FOCA"));
                ps.setInt(7, CifradoBBDD.encriptarNumero(j.getTurnosPerdidos()));
                ps.setInt(8, CifradoBBDD.encriptarNumero(j instanceof Foca ? ((Foca) j).getTurnosBloqueada() : 0));
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    idJugador = keys.getInt(1);
                }
            }

            // INSERT items del inventario (solo Pinguinos)
            if (j instanceof Pinguino) {
                Pinguino ping = (Pinguino) j;
                String sqlItem = "INSERT INTO INVENTARIO_ITEM (ID_JUGADOR, TIPO_ITEM, CANTIDAD) VALUES (?, ?, ?)";

                for (Item item : ping.getInventario().getlista()) {
                    try (PreparedStatement psItem = con.prepareStatement(sqlItem)) {
                        psItem.setInt(1, idJugador);
                        psItem.setString(2, CifradoBBDD.encriptarTexto(item.getNombre()));
                        psItem.setInt(3, CifradoBBDD.encriptarNumero(item.getCantidad()));
                        psItem.executeUpdate();
                    }
                }
            }
        }
    }

    private void insertarCasillas(Connection con, Partida p, int idPartida) throws SQLException {
        String sqlCasilla = "INSERT INTO CASILLA_TABLERO (ID_PARTIDA, POSICION, TIPO) VALUES (?, ?, ?)";

        for (Casilla c : p.getTablero().getListaCasillas()) {
            try (PreparedStatement ps = con.prepareStatement(sqlCasilla)) {
                ps.setInt(1, idPartida);
                ps.setInt(2, CifradoBBDD.encriptarNumero(c.getPosicion()));
                ps.setString(3, CifradoBBDD.encriptarTexto(c.getClass().getSimpleName()));
                ps.executeUpdate();
            }
        }
    }

    // ============================================================
    //  CARGAR PARTIDA COMPLETA
    // ============================================================

    public Partida cargarBBDD(int id) {
        try (Connection con = getConexion()) {

            // 1. CARGAR DATOS DE LA PARTIDA
            int turnos, jugadorActual, finalizada;
            String nombrePartida = "";

            try (PreparedStatement ps = con.prepareStatement("SELECT TURNOS, JUGADOR_ACTUAL, FINALIZADA, NOMBRE_PARTIDA FROM PARTIDA WHERE ID_PARTIDA = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("No se encontro partida con ID: " + id);
                        return null;
                    }
                    turnos = CifradoBBDD.desencriptarNumero(rs.getInt("TURNOS"));
                    jugadorActual = CifradoBBDD.desencriptarNumero(rs.getInt("JUGADOR_ACTUAL"));
                    finalizada = rs.getInt("FINALIZADA");
                    nombrePartida = CifradoBBDD.desencriptarTexto(rs.getString("NOMBRE_PARTIDA"));
                }
            }

            // 2. CARGAR CASILLAS DEL TABLERO
            ArrayList<Casilla> casillas = new ArrayList<>();

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT POSICION, TIPO FROM CASILLA_TABLERO WHERE ID_PARTIDA = ? ORDER BY POSICION")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int pos = CifradoBBDD.desencriptarNumero(rs.getInt("POSICION"));
                        String tipo = CifradoBBDD.desencriptarTexto(rs.getString("TIPO"));
                        casillas.add(crearCasillaPorTipo(pos, tipo));
                    }
                }
            }

            // 3. CARGAR JUGADORES
            ArrayList<Jugador> jugadores = new ArrayList<>();

            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM JUGADOR WHERE ID_PARTIDA = ? ORDER BY INDICE_ORDEN")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int idJugador = rs.getInt("ID_JUGADOR");
                        String nombre = CifradoBBDD.desencriptarTexto(rs.getString("NOMBRE"));
                        String color = CifradoBBDD.desencriptarTexto(rs.getString("COLOR"));
                        int posicion = CifradoBBDD.desencriptarNumero(rs.getInt("POSICION"));
                        String tipo = CifradoBBDD.desencriptarTexto(rs.getString("TIPO"));
                        int turnosPerdidos = CifradoBBDD.desencriptarNumero(rs.getInt("TURNOS_PERDIDOS"));

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
                                        String tipoItem = CifradoBBDD.desencriptarTexto(rsItem.getString("TIPO_ITEM"));
                                        int cantidad = CifradoBBDD.desencriptarNumero(rsItem.getInt("CANTIDAD"));
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
                            f.setTurnosBloqueada(CifradoBBDD.desencriptarNumero(rs.getInt("TURNOS_BLOQUEADA")));
                            j = f;
                        }

                        jugadores.add(j);
                    }
                }
            }

            // 4. CONSTRUIR OBJETO PARTIDA
            Partida partida = new Partida();
            partida.setId(id);
            partida.setNombrePartida(nombrePartida);
            partida.setTurnos(turnos);
            partida.setJugadorActualIndice(jugadorActual);
            partida.setFinalizada(finalizada == 1);
            partida.getTablero().setListaCasillas(casillas);
            partida.setJugadores(jugadores);

            System.out.println("Partida ID " + id + " cargada correctamente.");
            System.out.println("  Turnos: " + turnos + " | Jugador actual: " + jugadorActual + " | Jugadores: " + jugadores.size());
            for (Jugador j : jugadores) {
                System.out.println("  -> " + j.getNombre() + " | Pos: " + j.getPosicion() + " | Tipo: " + j.getClass().getSimpleName());
            }
            return partida;

        } catch (SQLException e) {
            System.out.println("Error al cargar partida: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ============================================================
    //  LISTAR PARTIDAS GUARDADAS
    // ============================================================

    public ArrayList<String[]> listarPartidas() {
        ArrayList<String[]> lista = new ArrayList<>();

        try (Connection con = getConexion()) {
            String sql = "SELECT p.ID_PARTIDA, p.TURNOS, p.FINALIZADA, p.NOMBRE_PARTIDA, p.FECHA_GUARDADO, " +
                         "(SELECT COUNT(*) FROM JUGADOR j WHERE j.ID_PARTIDA = p.ID_PARTIDA) AS NUM_JUGADORES " +
                         "FROM PARTIDA p ORDER BY p.ID_PARTIDA DESC";

            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                while (rs.next()) {
                    String[] info = new String[2];
                    info[0] = String.valueOf(rs.getInt("ID_PARTIDA"));

                    int numTurnos = CifradoBBDD.desencriptarNumero(rs.getInt("TURNOS"));
                    int numJugadores = rs.getInt("NUM_JUGADORES"); // COUNT no está encriptado
                    int finalizada = rs.getInt("FINALIZADA");
                    
                    String nombreReal = CifradoBBDD.desencriptarTexto(rs.getString("NOMBRE_PARTIDA"));
                    if (nombreReal == null || nombreReal.trim().isEmpty()) {
                        nombreReal = "Partida #" + info[0]; // Fallback partidas antiguas
                    }

                    Timestamp fechaGuardado = rs.getTimestamp("FECHA_GUARDADO");
                    String fechaStr = fechaGuardado != null ? sdf.format(fechaGuardado) : "Fecha desconocida";

                    info[1] = "🎮 " + nombreReal + "  |  🕒 " + fechaStr + "  |  👥 " + numJugadores + " Jugadores";

                    lista.add(info);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al listar partidas: " + e.getMessage());
        }

        return lista;
    }

    // ============================================================
    //  ELIMINAR PARTIDA
    // ============================================================

    public boolean eliminarPartida(int id) {
        try (Connection con = getConexion()) {
            String sql = "DELETE FROM PARTIDA WHERE ID_PARTIDA = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                int filas = ps.executeUpdate();
                if (filas > 0) {
                    System.out.println("Partida ID " + id + " eliminada.");
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

    // ============================================================
    //  FABRICAS DE OBJETOS
    // ============================================================

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