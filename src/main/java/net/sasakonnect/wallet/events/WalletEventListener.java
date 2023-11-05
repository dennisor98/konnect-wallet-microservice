package net.sasakonnect.wallet.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import net.sasakonnect.wallet.config.websocket.SockectManager;
import net.sasakonnect.wallet.workers.MerchantWoker;

@Component

public class WalletEventListener {
	@Autowired
	SockectManager socketManager;
	@Autowired
	MerchantWoker merchantWorker;

	@EventListener
	public void handleCustomEvent(BaseEvent event) {
		if (event instanceof MessageEvent) {
			socketManager.sendMessage(((MessageEvent) event).messagePayload);
		} else if (event instanceof TransactionEvent) {
			TransactionEvent transactionEvent = (TransactionEvent) event;

			merchantWorker.notifyMerchant(transactionEvent.getTransaction());// Cast to TransactionEvent
			if (transactionEvent.createNotificationPayLoad().isPresent()) {
				socketManager.sendMessage(transactionEvent.createNotificationPayLoad().get());

			}
		}
		System.out.println("Received custom event: " + event.getClass());
		// Do something with the event
	}
}
