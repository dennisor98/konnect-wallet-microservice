package net.sasakonnect.wallet.services;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;

import net.sasakonnect.wallet.domain.invoice.InvoiceItem;
import net.sasakonnect.wallet.domain.invoice.InvoiceMetaData;
import net.sasakonnect.wallet.domain.invoice.Tariff;
import net.sasakonnect.wallet.invoice.InvoiceManager;
import net.sasakonnect.wallet.repository.TarrifRepository;
import net.sasakonnect.wallet.repository.TransactionRepository;
import net.sasakonnect.wallet.constant.ChannelType;
import net.sasakonnect.wallet.domain.Transaction;

@Service
public class InvoiceService {
	
	@Autowired
	InvoiceManager invoicemanager;
	@Autowired
	TransactionService transactionService;
	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	TarrifRepository tarrifRepository;
	
   public ResponseEntity<Object> generateInvoice(LocalDate startDate,LocalDate endDate){
	   List<ChannelType> channelTypes = new ArrayList<>(Arrays.asList(ChannelType.MPESA_ACCOUNT,
			   ChannelType.MPESA_PAYBILL, ChannelType.MPESA_TILL, ChannelType.PESA_LINK,
			   ChannelType.WALLET));
	   
	   ArrayList<InvoiceItem> ivoiceItems= new ArrayList<InvoiceItem>();
	   ivoiceItems.add(InvoiceItem.builder()
               .amount(300)
               .tax(400)
               .count(1)
               .noOfTransaction("40")
               .description(ChannelType.MPESA_ACCOUNT.getValue()).build()
               );
ivoiceItems.add(InvoiceItem.builder()
               .amount(250)
               .noOfTransaction("30")
               .count(2)
               .description(ChannelType.PESA_LINK.getValue()).build()
               );
ivoiceItems.add(InvoiceItem.builder()
               .amount(250)
               .noOfTransaction("30")
               .count(2)
               .description(ChannelType.MPESA_TILL.getValue()).build()
               );
ivoiceItems.add(InvoiceItem.builder()
               .amount(250)
               .noOfTransaction("30")
               .count(2)
               .description(ChannelType.MPESA_PAYBILL.getValue()).build()
               );
ivoiceItems.add(InvoiceItem.builder()
               .amount(250)
               .noOfTransaction("30")
               .count(2)
               .description(ChannelType.WALLET.getValue()).build()
               );
//	  var invoiceItems = channelTypes.stream().map(channel ->{
//		   
//         return ivoiceItems;
//	   });
      
       try {
              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
              var invoicemetaData=InvoiceMetaData.builder()
                              .invoiceNo( LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).format(formatter))
                               .invoiceFrom(Date.from(Instant.now().atZone(ZoneId.systemDefault()).minus(1,ChronoUnit.MONTHS).toInstant()))
                               .invoiceTo(Date.from(Instant.now()))
                               .invoiceDate(new Date()).build();                 
              invoicemanager.init("invoice");
              invoicemanager.invoiceMetaData(invoicemetaData);
              invoicemanager.loadData(ivoiceItems);
              invoicemanager.signInvoice();
              invoicemanager.generateBy("David Macharia");
              invoicemanager.close();
      } catch (DocumentException | IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
      }
	   return null;
   }
   
   private Object getInvoiceData() {
	   
	 return null;   
   }
   
   private List<Transaction>computeValidTransactionsByChannel(ChannelType channel,LocalDate startDate,LocalDate endDate){
	   List<Tariff> tarrifs = this.tarrifRepository.findByChannelTypeOrderByMinAsc(channel);
	   List<Transaction> transactions = this.transactionService.getAllTransactions(channel, startDate, endDate);
	   if(!tarrifs.isEmpty() && !tarrifs.isEmpty()) {
		   transactions.stream().map(tr->{
			  Map<String,Object> map = new HashMap<>(); 
			  map.put("amount", transactions);
			  return map;
		   });
	   }
	   return null;
   }
   
 
}
