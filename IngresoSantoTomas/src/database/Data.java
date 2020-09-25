package database;

import database.Conexion;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPTemplate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.FPUser;

public class Data {

    public static Logger log = Logger.getLogger(Data.class.getName());
    private Conexion c;

    public Data(Conexion con) {
        this.c = con;
        log.setLevel(Level.ALL);
    }

    public List<FPUser> getAllUsers() throws ClassNotFoundException, SQLException {

        List<FPUser> userList = new ArrayList<>();
        ResultSet rs = c.ejecutar("SELECT * FROM user;");

        while (rs.next()) {
            FPUser fpu = new FPUser();
            fpu.setUserId(rs.getInt("id"));
            {   // template set
                byte[] bytes = rs.getBytes("finger_print");
                System.out.println("bytes:" + bytes.toString());
                DPFPTemplate tmp = DPFPGlobal.getTemplateFactory().createTemplate(bytes);
                fpu.setTemplate(tmp);
            }

            log.info("ID:" + fpu.getUserId());
            log.info("template: " + fpu.getTemplate());
            userList.add(fpu);

        }
        return userList;

    }

    public List<FPUser> getUserIdByRut(String rut) throws SQLException {
        List<FPUser> userIdList = new ArrayList<>();
        ResultSet rs = c.ejecutar("SELECT id from user WHERE rut LIKE '" + rut + "';");
        while (rs.next()) {
            FPUser fpu = new FPUser();
            fpu.setUserId(rs.getInt("id"));
            userIdList.add(fpu);
        }
        System.out.println("HOLA");
        return userIdList;
    }

}
