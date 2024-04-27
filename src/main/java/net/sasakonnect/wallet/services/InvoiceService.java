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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.domain.invoice.InvoiceItem;
import net.sasakonnect.wallet.domain.invoice.InvoiceMetaData;
import net.sasakonnect.wallet.domain.invoice.Tariff;
import net.sasakonnect.wallet.enums.LogTypes;
import net.sasakonnect.wallet.invoice.InvoiceManager;
import net.sasakonnect.wallet.repository.InvoiceJobRepository;
import net.sasakonnect.wallet.repository.LogsRepository;
import net.sasakonnect.wallet.repository.RejectedAccountRepository;
import net.sasakonnect.wallet.repository.TarrifRepository;
import net.sasakonnect.wallet.repository.TransactionRepository;
import net.sasakonnect.wallet.ResponseDto.InvoiceDataDTO;
import net.sasakonnect.wallet.constant.ChannelType;
import net.sasakonnect.wallet.domain.InvoiceJob;
import net.sasakonnect.wallet.domain.Logs;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.domain.User;
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
	
	@Autowired
	InvoiceJobRepository  invoiceJobRepository;
	
	@Autowired
	LogsRepository logsRepository;
	
	

	@Transactional
   public ResponseEntity<Object> generateInvoice(Date startDate,Date endDate){
	   List<ChannelType> channelTypes = new ArrayList<>(Arrays.asList(ChannelType.MPESA_ACCOUNT,
			   ChannelType.MPESA_PAYBILL, ChannelType.MPESA_TILL, ChannelType.PESA_LINK,
			   ChannelType.WALLET));
	   
	   ArrayList<InvoiceItem> ivoiceItems= new ArrayList<InvoiceItem>();
	   var invoiceItem1 = this.computeValidTransactionsByChannel(ChannelType.MPESA_ACCOUNT, startDate, endDate);
//	   return ResponseEntity.status(HttpStatus.OK).body(invoiceItem1);
	       ivoiceItems.add(invoiceItem1);
	   var invoiceItem2 = this.computeValidTransactionsByChannel(ChannelType.MPESA_TILL, startDate, endDate);
	       ivoiceItems.add(invoiceItem2);
	   var invoiceItem3 = this.computeValidTransactionsByChannel(ChannelType.PESA_LINK, startDate, endDate);
	     ivoiceItems.add(invoiceItem3);
       var invoiceItem4 = this.computeValidTransactionsByChannel(ChannelType.MPESA_PAYBILL, startDate, endDate);
          ivoiceItems.add(invoiceItem4);
     var invoiceItem5 = this.computeValidTransactionsByChannel(ChannelType.WALLET, startDate, endDate);
          ivoiceItems.add(invoiceItem5);
//	  var invoiceItems = channelTypes.stream().map(channel ->{
//		   
//         return ivoiceItems;
//	   });
      
       try {
              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
              var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
              var invoicemetaData = InvoiceMetaData.builder()
                              .invoiceNo( LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).format(formatter))
                               .invoiceFrom(startDate)
                               .invoiceTo(endDate)
                               .invoiceDate(new Date()).build();                 
              invoicemanager.init(startDate,endDate);
              invoicemanager.invoiceMetaData(invoicemetaData);
              invoicemanager.loadData(ivoiceItems);
              invoicemanager.signInvoice();
              invoicemanager.generateBy(user.getFirstName()+" "+user.getMiddleName()+" "+user.getLastName());
              invoicemanager.close();
              Map<String,Object> map = new HashMap<>();
              map.put("success",true);
              map.put("message","Request successful");
              
              var log = Logs.builder()
             		 .activity(LogTypes.INVOICE_REQUEST)
             		 .description(user.getFirstName()+" "+user.getLastName()+"of id:"+user.getId()+"requested transactions invoice")
             		 .build();
              this.logsRepository.save(log);
              } catch (DocumentException | IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
      }
	   return null;
   }
      
   private  InvoiceItem computeValidTransactionsByChannel(ChannelType channel,Date startDate,Date endDate){
	   List<Tariff> tarrifs = this.tarrifRepository.findByChannelTypeOrderByMinAsc(channel);
	   List<Transaction> transactions = this.transactionService.getAllTransactions(channel, startDate, endDate);
	   log.info(tarrifs+"");
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

	   return InvoiceDataDTO.builder()
			   .description(channel.getValue())
			   .noOfTransaction("0")
			   .build();
   }
   
   private Tariff getTarrifByChannelAndAmount(List<Tariff> tariffs, BigDecimal amount) {
       Optional<Tariff> tariffOptional = tariffs.stream()
               .filter(tariff ->amount.doubleValue() >= tariff.getMin() && amount.doubleValue() <= tariff.getMax())
               .findFirst();
       return tariffOptional.orElse(null);
   }
   
   
   public ResponseEntity<Object> getInvoices(Integer pageNumber,Integer pageSize) {
	   Page<InvoiceJob> invoices = this.invoiceJobRepository.findByOrderByCreatedAtDesc(PageRequest.of(pageNumber,pageSize));
	   Map<String,Object> resMap =  new HashMap<>();

	   if(!invoices.isEmpty()) {
		   
		   Map<String,Object> map = new HashMap<>();
		   map.put("hasMore",invoices.hasNext());
		   map.put("nextPage",invoices.hasNext() ? invoices.nextPageable().getPageNumber()  : null);
		   map.put("hasPrevious",invoices.hasPrevious());
		   map.put("previousPage",invoices.hasPrevious()? invoices.previousPageable().getPageNumber() : null);
	       map.put("invoices",invoices.get().map(i->{
	    	   Map<String,Object> invoiceMap = new HashMap<>();
	    	   invoiceMap.put("id",i.getId());
	    	   invoiceMap.put("owner",i.getJobOwner().getFirstName() +" "+i.getJobOwner().getMiddleName() +" "+i.getJobOwner().getLastName());
	    	   invoiceMap.put("period","From "+i.getStartDate().toString().split(" ")[0]+" to " +i.getEndDate().toString().split(" ")[0]);
	    	   invoiceMap.put("description",i.getDescription());
	    	   invoiceMap.put("link",i.getDownloadLink());
	    	   return invoiceMap;
	       }).collect(Collectors.toList()));
		  resMap.put("payload",map);
	   }else {
		   Map<String,Object> map = new HashMap<>();
		   map.put("invoices",new ArrayList<>());
		   resMap.put("payload",map);
	   }
	   log.info(invoices.getContent().get(0).toString());
	   return ResponseEntity.status(HttpStatus.OK).body(resMap);

   }
 
}
