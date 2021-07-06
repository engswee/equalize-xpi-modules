package com.equalize.xpi.af.modules.tester.junit;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Test;

import com.equalize.xpi.af.modules.tester.ModuleTester;
import com.equalize.xpi.tester.util.ParameterHelper;
import com.equalize.xpi.tester.util.ResultHelper;
import com.sap.engine.interfaces.messaging.api.exception.MessagingException;

public class DACBTest {

	@Test
	public void testScenario1() throws MessagingException {
		String inputFile = "TestData/Module/DACB/DACB_Scenario1_param.txt";
		String paramFile = "TestData/Module/DACB/DACB_Scenario1_param.txt";
		String dcOutputFile = "TestData/Module/DACB/DACB_Scenario1_dc_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String dcOutput = ResultHelper.newInstance(dcOutputFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.DynamicAttributeChangeBean", inputFile, contextData);
		mt.addDynCfg("http://sap.com/xi/XI/System/File", "FileName", "Order_1234.txt");
		
		mt.execute();
		assertEquals("Error", dcOutput, mt.getDynCfg());
	}
	@Test
	public void testScenario2() throws MessagingException {
		String inputFile = "TestData/Module/DACB/DACB_Scenario2_param.txt";
		String paramFile = "TestData/Module/DACB/DACB_Scenario2_param.txt";
		String dcOutputFile = "TestData/Module/DACB/DACB_Scenario2_dc_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String dcOutput = ResultHelper.newInstance(dcOutputFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.DynamicAttributeChangeBean", inputFile, contextData);
		mt.addDynCfg("http://sap.com/xi/XI/System/File", "FileName", "Order_1234.txt");
		
		mt.execute();
		assertEquals("Error", dcOutput, mt.getDynCfg());
	}
	@Test
	public void testScenario3() throws MessagingException {
		String inputFile = "TestData/Module/DACB/DACB_Scenario3_param.txt";
		String paramFile = "TestData/Module/DACB/DACB_Scenario3_param.txt";
		String dcOutputFile = "TestData/Module/DACB/DACB_Scenario3_dc_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String dcOutput = ResultHelper.newInstance(dcOutputFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.DynamicAttributeChangeBean", inputFile, contextData);
		mt.addDynCfg("http://sap.com/xi/XI/System/File", "FileName", "Order_1234.txt.pgp");
		
		mt.execute();
		assertEquals("Error", dcOutput, mt.getDynCfg());
	}
	@Test
	public void testScenario4() throws MessagingException {
		String inputFile = "TestData/Module/DACB/DACB_Scenario4_param.txt";
		String paramFile = "TestData/Module/DACB/DACB_Scenario4_param.txt";
		String dcOutputFile = "TestData/Module/DACB/DACB_Scenario4_dc_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String dcOutput = ResultHelper.newInstance(dcOutputFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.DynamicAttributeChangeBean", inputFile, contextData);
		mt.addDynCfg("http://sap.com/xi/XI/System/File", "FileName", "Order_1234_Batch_10.xml");
		
		mt.execute();
		assertEquals("Error", dcOutput, mt.getDynCfg());
	}
	@Test
	public void testScenario5() throws MessagingException {
		String inputFile = "TestData/Module/DACB/DACB_Scenario5_param.txt";
		String paramFile = "TestData/Module/DACB/DACB_Scenario5_param.txt";
		String dcOutputFile = "TestData/Module/DACB/DACB_Scenario5_dc_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String dcOutput = ResultHelper.newInstance(dcOutputFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.DynamicAttributeChangeBean", inputFile, contextData);
		mt.addDynCfg("http://sap.com/xi/XI/SFTP/SFTP", "FileName", "Partner1_002_Invoice.xml");
		
		mt.execute();
		assertEquals("Error", dcOutput, mt.getDynCfg());
	}
}
