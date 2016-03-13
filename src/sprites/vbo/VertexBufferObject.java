package sprites.vbo;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by przemek on 11.03.16.
 */
public class VertexBufferObject {


    private static ArrayList<Integer> vaos = new ArrayList<>();
    private static ArrayList<Integer> vbos = new ArrayList<>();

    private int vaoID;
    private int vertexCount;

    public VertexBufferObject(float[] positions) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, positions);
        GL30.glBindVertexArray(0);
        this.vaoID = vaoID;
        this.vertexCount = positions.length / 2;
    }

    public static void cleanUp() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }
    }

    private static int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private static void storeDataInAttributeList(int attributeNumber, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, 2, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public void renderTriangles(int start, int renderCount, int sizeInTriangles) {
        sizeInTriangles *= 3;
        start *= sizeInTriangles;
        renderCount *= sizeInTriangles;
        render(start, renderCount, GL11.GL_TRIANGLES);
    }

    public void renderTriangleStrip() {
        render(0, vertexCount, GL11.GL_TRIANGLE_STRIP);
    }

    public void renderTriangleStrip(int start, int renderCount) {
        render(start, renderCount, GL11.GL_TRIANGLE_STRIP);
    }

    public void renderTriangles() {
        render(0, vertexCount, GL11.GL_TRIANGLES);
    }

    private void render(int start, int renderCount, int type) {
        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawArrays(type, start, renderCount);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

}
