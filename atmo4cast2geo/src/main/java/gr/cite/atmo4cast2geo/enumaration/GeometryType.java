package gr.cite.atmo4cast2geo.enumaration;

public enum GeometryType {
    POINT("Point"),
    LINE_STRING("LineString"),
    POLYGON("Polygon"),
    MULTI_POINT("MultiPoint"),
    MULTI_LINE_STRING("MultiLineString"),
    MULTI_POLYGON("MultiPolygon");

    private final String name;

    GeometryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
