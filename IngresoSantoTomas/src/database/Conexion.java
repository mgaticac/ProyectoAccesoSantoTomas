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
        String url = "jdbc:mysql://fp-db-endpoint.cqslapnsdzfa.us-east-2.rds.amazonaws.com/" + bd + "?user=xzero&password=asdqwe123";

        //
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

    

    public void close() throws SQLException {
        sen.close();
    }

    public Connection getCon() {
        return con;
    }

    

}
