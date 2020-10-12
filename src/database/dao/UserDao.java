/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.dao;

import database.model.DBSede;
import database.model.DBUser;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Kenshi
 */
public interface UserDao extends GenericCrud<DBUser> {

    DBUser getAllUserBySede(DBSede sede);

    Optional<DBUser> getUserById(int id);

    List<DBUser> getLatestEnrollments(int instituteId, int quantity);

    Optional<List<DBUser>> exportDailyData();

    Optional<DBUser> getUserByRut(String rut);

    boolean userRutExists(String rut);
}
