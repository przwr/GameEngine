package sprites.shaders;

import engine.matrices.MatrixMath;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by przemek on 28.04.16.
 */
public class ParticleShader extends ShaderProgram {


    private static final String VERTEX_FILE = "src/sprites/shaders/particle.vert";
    private static final String FRAGMENT_FILE = "src/sprites/shaders/particle.frag";

    private int locationTransformationMatrix;
    private int locationColorModifier;
    private int locationFrames;

    public ParticleShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        super.start();
        loadColorModifier(new Vector4f(1f, 1f, 1f, 1f));
        loadMatrix(locationTransformationMatrix, MatrixMath.STATIC_MATRIX);
        resetOrtho();
        stop();
    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = getUniformLocation("transformationMatrix");
        locationMVPMatrix = getUniformLocation("mvpMatrix");
        locationColorModifier = getUniformLocation("colorModifier");
        locationFrames = getUniformLocation("frames");
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "stage");
    }

    public void loadFrames(float x, float y) {
        load2Floats(locationFrames, x, y);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        loadMatrix(locationTransformationMatrix, matrix);
    }

    public void loadColorModifier(Vector4f vector) {
        loadVector4f(locationColorModifier, vector);
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