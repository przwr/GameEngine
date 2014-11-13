/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

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
    
    //                                                                   ?
    //---PONIŻSZE METODY TRZEBA SPRÓBOWAĆ JAKOŚ WYCIACHAĆ, LUB COŚ... <(-.-)>
    
    public Sprite getSprite(String textureKey, int w, int h) {
        for (Sprite s : list) {
            if (s.getKey().equals(textureKey)) {
                s.setWidth(w);
                s.setHeight(h);
                return s;
            }
        }
        Sprite temp = new Sprite(textureKey, w, h, this);
        list.add(temp);
        temp.setId(texCounter++);
        return temp;
    }

    public Sprite getSprite(String textureKey, int w, int h, int sx, int sy) {
        for (Sprite s : list) {
            if (s.getKey().equals(textureKey)) {
                s.setWidth(w);
                s.setHeight(h);
                s.setSx(sx);
                s.setSy(sy);
                return s;
            }
        }
        Sprite temp = new Sprite(textureKey, w, h, sx, sy, this);
        list.add(temp);
        temp.setId(texCounter++);
        return temp;
    }
    
    public SpriteSheet getSpriteSheet(String textureKey, int sx, int sy) {
        for (Sprite s : list) {
            if (s.getKey().equals(textureKey)) {
                s.setWidth(sx);
                s.setHeight(sy);
                return (SpriteSheet) s;
            }
        }
        SpriteSheet temp = new SpriteSheet(textureKey, sx, sy, 64, 64, this);
        list.add(temp);
        temp.setId(texCounter++);
        return temp;
    }
    
    //----------------------------------------------------//
    
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
        } catch (IOException e) {
            System.err.println("File " + name + " not found!\n" + e.getMessage());
            return null;
        }
        try {
            tmp = TextureLoader.getTexture("png", ResourceLoader.getResourceAsStream(spr), GL_LINEAR);
        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        if (sprSheet) {
            lst = new SpriteSheet(tmp, width, height, w, h, sx, sy, this);
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
}
