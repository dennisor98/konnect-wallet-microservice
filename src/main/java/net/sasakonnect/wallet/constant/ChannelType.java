package net.sasakonnect.wallet.constant;

public enum ChannelType {
    MPESA_TILL("MPESA_TILL"),
    MPESA_PAYBILL("MPESA_PAYBILL"),
    PESA_LINK("PESA_LINK"),
    WALLET("WALLET"),
    MPESA_ACCOUNT("MPESA_ACCOUNT");

    private final String value;

    ChannelType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
