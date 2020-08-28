package view;

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
        FPSensorBehivor enrolling = FPSensorBehivor.ENROLLING;
        String sensorId = SensorUtils.getSensorsSerialIds().get(0);

        System.out.println("Cabiando comportamiento de sensor" + sensorId + " a " + enrolling);
        sa.changeSensorBehivor(sensorId, FPSensorBehivor.ENROLLING);
        sa.addEnrollingListener(new EnrollingListener() {
            @Override
            public void enrollingEvent(DPFPSample data) {
                try {
                    DPFPFeatureSet featureSet = SensorUtils.getFeatureSet(data, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

                } catch (DPFPImageQualityException e) {
                    e.printStackTrace();
                }
                System.out.println("Tratando de enrolar");
            }
        });
    }

    public static void main(String[] args) {
        new Main();
    }
}
