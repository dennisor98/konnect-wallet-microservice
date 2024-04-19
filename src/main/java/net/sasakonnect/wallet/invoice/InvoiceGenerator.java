package net.sasakonnect.wallet.invoice;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.annotation.PostConstruct;
import net.sasakonnect.wallet.domain.invoice.*;
import net.sasakonnect.wallet.domain.invoice.InvoiceMetaData;

@Component
public class InvoiceGenerator {
Document document = new Document(PageSize.A6,0,0,20,0);

PdfWriter writer;
@Value("classpath:static/background.jpg")
Resource IMAGE;

@Value("classpath:static/logo.png")
Resource LOGO;

@Value("classpath:static/ceo_sign.png")
Resource CEO_SIGN;

@Value("classpath:static/kabiru_sign.png")
Resource DIRECTOR_SIGN;

//    public  final Image IMAGE = "resources/static/background.jpg";
//    public  final String LOGO = "resources/static/logo.png";
//    public  final String CEO_SIGN = "resources/static/ceo_sign.png";
//    public  final String DIRECTOR_SIGN= "resources/static/kabiru_sign.png";


	  
	    public void init(String path) throws DocumentException, MalformedURLException, IOException {
		  writer=PdfWriter.getInstance(document, new FileOutputStream(path));
		  document.open(); 
			buildBackground(writer,document);
			
			buildLogo(writer,document,210,330);
			

		  
		  
	    }
	    public void close() throws DocumentException, IOException {
	    	addWaterMark();
	    	document.close();
	    	
//	    	writer.close();
	    }
	public  void writeRaw(PdfWriter writer,String text,Font font,float x,float y,boolean underline) {
		 PdfContentByte cb = writer.getDirectContent();

         cb.beginText();
         cb.setFontAndSize(font.getBaseFont(), font.getSize());
         cb.setTextMatrix(x, y); // Adjust the coordinates as needed

//         cb.setTextMatrix(130, 355); // Adjust the coordinates as needed
         cb.showText(text);
         cb.endText();
         cb.stroke();
         float textWidth = font.getBaseFont().getWidthPoint(text, font.getSize());

         cb.moveTo(x, y - font.getSize() / 3); // Adjust position of underline
         cb.lineTo(x + textWidth, y - font.getSize() / 3); // Draw line
         cb.stroke();
	}
	public  void writeRaw(PdfWriter writer,String text,Font font,float x,float y) {
		 PdfContentByte cb = writer.getDirectContent();

        cb.beginText();
        cb.setFontAndSize(font.getBaseFont(), font.getSize());
        cb.setTextMatrix(x, y); // Adjust the coordinates as needed

//        cb.setTextMatrix(130, 355); // Adjust the coordinates as needed
        cb.showText(text);
        cb.endText();
        cb.stroke();
       
	}
	public  void writeText(PdfWriter writer,String text,float x,float y) {
	      PdfContentByte contentByte = writer.getDirectContent();

          // Define the font and size for the text
          Font font = FontFactory.getFont(FontFactory.COURIER, 12,BaseColor.RED);

          // Define the text content

          // Define the position to add text (in points)
         
          // Write text at the specified position
          contentByte.beginText();
          contentByte.setFontAndSize(font.getBaseFont(), font.getSize());
          contentByte.setTextMatrix(x, y);
          contentByte.showText(text);
          contentByte.endText();
	}
	public  void buildTableTotal(Document document,PdfWriter writer,Font font,String amount) throws DocumentException {
		
		   PdfPTable totalTable = new PdfPTable(3); // Create a table with 3 columns
		   totalTable.setTotalWidth((float) ((document.right(document.rightMargin())
		  		    - document.left(document.leftMargin()))*0.8));
           // Create a cell spanning the entire width
           PdfPCell cell = new PdfPCell(new Paragraph("TOTAL", font));
           cell.setBorderWidthRight(0);
           cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
           cell.setHorizontalAlignment(Element.ALIGN_LEFT);
           totalTable.addCell(cell);
           PdfPCell cell1 = new PdfPCell(new Paragraph("", font));
           cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

           cell1.setBorderWidthLeft(0);
           cell1.setBorderWidthRight(0);
           totalTable.addCell(cell1);
           PdfPCell cell2 = new PdfPCell(new Paragraph("Ksh "+amount, font));
           cell2.setBorderWidthLeft(0);
           cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
           cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);

