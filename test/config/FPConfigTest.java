/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author klawx
 */
public class FPConfigTest {
    
    public FPConfigTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getInstituteId method, of class FPConfig.
     */
    @Test
    public void testGetInstituteId() throws IOException {
        System.out.println("getInstituteId");
        FPConfig instance = new FPConfig();
        int expResult = 0;
        int result = instance.getInstituteId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDbHost method, of class FPConfig.
     */
    @Test
    public void testGetDbHost() throws IOException {
        System.out.println("getDbHost");
        FPConfig instance = new FPConfig();
        String expResult = "";
        String result = instance.getDbHost();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDbName method, of class FPConfig.
     */
    @Test
    public void testGetDbName() throws IOException {
        System.out.println("getDbName");
        FPConfig instance = new FPConfig();
        String expResult = "";
        String result = instance.getDbName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDbPasswd method, of class FPConfig.
     */
    @Test
    public void testGetDbPasswd() throws IOException {
        System.out.println("getDbPasswd");
        FPConfig instance = new FPConfig();
        String expResult = "";
        String result = instance.getDbPasswd();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDbUser method, of class FPConfig.
     */
    @Test
    public void testGetDbUser() throws IOException {
        System.out.println("getDbUser");
        FPConfig instance = new FPConfig();
        String expResult = "";
        String result = instance.getDbUser();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class FPConfig.
     * @throws java.io.IOException
     */
    @Test
    public void testToString() throws IOException {
        System.out.println("toString");
        FPConfig instance = new FPConfig();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
