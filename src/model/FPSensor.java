package model;

import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.DPFPCapturePriority;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPDataListener;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FPSensor implements DPFPDataListener, Runnable {

    public static Logger log = Logger.getLogger(FPSensor.class.getName());

    public static FPSensorBehivor defaultBehivor = FPSensorBehivor.VALIDATING;

    private String serialId;
    private DPFPCapture capture;
    private Thread thread;
    private FPSensorBehivor behivor;
    private List<SensorFingerListener> listeners;
    private int lastStatus;

    public FPSensorBehivor getBehivor() {
        return behivor;
    }

    public FPSensor(String serialId) {
        this.serialId = serialId;
        capture = DPFPGlobal.getCaptureFactory().createCapture();
        capture.setReaderSerialNumber(serialId);
        capture.setPriority(DPFPCapturePriority.CAPTURE_PRIORITY_LOW);
        capture.addDataListener(this::dataAcquired);
        thread = new Thread(this::run);
        capture.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            public void readerConnected(DPFPReaderStatusEvent e) {
                if (lastStatus != e.getReaderStatus()) {
                    log.log(Level.INFO, "Reader {0} is connected", serialId);
                }
                lastStatus = e.getReaderStatus();
            }

            public void readerDisconnected(DPFPReaderStatusEvent e) {
                if (lastStatus != e.getReaderStatus()) {
                    log.log(Level.INFO, "Reader  {0} is disconnected", serialId);
                }
                lastStatus = e.getReaderStatus();
            }
        });
        behivor = defaultBehivor;
    }

    public String getSerialId() {
        return serialId;
    }

    public void setBehivor(FPSensorBehivor behivor) {
        log.log(Level.INFO, "Changing sensor {0} to {1}", new Object[]{serialId, behivor});
        this.behivor = behivor;
    }

    public void addFingerListener(SensorFingerListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    public void removeFingerListener(SensorFingerListener listener) {
        if (listeners == null) {
            listeners.remove(listener);
        }
    }

    public void startReading() {
        if (!capture.isStarted()) {
            log.log(Level.INFO, "Sensor {0} Started", serialId);
            thread.start();
        }

    }

    public void stopReading() {
        if (capture.isStarted()) {
            log.log(Level.INFO, "Sensor {0} Stopped", serialId);
            capture.stopCapture();
        }
    }

    private void fireEvent(FPSensorBehivor behivor, DPFPDataEvent dpfpDataEvent) {
        if (listeners != null) {
            listeners.forEach(l -> {
                l.dataAdquired(serialId, behivor, dpfpDataEvent.getSample());
                log.fine(String.format("Data Adquired on %s -> %s", serialId, dpfpDataEvent.toString()));
            });
        }
    }

    @Override
    public void dataAcquired(DPFPDataEvent dpfpDataEvent) {
        fireEvent(behivor, dpfpDataEvent);
    }

    @Override
    public void run() {
        capture.startCapture();
    }

}
