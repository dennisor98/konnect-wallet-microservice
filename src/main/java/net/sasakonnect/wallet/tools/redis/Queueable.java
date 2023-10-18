package net.sasakonnect.wallet.tools.redis;

import java.io.Serializable;

public abstract class Queueable<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	public T params;

	public abstract void executeJob(Queueable<T> job);

}
