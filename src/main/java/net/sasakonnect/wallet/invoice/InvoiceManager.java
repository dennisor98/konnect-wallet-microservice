package net.sasakonnect.wallet.invoice;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.DocumentException;

import jakarta.annotation.PostConstruct;
import net.sasakonnect.wallet.domain.invoice.InvoiceItem;
import net.sasakonnect.wallet.domain.invoice.InvoiceMetaData;
@Component
public class InvoiceManager {
    @Value("${invoicePath:}")
    String invoicePath;
    @Autowired
    InvoiceGenerator invoicegenerator;
    
    public void init(String path ) throws MalformedURLException, DocumentException, IOException {
      
            // Generate random name
        	invoicePath = path+".pdf";
        
        System.out.println(invoicePath);
        this.invoicegenerator.init(invoicePath);
        
    }
    public void close() throws DocumentException, IOException {
    	this.invoicegenerator.close();
    }
    public void  generateBy(String name) {
    	this.invoicegenerator.generateBy(name);
    }
    public void init( ) throws MalformedURLException, DocumentException, IOException {
        if (invoicePath.isEmpty()) {
            // Generate random name
        	invoicePath = UUID.randomUUID().toString()+".pdf";
        }
        System.out.println(invoicePath);
        this.invoicegenerator.init(invoicePath);
        
    }
    
    public void loadData(List<InvoiceItem> invoiceItem){
    	this.invoicegenerator.loadTable(invoiceItem);
    	this.invoicegenerator.loadTotalRow(invoiceItem);
    	
    }
    public void invoiceMetaData(InvoiceMetaData invoiceData) {
    	this.invoicegenerator.addMetaData(invoiceData)	;
    	this.invoicegenerator.addInvoiceData(invoiceData);
    	this.invoicegenerator.addSubject(invoiceData);
    }
	public void signInvoice() {
		this.invoicegenerator.signInvoice();
		
	}

	

}

