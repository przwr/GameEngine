/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.Main;
import engine.utilities.ErrorHandler;
import engine.utilities.Point;
import engine.utilities.PointedValue;
import game.Settings;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_NEAREST;

/**
 * @author Wojtek
 */
public class SpriteBase {

    //    private static final int GL_MODE = org.lwjgl.opengl.GL11.GL_NEAREST;
    private final Map<String, Sprite> sprites = new HashMap<>();

    public SpriteBase() {
    }

    public static String getSpritePath(File f) {    // C:/...<('-'<).../res/textures/folder/podfolder/sprite.spr -> folder/podfolder
        String sep = File.pathSeparator.equals("/") ? "\\/" : "\\\\";
        String path = f.getPath().replaceAll(".*textures" + sep + "|" + sep + "[^" + File.pathSeparator + "]*$", "");
        if (path.endsWith(f.getName())) {
            path = path.substring(0, path.length() - f.getName().length());
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static String fullFolderPath(String folder) {
        if (folder.isEmpty()) {
            return "res/textures/";
        } else {
            if (folder.startsWith("res/textures")) {
                return folder + (folder.endsWith("/") ? "" : "/");
            } else {
                return "res/textures/" + folder + (folder.endsWith("/") ? "" : "/");
            }
        }
    }

    public Sprite getSprite(String textureKey, String folder, boolean... now) {
        Sprite sprite = sprites.get(folder + textureKey);
        if (sprite != null) {
            loadTextureIfRequired(now, sprite);
            return sprite;
        }
        Sprite newSprite = loadSprite(textureKey, folder, now);
        if (newSprite != null) {
            sprites.put(folder + textureKey, newSprite);
        }
        return newSprite;
    }

    public SpriteSheet getSpriteSheet(String textureKey, String folder, boolean... now) {
        Sprite sprite = sprites.get(folder + textureKey);
        if (sprite != null) {
            loadTextureIfRequired(now, sprite);
            return (SpriteSheet) sprite;
        }
        SpriteSheet temp = (SpriteSheet) loadSprite(textureKey, folder, now);
        if (temp != null) {
            sprites.put(folder + textureKey, temp);
        }
        return temp;
    }

    public Point[] getStartPointFromFile(String folder) { //[startingPoint, deltaPoint]
        int startX, startY;
        int deltaX, deltaY;
        try (BufferedReader input = new BufferedReader(
                new FileReader(fullFolderPath(folder) + "dims.txt"))) {

            String[] data = input.readLine().split(";");
            int wholeX = Integer.parseInt(data[0]);
            int wholeY = Integer.parseInt(data[1]);

            data = input.readLine().split(";");
            deltaX = wholeX - Integer.parseInt(data[0]);
            deltaY = wholeY - Integer.parseInt(data[1]);

            data = input.readLine().split(";");
            startX = Integer.parseInt(data[0]);
            startY = Integer.parseInt(data[1]);

            input.close();
        } catch (IOException e) {
            ErrorHandler.error("File " + folder + File.pathSeparator + "dims.txt not found!\n" + e.getMessage());
            return null;
        }
        return new Point[]{new Point(startX, startY), new Point(deltaX, deltaY)};
    }

    private Sprite loadSprite(String name, String folder, boolean... now) {
        int width, height, startX, startY, pieceWidth, pieceHeight, xOffset, yOffset, actualWidth, actualHeight;
        boolean spriteSheet, movingStart = false;
        PointedValue[] startPoints = null;
        String image, key, path;
        Sprite sprite;
        folder = fullFolderPath(folder);
        try (BufferedReader input = new BufferedReader(new FileReader(folder + name + ".spr"))) {
            String line = input.readLine();
            String[] data = line.split(";");
            key = data[0];
            spriteSheet = data[1].equals("1");
            line = input.readLine();
            image = line;

            image = image.replace("\\", File.separator);
            image = image.replace("/", File.separator);

            data = input.readLine().split(";");
            width = Integer.parseInt(data[0]);
            height = Integer.parseInt(data[1]);

            data = input.readLine().split(";");
            startX = Integer.parseInt(data[0]);
            startY = Integer.parseInt(data[1]);

            data = input.readLine().split(";");
            pieceWidth = Integer.parseInt(data[0]);
            pieceHeight = Integer.parseInt(data[1]);

            data = input.readLine().split(";");
            xOffset = Integer.parseInt(data[0]);
            yOffset = Integer.parseInt(data[1]);
            actualWidth = Integer.parseInt(data[2]);
            actualHeight = Integer.parseInt(data[3]);

            if ((line = input.readLine()) != null) {
                movingStart = true;
                startPoints = new PointedValue[Integer.parseInt(line)];
                int i = 0;
                int n = 0;
                PointedValue lastOne = null;
                while ((line = input.readLine()) != null) {
                    data = line.split(";");
                    switch (data[0]) {
                        /*case "r":
                         int j;
                         for (j = i; j < i + Integer.parseInt(data[1]); j++) {
                         startPoints[j] = lastOne;
                         }
                         i = j - 1;
                         break;
                         case "s":
                         startPoints[i] = startPoints[Integer.parseInt(data[1])];
                         break;*/
                        case "n":
                            startPoints[i] = null;
                            break;
                        default:
                            lastOne = new PointedValue(Integer.parseInt(data[0]), Integer.parseInt(data[1]), n);
                            startPoints[i] = lastOne;
                            n++;
                            break;
                    }
                    i++;
                }
            }
            input.close();
        } catch (IOException e) {
            ErrorHandler.error("File " + folder + name + " not found!\n" + e.getMessage());
            return null;
        }
        path = folder + image;
        if (spriteSheet) {
            if (movingStart) {
                sprite = SpriteSheet.createWithMovingStart(path, folder, pieceWidth, pieceHeight, startX, startY, this, startPoints);
            } else {
                sprite = SpriteSheet.create(path, folder, pieceWidth, pieceHeight, startX, startY, this);
            }
        } else {
            sprite = Sprite.create(path, folder, width, height, startX, startY, this);
        }
        sprite.setKey(key);
        sprite.xOffset = xOffset;
        sprite.yOffset = yOffset;
        sprite.actualWidth = actualWidth;
        sprite.actualHeight = actualHeight;
        if (now.length > 0 && now[0]) {
            loadTextureIfRequired(now, sprite);
        } else {
            Main.backgroundLoader.requestSprite(sprite);
        }
        return sprite;
    }

    public Sprite getSpriteInSize(String textureKey, String folder, int width, int height, boolean... now) {
        Sprite sprite = sprites.get(folder + textureKey);
        if (sprite != null) {
            if (sprite.getWidth() == width && sprite.getHeight() == height) {
                loadTextureIfRequired(now, sprite);
                return sprite;
            }
        }
        Sprite newSprite = loadSpriteInSize(textureKey, folder, width, height, now);
        if (newSprite != null) {
            sprites.put(folder + textureKey, newSprite);
        }
        return newSprite;
    }

    private Sprite loadSpriteInSize(String name, String folder, int width, int height, boolean... now) {
        String image, key, path;
        Sprite sprite;
        folder = fullFolderPath(folder);
        try (BufferedReader input = new BufferedReader(new FileReader(folder + name + ".spr"))) {
            String line = input.readLine();
            String[] data = line.split(";");
            key = data[0];
            line = input.readLine();
            image = line;
            input.close();
        } catch (IOException e) {
            ErrorHandler.error("File " + name + " not found!\n" + e.getMessage());
            return null;
        }
        path = folder + image;
        sprite = Sprite.create(path, folder, width, height, 0, 0, this);
        sprite.setKey(key);
        if (now.length > 0 && now[0]) {
            loadTextureIfRequired(now, sprite);
        } else {
            Main.backgroundLoader.requestSprite(sprite);
        }
        return sprite;
    }

    public SpriteSheet getSpriteSheetSetScale(String textureKey, String folder, boolean... now) {
        Sprite sprite = sprites.get(folder + textureKey);
        if (sprite != null) {
            if (sprite.getKey().equals(textureKey)
                    && (sprite.getWidth() == (int) (sprite.getWidthWhole() * Settings.nativeScale)
                    && sprite.getHeight() == (int) (sprite.getHeightWhole() * Settings.nativeScale))) {
                loadTextureIfRequired(now, sprite);
                return (SpriteSheet) sprite;
            }
        }
        SpriteSheet temp = (SpriteSheet) loadSpriteSetScale(textureKey, folder, now);
        if (temp != null) {
            sprites.put(folder + textureKey, temp);
        }
        return temp;
    }

    private Sprite loadSpriteSetScale(String name, String folder, boolean... now) {
        int width, height, startX, startY, pieceWidth, pieceHeight;
        boolean spriteSheet;
        String image, key, path;
        Sprite sprite;
        folder = fullFolderPath(folder);
        try (BufferedReader input = new BufferedReader(new FileReader(folder + name + ".spr"))) {
            String line = input.readLine();
            String[] data = line.split(";");
            key = data[0];
            spriteSheet = data[1].equals("1");
            line = input.readLine();
            image = line;
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
            ErrorHandler.error("File " + name + " not found!\n" + e.getMessage());
            return null;
        }
        path = folder + image;
        if (spriteSheet) {
            sprite = SpriteSheet.createSetScale(path, folder, pieceWidth, pieceHeight, startX, startY, this);
        } else {
            sprite = Sprite.create(path, folder, width, height, startX, startY, this);
        }
        sprite.setKey(key);
        if (now.length > 0 && now[0]) {
            loadTextureIfRequired(now, sprite);
        } else {
            Main.backgroundLoader.requestSprite(sprite);
        }
        return sprite;
    }

    private void loadTextureIfRequired(boolean[] now, Sprite sprite) {
        if (now.length > 0 && now[0] && sprite.getTextureID() == 0) {
            Texture tex = null;
            try {
                tex = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream(sprite.getPath()), GL_NEAREST);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sprite.setTexture(tex);
            Main.backgroundLoader.notifySprite(sprite);
        }
    }

    public Map<String, Sprite> getSprites() {
        return sprites;
    }
}
