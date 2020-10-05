package database.model;

public class DBUser {

    private int id;
    private String fullname;
    private String rut;
    private int userTypeIdFk;
    private byte[] fingerPrint;
    private String verifyDate;

    public DBUser(int id, String fullname, String rut, int userTypeIdFk, byte[] fingerPrint) {
        this.id = id;
        this.fullname = fullname;
        this.rut = rut;

        this.userTypeIdFk = userTypeIdFk;
        this.fingerPrint = fingerPrint;

    }

    public DBUser(String fullname, String rut, int userTypeIdFk, byte[] fingerPrint) {
        this.fullname = fullname;
        this.rut = rut;
        this.userTypeIdFk = userTypeIdFk;
        this.fingerPrint = fingerPrint;

    }

    public DBUser(int id, String fullname, String rut, String verifyDate) {
        this.id = id;
        this.fullname = fullname;
        this.rut = rut;
        this.verifyDate = verifyDate;
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

    public String getVerifyDate() {
        return verifyDate;
    }

    public void setVerifyDate(String verifyDate) {
        this.verifyDate = verifyDate;
    }

    @Override
    public String toString() {
        return "DBUser{" + "id=" + id + ", fullname=" + fullname + ", rut=" + rut + ", userTypeIdFk=" + userTypeIdFk + ", fingerPrint=" + fingerPrint + '}';
    }

    public String listLastUsersInfo() {
        return " " + id + " " + fullname + " " + rut;
    }

}
