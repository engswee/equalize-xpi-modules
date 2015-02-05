package com.equalize.xpi.af.modules.excel;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class XML2ExcelTransformer extends ExcelTransformer {

	// Module parameters
	private String sheetName;
	private String excelFormat;
	private String addHeaderLine;
	private String fieldNames;

	private ArrayList<ArrayList<String>> contents;
	private boolean addHeaderFromXML;
	private String[] columnNames;

	public XML2ExcelTransformer(ModuleContext mc, MessageKey key, AuditAccess audit) {
		super(mc, key, audit);
	}

	@Override
	public void retrieveModuleParameters() throws Exception {
		// Output format
		this.excelFormat = getParaWithDefault("excelFormat", "xlsx");
		if(!this.excelFormat.equalsIgnoreCase("xls") && !this.excelFormat.equalsIgnoreCase("xlsx")) {
			throw new Exception("Value " + this.excelFormat + " not valid for parameter excelFormat");
		}
		// Sheet name
		this.sheetName = getParaWithDefault("sheetName", "Sheet1");
		// Header line
		this.addHeaderLine = getParaWithDefault("addHeaderLine", "none");
		if(this.addHeaderLine.equalsIgnoreCase("none")) {
			this.addHeaderFromXML = false;
		} else if(this.addHeaderLine.equalsIgnoreCase("fromXML")) {
			this.addHeaderFromXML = true;			
		} else if(this.addHeaderLine.equalsIgnoreCase("fromConfiguration")) {
			this.addHeaderFromXML = false;
			this.fieldNames = this.moduleParam.getContextData("fieldNames");
			if(this.fieldNames == null || this.fieldNames.replaceAll("\\s+", "").equals("")) {
				throw new Exception("Parameter fieldNames is required when addHeaderLine = fromConfiguration");
			} else {
				this.columnNames = this.fieldNames.split(",");
			}
		} else {
			throw new Exception("Value " + this.addHeaderLine + " not valid for parameter addHeaderLine");
		}
	}

	@Override
	public void parseInput(InputStream inStream) throws Exception {
		// Parse input XML with SAX custom handler
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		this.contents = new ArrayList<ArrayList<String>>();
		SAXSimpleParser handler = new SAXSimpleParser(this.contents, this.addHeaderFromXML);
		addLog(AuditLogStatus.SUCCESS, "Parsing XML contents");
		parser.parse(inStream, handler);		

		// Add header line from configuration
		if(this.addHeaderLine.equalsIgnoreCase("fromConfiguration")) {
			ArrayList<String> header = new ArrayList<String>();
			for(int i = 0; i < this.columnNames.length; i++) {
				header.add(this.columnNames[i]);
			}
			this.contents.add(0, header);
		}		
	}

	@Override
	public ByteArrayOutputStream generateOutput() throws Exception {
		// Create workbook
		Workbook wb = null;
		if(this.excelFormat.equalsIgnoreCase("xls")) {
			wb = new HSSFWorkbook();
		} else if(this.excelFormat.equalsIgnoreCase("xlsx")) {
			wb = new XSSFWorkbook();
		}

		addLog(AuditLogStatus.SUCCESS, "Constructing Excel output");
		addLog(AuditLogStatus.SUCCESS, "Creating sheet " + this.sheetName);
		// Create sheet
		Sheet sheet = wb.createSheet(this.sheetName);
		
		// Loop through the 2D array of saved contents 
		for(int i = 0; i < this.contents.size(); i++) {
			Row row = sheet.createRow(i);
			ArrayList<String> contentRow = this.contents.get(i);
			for(int j = 0; j < contentRow.size(); j++) {
				Cell cell = row.createCell(j);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(contentRow.get(j));
			}				
		}
		// Write output stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		wb.write(baos);
		baos.close();

		addLog(AuditLogStatus.SUCCESS, "Conversion complete");
		return baos;
	}
}
