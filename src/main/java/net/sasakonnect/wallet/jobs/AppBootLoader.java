package net.sasakonnect.wallet.jobs;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import net.sasakonnect.wallet.constant.GlobalPermissionConstants;
import net.sasakonnect.wallet.domain.Bank;
import net.sasakonnect.wallet.domain.Currency;
import net.sasakonnect.wallet.domain.Permission;
import net.sasakonnect.wallet.domain.Role;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRole;
import net.sasakonnect.wallet.enums.EmploymentStatus;
import net.sasakonnect.wallet.enums.Gender;
import net.sasakonnect.wallet.enums.IdType;
import net.sasakonnect.wallet.enums.MonthlyIncome;
import net.sasakonnect.wallet.repository.BankRepository;
import net.sasakonnect.wallet.repository.CurrencyRepository;
import net.sasakonnect.wallet.services.PermissionService;
import net.sasakonnect.wallet.services.RoleService;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.services.sme.SmeUserService;

@Component
public class AppBootLoader implements ApplicationListener<ApplicationReadyEvent> {
	
	List<Currency> currencyEntities = List.of(new Currency("AFGHANISTAN", "Afghani", "AFN", 2),
			new Currency("ÅLAND ISLANDS", "Euro", "EUR", 2), new Currency("ALBANIA", "Lek", "ALL", 2),
			new Currency("ALGERIA", "Algerian Dinar", "DZD", 2), new Currency("AMERICAN SAMOA", "US Dollar", "USD", 2),
			new Currency("ANDORRA", "Euro", "EUR", 2), new Currency("ANGOLA", "Kwanza", "AOA", 2),
			new Currency("ANGUILLA", "East Caribbean Dollar", "XCD", 2),
			new Currency("ANTARCTICA", "No universal currency", "", 0),
			new Currency("ANTIGUA AND BARBUDA", "East Caribbean Dollar", "XCD", 2),
			new Currency("ARGENTINA", "Argentine Peso", "ARS", 2), new Currency("ARMENIA", "Armenian Dram", "AMD", 2),
			new Currency("ARUBA", "Aruban Florin", "AWG", 2), new Currency("AUSTRALIA", "Australian Dollar", "AUD", 2),
			new Currency("AUSTRIA", "Euro", "EUR", 2), new Currency("AZERBAIJAN", "Azerbaijanian Manat", "AZN", 2),
			new Currency("BAHAMAS", "Bahamian Dollar", "BSD", 2), new Currency("BAHRAIN", "Bahraini Dinar", "BHD", 3),
			new Currency("BANGLADESH", "Taka", "BDT", 2), new Currency("BARBADOS", "Barbados Dollar", "BBD", 2),
			new Currency("BELARUS", "Belarussian Ruble", "BYR", 0), new Currency("BELGIUM", "Euro", "EUR", 2),
			new Currency("BELIZE", "Belize Dollar", "BZD", 2), new Currency("BENIN", "CFA Franc BCEAO", "XOF", 0),
			new Currency("BERMUDA", "Bermudian Dollar", "BMD", 2), new Currency("BHUTAN", "Ngultrum", "BTN", 2),
			new Currency("BHUTAN", "Indian Rupee", "INR", 2),
			new Currency("BOLIVIA, PLURINATIONAL STATE OF", "Boliviano", "BOB", 2),
			new Currency("BOLIVIA, PLURINATIONAL STATE OF", "Mvdol", "BOV", 2),
			new Currency("BONAIRE, SINT EUSTATIUS AND SABA", "US Dollar", "USD", 2),
			new Currency("BOSNIA AND HERZEGOVINA", "Convertible Mark", "BAM", 2),
			new Currency("BOTSWANA", "Pula", "BWP", 2), new Currency("BOUVET ISLAND", "Norwegian Krone", "NOK", 2),
			new Currency("BRAZIL", "Brazilian Real", "BRL", 2),
			new Currency("BRITISH INDIAN OCEAN TERRITORY", "US Dollar", "USD", 2),
			new Currency("BRUNEI DARUSSALAM", "Brunei Dollar", "BND", 2),
			new Currency("BULGARIA", "Bulgarian Lev", "BGN", 2),
			new Currency("BURKINA FASO", "CFA Franc BCEAO", "XOF", 0),
			new Currency("BURUNDI", "Burundi Franc", "BIF", 0), new Currency("CAMBODIA", "Riel", "KHR", 2),
			new Currency("CAMEROON", "CFA Franc BEAC", "XAF", 0), new Currency("CANADA", "Canadian Dollar", "CAD", 2),
			new Currency("CABO VERDE", "Cabo Verde Escudo", "CVE", 2),
			new Currency("CAYMAN ISLANDS", "Cayman Islands Dollar", "KYD", 2),
			new Currency("CENTRAL AFRICAN REPUBLIC", "CFA Franc BEAC", "XAF", 0),
			new Currency("CHAD", "CFA Franc BEAC", "XAF", 0), new Currency("CHILE", "Unidad de Fomento", "CLF", 4),
			new Currency("CHILE", "Chilean Peso", "CLP", 0), new Currency("CHINA", "Yuan Renminbi", "CNY", 2),
			new Currency("CHRISTMAS ISLAND", "Australian Dollar", "AUD", 2),
			new Currency("COCOS (KEELING) ISLANDS", "Australian Dollar", "AUD", 2),
			new Currency("COLOMBIA", "Colombian Peso", "COP", 2),
			new Currency("COLOMBIA", "Unidad de Valor Real", "COU", 2),
			new Currency("COMOROS", "Comoro Franc", "KMF", 0), new Currency("CONGO", "CFA Franc BEAC", "XAF", 0),
			new Currency("CONGO, DEMOCRATIC REPUBLIC OF THE", "Congolese Franc", "CDF", 2),
			new Currency("COOK ISLANDS", "New Zealand Dollar", "NZD", 2),
			new Currency("COSTA RICA", "Costa Rican Colon", "CRC", 2),
			new Currency("CÔTE D'IVOIRE", "CFA Franc BCEAO", "XOF", 0),
			new Currency("CROATIA", "Croatian Kuna", "HRK", 2), new Currency("CUBA", "Peso Convertible", "CUC", 2),
			new Currency("CUBA", "Cuban Peso", "CUP", 2),
			new Currency("CURAÇAO", "Netherlands Antillean Guilder", "ANG", 2),
			new Currency("CYPRUS", "Euro", "EUR", 2), new Currency("CZECH REPUBLIC", "Czech Koruna", "CZK", 2),
			new Currency("DENMARK", "Danish Krone", "DKK", 2), new Currency("DJIBOUTI", "Djibouti Franc", "DJF", 0),
			new Currency("DOMINICA", "East Caribbean Dollar", "XCD", 2),
			new Currency("DOMINICAN REPUBLIC", "Dominican Peso", "DOP", 2),
			new Currency("ECUADOR", "US Dollar", "USD", 2), new Currency("EGYPT", "Egyptian Pound", "EGP", 2),
			new Currency("EL SALVADOR", "El Salvador Colon", "SVC", 2),
			new Currency("EL SALVADOR", "US Dollar", "USD", 2),
			new Currency("EQUATORIAL GUINEA", "CFA Franc BEAC", "XAF", 0), new Currency("ERITREA", "Nakfa", "ERN", 2),
			new Currency("ESTONIA", "Euro", "EUR", 2), new Currency("ETHIOPIA", "Ethiopian Birr", "ETB", 2),
			new Currency("EUROPEAN UNION", "Euro", "EUR", 2),
			new Currency("FALKLAND ISLANDS (MALVINAS)", "Falkland Islands Pound", "FKP", 2),
			new Currency("FAROE ISLANDS", "Danish Krone", "DKK", 2), new Currency("FIJI", "Fiji Dollar", "FJD", 2),
			new Currency("FINLAND", "Euro", "EUR", 2), new Currency("FRANCE", "Euro", "EUR", 2),
			new Currency("FRENCH GUIANA", "Euro", "EUR", 2), new Currency("FRENCH POLYNESIA", "CFP Franc", "XPF", 0),
			new Currency("FRENCH SOUTHERN TERRITORIES", "Euro", "EUR", 2),
			new Currency("GABON", "CFA Franc BEAC", "XAF", 0), new Currency("GAMBIA", "Dalasi", "GMD", 2),
			new Currency("GEORGIA", "Lari", "GEL", 2), new Currency("GERMANY", "Euro", "EUR", 2),
			new Currency("GHANA", "Ghana Cedi", "GHS", 2), new Currency("GIBRALTAR", "Gibraltar Pound", "GIP", 2),
			new Currency("GREECE", "Euro", "EUR", 2), new Currency("GREENLAND", "Danish Krone", "DKK", 2),
			new Currency("GRENADA", "East Caribbean Dollar", "XCD", 2), new Currency("GUADELOUPE", "Euro", "EUR", 2),
			new Currency("GUAM", "US Dollar", "USD", 2), new Currency("GUATEMALA", "Quetzal", "GTQ", 2),
			new Currency("GUERNSEY", "Pound Sterling", "GBP", 2), new Currency("GUINEA", "Guinea Franc", "GNF", 0),
			new Currency("GUINEA-BISSAU", "CFA Franc BCEAO", "XOF", 0),
			new Currency("GUYANA", "Guyana Dollar", "GYD", 2), new Currency("HAITI", "Gourde", "HTG", 2),
			new Currency("HAITI", "US Dollar", "USD", 2),
			new Currency("HEARD ISLAND AND McDONALD ISLANDS", "Australian Dollar", "AUD", 2),
			new Currency("HOLY SEE (VATICAN CITY STATE)", "Euro", "EUR", 2),
			new Currency("HONDURAS", "Lempira", "HNL", 2), new Currency("HONG KONG", "Hong Kong Dollar", "HKD", 2),
			new Currency("HUNGARY", "Forint", "HUF", 2), new Currency("ICELAND", "Iceland Krona", "ISK", 0),
			new Currency("INDIA", "Indian Rupee", "INR", 2), new Currency("INDONESIA", "Rupiah", "IDR", 2),
			new Currency("INTERNATIONAL MONETARY FUND (IMF)", "SDR (Special Drawing Right)", "XDR", 0),
			new Currency("IRAN, ISLAMIC REPUBLIC OF", "Iranian Rial", "IRR", 2),
			new Currency("IRAQ", "Iraqi Dinar", "IQD", 3), new Currency("IRELAND", "Euro", "EUR", 2),
			new Currency("ISLE OF MAN", "Pound Sterling", "GBP", 2),
			new Currency("ISRAEL", "New Israeli Sheqel", "ILS", 2), new Currency("ITALY", "Euro", "EUR", 2),
			new Currency("JAMAICA", "Jamaican Dollar", "JMD", 2), new Currency("JAPAN", "Yen", "JPY", 0),
			new Currency("JERSEY", "Pound Sterling", "GBP", 2), new Currency("JORDAN", "Jordanian Dinar", "JOD", 3),
			new Currency("KAZAKHSTAN", "Tenge", "KZT", 2), new Currency("KENYA", "Kenyan Shilling", "KES", 2),
			new Currency("KIRIBATI", "Australian Dollar", "AUD", 2),
			new Currency("KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF", "North Korean Won", "KPW", 2),
			new Currency("KOREA, REPUBLIC OF", "Won", "KRW", 0), new Currency("KOSOVO", "Euro", "EUR", 2),
			new Currency("KUWAIT", "Kuwaiti Dinar", "KWD", 3), new Currency("KYRGYZSTAN", "Som", "KGS", 2),
			new Currency("LAO PEOPLE'S DEMOCRATIC REPUBLIC", "Lao Kip", "LAK", 2),
			new Currency("LATVIA", "Euro", "EUR", 2), new Currency("LEBANON", "Lebanese Pound", "LBP", 2),
			new Currency("LESOTHO", "Loti", "LSL", 2), new Currency("LESOTHO", "Rand", "ZAR", 2),
			new Currency("LIBERIA", "Liberian Dollar", "LRD", 2), new Currency("LIBYA", "Libyan Dinar", "LYD", 3),
			new Currency("LIECHTENSTEIN", "Swiss Franc", "CHF", 2), new Currency("LITHUANIA", "Euro", "EUR", 2),
			new Currency("LUXEMBOURG", "Euro", "EUR", 2), new Currency("MACAO", "Pataca", "MOP", 2),
			new Currency("MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF", "Denar", "MKD", 2),
			new Currency("MADAGASCAR", "Malagasy Ariary", "MGA", 2), new Currency("MALAWI", "Kwacha", "MWK", 2),
			new Currency("MALAYSIA", "Malaysian Ringgit", "MYR", 2), new Currency("MALDIVES", "Rufiyaa", "MVR", 2),
			new Currency("MALI", "CFA Franc BCEAO", "XOF", 0), new Currency("MALTA", "Euro", "EUR", 2),
			new Currency("MARSHALL ISLANDS", "US Dollar", "USD", 2), new Currency("MARTINIQUE", "Euro", "EUR", 2),
			new Currency("MAURITANIA", "Ouguiya", "MRO", 2), new Currency("MAURITIUS", "Mauritius Rupee", "MUR", 2),
			new Currency("MAYOTTE", "Euro", "EUR", 2), new Currency("MEXICO", "Mexican Peso", "MXN", 2),
			new Currency("MICRONESIA, FEDERATED STATES OF", "US Dollar", "USD", 2),
			new Currency("MOLDOVA, REPUBLIC OF", "Moldovan Leu", "MDL", 2), new Currency("MONACO", "Euro", "EUR", 2),
			new Currency("MONGOLIA", "Tugrik", "MNT", 2), new Currency("MONTENEGRO", "Euro", "EUR", 2),
			new Currency("MONTSERRAT", "East Caribbean Dollar", "XCD", 2),
			new Currency("MOROCCO", "Moroccan Dirham", "MAD", 2),
			new Currency("MOZAMBIQUE", "Mozambique Metical", "MZN", 2), new Currency("MYANMAR", "Kyat", "MMK", 2),
			new Currency("NAMIBIA", "Namibia Dollar", "NAD", 2), new Currency("NAMIBIA", "Rand", "ZAR", 2),
			new Currency("NAURU", "Australian Dollar", "AUD", 2), new Currency("NEPAL", "Nepalese Rupee", "NPR", 2),
			new Currency("NETHERLANDS", "Euro", "EUR", 2), new Currency("NEW CALEDONIA", "CFP Franc", "XPF", 0),
			new Currency("NEW ZEALAND", "New Zealand Dollar", "NZD", 2),
			new Currency("NICARAGUA", "Cordoba Oro", "NIO", 2), new Currency("NIGER", "CFA Franc BCEAO", "XOF", 0),
			new Currency("NIGERIA", "Naira", "NGN", 2), new Currency("NIUE", "New Zealand Dollar", "NZD", 2),
			new Currency("NORFOLK ISLAND", "Australian Dollar", "AUD", 2),
			new Currency("NORTHERN MARIANA ISLANDS", "US Dollar", "USD", 2),
			new Currency("NORWAY", "Norwegian Krone", "NOK", 2), new Currency("OMAN", "Rial Omani", "OMR", 3),
			new Currency("PAKISTAN", "Pakistan Rupee", "PKR", 2), new Currency("PALAU", "US Dollar", "USD", 2),
			new Currency("PALESTINE, STATE OF", "No universal currency", "", 0),
			new Currency("PANAMA", "Balboa", "PAB", 2), new Currency("PANAMA", "US Dollar", "USD", 2),
			new Currency("PAPUA NEW GUINEA", "Kina", "PGK", 2), new Currency("PARAGUAY", "Guarani", "PYG", 0),
			new Currency("PERU", "Nuevo Sol", "PEN", 2), new Currency("PHILIPPINES", "Philippine Peso", "PHP", 2),
			new Currency("PITCAIRN", "New Zealand Dollar", "NZD", 2), new Currency("POLAND", "Zloty", "PLN", 2),
			new Currency("PORTUGAL", "Euro", "EUR", 2), new Currency("PUERTO RICO", "US Dollar", "USD", 2),
			new Currency("QATAR", "Qatari Rial", "QAR", 2), new Currency("RÉUNION", "Euro", "EUR", 2),
			new Currency("ROMANIA", "Romanian Leu", "RON", 2),
			new Currency("RUSSIAN FEDERATION", "Russian Ruble", "RUB", 2),
			new Currency("RWANDA", "Rwanda Franc", "RWF", 0), new Currency("SAINT BARTHÉLEMY", "Euro", "EUR", 2),
			new Currency("SAINT HELENA, ASCENSION AND TRISTAN DA CUNHA", "Saint Helena Pound", "SHP", 2),
			new Currency("SAINT KITTS AND NEVIS", "East Caribbean Dollar", "XCD", 2),
			new Currency("SAINT LUCIA", "East Caribbean Dollar", "XCD", 2),
			new Currency("SAINT MARTIN (FRENCH PART)", "Euro", "EUR", 2),
			new Currency("SAINT PIERRE AND MIQUELON", "Euro", "EUR", 2),
			new Currency("SAINT VINCENT AND THE GRENADINES", "East Caribbean Dollar", "XCD", 2),
			new Currency("SAMOA", "Tala", "WST", 2), new Currency("SAN MARINO", "Euro", "EUR", 2),
			new Currency("SAO TOME AND PRINCIPE", "Dobra", "STN", 2),
			new Currency("SAUDI ARABIA", "Saudi Riyal", "SAR", 2), new Currency("SENEGAL", "CFA Franc BCEAO", "XOF", 0),
			new Currency("SERBIA", "Serbian Dinar", "RSD", 2), new Currency("SEYCHELLES", "Seychelles Rupee", "SCR", 2),
			new Currency("SIERRA LEONE", "Leone", "SLL", 2), new Currency("SINGAPORE", "Singapore Dollar", "SGD", 2),
			new Currency("SINT MAARTEN (DUTCH PART)", "Netherlands Antillean Guilder", "ANG", 2),
			new Currency("SLOVAKIA", "Euro", "EUR", 2), new Currency("SLOVENIA", "Euro", "EUR", 2),
			new Currency("SOLOMON ISLANDS", "Solomon Islands Dollar", "SBD", 2),
			new Currency("SOMALIA", "Somali Shilling", "SOS", 2), new Currency("SOUTH AFRICA", "Rand", "ZAR", 2),
			new Currency("SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS", "No universal currency", "", 0),
			new Currency("SOUTH SUDAN", "South Sudanese Pound", "SSP", 2), new Currency("SPAIN", "Euro", "EUR", 2),
			new Currency("SRI LANKA", "Sri Lanka Rupee", "LKR", 2), new Currency("SUDAN", "Sudanese Pound", "SDG", 2),
			new Currency("SURINAME", "Surinam Dollar", "SRD", 2),
			new Currency("SVALBARD AND JAN MAYEN", "Norwegian Krone", "NOK", 2),
			new Currency("SWAZILAND", "Lilangeni", "SZL", 2), new Currency("SWAZILAND", "Rand", "ZAR", 2),
			new Currency("SWEDEN", "Swedish Krona", "SEK", 2), new Currency("SWITZERLAND", "Swiss Franc", "CHF", 2),
			new Currency("SYRIAN ARAB REPUBLIC", "Syrian Pound", "SYP", 2),
			new Currency("TAIWAN, PROVINCE OF CHINA", "New Taiwan Dollar", "TWD", 2),
			new Currency("TAJIKISTAN", "Somoni", "TJS", 2),
			new Currency("TANZANIA, UNITED REPUBLIC OF", "Tanzanian Shilling", "TZS", 2),
			new Currency("THAILAND", "Baht", "THB", 2), new Currency("TIMOR-LESTE", "US Dollar", "USD", 2),
			new Currency("TOGO", "CFA Franc BCEAO", "XOF", 0), new Currency("TOKELAU", "New Zealand Dollar", "NZD", 2),
			new Currency("TONGA", "Pa'anga", "TOP", 2),
			new Currency("TRINIDAD AND TOBAGO", "Trinidad and Tobago Dollar", "TTD", 2),
			new Currency("TUNISIA", "Tunisian Dinar", "TND", 3), new Currency("TURKEY", "Turkish Lira", "TRY", 2),
			new Currency("TURKMENISTAN", "Turkmenistan New Manat", "TMT", 2),
			new Currency("TURKS AND CAICOS ISLANDS", "US Dollar", "USD", 2),
			new Currency("TUVALU", "Australian Dollar", "AUD", 2), new Currency("UGANDA", "Uganda Shilling", "UGX", 0),
			new Currency("UKRAINE", "Hryvnia", "UAH", 2), new Currency("UNITED ARAB EMIRATES", "UAE Dirham", "AED", 2),
			new Currency("UNITED KINGDOM", "Pound Sterling", "GBP", 2),
			new Currency("UNITED STATES", "US Dollar", "USD", 2),
			new Currency("UNITED STATES MINOR OUTLYING ISLANDS", "US Dollar", "USD", 2),
			new Currency("URUGUAY", "Peso Uruguayo", "UYU", 2),
			new Currency("URUGUAY", "Uruguay Peso en Unidades Indexadas", "UYI", 0),
			new Currency("UZBEKISTAN", "Uzbekistan Sum", "UZS", 2), new Currency("VANUATU", "Vatu", "VUV", 0),
			new Currency("VENEZUELA, BOLIVARIAN REPUBLIC OF", "Bolivar", "VES", 2),
			new Currency("VIET NAM", "Dong", "VND", 0), new Currency("VIRGIN ISLANDS, BRITISH", "US Dollar", "USD", 2),
			new Currency("VIRGIN ISLANDS, U.S.", "US Dollar", "USD", 2),
			new Currency("WALLIS AND FUTUNA", "CFP Franc", "XPF", 0),
			new Currency("WESTERN SAHARA", "Moroccan Dirham", "MAD", 2), new Currency("YEMEN", "Yemeni Rial", "YER", 2),
			new Currency("ZAMBIA", "Zambian Kwacha", "ZMW", 2), new Currency("ZIMBABWE", "Zimbabwe Dollar", "ZWL", 2));

