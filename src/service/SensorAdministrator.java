package service;

import service.FPSensorVerificationService;
import service.FPUserService;
import util.SensorUtils;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import database.dao.UserDao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.EnrollingListener;
import model.FPSensor;
import model.FPSensorBehivor;
import model.FPUser;
import model.SensorFingerListener;
import model.VerificationListener;

public class SensorAdministrator implements SensorFingerListener {

    public static Logger log = Logger.getLogger(SensorAdministrator.class.getName());
    private static SensorAdministrator instance;
    private List<FPSensor> sensors;
    private List<EnrollingListener> enrollingListeners;

    private FPUserService userService;
    private FPSensorVerificationService verificationService;

    private List<VerificationListener> verificationListeners;
    private UserDao userDao;

    public SensorAdministrator(UserDao userDao) {
        this.userDao = userDao;
        userService = new FPUserService(this.userDao);
        verificationService = new FPSensorVerificationService(userService);

        FPSensor.defaultBehivor = FPSensorBehivor.VALIDATING;
        sensors = new ArrayList<>();
        List<String> sensorsSerialIds = SensorUtils.getSensorsSerialIds();
        sensorsSerialIds.forEach(s -> {
            FPSensor sensor = new FPSensor(s);
            sensors.add(sensor);
            sensor.addFingerListener(this);
            sensor.startReading();
        });
        log.info("Added sensors : " + String.valueOf(sensors.size()));
        if (sensorsSerialIds.isEmpty()) {
            log.warning("No sensors Detected");
        }
    }
    
    public List<FPSensor> getSensors(){
        return sensors;
    }

    public FPUserService getUserService() {
        return userService;
    }

    public FPSensorVerificationService getVerificationService() {
        return verificationService;
    }

    public void changeSensorBehivor(String sensorSerialId, FPSensorBehivor behivor) {

        for (FPSensor sensor : sensors) { //cambiad
            if (sensor.getSerialId().equals(sensorSerialId)) {
                sensor.setBehivor(behivor);
            }
        }
    }

    public void addVerificationListener(VerificationListener verificationListener) {
        if (verificationListeners == null) {
            verificationListeners = new ArrayList<>();
        }
        verificationListeners.add(verificationListener);
    }

    public void removeVerificationListener(VerificationListener verificationListener) {
        if (verificationListeners != null) {
            verificationListeners.remove(verificationListener);
        }
    }

    public void addEnrollingListener(EnrollingListener listener) {
        if (enrollingListeners == null) {
            enrollingListeners = new ArrayList<>();
        }
        enrollingListeners.add(listener);
    }

    public void removeEnrollingListener(EnrollingListener listener) {
        if (enrollingListeners != null) {
            enrollingListeners.remove(listener);
        }
    }

    private void fireEnrollingEvent(DPFPSample sample) {
        if (enrollingListeners != null) {
            enrollingListeners.forEach(l -> l.enrollingEvent(sample));
        }
    }

    private void fireValidatingEvent(Optional<FPUser> user) {
        if (verificationListeners != null) {
            verificationListeners.forEach(l -> l.verificationEvent(user));
        }
    }

    @Override
    public void dataAdquired(String sensorId, FPSensorBehivor behivor, DPFPSample sample) {
        switch (behivor) {
            case ENROLLING:
                log.info("Sensor " + sensorId + " is try to enrolling");
                enrolling(sample);
                break;
            case VALIDATING:
                log.info("Sensor " + sensorId + " is try to validate");
                 {
                    try {
                        validating(sample);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(SensorAdministrator.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(SensorAdministrator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            case NONE:
                log.warning("Sensor " + sensorId + " is in NONE status");
        }
    }

    private void validating(DPFPSample sample) throws ClassNotFoundException, SQLException {
        try {
            DPFPFeatureSet featureSet = SensorUtils.getFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
            Optional<FPUser> verify = verificationService.verify(featureSet);
            fireValidatingEvent(verify);
        } catch (DPFPImageQualityException e) {
            e.printStackTrace();
        }

    }

    private void enrolling(DPFPSample sample) {
        fireEnrollingEvent(sample);
    }

}
