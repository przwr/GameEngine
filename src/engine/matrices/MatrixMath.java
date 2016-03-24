package engine.matrices;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by przemek on 16.03.16.
 */
public class MatrixMath {

    public static Vector3f ZERO_VECTOR = new Vector3f(0, 0, 0);
    public static Vector3f ONE_VECTOR = new Vector3f(1, 1, 1);
    public static Vector3f ROTATE_VECTOR = new Vector3f(0, 0, 1);
    public static Matrix4f STATIC_MATRIX = new Matrix4f();

    {
        STATIC_MATRIX.setIdentity();
        Matrix4f.translate(ZERO_VECTOR, STATIC_MATRIX, STATIC_MATRIX);
        Matrix4f.translate(ZERO_VECTOR, STATIC_MATRIX, STATIC_MATRIX);
        Matrix4f.scale(ONE_VECTOR, STATIC_MATRIX, STATIC_MATRIX);
    }


    public static Matrix4f createTransformationMatrix(Vector3f translation, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
        return matrix;
    }

    public static void transformMatrix(Matrix4f matrix, Vector3f translation, float xScale, float yScale) {
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.scale(new Vector3f(xScale, yScale, 1f), matrix, matrix);
    }

    public static void scaleMatrix(Matrix4f matrix, float scale) {
        matrix.setIdentity();
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
    }

    public static void rotateMatrix(Matrix4f matrix, float angle) {
        matrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(angle), ROTATE_VECTOR, matrix, matrix);
    }

    public static void resetMatrix(Matrix4f matrix) {
        matrix.setIdentity();
        Matrix4f.translate(ZERO_VECTOR, matrix, matrix);
        Matrix4f.translate(ZERO_VECTOR, matrix, matrix);
        Matrix4f.scale(ONE_VECTOR, matrix, matrix);
    }

    public static boolean equals(Matrix4f f, Matrix4f s) {
        return f.m00 == s.m00 && f.m01 == s.m01 && f.m02 == s.m02 && f.m03 == s.m03 && f.m10 == s.m10 && f.m11 == s.m11 && f.m12 == s.m12 && f.m13 == s.m13
                && f.m20 == s.m20 && f.m21 == s.m21 && f.m22 == s.m22 && f.m23 == s.m23 && f.m30 == s.m30 && f.m31 == s.m31 && f.m32 == s.m32 && f.m33 == s.m33;
    }

}
