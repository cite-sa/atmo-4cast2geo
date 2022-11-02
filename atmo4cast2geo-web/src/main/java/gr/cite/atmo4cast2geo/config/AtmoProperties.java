package gr.cite.atmo4cast2geo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "paths")
@ConstructorBinding
public class AtmoProperties {
    private final String tempPath;

    public AtmoProperties(String tempPath) {
        this.tempPath = tempPath;
    }

    public String getTempPath() {
        return tempPath;
    }
}
