package net.sasakonnect.wallet.RequestDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.annotations.ValidDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPeriod {
	String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	@ValidDate

	String startDate;
	@ValidDate

	String endDate;

	public Long getStartDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		DateTimeFormatter formatterNow = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		LocalDateTime currentDateTime = LocalDateTime.now().minusDays(30);
		var now = currentDateTime.format(formatterNow);
		LocalDateTime dateTime = LocalDateTime.parse(startDate == null ? now : startDate, formatter);
		return dateTime.toEpochSecond(ZoneOffset.UTC) * 1000;
	}

	public static Long getThisMonthStart() {
		LocalDateTime currentDateTime = LocalDateTime.now();

		// Subtract one month
		LocalDateTime lastMonthDateTime = currentDateTime.minusMonths(1);

		// Set the time to the start of the day
		lastMonthDateTime = lastMonthDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

		// Convert to epoch seconds (milliseconds)
		long epochSeconds = lastMonthDateTime.toEpochSecond(ZoneOffset.UTC);
		return epochSeconds * 1000;
	}

	public static Long getTimeNow() {
		return Instant.now().toEpochMilli();
	}

	public Long getEndDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		DateTimeFormatter formatterNow = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		LocalDateTime currentDateTime = LocalDateTime.now();
		var now = currentDateTime.format(formatterNow);
		LocalDateTime dateTime = LocalDateTime.parse(endDate == null ? now : endDate, formatter);
		return dateTime.toEpochSecond(ZoneOffset.UTC) * 1000;
	}
}
