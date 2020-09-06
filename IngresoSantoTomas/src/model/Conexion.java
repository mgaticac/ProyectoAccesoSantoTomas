package model;

import com.mysql.jdbc.PreparedStatement;
import java.io.FileInputStream;
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

    public ResultSet insertHuella(String query) throws SQLException, FileNotFoundException, IOException {
        FileInputStream input = new FileInputStream("FingerPrint.jpg");
        PreparedStatement pstmt = (PreparedStatement) con.prepareStatement(query);
        pstmt.setString(1, "fprint");

        pstmt.setBinaryStream(6, input, input.available());

        System.out.println(query);

        if (query.toLowerCase().startsWith("insert")
                || query.toLowerCase().startsWith("update")
                || query.toLowerCase().startsWith("delete")) {
            pstmt.executeUpdate(query);
            close();
            return null;
        }

        return pstmt.executeQuery(query);
    }

    public void close() throws SQLException {
        sen.close();
    }

    public Connection getCon() {

        return con;
    }

}
