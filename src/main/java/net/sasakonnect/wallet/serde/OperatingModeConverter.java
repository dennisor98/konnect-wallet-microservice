package net.sasakonnect.wallet.serde;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import net.sasakonnect.wallet.enums.sme.OperatingMode;

//import jarkata.persistence.AttributeConverter;
//import javax.persistence.Converter;

@Converter(autoApply = true)
public class OperatingModeConverter implements AttributeConverter<OperatingMode, Integer> {
	@Override
	public Integer convertToDatabaseColumn(OperatingMode operatingMode) {
		if(operatingMode == null) {
			return null;
		}
		return operatingMode.getValue();
	}

	@Override
	public OperatingMode convertToEntityAttribute(Integer value) {
		if(value == null ) {
			return null;
		}
		return OperatingMode.fromValue(value);
	}
}
