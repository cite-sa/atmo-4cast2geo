package gr.cite.atmo4cast2geo.config;

import gr.cite.atmo4cast2geo.scheduling.FileDisposerScheduler;
import org.checkerframework.framework.qual.ConditionalPostconditionAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(DisposerProperties.class)
public class DisposerConfig {

    private final DisposerProperties properties;
    private final AtmoProperties atmoProperties;

    @Autowired
    public DisposerConfig(DisposerProperties properties, AtmoProperties atmoProperties) {
        this.properties = properties;
        this.atmoProperties = atmoProperties;
    }

    @Bean
    public ScheduledExecutorFactoryBean scheduledExecutorFactoryBean() {
        ScheduledExecutorFactoryBean factoryBean = new ScheduledExecutorFactoryBean();
        factoryBean.setScheduledExecutorTasks(disposerTask());
        return factoryBean;
    }

    @Bean
    public ScheduledExecutorTask disposerTask() {
        ScheduledExecutorTask scheduledExecutorTask = new ScheduledExecutorTask();
        scheduledExecutorTask.setRunnable(new FileDisposerScheduler(properties, atmoProperties));
        scheduledExecutorTask.setPeriod(properties.getScheduling().getInterval());
        scheduledExecutorTask.setTimeUnit(properties.getScheduling().getUnit());
        return scheduledExecutorTask;
    }
}
