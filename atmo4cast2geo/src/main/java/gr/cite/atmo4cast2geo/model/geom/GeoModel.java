package gr.cite.atmo4cast2geo.model.geom;

import gr.cite.atmo4cast2geo.model.geom.Feature;

import java.util.List;

public class GeoModel {
    private List<Feature> features;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    @Override
    public String toString() {
        StringBuilder geoModelBuilder = new StringBuilder();
        geoModelBuilder.append("{\n\t");
        if (features.size() > 1) {
            geoModelBuilder.append("\"type\": \"FeatureCollection\",\n\t");
            geoModelBuilder.append("\"features\": [\n\t\t");
        }
        for (int i = 0; i < features.size(); i++) {
            Feature feature = features.get(i);
            feature.setIndentation("\t");
            if (features.size() > 1) {
                geoModelBuilder.append("{\n");
                feature.setIndentation("\t\t\t");
            }
            geoModelBuilder.append(feature);
            if (features.size() > 1) {
                geoModelBuilder.append("\n\t\t}");
            }
            if (i < features.size() - 1) {
                geoModelBuilder.append(",\n\t\t");
            }
        }
        if (features.size() > 1) {
            geoModelBuilder.append("\n\t\t]");
        }
        geoModelBuilder.append("\n}");
        return geoModelBuilder.toString();
    }
}
