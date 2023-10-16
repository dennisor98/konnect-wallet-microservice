package net.sasakonnect.wallet.tools;

import org.springframework.web.reactive.function.client.WebClient;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OpenFireClient {

	WebClient webClient;

}
