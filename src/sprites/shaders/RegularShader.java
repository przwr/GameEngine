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
    private int locationColorModifier;
    private int locationSizeModifier;
    private int locationUseTexture;
    private int locationUseColor;

    public RegularShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        super.start();
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
        locationUseColor = getUniformLocation("useColor");
        locationMVPMatrix = getUniformLocation("mvpMatrix");
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "textureCoords");
        bindAttribute(2, "color");
    }

    public void loadSizeModifier(Vector4f vector) {
        loadVector4f(locationSizeModifier, vector);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        loadMatrix(locationTransformationMatrix, matrix);
    }

    public void loadColorModifier(float x, float y, float z, float w) {
        loadVector4f(locationColorModifier, x, y, z, w);
    }

    public void setUseTexture(boolean use) {
        loadBoolean(locationUseTexture, use);
    }

    public void setUseColor(boolean use) {
        loadBoolean(locationUseColor, use);
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

    public void rotateNoReset(float angle) {
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

    public void scale(float xScale, float yScale) {
        transformationMatrix.load(defaultMatrix);
        MatrixMath.scale(transformationMatrix, xScale, yScale);
        loadMatrix(locationTransformationMatrix, transformationMatrix);
    }
}