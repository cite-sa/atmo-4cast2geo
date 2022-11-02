package gr.cite.atmo4cast2geo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "disposer")
@ConstructorBinding
public class DisposerProperties {
    private final String location;
    private final Scheduler scheduling;

    public DisposerProperties(String location, Scheduler scheduling) {
        this.location = location;
        this.scheduling = scheduling;
    }

    public String getLocation() {
        return location;
    }

    public Scheduler getScheduling() {
        return scheduling;
    }

    public static class Scheduler {
        private final Long interval;
        private final TimeUnit unit;

        public Scheduler(Long interval, TimeUnit unit) {
            this.interval = interval;
            this.unit = unit;
        }

        public Long getInterval() {
            return interval;
        }

        public TimeUnit getUnit() {
            return unit;
        }
    }
}
