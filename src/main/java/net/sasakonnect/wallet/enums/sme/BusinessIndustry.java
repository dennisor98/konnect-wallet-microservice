package net.sasakonnect.wallet.enums.sme;

public enum BusinessIndustry {
	AGRICULTURE_AND_AGRIBUSINESS(1), MANUFACTURING_AND_PROCESSING(2), CONSTRUCTION_AND_ENGINEERING(3),
	RETAIL_AND_WHOLESALE_TRADE(4), INFORMATION_AND_COMMUNICATION_TECHNOLOGY(5), TOURISM_AND_HOSPITALITY(6),
	HEALTH_AND_WELLNESS_SERVICES(7), EDUCATION_AND_TRAINING(8), FINANCIAL_SERVICES(9), PROFESSIONAL_SERVICES(10),
	CREATIVE_INDUSTRIES(11), RENEWABLE_ENERGY_AND_ENVIRONMENTAL_CONSERVATION(12), TRANSPORT_AND_LOGISTICS(13),
	FOOD_AND_BEVERAGE(14), TEXTILES_AND_APPAREL(15), AUTOMOTIVE_AND_ENGINEERING_SERVICES(16),
	BEAUTY_AND_PERSONAL_CARE(17), REAL_ESTATE_AND_PROPERTY_DEVELOPMENT(18), CONSULTING_AND_BUSINESS_SERVICES(19),
	SOCIAL_ENTERPRISES_AND_NGOS(20), OTHERS(21);

	private final int value;

	BusinessIndustry(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static BusinessIndustry fromValue(int value) {
		for (BusinessIndustry industry : BusinessIndustry.values()) {
			if (industry.getValue() == value) {
				return industry;
			}
		}
		throw new IllegalArgumentException("Invalid BusinessIndustry value: " + value);
	}
}