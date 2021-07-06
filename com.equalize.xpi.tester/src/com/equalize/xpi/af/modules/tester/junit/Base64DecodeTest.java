package com.equalize.xpi.af.modules.tester.junit;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Test;

import com.equalize.xpi.af.modules.tester.ModuleTester;
import com.equalize.xpi.tester.util.ParameterHelper;
import com.equalize.xpi.tester.util.ResultHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class Base64DecodeTest {

	@Test
	public void testScenario1() throws ModuleException {
		String inputFile = "TestData/Module/Base64Bean/Base64Decode_Scenario1.txt";
		String paramFile = "TestData/Module/Base64Bean/Base64Decode_Scenario1_param.txt";
		String resultFile = "TestData/Module/Base64Bean/Base64Decode_Scenario1_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario2() throws ModuleException {
		String inputFile = "TestData/Module/Base64Bean/Base64Decode_Scenario2.txt";
		String paramFile = "TestData/Module/Base64Bean/Base64Decode_Scenario2_param.txt";
		String resultFile = "TestData/Module/Base64Bean/Base64Decode_Scenario2_output.jpg";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario3() throws ModuleException {
		String inputFile = "TestData/Module/Base64Bean/Base64Decode_Scenario3.xml";
		String paramFile = "TestData/Module/Base64Bean/Base64Decode_Scenario3_param.txt";
		String resultFile = "TestData/Module/Base64Bean/Base64Decode_Scenario3_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario4() throws ModuleException {
		String inputFile = "TestData/Module/Base64Bean/Base64Decode_Scenario4_WithHebrew.txt";
		String paramFile = "TestData/Module/Base64Bean/Base64Decode_Scenario4_WithHebrew_param.txt";
		String resultFile = "TestData/Module/Base64Bean/Base64Decode_Scenario4_WithHebrew_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
}
