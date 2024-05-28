package net.sasakonnect.wallet.serde;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import net.sasakonnect.wallet.enums.sme.BusinessIndustry;

@Converter(autoApply = true)
public class BusinessIndustryConverter implements AttributeConverter<BusinessIndustry, Integer> {
	@Override
	public Integer convertToDatabaseColumn(BusinessIndustry businessIndustry) {
		return businessIndustry.getValue();
	}

	@Override
	public BusinessIndustry convertToEntityAttribute(Integer value) {
		return BusinessIndustry.fromValue(value);
	}
}
