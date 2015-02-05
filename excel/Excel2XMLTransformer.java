package com.equalize.xpi.af.modules.excel;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sap.aii.af.lib.mp.module.ModuleContext;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class Excel2XMLTransformer extends ExcelTransformer {

	// Module parameters
	private String sheetName;
	private String sheetIndex;
	private String processFieldNames;
	private String fieldNames;
	private String columnCount;
	private String recordName;
	private String documentName;
	private String documentNamespace;
	private String formatting;
	private String evaluateFormulas;
	private String emptyCellOutput;
	private String emptyCellDefaultValue;
	private String rowOffset;
	private String skipEmptyRows;
	private String indentXML;
	private String debug;

	private String[] columnNames;
	private int noOfRows = 0;
	private int noOfColumns = 0;
	private int startingRow = 0;
	private ArrayList<String[]> sheetContents;

	// Constructor
	public Excel2XMLTransformer(ModuleContext mc, MessageKey key, AuditAccess audit) {
		super(mc, key, audit);
	}

	@Override
	public void retrieveModuleParameters() throws Exception {
		// Debug
		this.debug = getParaWithDefault("debug","NO");
		if(this.debug.equalsIgnoreCase("YES")) {
			addLog(AuditLogStatus.WARNING, "WARNING: Debug activated! Use only in non-productive systems!");
		}	
		// Active sheet
		this.sheetName = this.moduleParam.getContextData("sheetName");
		this.sheetIndex = this.moduleParam.getContextData("sheetIndex");
		if (this.sheetName == null && this.sheetIndex == null) {
			throw new Exception("Parameter sheetName or sheetIndex is missing");
		} else if (this.sheetName != null && this.sheetIndex != null) {
			throw new Exception("Use only parameter sheetName or sheetIndex, not both");
		} else if (this.sheetIndex != null) {
			checkIntegerInput(this.sheetIndex, "sheetIndex");
		}

		// Row processing options
		this.skipEmptyRows = getParaWithDefault("skipEmptyRows","YES");
		if(this.skipEmptyRows.equalsIgnoreCase("NO")) {
			addLog(AuditLogStatus.SUCCESS, "Empty rows will be included");
		}
		this.rowOffset = getParaWithDefault("rowOffset", "0");
		this.startingRow = checkIntegerInput(this.rowOffset, "rowOffset");

		// Determine number of columns and field names if any
		this.processFieldNames = getParaWithErrorDescription("processFieldNames"); 
		if (this.processFieldNames.equalsIgnoreCase("fromFile")) {
			// this.noOfColumns remains null
			if (this.startingRow == 0) {
				this.startingRow++;
				addLog(AuditLogStatus.SUCCESS, "Header row will be automatically skipped");
			}
		} else if (this.processFieldNames.equalsIgnoreCase("fromConfiguration")) {
			this.fieldNames = this.moduleParam.getContextData("fieldNames");
			if(this.fieldNames == null || this.fieldNames.replaceAll("\\s+", "").equals("")) {
				throw new Exception("Parameter fieldNames is required when processFieldNames = fromConfiguration");
			} else {
				this.columnNames = this.fieldNames.split(",");
				this.noOfColumns = this.columnNames.length;
			}
		} else if (this.processFieldNames.equalsIgnoreCase("notAvailable")) {
			this.columnCount = this.moduleParam.getContextData("columnCount");
			if(this.columnCount == null) {
				throw new Exception("Parameter columnCount is required when processFieldNames = notAvailable");
			} else {
				this.noOfColumns = checkIntegerInput(this.columnCount, "columnCount");
				if (this.noOfColumns <= 0 ) {
					throw new Exception("Only positive integers allowed for columnCount");
				}
			}
		} else {
			throw new Exception("Value " + this.processFieldNames + " not valid for parameter processFieldNames");
		}

		// Output XML document properties
		this.recordName = getParaWithDefault("recordName","Record");
		this.documentName = getParaWithErrorDescription("documentName");
		this.documentNamespace = getParaWithErrorDescription("documentNamespace");

		// Output options
		this.formatting = getParaWithDefault("formatting","excel");
		if(this.formatting.equalsIgnoreCase("raw")) {
			addLog(AuditLogStatus.SUCCESS, "Cell contents will not be formatted, raw values displayed instead");
		}
		this.evaluateFormulas = getParaWithDefault("evaluateFormulas","YES");
		if(this.evaluateFormulas.equalsIgnoreCase("NO")) {
			addLog(AuditLogStatus.SUCCESS, "Formulas will not be evaluated, formula logic displayed instead");
		}
		this.emptyCellOutput = getParaWithDefault("emptyCellOutput","suppress");
		if (this.emptyCellOutput.equalsIgnoreCase("defaultValue")) {
			this.emptyCellDefaultValue = getParaWithDefault("emptyCellDefaultValue",""); 
			addLog(AuditLogStatus.SUCCESS, "Empty cells will be filled with default value: '" + this.emptyCellDefaultValue + "'");
		}
		this.indentXML = getParaWithDefault("indentXML","NO");
		if(this.indentXML.equalsIgnoreCase("YES")) {
			addLog(AuditLogStatus.SUCCESS, "XML output will be indented");
		}		
	}

	@Override
	public void parseInput(InputStream inStream) throws Exception {
		// Get workbook 
		Workbook wb = WorkbookFactory.create(inStream);
		// Get the sheet
		Sheet sheet = retrieveSheet(wb, this.sheetName, this.sheetIndex);	
		// Get the number of rows and columns
		if (this.noOfColumns == 0) {
			this.noOfColumns = retrieveHeaderColumnCount(sheet);
		}
		this.noOfRows = sheet.getLastRowNum() + 1;

		// Get the column names from header
		if (this.processFieldNames.equalsIgnoreCase("fromFile")) {
			this.columnNames = retrieveColumnNamesFromFileHeader(sheet, this.noOfColumns);
		}

		// Get the cell contents of the sheet
		this.sheetContents = extractSheetContents(sheet, wb, 
				this.startingRow, this.noOfRows, this.noOfColumns, 
				this.skipEmptyRows, this.evaluateFormulas, this.formatting,
				this.debug);
	}

	@Override
	public ByteArrayOutputStream generateOutput() throws Exception {

		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document outDoc = docBuilder.newDocument();

		Node outRoot = outDoc.createElementNS(this.documentNamespace,"ns:"+ this.documentName);
		outDoc.appendChild(outRoot);

		addLog(AuditLogStatus.SUCCESS, "Constructing output XML");
		// Loop through the 2D array of saved contents
		for (int row = 0; row < this.sheetContents.size(); row++) {
			String[] rowContent = this.sheetContents.get(row);
			// Add new row
			Node outRecord = addElementToNode(outDoc, outRoot, this.recordName);
			for(int col = 0; col < rowContent.length; col++) {
				if (rowContent[col] == null && this.emptyCellDefaultValue != null) {
					rowContent[col] = this.emptyCellDefaultValue;
				}
				if (rowContent[col] != null) {
					String fieldName;
					if (this.columnNames != null) {
						fieldName = this.columnNames[col];
					} else {
						fieldName = "Column" + Integer.toString(col+1);
					}
					// Add fields of the row
					addElementToNode(outDoc, outRecord, fieldName, rowContent[col]);
				}
			}
		}
		// Transform the DOM to OutputStream
		javax.xml.transform.Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, this.indentXML); 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		transformer.transform(new DOMSource(outDoc), new StreamResult(baos));

		addLog(AuditLogStatus.SUCCESS, "Conversion complete");
		return baos;
	}

	private Sheet retrieveSheet(Workbook wb, String name, String index) throws Exception {
		Sheet sheet = null;
		if (name != null) {
			addLog(AuditLogStatus.SUCCESS, "Accessing sheet " + name);
			sheet = wb.getSheet(name);	
			if (sheet == null) {
				throw new Exception("Sheet " + name + " not found");
			}
		} else if (index != null) {
			addLog(AuditLogStatus.SUCCESS, "Accessing sheet at index " + index);
			sheet = wb.getSheetAt(Integer.parseInt(index));
		}
		return sheet;
	}

	private int retrieveHeaderColumnCount(Sheet sheet) throws Exception {
		Row header = sheet.getRow(0);
		int lastCellNum = 0;
		if (header != null) {
			lastCellNum = header.getLastCellNum();
		}
		if (lastCellNum != 0) {
			addLog(AuditLogStatus.SUCCESS, "No. of columns dynamically set to " + lastCellNum + " based on first row");
			return lastCellNum;
		} else {
			throw new Exception("No. of columns in first row is zero");
		}
	}

	private String[] retrieveColumnNamesFromFileHeader(Sheet sheet, int columnNo) throws Exception {
		Row row = sheet.getRow(0);
		addLog(AuditLogStatus.SUCCESS, "Retrieving column names from first row");
		String[] headerColumns = new String[columnNo];
		for (int col = 0; col < columnNo; col++) {
			Cell cell = row.getCell(col);			
			if(cell == null) {
				throw new Exception("Empty column name found");
			}
			headerColumns[col] = cell.getStringCellValue();
			String condensedName = headerColumns[col].replaceAll("\\s+", "");
			if(condensedName.equals("")) {
				throw new Exception("Empty column name found");
			}
			if(!condensedName.equals(headerColumns[col])) {
				addLog(AuditLogStatus.SUCCESS, "Renaming field '" + headerColumns[col] + "' to " + condensedName);
				headerColumns[col] = condensedName;
			}
		}
		return headerColumns;
	}

	private ArrayList<String[]> extractSheetContents(Sheet sheet, Workbook wb, int startRow, int noOfRows, int noOfColumns, String skipEmptyRows, String evaluateFormulas, String formatting, String debug) throws Exception {
		if(startRow >= noOfRows) {
			throw new Exception("Starting row is greater than last row of sheet");
		}
		addLog(AuditLogStatus.SUCCESS, "Extracting Excel sheet contents");
		addLog(AuditLogStatus.SUCCESS, "Start processing from row " + Integer.toString(startRow+1));
		ArrayList<String[]> contents = new ArrayList<String[]>();
		// Go through each row
		for (int rowNo = startRow; rowNo < noOfRows; rowNo++) {
			Row row = sheet.getRow(rowNo);
			boolean contentFound = false;
			if (row != null) {
				String[] rowContent = new String[noOfColumns];
				// Go through each column cell of the current row
				for (int colNo = 0; colNo < noOfColumns; colNo++) {
					Cell cell = row.getCell(colNo);
					if (cell != null) {
						rowContent[colNo] = retrieveCellContent(cell, wb, evaluateFormulas, formatting);
						if(rowContent[colNo] != null) {
							contentFound = true;
						}
					}
					if(debug.equalsIgnoreCase("YES")) {
						addLog(AuditLogStatus.SUCCESS, "DEBUG Cell " + Integer.toString(rowNo+1) + ":" + Integer.toString(colNo+1) + 
								" - " + rowContent[colNo]);
					}
				}
				if (contentFound) {
					contents.add(rowContent);
				}
			} else if(debug.equalsIgnoreCase("YES")) {
				addLog(AuditLogStatus.SUCCESS, "DEBUG Row " + Integer.toString(rowNo+1) + " empty");
			}
			// Add empty rows if skip parameter set to NO
			if (skipEmptyRows.equalsIgnoreCase("NO") && !contentFound) {
				contents.add(new String[noOfColumns]);
			}

		}
		if (contents.size()==0) {
			throw new Exception("No rows with valid contents found");
		} else {
			return contents;
		}
	}

	private String retrieveCellContent(Cell cell, Workbook wb, String evaluateFormulas, String formatting) {
		FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
		DataFormatter formatter = new DataFormatter(true);
		String cellContent = null;
		int cellType = cell.getCellType();
		switch(cellType) {
		case Cell.CELL_TYPE_BLANK:
			break;
		case Cell.CELL_TYPE_FORMULA:
			if (evaluateFormulas.equals("YES")) {
				cellContent = formatter.formatCellValue(cell, evaluator);
			} else {
				// Display the formula instead
				cellContent = cell.getCellFormula();
			}
			break;
		default:
			if(formatting.equalsIgnoreCase("excel")) {
				cellContent = formatter.formatCellValue(cell);
			} else if(formatting.equalsIgnoreCase("raw")) {
				// Display the raw cell contents
				switch (cellType) {
				case Cell.CELL_TYPE_NUMERIC:
					cellContent = Double.toString(cell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_STRING:
					cellContent = cell.getStringCellValue();
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					cellContent = Boolean.toString(cell.getBooleanCellValue());
					break;	
				}
			}
			break;
		}
		return cellContent;
	}

	private Node addElementToNode (Document doc, Node parentNode, String elementName) {
		Node element = doc.createElement(elementName);		
		parentNode.appendChild(element);
		return element;
	}

	private Node addElementToNode (Document doc, Node parentNode, String elementName, String elementTextValue) {
		Node element = addElementToNode(doc, parentNode, elementName);
		if (elementTextValue != null) {
			element.appendChild(doc.createTextNode(elementTextValue));
		}		
		return element;
	}

}
