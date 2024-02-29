package net.sasakonnect.wallet.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.Property;
import com.opencsv.CSVReader;
import net.sasakonnect.wallet.interfaces.PDFGenerationCallback;

@Service
public class AccountStatementService {
	@Value("${file.statementPath}")
	private String statementPath;

	public void readFileAndGeneratePDF(String jobId, String filePath, PDFGenerationCallback callBack) {
		Thread thread = new Thread(new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String[] expectedHeaders = { "Transaction Date", "Transaction ID", "Transaction Type",
						"Transaction Details", "Currency", "Debit Amount", "Credit Amount", "Account Balance" };

				try {
					// Download the CSV file from the provided URL
					File csvFile = downloadFile(filePath);

					// Process the downloaded CSV file
					try (InputStream inputStream = Files.newInputStream(csvFile.toPath());
							CSVReader csvReader = new CSVReader(new java.io.InputStreamReader(inputStream))) {

						try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(statementPath+jobId+".pdf"))) {
							
							
							String topImageFile = "/src/main/resources/static/images/receipt_2.png"; 
							ImageData topImage = ImageDataFactory.create(topImageFile);
							
							String bImageFile = "/src/main/resources/static/images/receipt_2.png"; 
							ImageData bottomImage = ImageDataFactory.create(bImageFile);
							Document document = new Document(pdfDocument, PageSize.A4.rotate());
							
							document.setMargins(20, 100, 20, 100);
							 for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
								    String bgImageFile = "/src/main/resources/static/images/receipt_1.png"; 
									ImageData bgImage = ImageDataFactory.create(bgImageFile);
	                                PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(i));
	                                canvas.addImage(bgImage, PageSize.A4.getWidth(), 0, 0, PageSize.A4.getHeight(), 0, 0);
	                            }
							document.add(new Paragraph("\n\n\n"));

							Table table = new Table(expectedHeaders.length);
							table.setProperty(Property.TABLE_LAYOUT, "fixed");
							// Read CSV row by row
							String[] nextRecord;
							boolean isFirstRow = true;
							while ((nextRecord = csvReader.readNext()) != null) {
								for (String cell : nextRecord) {
									Paragraph paragraph = new Paragraph(cell).setFontSize(10);
									if (isFirstRow) {
										paragraph.setBold().setFontSize(12)
												.setBackgroundColor(new DeviceRgb(211, 211, 211)); // Light
																									// gray
																									// color

									}
									table.addCell(new Cell().add(paragraph));
								}
								isFirstRow = false;
							}
							document.add(table);

						} catch (IOException ex) {
							ex.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} finally {
						// After processing, delete the CSV file
						Files.deleteIfExists(csvFile.toPath());
						callBack.onPDFGenerated(jobId, filePath);
						;
					}
				} catch (IOException ex) {
					ex.printStackTrace();
					callBack.onPDFGenerationFailed(jobId, ex);

				}
			}

		});

		thread.start();

	}

	// Method to download file from URL
	private File downloadFile(String fileURL) throws IOException {
		URL url = new URL(fileURL);
		File downloadedFile = File.createTempFile("tempfile", ".csv");
		try (InputStream in = url.openStream(); FileOutputStream out = new FileOutputStream(downloadedFile)) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}
		return downloadedFile;
	}
}
