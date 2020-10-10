/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import view.mainForm;

/**
 *
 * @author klawx
 */
public class FPConfig {
    public static String CONFIG_URL = "config.properties";
    
    private static FPConfig _instance;
    private final static Logger log = Logger.getLogger(FPConfig.class.getName());;
    private Properties prop;
    
    // FILE PROPERTIES!    
    private int instituteId;
    private String dbHost;
    private String dbName;
    private String dbPasswd;
    private String dbUser;
    

    public FPConfig() throws FileNotFoundException, IOException{ 
        InputStream input = new FileInputStream(CONFIG_URL);
        prop = new Properties();
        prop.load(input);
        loadConfig();  
    }

    private void loadConfig(){
         instituteId = Integer.parseInt(prop.getProperty("location.institute"));
         dbHost = prop.getProperty("db.host");
         dbName = prop.getProperty("db.name");
         dbUser = prop.getProperty("db.user");
         dbPasswd = prop.getProperty("db.passwd");
    }

    public int getInstituteId() {
        return instituteId;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbPasswd() {
        return dbPasswd;
    }

    public String getDbUser() {
        return dbUser;
    }
    

    

    @Override
    public String toString() {
        return "FPConfig{" + "instituteId=" + instituteId + ", dbHost=" + dbHost + ", dbName=" + dbName + ", dbUser=" + dbUser + '}';
    }
    
    


    
    
}
