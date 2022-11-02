package gr.cite.atmo4cast2geo.geotiff;

import gr.cite.atmo4cast2geo.utils.ColorGrader;
import gr.cite.atmo4cast2geo.utils.ZipUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.geojson.GeoJSONDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.util.URLs;
import org.geotools.util.factory.GeoTools;
import org.geotools.util.factory.Hints;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyGeoTiffWriter {

    public static File convertGeoJsonToRaster(File geoJson, String parentPath, String fileName) {
        File geoTiff = new File(parentPath + "/" + fileName + ".tiff");
        URL geoJsonUrl = URLs.fileToUrl(geoJson);
        Map<String, Object> params = new HashMap<>();
        params.put(GeoJSONDataStoreFactory.URL_PARAM.key, geoJsonUrl);

        DataStore srcDS = null;
        try {
            srcDS = DataStoreFinder.getDataStore(params);
            SimpleFeatureCollection featureCollection = srcDS.getFeatureSource(srcDS.getTypeNames()[0]).getFeatures();
            MapContent mapContent = new MapContent();
            mapContent.setTitle(fileName);
            int i = 1;
            List<SimpleFeature> featureList = new ArrayList<>();
            try(SimpleFeatureIterator featureIterator = featureCollection.features()) {
                while (featureIterator.hasNext()) {
                    featureList.add(featureIterator.next());
                }
            }

            String attrName = "";
            for (AttributeDescriptor attributeDescriptor: featureList.get(0).getFeatureType().getAttributeDescriptors()) {
                if (attributeDescriptor.getType().getBinding().equals(Double.class)) {
                    attrName = attributeDescriptor.getName().toString();
                }
            }
            String finalAttrName = attrName;
            double max = featureList.stream().filter(simpleFeature -> simpleFeature.getAttribute(finalAttrName) != null).map(simpleFeature -> ((Number)simpleFeature.getAttribute(finalAttrName)).doubleValue()).max(Double::compareTo).orElse(0.0);
            for (SimpleFeature feature: featureList) {
                double value = feature.getAttribute(attrName) != null ? ((Number)feature.getAttribute(attrName)).doubleValue() : 0d;
                Color fillColor = ColorGrader.calculateColor(value, max);
                Style style = SLD.createPointStyle("Square", ColorGrader.getTransparentColor(), fillColor, 1.0f, 12.5f);
                mapContent.addLayer(new FeatureLayer(new ListFeatureCollection(feature.getFeatureType(), feature), style));
            }
            Rectangle screen = new Rectangle(750, 750);
            StreamingRenderer renderer = new StreamingRenderer();
            ReferencedEnvelope maxBounds = mapContent.getMaxBounds();
            renderer.setMapContent(mapContent);

            BufferedImage image = new BufferedImage(screen.width, screen.height, BufferedImage.TYPE_INT_ARGB);
            mapContent.getViewport().setBounds(maxBounds);

            Graphics2D destGraphics = image.createGraphics();
            renderer.paint(destGraphics, screen, maxBounds);

            Hints hints = GeoTools.getDefaultHints();
            GridCoverage2D coverage = new GridCoverageFactory(hints)
                    .create("GeoTiff", image, featureCollection.getBounds());

            FileOutputStream fos = new FileOutputStream(geoTiff);
            GeoTiffWriter geoTiffWriter = new GeoTiffWriter(fos);
            GeoTiffWriteParams geoTiffparams = new GeoTiffWriteParams();
            ParameterValue<GeoToolsWriteParams> value = GeoTiffFormat.GEOTOOLS_WRITE_PARAMS.createValue();
            value.setValue(geoTiffparams);
            geoTiffWriter.write(coverage, new GeneralParameterValue[] { value });
            mapContent.dispose();
            geoTiffWriter.dispose();
            fos.close();
            //Files.deleteIfExists(geoJson.toPath());
            String filePath = geoTiff.getAbsolutePath().substring(0, geoTiff.getAbsolutePath().lastIndexOf("."));
            String folderPath = filePath.contains("\\")? filePath.substring(0, filePath.lastIndexOf("\\")) : filePath.substring(0, filePath.lastIndexOf("/"));
            try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
                geoTiff = new File(filePath + ".zip");
                List<File> files = paths
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .filter(file -> file.getAbsolutePath().startsWith(filePath)).collect(Collectors.toList());
                geoTiff = ZipUtils.createZipFile(geoTiff, files);
            }
            return geoTiff;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
