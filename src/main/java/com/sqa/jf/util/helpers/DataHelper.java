package com.sqa.jf.util.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sqa.jf.util.helpers.data.DataType;
import com.sqa.jf.util.helpers.data.TextFormat;
import com.sqa.jf.util.helpers.exceptions.BooleanFormatException;
import com.sqa.jf.util.helpers.exceptions.CharacterCountFormatException;
import com.sqa.jf.util.helpers.exceptions.DataTypesCountException;
import com.sqa.jf.util.helpers.exceptions.DataTypesMismatchException;
import com.sqa.jf.util.helpers.exceptions.DataTypesTypeException;
import com.sqa.jf.util.helpers.exceptions.InvalidExcelExtensionException;

public class DataHelper {
	public static Object[][] evalDatabaseTable(String driverClassString, String databaseStringUrl, String username,
			String password, String tableName) throws ClassNotFoundException, SQLException, DataTypesMismatchException {
		return evalDatabaseTable(driverClassString, databaseStringUrl, username, password, tableName, 0, 0, null);
	}

	public static Object[][] evalDatabaseTable(String driverClassString, String databaseStringUrl, String username,
			String password, String tableName, int rowOffset, int colOffset, DataType[] dataTypes)
			throws DataTypesMismatchException, ClassNotFoundException, SQLException {
		Object[][] myData;
		ArrayList<Object> myArrayData = new ArrayList<Object>();
		Class.forName(driverClassString);
		Connection dbconn = DriverManager.getConnection(databaseStringUrl, username, password);
		Statement stmt = dbconn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from " + tableName);
		int numOfColumns = rs.getMetaData().getColumnCount();
		if (dataTypes != null) {
			if (dataTypes.length != numOfColumns) {
				throw new DataTypesCountException();
			}
		}
		int curRow = 1;
		while (rs.next()) {
			if (curRow > rowOffset) {
				Object[] rowData = new Object[numOfColumns - colOffset];
				for (int i = 0, j = colOffset; i < rowData.length; i++) {
					try {
						switch (dataTypes[i]) {
						case STRING:
							rowData[i] = rs.getString(i + colOffset + 1);
							break;
						case INT:
							rowData[i] = rs.getInt(i + colOffset + 1);
							break;
						default:
							break;
						}
					} catch (Exception e) {
						System.out.println("Error in conversion...");
						e.printStackTrace();
						throw new DataTypesTypeException();
					}
				}
				myArrayData.add(rowData);
			}
			curRow++;
		}
		myData = new Object[myArrayData.size()][];
		for (int i = 0; i < myData.length; i++) {
			myData[i] = (Object[]) myArrayData.get(i);
		}
		// Step 5
		rs.close();
		stmt.close();
		dbconn.close();
		return myData;
	}

	// public static Object[][] getDatabaseData(String driverClass, String
	// databaseString, String username,
	// String password, String sqlQuery) {
	// Object[][] data = null;
	// ArrayList<Object[]> dataList = new ArrayList<Object[]>();
	// // This line calls the Driver.. Is driver available in pom.xml
	// // Class not found exception
	// try {
	// Class.forName(driverClass);
	// // Does that String specify your database, port number and all,
	// // confirm
	// // username, password
	// Connection dbconn = DriverManager.getConnection(databaseString, username,
	// password);
	// Statement stmt = dbconn.createStatement();
	// // SQL correct? Do they refer to actual columns with the specified
	// // table
	// // SQL: select name, age from person
	// ResultSet rs = stmt.executeQuery(sqlQuery);
	// while (rs.next()) {
	// int colCount = rs.getMetaData().getColumnCount();
	// Object[] rowData = new Object[colCount];
	// for (int i = 0; i < colCount; i++) {
	// rowData[i] = rs.getString(i + 1);
	// }
	// // These have to belong to fields and the getString needs to be
	// // exchanged with proper datatype
	// // String id = rs.getString("id");
	// // String name = rs.getString("name");
	// // String age = rs.getString("age");
	// // String address = rs.getString("address");
	// // String job = rs.getString("job");
	// // System out refer to actual fields or variables
	// // System.out.format("#" + "%2d" + " - " + name.toUpperCase() +
	// // "(" + age + ")\n", Integer.parseInt(id));
	// // System.out.println(" " + job.toUpperCase());
	// // System.out.println(" " + address.toUpperCase() + "\n");
	// dataList.add(rowData);
	// }
	// data = new Object[dataList.size()][];
	// for (int i = 0; i < data.length; i++) {
	// data[i] = dataList.get(i);
	// }
	// rs.close();
	// stmt.close();
	// dbconn.close();
	// } catch (ClassNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return data;
	// }
	public static Object[][] getExcelFileData(String fileLocation, String fileName, Boolean hasLabels)
			throws InvalidExcelExtensionException {
		Object[][] resultsObject;
		String[] fileNameParts = fileName.split("[.]");
		String extension = fileNameParts[fileNameParts.length - 1];
		ArrayList<Object> results = null;
		if (extension.equalsIgnoreCase("xlsx")) {
			results = getNewExcelFileResults(fileLocation, fileName, hasLabels);
		} else if (extension.equalsIgnoreCase("xls")) {
			results = getOldExcelFileResults(fileLocation, fileName, hasLabels);
		} else {
			throw new InvalidExcelExtensionException();
		}
		resultsObject = new Object[results.size()][];
		results.toArray(resultsObject);
		return resultsObject;
	}

