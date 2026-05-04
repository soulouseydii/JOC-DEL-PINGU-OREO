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
        try (Connection con = getConexion()) {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int guardarBBDD(Partida p) {
        if (p.getId() > 0) {
            return actualizarPartida(p);
        } else {
            return insertarPartida(p);
        }
    }

    private int insertarPartida(Partida p) {
        try (Connection con = getConexion()) {
            con.setAutoCommit(false);
            try {
                String sqlPartida = "INSERT INTO PARTIDA (TURNOS, JUGADOR_ACTUAL, FINALIZADA, NOMBRE_PARTIDA, FECHA_GUARDADO) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
                int idPartida;

                try (PreparedStatement ps = con.prepareStatement(sqlPartida, new String[]{"ID_PARTIDA"})) {
                    ps.setInt(1, CifradoBBDD.encriptarNumero(p.getTurnos()));
                    ps.setInt(2, CifradoBBDD.encriptarNumero(p.getJugadorActualIndice()));
                    ps.setInt(3, p.isFinalizada() ? 1 : 0);
                    ps.setString(4, p.getNombrePartida());
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        idPartida = keys.getInt(1);
                    }
                }

                insertarJugadores(con, p, idPartida);
                insertarCasillas(con, p, idPartida);

                con.commit();
                p.setId(idPartida);
                
                // Si la partida se guarda como finalizada, sumamos la victoria al ganador
                if (p.isFinalizada() && p.getGanador() != null && p.getGanador() instanceof Pinguino) {
                    incrementarVictoria(p.getGanador().getNombre());
                }
                
                return idPartida;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int actualizarPartida(Partida p) {
        try (Connection con = getConexion()) {
            con.setAutoCommit(false);
            try {
                int idPartida = p.getId();
                String sqlUpdate = "UPDATE PARTIDA SET TURNOS = ?, JUGADOR_ACTUAL = ?, FINALIZADA = ?, NOMBRE_PARTIDA = ?, FECHA_GUARDADO = CURRENT_TIMESTAMP WHERE ID_PARTIDA = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                    ps.setInt(1, CifradoBBDD.encriptarNumero(p.getTurnos()));
                    ps.setInt(2, CifradoBBDD.encriptarNumero(p.getJugadorActualIndice()));
                    ps.setInt(3, p.isFinalizada() ? 1 : 0);
                    ps.setString(4, p.getNombrePartida());
                    ps.setInt(5, idPartida);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement("DELETE FROM JUGADOR WHERE ID_PARTIDA = ?")) {
                    ps.setInt(1, idPartida);
                    ps.executeUpdate();
                }

                insertarJugadores(con, p, idPartida);

                try (PreparedStatement ps = con.prepareStatement("DELETE FROM CASILLA_TABLERO WHERE ID_PARTIDA = ?")) {
                    ps.setInt(1, idPartida);
                    ps.executeUpdate();
                }
                insertarCasillas(con, p, idPartida);

                con.commit();
                
                // Si la partida se guarda como finalizada, sumamos la victoria al ganador
                if (p.isFinalizada() && p.getGanador() != null && p.getGanador() instanceof Pinguino) {
                    incrementarVictoria(p.getGanador().getNombre());
                }
                
                return idPartida;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void insertarJugadores(Connection con, Partida p, int idPartida) throws SQLException {
        String sqlJugador = "INSERT INTO JUGADOR (ID_PARTIDA, INDICE_ORDEN, NOMBRE, COLOR, POSICION, TIPO, TURNOS_PERDIDOS, TURNOS_BLOQUEADA) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        for (int i = 0; i < p.getJugadores().size(); i++) {
            Jugador j = p.getJugadores().get(i);
            int idJugador;

            try (PreparedStatement ps = con.prepareStatement(sqlJugador, new String[]{"ID_JUGADOR"})) {
                ps.setInt(1, idPartida);
                ps.setInt(2, CifradoBBDD.encriptarNumero(i));
                ps.setString(3, j.getNombre());
                ps.setString(4, j.getColor());
                ps.setInt(5, CifradoBBDD.encriptarNumero(j.getPosicion()));
                ps.setString(6, j instanceof Pinguino ? "PINGUINO" : "FOCA");
                ps.setInt(7, CifradoBBDD.encriptarNumero(j.getTurnosPerdidos()));
                ps.setInt(8, CifradoBBDD.encriptarNumero(j instanceof Foca ? ((Foca) j).getTurnosBloqueada() : 0));
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    idJugador = keys.getInt(1);
                }
            }

            if (j instanceof Pinguino) {
                Pinguino ping = (Pinguino) j;
                String sqlItem = "INSERT INTO INVENTARIO_ITEM (ID_JUGADOR, TIPO_ITEM, CANTIDAD) VALUES (?, ?, ?)";
                for (Item item : ping.getInventario().getlista()) {
                    try (PreparedStatement psItem = con.prepareStatement(sqlItem)) {
                        psItem.setInt(1, idJugador);
                        psItem.setString(2, item.getNombre());
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
                ps.setString(3, c.getClass().getSimpleName());
                ps.executeUpdate();
            }
        }
    }

    public Partida cargarBBDD(int id) {
        try (Connection con = getConexion()) {
            int turnos, jugadorActual, finalizada;
            String nombrePartida = "";

            try (PreparedStatement ps = con.prepareStatement("SELECT TURNOS, JUGADOR_ACTUAL, FINALIZADA, NOMBRE_PARTIDA FROM PARTIDA WHERE ID_PARTIDA = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return null;
                    turnos = CifradoBBDD.desencriptarNumero(rs.getInt("TURNOS"));
                    jugadorActual = CifradoBBDD.desencriptarNumero(rs.getInt("JUGADOR_ACTUAL"));
                    finalizada = rs.getInt("FINALIZADA");
                    nombrePartida = rs.getString("NOMBRE_PARTIDA");
                }
            }

            ArrayList<Casilla> casillas = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT POSICION, TIPO FROM CASILLA_TABLERO WHERE ID_PARTIDA = ? ORDER BY POSICION")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int pos = CifradoBBDD.desencriptarNumero(rs.getInt("POSICION"));
                        String tipo = rs.getString("TIPO");
                        casillas.add(crearCasillaPorTipo(pos, tipo));
                    }
                }
            }
            
            // De esta manera fueza que la primera y la última casilla del tablero cargado sean NORMALES
            
            if (casillas.size() >= 2) {
                Casilla primera = casillas.get(0);
                Casilla ultima = casillas.get(casillas.size() - 1);
                
                casillas.set(0, new Normal(primera.getPosicion()));
                casillas.set(casillas.size() - 1, new Normal(ultima.getPosicion()));
            }

            ArrayList<Jugador> jugadores = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM JUGADOR WHERE ID_PARTIDA = ? ORDER BY INDICE_ORDEN")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int idJugador = rs.getInt("ID_JUGADOR");
                        String nombre = rs.getString("NOMBRE");
                        String color = rs.getString("COLOR");
                        int posicion = CifradoBBDD.desencriptarNumero(rs.getInt("POSICION"));
                        String tipo = rs.getString("TIPO");
                        int turnosPerdidos = CifradoBBDD.desencriptarNumero(rs.getInt("TURNOS_PERDIDOS"));

                        Jugador j;
                        if (tipo.equals("PINGUINO")) {
                            Inventario inv = new Inventario();
                            Pinguino p = new Pinguino(nombre, color, 0, inv);
                            p.setPosicion(posicion);
                            p.setTurnosPerdidos(turnosPerdidos);

                            try (PreparedStatement psItem = con.prepareStatement("SELECT TIPO_ITEM, CANTIDAD FROM INVENTARIO_ITEM WHERE ID_JUGADOR = ?")) {
                                psItem.setInt(1, idJugador);
                                try (ResultSet rsItem = psItem.executeQuery()) {
                                    while (rsItem.next()) {
                                        String tipoItem = rsItem.getString("TIPO_ITEM");
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

            Partida partida = new Partida();
            partida.setId(id);
            partida.setNombrePartida(nombrePartida);
            partida.setTurnos(turnos);
            partida.setJugadorActualIndice(jugadorActual);
            partida.setFinalizada(finalizada == 1);
            partida.getTablero().setListaCasillas(casillas);
            partida.setJugadores(jugadores);
            return partida;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

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
                    int numJugadores = rs.getInt("NUM_JUGADORES");
                    String nombreReal = rs.getString("NOMBRE_PARTIDA");
                    if (nombreReal == null || nombreReal.trim().isEmpty()) nombreReal = "Partida #" + info[0];

                    Timestamp fechaGuardado = rs.getTimestamp("FECHA_GUARDADO");
                    String fechaStr = fechaGuardado != null ? sdf.format(fechaGuardado) : "Fecha desconocida";
                    info[1] = "🎮 " + nombreReal + "  |  🕒 " + fechaStr + "  |  👥 " + numJugadores + " Jugadores";
                    lista.add(info);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean eliminarPartida(int id) {
        try (Connection con = getConexion()) {
            String sql = "DELETE FROM PARTIDA WHERE ID_PARTIDA = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            case "Sorpresa":        return new Sorpresa(posicion);
            default:                return new Normal(posicion);
        }
    }

    private Item crearItemPorTipo(String tipoItem) {
        switch (tipoItem) {
            case "Dado":            return new Dado("Normal");
            case "Pez":             return new Pez();
            case "Bola de Nieve":   return new bolaDeNieve();
            case "Moto de Nieve":   return new MotoDeNieve();
            default:                return null;
        }
    }

    // =========================================================
    //  MÉTODOS DE AUTENTICACIÓN DE USUARIOS
    // =========================================================

    /**
     * Crea la tabla USUARIOS en Oracle si no existe.
     * Se llama una vez al arrancar el juego desde Main.
     */
    public void crearTablaUsuarios() {
        try (Connection con = getConexion()) {
            // 1. Comprobar si la tabla ya existe
            boolean existeTabla = false;
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = 'USUARIOS'")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) existeTabla = true;
                }
            }

            if (!existeTabla) {
                // Crear tabla desde cero
                String sql = "CREATE TABLE USUARIOS (" +
                        "ID_USUARIO NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                        "USERNAME VARCHAR2(50) UNIQUE NOT NULL, " +
                        "PASSWORD_HASH VARCHAR2(255) NOT NULL, " +
                        "PARTIDAS_GANADAS NUMBER DEFAULT 0, " +
                        "FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                try (Statement st = con.createStatement()) {
                    st.executeUpdate(sql);
                    System.out.println("[BBDD] Tabla USUARIOS creada.");
                }
            } else {
                // La tabla existe, pero vamos a comprobar si le falta la columna PARTIDAS_GANADAS
                boolean existeColumna = false;
                try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM USER_TAB_COLUMNS WHERE TABLE_NAME = 'USUARIOS' AND COLUMN_NAME = 'PARTIDAS_GANADAS'")) {
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) existeColumna = true;
                    }
                }

                if (!existeColumna) {
                    try (Statement st = con.createStatement()) {
                        st.executeUpdate("ALTER TABLE USUARIOS ADD (PARTIDAS_GANADAS NUMBER DEFAULT 0)");
                        System.out.println("[BBDD] Columna PARTIDAS_GANADAS añadida a USUARIOS.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("[BBDD] Error en verificación de tablas: " + e.getMessage());
        }
    }

    /**
     * Comprueba si un nombre de usuario ya existe en la BBDD.
     * @return true si el usuario ya está registrado.
     */
    public boolean existeUsuario(String username) {
        try (Connection con = getConexion()) {
            String sql = "SELECT COUNT(*) FROM USUARIOS WHERE UPPER(USERNAME) = UPPER(?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al comprobar usuario: " + e.getMessage());
        }
        return false;
    }

    /**
     * Registra un nuevo usuario en la BBDD con la contraseña cifrada.
     * @return 1 = Éxito, -1 = Ya existe, 0 = Error BBDD
     */
    public int registrarUsuario(String username, String password) {
        if (existeUsuario(username)) {
            return -1; // Ya existe
        }
        try (Connection con = getConexion()) {
            String sql = "INSERT INTO USUARIOS (USERNAME, PASSWORD_HASH) VALUES (?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, CifradoBBDD.encriptarTexto(password));
                ps.executeUpdate();
                System.out.println("Usuario '" + username + "' registrado correctamente.");
                return 1; // Éxito
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return 0; // Error BBDD
        }
    }

    /**
     * Valida las credenciales de un usuario contra la BBDD.
     * @return true si el usuario existe y la contraseña coincide.
     */
    public boolean iniciarSesion(String username, String password) {
        try (Connection con = getConexion()) {
            String sql = "SELECT PASSWORD_HASH FROM USUARIOS WHERE UPPER(USERNAME) = UPPER(?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String hashGuardado = rs.getString("PASSWORD_HASH");
                        String hashIntroducido = CifradoBBDD.encriptarTexto(password);
                        return hashGuardado.equals(hashIntroducido);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
        }
        return false; // Usuario no encontrado o error
    }

    /**
     * Obtiene los nombres de los jugadores de tipo PINGUINO de una partida guardada.
     * Sirve para saber qué usuarios deben autenticarse al cargar la partida.
     * @return Lista de nombres de usuario (pingüinos) de esa partida.
     */
    public ArrayList<String> obtenerNombresPinguinosPartida(int idPartida) {
        ArrayList<String> nombres = new ArrayList<>();
        try (Connection con = getConexion()) {
            String sql = "SELECT NOMBRE FROM JUGADOR WHERE ID_PARTIDA = ? AND TIPO = 'PINGUINO' ORDER BY INDICE_ORDEN";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idPartida);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        nombres.add(rs.getString("NOMBRE"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener pingüinos de la partida: " + e.getMessage());
        }
        return nombres;
    }

    /**
     * Incrementa en 1 el contador de partidas ganadas de un usuario.
     */
    public void incrementarVictoria(String username) {
        try (Connection con = getConexion()) {
            String sql = "UPDATE USUARIOS SET PARTIDAS_GANADAS = PARTIDAS_GANADAS + 1 WHERE UPPER(USERNAME) = UPPER(?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, username);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Victoria sumada a: " + username);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al incrementar victoria: " + e.getMessage());
        }
    }

    /**
     * Obtiene el ranking de los 3 jugadores con más victorias.
     * @return Lista de arrays [username, victorias]
     */
    public ArrayList<String[]> obtenerRanking() {
        ArrayList<String[]> ranking = new ArrayList<>();
        try (Connection con = getConexion()) {
            String sql = "SELECT USERNAME, PARTIDAS_GANADAS FROM (SELECT USERNAME, PARTIDAS_GANADAS FROM USUARIOS ORDER BY PARTIDAS_GANADAS DESC) WHERE ROWNUM <= 3";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ranking.add(new String[]{rs.getString("USERNAME"), String.valueOf(rs.getInt("PARTIDAS_GANADAS"))});
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener ranking: " + e.getMessage());
        }
        return ranking;
    }
}