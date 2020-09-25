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
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JOptionPane;
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

    private List<String> sensorIds;
    //UI
    private ImageIcon okImageIcon;
    private ImageIcon notOkImageIcon;

    private String nombre;// ??
    private String rut;// ??
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
        setLocationRelativeTo(null);

        lblHuella.setPreferredSize(new Dimension(300, 250));
        lblHuella.setBorder(BorderFactory.createLoweredBevelBorder());
        okImageIcon = new ImageIcon("/resources/NOTOK.png");
        notOkImageIcon = new ImageIcon("/resources/OKIMG.png");
        

        btnIdentify.setEnabled(false);
        btnConfirmData.setEnabled(false);

        // VALUES
        sensorIds = SensorUtils.getSensorsSerialIds();
        for (int i = 0; i < sensorIds.size(); i++) {
            cmbSensorList.addItem(sensorIds.get(i).replaceAll("[{}]", ""));
        }

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
        for (int i = 0; i < sensorIds.size(); i++) {
            System.out.println("ID SENSOR:" + sensorIds.get(i));
            sensorAdministrator.changeSensorBehivor(sensorIds.get(i), FPSensorBehivor.NONE);
        }

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
            log.info("user not found");

        }

    }

    @Override
    public void enrollingEvent(DPFPSample data) {
        DPFPFeatureSet features;
        DPFPEnrollment dPFPEnrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();
        DPFPCapture capture = DPFPGlobal.getCaptureFactory().createCapture();

        if (dPFPEnrollment.getFeaturesNeeded() > 0) {
            try {

                features = SensorUtils.getFeatureSet(data, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
                Image img = crearImagenHuella(data);
                dibujarHuella(img);

                if (features != null) {
                    try {
                        DPFPGlobal.getEnrollmentFactory().createEnrollment().addFeatures(features);
                        System.out.println("features " + dPFPEnrollment.getFeaturesNeeded());

                        System.out.println("Enroller status" + dPFPEnrollment.getTemplateStatus());
                        if (dPFPEnrollment.getFeaturesNeeded() == 1) {
                            txtAreaInfo.setText("Falta " + dPFPEnrollment.getFeaturesNeeded() + " muestra, porfavor continue.");
                        } else {
                            txtAreaInfo.setText("Faltan " + dPFPEnrollment.getFeaturesNeeded() + " muestras, porfavor continue.");
                        }

                    } catch (DPFPImageQualityException ex) {
                    } finally {

                        switch (dPFPEnrollment.getTemplateStatus()) {
                            case TEMPLATE_STATUS_READY: {
                                txtAreaInfo.setText("");
                                btnCancelEnrollment.setText("Atras");
                                JOptionPane.showMessageDialog(this, "Enrolamiento exitoso\nse limpiará la ventana de enrolamiento", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                clearEnrollFrame();
                                DPFPTemplate template = dPFPEnrollment.getTemplate();
                                DBUser dbUser = new DBUser(nombre, rut, userTypeId, template.serialize());
                                userDao.add(dbUser);
                            }

                            dPFPEnrollment.clear();
                            capture.stopCapture();
                            break;

                            case TEMPLATE_STATUS_FAILED:
                                txtAreaInfo.setText("\n Intente nuevamente porfavor, ponga su dedo en el lector");
                                dPFPEnrollment.clear();
                                capture.stopCapture();
                                capture.startCapture();
                                break;
                        }
                    }
                }

            } catch (DPFPImageQualityException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnIdentify = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAreaInfo = new javax.swing.JTextArea();
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
        btnEnroll = new javax.swing.JButton();
        btnCancelEnrollment = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cmbSensorList = new javax.swing.JComboBox<>();
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
        lblStateIcon = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        listLatestInserts = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnIdentify.setText("Identificar Persona con este sensor");
        btnIdentify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIdentifyActionPerformed(evt);
            }
        });

        btnExit.setText("Salir del programa");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        txtAreaInfo.setColumns(20);
        txtAreaInfo.setRows(5);
        jScrollPane2.setViewportView(txtAreaInfo);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Huella Digital", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHuella, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Nombre: ");

        jLabel3.setText("RUT:");

        jLabel5.setText("Tipo de ingreso: ");

        cbUserType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione un opción", "Estudiante", "Docente", "Personal", "Proveedor" }));
        cbUserType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbUserTypeItemStateChanged(evt);
            }
        });

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
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(44, 44, 44)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtName)
                            .addComponent(txtRut)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbUserType, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnConfirmData, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addGap(20, 20, 20)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbUserType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConfirmData))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        btnEnroll.setText("Confirme datos antes de enrolar");
        btnEnroll.setEnabled(false);
        btnEnroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnrollActionPerformed(evt);
            }
        });

        btnCancelEnrollment.setText("Cancelar");
        btnCancelEnrollment.setEnabled(false);
        btnCancelEnrollment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelEnrollmentActionPerformed(evt);
            }
        });

        jLabel1.setText("*Para enrolar, debe haber 1 solo sensor activo en ese estado");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 519, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(116, 116, 116)
                                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(113, 113, 113))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(btnEnroll, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(161, 161, 161))))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancelEnrollment)
                .addGap(225, 225, 225))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEnroll)
                .addGap(18, 18, 18)
                .addComponent(btnCancelEnrollment)
                .addGap(33, 33, 33)
                .addComponent(jLabel1)
                .addGap(29, 29, 29))
        );

        cmbSensorList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione una opción" }));
        cmbSensorList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbSensorListItemStateChanged(evt);
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

        spTemperature.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(30.0f), Float.valueOf(30.0f), Float.valueOf(42.0f), Float.valueOf(0.1f)));

        lblTypeIdentificated.setText("--------------");

        lblRutIdentificated.setText("--------------");

        lblNombreIdentificated.setText("--------------");

        btnSaveIdentifiedUser.setText("Guardar");
        btnSaveIdentifiedUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveIdentifiedUserActionPerformed(evt);
            }
        });

        jLabel9.setText("Estado:");

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
                        .addGap(10, 10, 10)
                        .addComponent(lblTypeIdentificated, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spTemperature, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblNombreIdentificated, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel9))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblRutIdentificated, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStateIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81))))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(lblRutIdentificated))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(lblTypeIdentificated))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(spTemperature, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(lblStateIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 106, Short.MAX_VALUE)))
                .addGap(15, 15, 15)
                .addComponent(btnSaveIdentifiedUser, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Ultimos Registros"));

        listLatestInserts.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jScrollPane4.setViewportView(listLatestInserts);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(cmbSensorList, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnIdentify)
                                        .addGap(40, 40, 40)))
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 9, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(179, 179, 179)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmbSensorList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnIdentify, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1170, 770));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnIdentifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdentifyActionPerformed

        userService.retriveUserListFromDatabase(); // hace el select y obtiene todos los usuarios internamente
        String sensorId = cmbSensorList.getSelectedItem().toString();
        sensorAdministrator.changeSensorBehivor("{" + sensorId + "}", FPSensorBehivor.VALIDATING);

        JOptionPane.showMessageDialog(this, "Sensor " + cmbSensorList.getSelectedItem().toString() + " está Identificando.\nLos Datos se mostrarán en el apartado de 'identificación'\ncuando el sensor detecte información");


    }//GEN-LAST:event_btnIdentifyActionPerformed

    private void btnEnrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnrollActionPerformed

        txtAreaInfo.setText("Enrolando, porfavor ponga su dedo 4 veces en el sensor");
        FPSensorBehivor enrolling = FPSensorBehivor.ENROLLING;
        DPFPGlobal.getEnrollmentFactory().createEnrollment();

        System.out.println("ENROLANDO");


    }//GEN-LAST:event_btnEnrollActionPerformed

    private void btnConfirmDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmDataActionPerformed
        btnCancelEnrollment.setText("Cancelar enrolamiento");

        nombre = txtName.getText();
        rut = txtRut.getText();

        userTypeId = cbUserType.getSelectedIndex();
        // Orden comboBox Estudiante - Docente - Personal - Proveedor

        //Confirmacion Datos Enrolamiento
        confirmEnrollmentData();

    }//GEN-LAST:event_btnConfirmDataActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void cmbSensorListItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbSensorListItemStateChanged
        if (cmbSensorList.getSelectedIndex() > 0) {
            btnIdentify.setEnabled(true);
        } else {
            btnIdentify.setEnabled(false);
        }
    }//GEN-LAST:event_cmbSensorListItemStateChanged

    private void cbUserTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbUserTypeItemStateChanged
        if (cbUserType.getSelectedIndex() == 0) {
            btnConfirmData.setEnabled(false);
        }
    }//GEN-LAST:event_cbUserTypeItemStateChanged

    private void btnCancelEnrollmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelEnrollmentActionPerformed
        clearEnrollFrame();
    }//GEN-LAST:event_btnCancelEnrollmentActionPerformed

    private void btnSaveIdentifiedUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveIdentifiedUserActionPerformed

        System.out.println("NO ME CAIGO 1");
        if (JOptionPane.showConfirmDialog(null, "¿Los datos rescatados son los correctos?", "Confirmación", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(null, "Datos ingresados correctamente, persona registrada en la base de datos.");
            try {
                System.out.println("NO ME CAIGO 2");
                List<FPUser> usersList = data.getUserIdByRut(lblRutIdentificated.getText());
                System.out.println("NO ME CAIGO 3");
                FPUser fPUser = new FPUser();
                System.out.println("NO ME CAIGO 4");
                for (int i = 0; i < usersList.size(); i++) {
                    System.out.println("NO ME CAIGO " + i + 4);
                    fPUser.setUserId((int) usersList.get(i).getUserId());
                }

                System.out.println("USER ID:" + fPUser.getUserId());
                connection.ejecutar("INSERT INTO history VALUES(NULL, " + fPUser.getUserId() + ", NOW(),'" + spTemperature.getValue() + "');");

            } catch (SQLException ex) {
                Logger.getLogger(mainForm.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }//GEN-LAST:event_btnSaveIdentifiedUserActionPerformed

    /*
    
    
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {
            try {
                new mainForm().setVisible(true);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(mainForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(mainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelEnrollment;
    private javax.swing.JButton btnConfirmData;
    private javax.swing.JButton btnEnroll;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnIdentify;
    private javax.swing.JButton btnSaveIdentifiedUser;
    private javax.swing.JComboBox<String> cbUserType;
    private javax.swing.JComboBox<String> cmbSensorList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblHuella;
    private javax.swing.JLabel lblNombreIdentificated;
    private javax.swing.JLabel lblRutIdentificated;
    private javax.swing.JLabel lblStateIcon;
    private javax.swing.JLabel lblTypeIdentificated;
    private javax.swing.JList<String> listLatestInserts;
    private javax.swing.JSpinner spTemperature;
    private javax.swing.JTextArea txtAreaInfo;
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

        btnEnroll.setEnabled(true);
        btnEnroll.setText("Enrolar");
        txtName.setEnabled(false);
        txtRut.setEnabled(false);
        cbUserType.setEnabled(false);

        txtAreaInfo.setText("\n Datos confirmados, Presione 'enrolar' para iniciar el proceso de enrolamiento.");

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
    }

    private void userVerificated(DBUser user) {
        log.info("USER DETECTED!:" + user);
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

        lblStateIcon.setIcon(new ImageIcon(okImageIcon.getImage().getScaledInstance(lblStateIcon.getWidth(), lblHuella.getHeight(), Image.SCALE_DEFAULT)));

    }

}
