/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.dao.impl;

import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPTemplate;
import database.Conexion;
import static database.Conexion.log;
import static database.Data.log;
import database.dao.UserDao;
import database.model.DBSede;
import database.model.DBUser;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.FPUser;

public class UserDaoImpl implements UserDao {

    public static Logger log = Logger.getLogger(UserDaoImpl.class.getName());
    private Conexion con;

    public UserDaoImpl(Conexion con) {
        this.con = con;
    }

    @Override
    public List<DBUser> getAll() {
        try {
            List<DBUser> userList = new ArrayList<>();
            ResultSet rs = con.ejecutar("SELECT * FROM user;");
            while (rs.next()) {

                int id = rs.getInt("id");
                String fullname = rs.getString("fullname");
                String temperature = rs.getString("temperature");
                String rut = rs.getString("rut");
                int usetType = rs.getInt("user_type_id_fk");
                byte[] fingerPrint = rs.getBytes("finger_print");

                DBUser user = new DBUser(id, fullname, rut, temperature, usetType, fingerPrint);

                //System.out.println("bytes:" + bytes.toString());
                //DPFPTemplate tmp = DPFPGlobal.getTemplateFactory().createTemplate(bytes);
                //user.setTemplate(tmp);
                userList.add(user);

            }
            return userList;
        } catch (SQLException ex) {
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Collections.emptyList();
    }

    @Override
    public void add(DBUser t) {
        String query = "INSERT INTO user (id, fullname, rut, temperature, user_type_id_fk, finger_print) VALUES (?,?,?,?,?,?);";
        try {
            PreparedStatement pstmt = (PreparedStatement) con.getCon().prepareStatement(query);
            pstmt.setObject(1, null);
            pstmt.setString(2, t.getFullname());
            pstmt.setString(3, t.getRut());
            pstmt.setString(4, t.getTemperature());
            pstmt.setInt(5, t.getUserTypeIdFk());
            pstmt.setBytes(6, t.getFingerPrint());
            if (query.toLowerCase().startsWith("insert")
                    || query.toLowerCase().startsWith("update")
                    || query.toLowerCase().startsWith("delete")) {
                pstmt.execute();
                pstmt.close();
            }
            log.info("Executing Query (insertHuella):" + pstmt.toString());
        } catch (SQLException ex) {
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public Optional<DBUser> getUserById(int id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try {
            PreparedStatement pstmt = (PreparedStatement) con.getCon().prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                int uid = rs.getInt("id");
                String fullName = rs.getString("fullname");
                String rut = rs.getString("rut");
                String temperature = rs.getString("temperature");
                int userTipeIdFk = rs.getInt("user_type_id_fk");
                byte[] fingerPrint = rs.getBytes("finger_print");
                //(int id, String fullname, String rut, String temperature, int userTypeIdFk, byte[] fingerPrint) {
                DBUser user = new DBUser(uid,fullName,rut,temperature,userTipeIdFk,fingerPrint);
                return Optional.of(user);
            }
            pstmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Optional.empty();
    }

    @Override
    public void delete(DBUser t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(DBUser t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DBUser getAllUserBySede(DBSede sede) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
