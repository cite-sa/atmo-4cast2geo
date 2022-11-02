package gr.cite.atmo4cast2geo.model.geom.shape;

import java.util.Arrays;
import java.util.List;

public class Polygon implements Shape{
    private List<LineString> lineStrings;

    public Polygon() {
    }

    public Polygon(LineString... lineStrings) {
        this.lineStrings = Arrays.asList(lineStrings);
    }

    public List<LineString> getLineStrings() {
        return lineStrings;
    }

    public void setLineStrings(List<LineString> lineStrings) {
        this.lineStrings = lineStrings;
    }

    @Override
    public String toString() {
        StringBuilder polygonBuilder = new StringBuilder();
        polygonBuilder.append("[\n");
        for (int i = 0; i < lineStrings.size(); i++) {
            polygonBuilder.append(lineStrings.get(i).toString());
            if (i < lineStrings.size() - 1) {
                polygonBuilder.append(",\n");
            }
        }
        polygonBuilder.append("\n]");
        return polygonBuilder.toString();
    }
}
