package net.sasakonnect.wallet.RequestDto.sme;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.sasakonnect.wallet.enums.Gender;
import net.sasakonnect.wallet.enums.sme.BusinessIndustry;


@Data
public class SolePDto {
  @NotNull(message="onboardingRequestId is missing")
  String  onboardingRequestId;
  
  @NotNull(message="businessName is missing")
  String  businessName;
  
  @NotNull(message="businessCertNumber is missing")
  String  businessCerNum;
  
  @NotNull(message="firstName is missing")
  String  firstName;
  
  @NotNull(message="middleName is missing")
  String  middleName;
  
  @NotNull(message="lastName is missing")
  String  lastName;
  
  @NotNull(message="gender is missing")
  Gender  gender;
  
  @NotNull(message="birthday is missing")
  String  birthday;
  
  @NotNull(message="idNumber is missing")
  String  idNumber;
  
  @NotNull(message="kraPin is missing")
  String  kraPin;
  
  @NotNull(message="businessAddress is missing")
  String  businessAddress;
  
  @NotNull(message="kinFullName is missing")
  String  kinFullName;
  
  @NotNull(message="kinRelationship is missing")
  String  kinRelationship;
  
  @NotNull(message="kinCountryCode is missing")
  String  kinCountryCode;
  
  @NotNull(message="kinMobile is missing")
  String  kinMobile;
  
  @NotNull(message="businessIndustry is missing") 
 BusinessIndustry businessIndustry;
  
  
}
