package gr.cite.atmo4cast2geo.model.geom.shape;

import java.util.Arrays;
import java.util.List;

public class MultiPolygon implements Shape{
    private List<Polygon> polygons;

    public MultiPolygon() {
    }

    public MultiPolygon(Polygon... polygons) {
        this.polygons = Arrays.asList(polygons);
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public void setPolygons(List<Polygon> polygons) {
        this.polygons = polygons;
    }

    @Override
    public String toString() {
        StringBuilder multiPolygonBuilder = new StringBuilder();
        multiPolygonBuilder.append("[\n");
        for (int i = 0; i < polygons.size(); i++) {
            multiPolygonBuilder.append(polygons.get(i).toString());
            if (i < polygons.size() - 1) {
                multiPolygonBuilder.append(", ");
            }
        }
        multiPolygonBuilder.append("\n]");
        return multiPolygonBuilder.toString();
    }
}
