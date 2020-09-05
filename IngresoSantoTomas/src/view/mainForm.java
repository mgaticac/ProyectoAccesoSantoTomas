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
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import java.awt.Dimension;
import java.awt.Image;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
        sa = SensorAdministrator.getInstance();
        sa.changeSensorBehivor(sensorId, FPSensorBehivor.NONE);
        lblHuella.setPreferredSize(
                new Dimension(300, 250));
        lblHuella.setBorder(BorderFactory.createLoweredBevelBorder());
        //INICIALIZACION DE LISTENERS

        sensorId = SensorUtils.getSensorsSerialIds().get(0);
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
        sa.addEnrollingListener(
                new EnrollingListener() {

            @Override
            public void enrollingEvent(DPFPSample data
            ) {
                DPFPFeatureSet features;
                try {

                    features = SensorUtils.getFeatureSet(data, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
                    Image img = CrearImagenHuella(data);
                    DibujarHuella(img);
                    if (features != null) {
                        stats();
                        try {

                            //makeReport("The fingerprint feature set was created.");
                            enroller.addFeatures(features);		// Add feature set to template.
                            System.out.println("features " + enroller.getFeaturesNeeded());
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
        System.out.println("ENROLLING EVENT, " + enroller.getFeaturesNeeded() + "finger print left");
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
        btnCancelEnrollment = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnEnroll = new javax.swing.JButton();
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHuella, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP));

        txtAreaInformacion.setEditable(false);
        txtAreaInformacion.setColumns(20);
        txtAreaInformacion.setRows(5);
        txtAreaInformacion.setFocusable(false);
        jScrollPane1.setViewportView(txtAreaInformacion);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );

        btnCancelEnrollment.setText("Cancelar");
        btnCancelEnrollment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelEnrollmentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(220, 220, 220)
                .addComponent(btnCancelEnrollment, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(227, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancelEnrollment)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout enrollingFrameLayout = new javax.swing.GroupLayout(enrollingFrame.getContentPane());
        enrollingFrame.getContentPane().setLayout(enrollingFrameLayout);
        enrollingFrameLayout.setHorizontalGroup(
            enrollingFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        enrollingFrameLayout.setVerticalGroup(
            enrollingFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enrollingFrameLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

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

    private void btnEnrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnrollActionPerformed

        FPSensorBehivor enrolling = FPSensorBehivor.ENROLLING;
        sa.changeSensorBehivor(sensorId, enrolling);

        System.out.println("ENROLANDO");

        enrollingFrame.setVisible(true);
        enrollingFrame.setSize(753, 350);
        enrollingFrame.setResizable(false);

    }//GEN-LAST:event_btnEnrollActionPerformed

    private void btnVerifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyActionPerformed
        FPSensorBehivor validating = FPSensorBehivor.VALIDATING;
        sa.changeSensorBehivor(sensorId, validating);

        System.out.println("VALIDANDO");


    }//GEN-LAST:event_btnVerifyActionPerformed

    private void btnIdentifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIdentifyActionPerformed
        // SE IDENTIFICARÁ LA PERSONA PARA SU INGRESO A LA ISNITUCION, IMPLEMENTACION FUTURA
    }//GEN-LAST:event_btnIdentifyActionPerformed

    private void btnCancelEnrollmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelEnrollmentActionPerformed
        // Cancelar enrolado y volver al menú anterior
        //programar stop de sensor

        enrollingFrame.setVisible(false);

    }//GEN-LAST:event_btnCancelEnrollmentActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new mainForm().setVisible(true);

            }
        });

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelEnrollment;
    private javax.swing.JButton btnEnroll;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnIdentify;
    private javax.swing.JButton btnVerify;
    private javax.swing.JFrame enrollingFrame;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHuella;
    private javax.swing.JTextArea txtAreaInformacion;
    // End of variables declaration//GEN-END:variables

    public Image CrearImagenHuella(DPFPSample sample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }

    public void DibujarHuella(Image image) {

        lblHuella.setIcon(new ImageIcon(
                image.getScaledInstance(lblHuella.getWidth(), lblHuella.getHeight(), Image.SCALE_DEFAULT)));
    }

}
