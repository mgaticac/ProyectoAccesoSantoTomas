package view;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import config.FPConfig;
import java.awt.Dimension;
import java.awt.Image;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import database.Conexion;
import database.dao.DaoHistorial;
import database.dao.UserDao;
import database.dao.impl.HistoryDaoImpl;
import database.dao.impl.UserDaoImpl;
import database.model.DBHistory;
import database.model.DBUser;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import model.EnrollingListener;
import model.FPSensor;
import model.FPSensorBehivor;
import model.FPUser;
import service.SensorAdministrator;
import model.VerificationListener;
import service.FPSensorVerificationService;
import service.FPUserService;
import util.SensorUtils;
import util.UserUtils;

public class mainForm extends javax.swing.JFrame implements EnrollingListener, VerificationListener {

    //LOGGER SETUP
    public static Logger log = Logger.getLogger(mainForm.class.getName());
    private DPFPEnrollment dPFPEnrollment;

    //UI
    SensorTableThread stt = new SensorTableThread();
    VerifyInformationTimer vit = new VerifyInformationTimer();
    //DATA
    private Conexion connection;
    private List<String> sensorIds;
    private FPConfig fpconfig;

    //DAO
    private UserDao userDao;
    private DaoHistorial histDao;

    //SERVICES    
    private FPUserService userService;
    private SensorAdministrator sensorAdministrator;
    private FPSensorVerificationService verificationService;

