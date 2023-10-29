package net.sasakonnect.wallet.events;

import lombok.Builder;
import lombok.Data;
import net.sasakonnect.wallet.domain.User;

@Builder
@Data
public class OnboardingEvent implements BaseEvent {
	User user;
}
