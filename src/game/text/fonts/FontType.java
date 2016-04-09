package game.text.fonts;

import engine.utilities.ErrorHandler;
import game.ScreenPlace;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;


public class FontType {

    protected TextMeshCreator loader;
    private Texture textureAtlas;
    private String name;

    public FontType(String font) {
        this.textureAtlas = loadFontTexture(font);
        this.loader = new TextMeshCreator(new File("res/fonts/" + font + ".fnt"), new File("res/fonts/" + font + "O.fnt"));
        this.name = font;
    }

    private static Texture loadFontTexture(String font) {
        Texture texture = null;
        try {
            InputStream stream = ResourceLoader.getResourceAsStream("/res/fonts/" + font + ".png");
            texture = TextureLoader.getTexture("png", stream, GL_NEAREST);
            stream.close();
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        } catch (Exception exception) {
            Logger.getLogger(ScreenPlace.class.getName()).log(Level.SEVERE, null, exception);
            ErrorHandler.javaError(exception.getMessage());
        }
        return texture;
    }

    public int getTextureAtlasID() {
        return textureAtlas.getTextureID();
    }

    public TextMeshData loadText(TextPiece text) {
        return loader.createTextMesh(text);
    }

    public int getTextWidth(String string, float fontSize) {
        return loader.getTextWidth(string, fontSize);
    }

    public void cleanUp() {
        if (textureAtlas != null) {
            if (glGetInteger(GL_TEXTURE_BINDING_2D) == textureAtlas.getTextureID()) {
                glBindTexture(GL_TEXTURE_2D, 0);
            }
            if (textureAtlas.getTextureID() != 0) {
                glDeleteTextures(textureAtlas.getTextureID());
            }
            textureAtlas.release();
            textureAtlas = null;
        }
        loader = null;
    }

    public String getName() {
        return name;
    }
}
