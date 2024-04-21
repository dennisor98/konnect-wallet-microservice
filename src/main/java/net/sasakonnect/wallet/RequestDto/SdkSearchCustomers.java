package net.sasakonnect.wallet.RequestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SdkSearchCustomers {
	  

	     @NotNull(message = "Phone numbers are required")
	    @Schema(description = "List of phone numbers of the customer", example = "[\"123456789\", \"987654321\"]")
	    private List<@Pattern(regexp = "^[0-9]{9}$", message = "Please enter a valid 9 digit number") String> phoneNumbers;

 public  List<PhoneCountryPair>  createPhonePairs(){
	 return  this.phoneNumbers.stream().map(phone-> PhoneCountryPair.builder()
			  .mobile(phone)
			  .countryCode("254")
			  .build()).collect(Collectors.toList());
	 
	   
   }
}
