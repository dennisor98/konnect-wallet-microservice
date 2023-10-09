package net.sasakonnect.wallet.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.sasakonnect.wallet.constant.GlobalPermissionConstants;
import net.sasakonnect.wallet.domain.Permission;
import net.sasakonnect.wallet.services.PermissionService;

@Component
public class ApplicationPermissionLoader implements ApplicationListener<ApplicationReadyEvent> {
	@Autowired
	PermissionService permissionService;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		var permssions = GlobalPermissionConstants.scan();
		permssions.forEach((permmsion, desc) -> {
			var permission = Permission.builder().description(desc).name(permmsion).build();
			this.permissionService.insertPermissionIfNotExistsOrUpdateDescription(permission);
		});
		// Your custom logic here

	}
}