package gr.cite.atmo4cast2geo.scheduling;

import gr.cite.atmo4cast2geo.config.AtmoProperties;
import gr.cite.atmo4cast2geo.config.DisposerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

public class FileDisposerScheduler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FileDisposerScheduler.class);

    private final DisposerProperties properties;
    private final AtmoProperties atmoProperties;

    public FileDisposerScheduler(DisposerProperties properties, AtmoProperties atmoProperties) {
        this.properties = properties;
        this.atmoProperties = atmoProperties;
    }

    public void disposeFiles() {
        try (Stream<Path> paths = Files.walk(Paths.get(atmoProperties.getTempPath()))) {
            logger.debug("Start Disposing Files");
            paths.filter(path -> path.toFile().isDirectory() && !path.toString().endsWith(atmoProperties.getTempPath().substring(atmoProperties.getTempPath().lastIndexOf("/") + 1)))
                    .filter(path -> {
                        try {
                            return Files.getLastModifiedTime(path).toInstant().isBefore(Instant.now().minus(1, ChronoUnit.DAYS));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .forEach(path -> {
                try {
                    Files.move(path, Paths.get(properties.getLocation() + "/" + path.toFile().getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public void run() {
        this.disposeFiles();
    }
}
