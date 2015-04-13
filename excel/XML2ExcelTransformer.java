package com.equalize.xpi.af.modules.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.equalize.xpi.af.modules.util.AbstractModuleConverter;
import com.equalize.xpi.af.modules.util.AuditLogHelper;
import com.equalize.xpi.af.modules.util.DynamicConfigurationHelper;
import com.equalize.xpi.af.modules.util.ParameterHelper;
import com.sap.aii.af.lib.mp.module.ModuleException;
import com.sap.engine.interfaces.messaging.api.Message;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class XML2ExcelTransformer extends AbstractModuleConverter {

	// Module parameters
	private String sheetName;
	private String excelFormat;
	private String addHeaderLine;
	private String fieldNames;

	private ArrayList<ArrayList<String>> contents;
	private boolean addHeaderFromXML;
	private String[] columnNames;

	public XML2ExcelTransformer(Message msg, ParameterHelper param, AuditLogHelper audit, DynamicConfigurationHelper dyncfg, Boolean debug) {
		super(msg, param, audit, dyncfg, debug);
	}

	@Override
	public void retrieveModuleParameters() throws ModuleException {
		// Output format
		this.excelFormat = this.param.getParameter("excelFormat", "xlsx", true);
		this.param.checkParamValidValues("excelFormat", "xlsx,xls");

		// Sheet name
		this.sheetName = this.param.getParameter("sheetName", "Sheet1", true);
		// Header line
		this.addHeaderLine = this.param.getParameter("addHeaderLine", "none", false);
		this.param.checkParamValidValues("addHeaderLine", "none,fromXML,fromConfiguration");
		if(this.addHeaderLine.equalsIgnoreCase("none")) {
			this.addHeaderFromXML = false;
		} else if(this.addHeaderLine.equalsIgnoreCase("fromXML")) {
			this.addHeaderFromXML = true;			
		} else if(this.addHeaderLine.equalsIgnoreCase("fromConfiguration")) {
			this.addHeaderFromXML = false;
			this.fieldNames = this.param.getParameter("fieldNames");
			if(this.fieldNames == null || this.fieldNames.replaceAll("\\s+", "").isEmpty()) {
				throw new ModuleException("Parameter 'fieldNames' required when 'addHeaderLine' = fromConfiguration");
			} else {
				this.columnNames = this.fieldNames.split(",");
			}
		}
	}

	@Override
	public void parseInput() throws ModuleException {
		try {
			// Parse input XML with SAX custom handler
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			this.contents = new ArrayList<ArrayList<String>>();
			SAXSimpleParser handler = new SAXSimpleParser(this.contents, this.addHeaderFromXML);
			this.audit.addLog(AuditLogStatus.SUCCESS, "Parsing input XML");
			parser.parse(this.payload.getInputStream(), handler);		
		} catch (Exception e) {
			throw new ModuleException(e.getMessage(), e);
		}
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
	public byte[] generateOutput() throws ModuleException {
		// Create workbook
		Workbook wb = null;
		if(this.excelFormat.equalsIgnoreCase("xls")) {
			wb = new HSSFWorkbook();
		} else if(this.excelFormat.equalsIgnoreCase("xlsx")) {
			wb = new XSSFWorkbook();
		}

		this.audit.addLog(AuditLogStatus.SUCCESS, "Constructing output Excel");
		this.audit.addLog(AuditLogStatus.SUCCESS, "Creating sheet " + this.sheetName);
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
		try {
			wb.write(baos);
			baos.close();
		} catch (IOException e) {
			throw new ModuleException(e.getMessage(), e);
		}

		this.audit.addLog(AuditLogStatus.SUCCESS, "Conversion complete");
		return baos.toByteArray();
	}
}
