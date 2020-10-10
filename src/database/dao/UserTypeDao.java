/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.dao;

import database.model.DBUserType;

/**
 *
 * @author klawx
 */
public interface UserTypeDao extends GenericCrud<DBUserType> {
    DBUserType getUserTypeById(int id);
}
