package net.sasakonnect.wallet.services;

import java.io.IOException;
import java.math.BigDecimal;
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
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.domain.invoice.InvoiceItem;
import net.sasakonnect.wallet.domain.invoice.InvoiceMetaData;
import net.sasakonnect.wallet.domain.invoice.Tariff;
import net.sasakonnect.wallet.invoice.InvoiceManager;
import net.sasakonnect.wallet.repository.TarrifRepository;
import net.sasakonnect.wallet.repository.TransactionRepository;
import net.sasakonnect.wallet.ResponseDto.InvoiceDataDTO;
import net.sasakonnect.wallet.constant.ChannelType;
import net.sasakonnect.wallet.domain.Transaction;
@Slf4j
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
	
	
	
   public ResponseEntity<Object> generateInvoice(Date startDate,Date endDate){
	   List<ChannelType> channelTypes = new ArrayList<>(Arrays.asList(ChannelType.MPESA_ACCOUNT,
			   ChannelType.MPESA_PAYBILL, ChannelType.MPESA_TILL, ChannelType.PESA_LINK,
			   ChannelType.WALLET));
	   
	   ArrayList<InvoiceItem> ivoiceItems= new ArrayList<InvoiceItem>();
	   var invoiceItem1 = this.computeValidTransactionsByChannel(ChannelType.MPESA_ACCOUNT, startDate, endDate);
//	   return ResponseEntity.status(HttpStatus.OK).body(invoiceItem1);
	   ivoiceItems.add(invoiceItem1);
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
   
   private  InvoiceItem computeValidTransactionsByChannel(ChannelType channel,Date startDate,Date endDate){
	   List<Tariff> tarrifs = this.tarrifRepository.findByChannelTypeOrderByMinAsc(channel);
	   List<Transaction> transactions = this.transactionService.getAllTransactions(channel, startDate, endDate);
	   log.info(tarrifs.size()+"Tar"+transactions.size()+"");
	   if(!transactions.isEmpty() && !tarrifs.isEmpty()) {
		   var resultMap = transactions.stream()
                   .map(tr -> {
//                       Map<, Object> map = new HashMap<>();
                	   var transactionTariff = new InvoiceItem();
                	   transactionTariff.setTransaction(tr);
                       Tariff tariff = getTarrifByChannelAndAmount(tarrifs, tr.getAmount().abs());
                       transactionTariff.setTariff(tariff);
                       return transactionTariff;
                   })
                   .reduce(InvoiceItem.builder().build(), (acc, incomingInvoiceData) -> {
                	   log.info(incomingInvoiceData.getTariff().toString());
                	   log.info(incomingInvoiceData.getTransaction().toString());
                	   acc.setAmount(acc.getAmount()+incomingInvoiceData.getTariff().getTotalPartnerProfit());
                	   acc.setTax(acc.getTax()+incomingInvoiceData.getTariff().getExciseDutyTax());
                	   acc.setDescription(channel.getValue());
                	   acc.setTariff(null);
                	   acc.setTransaction(null);
                	   return acc;
                   });
		   resultMap.setNoOfTransaction(String.valueOf(transactions.size()));
         return resultMap;
	   }

	   return InvoiceDataDTO.builder().build();
   }
   
   private Tariff getTarrifByChannelAndAmount(List<Tariff> tariffs, BigDecimal amount) {
       Optional<Tariff> tariffOptional = tariffs.stream()
               .filter(tariff ->amount.doubleValue() >= tariff.getMin() && amount.doubleValue() <= tariff.getMax())
               .findFirst();
       return tariffOptional.orElse(null);
   }
   
 
}
