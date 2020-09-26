package service;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import java.awt.Image;
import model.EnrollingListener;
import model.FPSensor;
import util.SensorUtils;

public class FPSensorEnrollmentService extends FPSensor implements EnrollingListener {

    private DPFPEnrollment enroller = DPFPGlobal.getEnrollmentFactory().createEnrollment();

    public FPSensorEnrollmentService(String serialId) {
        super(serialId);

    }

    @Override
    public void enrollingEvent(DPFPSample data) {

        DPFPFeatureSet features;
        if (enroller.getFeaturesNeeded() > 0) {
            try {

                features = SensorUtils.getFeatureSet(data, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

                if (features != null) {
                    try {
                        enroller.addFeatures(features);

                    } catch (DPFPImageQualityException ex) {
                    } finally {

                        switch (enroller.getTemplateStatus()) {
                            case TEMPLATE_STATUS_READY: {
                                DPFPTemplate template = enroller.getTemplate();

                            }
                            enroller.clear();
                            break;
                            case TEMPLATE_STATUS_FAILED:
                                enroller.clear();
                                stopReading();
                                startReading();
                                break;
                        }
                    }
                }

            } catch (DPFPImageQualityException e) {
                e.printStackTrace();
            }
        }

    }

    public Image crearImagenHuella(DPFPSample sample) {
        return DPFPGlobal.getSampleConversionFactory().createImage(sample);
    }

}
