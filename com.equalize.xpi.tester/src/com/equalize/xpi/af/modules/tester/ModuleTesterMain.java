package com.equalize.xpi.af.modules.tester;

import java.util.Hashtable;

import com.equalize.xpi.tester.util.ParameterHelper;
import com.equalize.xpi.af.modules.*;
public class ModuleTesterMain {

	public static void main(String[] args) {
		try {
			// Sample arguments:-
			// arg0 - com.equalize.xpi.af.modules.FormatConversionBean
			// arg1 - C:\Users\ksap086\Desktop\input.txt
			// arg2 - C:\Users\ksap086\Desktop\param.txt
			// arg3 - C:\Users\ksap086\Desktop\output.txt
			if(args.length < 4)
				throw new RuntimeException("Please enter arguments in Run Configuration");

			// Module to be tested
			String module = args[0] ;
			// Files
			String inputFile = args[1];
			String paramFile = args[2];
			String outFile = args[3];
			
			// Get module parameters and initialize tester
			Hashtable<String, String> contextData = ParameterHelper.newInstance(paramFile).getParams();
			ModuleTester tester = ModuleTester.newInstance(module, inputFile, contextData);

			// Add dynamic configuration
			//tester.addDynCfg("http://sap.com/xi/XI/System/File", "FileName", "FileA.txt");	

			// Execute processing
			tester.getDynCfg("before");
			tester.execute(outFile);
			tester.getDynCfg("after");
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
