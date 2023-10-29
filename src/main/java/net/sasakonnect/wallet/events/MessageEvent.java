package net.sasakonnect.wallet.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sasakonnect.wallet.config.websocket.defination.MessagePayload;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class MessageEvent implements BaseEvent {
	MessagePayload messagePayload;

}
