Equalize PI Custom Adapter Modules
==================
This repository contains the source code for PI custom EJB adapter modules.
All modules are written for EJB 3.0

Main project - com.equalize.xpi.af.modules.app (EAR) & com.equalize.xpi.af.modules.ejb (EJB)
----------------------------------------------------
(1) AttachmentSplitterBean.java

http://scn.sap.com/docs/DOC-63349 - AttachmentSplitterBean - Split attachments into multiple child messages

(2) DynamicAttributeChangeBean.java

http://scn.sap.com/docs/DOC-61728 - DynamicAttributeChangeBean - The no-mapping solution to change Dynamic Configuration ... dynamically!

(3) FormatConversionBean.java

http://scn.sap.com/docs/DOC-59021 - ExcelTransformBean Part 1: Convert various Excel formats to simple XML easily

http://scn.sap.com/docs/DOC-59281 - ExcelTransformBean Part 2: Convert simple XML to various Excel formats easily

http://scn.sap.com/docs/DOC-62488 - DeepFCCBean - The better FCC at meeting your deep (structure) needs! (Part 1 - Deep XML to Flat File)

http://scn.sap.com/docs/DOC-62559 - DeepFCCBean - The better FCC at meeting your deep (structure) needs! (Part 2 - Flat File to Deep XML)

http://scn.sap.com/docs/DOC-62782 - JSONTransformBean Part 1: Converting JSON content to XML

http://scn.sap.com/docs/DOC-62744 - JSONTransformBean Part 2: Converting XML to JSON content

http://scn.sap.com/docs/DOC-63760 - Base64DecodeConverter - Base64 decoding made easy!

http://scn.sap.com/docs/DOC-64087 - Base64EncodeConverter - Base64 encoding made easy!

(4) SetCorrelationBean.java

http://scn.sap.com/docs/DOC-63668 - Synchronous retrieval of dynamically specified file from file system/server

(5) SetMailAttachmentNameBean.java

https://scn.sap.com/docs/DOC-71368 - SetMailAttachmentNameBean - Setting dynamic attachment name for main payload

(6) SetMailAttachmentNameBean.java

https://scn.sap.com/docs/DOC-71584 - UnzipSplitterBean - Split zip entries into child messages

Other projects
----------------------------------------------------
(1) com.equalize.xpi.af.modules.testing - Java project for standalone module testing in NWDS based on following SCN article

http://scn.sap.com/community/pi-and-soa-middleware/blog/2014/10/01/standalone-testing-of-adapter-module-in-nwds

(2) com.equalize.xpi.util - Java project for common utilities used for format conversion

