package gr.cite.atmo4cast2geo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"gr.cite.atmo4cast2geo"})
public class Atmo4Cast2GeoApplication {

    public static void main(String[] args) {
        SpringApplication.run(Atmo4Cast2GeoApplication.class, args);
    }
}
