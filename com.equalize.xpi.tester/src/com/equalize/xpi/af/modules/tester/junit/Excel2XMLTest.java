package com.equalize.xpi.af.modules.tester.junit;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Test;

import com.equalize.xpi.af.modules.tester.ModuleTester;
import com.equalize.xpi.tester.util.ParameterHelper;
import com.equalize.xpi.tester.util.ResultHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class Excel2XMLTest {

	@Test
	public void testScenario1() throws ModuleException {
		String inputFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario1.xlsx";
		String paramFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario1_param.txt";
		String resultFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario1_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario2() throws ModuleException {
		String inputFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario2.xls";
		String paramFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario2_param.txt";
		String resultFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario2_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario3() throws ModuleException {
		String inputFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario3.xlsx";
		String paramFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario3_param.txt";
		String resultFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario3_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario4() throws ModuleException {
		// Test new parameters headerRow & onlyValidCharsInXMLName
		String inputFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario4.xlsx";
		String paramFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario4_param.txt";
		String resultFile = "TestData/Module/ExcelTransformBean/Excel2XML_Scenario4_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
}
