package net.sasakonnect.wallet.aspects;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.annotations.RateLimit;
import net.sasakonnect.wallet.tools.security.RateLimiter;

@Aspect
@Component
@Slf4j
public class RateLimitAspect {
	 @Autowired
	    private RateLimiter rateLimiter;

	    @Around("@annotation(rateLimit)")
	    public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
	        String apiKey = getApiKey();
	        String methodName = joinPoint.getSignature().toShortString() + "_" + rateLimit.value() + "_" + apiKey;
	        long rateLimitDuration = rateLimit.value();
            
	       
	            if (rateLimiter.isBlocked(methodName)) {
	            	 log.info("method blocked" +methodName+ rateLimitDuration);

	                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded");
	            }else {
	            	 log.info("track this method" +methodName+ rateLimitDuration);
	            	rateLimiter.track(methodName, rateLimitDuration);
	            }
	           
	        

	        Object result = joinPoint.proceed();

	        // Reset request counts after the rate limit duration

	        return result;
	    }

		private String getApiKey() {
	        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	       return attributes.getRequest().getHeader("secret-key");

			
		}

}
