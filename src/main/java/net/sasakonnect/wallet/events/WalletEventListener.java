package net.sasakonnect.wallet.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import net.sasakonnect.wallet.config.websocket.SockectManager;

@Component

public class WalletEventListener {
	@Autowired
	SockectManager socketManager;

	@EventListener
	public void handleCustomEvent(BaseEvent event) {
		if (event instanceof MessageEvent) {
			socketManager.sendMessage(((MessageEvent) event).messagePayload);
		}
		System.out.println("Received custom event: " + event.getClass());
		// Do something with the event
	}
}
