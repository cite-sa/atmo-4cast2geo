package gr.cite.atmo4cast2geo.controller;

import gr.cite.atmo4cast2geo.Atmo4Cast2Geo;
import gr.cite.atmo4cast2geo.config.AtmoProperties;
import gr.cite.atmo4cast2geo.exception.ExceptionParser;
import gr.cite.atmo4cast2geo.utils.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class Atmo4Cast2GeoController {
    private static final Logger logger = LoggerFactory.getLogger(Atmo4Cast2GeoController.class);
    private final AtmoProperties properties;

    @Autowired
    public Atmo4Cast2GeoController(AtmoProperties properties) {
        this.properties = properties;
    }

    @PostMapping("generateGeoData")
    public ResponseEntity<Resource> getFile(@RequestPart(name = "zip") List<MultipartFile> zipFiles, @RequestParam(name = "type", required = false) String type) {
        File result = null;
        String processId = UUID.randomUUID().toString();
        try {
            if (zipFiles.get(0) == null || zipFiles.get(0).getOriginalFilename().isEmpty() || !zipFiles.get(0).getOriginalFilename().endsWith("zip")) throw new FileNotFoundException("Please provide a zip file");
            new File(properties.getTempPath() + "/" + processId).mkdirs();
            File zipFile = new File(properties.getTempPath() + "/" + processId + "/" + zipFiles.get(0).getOriginalFilename());
            Files.copy(zipFiles.get(0).getInputStream(), zipFile.toPath());
            List<File> files = ZipUtils.extractZipFile(zipFile, properties.getTempPath() + "/" + processId);
            Files.deleteIfExists(zipFile.toPath());
            result = new File(properties.getTempPath() + "/" + processId + "/result-" + DateTimeFormatter.ofPattern("YYYY-MM-dd-HH-mm").withZone(ZoneId.systemDefault()).format(Instant.now()) + ".zip");
            List<File> newFiles = files.stream()
                    .map(path -> {
                        try {
                            return new File(Atmo4Cast2Geo.convert(path.toString(), type, properties.getTempPath() + "/" + processId));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }).collect(Collectors.toList());
            ZipUtils.createZipFile(result, newFiles);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + result.getName())
                    .body(new FileSystemResource(result));
        } catch (Exception e) {
            String message = ExceptionParser.parseMessage(e);
            logger.error(message, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
        }
    }
}
