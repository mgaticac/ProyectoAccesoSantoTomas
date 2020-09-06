package model;

import com.mysql.jdbc.PreparedStatement;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {

    private Statement sen;
    private final Connection con;

    public Conexion(String bd) throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost/" + bd + "?user=root&password=Olakease123&serverTimezone=UTC";
        System.out.println(url);
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(url);
    }

    public ResultSet ejecutar(String query) throws SQLException {
        System.out.println(query);
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

        System.out.println(pstmt.getPreparedSql());

        if (query.toLowerCase().startsWith("insert")
                || query.toLowerCase().startsWith("update")
                || query.toLowerCase().startsWith("delete")) {
            pstmt.execute();
            close();
            return null;
        }
        System.out.println(pstmt.asSql());
        return null;
    }

    public void close() throws SQLException {
        sen.close();
    }

    public Connection getCon() {
        return con;
    }

}
