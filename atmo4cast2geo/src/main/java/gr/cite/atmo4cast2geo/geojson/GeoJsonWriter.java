package gr.cite.atmo4cast2geo.geojson;

import gr.cite.atmo4cast2geo.enumaration.GeometryType;
import gr.cite.atmo4cast2geo.model.geom.Feature;
import gr.cite.atmo4cast2geo.model.geom.GeoModel;
import gr.cite.atmo4cast2geo.model.geom.Geometry;
import gr.cite.atmo4cast2geo.model.geom.shape.LineString;
import gr.cite.atmo4cast2geo.model.geom.shape.Point;
import gr.cite.atmo4cast2geo.model.geom.shape.Polygon;
import gr.cite.atmo4cast2geo.model.input.InputModel;
import gr.cite.atmo4cast2geo.model.input.InputPoint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GeoJsonWriter {

    public static String generateGeoJsonStringFromInputData(List<InputModel> inputModels) {
        List<Feature> features = inputModels.stream().map(inputModel -> {
            Feature feature = new Feature();
            Geometry geometry = new Geometry();
            int numberOfPoints = inputModel.getGeoData().size();
            List<Point> points = inputModel.getGeoData().stream().sorted(Comparator.comparing(InputPoint::getOrdinal)).map(point -> new Point(point.getLongitude(), point.getLatitude())).collect(Collectors.toList());
            if (numberOfPoints == 1) {
                geometry.setType(GeometryType.POINT);
                geometry.setCoordinates(points.get(0));
            } else if (numberOfPoints > 1) {
                if (points.get(0).equals(points.get(points.size() - 1))) {
                    geometry.setType(GeometryType.POLYGON);
                    geometry.setCoordinates(new Polygon(Collections.singletonList(new LineString(points.toArray(new Point[0]))).toArray(new LineString[0])));
                } else {
                    geometry.setType(GeometryType.LINE_STRING);
                    geometry.setCoordinates(new LineString(points.toArray(new  Point[0])));
                }
            }
            if (geometry.getType() != null) {
                feature.setGeometries(Collections.singletonList(geometry));
                feature.setProperties(inputModel.getProperties());
                return feature;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        GeoModel geoModel = new GeoModel();
        geoModel.setFeatures(features);
        return geoModel.toString();
    }

    public static File generateGeoJsonFromInputData(List<InputModel> inputModels, String parentPath, String fileName) {

        try {
            String tmpFileName = parentPath + "/" + fileName + ".geojson";
            FileWriter writer = new FileWriter(tmpFileName);
            writer.write(generateGeoJsonStringFromInputData(inputModels));
            writer.flush();
            writer.close();
            return new File(tmpFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
