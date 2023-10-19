package net.sasakonnect.wallet.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import lombok.Data;
import net.sasakonnect.wallet.domain.WalletClient;

@Data
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ClientAppsBean {
	WalletClient walletClient;
}
