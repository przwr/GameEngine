package game.text.fonts;

import org.lwjgl.opengl.Display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MetaFile {

    private static final int PAD_TOP = 0;
    private static final int PAD_LEFT = 1;
    private static final int PAD_BOTTOM = 2;
    private static final int PAD_RIGHT = 3;

    private static final int DESIRED_PADDING = 8;

    private static final String SPLITTER = " ";
    private static final String NUMBER_SEPARATOR = ",";
    private static String workingLine;

    private double aspectRatio;

    private double verticalPerPixelSize;
    private double horizontalPerPixelSize;
    private double spaceWidth;
    private int[] padding;
    private int paddingWidth;
    private int paddingHeight;

    private Map<Integer, Character> metaData = new HashMap<>();
    private Map<Integer, Double> kernings = new HashMap<>();

    private BufferedReader reader;
    private Map<String, String> values = new HashMap<>();

    protected MetaFile(File file, File xOff) {
        this.aspectRatio = (double) Display.getWidth() / (double) Display.getHeight();
        openFile(file);
        loadPaddingData();
        loadLineSizes();
        int imageWidth = getValueOfVariable("scaleW");
        loadCharacterData(imageWidth);
        close();
        openFile(xOff);
        changeCharacterData();
        close();
    }

    protected double getSpaceWidth() {
        return spaceWidth;
    }

    protected Character getCharacter(int ascii) {
        return metaData.get(ascii);
    }

    protected double getKerning(int code) {
        Double kerning = kernings.get(code);
        if (kerning != null) {
            return kerning;
        } else {
            return 0;
        }
    }

    private boolean processNextLine() {
        values.clear();
        workingLine = null;
        try {
            workingLine = reader.readLine();
        } catch (IOException e1) {
        }
        if (workingLine == null) {
            return false;
        }
        for (String part : workingLine.split(SPLITTER)) {
            String[] valuePairs = part.split("=");
            if (valuePairs.length == 2) {
                values.put(valuePairs[0], valuePairs[1]);
            }
        }
        return true;
    }

    private int getValueOfVariable(String variable) {
        return Integer.parseInt(values.get(variable));
    }

    private int[] getValuesOfVariable(String variable) {
        String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
        int[] actualValues = new int[numbers.length];
        for (int i = 0; i < actualValues.length; i++) {
            actualValues[i] = Integer.parseInt(numbers[i]);
        }
        return actualValues;
    }

    private void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openFile(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't read font meta file!");
        }
    }

    private void loadPaddingData() {
        processNextLine();
        this.padding = getValuesOfVariable("padding");
        this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
        this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
    }


    private void loadLineSizes() {
        processNextLine();
        int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
        verticalPerPixelSize = TextMeshCreator.getFontHeightFactor() / (double) lineHeightPixels;
        horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
    }

    private void loadCharacterData(int imageWidth) {
        processNextLine();
        processNextLine();
        while (processNextLine()) {
            if (values.get("id") != null) {
                Character c = loadCharacter(imageWidth);
                if (c != null) {
                    metaData.put(c.getId(), c);
                }
            } else if (values.get("first") != null) {
                kernings.put(1000 * getValueOfVariable("first") + getValueOfVariable("second"), getValueOfVariable("amount") * horizontalPerPixelSize);
            }
        }
    }

    private void changeCharacterData() {
        processNextLine();
        processNextLine();
        while (processNextLine()) {
            if (values.get("id") != null && values.get("xoffset") != null) {
                int id = getValueOfVariable("id");
                double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
                Character c = metaData.get(id);
                if (c != null) {
                    c.setXOffset(xOff);
                }
            }
        }
    }


    private Character loadCharacter(int imageSize) {
        int id = getValueOfVariable("id");
        if (id == TextMeshCreator.SPACE_ASCII) {
            this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
            return null;
        }
        double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
        double yTex = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
        int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
        int height = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING));
        double quadWidth = width * horizontalPerPixelSize;
        double quadHeight = height * verticalPerPixelSize;
        double xTexSize = (double) width / imageSize;
        double yTexSize = (double) height / imageSize;
        double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
        double yOff = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING)) * verticalPerPixelSize;
        double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
        return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
    }
}
