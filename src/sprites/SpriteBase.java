/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Methods;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author Wojtek
 */
public class SpriteBase {

    private final ArrayList<Sprite> sprites = new ArrayList<>();
    private final double scale;

    public SpriteBase(double scale) {
        this.scale = scale;
    }

    public Sprite getSprite(String textureKey) {
        for (Sprite sprite : sprites) {
            if (sprite.getKey().equals(textureKey)) {
                return sprite;
            }
        }
        Sprite newSprite = loadSprite(textureKey);
        sprites.add(newSprite);
        return newSprite;
    }

    public SpriteSheet getSpriteSheet(String textureKey) {
        for (Sprite sprite : sprites) {
            if (sprite.getKey().equals(textureKey)) {
                return (SpriteSheet) sprite;
            }
        }
        SpriteSheet temp = (SpriteSheet) loadSprite(textureKey);
        sprites.add(temp);
        return temp;
    }

    public Sprite loadSprite(String name) {
        int width, height, startX, startY, pieceWidth, pieceHeight;
        boolean spriteSheet;
        String sprite, key;
        Texture texture;
        Sprite image;
        try (BufferedReader input = new BufferedReader(new FileReader("res/" + name + ".spr"))) {
            String line = input.readLine();
            String[] data = line.split(";");
            key = data[0];
            spriteSheet = data[1].equals("1");
            line = input.readLine();
            sprite = line;
            data = input.readLine().split(";");
            width = (int) (Integer.parseInt(data[0]) * scale);
            height = (int) (Integer.parseInt(data[1]) * scale);
            data = input.readLine().split(";");
            startX = (int) (Integer.parseInt(data[0]) * scale);
            startY = (int) (Integer.parseInt(data[1]) * scale);
            data = input.readLine().split(";");
            pieceWidth = Integer.parseInt(data[0]);
            pieceHeight = Integer.parseInt(data[1]);
            input.close();
        } catch (IOException e) {
            Methods.Error("File " + name + " not found!\n" + e.getMessage());
            return null;
        }
        try {
            texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream(sprite), GL_LINEAR);
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        if (spriteSheet) {
            image = new SpriteSheet(texture, pieceWidth, pieceHeight, startX, startY, this);
        } else {
            image = Sprite.create(texture, width, height, startX, startY, this);
        }
        image.setKey(key);
        return image;
    }

    public double getScale() {
        return scale;
    }
}
