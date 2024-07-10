package net.sasakonnect.wallet.config.sme;

import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import net.sasakonnect.wallet.annotations.sme.HasSmeAccountPermission;
import net.sasakonnect.wallet.annotations.sme.HasSmePermission;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.Sme;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeUserRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountUserRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeAccountPermissions;
import net.sasakonnect.wallet.services.sme.SmeRoleService;
import net.sasakonnect.wallet.services.sme.SmeUserService;

@Aspect
@Component
public class SmeAccPermissionEvaluatorAspect {
	private final SmeUserService smeUserService;
	private final SmeRoleService smeRoleService;
	public SmeAccPermissionEvaluatorAspect(SmeUserService smeUserService, SmeRoleService smeRoleService) {
        this.smeUserService = smeUserService;
        this.smeRoleService = smeRoleService;
    }


	 @Before("@annotation(hasSmePermissionAnnotation)")
	    public void beforeControllerMethodExecution(JoinPoint joinPoint, HasSmeAccountPermission hasSmePermissionAnnotation) {
		 String permission = hasSmePermissionAnnotation.value();

	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication == null || !authentication.isAuthenticated()) {
	            throw new IllegalStateException("User not authenticated");
	        }

	        User user = (User) authentication.getPrincipal();
	        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
		  
		  String smeId =  this.smeUserService.jwtService.extractUserSmeId(request.getHeader("Authorization").split("Bearer ")[1]);
		  Optional<Sme> sme = this.smeUserService.findSmeById(smeId);
	        Optional<SmeAccountUserRole> roleOptional = smeUserService.getSmeAccUserRoleByUser(user,sme.get());
	        if (roleOptional.isEmpty()) {
	            throw new IllegalStateException("User does not have a role assigned");
	        }

	        SmeAccountUserRole smeUserRole = roleOptional.get();
	        Optional<SmeAccountRole> smeRoleOptional = smeRoleService.findSmeAccRoleById(smeUserRole.getRole().getId());
	        if (smeRoleOptional.isEmpty()) {
	            throw new IllegalStateException("Role not found");
	        }

	        SmeAccountRole smeRole = smeRoleOptional.get();
	        if (!smeUserService.findSmeAccRolePermissionsByRole(smeRole, permission)) {
	            throw new IllegalStateException("User does not have expected permission");
	        }
	        
	    }
}
