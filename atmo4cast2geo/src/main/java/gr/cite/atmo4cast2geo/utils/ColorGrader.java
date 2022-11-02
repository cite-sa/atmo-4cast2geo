package gr.cite.atmo4cast2geo.utils;

import java.awt.*;

public class ColorGrader {

    public static final double MAX_STEPS = 100.0;
    private static final double MAX_LEVELS = 5.0;
    private static final float ALPHA = 0.5f;
    private static final int MIN_STEP = 10;
    private static final int MAX_STEP = 20;

    public static Color getTransparentColor() {
        return new Color(0f, 0f, 0f, 0f);
    }

    public static Color calculateColor(double value, double max) {
        double step = max / MAX_STEPS;
        double levelStep = MAX_STEPS / MAX_LEVELS;
        int r = 0;
        int g = 0;
        int b = 100;
        Color result = applyTransparency(new Color(r,g,b));

        int level = 0;
        for (int i = 0; i < MAX_STEPS; i++) {
            if (value > (i * step) ) {
                if (i > (levelStep * (level + 1))){
                    level++;
                }
                switch (level) {
                    case 0:
                        g = Math.min(g + MIN_STEP, 255);
                        b = Math.min(b + MIN_STEP, 255);
                        break;
                    case 1:
                        g = Math.min(g + MIN_STEP, 255);
                        b = Math.max(b - MIN_STEP, 0);
                        break;
                    case 2:
                        r = Math.min(r + MIN_STEP, 255);
                        g = Math.min(g + MIN_STEP, 255);
                        b = Math.max(b - MAX_STEP, 0);
                        break;
                    case 3:
                        r = Math.min(r + MAX_STEP, 255);
                        g = Math.max(g - MIN_STEP, 0);
                        b = Math.max(b - MAX_STEP, 0);
                        break;
                    case 4:
                        r = Math.max(r - MIN_STEP, 0);
                        g = Math.max(g - MAX_STEP, 0);
                        b = Math.max(b - MAX_STEP, 0);
                        break;
                }
                result = applyTransparency(new Color(r, g, b));
            } else {
                break;
            }
        }
        return result;
    }

    private static Color applyTransparency(Color color) {
        return new Color(convertValue(color.getRed()), convertValue(color.getGreen()), convertValue(color.getBlue()), ALPHA);
    }

    private static Float convertValue(Integer value) {
        return (value - 0.5f) /255.0f;
    }
}
