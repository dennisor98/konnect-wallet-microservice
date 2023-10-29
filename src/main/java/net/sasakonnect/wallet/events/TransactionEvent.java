package net.sasakonnect.wallet.events;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;

import lombok.Builder;
import lombok.Data;
import net.sasakonnect.wallet.config.websocket.defination.MessagePayload;
import net.sasakonnect.wallet.domain.Transaction;
import net.sasakonnect.wallet.services.UserService;

@Builder
@Data
public class TransactionEvent implements BaseEvent {
	Transaction transaction;
	@Autowired
	UserService userService;

	public Optional<MessagePayload> createNotificationPayLoad() {
		ObjectNode objectNode = objectMapper.convertValue(this.transaction, ObjectNode.class);
		var user = this.userService.findUserByAccountd(transaction.getAccountId());
		if (user.isPresent()) {
			var messagepayLoad = new MessagePayload();
			messagepayLoad.buildFromJson(objectNode);
			messagepayLoad.setReceiverId(user.get().getId());
			return Optional.of(messagepayLoad);

		}
		return Optional.absent();

	}
}
