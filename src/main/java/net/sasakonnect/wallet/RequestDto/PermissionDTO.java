package net.sasakonnect.wallet.RequestDto;

import jakarta.validation.constraints.NotNull;

public class PermissionDTO {
	@NotNull(message="Permission name cannot be null")
   public 	String name;
	
	@NotNull(message="Permission description cannot be null")
	public String description;
}