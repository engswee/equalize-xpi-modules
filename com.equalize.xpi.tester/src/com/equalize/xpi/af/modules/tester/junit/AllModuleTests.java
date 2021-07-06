package com.equalize.xpi.af.modules.tester.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DeepPlain2XMLTest.class, XML2DeepPlainTest.class,
				Excel2XMLTest.class, XML2ExcelTest.class,
				JSON2XMLTest.class, XML2JSONTest.class,
				Base64DecodeTest.class, Base64EncodeTest.class,
				DACBTest.class })
public class AllModuleTests {

} 
