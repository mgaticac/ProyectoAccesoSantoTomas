/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.dao.impl;

import config.FPConfig;
import database.Conexion;
import database.dao.UserTypeDao;
import database.model.DBUserType;
import java.util.List;

/**
 *
 * @author klawx
 */
public class UserTypeDaoImpl implements UserTypeDao {
    
    private Conexion con;
    private FPConfig config;
    public UserTypeDaoImpl(Conexion con){
        this.con = con;

    }

    @Override
    public void add(DBUserType t) {
        
    }

    @Override
    public void delete(DBUserType t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(DBUserType t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<DBUserType> getAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DBUserType getUserTypeById(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
