package sprites.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by przemek on 16.03.16.
 */
public class ShadowShader extends ShaderProgram {


    private static final String VERTEX_FILE = "src/sprites/shaders/shadow.vert";
    private static final String FRAGMENT_FILE = "src/sprites/shaders/shadow.frag";

    private int locationTransformationMatrix;
    private int locationTextureShift;
    private int locationColourModifier;
    private int locationSizeModifier;
    private int locationUseTexture;

    public ShadowShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
        locationTextureShift = super.getUniformLocation("textureShift");
        locationColourModifier = super.getUniformLocation("colourModifier");
        locationSizeModifier = super.getUniformLocation("sizeModifier");
        locationUseTexture = super.getUniformLocation("useTexture");
        setUseTexture(true);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    public void loadTextureShift(float x, float y) {
        super.load2Floats(locationTextureShift, x, y);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(locationTransformationMatrix, matrix);
    }

    public void loadColourModifier(Vector4f vector) {
        super.loadVector4f(locationColourModifier, vector);
    }

    public void loadSizeModifier(Vector4f vector) {
        super.loadVector4f(locationSizeModifier, vector);
    }

    public void setUseTexture(boolean use) {
        loadBoolean(locationUseTexture, use);
    }

}

