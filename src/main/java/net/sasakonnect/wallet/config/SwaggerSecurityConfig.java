package net.sasakonnect.wallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@EnableWebSecurity
public class SwaggerSecurityConfig {
	private final PasswordEncoder passwordEncoder;
	@Value("${spring.profiles.active}")
	String profileActive;

	public SwaggerSecurityConfig(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

//	@Bean
//	UserDetailsService userDetailsService() {
//		UserDetails user = User.builder().username("user").password(passwordEncoder.encode("password")).roles("USER")
//				.build();
//		return new InMemoryUserDetailsManager(user);
//	}

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

}
