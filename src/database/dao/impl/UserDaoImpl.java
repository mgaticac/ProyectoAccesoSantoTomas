/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.dao.impl;

import config.FPConfig;
import database.Conexion;
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

public class UserDaoImpl implements UserDao {

    public static Logger log = Logger.getLogger(UserDaoImpl.class.getName());
    private Conexion con;

    private FPConfig config;

    public UserDaoImpl(Conexion con, FPConfig config) {
        this.con = con;
        this.config = config;
    }

    @Override
    public List<DBUser> getAll() {
        try {
            List<DBUser> userList = new ArrayList<>();
            ResultSet rs = con.ejecutar("SELECT * FROM user WHERE institute_fk = " + config.getInstituteId() + ";");
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
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        }
        return Collections.emptyList();
    }

    @Override
    public void add(DBUser t) {
        String query = "INSERT INTO user (id, fullname, rut, user_type_id_fk, finger_print, institute_fk) VALUES (?,?,?,?,?,?);";
        int instituteId = config.getInstituteId();
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
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        }
    }

    @Override
    public Optional<DBUser> getUserById(int id) {
        String sql = "SELECT * FROM user WHERE id = ? AND institute_fk = " + config.getInstituteId();
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
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, ex.toString(), ex);
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

    @Override
    public List<DBUser> getLatestEnrollments() {
        try {
            List<DBUser> userIdList = new ArrayList<>();
            ResultSet rs = con.ejecutar("SELECT * from user WHERE institute_fk = " + config.getInstituteId() + " ORDER BY id DESC LIMIT 15;");
            while (rs.next()) {
                DBUser user = new DBUser();
                user.setId(rs.getInt(1));
                user.setFullname(rs.getString(2));
                user.setRut(rs.getString(3));
                userIdList.add(user);
            }

            return userIdList;
        } catch (SQLException ex) {
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        }
        return Collections.emptyList();
    }

    @Override
    public Optional<List<DBUser>> exportDailyData() {
        try {
            List<DBUser> userList = new ArrayList<>();
            ResultSet rs = con.ejecutar("SELECT user.id, user.fullname, user.rut, history.register_date FROM history "
                    + "INNER JOIN user "
                    + "ON history.user_id_fk = user.id "
                    + "WHERE history.register_date > DATE_SUB(CURDATE(), INTERVAL 1 DAY) "
                    + "AND institute_fk = " + config.getInstituteId() + " "
                    + "ORDER BY history.register_date ASC;");

            while (rs.next()) {
                DBUser user = new DBUser();
                user.setId(rs.getInt(1));
                user.setFullname(rs.getString(2));
                user.setRut(rs.getString(3));
                user.setVerifyDate(rs.getString(4));
                userList.add(user);
            }
            return Optional.of(userList);
        } catch (SQLException ex) {
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<DBUser> getUserByRut(String rut) {
        String sql = "SELECT * FROM user WHERE rut LIKE '" + rut + "' AND institute_fk = " + config.getInstituteId() + ";";
        try {
            DBUser user = null;
            ResultSet rs = con.ejecutar(sql);
            if (rs.next()) {
                int id = rs.getInt("id");
                String fullname = rs.getString("fullname");
                String _rut = rs.getString("rut");
                int usetType = rs.getInt("user_type_id_fk");
                byte[] fingerPrint = rs.getBytes("finger_print");

                user = new DBUser(id, fullname, _rut, usetType, fingerPrint);
            }
            return Optional.of(user);
        } catch (SQLException ex) {
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        }
        return Optional.empty();
    }

}
