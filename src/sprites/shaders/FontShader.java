package sprites.shaders;

import org.lwjgl.util.vector.Vector4f;

public class FontShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/sprites/shaders/font.vert";
    private static final String FRAGMENT_FILE = "src/sprites/shaders/font.frag";

    private int locationColor;
    private int locationTranslation;
    private int locationWidth;
    private int locationEdge;
    private int locationBorderWidth;
    private int locationBorderEdge;
    private int locationOffset;
    private int locationBorderColor;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        locationColor = getUniformLocation("color");
        locationWidth = getUniformLocation("width");
        locationEdge = getUniformLocation("edge");
        locationBorderWidth = getUniformLocation("borderWidth");
        locationBorderEdge = getUniformLocation("borderEdge");
        locationOffset = getUniformLocation("offset");
        locationBorderColor = getUniformLocation("borderColor");
        locationTranslation = getUniformLocation("translation");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    public void loadWidth(float width) {
        loadFloat(locationWidth, width);
    }

    public void loadEdge(float edge) {
        loadFloat(locationEdge, edge);
    }

    public void loadBorderWidth(float borderWidth) {
        loadFloat(locationBorderWidth, borderWidth);
    }

    public void loadBorderEdge(float borderEdge) {
        loadFloat(locationBorderEdge, borderEdge);
    }

    public void loadColor(Vector4f color) {
        loadVector4f(locationColor, color);
    }

    public void loadBorderColor(Vector4f borderColor) {
        loadVector4f(locationBorderColor, borderColor);
    }

    public void loadTranslation(float x, float y) {
        load2Floats(locationTranslation, x, y);
    }

    public void loadOffset(float x, float y) {
        load2Floats(locationOffset, x, y);
    }

}
