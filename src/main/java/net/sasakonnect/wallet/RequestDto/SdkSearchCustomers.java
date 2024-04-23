package net.sasakonnect.wallet.RequestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
@Data
@Builder
@AllArgsConstructor
public class SdkSearchCustomers {
	private Long createdAtStart;
	private Long createdAtEnd;
    @Min(value = 0, message = "Page number must be greater than zero or equal zero")

	private int pageNumber;
    @Min(value = 1, message = "Page number must be greater than zero")

	private int pageSize;
    public SdkSearchCustomers() {
    	var instant=Instant.now().getEpochSecond();
        if (this.createdAtStart == null) {
        	 Instant midnightInstant = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.DAYS);
             long startOfDay = midnightInstant.getEpochSecond();
            this.createdAtStart = startOfDay;
        }
         if(createdAtEnd==null||createdAtEnd>instant) {
        	 this.createdAtEnd=instant;
         }
    }

}
