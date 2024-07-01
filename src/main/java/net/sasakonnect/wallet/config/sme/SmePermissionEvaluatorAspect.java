package net.sasakonnect.wallet.config.sme;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import net.sasakonnect.wallet.annotations.sme.HasSmePermission;
import net.sasakonnect.wallet.domain.User;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeRole;
import net.sasakonnect.wallet.domain.sme.authorisation.SmeUserRole;
import net.sasakonnect.wallet.services.sme.SmeRoleService;
import net.sasakonnect.wallet.services.sme.SmeUserService;

import java.util.Optional;

@Aspect
@Component
public class SmePermissionEvaluatorAspect {

    private final SmeUserService smeUserService;
    private final SmeRoleService smeRoleService;

    public SmePermissionEvaluatorAspect(SmeUserService smeUserService, SmeRoleService smeRoleService) {
        this.smeUserService = smeUserService;
        this.smeRoleService = smeRoleService;
    }

    @Before("@annotation(hasSmePermissionAnnotation)")
    public void beforeControllerMethodExecution(JoinPoint joinPoint, HasSmePermission hasSmePermissionAnnotation) {
        String permission = hasSmePermissionAnnotation.value();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        User user = (User) authentication.getPrincipal();
        Optional<SmeUserRole> roleOptional = smeUserService.getSmeUserRoleByUser(user);
        if (roleOptional.isEmpty()) {
            throw new IllegalStateException("User does not have a role assigned");
        }

        SmeUserRole smeUserRole = roleOptional.get();
        Optional<SmeRole> smeRoleOptional = smeRoleService.findRoleById(smeUserRole.getSme_role().getId());
        if (smeRoleOptional.isEmpty()) {
            throw new IllegalStateException("Role not found");
        }

        SmeRole smeRole = smeRoleOptional.get();
        if (!smeUserService.findRolePermissionsByRole(smeRole, permission)) {
            throw new IllegalStateException("User does not have expected permission");
        }
        
    }
}


