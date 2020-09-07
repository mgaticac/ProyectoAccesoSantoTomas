package model;

import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPTemplate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Data {

    public List<FPUser> getAllUsers() throws ClassNotFoundException, SQLException {
        Conexion c = new Conexion("fpdatabase");
        ResultSet rs = c.ejecutar("SELECT * FROM user;");
        FPUser fpu;

        List<FPUser> userList = new ArrayList<>();

        while (rs.next()) {
            fpu = new FPUser();
            fpu.setUserId(rs.getInt(1));
            System.out.println("ID:" + fpu.getUserId());
            DPFPTemplate tmp = DPFPGlobal.getTemplateFactory().createTemplate(rs.getBytes(5));
            fpu.setTemplate(tmp);
            System.out.println("template: " + fpu.getTemplate());

            userList.add(fpu);

        }
        return userList;

    }

}
