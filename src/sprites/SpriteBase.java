/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Methods;
import game.Settings;
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

    public SpriteBase() {
    }

    public Sprite getSprite(String textureKey) {
        for (Sprite sprite : sprites) {
            if (sprite.getKey().equals(textureKey)) {
                return sprite;
            }
        }
        Sprite newSprite = loadSprite(textureKey);
        if (newSprite != null) {
            sprites.add(newSprite);
        }
        return newSprite;
    }

    public SpriteSheet getSpriteSheet(String textureKey) {
        for (Sprite sprite : sprites) {
            if (sprite.getKey().equals(textureKey)) {
                return (SpriteSheet) sprite;
            }
        }
        SpriteSheet temp = (SpriteSheet) loadSprite(textureKey);
        if (temp != null) {
            sprites.add(temp);
        }
        return temp;
    }

    private Sprite loadSprite(String name) {
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
            width = (int) (Integer.parseInt(data[0]));
            height = (int) (Integer.parseInt(data[1]));
            data = input.readLine().split(";");
            startX = (int) (Integer.parseInt(data[0]));
            startY = (int) (Integer.parseInt(data[1]));
            data = input.readLine().split(";");
            pieceWidth = Integer.parseInt(data[0]);
            pieceHeight = Integer.parseInt(data[1]);
            input.close();
        } catch (IOException e) {
            Methods.error("File " + name + " not found!\n" + e.getMessage());
            return null;
        }
        try {
            texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream(sprite), GL_LINEAR);
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        if (spriteSheet) {
            image = SpriteSheet.create(texture, pieceWidth, pieceHeight, startX, startY, this);
        } else {
            image = Sprite.create(texture, width, height, startX, startY, this);
        }
        image.setKey(key);
        return image;
    }

    public Sprite getSpriteInSize(String textureKey, int width, int height) {
        for (Sprite sprite : sprites) {
            if (sprite.getKey().equals(textureKey)
                    && (sprite.getWidth() == width)
                    && sprite.getHeight() == height) {
                return sprite;
            }
        }
        Sprite newSprite = loadSpriteInSize(textureKey, width, height);
        sprites.add(newSprite);
        return newSprite;
    }

    private Sprite loadSpriteInSize(String name, int width, int height) {
        String image, key;
        Texture texture;
        Sprite sprite;
        try (BufferedReader input = new BufferedReader(new FileReader("res/" + name + ".spr"))) {
            String line = input.readLine();
            String[] data = line.split(";");
            key = data[0];
            line = input.readLine();
            image = line;
            input.close();
        } catch (IOException e) {
            Methods.error("File " + name + " not found!\n" + e.getMessage());
            return null;
        }
        try {
            texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream(image), GL_LINEAR);
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        sprite = Sprite.create(texture, width, height, 0, 0, this);
        sprite.setKey(key);
        return sprite;
    }

    public SpriteSheet getSpriteSheetSetScale(String textureKey) {
        for (Sprite sprite : sprites) {
            if (sprite.getKey().equals(textureKey)
                    && (sprite.getWidth() == (int) (sprite.getTexture().getImageWidth() * Settings.nativeScale)
                    && sprite.getHeight() == (int) (sprite.getTexture().getImageHeight() * Settings.nativeScale))) {
                return (SpriteSheet) sprite;
            }
        }
        SpriteSheet temp = (SpriteSheet) loadSpriteSetScale(textureKey);
        sprites.add(temp);
        return temp;
    }

    public Sprite loadSpriteSetScale(String name) {
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
            width = (int) (Integer.parseInt(data[0]) * Settings.nativeScale);
            height = (int) (Integer.parseInt(data[1]) * Settings.nativeScale);
            data = input.readLine().split(";");
            startX = (int) (Integer.parseInt(data[0]) * Settings.nativeScale);
            startY = (int) (Integer.parseInt(data[1]) * Settings.nativeScale);
            data = input.readLine().split(";");
            pieceWidth = (int) (Integer.parseInt(data[0]) * Settings.nativeScale);
            pieceHeight = (int) (Integer.parseInt(data[1]) * Settings.nativeScale);
            input.close();
        } catch (IOException e) {
            Methods.error("File " + name + " not found!\n" + e.getMessage());
            return null;
        }
        try {
            texture = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream(sprite), GL_LINEAR);
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        if (spriteSheet) {
            image = SpriteSheet.createSetScale(texture, pieceWidth, pieceHeight, startX, startY, this);
        } else {
            image = Sprite.create(texture, width, height, startX, startY, this);
        }
        image.setKey(key);
        return image;
    }
}
