package sprites.vbo;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * Created by przemek on 11.03.16.
 */
public class VertexBufferObject {


    private static ArrayList<VertexBufferObject> vbos = new ArrayList<>();

    private int vaoID;
    private ArrayList<Integer> vbosIDs = new ArrayList<>();
    private int vertexCount;

    public VertexBufferObject(float[] positions) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 2, positions);
        GL30.glBindVertexArray(0);
        this.vaoID = vaoID;
        this.vertexCount = positions.length / 2;
        vbos.add(this);
    }

    public VertexBufferObject(float[] positions, float[] textureCoords, int[] indices) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        bindIndicesBuffer(indices);
        GL30.glBindVertexArray(0);
        this.vaoID = vaoID;
        this.vertexCount = indices.length;
        vbos.add(this);
    }

    public static void cleanUp() {
        for (VertexBufferObject vbo : vbos) {
            vbo.delete();
        }
        vbos.clear();
    }

    private static int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    public void clear() {
        delete();
        if (vbos.contains(this)) {
            vbos.remove(this);
        }
    }

    private void delete() {
        GL30.glDeleteVertexArrays(vaoID);
        for (int vbo : vbosIDs) {
            GL15.glDeleteBuffers(vbo);
        }
        vbosIDs.clear();
    }

    private void storeDataInAttributeList(int attributeNumber, int size, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbosIDs.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbosIDs.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
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

    public void renderTextured(int start, int renderCount) {
        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL11.glDrawElements(GL11.GL_TRIANGLES, renderCount, GL11.GL_UNSIGNED_INT, start * 4L); // size of UNSIGNED_INT in BYTES
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    public void renderTexturePiece(int start) {
        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    public int getVertexCount() {
        return vertexCount;
    }

}
