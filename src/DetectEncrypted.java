import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.aspose.cells.FileFormatUtil;

public class DetectEncrypted {
   public static ArrayList<String> encryptedFiles = new ArrayList<String>();
   public static Map<String, String> extensionMap;
   public static String fileType;
	public static void main(String args[]) throws Throwable{
		String homePath, csvPath;
		csvPath = new java.io.File( "." ).getCanonicalPath();
		homePath = args[0];
		fileType = args[1];
		if(!(new File(homePath).isDirectory())){
			System.out.println(homePath + " is not a valid Directory!!");
			return;
		}
			
		if(!(fileType.equalsIgnoreCase("Document") || fileType.equalsIgnoreCase("SpreadSpheet") || fileType.equalsIgnoreCase("Presentation") || fileType.equalsIgnoreCase("PDF") || fileType.equalsIgnoreCase("All"))){
			System.out.println("Enter a Valid FileType!!");
			return;
		}
		extensionMap = createExtensionMap();
		recurseFolders(homePath);
		writeToCSV(csvPath);
	}
	
	static void recurseFolders(String folderName) throws Throwable{
		File[] files = new java.io.File(folderName).listFiles();
		int number = files.length;
		if(number != 0){
			for (int i=0; i<number; i++){
				if(!files[i].isDirectory()){
					checkEncryption(files[i]);
				}
				else
					recurseFolders(folderName + "/" + files[i].getName());			
			}		
		}
	}
	
	static void checkEncryption(File filePath) throws Throwable{
		String[] fileName = filePath.getName().split("\\.");
		String fileExtension = "." + fileName[fileName.length-1];	
		if(extensionMap.get("Document").contains(fileExtension) && (fileType.equalsIgnoreCase("Document") || fileType.equalsIgnoreCase("All"))){
			try{
				com.aspose.words.FileFormatInfo info = com.aspose.words.FileFormatUtil.detectFileFormat(filePath.toString());
				if(info.isEncrypted()){
					System.out.println(filePath.toString());
					encryptedFiles.add(filePath.toString());
				}
			} catch ( com.aspose.words.FileCorruptedException e){
				System.out.println("CORRUPTED/DAMAGED: " + filePath.toString());
			}
		}
		else if(extensionMap.get("SpreadSheet").contains(fileExtension) && (fileType.equalsIgnoreCase("SpreadSheet") || fileType.equalsIgnoreCase("All"))){
			try{
				com.aspose.cells.FileFormatInfo in = com.aspose.cells.FileFormatUtil.detectFileFormat(filePath.toString());
				if(in.isEncrypted()){
					System.out.println(filePath.toString());
					encryptedFiles.add(filePath.toString());
				}
			} catch (com.aspose.cells.CellsException e){
				System.out.println("CORRUPTED/DAMAGED: " + filePath.toString());
			}
		}
		
		else if(extensionMap.get("Presentation").contains(fileExtension) && (fileType.equalsIgnoreCase("Presentation") || fileType.equalsIgnoreCase("All"))){
			try{
		       com.aspose.slides.Presentation pres = new com.aspose.slides.Presentation(filePath.toString());
			} catch (com.aspose.slides.InvalidPasswordException e) {
		       System.out.println(filePath.toString());		 
		       encryptedFiles.add(filePath.toString());
			} catch (com.aspose.slides.PptxCorruptFileException e) {
				System.out.println("CORRUPTED/DAMAGED: " + filePath.toString());
			}
		}
		
		else if(extensionMap.get("PDF").contains(fileExtension) && (fileType.equalsIgnoreCase("PDF") || fileType.equalsIgnoreCase("All"))){
			com.aspose.pdf.facades.PdfFileInfo pdfFileInfo = null;
			try{
				pdfFileInfo = new com.aspose.pdf.facades.PdfFileInfo(filePath.toString());
				if (pdfFileInfo.isPdfFile() && (pdfFileInfo.hasOpenPassword() || pdfFileInfo.hasEditPassword())){
		        	System.out.println(filePath.toString());
		        	encryptedFiles.add(filePath.toString());
				}
			}
			catch(com.aspose.pdf.exceptions.PdfException e){
				System.out.println("CORRUPTED/DAMAGED: " + filePath.toString());
			}
		}
	}
	
	static int returnExtension(String fileExtension, Map<Integer, String> extensionMap) throws Throwable{
		for(int i=0; i<extensionMap.size(); i++){
			System.out.println(extensionMap.get(i+1));
			if(extensionMap.get(i+1).contains(fileExtension))
				return i+1;
		}
		return 0;
	}
	
	static Map<String, String> createExtensionMap() throws Throwable{
		Map<String, String> extensionMap = new HashMap<String, String>();
		extensionMap.put("Document", ".doc, .docx, .xml,");
		extensionMap.put("SpreadSheet", ".xls, .xlsx, .csv");
		extensionMap.put("Presentation", ".ppt, .pptx");
		extensionMap.put("PDF", ".pdf");
		return extensionMap;
	}
	
	static void writeToCSV(String csvPath) throws IOException{
		String fileName;
		fileName = "/EncryptedFiles-" + new Date().getTime() + ".csv";
		try
		{
		    FileWriter writer = new FileWriter(csvPath + fileName);
		    for(int i=0; i<encryptedFiles.size(); i++){
			    writer.append(encryptedFiles.get(i));
			    writer.append("\n");
		    }	 
		    writer.flush();
		    writer.close();
		    System.out.println("Encrypted Files list written successfully to " + csvPath + fileName);
		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 	
	}
}