	public static Object[][] getTextFileData(String fileName) {
		return getTextFileData("", fileName, TextFormat.CSV, false, null);
	}

	public static Object[][] getTextFileData(String fileLocation, String fileName, TextFormat textFormat) {
		return getTextFileData(fileLocation, fileName, textFormat, false, null);
	}

	public static Object[][] getTextFileData(String fileLocation, String fileName, TextFormat textFormat,
			Boolean hasLabels, DataType[] dataTypes) {
		Object[][] data;
		ArrayList<String> lines = openFileAndCollectData(fileLocation, fileName);
		switch (textFormat) {
		case CSV:
			data = parseCSVData(lines, hasLabels, dataTypes);
			break;
		case XML:
			data = parseXMLData(lines, hasLabels);
			break;
		case TAB:
			data = parseTabData(lines, hasLabels);
			break;
		case JSON:
			data = parseJSONData(lines, hasLabels);
			break;
		default:
			data = null;
			break;
		}
		return data;
	}

	public static Object[][] getTextFileData(String fileLocation, String fileName, TextFormat textFormat,
			DataType[] dataTypes) {
		return getTextFileData(fileLocation, fileName, textFormat, false, dataTypes);
	}

	private static Object convertDataType(String parameter, DataType dataType)
			throws BooleanFormatException, CharacterCountFormatException {
		Object data = null;
		try {
			switch (dataType) {
			case STRING:
				data = parameter;
				break;
			case CHAR:
				if (parameter.length() > 1) {
					throw new CharacterCountFormatException();
				}
				data = parameter.charAt(0);
				break;
			case DOUBLE:
				data = Double.parseDouble(parameter);
			case FLOAT:
				data = Float.parseFloat(parameter);
			case INT:
				data = Integer.parseInt(parameter);
			case BOOLEAN:
				if (parameter.equalsIgnoreCase("true") | parameter.equalsIgnoreCase("false")) {
					data = Boolean.parseBoolean(parameter);
				} else {
					throw new BooleanFormatException();
				}
			default:
				break;
			}
		} catch (NumberFormatException | BooleanFormatException | CharacterCountFormatException e) {
			System.out.println("Converstion issue when converting String to " + dataType + "(" + parameter
					+ ")convertDataType: DataHelper.class");
			System.out.println(e.getMessage());
		}
		return data;
	}

