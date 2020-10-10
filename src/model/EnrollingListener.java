package model;

import com.digitalpersona.onetouch.DPFPSample;

@FunctionalInterface
public interface EnrollingListener {

    void enrollingEvent(DPFPSample data);
}
