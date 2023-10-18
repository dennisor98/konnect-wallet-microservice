package net.sasakonnect.wallet.provider;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.google.firebase.FirebaseApp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class FirebaseWrapper implements Serializable {
	FirebaseApp firebaseApp;
}
