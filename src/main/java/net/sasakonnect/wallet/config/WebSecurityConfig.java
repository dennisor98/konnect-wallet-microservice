package net.sasakonnect.wallet.config;

import java.util.Arrays;

import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import net.sasakonnect.wallet.services.UserService;

@Configuration
@EnableMethodSecurity
@Slf4j
@EnableAspectJAutoProxy

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
		configuration.setAllowedOrigins(
				Arrays.asList("https://gw.sasakonnect.net", "http://localhost:4200", "https://wallet.sasakonnect.net"));

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

				.requestMatchers("/user/userLogin", "/user/confirmOtp", "/konnect/callBack", "/user/refresh/token",
						"/wallet/getOnboardingStatusById", "/sdk/transaction/{id}", "user/corporateLogin",
						"/sdk/openId")

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

	@Bean
	static MethodSecurityExpressionHandler expressionHandler(UserService userService) {
		var expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator(userService));
		return expressionHandler;
	}

	@Bean
	OpenAPI openApiInformation() throws Exception {
		Server localServer = new Server().url("http://localhost:8081/konnect-wallet")
				.description("Localhost Server URL");
		Server gatewayServer = new Server().url("https://gw.sasakonnect.net/konnect-wallet")
				.description("Gateway Server Server URL(Dev)");
		Server nginxServer = new Server().url("https://wallet.sasakonnect.net/konnect-wallet")
				.description("Production env");

		Server ngrokServer = new Server().url("https://328c-105-29-165-232.ngrok-free.app").description("Ngrok env");

		Contact contact = new Contact().email("devops@gmail.com").name("DevOps");
		Info info = new Info().contact(contact).description("Wallet Based implementation Through Choice Bank")
				.summary("Easy way to Buy").title("Konnect Wallet").version("V1.0.0")
				.license(new License().name("Apache 2.0").url("http://springdoc.org"));
		// Define custom header here
		Components components = new Components();
		components.addHeaders("X-Custom-Header",
				new Header().description("Description of custom header").schema(new StringSchema()));
		components.addSecuritySchemes("Bearer Authentication", createAPIKeyScheme());

		Object example_token = "xy......bearertoken";
		var openApi = new OpenAPI();
		openApi.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication")).components(components

		);

		switch (profileActive) {
		case "dev": {
			openApi.info(info).addServersItem(gatewayServer).addServersItem(nginxServer).addServersItem(localServer)
					.addServersItem(ngrokServer);
			break;

		}
		default: {
			openApi.info(info).addServersItem(nginxServer).addServersItem(gatewayServer).addServersItem(localServer)
					.addServersItem(ngrokServer);
			break;

		}

		}

		return openApi;
	}

	private SecurityScheme createAPIKeyScheme() {
		return new SecurityScheme().type(SecurityScheme.Type.HTTP).bearerFormat("JWT").scheme("bearer");
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

}