	List<Bank> banks = Arrays.asList(new Bank("CIC0011", "KOA", true, false, "https://example.com/koa_image.jpg"),
			new Bank("CIC0012", "Lenco Bank", true, false, "https://example.com/lenco_bank_image.jpg"),
			new Bank("CIC0013", "My Wage Pay", true, false, "https://example.com/my_wage_pay_image.jpg"),
			new Bank("CIC0014", "Paya Finance", true, false, "https://example.com/paya_finance_image.jpg"),
			new Bank("CIC0016", "Lipa Later", true, false, "https://example.com/lipa_later_image.jpg"),
			new Bank("M-PESA", "M-PESA", true, false, "https://example.com/lipa_later_image.jpg"),
			new Bank("01", "KCB Bank Kenya", true, false, "https://example.com/mpesa_image.jpg"),
			new Bank("02", "Standard Chartered Bank Kenya", true, false,
					"https://example.com/standard_chartered_image.jpg"),
			new Bank("03", "ABSA Bank Kenya", true, false, "https://example.com/absa_image.jpg"),
			new Bank("07", "NCBA Bank Kenya", true, false, "https://example.com/ncba_image.jpg"),
			new Bank("10", "Prime Bank", true, false, "https://example.com/prime_bank_image.jpg"),
			new Bank("11", "Co-operative Bank of Kenya", true, true, "https://example.com/coop_bank_image.jpg"),
			new Bank("12", "National Bank of Kenya", true, false, "https://example.com/national_bank_image.jpg"),
			new Bank("14", "M-Oriental Bank", true, false, "https://example.com/m_oriental_bank_image.jpg"),
			new Bank("16", "Citibank N.A. Kenya", true, false, "https://example.com/citibank_image.jpg"),
			new Bank("18", "Middle East Bank Kenya", true, true, "https://example.com/middle_east_bank_image.jpg"),
			new Bank("19", "Bank of Africa Kenya", true, false, "https://example.com/bank_of_africa_image.jpg"),
			new Bank("23", "Consolidated Bank of Kenya", true, false,
					"https://example.com/consolidated_bank_image.jpg"),
			new Bank("25", "Credit Bank", true, false, "https://example.com/credit_bank_image.jpg"),
			new Bank("31", "Stanbic Bank Kenya", true, false, "https://example.com/stanbic_bank_image.jpg"),
			new Bank("35", "African Banking Corporation", true, false,
					"https://example.com/african_banking_corp_image.jpg"),
			new Bank("43", "ECO Bank", true, false, "https://example.com/eco_bank_image.jpg"),
			new Bank("46", "Choice Microfinance Bank", true, false,
					"https://example.com/choice_microfinance_bank_image.jpg"),
			new Bank("50", "Paramount Universal Bank", true, false,
					"https://example.com/paramount_universal_bank_image.jpg"),
			new Bank("51", "Kingdom Bank", true, true, "https://example.com/kingdom_bank_image.jpg"),
			new Bank("53", "Guaranty Trust Bank", true, false, "https://example.com/guaranty_trust_bank_image.jpg"),
			new Bank("54", "Victoria Commercial Bank", true, false,
					"https://example.com/victoria_commercial_bank_image.jpg"),
			new Bank("55", "Guardian Bank", true, false, "https://example.com/guardian_bank_image.jpg"),
			new Bank("57", "I&M Bank", true, false, "https://example.com/im_bank_image.jpg"),
			new Bank("60", "SBM Bank Kenya", false, true, "https://example.com/sbm_bank_image.jpg"),
			new Bank("61", "Housing Finance Bank", true, false, "https://example.com/housing_finance_bank_image.jpg"),
			new Bank("63", "Diamond Trust Bank", true, true, "https://example.com/diamond_trust_bank_image.jpg"),
			new Bank("65", "Mayfair CIB Bank", true, false, "https://example.com/mayfair_cib_bank_image.jpg"),
			new Bank("66", "Sidian Bank", true, false, "https://example.com/sidian_bank_image.jpg"),
			new Bank("68", "Equity Bank Kenya", true, true, "https://example.com/equity_bank_image.jpg"),
			new Bank("70", "Family Bank", true, false, "https://example.com/family_bank_image.jpg"),
			new Bank("72", "Gulf African Bank", true, false, "https://example.com/gulf_african_bank_image.jpg"),
			new Bank("74", "Premier Bank Kenya Limited", true, false, "https://example.com/premier_bank_image.jpg"),
			new Bank("75", "DIB Bank Kenya", true, false, "https://example.com/dib_bank_image.jpg"),
			new Bank("76", "UBA Kenya Bank", true, false, "https://example.com/uba_bank_image.jpg"),
			new Bank("78", "Kenya Women Microfinance Bank", true, false,
					"https://example.com/kenya_women_microfinance_bank_image.jpg"),
			new Bank("79", "Spire Bank", false, false, "https://example.com/spire_bank_image.jpg"),
			new Bank("80", "Habib Bank A.G Zurich", false, false, "https://example.com/habib_bank_image.jpg"),
			new Bank("81", "Development Bank of Kenya", false, false, "https://example.com/development_bank_image.jpg"),
			new Bank("82", "Bank of India", false, false, "https://example.com/bank_of_india_image.jpg"),
			new Bank("83", "Access Bank", false, false, "https://example.com/access_bank_image.jpg"),
			new Bank("84", "Caritas Microfinance Bank", true, false,
					"https://example.com/caritas_microfinance_bank_image.jpg"),
			new Bank("85", "Unaitas Sacco", true, false, "https://example.com/unaitas_sacco_image.jpg"));

