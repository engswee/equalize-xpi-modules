package com.equalize.xpi.af.modules.tester.junit;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Test;

import com.equalize.xpi.af.modules.tester.ModuleTester;
import com.equalize.xpi.tester.util.ParameterHelper;
import com.equalize.xpi.tester.util.ResultHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;

public class DeepPlain2XMLTest {

	@Test
	public void testScenario1() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario1.txt";
		String paramFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario1_param.txt";
		String resultFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario1_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario2() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario2.txt";
		String paramFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario2_param.txt";
		String resultFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario2_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario3() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario3.txt";
		String paramFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario3_param.txt";
		String resultFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario3_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario4() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario4.txt";
		String paramFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario4_param.txt";
		String resultFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario4_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario5a() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario5a.txt";
		String paramFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario5a_param.txt";
		String resultFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario5a_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario5b() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario5b.txt";
		String paramFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario5b_param.txt";
		String resultFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario5b_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario6() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario6.txt";
		String paramFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario6_param.txt";
		String resultFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario6_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test
	public void testScenario7() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario7.txt";
		String paramFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario7_param.txt";
		String resultFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario7_output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
	@Test (expected = ModuleException.class)
	public void testScenario7a() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario7.txt";
		String paramFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario7a_param.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);
		mt.execute();
	}
	@Test (expected = ModuleException.class)
	public void testScenario7b() throws ModuleException {
		String inputFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario7.txt";
		String paramFile = "TestData/Module/DeepFCCBean/DeepPlain2XML_Scenario7b_param.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.FormatConversionBean", inputFile, contextData);
		mt.execute();
	}
}
