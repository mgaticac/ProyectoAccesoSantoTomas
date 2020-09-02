package view;

import model.SensorAdministrator;
import model.EnrollingListener;
import model.FPSensorBehivor;
import model.FPUser;
import model.VerificationListener;
import util.SensorUtils;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;

import java.util.Optional;

public class Main {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }

    public Main() {
        SensorAdministrator sa = SensorAdministrator.getInstance();

        //LISTENER PARA VERIFICAR >>>>>>>
        sa.addVerificationListener(new VerificationListener() {
            @Override
            public void verificationEvent(Optional<FPUser> user) {
                if (user.isPresent()) {
                    FPUser fpUser = user.get();
                    System.out.println("Usuario encontrado" + fpUser);
                } else {
                    System.out.println("Usuario no encontrado");
                }
            }
        });
        // <<<<<<< LISTENER PARA VERIFICAR 

        FPSensorBehivor enrolling = FPSensorBehivor.ENROLLING; // Cambia el comportamiento del sensor
        String sensorId = SensorUtils.getSensorsSerialIds().get(0);

        System.out.println("Cabiando comportamiento de sensor" + sensorId + " a " + enrolling);
        sa.changeSensorBehivor(sensorId, enrolling);

        //LISTENER PARA ENROLAR >>>>>>>
        sa.addEnrollingListener(new EnrollingListener() {
            @Override
            public void enrollingEvent(DPFPSample data) {
                try {
                    DPFPFeatureSet featureSet = SensorUtils.getFeatureSet(data, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

                } catch (DPFPImageQualityException e) {
                    e.printStackTrace();
                }

            }
        });
        // <<<<<<< LISTENER PARA ENROLAR 
        FPSensorBehivor validating = FPSensorBehivor.VALIDATING;
        sa.changeSensorBehivor(sensorId, validating);

    }

//    public static void main(String[] args) {
//        new Main();
//    }
}
