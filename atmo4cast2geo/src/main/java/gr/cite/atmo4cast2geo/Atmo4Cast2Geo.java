package gr.cite.atmo4cast2geo;

import gr.cite.atmo4cast2geo.enumaration.OutputType;
import gr.cite.atmo4cast2geo.geojson.GeoJsonWriter;
import gr.cite.atmo4cast2geo.geotiff.MyGeoTiffWriter;
import gr.cite.atmo4cast2geo.model.input.InputModel;
import gr.cite.atmo4cast2geo.shapefile.MyShapeFileWriter;
import gr.cite.atmo4cast2geo.input.InputFileReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Atmo4Cast2Geo {

    public static String convert(String data, String type, String parentPath) throws IOException {
        File dataFile = new File(data);
        InputFileReader inputFileReader = new InputFileReader();
        List<InputModel> inputData = inputFileReader.readInputFromFile(dataFile);
        String fileName = dataFile.getName().replace(dataFile.getName().substring(dataFile.getName().lastIndexOf(".")), "");
        Files.deleteIfExists(dataFile.toPath());
        File geoJsonFile = GeoJsonWriter.generateGeoJsonFromInputData(inputData, parentPath, fileName);
        OutputType outputType;
        if (type != null && !type.isEmpty()) {
            outputType = OutputType.fromName(type);
        } else {
            outputType = inputFileReader.getType();
        }
        switch (outputType) {
            case SHAPE_FILE:
                return MyShapeFileWriter.convertGeoJsonToShapeFile(geoJsonFile, parentPath, fileName).getAbsolutePath();
            case GEO_TIFF:
                return MyGeoTiffWriter.convertGeoJsonToRaster(geoJsonFile, parentPath, fileName).getAbsolutePath();
            case JSON:
            default:
                return geoJsonFile.getAbsolutePath();
        }

    }
}
