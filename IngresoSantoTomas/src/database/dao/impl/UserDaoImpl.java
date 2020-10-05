/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.dao.impl;

import database.Conexion;
import database.Data;
import database.dao.UserDao;
import database.model.DBSede;
import database.model.DBUser;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                String rut = rs.getString("rut");
                int usetType = rs.getInt("user_type_id_fk");
                byte[] fingerPrint = rs.getBytes("finger_print");

                DBUser user = new DBUser(id, fullname, rut, usetType, fingerPrint);

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
        int instituteId = 0;
        try (InputStream input = new FileInputStream("src/resources/config.properties")) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            // get the property value
            String actualInstitute = prop.getProperty("institute");
            Data d = new Data(con);
            instituteId = d.getInstituteId(actualInstitute);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "INSERT INTO user (id, fullname, rut, user_type_id_fk, finger_print, institute_fk) VALUES (?,?,?,?,?,?);";
        try {
            PreparedStatement pstmt = (PreparedStatement) con.getCon().prepareStatement(query);
            pstmt.setObject(1, null);
            pstmt.setString(2, t.getFullname());
            pstmt.setString(3, t.getRut());
            pstmt.setInt(4, t.getUserTypeIdFk());
            pstmt.setBytes(5, t.getFingerPrint());
            pstmt.setInt(6, instituteId);

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
            if (rs.next()) {
                int uid = rs.getInt("id");
                String fullName = rs.getString("fullname");
                String rut = rs.getString("rut");
                int userTipeIdFk = rs.getInt("user_type_id_fk");
                byte[] fingerPrint = rs.getBytes("finger_print");
                //(int id, String fullname, String rut, String temperature, int userTypeIdFk, byte[] fingerPrint) {
                DBUser user = new DBUser(uid, fullName, rut, userTipeIdFk, fingerPrint);
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