           totalTable.addCell(cell2);
           totalTable.writeSelectedRows(0, -1,
        		   (float) 29.5,
       		    67,
        		    writer.getDirectContent());
	}
  public  void buildTable(Document document,PdfWriter writer,List<InvoiceItem> invoiceItems) throws DocumentException {
		
        float[] columnWidths = {1f, 3f, 2f,2f,2f}; // Adjust these values as needed

  	PdfPTable table = new PdfPTable(columnWidths);
  	table.setTotalWidth((float) ((document.right(document.rightMargin())
  		    - document.left(document.leftMargin()))*0.8));
  	
  	// Set the border color of the table to red

  	
  	// Add PDF Table Header ->
		Stream.of("NO.", "DESCRIPTION","NO.TXT ","TAX", "AMOUNT(Ksh)")
		    .forEach(headerTitle -> {
		          PdfPCell header = new PdfPCell();
		          
		          Font headFont = FontFactory.getFont(FontFactory.COURIER);
		          headFont.setSize(6);
		          header.setBackgroundColor(BaseColor.LIGHT_GRAY);
		          header.setHorizontalAlignment(Element.ALIGN_CENTER);
	            	header.setVerticalAlignment(Element.ALIGN_CENTER);

		          header.setBorderWidth(1);
		          header.setPhrase(new Phrase(headerTitle, headFont));
		          table.addCell(header);
		    });
		var count=1;
      for (InvoiceItem invoiceItem : invoiceItems) {
    	  invoiceItem.setCount(count++);
      	Font headFont = FontFactory.getFont(FontFactory.COURIER);
	          headFont.setSize(6);
      	PdfPCell countCell = new PdfPCell(new Phrase(String.valueOf(invoiceItem.getCount()),headFont));
      	
	          
      	countCell.setPaddingLeft(2);
      	countCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
      	countCell.setHorizontalAlignment(Element.ALIGN_CENTER);
          table.addCell(countCell);

          PdfPCell description = new PdfPCell(new Phrase(invoiceItem.getDescription(),headFont));
          description.setPaddingLeft(2);
          description.setVerticalAlignment(Element.ALIGN_MIDDLE);
          description.setHorizontalAlignment(Element.ALIGN_CENTER);
          table.addCell(description);
          

          PdfPCell transactionCount = new PdfPCell(new Phrase(invoiceItem.getNoOfTransaction(),headFont));
          transactionCount.setPaddingLeft(2);
          transactionCount.setVerticalAlignment(Element.ALIGN_MIDDLE);
          transactionCount.setHorizontalAlignment(Element.ALIGN_CENTER);
          table.addCell(transactionCount);
          
          PdfPCell taxt = new PdfPCell(new Phrase(String.valueOf(invoiceItem.getTax()),headFont));
          taxt.setPaddingLeft(2);
          taxt.setVerticalAlignment(Element.ALIGN_MIDDLE);
          taxt.setHorizontalAlignment(Element.ALIGN_CENTER);
          table.addCell(taxt);

          PdfPCell amountCell = new PdfPCell(new Phrase(String.valueOf(invoiceItem.getAmount()),headFont));
          amountCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
          amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
          amountCell.setPaddingRight(2);
          table.addCell(amountCell);
      }
     // document.add(table);
      
      table.writeSelectedRows(0, -1,
    		   (float) 29.5,
    		    180,
    		    writer.getDirectContent());
	  
  }
  public  void buildBackground( PdfWriter writer,Document document) throws MalformedURLException, IOException, DocumentException {
	  PdfContentByte canvas = writer.getDirectContent();
	  InputStream imageStream   = IMAGE.getInputStream();
	  byte[] imageData = StreamUtils.copyToByteArray(imageStream);
//	  var  imagef=ImageDataFactory.create(imageData);
      // Add image to the canvas
       Image image = Image.getInstance(imageData);
       
       // Calculate scaling factors
       float widthRatio = document.getPageSize().getWidth() / image.getWidth();
       float heightRatio = document.getPageSize().getHeight() / image.getHeight();
       
       // Choose the minimum ratio to maintain aspect ratio and fit the image on the page
       float ratio = Math.max(widthRatio, heightRatio);
       
       // Scale image dimensions
       float scaledWidth = image.getWidth() * ratio;
       float scaledHeight = image.getHeight() * ratio;
       
       // Set position and scale of the image
       image.scaleToFit((float) (scaledWidth*0.95), (float) (scaledHeight*1.5));
       image.setAbsolutePosition(0, 0); // Set position of the image
       canvas.addImage(image);
  }
  public  void buildLogo( PdfWriter writer,Document document,float x,float y) throws MalformedURLException, IOException, DocumentException {
	  PdfContentByte canvas = writer.getDirectContent();
	  InputStream imageStream   = LOGO.getInputStream();
	  byte[] imageData = StreamUtils.copyToByteArray(imageStream);
      // Add image to the canvas
       Image image = Image.getInstance(imageData);
       
       // Calculate scaling factors
       float widthRatio = document.getPageSize().getWidth() / image.getWidth();
       float heightRatio = document.getPageSize().getHeight() / image.getHeight();
       
       // Choose the minimum ratio to maintain aspect ratio and fit the image on the page
       float ratio = Math.max(widthRatio, heightRatio);
       
       // Scale image dimensions
       float scaledWidth = image.getWidth() * ratio;
       float scaledHeight = image.getHeight() * ratio;
       
       // Set position and scale of the image
       image.scaleToFit(50,50);
       image.setAbsolutePosition(x, y); // Set position of the image
       canvas.addImage(image);
  }

