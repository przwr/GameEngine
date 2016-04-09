package game.text.fonts;

import engine.utilities.Methods;
import game.Settings;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;
import sprites.vbo.VertexBufferObject;

public class TextPiece {

    private String textString;
    private float fontSize;

    private VertexBufferObject vbo;
    private Vector4f color = new Vector4f(1f, 1f, 1f, 1f);

    private float lineMaxSize;
    private int numberOfLines;

    private FontType font;

    private boolean centerText = false;
    private boolean visible = true;
    private int width = 0;

    public TextPiece(String text, float fontSize, FontType font, int maxLineLength, boolean centered) {
        this.textString = text;
        this.fontSize = fontSize;
        this.font = font;
        this.lineMaxSize = maxLineLength / (float) Display.getWidth();
        this.centerText = centered;
        TextMaster.loadText(this);
    }


    public void setLineMaxSize(int maxLineLength) {
        this.lineMaxSize = maxLineLength / (float) Display.getWidth();
    }

    public void destroy() {
        TextMaster.destroyText(this);
    }

    public FontType getFont() {
        return font;
    }

    public void setFont(FontType font) {
        if (font != this.font) {
            this.font = font;
            TextMaster.loadText(this);
        }
    }

    public void setColor(float r, float g, float b) {
        color.set(r, g, b, 1f);
    }

    public void setText(String text) {
        if (textString != text) {
            this.textString = text;
            TextMaster.loadText(this);
        }
    }

    public int getTextWidth(String string, float fontSize) {
        return font.getTextWidth(string, fontSize);
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Color newColor) {
        color.set(newColor.r, newColor.g, newColor.b, newColor.a);
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(int number) {
        this.numberOfLines = number;
    }

    public VertexBufferObject getMesh() {
        return vbo;
    }

    public void setMeshInfo(VertexBufferObject vbo) {
        this.vbo = vbo;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        if (fontSize != this.fontSize) {
            this.fontSize = fontSize;
            TextMaster.loadText(this);
        }
    }

    public int getHeight() {
        return Methods.roundDouble(fontSize * Settings.nativeScale * Settings.nativeScale / 0.75);
    }

    public boolean isCentered() {
        return centerText;
    }

    public float getMaxLineSize() {
        return lineMaxSize;
    }

    public String getTextString() {
        return textString;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
