package util;

import model.FPSensor;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SensorUtils {
    public static Logger log = Logger.getLogger(FPSensor.class.getName());

    public static List<String> getSensorsSerialIds(){
        return DPFPGlobal.getReadersFactory().getReaders().stream()
                .map(r -> r.getSerialNumber())
                .collect(Collectors.toList());
    }

    public static DPFPFeatureSet getFeatureSet(DPFPSample sample, DPFPDataPurpose purpose) throws DPFPImageQualityException {
        DPFPFeatureExtraction featureExtractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        DPFPFeatureSet featureSet = featureExtractor.createFeatureSet(sample, purpose);
        return featureSet;
    }

}
