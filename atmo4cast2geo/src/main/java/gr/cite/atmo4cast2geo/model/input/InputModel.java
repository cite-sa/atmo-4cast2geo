package gr.cite.atmo4cast2geo.model.input;

import java.util.List;
import java.util.Map;

public class InputModel {
    List<InputPoint> geoData;
    private Map<String, String> properties;

    public List<InputPoint> getGeoData() {
        return geoData;
    }

    public void setGeoData(List<InputPoint> geoData) {
        this.geoData = geoData;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
