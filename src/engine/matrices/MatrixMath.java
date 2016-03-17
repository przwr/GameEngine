package engine.matrices;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by przemek on 16.03.16.
 */
public class MatrixMath {

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(0), new Vector3f(0, 0, 1), matrix, matrix);
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
        return matrix;
    }

    public static void transformationMatrix(Matrix4f matrix, Vector3f translation) {
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.translate(translation, matrix, matrix);
    }

}
