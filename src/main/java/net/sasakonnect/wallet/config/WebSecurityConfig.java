package net.sasakonnect.wallet.config;

import java.util.Arrays;

import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.v3.oas.models.parameters.HeaderParameter;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.aspects.CustomPermissionEvaluator;
import net.sasakonnect.wallet.services.UserService;
import net.sasakonnect.wallet.services.sme.SmeUserService;

@Configuration
@EnableMethodSecurity
@Slf4j
@EnableAspectJAutoProxy
@EnableAsync

public class WebSecurityConfig {

	UserService userService;
	JwtAuthenticationFilter jwtAuthenticationFilter;
	@Value("${spring.profiles.active}")
	String profileActive;

	WebSecurityConfig(UserService userService, JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.userService = userService;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Value("${FIREBASE_ADMIN_CONFIG_FILE}")
	private String firebaseconfig_file;
	@Autowired
	private ResourceLoader resourceLoader;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CorsConfigurationSource corsFilter() {
		CorsConfiguration configuration = new CorsConfiguration();

		// Specify the allowed origins (replace "*" with your specific origin)
		configuration.setAllowedOrigins(Arrays.asList("https://gw.sasakonnect.net", "http://localhost:4200",
				"https://wallet.sasakonnect.net", "https://b729-105-27-226-165.ngrok-free.app"));

		// Specify the allowed HTTP methods (e.g., GET, POST, PUT, DELETE)
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		// Specify the allowed headers (e.g., Content-Type, Authorization)
		configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));

		// Allow credentials (e.g., cookies)
		configuration.setAllowCredentials(true);

		// Set max age (in seconds) for preflight requests
		configuration.setMaxAge(3600L); // 1 hour

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;

	}

	@Bean
	@Order(1)
	SecurityFilterChain auth0FilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests((authz) -> authz.requestMatchers("api-docs/**", // Swagger API documentation
				"/swagger-ui/**", // Swagger UI web interface
				"/swagger-resources/**", // Swagger resources like JS and CSS
				"/webjars/**").permitAll()

				.requestMatchers("/user/userLogin", "/user/confirmOtp", "/user/admin/confirmOtp", "/konnect/callBack","/user/account/confirm",
						"/user/refresh/token", "/sme/login", "/sme/verifyOtp", "/lark/callback","/user/login/resendOtp","/sdk/mobile/confirm",
//						"/user/admin/password/set",
						"/wallet/getOnboardingStatusById", "/sdk/transaction/{id}", "user/corporateLogin",
						"/sdk/openId", "/sdk/customer", "/sdk/customers", "/sdk/customer/stkpush","/sdk/transaction/history","/sdk/app/authorities")

				.permitAll().requestMatchers("/wallet").permitAll().requestMatchers(HttpMethod.OPTIONS, "/**")
				.permitAll() // Permit OPTIONS requests

				.anyRequest().authenticated()

		// require authentication for any endpoint that's not
		// whitelisted

		);
		http.httpBasic(basic -> basic.disable());
		http.csrf(csrf -> csrf.disable());
		http.cors(cors -> cors.disable());
		http.headers(headers -> headers.disable());

		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	

		return http.build();

	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

//	@Bean
//	static MethodSecurityExpressionHandler expressionHandler(UserService userService) {
//		var expressionHandler = new DefaultMethodSecurityExpressionHandler();
//		expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator(userService));
//		return expressionHandler;
//	}

	

    @Bean
    @Primary
     MethodSecurityExpressionHandler expressionHandler1(UserService userService) {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator(userService));
        return expressionHandler;
    }

    @Bean
    @Qualifier("smeExpressionHandler")
     MethodSecurityExpressionHandler smeExpressionHandler(SmeUserService smeUserService) {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        return expressionHandler;
    }
    
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE").allowedOrigins("*")
						.allowedHeaders("*");
			}
		};
	}

	@Bean
	GlobalOpenApiCustomizer globalOpenApiConstomizer() {
		Object example_token = "bearertoken";
		return openApi -> {
			openApi.getPaths().forEach((path, pathItem) -> {
				pathItem.readOperations().forEach(operation -> {
					if ("/user/refresh/token".equals(path)) {
						operation.addParametersItem(
								new HeaderParameter().name(KonnectHeader.REFRESH_TOKEN_HEADER.toString())
										.allowEmptyValue(false).example("ej....").required(false));
						return;
					}
					operation
							.addParametersItem(new HeaderParameter().name(KonnectHeader.X_TRANSACTION_HEADER.toString())
									.allowEmptyValue(false).example(example_token).required(false));
				});
			});
		};

	}

	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		// Enable pretty-printing for JSON output
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		return objectMapper;
	}

	@Bean
	FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
		FilterRegistrationBean<RequestContextFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestContextFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

}
