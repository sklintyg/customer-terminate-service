package se.inera.intyg.cts.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Profile("dev")
@Configuration
public class OpenApiConfig {

    public OpenApiConfig() {
    }

    @Bean
    public OpenAPI ctsOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("CTS API")
                .description("Customer termination service - CTS")
            );
    }
}
