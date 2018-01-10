package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ProcessResults {
	public ProcessResults(){}
	public void retriveData(){
		@SuppressWarnings("resource")
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(String.valueOf(Config.len));
		int rowCount = 0;
		int colCount = 0;
		//Header
		Row row = sheet.createRow(rowCount++);
		Cell cell = row.createCell(colCount++);
		cell.setCellValue("Instance");
		for (int j = 1; j <= Config.NUM_RUN; ++j) {
			cell = row.createCell(colCount++);
			cell.setCellValue("run " + j);
		}
		cell = row.createCell(colCount++);
		cell.setCellValue("Best");
		
		cell = row.createCell(colCount++);
		cell.setCellValue("Average");
		
		cell = row.createCell(colCount++);
		cell.setCellValue("Standard");
		
		cell = row.createCell(colCount++);
		cell.setCellValue("Time(s)");
		
		cell = row.createCell(colCount++);
		cell.setCellValue("Dijkstra");
		
		cell = row.createCell(colCount++);
		cell.setCellValue("Time(s)");
		
		//each instance
		File folder = new File("result");
		FileReader fr = null;
		BufferedReader br = null;
		for (int i = 0; i < folder.listFiles().length; ++i){
			String file = "result/" + folder.listFiles()[i].getName();
			File f = new File(file);
			String idx = file.substring(file.lastIndexOf('_')+1).split("\\.")[0];
			String instance = folder.listFiles()[i].getName().substring(0, folder.listFiles()[i].getName().lastIndexOf('_'));
			boolean isExist = false;
			int ir;
			for (ir = 1; ir < sheet.getLastRowNum(); ++ir){
				if (sheet.getRow(ir).getCell(0).equals(instance)){
					isExist = true;
				}
			}
			if (isExist) {
				row = sheet.getRow(ir);
			} else {
				row = sheet.createRow(rowCount++);
				for (int j = 0; j < colCount; ++j) {
					cell = row.createCell(j);
				}
				row.getCell(0).setCellValue(instance);
			}
			
			if (idx.equals("summary")) {
				try {
					fr = new FileReader(f);
					br = new BufferedReader(fr);
					String str = br.readLine().trim();
					str = str.substring(str.lastIndexOf(':')+1).trim();
					row.getCell(colCount-3).setCellValue(Double.valueOf(str));
					
					str = br.readLine().trim();
					str = str.substring(str.lastIndexOf(':')+1).trim();
					row.getCell(colCount-5).setCellValue(Double.valueOf(str));
					
					str = br.readLine().trim();
					str = str.substring(str.lastIndexOf(':')+1).trim();
					row.getCell(colCount-6).setCellValue(Double.valueOf(str));
					
					str = br.readLine();
					str = br.readLine().trim();
					row.getCell(colCount-2).setCellValue(Double.valueOf(str));
				} catch (FileNotFoundException e) {
					System.out.println("Read file: File not found");
				} catch (IOException e) {
					System.out.println("Read file: Error in reading");
				} finally {
					if (null != fr){
						try {
							fr.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if (null != br) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				}
			} else {
				int nbRun = Integer.valueOf(idx) + 1;
				int nbPoint;
				try {
					fr = new FileReader(f);
					br = new BufferedReader(fr);
					String str = br.readLine().trim();
					while(!str.equals("NumberOfPoint")){
						str = br.readLine().trim();
					}
					str = br.readLine().trim();
					nbPoint = Integer.valueOf(str);
					
					while (!str.equals("Value")) {
						str = br.readLine();
					}
					
					str = br.readLine();
					row.getCell(nbRun).setCellValue(Double.valueOf(str));
				} catch (FileNotFoundException e) {
					System.out.println("Read file: File not found");
				} catch (IOException e) {
					System.out.println("Read file: Error in reading");
				} finally {
					if (null != fr){
						try {
							fr.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if (null != br) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				}
				
			}
		}
		
		try (
				FileOutputStream outputStream = new FileOutputStream("statistic.xls")) {
				workbook.write(outputStream);
        } catch (FileNotFoundException e) {
			System.out.println("Statistic: write fail");
		} catch (IOException e) {
			System.out.println("Statistic: write fail 2");
		}
	}
	
	public static void main(String args[]) {
		ProcessResults process = new ProcessResults();
		process.retriveData();
	}
}
