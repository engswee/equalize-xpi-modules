Equalize PI Custom Adapter Modules
==================
This repository contains the source code for PI custom EJB adapter modules.
All modules are written for EJB 3.0

Main package - com.equalize.xpi.af.modules
----------------------------------------------------
(1) DynamicAttributeChangeBean.java

http://scn.sap.com/docs/DOC-61728 - DynamicAttributeChangeBean - The no-mapping solution to change Dynamic Configuration ... dynamically!

(2) FormatConversionBean.java

http://scn.sap.com/docs/DOC-59021 - ExcelTransformBean Part 1: Convert various Excel formats to simple XML easily

http://scn.sap.com/docs/DOC-59281 - ExcelTransformBean Part 2: Convert simple XML to various Excel formats easily

http://scn.sap.com/docs/DOC-62488 - DeepFCCBean - The better FCC at meeting your deep (structure) needs! (Part 1 - Deep XML to Flat File)

http://scn.sap.com/docs/DOC-62559 - DeepFCCBean - The better FCC at meeting your deep (structure) needs! (Part 2 - Flat File to Deep XML)

http://scn.sap.com/docs/DOC-62782 - JSONTransformBean Part 1: Converting JSON content to XML

http://scn.sap.com/docs/DOC-62744 - JSONTransformBean Part 2: Converting XML to JSON content

Sub packages
----------------------------------------------------
(1) com.equalize.xpi.af.modules.excel - sub package for classes related to ExcelTransformBean

(2) com.equalize.xpi.af.modules.util - common helper classes for adapter module

(3) com.equalize.xpi.af.modules.testing - sub package for classes related to standalone module testing in NWDS based on following SCN article

http://scn.sap.com/community/pi-and-soa-middleware/blog/2014/10/01/standalone-testing-of-adapter-module-in-nwds

(4) com.equalize.xpi.af.modules.deepfcc - sub package for classes related to DeepFCCBean

(5) com.equalize.xpi.af.modules.json - sub package for classes related to JSONTransformBean

Dependencies
----------------------------------------------------
(1) com.equalize.xpi.util.converter - Library for format conversion from following repository
https://github.com/engswee/com.equalize.xpi.util