    public mainForm(Conexion con, FPConfig fpconfig) {

        this.fpconfig = fpconfig;
        // GUI
        initComponents();

        setLocationRelativeTo(null);
        lblHuella.setPreferredSize(new Dimension(300, 250));
        lblHuella.setBorder(BorderFactory.createLoweredBevelBorder());

        // VALUES
        sensorIds = SensorUtils.getSensorsSerialIds();

        // DATA
        connection = con;

        //DAO
        userDao = new UserDaoImpl(connection, this.fpconfig);
        histDao = new HistoryDaoImpl(con);

        // SERVICES
        sensorAdministrator = new SensorAdministrator(userDao); // admin contains all services
        userService = sensorAdministrator.getUserService();
        verificationService = sensorAdministrator.getVerificationService();

        // BEHAVIOR
        for (int i = 0; i < sensorIds.size(); i++) {
            log.log(Level.INFO, "ID SENSOR Detected:" + sensorIds.get(i));
            sensorAdministrator.changeSensorBehivor(sensorIds.get(i), FPSensorBehivor.VALIDATING);
            cbEnrollSensor.addItem(sensorIds.get(i));
        }

        sensorAdministrator.addEnrollingListener(this::enrollingEvent);
        sensorAdministrator.addVerificationListener(this::verificationEvent);
        listLastEnrollments();
        listLastVerified();
        stt.start();

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

            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            txtAreaState.setText("Usuario no encontrado!\n"
                    + "Fecha y Hora: " + hourdateFormat.format(date));

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
                    log.fine("Enroller status:" + dPFPEnrollment.getTemplateStatus() + "\tfeatures:" + dPFPEnrollment.getFeaturesNeeded());
                    switch (dPFPEnrollment.getFeaturesNeeded()) {
                        case 1:
                            txtAreaInfo.setText("Falta " + dPFPEnrollment.getFeaturesNeeded() + " muestra, porfavor continue.");
                            break;
                        case 0:
                            txtAreaInfo.setText("Tarea Completada!");
                            break;
                        default:
                            txtAreaInfo.setText("Faltan " + dPFPEnrollment.getFeaturesNeeded() + " muestras, porfavor continue.");
                            break;
                    }
                    switch (dPFPEnrollment.getTemplateStatus()) {

                        case TEMPLATE_STATUS_READY:

                            DPFPTemplate template = dPFPEnrollment.getTemplate();
                            DBUser dbUser = new DBUser(txtName.getText(), txtRut.getText(), cbUserType.getSelectedIndex(), template.serialize());
                            userDao.add(dbUser);
                            dPFPEnrollment.clear();
                            capture.stopCapture();
                            JOptionPane.showMessageDialog(this, "Enrolamiento exitoso\nse limpiará la ventana de enrolamiento", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            sensorAdministrator.changeSensorBehivor(cbEnrollSensor.getSelectedItem().toString(), FPSensorBehivor.VALIDATING);
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

                dPFPEnrollment.clear();
                int choice = JOptionPane.showConfirmDialog(this,
                        String.format("Error al procesar imagen, se pusieron muchos dedos o \nel sensor está sucio. Pruebe a limpiar el sensor.\n"
                                + "se limpiará el fomulario si selecciona 'no'\n"
                                + "¿Desea intentarlo nuevamente?"),
                        "Error",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                switch (choice) {
                    case JOptionPane.NO_OPTION:
                        sensorAdministrator.changeSensorBehivor(cbEnrollSensor.getSelectedItem().toString(), FPSensorBehivor.VALIDATING);
                        clearEnrollFrame();
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        sensorAdministrator.changeSensorBehivor(cbEnrollSensor.getSelectedItem().toString(), FPSensorBehivor.VALIDATING);
                        clearEnrollFrame();
                        break;
                    case JOptionPane.YES_OPTION:
                        reStartEnrollmentService();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblTypeIdentificated = new javax.swing.JLabel();
        lblRutIdentificated = new javax.swing.JLabel();
        lblNombreIdentificated = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaState = new javax.swing.JTextArea();
        jPanel10 = new javax.swing.JPanel();
        btnExportDailyData = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtableSensorBehivor = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        lblHuella = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAreaInfo = new javax.swing.JTextArea();
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
        btnCancelEnrollment = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        btnShowXEnrollmentRecords = new javax.swing.JButton();
        spinnerEnrollmentsQuantity = new javax.swing.JSpinner();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableIdentifiedUsers = new javax.swing.JTable();
        spinnerVerifiedQuantity = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        btnShowXVerifyRecords = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Identificación", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel4.setText("Nombre: ");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel6.setText("Rut:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel7.setText("Tipo:");

        lblTypeIdentificated.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblTypeIdentificated.setText("--------------");

        lblRutIdentificated.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblRutIdentificated.setText("--------------");

        lblNombreIdentificated.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lblNombreIdentificated.setText("--------------");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel9.setText("Observación:");

        txtAreaState.setEditable(false);
        txtAreaState.setBackground(new java.awt.Color(231, 231, 231));
        txtAreaState.setColumns(15);
        txtAreaState.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtAreaState.setRows(5);
        jScrollPane1.setViewportView(txtAreaState);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTypeIdentificated, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNombreIdentificated, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRutIdentificated, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(67, 67, 67))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(252, 252, 252)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombreIdentificated)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblTypeIdentificated))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblRutIdentificated))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Informe", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        btnExportDailyData.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
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
                .addGap(129, 129, 129)
                .addComponent(btnExportDailyData, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addComponent(btnExportDailyData, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 17, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Información actual sensores", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 0, 18)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jtableSensorBehivor.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jtableSensorBehivor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Sensor ID", "Comportamiento"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jtableSensorBehivor);
        if (jtableSensorBehivor.getColumnModel().getColumnCount() > 0) {
            jtableSensorBehivor.getColumnModel().getColumn(0).setResizable(false);
            jtableSensorBehivor.getColumnModel().getColumn(1).setResizable(false);
            jtableSensorBehivor.getColumnModel().getColumn(1).setPreferredWidth(5);
        }

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Huella Digital", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 18))); // NOI18N
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblHuella.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel4.add(lblHuella, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 250, 240));

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Información", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        txtAreaInfo.setEditable(false);
        txtAreaInfo.setBackground(new java.awt.Color(231, 231, 231));
        txtAreaInfo.setColumns(20);
        txtAreaInfo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        txtAreaInfo.setForeground(new java.awt.Color(255, 0, 51));
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

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel2.setText("Nombre: ");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel3.setText("RUT:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel5.setText("Tipo de ingreso: ");

        txtName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        txtRut.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cbUserType.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        cbUserType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione un opción", "Estudiante", "Docente", "Personal", "Proveedor" }));

        btnConfirmData.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnConfirmData.setText("Confirmar datos y  Enrolar");
        btnConfirmData.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnConfirmData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmDataActionPerformed(evt);
            }
        });

        cbEnrollSensor.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        cbEnrollSensor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione una opción" }));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setText("Sensor:");

        btnCancelEnrollment.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCancelEnrollment.setText("Cancelar");
        btnCancelEnrollment.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCancelEnrollment.setEnabled(false);
        btnCancelEnrollment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelEnrollmentActionPerformed(evt);
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
                        .addGap(80, 80, 80)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRut, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel10))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnConfirmData, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(82, 82, 82)
                                .addComponent(btnCancelEnrollment, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(cbUserType, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbEnrollSensor, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtRut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbUserType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbEnrollSensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConfirmData, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelEnrollment, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(208, 208, 208)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Menú Principal", jPanel1);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Personas Agregadas al sistema", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        dataTable.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "RUT", "Nombre"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setText("Cantidad de registros a mostrar:");

        btnShowXEnrollmentRecords.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnShowXEnrollmentRecords.setText("Actualizar");
        btnShowXEnrollmentRecords.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnShowXEnrollmentRecords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowXEnrollmentRecordsActionPerformed(evt);
            }
        });

        spinnerEnrollmentsQuantity.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        spinnerEnrollmentsQuantity.setModel(new javax.swing.SpinnerNumberModel(15, 1, null, 1));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(spinnerEnrollmentsQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnShowXEnrollmentRecords, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnShowXEnrollmentRecords, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerEnrollmentsQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Personas Identificadas", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 18))); // NOI18N

        jTableIdentifiedUsers.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTableIdentifiedUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Nombre", "Rut", "Tipo de usuario"
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
        jScrollPane4.setViewportView(jTableIdentifiedUsers);
        if (jTableIdentifiedUsers.getColumnModel().getColumnCount() > 0) {
            jTableIdentifiedUsers.getColumnModel().getColumn(0).setResizable(false);
            jTableIdentifiedUsers.getColumnModel().getColumn(1).setResizable(false);
            jTableIdentifiedUsers.getColumnModel().getColumn(2).setResizable(false);
        }

        spinnerVerifiedQuantity.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        spinnerVerifiedQuantity.setModel(new javax.swing.SpinnerNumberModel(15, 1, null, 1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setText("Cantidad de registros a mostrar:");

        btnShowXVerifyRecords.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnShowXVerifyRecords.setText("Actualizar");
        btnShowXVerifyRecords.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnShowXVerifyRecords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowXVerifyRecordsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spinnerVerifiedQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnShowXVerifyRecords, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
                    .addComponent(jScrollPane4))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnShowXVerifyRecords, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerVerifiedQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(392, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Información de Registros", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
                    String[] columnTitle = {"ID Usuario;", "Nombre;", "Rut;", "Fecha Verificacion\n"};
                    for (String columnTitle1 : columnTitle) {
                        stream.write(columnTitle1);
                    }
                    Optional<List<DBUser>> exportDailyData = userDao.exportDailyData();
                    if (exportDailyData.isPresent()) {
                        List<DBUser> listDailyUsers = exportDailyData.get();
                        for (DBUser user : listDailyUsers) {
                            stream.write(user.getId() + ";");
                            stream.write(user.getFullname() + ";");
                            stream.write(user.getRut() + ";");
                            stream.write(user.getVerifyDate() + "\n");
                        }
                    } else {
                        log.warning("No se encontraron registros");
                    }
                    stream.close();
                } catch (HeadlessException | IOException ex) {
                    JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Guardar Informe", JOptionPane.ERROR_MESSAGE);
                }
            }
            break;

        }
    }//GEN-LAST:event_btnExportDailyDataActionPerformed

    private void btnCancelEnrollmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelEnrollmentActionPerformed
        sensorAdministrator.changeSensorBehivor(cbEnrollSensor.getSelectedItem().toString(), FPSensorBehivor.VALIDATING);
        clearEnrollFrame();
    }//GEN-LAST:event_btnCancelEnrollmentActionPerformed

    private void btnConfirmDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmDataActionPerformed

        UserDaoImpl daoImpl = new UserDaoImpl(connection, fpconfig);
        boolean rutExists = daoImpl.userRutExists(txtRut.getText());

        if ((txtName.getText().equals("")) || (txtRut.getText().equals("")) || (cbEnrollSensor.getSelectedIndex() == 0) || (cbUserType.getSelectedIndex() == 0)) {

            JOptionPane.showMessageDialog(null, "No ha completado todos los campos, porfavor verifique");
        } else {

            if (!rutExists) {
                btnCancelEnrollment.setText("Cancelar enrolamiento");
                btnCancelEnrollment.setEnabled(true);

                confirmEnrollmentData();
                reStartEnrollmentService();

            } else {
                JOptionPane.showMessageDialog(null, "Rut ya existe, porfavor verifique la información");
            }

        }
    }//GEN-LAST:event_btnConfirmDataActionPerformed

    private void btnShowXEnrollmentRecordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowXEnrollmentRecordsActionPerformed
        listLastEnrollments();
    }//GEN-LAST:event_btnShowXEnrollmentRecordsActionPerformed

    private void btnShowXVerifyRecordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowXVerifyRecordsActionPerformed
        listLastVerified();
    }//GEN-LAST:event_btnShowXVerifyRecordsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelEnrollment;
    private javax.swing.JButton btnConfirmData;
    private javax.swing.JButton btnExportDailyData;
    private javax.swing.JButton btnShowXEnrollmentRecords;
    private javax.swing.JButton btnShowXVerifyRecords;
    private javax.swing.JComboBox<String> cbEnrollSensor;
    private javax.swing.JComboBox<String> cbUserType;
    private javax.swing.JTable dataTable;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableIdentifiedUsers;
    private javax.swing.JTable jtableSensorBehivor;
    private javax.swing.JLabel lblHuella;
    private javax.swing.JLabel lblNombreIdentificated;
    private javax.swing.JLabel lblRutIdentificated;
    private javax.swing.JLabel lblTypeIdentificated;
    private javax.swing.JSpinner spinnerEnrollmentsQuantity;
    private javax.swing.JSpinner spinnerVerifiedQuantity;
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

        txtAreaInfo.setText("Datos confirmados");

        txtName.setEnabled(false);
        txtRut.setEnabled(false);
        cbUserType.setEnabled(false);
        cbEnrollSensor.setEnabled(false);
    }

    private void clearEnrollFrame() {

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
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        txtAreaState.setText("Usuario identificado correctamente!\n"
                + "Fecha y Hora: " + hourdateFormat.format(date));

        String tipoPersona = "";

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

        Optional<DBUser> userByRut = userDao.getUserByRut(lblRutIdentificated.getText());
        if (userByRut.isPresent()) {
            FPUser fpUser = UserUtils.convertDBUserToFPUser(userByRut.get());
            DBHistory history = new DBHistory();
            history.setUserId(fpUser.getUserId());

            histDao.add(history);
            listLastVerified();

        } else { // TODO: generar manejo
            log.warning("Rut not founded");
        }

        if (vit.timer.isRunning()) {
            vit.timer.restart();
        } else {
            vit.timer.start();
        }

    }

    private void listLastEnrollments() {

        List<DBUser> latestEnrolledUsers = userDao.getLatestEnrollments(fpconfig.getInstituteId(), (int) spinnerEnrollmentsQuantity.getValue());
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.addColumn("Nombre");
        dtm.addColumn("Rut");

        latestEnrolledUsers.stream().map((userinfo) -> new String[]{
            userinfo.getRut(), userinfo.getFullname()
        }).forEachOrdered((userData) -> {
            dtm.addRow(userData);
        });
        dataTable.setModel(dtm);
        dataTable.sizeColumnsToFit(WIDTH);
        dataTable.sizeColumnsToFit(HEIGHT);

    }

    private void listLastVerified() {
        List<DBUser> latestVerifiedUsers = userDao.getLatestVerified(fpconfig.getInstituteId(), (int) spinnerVerifiedQuantity.getValue());
        DefaultTableModel dtm = new DefaultTableModel();

        dtm.addColumn("Nombre");
        dtm.addColumn("Rut");
        dtm.addColumn("Tipo de Usuario");
        dtm.addColumn("Fecha Ingreso");

        for (DBUser latestVerifiedUser : latestVerifiedUsers) {

            String tipoPersona = "";
            switch (latestVerifiedUser.getUserTypeIdFk()) {

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

            String[] userData = new String[]{
                latestVerifiedUser.getFullname(), latestVerifiedUser.getRut(), tipoPersona, latestVerifiedUser.getVerifyDate()
            };
            dtm.addRow(userData);

        }

        jTableIdentifiedUsers.setModel(dtm);
        jTableIdentifiedUsers.sizeColumnsToFit(WIDTH);

    }

    private void reStartEnrollmentService() {
        JOptionPane.showMessageDialog(this, "Se ha iniciado el proceso de enrolado. Haga click en Aceptar \ny siga las instrucciones mostradas en rojo (Panel de Información)");
        txtAreaInfo.setText("Enrolado iniciado, porfavor ponga su dedo 4 veces en el lector,\npresionando de forma considerada y levantando cuando\nse capture la imagen");
        String sensorEnrollingId = cbEnrollSensor.getSelectedItem().toString();
        sensorAdministrator.changeSensorBehivor(sensorEnrollingId, FPSensorBehivor.ENROLLING);
        dPFPEnrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    }

    public void getSensorsBehivors() {
        List<FPSensor> sensors = sensorAdministrator.getSensors();
        DefaultTableModel dtm = new DefaultTableModel();

        dtm.addColumn("Sensor ID");
        dtm.addColumn("Comportamiento");

        sensors.stream().map((sensor) -> new String[]{
            String.valueOf(sensor.getSerialId()), sensor.getBehivor().toString()
        }).forEachOrdered((sensorData) -> {
            dtm.addRow(sensorData);
        });
        jtableSensorBehivor.setModel(dtm);
        jtableSensorBehivor.sizeColumnsToFit(WIDTH);

    }

    //Los objetos de MiHilo se pueden ejecutar en sus propios hilos
// porque MiHilo implementa Runnable.
    class SensorTableThread extends Thread implements Runnable {

        public SensorTableThread() {

        }

        @Override
        public void run() {
            while (true) {
                getSensorsBehivors();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(mainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class VerifyInformationTimer extends Thread implements Runnable {

        Timer timer = new Timer(10000, (ActionEvent e) -> {
            clearVerifyInfo();
        });
    }

    public void clearVerifyInfo() {
        lblNombreIdentificated.setText("--------------");
        lblRutIdentificated.setText("--------------");
        lblTypeIdentificated.setText("--------------");
        txtAreaState.setText("");
    }

}
