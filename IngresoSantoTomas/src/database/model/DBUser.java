/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.model;

/**
 *
 * @author Kenshi
 */
public class DBUser {
    
    private int id;
    private String fullname;
    private String rut;
    private String temperature;
    private int userTypeIdFk;
    private byte[] fingerPrint;

    public DBUser(int id, String fullname, String rut, String temperature, int userTypeIdFk, byte[] fingerPrint) {
        this.id = id;
        this.fullname = fullname;
        this.rut = rut;
        this.temperature = temperature;
        this.userTypeIdFk = userTypeIdFk;
        this.fingerPrint = fingerPrint;
    }

    public DBUser(String fullname, String rut, String temperature, int userTypeIdFk, byte[] fingerPrint) {
        this.fullname = fullname;
        this.rut = rut;
        this.temperature = temperature;
        this.userTypeIdFk = userTypeIdFk;
        this.fingerPrint = fingerPrint;
    }

    public DBUser() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public int getUserTypeIdFk() {
        return userTypeIdFk;
    }

    public void setUserTypeIdFk(int userTypeIdFk) {
        this.userTypeIdFk = userTypeIdFk;
    }

    public byte[] getFingerPrint() {
        return fingerPrint;
    }

    public void setFingerPrint(byte[] fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    @Override
    public String toString() {
        return "DBUser{" + "id=" + id + ", fullname=" + fullname + ", rut=" + rut + ", temperature=" + temperature + ", userTypeIdFk=" + userTypeIdFk + ", fingerPrint=" + fingerPrint + '}';
    }
    
    
    
    
    
   
}
