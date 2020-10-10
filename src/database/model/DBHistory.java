/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.model;

import java.sql.Date;

/**
 *
 * @author klawx
 */
public class DBHistory {
    private int id;
    private int userId;
    private Date registerDate;
    private String observation;

    public DBHistory(int id, int userId, Date registerDate, String observation) {
        this.id = id;
        this.userId = userId;
        this.registerDate = registerDate;
        this.observation = observation;
    }

    public DBHistory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
    
    
    
    
    
    
    
    
    
}
