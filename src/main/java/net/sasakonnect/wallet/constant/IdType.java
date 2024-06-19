package net.sasakonnect.wallet.constant;

public enum IdType {
	KENYA_ID("KENYA_ID"), PASSPORT(""), ALIEN_KENYA_ID("");

	IdType(String string) {

		// TODO Auto-generated constructor stub
	}

//	@JsonIgnore
//	public int getIdTypeVerbal() {
//		switch (this) {
//		case "KENYA_ID": {
//			return 101;
//		}
//		case "ALIEN_KENYA_ID": {
//			return 102;
//
//		}
//		case "PASSPORT": {
//			return 103;
//
//		}
//		}
//		return 101;
//	}

//	@JsonIgnore
//	public IdType getIdTypeEnum() {
//		switch (this.idType) {
//		case "KENYA_ID": {
//			return IdType.KENYA_ID;
//		}
//		case "ALIEN_KENYA_ID": {
//			return IdType.ALIEN_KENYA_ID;
//
//		}
//		case "PASSPORT": {
//			return IdType.PASSPORT;
//
//		}
//		}
//		return IdType.KENYA_ID;
//	}

}
