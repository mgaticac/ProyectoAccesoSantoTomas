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
import java.awt.Dimension;
import java.awt.Image;
import java.sql.SQLException;
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
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
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
    private DPFPEnrollment dPFPEnrollment;

    //UI
    //DATA
    private Conexion connection;
    private Data data;
    private List<String> sensorIds;

    //DAO
    private UserDao userDao;

    //SERVICES    
    private FPUserService userService;
    private SensorAdministrator sensorAdministrator;
    private FPSensorVerificationService verificationService;

    public mainForm() throws ClassNotFoundException, SQLException {
        // GUI

        initComponents();

        setLocationRelativeTo(null);

        lblHuella.setPreferredSize(new Dimension(300, 250));
        lblHuella.setBorder(BorderFactory.createLoweredBevelBorder());

        btnIdentify.setEnabled(false);

        // VALUES
        sensorIds = SensorUtils.getSensorsSerialIds();
        for (int i = 0; i < sensorIds.size(); i++) {
            cbVerifySensor.addItem(sensorIds.get(i).replaceAll("[{}]", ""));
            cbEnrollSensor.addItem(sensorIds.get(i).replaceAll("[{}]", ""));
        }

        // DATA
        connection = new Conexion("fpdb");
        data = new Data(connection);

        //DAO
        userDao = new UserDaoImpl(connection);

        // SERVICES
        sensorAdministrator = new SensorAdministrator(userDao); // admin contains all services
        userService = sensorAdministrator.getUserService();
        verificationService = sensorAdministrator.getVerificationService();

        // BEHAVIOR
        for (int i = 0; i < sensorIds.size(); i++) {
            System.out.println("ID SENSOR:" + sensorIds.get(i));
            sensorAdministrator.changeSensorBehivor(sensorIds.get(i), FPSensorBehivor.NONE);
        }

        sensorAdministrator.addEnrollingListener(this::enrollingEvent);
        sensorAdministrator.addVerificationListener(this::verificationEvent);
        listLastEnrollments();
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
            txtAreaState.setText("Usuario no identificado, \nintente nuevamente");

            log.info("user not found");

        }

    }

    @Override
    public void enrollingEvent(DPFPSample data) {

        DPFPFeatureSet features;
        DPFPCapture capture = DPFPGlobal.getCaptureFactory().createCapture();
        if (dPFPEnrollment.getFeaturesNeeded() > 0) {
            try {

                features = SensorUtils.getFeatureSet(data, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
                dPFPEnrollment.addFeatures(features);
                Image img = crearImagenHuella(data);
                dibujarHuella(img);

                if (features != null) {
                    System.out.println("features " + dPFPEnrollment.getFeaturesNeeded());
                    System.out.println("Enroller status " + dPFPEnrollment.getTemplateStatus());
                    if (dPFPEnrollment.getFeaturesNeeded() == 1) {
                        txtAreaInfo.setText("Falta " + dPFPEnrollment.getFeaturesNeeded() + " muestra, porfavor continue.");
                    } else {
                        txtAreaInfo.setText("Faltan " + dPFPEnrollment.getFeaturesNeeded() + " muestras, porfavor continue.");
                    }
                    switch (dPFPEnrollment.getTemplateStatus()) {

                        case TEMPLATE_STATUS_READY:
                            txtAreaInfo.setText("");

                            DPFPTemplate template = dPFPEnrollment.getTemplate();
                            DBUser dbUser = new DBUser(txtName.getText(), txtRut.getText(), cbUserType.getSelectedIndex(), template.serialize());
                            System.out.println(dbUser.toString());
                            userDao.add(dbUser);
                            dPFPEnrollment.clear();
                            capture.stopCapture();
                            JOptionPane.showMessageDialog(this, "Enrolamiento exitoso\nse limpiará la ventana de enrolamiento", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            clearEnrollFrame();
                            listLastEnrollments();
                            break;

                        case TEMPLATE_STATUS_FAILED:
                            txtAreaInfo.setText("\n Intente nuevamente porfavor, ponga su dedo en el lector");
                            dPFPEnrollment.clear();
                            capture.stopCapture();
                            capture.startCapture();
                            break;

                    }
                }

            } catch (DPFPImageQualityException e) {
                e.printStackTrace();
            } catch (SQLException ex) {
                Logger.getLogger(mainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnIdentify = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lblHuella = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtRut = new javax.swing.JTextField();
        cbUserType = new javax.swing.JComboBox<>();
        btnConfirmData = new javax.swing.JButton();
        cbEnrollSensor = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        btnEnroll = new javax.swing.JButton();
        btnCancelEnrollment = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAreaInfo = new javax.swing.JTextArea();
        cbVerifySensor = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        spTemperature = new javax.swing.JSpinner();
        lblTypeIdentificated = new javax.swing.JLabel();
        lblRutIdentificated = new javax.swing.JLabel();
        lblNombreIdentificated = new javax.swing.JLabel();
        btnSaveIdentifiedUser = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaState = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        btnExit = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        btnExportDailyData = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        btnIdentify.setText("Identificar Persona con este sensor");
        btnIdentify.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnIdentify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdentifyActionPerformed(evt);
            }
        });

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Huella Digital", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblHuella.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel4.add(lblHuella, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 220, 200));

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Nombre: ");

        jLabel3.setText("RUT:");

        jLabel5.setText("Tipo de ingreso: ");

        cbUserType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione un opción", "Estudiante", "Docente", "Personal", "Proveedor" }));

        btnConfirmData.setText("Confirmar datos y \nreservar lector");
        btnConfirmData.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnConfirmData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmDataActionPerformed(evt);
            }
        });

        cbEnrollSensor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione una opción" }));

        jLabel10.setText("Sensor:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRut)
                    .addComponent(txtName)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cbEnrollSensor, javax.swing.GroupLayout.Alignment.LEADING, 0, 187, Short.MAX_VALUE)
                            .addComponent(cbUserType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConfirmData, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtRut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(cbUserType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbEnrollSensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)))
                    .addComponent(btnConfirmData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(55, 55, 55))
        );

        btnEnroll.setText("Confirme datos antes de enrolar");
        btnEnroll.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnEnroll.setEnabled(false);
        btnEnroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnrollActionPerformed(evt);
            }
        });

        btnCancelEnrollment.setText("Cancelar");
        btnCancelEnrollment.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCancelEnrollment.setEnabled(false);
        btnCancelEnrollment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelEnrollmentActionPerformed(evt);
            }
        });

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));

        txtAreaInfo.setEditable(false);
        txtAreaInfo.setBackground(new java.awt.Color(231, 231, 231));
        txtAreaInfo.setColumns(20);
        txtAreaInfo.setRows(5);
        jScrollPane2.setViewportView(txtAreaInfo);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 20, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnCancelEnrollment, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEnroll, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(171, 171, 171))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(158, 158, 158))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnEnroll)
                .addGap(18, 18, 18)
                .addComponent(btnCancelEnrollment)
                .addGap(137, 137, 137))
        );

        cbVerifySensor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione una opción" }));
        cbVerifySensor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbVerifySensorItemStateChanged(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(231, 231, 231));
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText("seleccione el sensor de la lista\na la izquierda que quiera utilizar\npara identificar");
        jTextArea1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane3.setViewportView(jTextArea1);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Identificación"));

        jLabel4.setText("Nombre: ");

        jLabel6.setText("Rut:");

        jLabel7.setText("Tipo:");

        jLabel8.setText("Temperatura:");

        spTemperature.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(33.0f), Float.valueOf(33.0f), Float.valueOf(40.0f), Float.valueOf(0.1f)));

        lblTypeIdentificated.setText("--------------");

        lblRutIdentificated.setText("--------------");

        lblNombreIdentificated.setText("--------------");

        btnSaveIdentifiedUser.setText("Guardar");
        btnSaveIdentifiedUser.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnSaveIdentifiedUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveIdentifiedUserActionPerformed(evt);
            }
        });

        jLabel9.setText("Estado:");

        txtAreaState.setEditable(false);
        txtAreaState.setBackground(new java.awt.Color(231, 231, 231));
        txtAreaState.setColumns(15);
        txtAreaState.setRows(5);
        jScrollPane1.setViewportView(txtAreaState);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spTemperature, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTypeIdentificated, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblRutIdentificated, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblNombreIdentificated, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(48, 48, 48))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addComponent(jScrollPane1)))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSaveIdentifiedUser, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNombreIdentificated)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblRutIdentificated))
                        .addGap(33, 33, 33))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblTypeIdentificated))
                .addGap(32, 32, 32)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(spTemperature, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(btnSaveIdentifiedUser, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Ultimos Registros"));

        dataTable.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID Registro", "RUT", "Nombre"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dataTable.setRowHeight(20);
        jScrollPane5.setViewportView(dataTable);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        btnExit.setText("Salir del programa");
        btnExit.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Informe", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));

        btnExportDailyData.setText("Exportar informe del día");
        btnExportDailyData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportDailyDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnExportDailyData, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnExportDailyData, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cbVerifySensor, 0, 281, Short.MAX_VALUE)
                                    .addComponent(btnIdentify, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 717, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(34, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbVerifySensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnIdentify, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnExit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveIdentifiedUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveIdentifiedUserActionPerformed

        if (JOptionPane.showConfirmDialog(null, "¿Los datos rescatados son los correctos?", "Confirmación", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            try {
                List<FPUser> usersList = data.getUserIdByRut(lblRutIdentificated.getText());
                FPUser fPUser = new FPUser();
                for (int i = 0; i < usersList.size(); i++) {
                    fPUser.setUserId((int) usersList.get(i).getUserId());
                }

                System.out.println("USER ID:" + fPUser.getUserId());
                connection.ejecutar("INSERT INTO history VALUES(NULL, " + fPUser.getUserId() + ", NOW(),'" + spTemperature.getValue() + "');");

                JOptionPane.showMessageDialog(null, "Datos ingresados correctamente, persona registrada en la base de datos.");
                spTemperature.setValue(33);
                lblNombreIdentificated.setText("--------------");
                lblRutIdentificated.setText("--------------");
                lblTypeIdentificated.setText("--------------");
            } catch (SQLException ex) {
                Logger.getLogger(mainForm.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_btnSaveIdentifiedUserActionPerformed

    private void cbVerifySensorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbVerifySensorItemStateChanged
        if (cbVerifySensor.getSelectedIndex() > 0) {
            btnIdentify.setEnabled(true);
        } else {
            btnIdentify.setEnabled(false);
        }
    }//GEN-LAST:event_cbVerifySensorItemStateChanged

    private void btnCancelEnrollmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelEnrollmentActionPerformed
        clearEnrollFrame();
    }//GEN-LAST:event_btnCancelEnrollmentActionPerformed

    private void btnEnrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnrollActionPerformed
        JOptionPane.showMessageDialog(this, "Se ha iniciado el proceso de enrolado, siga las instrucciones mostradas más arriba");
        txtAreaInfo.setText("Enrolado iniciado, porfavor ponga su dedo 4 veces en el lector, \npresionando de forma considerada y levantando cuando se capture la imagen");
        sensorAdministrator.changeSensorBehivor("{" + cbEnrollSensor.getSelectedItem().toString() + "}", FPSensorBehivor.ENROLLING);
        dPFPEnrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    }//GEN-LAST:event_btnEnrollActionPerformed

    private void btnConfirmDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmDataActionPerformed

        if ((txtName.getText().equals("")) || (txtRut.getText().equals("")) || (cbEnrollSensor.getSelectedIndex() == 0) || (cbUserType.getSelectedIndex() == 0)) {

            JOptionPane.showMessageDialog(null, "No ha completado todos los campos, porfavor verifique");
        } else {

            btnCancelEnrollment.setText("Cancelar enrolamiento");
            btnCancelEnrollment.setEnabled(true);
            // Orden comboBox Estudiante - Docente - Personal - Proveedor
            //Confirmacion Datos Enrolamiento
            confirmEnrollmentData();

        }
    }//GEN-LAST:event_btnConfirmDataActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnIdentifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdentifyActionPerformed

        if ((cbEnrollSensor.getSelectedItem().equals(cbVerifySensor.getSelectedItem().toString())) && !(cbEnrollSensor.isEnabled())) {
            JOptionPane.showMessageDialog(this, "El sensor seleccionado está siendo usado para enrolar,\nporfavor elija otro de la lista o termine de ocuparlo");
        } else {
            userService.retriveUserListFromDatabase(); // hace el select y obtiene todos los usuarios internamente
            String sensorId = cbVerifySensor.getSelectedItem().toString();
            sensorAdministrator.changeSensorBehivor("{" + sensorId + "}", FPSensorBehivor.VALIDATING);

            JOptionPane.showMessageDialog(this, "Sensor " + cbVerifySensor.getSelectedItem().toString() + " está Identificando.\nLos Datos se mostrarán en el apartado de 'identificación'\ncuando el sensor detecte información");
            cbVerifySensor.setSelectedIndex(0);
        }
    }//GEN-LAST:event_btnIdentifyActionPerformed

    private void btnExportDailyDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportDailyDataActionPerformed
        //Exportar informe del día en formato CSV y ordenarlo en un archivo excel o pdf

        JFileChooser chooser = new JFileChooser();
        while (true) {
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = chooser.getSelectedFile();
                    if (!file.toString().toLowerCase().endsWith(".csv")) {
                        file = new File(file.toString() + ".csv");
                    }
                    if (file.exists()) {
                        int choice = JOptionPane.showConfirmDialog(this,
                                String.format("El Archivo \"%1$s\" ya existe.\n¿Quiere reemplazarlo?", file.toString()),
                                "Guardar Informe",
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        if (choice == JOptionPane.NO_OPTION) {
                            continue;
                        } else if (choice == JOptionPane.CANCEL_OPTION) {
                            break;
                        }
                    }
                    FileWriter stream = new FileWriter(file);
                    String[] columnTitle = {"ID Usuario;","Nombre;","Rut;"};
                    for (int i = 0; i < columnTitle.length; i++) {
                        stream.write(columnTitle[i]);
                        if((i+1)==columnTitle.length){
                            stream.write("\n");
                        }
                    }
                    List<DBUser> listDailyUsers = data.exportDailyData();
                    for (DBUser user : listDailyUsers) {
                        stream.write(user.getId()+";");
                        stream.write(user.getFullname()+";");
                        stream.write(user.getRut()+"\n");
                    }
                    
                    
                    stream.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Guardar Informe", JOptionPane.ERROR_MESSAGE);
                }
            }
            break;

        }


    }//GEN-LAST:event_btnExportDailyDataActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {
            try {
                new mainForm().setVisible(true);

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(mainForm.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (SQLException ex) {
                Logger.getLogger(mainForm.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        });

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelEnrollment;
    private javax.swing.JButton btnConfirmData;
    private javax.swing.JButton btnEnroll;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnExportDailyData;
    private javax.swing.JButton btnIdentify;
    private javax.swing.JButton btnSaveIdentifiedUser;
    private javax.swing.JComboBox<String> cbEnrollSensor;
    private javax.swing.JComboBox<String> cbUserType;
    private javax.swing.JComboBox<String> cbVerifySensor;
    private javax.swing.JTable dataTable;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblHuella;
    private javax.swing.JLabel lblNombreIdentificated;
    private javax.swing.JLabel lblRutIdentificated;
    private javax.swing.JLabel lblTypeIdentificated;
    private javax.swing.JSpinner spTemperature;
    private javax.swing.JTextArea txtAreaInfo;
    private javax.swing.JTextArea txtAreaState;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtRut;
    // End of variables declaration//GEN-END:variables

    public Image crearImagenHuella(DPFPSample sample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }

    public void dibujarHuella(Image image) {

        lblHuella.setIcon(new ImageIcon(
                image.getScaledInstance(lblHuella.getWidth(), lblHuella.getHeight(), Image.SCALE_DEFAULT)));
    }

    private void confirmEnrollmentData() {

        txtAreaInfo.setText("Datos confirmados, haga click en 'enrolar' para iniciar el proceso de registro");

        btnEnroll.setEnabled(true);
        btnEnroll.setText("Enrolar");
        txtName.setEnabled(false);
        txtRut.setEnabled(false);
        cbUserType.setEnabled(false);
        cbEnrollSensor.setEnabled(false);
    }

    private void clearEnrollFrame() {

        btnEnroll.setEnabled(false);
        btnEnroll.setText("Confirme datos antes de enrolar");
        txtName.setEnabled(true);
        txtName.setText("");
        txtRut.setEnabled(true);
        txtRut.setText("");
        cbUserType.setEnabled(true);
        cbUserType.setSelectedIndex(0);
        txtAreaInfo.setText("");
        lblHuella.setIcon(new ImageIcon());
        cbEnrollSensor.setEnabled(true);
        cbEnrollSensor.setSelectedIndex(0);
        btnCancelEnrollment.setEnabled(false);
    }

    private void userVerificated(DBUser user) {

        log.log(Level.INFO, "USER DETECTED!:{0}", user);
        txtAreaState.setText("Usuario identificado correctamente!");
        String tipoPersona = "";

        //CONSULTA INNERJOIN PARA LOS DATOS COMPLETOS DEL USUARIO (NOMBRE, RUT, TIPO['String'])
        switch (user.getUserTypeIdFk()) {

            case 1:
                tipoPersona = "Estudiante";
                break;
            case 2:
                tipoPersona = "Docente";
                break;
            case 3:
                tipoPersona = "Personal";
                break;
            case 4:
                tipoPersona = "Proveedor";
                break;

        }

        lblNombreIdentificated.setText(user.getFullname());
        lblRutIdentificated.setText(user.getRut());
        lblTypeIdentificated.setText(tipoPersona);

    }

    private void listLastEnrollments() throws SQLException {
        List<DBUser> latestEnrolledUsers = data.getLatestEnrollments();
        DefaultTableModel dtm = new DefaultTableModel();

        dtm.addColumn("ID Usuario");
        dtm.addColumn("Rut");
        dtm.addColumn("Nombre");
        for (DBUser userinfo : latestEnrolledUsers) {
            String[] userData = new String[]{
                String.valueOf(userinfo.getId()), userinfo.getRut(), userinfo.getFullname()
            };
            dtm.addRow(userData);
        }
        dataTable.setModel(dtm);
        dataTable.sizeColumnsToFit(WIDTH);
        dataTable.sizeColumnsToFit(HEIGHT);

    }

}
