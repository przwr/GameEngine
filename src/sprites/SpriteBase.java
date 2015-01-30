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

    private int lastTex;
    private final ArrayList<Sprite> list = new ArrayList<>();
    private int texCounter = 0;
    private final double scale;

    public SpriteBase(double s) {
        scale = s;
    }

    public void reset() {
        Sprite t = getSprite("apple");
        if (t != null) {
            t.bindCheck();
        }
    }
    
    public Sprite getSprite(String textureKey) {
        for (Sprite s : list) {
            if (s.getKey().equals(textureKey)) {
                return s;
            }
        }
        Sprite temp = loadSprite(textureKey);
        list.add(temp);
        temp.setId(texCounter++);
        return temp;
    }

    public SpriteSheet getSpriteSheet(String textureKey) {
        for (Sprite s : list) {
            if (s.getKey().equals(textureKey)) {
                return (SpriteSheet) s;
            }
        }
        SpriteSheet temp = (SpriteSheet) loadSprite(textureKey);
        list.add(temp);
        temp.setId(texCounter++);
        return temp;
    }

    public Sprite loadSprite(String name) {
        int width, height, sx, sy, w, h;
        boolean sprSheet;
        String spr, key;
        Texture tmp;
        Sprite lst;
        try (BufferedReader wczyt = new BufferedReader(new FileReader("res/" + name + ".spr"))) {
            String line = wczyt.readLine();
            String[] t = line.split(";");
            key = t[0];
            sprSheet = t[1].equals("1");

            line = wczyt.readLine();
            spr = line;

            t = wczyt.readLine().split(";");
            width = (int) (Integer.parseInt(t[0]) * scale);
            height = (int) (Integer.parseInt(t[1]) * scale);

            t = wczyt.readLine().split(";");
            sx = (int) (Integer.parseInt(t[0]) * scale);
            sy = (int) (Integer.parseInt(t[1]) * scale);

            t = wczyt.readLine().split(";");
            w = Integer.parseInt(t[0]);
            h = Integer.parseInt(t[1]);
            wczyt.close();
        } catch (IOException e) {
            Methods.Error("File " + name + " not found!\n" + e.getMessage());
            return null;
        }
        try {
            tmp = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream(spr), GL_LINEAR);
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        if (sprSheet) {
            lst = new SpriteSheet(tmp, w, h, sx, sy, this);
        } else {
            lst = new Sprite(tmp, width, height, sx, sy, this);
        }
        lst.setKey(key);
        return lst;
    }

    public int getLastTex() {
        return lastTex;
    }

    public void setLastTex(int i) {
        lastTex = i;
    }

    public double getScale() {
        return scale;
    }
}
