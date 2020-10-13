/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 *
 * @author klawx
 */
public class LoggingConfig {

    public static final String LOGGING_FILE = "logging.properties";

    public static void loadConfig() {
        try {
            InputStream stream = new FileInputStream(LOGGING_FILE);
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException | SecurityException ex) {
            System.err.println("Executing without logging Config, NOT FOUNDED!");
            System.err.println(ex.toString());
            System.setProperty("java.util.logging.SimpleFormatter.format",
                    "[%1$tF %1$tT] [%4$-7s] [%2$-45s] %5$s %n");
        }
    }

}
