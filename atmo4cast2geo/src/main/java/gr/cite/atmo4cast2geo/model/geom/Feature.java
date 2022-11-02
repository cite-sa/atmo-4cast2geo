package gr.cite.atmo4cast2geo.model.geom;

import gr.cite.atmo4cast2geo.model.misc.IndentationObject;

import java.util.List;
import java.util.Map;

public class Feature extends IndentationObject {
    private List<Geometry>  geometries;
    private Map<String, String> properties;

    public List<Geometry> getGeometries() {
        return geometries;
    }

    public void setGeometries(List<Geometry> geometries) {
        this.geometries = geometries;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return this.getIndentation() +"\"type\":" + (geometries.size() > 1 ? " \"GeometryCollection\"" : " \"Feature\"") + ",\n" +
                this.geometriesToString() +
                ",\n" + this.getIndentation() + "\"properties\": {\n" + this.propertiesToString() +
                "\n" + this.getIndentation() + "}";
    }

    private String geometriesToString() {
        StringBuilder geometryBuilder = new StringBuilder();
        if (geometries.size() > 1) {
            geometryBuilder.append(this.getIndentation()).append("\"geometries\": [\n");
        } else {
            geometryBuilder.append(this.getIndentation()).append("\"geometry\": ");
        }
        for (int i = 0; i < geometries.size(); i++) {
            Geometry geometry = geometries.get(i);
            geometryBuilder.append("{\n");
            geometry.setIndentation(this.getIndentation() + "\t");
            geometryBuilder.append(geometry);
            geometryBuilder.append("\n").append(this.getIndentation()).append("}");
            if(i < geometries.size() - 1) {
                geometryBuilder.append(",\n");
            }
        }
        if (geometries.size() > 1) {
            geometryBuilder.append("\n").append(this.getIndentation()).append("\t]");
        }
        return geometryBuilder.toString();
    }

    private String propertiesToString() {
        StringBuilder propertiesBuilder = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, String> entry: properties.entrySet()) {
            propertiesBuilder.append(this.getIndentation()).append("\t\"").append(entry.getKey()).append("\": ").append(entry.getValue());
            if (i < properties.entrySet().size() - 1) {
                propertiesBuilder.append(",\n");
            }
            i++;
        }
        return propertiesBuilder.toString();
    }
}
