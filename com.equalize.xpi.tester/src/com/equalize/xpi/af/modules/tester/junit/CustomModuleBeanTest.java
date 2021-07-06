package com.equalize.xpi.af.modules.tester.junit;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Test;

import com.equalize.xpi.af.modules.tester.ModuleTester;
import com.equalize.xpi.tester.util.ParameterHelper;
import com.equalize.xpi.tester.util.ResultHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;


public class CustomModuleBeanTest {
	@Test
	public void testScenario1() throws ModuleException {
		String inputFile = "TestData/Module/input.txt";
		String paramFile = "TestData/Module/parameter.txt";
		String resultFile = "TestData/Module/output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.CustomModuleBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
}
