package model;

import com.digitalpersona.onetouch.DPFPTemplate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Data {

    private Conexion c;
    private ResultSet rs;
    private FPUser fPUser;

    public List<FPUser> getUsuarios() throws ClassNotFoundException, SQLException {
        c = new Conexion("fpdatabase");
        rs = c.ejecutar("SELECT * FROM user;");
        fPUser = new FPUser();

        List<FPUser> userList = new ArrayList<>();

        byte[] fp;
        DPFPTemplate tmp;
        while (rs.next()) {
            fPUser.setUserId(rs.getInt(0));
            fp = rs.getBytes(1);

//            fPUser.setTemplate();
            userList.add(fPUser);

        }

        return userList;
    }

}
