package gr.cite.atmo4cast2geo.model.geom.shape;

import java.util.Arrays;
import java.util.List;

public class LineString implements Shape{
    private List<Point> points;

    public LineString() {
    }

    public LineString(Point... points) {
        this.points = Arrays.asList(points);
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        StringBuilder lineStringBuilder = new StringBuilder();
        lineStringBuilder.append("[");
        for(int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            lineStringBuilder.append("[").append(point.getX()).append(", ").append(point.getY()).append("]");
            if (i < points.size() - 1) {
                lineStringBuilder.append(", ");
            }
        }
        lineStringBuilder.append("]");
        return lineStringBuilder.toString();
    }
}
