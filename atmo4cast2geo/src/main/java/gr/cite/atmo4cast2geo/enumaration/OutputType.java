package gr.cite.atmo4cast2geo.enumaration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OutputType {
    JSON("json"), SHAPE_FILE("shapefile"), GEO_TIFF("tiff");

    private final String name;

    OutputType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    public static OutputType fromName(String name) {
        for (OutputType type: OutputType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return JSON;
    }
}
