package net.sasakonnect.wallet.invoice;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.itextpdf.text.DocumentException;

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.domain.InvoiceJob;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.invoice.InvoiceItem;
import net.sasakonnect.wallet.domain.invoice.InvoiceMetaData;
import net.sasakonnect.wallet.repository.InvoiceJobRepository;
@Component
public class InvoiceManager {
    @Value("${invoicePath}")
    String invoiceDirectory;
    
    @Value("${WALLET_BASE_URL}")
    String walletBaseUrl;
    
    @Autowired
    InvoiceGenerator invoicegenerator;
    
    @Autowired
    InvoiceJobRepository invoiceJobRepository;
    
    public void init(String path ) throws MalformedURLException, DocumentException, IOException {
      
            // Generate random name
        var	invoicePath = path+".pdf";
        
        System.out.println(invoicePath);
        this.invoicegenerator.init(invoicePath);
     
        
    }
    public void close() throws DocumentException, IOException {
    	this.invoicegenerator.close();
    }
    public void  generateBy(String name) {
    	this.invoicegenerator.generateBy(name);
    }
    public String init(Date startDate,Date endDate) throws MalformedURLException, DocumentException, IOException {
    	
    	    String uuid = UUID.randomUUID().toString();
    	  var  invoicePath = invoiceDirectory + "/" + uuid + ".pdf";
    	    this.invoicegenerator.init(invoicePath);
    	

        
    	this.persistMetadata(startDate,endDate, invoicePath);

        
        System.out.println(invoicePath);
        return invoicePath;
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

	public void persistMetadata(Date startDate,Date endDate,String link) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		 var invoiceJob = InvoiceJob.builder()
			        .description(null)
			        .startDate(startDate)
			        .endDate(endDate)
			        .jobOwner(user)
			        .downloadLink(walletBaseUrl+"ivoices/"+link.split("invoices/")[1])
			        .build();
		 this.invoiceJobRepository.save(invoiceJob);
			     
	}
	

}

