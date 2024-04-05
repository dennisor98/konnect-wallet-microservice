package net.sasakonnect.wallet.domain.invoice;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceMetaData {
	String invoiceNo;
	Date invoiceDate;
	Date invoiceFrom;
	Date invoiceTo;
	@Builder.Default
	BillTo billTo=new BillTo();
	@Builder.Default
	From from= new From();
	public static String toHumanDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return  date.format(formatter);


	}
	public  String toHumanInvoiceDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
      var date=  invoiceDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return  date.format(formatter);


	}
	public  String toHumanInvoiceFrom(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        var date=  invoiceFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return  date.format(formatter);


	}
	public  String toHumanInvoiceTo(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        var date=  invoiceTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return  date.format(formatter);


	}
	

}
