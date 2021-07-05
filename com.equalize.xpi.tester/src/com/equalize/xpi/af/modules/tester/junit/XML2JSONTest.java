package com.equalize.xpi.af.modules.tester.junit;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Test;

import com.equalize.xpi.af.modules.tester.ModuleTester;
import com.equalize.xpi.tester.util.ParameterHelper;
import com.equalize.xpi.tester.util.ResultHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class XML2JSONTest {

	@Test
	public void testScenario1() throws ModuleException {
		String inputFile = "TestData/Module/JSONTransformBean/XML2JSON_Scenario1.txt";
		String paramFile = "TestData/Module/JSONTransformBean/XML2JSON_Scenario1_param.txt";
		String resultFile = "TestData/Module/JSONTransformBean/XML2JSON_Scenario1_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	
	@Test
	public void testScenario3() throws ModuleException {
		String inputFile = "TestData/Module/JSONTransformBean/XML2JSON_Scenario3.txt";
		String paramFile = "TestData/Module/JSONTransformBean/XML2JSON_Scenario3_param.txt";
		String resultFile = "TestData/Module/JSONTransformBean/XML2JSON_Scenario3_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
}
