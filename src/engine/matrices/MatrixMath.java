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
    public static Matrix4f orthoMatrix = new Matrix4f();

    {
        STATIC_MATRIX.setIdentity();
        Matrix4f.translate(ZERO_VECTOR, STATIC_MATRIX, STATIC_MATRIX);
        Matrix4f.scale(ONE_VECTOR, STATIC_MATRIX, STATIC_MATRIX);
    }


    public static Matrix4f createTransformationMatrix(float x, float y, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        translateMatrix(matrix, x, y);
        scale(matrix, scale, scale);
        return matrix;
    }

    public static void translateScale(Matrix4f matrix, float x, float y, float xScale, float yScale) {
        translateMatrix(matrix, x, y);
        scale(matrix, xScale, yScale);
    }

    public static void scaleTranslate(Matrix4f matrix, float x, float y, float xScale, float yScale) {
        scale(matrix, xScale, yScale);
        translateMatrix(matrix, x, y);
    }


    public static void translateRotate(Matrix4f matrix, float x, float y, float angle) {
        translateMatrix(matrix, x, y);
        rotate(matrix, angle);
    }

    public static void translateMatrix(Matrix4f matrix, float x, float y) {
        matrix.m30 += matrix.m00 * x + matrix.m10 * y;
        matrix.m31 += matrix.m01 * x + matrix.m11 * y;
        matrix.m32 += matrix.m02 * x + matrix.m12 * y;
        matrix.m33 += matrix.m03 * x + matrix.m13 * y;
    }

    public static void scale(Matrix4f matrix, float xScale, float yScale) {
        matrix.m00 = matrix.m00 * xScale;
        matrix.m01 = matrix.m01 * xScale;
        matrix.m02 = matrix.m02 * xScale;
        matrix.m03 = matrix.m03 * xScale;
        matrix.m10 = matrix.m10 * yScale;
        matrix.m11 = matrix.m11 * yScale;
        matrix.m12 = matrix.m12 * yScale;
        matrix.m13 = matrix.m13 * yScale;
    }

    public static void ortho(Matrix4f matrix, float left, float right, float bottom, float top, float near, float far) {
        orthoMatrix.m00 = 2f / (right - left);
        orthoMatrix.m11 = 2f / (top - bottom);
        orthoMatrix.m22 = -2f / (far - near);
        orthoMatrix.m33 = 1f;
        orthoMatrix.m30 = -(right + left) / (right - left);
        orthoMatrix.m31 = -(top + bottom) / (top - bottom);
        orthoMatrix.m32 = -(far + near) / (far - near);
        Matrix4f.mul(matrix, orthoMatrix, matrix);
    }

    public static void rotate(Matrix4f matrix, float angle) {
        Matrix4f.rotate((float) Math.toRadians(angle), ROTATE_VECTOR, matrix, matrix);
    }

    public static void resetMatrix(Matrix4f matrix) {
        matrix.setIdentity();
    }

    public static boolean equals(Matrix4f f, Matrix4f s) {
        return f.m00 == s.m00 && f.m01 == s.m01 && f.m02 == s.m02 && f.m03 == s.m03 && f.m10 == s.m10 && f.m11 == s.m11 && f.m12 == s.m12 && f.m13 == s.m13
                && f.m20 == s.m20 && f.m21 == s.m21 && f.m22 == s.m22 && f.m23 == s.m23 && f.m30 == s.m30 && f.m31 == s.m31 && f.m32 == s.m32 && f.m33 == s.m33;
    }

}
