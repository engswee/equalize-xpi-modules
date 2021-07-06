package com.equalize.xpi.af.modules.tester.junit;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Test;

import com.equalize.xpi.af.modules.tester.ModuleTester;
import com.equalize.xpi.tester.util.ParameterHelper;
import com.equalize.xpi.tester.util.ResultHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class XML2DeepPlainTest {

	@Test
	public void testScenario1a() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/XML2DeepPlain_Scenario1.xml";
		String paramFile = "TestData/Module/DeepFCCBean/XML2DeepPlain_Scenario1a_param.txt";
		String resultFile = "TestData/Module/DeepFCCBean/XML2DeepPlain_Scenario1a_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario1b() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/XML2DeepPlain_Scenario1.xml";
		String paramFile = "TestData/Module/DeepFCCBean/XML2DeepPlain_Scenario1b_param.txt";
		String resultFile = "TestData/Module/DeepFCCBean/XML2DeepPlain_Scenario1b_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario3() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/XML2DeepPlain_Scenario3.xml";
		String paramFile = "TestData/Module/DeepFCCBean/XML2DeepPlain_Scenario3_param.txt";
		String resultFile = "TestData/Module/DeepFCCBean/XML2DeepPlain_Scenario3_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
}
