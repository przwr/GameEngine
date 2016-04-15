package game.text.fonts;

import engine.utilities.Drawer;
import engine.view.Popup;
import game.Settings;
import game.text.effects.TextController;
import game.text.effects.TextRenderer;
import gamecontent.effects.DamageNumber;
import gamedesigner.GUIHandler;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector3f;
import sprites.shaders.ShaderProgram;
import sprites.vbo.VertexBufferObject;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_BINDING_2D;
import static org.lwjgl.opengl.GL11.glGetInteger;

public class TextMaster {

//    TODO UWAGA! Czcionki dobrze się skalują - są odpowiedniej grubości dla czcionek od 12 do 64

    public static ArrayList<FontType> fonts = new ArrayList<>(1);
    private static ArrayList<VertexBufferObject> vbos = new ArrayList<>();
    private static Vector3f color = new Vector3f(1f, 0f, 1f);

    public static void render(TextPiece text, int x, int y) {
        if (Drawer.fontShader != null) {
            if (text.isVisible()) {
                if (glGetInteger(GL_TEXTURE_BINDING_2D) != text.getFont().getTextureAtlasID()) {
                    GL13.glActiveTexture(GL13.GL_TEXTURE0);
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, text.getFont().getTextureAtlasID());
                }
                renderText(text, x, y, 0, text.getMesh().getVertexCount());
            }
        }
    }

    public static void renderOnce(TextPiece text, int x, int y) {
        startRenderText();
        render(text, x, y);
        endRenderText();
    }


    public static void renderFirstCharacters(TextPiece text, int x, int y, int firstCharacters) {
        if (Drawer.fontShader != null) {
            if (text.isVisible()) {
                if (glGetInteger(GL_TEXTURE_BINDING_2D) != text.getFont().getTextureAtlasID()) {
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, text.getFont().getTextureAtlasID());
                }
                renderText(text, x, y, 0, Math.min(firstCharacters * 6, text.getMesh().getVertexCount()));
            }
        }
    }

    public static void renderFirstCharactersOnce(TextPiece text, int x, int y, int firstCharacters) {
        startRenderText();
        renderFirstCharacters(text, x, y, firstCharacters);
        endRenderText();
    }

    public static void renderCharacters(TextPiece text, int x, int y, int from, int count) {
        if (Drawer.fontShader != null) {
            if (text.isVisible()) {
                if (glGetInteger(GL_TEXTURE_BINDING_2D) != text.getFont().getTextureAtlasID()) {
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, text.getFont().getTextureAtlasID());
                }
                renderText(text, x, y, from * 6, count * 6);
            }
        }
    }

    public static void renderCharactersOnce(TextPiece text, int x, int y, int from, int count) {
        startRenderText();
        renderCharacters(text, x, y, from, count);
        endRenderText();
    }

    public static void startRenderText() {
        if (Drawer.fontShader != null) {
            Drawer.fontShader.start();

        }
    }


    private static void renderText(TextPiece text, int x, int y, int start, int count) {
        if (text.getMesh().getVAOID() >= 0) {
            GL30.glBindVertexArray(text.getMesh().getVAOID());
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
            Drawer.fontShader.loadColor(text.getColor());
            float ff = text.getFontSize() * (float) Settings.nativeScale;
            float width = (-0.000145f * ff * ff + 0.016f * ff + 2f / ff);
            Drawer.fontShader.loadWidth(width);
            Drawer.fontShader.loadEdge(4f / ff);
            Drawer.fontShader.loadBorderWidth(text.hasBorder() ? width * 1.2f : text.hasShadow() ? width : 0);
            Drawer.fontShader.loadBorderEdge(5f / ff);
            Drawer.fontShader.loadOffset(text.hasShadow() ? 0.005f : 0, text.hasShadow() ? 0.005f : 0);
            Drawer.fontShader.loadBorderColor(text.getBorderColor());
            Drawer.fontShader.loadTranslation(x / (float) Display.getWidth(), y / (float) Display.getHeight());
            GL11.glDrawArrays(GL11.GL_TRIANGLES, start, count);
            GL20.glDisableVertexAttribArray(0);
            GL20.glDisableVertexAttribArray(1);
            GL30.glBindVertexArray(0);
        }
    }


    public static void endRenderText() {
        if (Drawer.regularShader != null) {
            Drawer.regularShader.start();
        } else {
            ShaderProgram.stop();
        }
    }

    public static void loadText(TextPiece text) {
        FontType font = text.getFont();
        if (font != null && font.loader != null) {
            TextMeshData data = font.loadText(text);
            if (text.getMesh() != null) {
                text.getMesh().updateVerticesAndTextureCoords(data.getVertexPositions(), data.getTextureCoords());
            } else {
                VertexBufferObject vbo = VertexBufferObject.createNoStore(data.getVertexPositions(), data.getTextureCoords());
                vbos.add(vbo);
                text.setMeshInfo(vbo);
            }
        }
    }

    public static void destroyText(TextPiece text) {
        VertexBufferObject vbo = text.getMesh();
        vbos.remove(vbo);
        vbo.clear();
    }

    public static void cleanUp() {
        for (VertexBufferObject vbo : vbos) {
            vbo.clear();
        }
        for (FontType font : fonts) {
            font.cleanUp();
        }
        vbos.clear();
        fonts.clear();
        if (Drawer.fontShader != null) {
            Drawer.fontShader.cleanUp();
        }
        if (DamageNumber.text != null) {
            DamageNumber.text.destroy();
            DamageNumber.text = null;
        }
        if (DamageNumber.text != null) {
            DamageNumber.text.destroy();
            DamageNumber.text = null;
        }
        if (Popup.text != null) {
            Popup.text.destroy();
            Popup.text = null;
        }
        if (Popup.title != null) {
            Popup.title.destroy();
            Popup.title = null;
        }
        if (TextController.choice != null) {
            TextController.choice.destroy();
            TextController.choice = null;
        }

        if (TextController.speakerName != null) {
            TextController.speakerName.destroy();
            TextController.speakerName = null;
        }
        if (TextRenderer.line != null) {
            TextRenderer.line.destroy();
            TextRenderer.line = null;
        }
        if (GUIHandler.text != null) {
            GUIHandler.text.destroy();
            GUIHandler.text = null;
        }
    }

    public static FontType getFont(String name) {
        for (FontType font : fonts) {
            if (font.getName() == name) {
                return font;
            }
        }
        FontType newFont = new FontType(name);
        fonts.add(newFont);
        return newFont;
    }
}