public void addMetaData(InvoiceMetaData invoiceData) {


    Font font = FontFactory.getFont(FontFactory.COURIER, 12);

 
  Font headFont = FontFactory.getFont(FontFactory.COURIER);
  headFont.setSize(8);
  writeRaw(writer,"INVOICE",font,120, 345);
 
  var from=invoiceData.getFrom();
  var to=invoiceData.getBillTo();
  writeRaw(writer,"FROM:",font,25, 335);
  writeRaw(writer,from.getName(),headFont,25, 325);
  writeRaw(writer,from.getAddress(),headFont,25, 315);
  writeRaw(writer,from.getStreat(),headFont,25, 305);
  
  writeRaw(writer,"BILL TO:",font,25, 290);
  writeRaw(writer,to.getName(),headFont,25, 280);
  writeRaw(writer,to.getAddress(),headFont,25, 270);
  writeRaw(writer,to.getStreat(),headFont,25, 260);
	
}
public void addInvoiceData(InvoiceMetaData invoiceData) {
     Font invoicedateFont = FontFactory.getFont(FontFactory.COURIER, 6);

     writeRaw(writer,"INVOICE NO     :",invoicedateFont,160, 280);
     writeRaw(writer,"INVOICE DATE   :",invoicedateFont,160, 270);
     writeRaw(writer,"FROM           :",invoicedateFont,160, 260);
     writeRaw(writer,"TO             :",invoicedateFont,160, 250);


     writeRaw(writer,invoiceData.getInvoiceNo(),invoicedateFont,220, 280);
     writeRaw(writer,invoiceData.toHumanInvoiceDate(),invoicedateFont,220, 270);
     writeRaw(writer,invoiceData.toHumanInvoiceFrom(),invoicedateFont,220, 260);
     writeRaw(writer,invoiceData.toHumanInvoiceTo(),invoicedateFont,220, 250);
	
}
public void addSubject(InvoiceMetaData invoiceData) {
	 Font subjectFont = FontFactory.getFont(FontFactory.COURIER);
     subjectFont.setSize(6);

     // Convert the Instant to LocalDate
     LocalDate localDate = LocalDateTime.ofInstant(invoiceData.getInvoiceFrom().toInstant(), ZoneId.systemDefault()).toLocalDate();

     // Set the desired date (1st June 2024)

     // Create a DateTimeFormatter with the desired pattern
     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");

     // Format the LocalDate object
     String formattedDate = localDate.format(formatter);
     
     
     LocalDate localDateTo = LocalDateTime.ofInstant(invoiceData.getInvoiceTo().toInstant(), ZoneId.systemDefault()).toLocalDate();

     // Set the desired date (1st June 2024)

     // Create a DateTimeFormatter with the desired pattern
     DateTimeFormatter formatterTo = DateTimeFormatter.ofPattern("d MMMM yyyy");

     // Format the LocalDate object
     String formattedDateTo = localDateTo.format(formatter);
     
     
    writeRaw(writer,"Revenue Share from ["+formattedDate +"] though ["+ formattedDateTo+"]",subjectFont,50, 200,true);

	
}
private void addWaterMark() throws DocumentException, IOException {
	  PdfContentByte content = writer.getDirectContent();

      // Create a font for the watermark
      BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
      Font font = new Font(baseFont, 60);
      font.setColor(BaseColor.LIGHT_GRAY);
      PdfGState gs = new PdfGState();
      gs.setFillOpacity(0.5f); // Set opacity (0.0f to 1.0f)

      // Set the opacity
      content.setGState(gs);
      // Set watermark text
      String watermarkText = "Konnect Wallet";

      // Add the watermark to each page
      int totalPages = writer.getPageNumber();
      for (int i = 1; i <= totalPages; i++) {
          // Add watermark at the center of each page
          content.beginText();
          content.setFontAndSize(baseFont, 30);
          content.setColorFill(BaseColor.LIGHT_GRAY);
          content.showTextAligned(com.itextpdf.text.Element.ALIGN_CENTER, watermarkText,
                  document.getPageSize().getWidth() / 2,
                  document.getPageSize().getHeight() / 2, 45);
          content.endText();

      }
      content.stroke();
      gs.setFillOpacity(0.5f); // Set opacity (0.0f to 1.0f)

      content.setGState(gs);




}
public void loadTable(List<InvoiceItem> invoiceItem) {
	try {
		buildTable(document,writer,invoiceItem);
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	loadTotalText(invoiceItem);
	
}
private void loadTotalText(List<InvoiceItem> invoiceItems) {
    Font contentFont = FontFactory.getFont(FontFactory.COURIER, 6);
    BigDecimal totalSum = invoiceItems.stream()
            .map(a->BigDecimal.valueOf(a.getAmount())) // Extract the value of each InvoiceItem
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalTax = invoiceItems.stream()
            .map(a->BigDecimal.valueOf(a.getTax())) // Extract the value of each InvoiceItem
            .reduce(BigDecimal.ZERO, BigDecimal::add);

	 writeRaw(writer,"Subtotal",contentFont,170, 90);
     writeRaw(writer,"Ksh "+String.valueOf(totalSum),contentFont,210, 90);
     
     writeRaw(writer,"Total Tax",contentFont,170, 80);
     writeRaw(writer,"Ksh "+String.valueOf(totalTax),contentFont,210, 80);
	
}
public void loadTotalRow(List<InvoiceItem> invoiceItems) {
    Font contentFont = FontFactory.getFont(FontFactory.COURIER, 8);
    contentFont.setStyle(Font.BOLD);

	 BigDecimal totalSum = invoiceItems.stream()
	            .map(a->BigDecimal.valueOf(a.getAmount())) // Extract the value of each InvoiceItem
	            .reduce(BigDecimal.ZERO, BigDecimal::add);
	// TODO Auto-generated method stub
	try {
		buildTableTotal(document,writer,contentFont,String.valueOf(totalSum));
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
public void generateBy(String name) {
    Font contentFont = FontFactory.getFont(FontFactory.COURIER, 6);

	 writeRaw(writer,"Generated By : "+ name,contentFont,10, document.getPageSize().getHeight()-20);

}
public void signInvoice() {
	try {
		this.ceoSign(writer, document, 55, 30);
		this.directorSign(writer, document,190, 30);
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
private  void ceoSign( PdfWriter writer,Document document,float x,float y) throws MalformedURLException, IOException, DocumentException {
	  PdfContentByte canvas = writer.getDirectContent();
	  InputStream imageStream   = CEO_SIGN.getInputStream();
	  byte[] imageData = StreamUtils.copyToByteArray(imageStream);
    // Add image to the canvas
     Image image = Image.getInstance(imageData);
     
     // Calculate scaling factors
     float widthRatio = document.getPageSize().getWidth() / image.getWidth();
     float heightRatio = document.getPageSize().getHeight() / image.getHeight();
     
     // Choose the minimum ratio to maintain aspect ratio and fit the image on the page
     float ratio = Math.max(widthRatio, heightRatio);
     
     // Scale image dimensions
     float scaledWidth = image.getWidth() * ratio;
     float scaledHeight = image.getHeight() * ratio;
     
     // Set position and scale of the image
     image.scaleToFit(70,70);
     image.setAbsolutePosition(x, y); // Set position of the image
     canvas.addImage(image);
}
private  void directorSign( PdfWriter writer,Document document,float x,float y) throws MalformedURLException, IOException, DocumentException {
	  PdfContentByte canvas = writer.getDirectContent();
	  InputStream imageStream   = DIRECTOR_SIGN.getInputStream();
	  byte[] imageData = StreamUtils.copyToByteArray(imageStream);
  // Add image to the canvas
   Image image = Image.getInstance(imageData);
   
   // Calculate scaling factors
   float widthRatio = document.getPageSize().getWidth() / image.getWidth();
   float heightRatio = document.getPageSize().getHeight() / image.getHeight();
   
   // Choose the minimum ratio to maintain aspect ratio and fit the image on the page
   float ratio = Math.max(widthRatio, heightRatio);
   
   // Scale image dimensions
   float scaledWidth = image.getWidth() * ratio;
   float scaledHeight = image.getHeight() * ratio;
   
   // Set position and scale of the image
   image.scaleToFit(50,50);
   image.setAbsolutePosition(x, y); // Set position of the image
   canvas.addImage(image);
}
}
