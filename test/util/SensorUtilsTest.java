/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPSample;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;

/**
 *
 * @author klawx
 */
public class SensorUtilsTest {
    

    @Test
    public void testGetSensorsSerialIds() {
        System.out.println("Getting at least 1 physical sensors");

        List<String> list = SensorUtils.getSensorsSerialIds();
        boolean result = list.size() >= 1;
        boolean expResult = true;

        Assertions.assertEquals(result, expResult);
    }

    @Test
    @Disabled("asd")
    public void testGetFeatureSet() throws Exception {
        System.out.println("getFeatureSet");
        DPFPSample sample = null;
        DPFPDataPurpose purpose = null;
        DPFPFeatureSet expResult = null;
        DPFPFeatureSet result = SensorUtils.getFeatureSet(sample, purpose);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }
    
}
