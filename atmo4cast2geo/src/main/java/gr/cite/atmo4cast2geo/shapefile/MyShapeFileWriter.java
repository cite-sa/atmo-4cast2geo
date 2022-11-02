package gr.cite.atmo4cast2geo.shapefile;

import gr.cite.atmo4cast2geo.utils.ZipUtils;
import org.geotools.data.*;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.geojson.GeoJSONDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.feature.type.GeometryTypeImpl;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.URLs;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;

public class MyShapeFileWriter {

    public static File convertGeoJsonToShapeFile(File geoJson, String parentPath, String fileName) {
        File shapeFile = new File(parentPath + "/" + fileName + ".shp");
        URL geoJsonUrl = URLs.fileToUrl(geoJson);
        Map<String, Object> params = new HashMap<>();
        params.put(GeoJSONDataStoreFactory.URL_PARAM.key, geoJsonUrl);
        try {
            DataStore srcDS = DataStoreFinder.getDataStore(params);
            SimpleFeatureCollection featureCollection = srcDS.getFeatureSource(srcDS.getTypeNames()[0]).getFeatures();
            SimpleFeatureType schema = featureCollection.getSchema();
            GeometryDescriptor geom = schema.getGeometryDescriptor();
            List<AttributeDescriptor> attributeDescriptors = schema.getAttributeDescriptors();
            GeometryType geomType = null;
            List<AttributeDescriptor> attribs = new ArrayList<>();
            for (AttributeDescriptor attributeDescriptor: attributeDescriptors) {
                AttributeType type = attributeDescriptor.getType();
                if (type instanceof GeometryType) {
                    geomType = (GeometryType) type;
                } else {
                    attribs.add(attributeDescriptor);
                }
            }
            GeometryType gt = new GeometryTypeImpl(new NameImpl("the_geom"), ((SimpleFeature)featureCollection.toArray()[0]).getDefaultGeometry().getClass(),
                    DefaultGeographicCRS.WGS84, geomType.isIdentified(), geomType.isAbstract(), geomType.getRestrictions(),
                    geomType.getSuper(), geomType.getDescription());

            GeometryDescriptor geomDesc = new GeometryDescriptorImpl(gt, new NameImpl("the_geom"), geom.getMinOccurs(),
                    geom.getMaxOccurs(), geom.isNillable(), geom.getDefaultValue());

            attribs.add(0, geomDesc);

            SimpleFeatureType outSchema = new SimpleFeatureTypeImpl(schema.getName(), attribs, geomDesc, schema.isAbstract(),
                    schema.getRestrictions(), schema.getSuper(), schema.getDescription());

            FileDataStore ds = FileDataStoreFinder.getDataStore(shapeFile);
            ds.createSchema(outSchema);
            SimpleFeatureSource featureSource = ds.getFeatureSource();
            if (featureSource instanceof SimpleFeatureStore) {
                SimpleFeatureCollection collection;
                List<SimpleFeature> feats = new ArrayList<>();
                try (FeatureIterator<SimpleFeature> features2 = featureCollection.features()) {
                    while (features2.hasNext()) {
                        SimpleFeature f = features2.next();
                        SimpleFeature reType = DataUtilities.reType(outSchema, f, true);

                        reType.setAttribute(outSchema.getGeometryDescriptor().getName(),
                                f.getAttribute(schema.getGeometryDescriptor().getName()));

                        feats.add(reType);
                    }
                }

                collection = new ListFeatureCollection(outSchema, feats);

                SimpleFeatureStore outStore = (SimpleFeatureStore) featureSource;

                outStore.addFeatures(collection);
                ds.dispose();
            } else {
                System.err.println("Unable to write to " + shapeFile);
            }
            srcDS.dispose();
            final String tmpFileName = shapeFile.getAbsolutePath().replace(".shp", "");
            //Files.deleteIfExists(Path.of(tmpFileName + ".geojson"));
            int lastIndex = tmpFileName.lastIndexOf("/") < 0 ? tmpFileName.lastIndexOf("\\") : tmpFileName.lastIndexOf("/");
            String folder = tmpFileName.substring(0, lastIndex);
            try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
                shapeFile = new File(parentPath + "/" + fileName + ".zip");
                List<File> files = paths
                        .filter(Files::isRegularFile)
                                .filter(path -> path.toFile().getAbsolutePath().startsWith(tmpFileName))
                        .map(Path::toFile).collect(Collectors.toList());
                shapeFile = ZipUtils.createZipFile(shapeFile, files);
            }
            return shapeFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
