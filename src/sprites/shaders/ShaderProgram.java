package sprites.shaders;

import engine.matrices.MatrixMath;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Created by przemek on 16.03.16.
 */
public abstract class ShaderProgram {
    public static Matrix4f defaultMatrix = new Matrix4f();
    public static Matrix4f orthoMatrix = new Matrix4f();
    protected static Matrix4f transformationMatrix = new Matrix4f();
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
    protected int locationMVPMatrix;
    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    {
        transformationMatrix.setIdentity();
        defaultMatrix.setIdentity();
    }

    public ShaderProgram(String vertexFile, String fragmentFile) {
        vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
        getAllUniformLocations();
    }

    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader");
        }
        return shaderID;
    }

    public static void stop() {
        try {
            GL20.glUseProgram(0);
        } catch (Exception e) {
        }
    }

    public void resetOrtho() {
        start();
        orthoMatrix.setIdentity();
        MatrixMath.ortho(ShaderProgram.orthoMatrix, 0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
        loadMVPMatrix(orthoMatrix);
    }

    public void setOrtho(float left, float right, float bottom, float top) {
        start();
        orthoMatrix.setIdentity();
        MatrixMath.ortho(orthoMatrix, left, right, bottom, top, 1, -1);
        loadMVPMatrix(orthoMatrix);
    }

    public void loadMVPMatrix(Matrix4f matrix) {
        loadMatrix(locationMVPMatrix, matrix);
    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(programID, uniformName);
    }

    public void start() {
        GL20.glUseProgram(programID);
    }

    public void cleanUp() {
        stop();
        try {
            GL20.glDetachShader(programID, vertexShaderID);
            GL20.glDetachShader(programID, fragmentShaderID);
            GL20.glDeleteShader(vertexShaderID);
            GL20.glDeleteShader(fragmentShaderID);
            GL20.glDeleteProgram(programID);
        } catch (Exception e) {
        }
    }

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    protected void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    protected void loadVector3f(int location, Vector3f vector) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    protected void loadVector4f(int location, Vector4f vector) {
        GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
    }

    protected void load2Floats(int location, float x, float y) {
        GL20.glUniform2f(location, x, y);
    }

    protected void loadBoolean(int location, boolean value) {
        float toLoad = 0;
        if (value) {
            toLoad = 1;
        }
        GL20.glUniform1f(location, toLoad);
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4(location, false, matrixBuffer);
    }

    protected abstract void bindAttributes();
}
