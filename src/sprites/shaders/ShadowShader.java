package sprites.shaders;

import engine.matrices.MatrixMath;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by przemek on 16.03.16.
 */
public class ShadowShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/sprites/shaders/shadow.vert";
    private static final String FRAGMENT_FILE = "src/sprites/shaders/shadow.frag";

    private int locationTransformationMatrix;
    private int locationColorModifier;
    private int locationSizeModifier;
    private int locationUseTexture;

    public ShadowShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        start();
        loadBoolean(locationUseTexture, true);
        loadMatrix(locationTransformationMatrix, MatrixMath.STATIC_MATRIX);
        resetOrtho();
        stop();
    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = getUniformLocation("transformationMatrix");
        locationColorModifier = getUniformLocation("colorModifier");
        locationSizeModifier = getUniformLocation("sizeModifier");
        locationUseTexture = getUniformLocation("useTexture");
        locationMVPMatrix = getUniformLocation("mvpMatrix");
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "textureCoords");
        bindAttribute(2, "shade");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(locationTransformationMatrix, matrix);
    }

    public void loadSizeModifier(Vector4f vector) {
        loadVector4f(locationSizeModifier, vector);
    }

    public void loadColorModifier(Vector4f vector) {
        loadVector4f(locationColorModifier, vector);
    }

    public void resetWorkingMatrix() {
        transformationMatrix.load(defaultMatrix);
    }

    public void setUseTexture(boolean use) {
        loadBoolean(locationUseTexture, use);
    }

    public void resetTransformationMatrix() {
        loadMatrix(locationTransformationMatrix, defaultMatrix);
    }

    public void translate(float x, float y) {
        transformationMatrix.load(defaultMatrix);
        MatrixMath.translateMatrix(transformationMatrix, x, y);
        loadMatrix(locationTransformationMatrix, transformationMatrix);
    }

    public void translateNoReset(float x, float y) {
        MatrixMath.translateMatrix(transformationMatrix, x, y);
        loadMatrix(locationTransformationMatrix, transformationMatrix);
    }

    public void translateDefault(int x, int y) {
        MatrixMath.translateMatrix(defaultMatrix, x, y);
        loadMatrix(locationTransformationMatrix, defaultMatrix);
    }
}

