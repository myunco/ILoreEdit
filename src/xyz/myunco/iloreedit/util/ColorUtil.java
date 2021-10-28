package xyz.myunco.iloreedit.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    public static Pattern HEX_COLOR = Pattern.compile("&#([0-9a-fA-F]{6})");
    public static Pattern GRADIENT = Pattern.compile("&\\[#([0-9a-fA-F]{6})-#([0-9a-fA-F]{6})(&[lL])?(.+)]");

    public static String processHexColor(String str) {
        if (str.contains("&#")) {
            Matcher matcher = HEX_COLOR.matcher(str);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(buffer, ofHex(matcher.group(1)));
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        }
        return str;
    }

    private static String ofHex(String str) {
            StringBuilder builder = new StringBuilder("§x");
            for (char c : str.toCharArray()) {
                builder.append('§').append(c);
            }
        return builder.toString();
    }

    public static String processGradientColor(String str) {
        if (str.contains("&[#")) {
            Matcher matcher = GRADIENT.matcher(str);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                boolean bold = matcher.group(3) != null;
                String text = matcher.group(4);
                String[] color = generateGradient(new RGB(matcher.group(1)), new RGB(matcher.group(2)), text.length());
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < color.length; i++) {
                    builder.append(ofHex(color[i]));
                    if (bold) {
                        builder.append("§l");
                    }
                    builder.append(text.charAt(i));
                }
                matcher.appendReplacement(buffer, builder.toString());
            }
            matcher.appendTail(buffer);
            return buffer.toString();
        }
        return str;
    }

    private static String[] generateGradient(RGB startColor, RGB endColor, int step) {
        String[] color = new String[step];
        if (step == 0) {
            return color;
        } else if (step == 1) {
            color[0] = startColor.toString();
            return color;
        } else if (step == 2) {
            color[0] = startColor.toString();
            color[1] = endColor.toString();
            return color;
        }
        RGB temp = new RGB();
        for (int i = 0; i < step; i++) {
            temp.r = startColor.r + (endColor.r - startColor.r) * i / (step - 1);
            temp.g = startColor.g + (endColor.g - startColor.g) * i / (step - 1);
            temp.b = startColor.b + (endColor.b - startColor.b) * i / (step - 1);
            color[i] = temp.toString();
        }
        return color;
    }

    static class RGB {
        private static final String HEX = "0123456789ABCDEF";
        public int r;
        public int g;
        public int b;

        public RGB() {}

        public RGB(String RGB) {
            this(Integer.parseInt(RGB.substring(0, 2), 16), Integer.parseInt(RGB.substring(2, 4), 16), Integer.parseInt(RGB.substring(4), 16));
        }

        public RGB(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        @SuppressWarnings("StringBufferReplaceableByString")
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(HEX.charAt(r / 16)).append(HEX.charAt(r % 16));
            builder.append(HEX.charAt(g / 16)).append(HEX.charAt(g % 16));
            builder.append(HEX.charAt(b / 16)).append(HEX.charAt(b % 16));
            return builder.toString();
        }
    }

}
