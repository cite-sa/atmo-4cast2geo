package gr.cite.atmo4cast2geo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

    public static String removeHeading(String original, String delim) {
        String tmp = original;
        while(tmp.startsWith(delim)) {
            tmp = tmp.substring(1);
        }
        return tmp;
    }

    public static List<String> csvSplit(String source) {
        List<String> separators = Arrays.asList(", ", ",", ";");
        List<String> results = new ArrayList<>();
        for (String separator: separators) {
            results = Arrays.asList(source.split(separator));
            if (results.size() > 1) {
                break;
            }
        }

        return results;
    }
}
