package gr.cite.atmo4cast2geo.input;

import gr.cite.atmo4cast2geo.enumaration.OutputType;
import gr.cite.atmo4cast2geo.model.input.InputModel;
import gr.cite.atmo4cast2geo.model.input.InputPoint;
import gr.cite.atmo4cast2geo.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

public class InputFileReader {

    private final static String LON = "lon";
    private final static String LAT = "lat";
    private final static String ID = "id";
    private final static String VERTEX_INDEX = "VERTEX_INDEX";

    private OutputType type;

    public OutputType getType() {
        return type;
    }

    public List<InputModel> readInputFromFile(File file) {
            return readInputFromCsv(file);
    }

    private List<InputModel> readInputFromCsv(File file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String firstLine = reader.readLine();
            List<String> heads = StringUtils.csvSplit(firstLine);
            final Map<String, Integer> headsMap = heads.stream().collect(Collectors.toMap(s -> s, heads::indexOf));
            if (!headsMap.containsKey(LON) && !headsMap.containsKey(LAT)) throw new IllegalArgumentException("The Document is missing lon and lat values");
            if (heads.contains(ID)) {
                this.type = OutputType.SHAPE_FILE;
            } else {
                this.type = OutputType.GEO_TIFF;
            }
            Map<Integer, InputModel> inputModels = new HashMap<>();
            int ordinal = 0;
            for (String line: reader.lines().collect(Collectors.toList())) {
                List<String> values = StringUtils.csvSplit(line).stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
                int id;
                if (headsMap.containsKey(ID)) {
                    id = Integer.parseInt(values.get(headsMap.get(ID)));
                } else {
                    id = ordinal;
                    ordinal++;
                }
                InputModel inputModel;
                if (inputModels.containsKey(id)) {
                    inputModel = inputModels.get(id);
                } else {
                    inputModel = new InputModel();
                    inputModel.setGeoData(new ArrayList<>());
                    inputModels.put(id, inputModel);
                }
                InputPoint inputPoint = new InputPoint();
                inputPoint.setLongitude(Double.parseDouble(values.get(headsMap.get(LON))));
                inputPoint.setLatitude(Double.parseDouble(values.get(headsMap.get(LAT))));
                if (headsMap.containsKey(VERTEX_INDEX)) {
                    inputPoint.setOrdinal(Integer.parseInt(values.get(headsMap.get(VERTEX_INDEX))));
                } else {
                    inputPoint.setOrdinal(0);
                }
                inputModel.getGeoData().add(inputPoint);
                if (values.size() == headsMap.size()) {
                    generateProperties(values, headsMap, inputModel);
                }
            }
            reader.close();
            fr.close();
            return new ArrayList<>(inputModels.values());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateProperties(List<String> values, Map<String, Integer> headsMap, InputModel inputModel) {
        Map<String, String> properties = new LinkedHashMap<>();
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(',');
        formatSymbols.setPatternSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat(type.equals(OutputType.SHAPE_FILE) ? "#####################0.######################": "#####################0.0#####################", formatSymbols);
        headsMap.entrySet().stream().filter(s -> !s.getKey().equals(LON) && !s.getKey().equals(LAT) && !s.getKey().equals(VERTEX_INDEX))
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .forEach(s -> {
            String ts = s;
            if (this.type.equals(OutputType.SHAPE_FILE) && ts.length() > 9) {
                ts = ts.substring(0, 9);
            }
            properties.put(ts, decimalFormat.format(Double.parseDouble(values.get(headsMap.get(s)))));
        });
        inputModel.setProperties(properties);
    }
}
