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


    private VertexBufferObject(float[] positions, int usage) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, positions, usage);
        GL30.glBindVertexArray(0);
        this.vaoID = vaoID;
        this.vertexCount = positions.length / 2;
        vbos.add(this);
    }

    private VertexBufferObject(float[] positions, float[] colors, boolean shaded) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, positions, GL15.GL_STATIC_DRAW);
        if (shaded) {
            storeShadeInAttributeList(2, colors, GL15.GL_STATIC_DRAW);
        } else {
            storeColorInAttributeList(2, colors, GL15.GL_STATIC_DRAW);
        }
        GL30.glBindVertexArray(0);
        this.vaoID = vaoID;
        vbos.add(this);
    }

    private VertexBufferObject(float[] positions, float[] textureCoords, int[] indices, int usage) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, positions, usage);
        storeDataInAttributeList(1, textureCoords, usage);
        bindIndicesBuffer(indices);
        GL30.glBindVertexArray(0);
        this.vaoID = vaoID;
        this.vertexCount = indices.length;
        vbos.add(this);
    }

    public static VertexBufferObject create(float[] positions) {
        return new VertexBufferObject(positions, GL15.GL_STATIC_DRAW);
    }

    public static VertexBufferObject create(float[] positions, float[] textureCoords, int[] indices) {
        return new VertexBufferObject(positions, textureCoords, indices, GL15.GL_STATIC_DRAW);
    }

    public static VertexBufferObject createColored(float[] positions, float[] colors) {
        return new VertexBufferObject(positions, colors, false);
    }

    public static VertexBufferObject createShaded(float[] positions, float[] colors) {
        return new VertexBufferObject(positions, colors, true);
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

    public void renderColoredTriangleStream(float[] positions, float[] colors) {
        storeDataInAttributeList(vbosIDs.get(0), 0, positions, GL15.GL_STREAM_DRAW);
        storeColorInAttributeList(vbosIDs.get(1), 2, colors, GL15.GL_STREAM_DRAW);
        renderColoredTriangles(0, positions.length / 2);
    }

    public void renderShadedTriangleStream(float[] positions, float[] colors) {
        storeDataInAttributeList(vbosIDs.get(0), 0, positions, GL15.GL_STREAM_DRAW);
        storeShadeInAttributeList(vbosIDs.get(1), 2, colors, GL15.GL_STREAM_DRAW);
        renderColoredTriangles(0, positions.length / 2);
    }


    public void renderTriangleStripStream(float[] positions) {
        storeDataInAttributeList(vbosIDs.get(0), 0, positions, GL15.GL_STREAM_DRAW);
        render(0, positions.length / 2, GL11.GL_TRIANGLE_STRIP);
    }

    public void renderLineLoopStream(float[] positions) {
        storeDataInAttributeList(vbosIDs.get(0), 0, positions, GL15.GL_STREAM_DRAW);
        render(0, positions.length / 2, GL11.GL_LINE_LOOP);
    }

    public void renderTriangleFanStream(float[] positions) {
        storeDataInAttributeList(vbosIDs.get(0), 0, positions, GL15.GL_STREAM_DRAW);
        render(0, positions.length / 2, GL11.GL_TRIANGLE_FAN);
    }

    public void renderTriangleStream(float[] positions) {
        storeDataInAttributeList(vbosIDs.get(0), 0, positions, GL15.GL_STREAM_DRAW);
        render(0, positions.length / 2, GL11.GL_TRIANGLES);
    }

    public void updateVerticesStream(float[] positions) {
        storeDataInAttributeList(vbosIDs.get(0), 0, positions, GL15.GL_STREAM_DRAW);
    }

    public void updateVerticesAndTextureCoords(float[] positions, float[] textureCoords) {
        storeDataInAttributeList(vbosIDs.get(0), 0, positions, GL15.GL_STATIC_DRAW);
        storeDataInAttributeList(vbosIDs.get(1), 1, textureCoords, GL15.GL_STATIC_DRAW);
    }

    public void updateAll(float[] positions, float[] textureCoords, int[] indices) {
        storeDataInAttributeList(vbosIDs.get(0), 0, positions, GL15.GL_STATIC_DRAW);
        storeDataInAttributeList(vbosIDs.get(1), 1, textureCoords, GL15.GL_STATIC_DRAW);
        bindIndicesBuffer(vbosIDs.get(2), indices, GL15.GL_STATIC_DRAW);
    }

    public void updateIndices(int[] indices) {
        bindIndicesBuffer(vbosIDs.get(2), indices, GL15.GL_STATIC_DRAW);
    }

    public void updateIndicesStream(int[] indices) {
        bindIndicesBuffer(vbosIDs.get(2), indices, GL15.GL_STREAM_DRAW);
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

    private void storeColorInAttributeList(int attributeNumber, float[] data, int usage) {
        int vboID = GL15.glGenBuffers();
        vbosIDs.add(vboID);
        storeColorInAttributeList(vboID, attributeNumber, data, usage);
    }

    private void storeColorInAttributeList(int vboID, int attributeNumber, float[] data, int usage) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, usage);
        GL20.glVertexAttribPointer(attributeNumber, 3, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void storeShadeInAttributeList(int attributeNumber, float[] data, int usage) {
        int vboID = GL15.glGenBuffers();
        vbosIDs.add(vboID);
        storeShadeInAttributeList(vboID, attributeNumber, data, usage);
    }

    private void storeShadeInAttributeList(int vboID, int attributeNumber, float[] data, int usage) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, usage);
        GL20.glVertexAttribPointer(attributeNumber, 1, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void storeDataInAttributeList(int attributeNumber, float[] data, int usage) {
        int vboID = GL15.glGenBuffers();
        vbosIDs.add(vboID);
        storeDataInAttributeList(vboID, attributeNumber, data, usage);
    }

    private void storeDataInAttributeList(int vboID, int attributeNumber, float[] data, int usage) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, usage);
        GL20.glVertexAttribPointer(attributeNumber, 2, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbosIDs.add(vboID);
        bindIndicesBuffer(vboID, indices, GL15.GL_STATIC_DRAW);
    }

    private void bindIndicesBuffer(int vboID, int[] indices, int type) {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, type);
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

    public void renderTriangles(int start, int renderCount) {
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

    public void renderColoredTriangles(int start, int renderCount) {
        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(2);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, start, renderCount);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    public void renderTextured(int start, int renderCount) {
        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL11.glDrawElements(GL11.GL_TRIANGLE_STRIP, renderCount, GL11.GL_UNSIGNED_INT, start * 4L); // size of UNSIGNED_INT in BYTES
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    public void renderTexturedTriangles(int start, int renderCount) {
        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL11.glDrawElements(GL11.GL_TRIANGLES, renderCount, GL11.GL_UNSIGNED_INT, start * 4L); // size of UNSIGNED_INT in BYTES
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    public int getVertexCount() {
        return vertexCount;
    }

}