	private static ArrayList<Object> getNewExcelFileResults(String fileLocation, String fileName, Boolean hasLabels) {
		ArrayList<Object> results = new ArrayList<Object>();
		try {
			String fullFilePath = fileLocation + fileName;
			InputStream newExcelFormatFile = new FileInputStream(new File(fullFilePath));
			XSSFWorkbook workbook = new XSSFWorkbook(newExcelFormatFile);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			if (hasLabels) {
				rowIterator.next();
			}
			while (rowIterator.hasNext()) {
				ArrayList<Object> rowData = new ArrayList<Object>();
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						System.out.print(cell.getBooleanCellValue() + "\t\t\t");
						rowData.add(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						System.out.print(cell.getNumericCellValue() + "\t\t\t");
						rowData.add(cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						System.out.print(cell.getStringCellValue() + "\t\t\t");
						rowData.add(cell.getStringCellValue());
						break;
					}
				}
				Object[] rowDataObject = new Object[rowData.size()];
				rowData.toArray(rowDataObject);
				results.add(rowDataObject);
				System.out.println("");
			}
			newExcelFormatFile.close();
			FileOutputStream out = new FileOutputStream(new File("src/main/resources/excel-output.xlsx"));
			workbook.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * @param fileLocation
	 * @param fileName
	 * @param hasLabels
	 * @return
	 */
	private static ArrayList<Object> getOldExcelFileResults(String fileLocation, String fileName, Boolean hasLabels) {
		ArrayList<Object> results = new ArrayList<Object>();
		try {
			String fullFilePath = fileLocation + fileName;
			InputStream newExcelFormatFile = new FileInputStream(new File(fullFilePath));
			HSSFWorkbook workbook = new HSSFWorkbook(newExcelFormatFile);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			if (hasLabels) {
				rowIterator.next();
			}
			while (rowIterator.hasNext()) {
				ArrayList<Object> rowData = new ArrayList<Object>();
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						System.out.print(cell.getBooleanCellValue() + "\t\t\t");
						rowData.add(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						System.out.print((int) cell.getNumericCellValue() + "\t\t\t");
						rowData.add((int) cell.getNumericCellValue());
						break;
					case Cell.CELL_TYPE_STRING:
						System.out.print(cell.getStringCellValue() + "\t\t\t");
						rowData.add(cell.getStringCellValue());
						break;
					}
				}
				Object[] rowDataObject = new Object[rowData.size()];
				rowData.toArray(rowDataObject);
				results.add(rowDataObject);
				System.out.println("");
			}
			newExcelFormatFile.close();
			FileOutputStream out = new FileOutputStream(new File("src/main/resources/excel-output.xlsx"));
			workbook.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}

	private static ArrayList<String> openFileAndCollectData(String fileLocation, String fileName) {
		String fullFilePath = fileLocation + fileName;
		ArrayList<String> dataLines = new ArrayList<String>();
		try {
			FileReader fileReader = new FileReader(fullFilePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();
			while (line != null) {
				dataLines.add(line);
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fullFilePath + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fullFilePath + "'");
		}
		return dataLines;
	}

	private static Object[][] parseCSVData(ArrayList<String> lines, boolean hasLabels, DataType[] dataTypes) {
		ArrayList<Object> results = new ArrayList<Object>();
		if (hasLabels) {
			lines.remove(0);
		}
		String pattern = "(,*)([a-zA-Z0-9\\s-]+)(,*)";
		Pattern r = Pattern.compile(pattern);
		for (int i = 0; i < lines.size(); i++) {
			int curDataType = 0;
			ArrayList<Object> curMatches = new ArrayList<Object>();
			Matcher m = r.matcher(lines.get(i));
			while (m.find()) {
				if (dataTypes.length > 0) {
					try {
						curMatches.add(convertDataType(m.group(2).trim(), dataTypes[curDataType]));
					} catch (Exception e) {
						System.out.println("DataTypes provided do not match parsed data results.");
					}
				} else {
					curMatches.add(m.group(2).trim());
				}
				curDataType++;
			}
			Object[] resultsObj = new Object[curMatches.size()];
			curMatches.toArray(resultsObj);
			results.add(resultsObj);
		}
		System.out.println("Results:" + results);
		Object[][] resultsObj = new Object[results.size()][];
		results.toArray(resultsObj);
		return resultsObj;
	}

	private static Object[][] parseJSONData(ArrayList<String> lines, Boolean hasLabels) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Object[][] parseTabData(ArrayList<String> lines, Boolean hasLabels) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Object[][] parseXMLData(ArrayList<String> lines, Boolean hasLabels) {
		// TODO Auto-generated method stub
		return null;
	}
}
