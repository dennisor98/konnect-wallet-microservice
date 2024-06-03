package net.sasakonnect.wallet.RequestDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.enums.EmploymentStatus;
import net.sasakonnect.wallet.enums.Gender;
import net.sasakonnect.wallet.enums.IdType;
import net.sasakonnect.wallet.enums.MonthlyIncome;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EasyOnboardingRequestParams {
	@NotBlank
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Only alphabetical characters are allowed")
	String firstName;

	String middleName;

	@NotBlank
	String lastName;

	@NotBlank
	String birthday;

	@NotNull
	String gender;

	@NotBlank
	String countryCode;

	@NotBlank
	String mobile;

	@NotBlank
	String idType;

	@Nullable()
	String monthlyIncome;

	@NotBlank
	String idNumber;
	@Nullable()
	String address;
	@Nullable()
	String employmentStatus;
	@Nullable()
	String kraPin;

	@JsonIgnore
	public Gender getGenderVerbal() {
		switch (gender) {
		case "FEMALE": {
			return Gender.FEMALE;
		}
		case "MALE": {
			return Gender.MALE;

		}
		}
		return Gender.MALE;
	}

	@JsonIgnore
	public int getIdTypeVerbal() {
		switch (this.idType) {
		case "KENYA_ID": {
			return 101;
		}
		case "ALIEN_KENYA_ID": {
			return 102;

		}
		case "PASSPORT": {
			return 103;

		}
		}
		return 101;
	}

	@JsonIgnore
	public IdType getIdTypeEnum() {
		switch (this.idType) {
		case "KENYA_ID": {
			return IdType.KENYA_ID;
		}
		case "ALIEN_KENYA_ID": {
			return IdType.ALIEN_KENYA_ID;

		}
		case "PASSPORT": {
			return IdType.PASSPORT;

		}
		}
		return IdType.KENYA_ID;
	}

	@JsonIgnore
	public Date parseBithDay() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);

		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime currentDateTime = LocalDateTime.now();
			return dateFormat.parse(birthday != null ? birthday : currentDateTime.format(formatter).toString());
		} catch (ParseException e) {
			return new Date();
		} catch (NullPointerException e) {
			return new Date();

		}
	}

	@JsonIgnore
	public MonthlyIncome monthlyIncomeType() {
		switch (this.monthlyIncome) {
		case "LESS_THAN_FIVE_THOUSAND": {
			return MonthlyIncome.LESS_THAN_FIVE_THOUSAND;
		}
		case "FIVE_THOUSAND_TO_TEN_THOUSAND": {
			return MonthlyIncome.FIVE_THOUSAND_TO_TEN_THOUSAND;

		}
		case "TEN_THOUSAND_TO_FIFTEEN_THOUSAND": {
			return MonthlyIncome.TEN_THOUSAND_TO_FIFTEEN_THOUSAND;

		}
		case "FIFTEEN_THOUSAND_TO_TWENTY_FIVE_THOUSAND": {
			return MonthlyIncome.FIFTEEN_THOUSAND_TO_TWENTY_FIVE_THOUSAND;

		}
		case "TWENTY_FIVE_THOUSAND_AND_ABOVE": {
			return MonthlyIncome.TWENTY_FIVE_THOUSAND_AND_ABOVE;

		}

		}
		return MonthlyIncome.LESS_THAN_FIVE_THOUSAND;
	}

	@JsonIgnore
	public EmploymentStatus getEmploymentStatusType() {
		switch (this.employmentStatus) {
		case "STUDENT": {
			return EmploymentStatus.STUDENT;
		}
		case "EMPLOYEE": {
			return EmploymentStatus.EMPLOYEE;
		}
		case "SELF_EMPLOYEE": {
			return EmploymentStatus.SELF_EMPLOYEE;
		}
		case "UNEMPLOYED": {
			return EmploymentStatus.UNEMPLOYED;
		}
		case "EMPLOYER": {
			return EmploymentStatus.EMPLOYER;
		}
		}
		// TODO Auto-generated method stub
		return EmploymentStatus.UNEMPLOYED;
	}

	@JsonIgnore
	public String getSerchablePhone() {
		var phone_length = mobile.length();
		if (phone_length > 9) {
			return mobile.substring(phone_length - 9);
		}
		return this.mobile;
	}
}
