import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.aspose.cells.Workbook;

public class DetectEncrypted {
   public static ArrayList<String> encryptedFiles = new ArrayList<String>();
   public static Map<String, String> extensionMap;
   public static String fileType, fileName = "/EncryptedFiles-" + new Date().getTime() + ".csv";
   public static FileWriter writer;
	public static void main(String args[]) throws Throwable{
		String homePath, csvPath = new java.io.File( "." ).getCanonicalPath();
		writer = new FileWriter(csvPath + fileName);
		homePath = args[0];
		fileType = args[1];
		if(!(new File(homePath).isDirectory())){
			System.out.println(homePath + " is not a valid Directory!!");
			return;
		}
			
		if(!(fileType.equalsIgnoreCase("Document") || fileType.equalsIgnoreCase("SpreadSheet") || fileType.equalsIgnoreCase("Presentation") || fileType.equalsIgnoreCase("PDF") || fileType.equalsIgnoreCase("All"))){
			System.out.println("Enter a Valid FileType!!");
			return;
		}
		extensionMap = createExtensionMap();
		try{
			recurseFolders(homePath);
		}
		catch(Exception e){
			System.out.println("EXCEPTION: " + e.getCause());
		}
		finally{
			writer.close();
		    System.out.println("Encrypted Files list written successfully to " + csvPath + fileName);

		}
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
					writer.append(filePath.toString());
				    writer.append("\n");
				    writer.flush();
				}
			} /*catch ( com.aspose.words.FileCorruptedException e){
				System.out.println("CORRUPTED/DAMAGED: " + filePath.toString());
			} */catch (Exception e){
				System.out.println("EXCEPTION: " + e.getCause() + " <--> " + filePath.toString());
			}
		}
		else if(extensionMap.get("SpreadSheet").contains(fileExtension) && (fileType.equalsIgnoreCase("SpreadSheet") || fileType.equalsIgnoreCase("All"))){
			if(fileExtension.equalsIgnoreCase(".xlsx")){
				try{
					com.aspose.cells.FileFormatInfo in = com.aspose.cells.FileFormatUtil.detectFileFormat(filePath.toString());
					if(in.isEncrypted()){
						System.out.println(filePath.toString());
						writer.append(filePath.toString());
					    writer.append("\n");
					    writer.flush();
					}
				} /*catch (com.aspose.cells.CellsException e){
					System.out.println("CORRUPTED/DAMAGED: " + filePath.toString());
				} */catch (Exception e){
					System.out.println("EXCEPTION: " + e.getCause() + " <--> " + filePath.toString());
				}
			}
			if(fileExtension.equalsIgnoreCase(".xls")){
				try{
					FileInputStream file = new FileInputStream(new File(filePath.toString()));
					Workbook workbook = new Workbook(file);
				}
				catch (Exception e){
					if(e.toString().contains("Please provide password for the Workbook file.")){
						System.out.println(filePath.toString());
						writer.append(filePath.toString());
					    writer.append("\n");
					    writer.flush();
					}
					System.out.println("EXCEPTION: " + e.getMessage() + " <--> " + filePath.toString());
				}
			}
		}
		
		else if(extensionMap.get("Presentation").contains(fileExtension) && (fileType.equalsIgnoreCase("Presentation") || fileType.equalsIgnoreCase("All"))){
			if(fileExtension.equalsIgnoreCase(".ppt")){
				try{
			       com.aspose.slides.Presentation pres = new com.aspose.slides.Presentation(filePath.toString());			       
				} catch (com.aspose.slides.InvalidPasswordException e) {
			       System.out.println(filePath.toString());		 
			       writer.append(filePath.toString());
				    writer.append("\n");
				    writer.flush();
				}
				catch (Exception e){
					System.out.println("EXCEPTION: " + e.getCause() + " <--> " + filePath.toString());
				}
			}
			if(fileExtension.equalsIgnoreCase(".pptx")){
				try{
					com.aspose.slides.PresentationEx pre = new com.aspose.slides.PresentationEx(filePath.toString());
					if(pre.isEncrypted()){
						System.out.println(filePath.toString());		 
					       writer.append(filePath.toString());
						    writer.append("\n");
						    writer.flush();
					}
				}
				catch (Exception e){
					System.out.println("EXCEPTION: " + e.getCause() + " <--> " + filePath.toString());

				}
				
			}
		}
		
		else if(extensionMap.get("PDF").contains(fileExtension) && (fileType.equalsIgnoreCase("PDF") || fileType.equalsIgnoreCase("All"))){
			com.aspose.pdf.facades.PdfFileInfo pdfFileInfo = null;
			try{
				com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document(filePath.toString());
				pdfFileInfo = new com.aspose.pdf.facades.PdfFileInfo(filePath.toString());
				if (pdfFileInfo.hasOpenPassword() && pdfFileInfo.hasEditPassword()){
		        	System.out.println(filePath.toString());
		        	writer.append(filePath.toString());
				    writer.append("\n");
				    writer.flush();
				}
			} /*catch(com.aspose.pdf.exceptions.PdfException e){
				System.out.println("CORRUPTED/DAMAGED: " + filePath.toString());
			} */catch (Exception e){
				System.out.println("EXCEPTION: " + e.getCause() + " <--> " + filePath.toString());
			}
		}
	}
	
	static Map<String, String> createExtensionMap() throws Throwable{
		Map<String, String> extensionMap = new HashMap<String, String>();
		extensionMap.put("Document", ".doc, .docx, .xml,");
		extensionMap.put("SpreadSheet", ".xls, .xlsx");
		extensionMap.put("Presentation", ".ppt, .pptx");
		extensionMap.put("PDF", ".pdf");
		return extensionMap;
	}
}
