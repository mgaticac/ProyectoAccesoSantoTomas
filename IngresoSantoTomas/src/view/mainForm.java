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
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.DPFPCapturePriority;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPDataListener;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusListener;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorListener;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.readers.DPFPReaderDescription;
import com.digitalpersona.onetouch.readers.DPFPReadersCollection;
import java.awt.Dimension;
import java.awt.Image;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import model.Conexion;
import model.EnrollingListener;
import model.FPSensorBehivor;
import model.FPUser;
import model.SensorAdministrator;
import model.VerificationListener;
import util.SensorUtils;

public class mainForm extends javax.swing.JFrame {

    private Conexion c;
    private SensorAdministrator sa;
    private String sensorId;
    private DPFPEnrollment enroller;
    private DPFPCapture capturer;

    public mainForm() {
        initComponents();
        IniciarLector();
        sa = SensorAdministrator.getInstance();
        sensorId = SensorUtils.getSensorsSerialIds().get(0);
        enroller = DPFPGlobal.getEnrollmentFactory().createEnrollment();
        capturer = DPFPGlobal.getCaptureFactory().createCapture();

        lblHuella.setPreferredSize(
                new Dimension(300, 250));
        lblHuella.setBorder(BorderFactory.createLoweredBevelBorder());

        //INICIALIZACION DE LISTENERS
        //LISTENER PARA VERIFICAR >>>>>>>
        sa.addVerificationListener(
                new VerificationListener() {
            @Override
            public void verificationEvent(Optional<FPUser> user
            ) {

                if (user.isPresent()) {
                    FPUser fpUser = user.get();
                    System.out.println("Usuario encontrado" + fpUser);
                } else {
                    System.out.println("Usuario no encontrado");
                }
            }
        }
        );
        // <<<<<<< LISTENER PARA VERIFICAR 

        //LISTENER PARA ENROLAR >>>>>>>
        System.out.println("HOLAAA 1");
        sa.addEnrollingListener(
                new EnrollingListener() {
            DPFPFeatureSet features;

            @Override
            public void enrollingEvent(DPFPSample data
            ) {
                try {
                     System.out.println("ENROLLING EVENT STARTED");
                    features = SensorUtils.getFeatureSet(data, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
                    if (features != null) {
                        stats();
                        try {
                            //makeReport("The fingerprint feature set was created.");
                            enroller.addFeatures(features);		// Add feature set to template.
                        } catch (DPFPImageQualityException ex) {
                        } finally {

                            // Check if template has been created.
                            switch (enroller.getTemplateStatus()) {
                                case TEMPLATE_STATUS_READY:	// report success and stop capturing
                                    stats();
                                    //((MainForm) getOwner()).setTemplate(enroller.getTemplate());
                                    //setPrompt("Click Close, and then click Fingerprint Verification.");
                                    break;

                                case TEMPLATE_STATUS_FAILED:	// report failure and restart capturing
                                    enroller.clear();
                                    capturer.stopCapture();
                                    stats();
                                    //((MainForm) getOwner()).setTemplate(null);
                                    capturer.startCapture();
                                    break;
                            }
                            try {
                                c.ejecutar("INSERT INTO user VALUES(NULL, 'Marcelo Gatica Contreras', '19.387.802-4','36.2',4,'" + features.toString() + "';)");
                            } catch (SQLException ex) {
                                Logger.getLogger(mainForm.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                } catch (DPFPImageQualityException e) {
                    e.printStackTrace();
                }

            }
        }
        );
        // <<<<<<< LISTENER PARA ENROLAR 

    }

    private void stats() {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + enroller.getFeaturesNeeded());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnEnroll = new javax.swing.JButton();
        btnVerify = new javax.swing.JButton();
        btnIdentify = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblHuella = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaHuella = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Huella Digital", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jPanel1.setFocusable(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnEnroll.setText("Enrolar Huella");
        btnEnroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnrollActionPerformed(evt);
            }
        });

        btnVerify.setText("Verificar Existencia");
        btnVerify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerifyActionPerformed(evt);
            }
        });

        btnIdentify.setText("Identificar Persona");

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
                    .addComponent(btnEnroll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(btnEnroll, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerify, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnIdentify, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42))
        );

        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel3.setFocusable(false);

        lblHuella.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHuella.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblHuella.setFocusable(false);
        jPanel3.add(lblHuella);

        txtAreaHuella.setEditable(false);
        txtAreaHuella.setColumns(20);
        txtAreaHuella.setRows(5);
        jScrollPane1.setViewportView(txtAreaHuella);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnEnrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnrollActionPerformed
        FPSensorBehivor enrolling = FPSensorBehivor.ENROLLING;
        sa.changeSensorBehivor(sensorId, enrolling);

        System.out.println("ENROLANDO");
        lblHuella.setText("");
        txtAreaHuella.setText(txtAreaHuella.getText() + "\n Changing sensor " + sensorId + " to ENROLLING");
    }//GEN-LAST:event_btnEnrollActionPerformed

    private void btnVerifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyActionPerformed
        FPSensorBehivor validating = FPSensorBehivor.VALIDATING;
        sa.changeSensorBehivor(sensorId, validating);

        System.out.println("VALIDANDO");

        lblHuella.setText("");
        txtAreaHuella.setText(txtAreaHuella.getText() + "\n Changing sensor " + sensorId + " to VALIDATING");
    }//GEN-LAST:event_btnVerifyActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new mainForm().setVisible(true);

            }
        });

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnroll;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnIdentify;
    private javax.swing.JButton btnVerify;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHuella;
    private javax.swing.JTextArea txtAreaHuella;
    // End of variables declaration//GEN-END:variables

    private void IniciarLector() {
        DPFPReadersCollection readers = DPFPGlobal.getReadersFactory().getReaders();
        if (readers == null || readers.isEmpty()) {
            System.out.println("Sin Lectores!");
            return;
        }

        System.out.println("ID de Lectores Disponibles");
        for (DPFPReaderDescription readerDescription : readers) {
            //Iniciando lector
            String idLector = readerDescription.getSerialNumber();
            System.out.println(readerDescription.getSerialNumber());
            capturer = DPFPGlobal.getCaptureFactory().createCapture();
            capturer.setReaderSerialNumber(idLector);
            capturer.setPriority(DPFPCapturePriority.CAPTURE_PRIORITY_LOW);

            capturer.addDataListener(new DPFPDataListener() {
                @Override
                public void dataAcquired(DPFPDataEvent dpfpDataEvent) {
                    //Se adquiere data
                    Image img = CrearImagenHuella(dpfpDataEvent.getSample());
                    DibujarHuella(img);
                }
            });

            capturer.addReaderStatusListener(new DPFPReaderStatusListener() {
                @Override
                public void readerConnected(DPFPReaderStatusEvent dpfpReaderStatusEvent) {
                    System.out.println("Reader conectado!");
                }

                @Override
                public void readerDisconnected(DPFPReaderStatusEvent dpfpReaderStatusEvent) {
                    System.out.println("Reader desconectado!");
                }
            });
            capturer.addSensorListener(new DPFPSensorListener() {
                @Override
                public void fingerTouched(DPFPSensorEvent dpfpSensorEvent) {
                    //dedo colocado en sensor
                }

                @Override
                public void fingerGone(DPFPSensorEvent dpfpSensorEvent) {
                    //dedo quitado del sensor
                }

                @Override
                public void imageAcquired(DPFPSensorEvent dpfpSensorEvent) {

                }
            });
            try {
                capturer.startCapture();

            } catch (RuntimeException e) {
                System.out.printf("Failed to start capture. Check that reader is not used by another application.\n");
                throw e;
            }

        }

    }

    public Image CrearImagenHuella(DPFPSample sample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }

    public void DibujarHuella(Image image) {

        lblHuella.setIcon(new ImageIcon(
                image.getScaledInstance(lblHuella.getWidth(), lblHuella.getHeight(), Image.SCALE_DEFAULT)));

    }
    
}
