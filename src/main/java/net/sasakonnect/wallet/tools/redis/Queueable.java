package net.sasakonnect.wallet.tools.redis;

import java.io.Serializable;

public abstract class Queueable implements Serializable {
	private static final long serialVersionUID = 1L;
	public Object params;

	public abstract void executeJob(Queueable job);

}