	@Autowired
	PermissionService permissionService;
	@Autowired
	CurrencyRepository currencyRepository;
	@Autowired
	BankRepository bankRepository;
	@Autowired
	UserService userService;
	@Autowired
	RoleService roleService;
    @Autowired
    SmeUserService smeUserService;
	
	@Override
	@Transactional
	public void onApplicationEvent(ApplicationReadyEvent event) {
		long fortyYearsInMilliseconds = 40L * 365 * 24 * 60 * 60 * 1000;

		var permssions = GlobalPermissionConstants.scan();
		permssions.forEach((permmsion, desc) -> {
			var permission = Permission.builder().description(desc).name(permmsion).build();
			this.permissionService.insertPermissionIfNotExistsOrUpdateDescription(permission);
		});
		Optional<User> user = this.userService.findUserByPhoneNumber("7999999999");
		if (user.isEmpty()) {
			var u = User.builder().firstName("AI").lastName("Billfold").middleName("Buddy").address("Zimmerman")
					.birthday(new Date(fortyYearsInMilliseconds)).employmentStatus(EmploymentStatus.OTHERS)
					.countryCode(254).gender(Gender.FEMALE).idNumber("00000000").idType(IdType.KENYA_ID)
					.kraPin("A0000000000").mobile("7999999999").onboardingRequestId("0000000000000000000")
					.monthlyIncome(MonthlyIncome.TWENTY_FIVE_THOUSAND_AND_ABOVE)

					.build();
			user = Optional.of(this.userService.createUser(u));
		}

		// this.currencyRepository.deleteAll();
		// this.currencyRepository.saveAll(currencyEntities);
		this.bankRepository.saveAll(this.banks);
		var role = new Role();
		role.setRoleName("SUPER_ADMIN");
		role.setDescription("Has All permermission in the system");
		role.setUser(user.get());
		var savedRole = this.roleService.insertRole(role);
		var allpermsions = this.permissionService.findAll().stream().map((data) -> data.getId())
				.collect(Collectors.toList());
		this.roleService.insertPermissionsNotAttachedToRole(savedRole, user.get(), allpermsions);
		// Your custom logic here
		
		

	}
	
	
}
