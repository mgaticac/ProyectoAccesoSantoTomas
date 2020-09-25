package database;

import java.sql.PreparedStatement;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class Conexion {

    public static Logger log = Logger.getLogger(Conexion.class.getName());
    private Statement sen;
    private Connection con;

    public Conexion(String bd) throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost/" + bd + "?user=root&password=Olakease123";
        log.info("Conecction url string:" + url);
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url);
    }

    public ResultSet ejecutar(String query) throws SQLException {
        log.info("Executing Query:" + query);
        sen = con.createStatement();

        if (query.toLowerCase().startsWith("insert")
                || query.toLowerCase().startsWith("update")
                || query.toLowerCase().startsWith("delete")) {
            sen.executeUpdate(query);
            close();
            return null;
        }

        return sen.executeQuery(query);

    }

    public ResultSet insertHuella(String nombre, String rut, String temperatura, int userTypeId, byte[] fingerPrintArray) throws SQLException, FileNotFoundException, IOException {
        String query = "INSERT INTO user (id, fullname, rut, temperature, user_type_id_fk, finger_print) VALUES (?,?,?,?,?,?);";
        PreparedStatement pstmt = (PreparedStatement) con.prepareStatement(query);

        pstmt.setObject(1, null);
        pstmt.setString(2, nombre);
        pstmt.setString(3, rut);
        pstmt.setString(4, temperatura);
        pstmt.setInt(5, userTypeId);
        pstmt.setBytes(6, fingerPrintArray);

        if (query.toLowerCase().startsWith("insert")
                || query.toLowerCase().startsWith("update")
                || query.toLowerCase().startsWith("delete")) {
            pstmt.execute();
            close();
            return null;
        }

        log.info("Executing Query (insertHuella):" + pstmt.toString());

        return null;
    }

    public void close() throws SQLException {
        sen.close();
    }

    public Connection getCon() {
        return con;
    }

}
