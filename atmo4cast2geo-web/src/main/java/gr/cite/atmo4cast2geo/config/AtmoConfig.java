package gr.cite.atmo4cast2geo.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AtmoProperties.class)
public class AtmoConfig {
}
