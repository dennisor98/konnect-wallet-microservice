package net.sasakonnect.wallet.RequestDto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountStatementDto {
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "Start date (YYYY-MM-DD)", example = "2024-02-01")

	private LocalDate startDate;
	@Schema(description = "End date (YYYY-MM-DD)", example = "2024-02-01")

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	// Getters and setters
	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}
