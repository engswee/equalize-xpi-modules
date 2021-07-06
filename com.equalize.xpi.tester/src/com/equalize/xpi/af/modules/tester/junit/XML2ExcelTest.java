package com.equalize.xpi.af.modules.tester.junit;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Test;

import com.equalize.xpi.af.modules.tester.ModuleTester;
import com.equalize.xpi.tester.util.ParameterHelper;
import com.equalize.xpi.tester.util.ResultHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class XML2ExcelTest {
	// XLSX output are different each time generated, so can't be compared
/*	@Test
	public void testScenario1() throws IOException, MessagingException {
		String inputFile = "TestData/Module/ExcelTransformBean/XML2Excel_Scenario1.txt";
		String paramFile = "TestData/Module/ExcelTransformBean/XML2Excel_Scenario1_param.txt";
		String resultFile = "TestData/Module/ExcelTransformBean/XML2Excel_Scenario1_output.xlsx";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance(inputFile, contextData);

		assertEquals("Error", results, mt.mainTest());
	}*/
	@Test
	public void testScenario2() throws ModuleException {
		String inputFile = "TestData/Module/ExcelTransformBean/XML2Excel_Scenario2.txt";
		String paramFile = "TestData/Module/ExcelTransformBean/XML2Excel_Scenario2_param.txt";
		String resultFile = "TestData/Module/ExcelTransformBean/XML2Excel_Scenario2_output.xls";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
/*	@Test
	public void testScenario3() throws IOException, MessagingException {
		String inputFile = "TestData/Module/ExcelTransformBean/XML2Excel_Scenario3.txt";
		String paramFile = "TestData/Module/ExcelTransformBean/XML2Excel_Scenario3_param.txt";
		String resultFile = "TestData/Module/ExcelTransformBean/XML2Excel_Scenario3_output.xlsx";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance(inputFile, contextData);

		assertEquals("Error", results, mt.mainTest());
	}*/
}
