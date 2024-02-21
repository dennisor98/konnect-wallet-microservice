package net.sasakonnect.wallet.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import javax.swing.JPanel;

import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.itextpdf.io.IOException;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;

@Service
public class AccountStatementService {
  
    public void readFileAndGeneratePDF(String filePath,String mobileNumber) throws GeneralSecurityException {
//        File excelFilePath = new File("/home/ronny/Downloads/Choice_BaaS_Customer_Statement_20240116_20240215_46012000014879.xlsx");
        File excelFilePath =  new File(filePath);
        String password = mobileNumber.substring(mobileNumber.length() -6);
        // Define the table headers
        String[] expectedHeaders = {"Transaction Date", "Transaction ID", "Transaction Type", "Transaction Details", "Debit Amount", "Credit Amount", "Account Balance"};

        try (InputStream inputStream = new FileInputStream(excelFilePath)) {
            // Decrypt the workbook if it is password-protected
            POIFSFileSystem poifs = new POIFSFileSystem(inputStream);
            EncryptionInfo info = new EncryptionInfo(poifs);
            Decryptor decryptor = Decryptor.getInstance(info);
            decryptor.verifyPassword(password);
            InputStream decryptedStream = decryptor.getDataStream(poifs);

            // Create a workbook instance from the decrypted input stream
            XSSFWorkbook workbook = new XSSFWorkbook(decryptedStream);

            // Reading the first sheet of the excel file
            XSSFSheet sheet = workbook.getSheetAt(0);

            // Create a PDF document
            try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter("/home/ronny/Downloads/ExcelData.pdf"))) {
                Document document = new Document(pdfDocument, PageSize.A4.rotate());
                document.setMargins(10,100,10,100);
                PdfPage pdfPage = pdfDocument.addNewPage();           
                

                // Add watermark event handler
                pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, new WatermarkEventHandler("Konnect Wallet"));

                // Extract information from rows 8 to 16 and add it to the infoTable
                Table infoTable = new Table(UnitValue.createPercentArray(new float[]{100,100}));
                infoTable.setWidth(UnitValue.createPercentValue(100));
                infoTable.setSpacingRatio(1);
                addInfoToTable(infoTable, sheet);

                document.add(infoTable);
                
                // Create a table for the PDF content with the specified number of columns
                Table table = new Table(expectedHeaders.length);

                // Find the last row index with data
                int lastRowIndex = sheet.getLastRowNum();

                // Iterating all the rows, starting from the 18th row up to the last row with data
                boolean allRowsProcessed = false;
                for (int rowIndex = 17; rowIndex <= lastRowIndex; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    // Check if all rows have been processed
                    if (rowIndex == 17) {
                        for (org.apache.poi.ss.usermodel.Cell cell : row) {
                            Cell pdfCell = new Cell();
                            pdfCell.add(new Paragraph(cell.getStringCellValue()).setFontColor(ColorConstants.WHITE));
                            pdfCell.setBackgroundColor(DeviceRgb.BLACK);
                            pdfCell.setFontSize(6);
                            table.addCell(pdfCell);
                        }
                    } else {
                        // Adding each row as cells in the table
                        for (org.apache.poi.ss.usermodel.Cell cell : row) {
                            String value = "";
                            // Check the cell type and retrieve value accordingly
                            if (cell.getCellType() == CellType.STRING) {
                                value = cell.getStringCellValue();
                            } else if (cell.getCellType() == CellType.NUMERIC) {
                                value = Double.toString(cell.getNumericCellValue());
                            } else if (cell.getCellType() == CellType.BOOLEAN) {
                                value = Boolean.toString(cell.getBooleanCellValue());
                            }
                            Cell pdfCell = new Cell();
                            pdfCell.setFontSize(4);
                            pdfCell.add(new Paragraph(value));
                            table.addCell(pdfCell);
                        }
                    }
                }

                // Add the table headers

                // Add the table to the document
                document.add(table);
           
                // Check if all rows have been processed, if not, log a warning
                if (!allRowsProcessed) {
                    System.out.println("Warning: Not all rows from the Excel sheet have been processed.");
                }
                document.getPdfDocument().setDefaultPageSize(PageSize.A4);

            }

        } catch (IOException | java.io.IOException ex) {
            ex.printStackTrace();
        }
    }
    

    // Method to get the column index of a header in the Excel sheet
    private int getColumnIndex(XSSFSheet sheet, String header) {
        Row headerRow = sheet.getRow(17); // Assuming headers are in the 18th row
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            String headerValue = headerRow.getCell(i).getStringCellValue().trim();
            if (header.equalsIgnoreCase(headerValue)) {
                return i;
            }
        }
        return -1;
    }

    // Method to add key-value pairs to the infoTable with font size 6 and stretch second column
    private void addInfoToTable(Table table, XSSFSheet sheet) throws java.io.IOException {

        for (int i = 7; i <= 15; i++) {
            Row row = sheet.getRow(i);
            String key = row.getCell(0).getStringCellValue();
            String value = row.getCell(1).getStringCellValue();

            Cell keyCell = new Cell();
            Paragraph keyParagraph = new Paragraph(key).setFontSize(5);
            keyCell.add(keyParagraph);
            keyCell.setBorder(Border.NO_BORDER);
            keyCell.setFontSize(22);
            keyCell.setPadding(0);
            table.addCell(keyCell);

            Cell valueCell = new Cell(1, 1);// Stretch second column
            Paragraph valueParagraph = new Paragraph(value).setFontSize(4);
            valueCell.setPadding(0);
            valueCell.add(valueParagraph);
            // Remove borders
            valueCell.setBorder(Border.NO_BORDER);
            table.addCell(valueCell);
        }
    }

    // Custom event handler for adding watermark to each page
    private static class WatermarkEventHandler implements IEventHandler {
        private String watermarkText;

        public WatermarkEventHandler(String watermarkText) {
            this.watermarkText = watermarkText;
        }
        
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            Rectangle pageSize = page.getPageSize();
            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

            // Define watermark properties
            float fontSize = 16;
            float x = (pageSize.getWidth() / 2) - 100;
            float y = (pageSize.getHeight() / 2) - 100;
            float angle = (float) Math.toRadians(45);

            // Write watermark text
            canvas.setStrokeColor(new DeviceRgb(255, 165, 0)); // Orange color
            PdfExtGState extGState = new PdfExtGState().setStrokeOpacity(0.5f); // Set opacity to 50%
            canvas.setExtGState(extGState);
            canvas.setFontAndSize(canvas.getDocument().getDefaultFont(), fontSize);
            canvas.beginText()
                    .setTextMatrix((float) Math.cos(angle), (float) Math.sin(angle), (float) (-Math.sin(angle)), (float) Math.cos(angle), x, y)
                    .showText(watermarkText)  // Use showText method to draw the text
                    .endText();

            canvas.release();
        }
    }
}
