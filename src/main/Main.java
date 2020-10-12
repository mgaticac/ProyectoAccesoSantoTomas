package main;

import config.FPConfig;
import config.LoggingConfig;
import database.Conexion;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import view.mainForm;

/**
 *
 * @author klawx
 */
public class Main {

    public static Logger log = Logger.getLogger(Main.class.getName());

    public Main() {
        LoggingConfig.loadConfig();
        try {
            FPConfig config = new FPConfig();
            Conexion con = new Conexion(config.getDbName(), config.getDbHost(),
                    config.getDbUser(), config.getDbPasswd());
            SwingUtilities.invokeLater(() -> {
                mainForm form = new mainForm(con, config);
                form.setVisible(true);
            });
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Sin conexión a la BD o Internet",
                    "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Sin el archivo de configuración inicial -> reporte al wn",
                    "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public static void main(String args[]) {
        new Main();
    }
}
