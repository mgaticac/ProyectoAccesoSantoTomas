/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import database.Conexion;
import database.Data;
import database.dao.UserDao;
import database.dao.impl.UserDaoImpl;
import database.model.DBUser;
import model.EnrollingListener;
import model.FPSensorBehivor;
import model.FPUser;
import model.SensorAdministrator;
import model.VerificationListener;
import service.FPSensorVerificationService;
import service.FPUserService;
import util.SensorUtils;

public class mainForm extends javax.swing.JFrame implements EnrollingListener, VerificationListener {

    //LOGGER SETUP
    private final static Logger log;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] [%2$-45s] %5$s %n");
        log = Logger.getLogger(mainForm.class.getName());
    }

    private String sensorId; // ?? pueden existir mas de 1 sensor
    private DPFPEnrollment enroller;// NO KCHO
    private DPFPCapture capturer; // X2
    private SensorUtils su; // esta clase tiene metodos estaticos no es necesaria declararla
    private String nombre;// ??
    private String rut;// ??
    private String temperatura; // ??
    private int userTypeId; // ?????????????

    //DATA
    private Conexion connection;
    private Data data;

    //DAO
    private UserDao userDao;

    //SERVICES    
    private FPUserService userService;
    private SensorAdministrator sensorAdministrator;
    private FPSensorVerificationService verificationService;

    public mainForm() throws ClassNotFoundException, SQLException {
        // GUI
        initComponents();
        lblHuella.setPreferredSize(new Dimension(300, 250));
        lblHuella.setBorder(BorderFactory.createLoweredBevelBorder());

        // VALUES
        sensorId = SensorUtils.getSensorsSerialIds().get(0); // FIXME: eston pueden ser varios sensores 

        // DATA
        connection = new Conexion("fpdatabase");
        data = new Data(connection);

        //DAO
        userDao = new UserDaoImpl(connection);

        // SERVICES
        sensorAdministrator = new SensorAdministrator(userDao); // admin contains all services
        userService = sensorAdministrator.getUserService();
        verificationService = sensorAdministrator.getVerificationService();

        // BEHAVIOR
        sensorAdministrator.changeSensorBehivor(sensorId, FPSensorBehivor.NONE);
        sensorAdministrator.addEnrollingListener(this::enrollingEvent);
        sensorAdministrator.addVerificationListener(this::verificationEvent);
    }

    @Override
    public void verificationEvent(Optional<FPUser> user) {
        if (user.isPresent()) {
            FPUser fpUser = user.get();
            Optional<DBUser> userById = userDao.getUserById((int) fpUser.getUserId());
            if (userById.isPresent()) {
                DBUser dbUser = userById.get();
                userVerificated(dbUser);                
            } else {
                log.severe("USER MISSMATCH:" + fpUser + " NOT FOUNDED IN DB!");
            }
        } else {
            log.info("used not founded");
        }
    }
    
    private void userVerificated(DBUser user){
        log.info("USER DETECTED!:" + user);
        
        // code....
    }

    @Override
    public void enrollingEvent(DPFPSample data) {
        DPFPFeatureSet features;
        if (enroller.getFeaturesNeeded() > 0) { // FIXME: falta mostrar cuantos registros quedan restantes en la UI
            try {
                features = SensorUtils.getFeatureSet(data, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
                Image img = crearImagenHuella(data);
                dibujarHuella(img);

                if (features != null) {
                    try {
                        //makeReport("The fingerprint feature set was created.");
                        enroller.addFeatures(features);
                        System.out.println("features " + enroller.getFeaturesNeeded());
                        System.out.println("Enroller status" + enroller.getTemplateStatus());

                    } catch (DPFPImageQualityException ex) {
                    } finally {

                        switch (enroller.getTemplateStatus()) {
                            case TEMPLATE_STATUS_READY:
                                stats();
                                 {
                                    DPFPTemplate template = enroller.getTemplate();
                                    DBUser dbUser = new DBUser(nombre, rut, temperatura, userTypeId, template.serialize());
                                    userDao.add(dbUser);
                                }
                                enroller.clear();
                                capturer.stopCapture();
                                break;

                            case TEMPLATE_STATUS_FAILED:
                                enroller.clear();
                                capturer.stopCapture();
                                capturer.startCapture();
                                break;
                        }
                    }
                }
                stats();

            } catch (DPFPImageQualityException e) {
                e.printStackTrace();
            }
        }
    }

    private void stats() {
        System.out.println("ENROLLING EVENT, " + enroller.getFeaturesNeeded() + "finger print left");
        System.out.println("status: " + enroller.getTemplateStatus());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enrollingFrame = new javax.swing.JFrame();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lblHuella = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaInformacion = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtRut = new javax.swing.JTextField();
        txtTemperature = new javax.swing.JTextField();
        cbUserType = new javax.swing.JComboBox<>();
        btnConfirmData = new javax.swing.JButton();
        btnCancelEnrollment = new javax.swing.JButton();
        btnEnroll = new javax.swing.JButton();
        VerifyFrame = new javax.swing.JFrame();
        jPanel6 = new javax.swing.JPanel();
        btnCloseVerify = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnEnrollWindow = new javax.swing.JButton();
        btnVerify = new javax.swing.JButton();
        btnIdentify = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(lblHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));

        txtAreaInformacion.setEditable(false);
        txtAreaInformacion.setColumns(20);
        txtAreaInformacion.setRows(5);
        txtAreaInformacion.setFocusable(false);
        jScrollPane1.setViewportView(txtAreaInformacion);

        jLabel2.setText("Nombre: ");

        jLabel3.setText("RUT:");

        jLabel4.setText("Temperatura: ");

        jLabel5.setText("Tipo de ingreso: ");

        cbUserType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Estudiante", "Docente", "Personal", "Proveedor" }));

        btnConfirmData.setText("Confirmar datos");
        btnConfirmData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnConfirmData, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                                .addComponent(txtRut)
                                .addComponent(txtTemperature)
                                .addComponent(cbUserType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtRut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTemperature, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbUserType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(btnConfirmData))
        );

        btnCancelEnrollment.setText("Cancelar");
        btnCancelEnrollment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelEnrollmentActionPerformed(evt);
            }
        });

        btnEnroll.setText("Enrolar");
        btnEnroll.setEnabled(false);
        btnEnroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnrollActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnCancelEnrollment, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEnroll, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelEnrollment)
                    .addComponent(btnEnroll))
                .addContainerGap())
        );

        javax.swing.GroupLayout enrollingFrameLayout = new javax.swing.GroupLayout(enrollingFrame.getContentPane());
        enrollingFrame.getContentPane().setLayout(enrollingFrameLayout);
        enrollingFrameLayout.setHorizontalGroup(
            enrollingFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        enrollingFrameLayout.setVerticalGroup(
            enrollingFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enrollingFrameLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnCloseVerify.setText("Cerrar");

        jLabel1.setFont(new java.awt.Font("Sylfaen", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("-");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(176, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCloseVerify, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(161, 161, 161))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(101, 101, 101)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                .addComponent(btnCloseVerify)
                .addGap(52, 52, 52))
        );

        javax.swing.GroupLayout VerifyFrameLayout = new javax.swing.GroupLayout(VerifyFrame.getContentPane());
        VerifyFrame.getContentPane().setLayout(VerifyFrameLayout);
        VerifyFrameLayout.setHorizontalGroup(
            VerifyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        VerifyFrameLayout.setVerticalGroup(
            VerifyFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Huella Digital", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jPanel1.setFocusable(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnEnrollWindow.setText("Enrolar Huella");
        btnEnrollWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnrollWindowActionPerformed(evt);
            }
        });

        btnVerify.setText("Verificar Existencia");
        btnVerify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerifyActionPerformed(evt);
            }
        });

        btnIdentify.setText("Identificar Persona");
        btnIdentify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdentifyActionPerformed(evt);
            }
        });

        btnExit.setText("Salir");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnEnrollWindow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnIdentify, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnExit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVerify, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEnrollWindow, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerify, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnIdentify, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 230));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnEnrollWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnrollWindowActionPerformed

        enrollingFrame.setVisible(true);
        enrollingFrame.setSize(860, 460);
        enrollingFrame.setResizable(false);


    }//GEN-LAST:event_btnEnrollWindowActionPerformed

    private void btnVerifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyActionPerformed

        userService.retriveUserListFromDatabase(); // hace el select y obtiene todos los usuarios internamente
        sensorAdministrator.changeSensorBehivor(sensorId, FPSensorBehivor.VALIDATING);

    }//GEN-LAST:event_btnVerifyActionPerformed

    private void btnIdentifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdentifyActionPerformed
        // SE IDENTIFICARÁ LA PERSONA PARA SU INGRESO A LA INSTITUCION, IMPLEMENTACION FUTURA
    }//GEN-LAST:event_btnIdentifyActionPerformed

    private void btnCancelEnrollmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelEnrollmentActionPerformed
        // Cancelar enrolado y volver al menú anterior
        sensorAdministrator.changeSensorBehivor(sensorId, FPSensorBehivor.NONE);
        //programar stop de sensor

        enrollingFrame.setVisible(false);

    }//GEN-LAST:event_btnCancelEnrollmentActionPerformed

    private void btnEnrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnrollActionPerformed

        FPSensorBehivor enrolling = FPSensorBehivor.ENROLLING;
        enroller = DPFPGlobal.getEnrollmentFactory().createEnrollment();
        sensorAdministrator.changeSensorBehivor(sensorId, enrolling);

        System.out.println("ENROLANDO");

    }//GEN-LAST:event_btnEnrollActionPerformed

    private void btnConfirmDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmDataActionPerformed
        nombre = txtName.getText();
        rut = txtRut.getText();
        temperatura = txtTemperature.getText();

        userTypeId = 0;
        /*
    Estudiante
    Docente
    Personal
    Proveedor
         */
        switch (cbUserType.getItemAt(cbUserType.getSelectedIndex())) { // FIXME: esto no puede estar hardcodeado

            case "Estudiante":
                userTypeId = 6;
                break;
            case "Docente":
                userTypeId = 5;
                break;

            case "Personal":
                userTypeId = 4;
                break;
            case "Proveedor":
                userTypeId = 3;
                break;

        }

        btnEnroll.setEnabled(true);
    }//GEN-LAST:event_btnConfirmDataActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new mainForm().setVisible(true);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(mainForm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(mainForm.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFrame VerifyFrame;
    private javax.swing.JButton btnCancelEnrollment;
    private javax.swing.JButton btnCloseVerify;
    private javax.swing.JButton btnConfirmData;
    private javax.swing.JButton btnEnroll;
    private javax.swing.JButton btnEnrollWindow;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnIdentify;
    private javax.swing.JButton btnVerify;
    private javax.swing.JComboBox<String> cbUserType;
    private javax.swing.JFrame enrollingFrame;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHuella;
    private javax.swing.JTextArea txtAreaInformacion;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtRut;
    private javax.swing.JTextField txtTemperature;
    // End of variables declaration//GEN-END:variables

    public Image crearImagenHuella(DPFPSample sample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }

    public void dibujarHuella(Image image) {

        lblHuella.setIcon(new ImageIcon(
                image.getScaledInstance(lblHuella.getWidth(), lblHuella.getHeight(), Image.SCALE_DEFAULT)));
    }

}
