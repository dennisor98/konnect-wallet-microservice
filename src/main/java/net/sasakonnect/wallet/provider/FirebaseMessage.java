package net.sasakonnect.wallet.provider;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseMessage implements Serializable {
	private static final long serialVersionUID = -4088375623506759396L;
	String token;
	String message;
}
