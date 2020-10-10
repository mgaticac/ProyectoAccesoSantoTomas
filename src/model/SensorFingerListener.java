package model;

import com.digitalpersona.onetouch.DPFPSample;

@FunctionalInterface
public interface SensorFingerListener {
    void dataAdquired(String serialId, FPSensorBehivor behivor, DPFPSample dataEvent);
}
