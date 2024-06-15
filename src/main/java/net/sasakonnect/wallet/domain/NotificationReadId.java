package net.sasakonnect.wallet.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

public class NotificationReadId implements Serializable {
	private static final long serialVersionUID = 1L;
    private Long userId;
    private Long messageId;
}
