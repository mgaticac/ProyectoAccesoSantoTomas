package model;

import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;

@FunctionalInterface
public interface EnrollingListener {
    void enrollingEvent(DPFPSample data);
}
