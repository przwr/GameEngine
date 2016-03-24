package sprites.shaders;

import engine.matrices.MatrixMath;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import sprites.Appearance;

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

    private boolean reset;

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
        super.bindAttribute(2, "shade");
    }

    public void resetUniform() {
        if (!reset) {
            super.load2Floats(locationTextureShift, 0, 0);
            super.loadVector4f(locationSizeModifier, Appearance.ZERO_VECTOR);
            super.loadMatrix(locationTransformationMatrix, MatrixMath.STATIC_MATRIX);
            reset = true;
        }
    }


    public void loadTextureShift(float x, float y) {
        super.load2Floats(locationTextureShift, x, y);
        reset = false;
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(locationTransformationMatrix, matrix);
        reset = false;
    }

    public void loadSizeModifier(Vector4f vector) {
        super.loadVector4f(locationSizeModifier, vector);
        reset = false;
    }

    public void loadColourModifier(Vector4f vector) {
        super.loadVector4f(locationColourModifier, vector);
    }


    public void setUseTexture(boolean use) {
        loadBoolean(locationUseTexture, use);
    }

}

