package model;

import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;

@FunctionalInterface
public interface SensorFingerListener {
    void dataAdquired(String serialId, FPSensorBehivor behivor, DPFPSample dataEvent);
}
