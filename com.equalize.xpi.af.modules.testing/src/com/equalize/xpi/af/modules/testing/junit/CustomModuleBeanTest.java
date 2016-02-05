package com.equalize.xpi.af.modules.testing.junit;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.Test;

import com.equalize.xpi.af.modules.testing.ModuleTester;
import com.equalize.xpi.af.modules.testing.ParameterHelper;
import com.equalize.xpi.af.modules.testing.ResultHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;


public class CustomModuleBeanTest {
	@Test
	public void testScenario1() throws ModuleException {
		String inputFile = "TestData/input.txt";
		String paramFile = "TestData/parameter.txt";
		String resultFile = "TestData/output.txt";
		Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
		String results = ResultHelper.newInstance(resultFile).getResultContent();
		ModuleTester mt = ModuleTester.newInstance("com.equalize.xpi.af.modules.testing.CustomModuleBean", inputFile, contextData);

		assertEquals("Error", results, mt.execute());
	}
}
