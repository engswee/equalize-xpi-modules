package com.equalize.xpi.af.modules.tester.junit;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Test;

import com.equalize.xpi.af.modules.tester.ModuleTester;
import com.equalize.xpi.tester.util.ParameterHelper;
import com.equalize.xpi.tester.util.ResultHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class Base64EncodeTest {
	// Can't be compared due to differences in output caused by compression
/*	@Test
	public void testScenario1() throws IOException, MessagingException {
		String inputFile = "TestData/Module/Base64Bean/Base64Encode_Scenario1.xml";
		String paramFile = "TestData/Module/Base64Bean/Base64Encode_Scenario1_param.txt";
		String resultFile = "TestData/Module/Base64Bean/Base64Encode_Scenario1_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance(inputFile, contextData);

		assertEquals("Error", results, mt.mainTest());
	}*/
	@Test
	public void testScenario2() throws ModuleException {
		String inputFile = "TestData/Module/Base64Bean/Base64Encode_Scenario2.txt";
		String paramFile = "TestData/Module/Base64Bean/Base64Encode_Scenario2_param.txt";
		String resultFile = "TestData/Module/Base64Bean/Base64Encode_Scenario2_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
}
