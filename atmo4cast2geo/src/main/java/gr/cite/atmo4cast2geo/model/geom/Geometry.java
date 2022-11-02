package gr.cite.atmo4cast2geo.model.geom;

import gr.cite.atmo4cast2geo.enumaration.GeometryType;
import gr.cite.atmo4cast2geo.model.misc.IndentationObject;
import gr.cite.atmo4cast2geo.model.geom.shape.Shape;

public class Geometry extends IndentationObject {
    private Shape coordinates;
    private GeometryType type;

    public Shape getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Shape coordinates) {
        this.coordinates = coordinates;
    }

    public GeometryType getType() {
        return type;
    }

    public void setType(GeometryType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.getIndentation() + "\"coordinates\": " + this.coordinates.toString() +
                ",\n" + this.getIndentation() + "\"type\": \"" + this.type.getName() + "\"";
    }
}
