package sprites.shaders;

import engine.matrices.MatrixMath;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by przemek on 16.03.16.
 */
public class RegularShader extends ShaderProgram {


    private static final String VERTEX_FILE = "src/sprites/shaders/regular.vert";
    private static final String FRAGMENT_FILE = "src/sprites/shaders/regular.frag";

    private int locationTransformationMatrix;
    private int locationColourModifier;
    private int locationSizeModifier;
    private int locationUseTexture;
    private int locationUseColour;

    public RegularShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        super.start();
        loadBoolean(locationUseTexture, true);
        loadMatrix(locationTransformationMatrix, MatrixMath.STATIC_MATRIX);
        resetOrtho();
        super.stop();
    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = getUniformLocation("transformationMatrix");
        locationColourModifier = getUniformLocation("colourModifier");
        locationSizeModifier = getUniformLocation("sizeModifier");
        locationUseTexture = getUniformLocation("useTexture");
        locationUseColour = getUniformLocation("useColour");
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "textureCoords");
        bindAttribute(2, "colour");
    }

    public void loadSizeModifier(Vector4f vector) {
        loadVector4f(locationSizeModifier, vector);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        loadMatrix(locationTransformationMatrix, matrix);
    }

    public void loadColourModifier(Vector4f vector) {
        loadVector4f(locationColourModifier, vector);
    }

    public void setUseTexture(boolean use) {
        loadBoolean(locationUseTexture, use);
    }

    public void setUseColour(boolean use) {
        loadBoolean(locationUseColour, use);
    }

    public void resetWorkingMatrix() {
        transformationMatrix.load(defaultMatrix);
    }

    public void resetTransformationMatrix() {
        loadMatrix(locationTransformationMatrix, defaultMatrix);
    }

    public void resetDefaultMatrix() {
        defaultMatrix.setIdentity();
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

    public void rotateTranslate(float x, float y, float angle) {
        transformationMatrix.load(defaultMatrix);
        MatrixMath.translateRotate(transformationMatrix, x, y, angle);
        loadMatrix(locationTransformationMatrix, transformationMatrix);
    }

    public void rotate(float angle) {
        transformationMatrix.load(defaultMatrix);
        MatrixMath.rotate(transformationMatrix, angle);
        loadMatrix(locationTransformationMatrix, transformationMatrix);
    }

    public void translateScale(float x, float y, float xScale, float yScale) {
        transformationMatrix.load(defaultMatrix);
        MatrixMath.translateScale(transformationMatrix, x, y, xScale, yScale);
        loadMatrix(locationTransformationMatrix, transformationMatrix);
    }

    public void scaleTranslate(float x, float y, float xScale, float yScale) {
        transformationMatrix.load(defaultMatrix);
        MatrixMath.scaleTranslate(transformationMatrix, x, y, xScale, yScale);
        loadMatrix(locationTransformationMatrix, transformationMatrix);
    }

    public void translateDefault(int x, int y) {
        MatrixMath.translateMatrix(defaultMatrix, x, y);
        loadMatrix(locationTransformationMatrix, defaultMatrix);
    }

    public void scaleTranslateDefault(int x, int y, float scale) {
        MatrixMath.translateMatrix(defaultMatrix, x, y);
        MatrixMath.scale(defaultMatrix, scale, scale);
        loadMatrix(locationTransformationMatrix, defaultMatrix);
    }

    public void scaleNoReset(float xScale, float yScale) {
        MatrixMath.scale(transformationMatrix, xScale, yScale);
        loadMatrix(locationTransformationMatrix, transformationMatrix);
    }
}