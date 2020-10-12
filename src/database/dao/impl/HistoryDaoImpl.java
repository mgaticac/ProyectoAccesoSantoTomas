/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.dao.impl;

import database.Conexion;
import database.dao.DaoHistorial;
import database.model.DBHistory;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author klawx
 */
public class HistoryDaoImpl implements DaoHistorial {

    public static Logger log = Logger.getLogger(HistoryDaoImpl.class.getName());
    private Conexion con;

    public HistoryDaoImpl(Conexion con) {
        this.con = con;
    }

    @Override
    public void add(DBHistory t) {
        String sql = "INSERT INTO history VALUES(NULL, " + t.getUserId() + ", NOW());";
        try {
            log.fine(sql);
            con.ejecutar(sql);
        } catch (SQLException ex) {
            Logger.getLogger(HistoryDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(DBHistory t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(DBHistory t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DBHistory> getAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
