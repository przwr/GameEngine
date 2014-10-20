/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import java.util.ArrayList;

/**
 *
 * @author Wojtek
 */
public class SpriteBase {
    
    private int lastTex;
    private ArrayList<Sprite> list = new ArrayList<>();
    private int texCounter = 0;
    
    public Sprite getSprite(String textureKey, int sx, int sy) {
        for (Sprite s : list) {
            if (s.getKey().equals(textureKey))
                return s;
        }
        Sprite temp = new Sprite(textureKey, sx, sy, this);
        list.add(temp);
        temp.setId(texCounter++);
        return temp;
    }
    
    public SpriteSheet getSpriteSheet(String textureKey, int sx, int sy) {
        for (Sprite s : list) {
            if (s.getKey().equals(textureKey))
                return (SpriteSheet) s;
        }
        SpriteSheet temp = new SpriteSheet(textureKey, sx, sy, 64, 64, this);
        list.add(temp);
        temp.setId(texCounter++);
        return temp;
    }
    
    public int getLastTex() {
        return lastTex;
    }
    
    public void setLastTex(int i) {
        lastTex = i;
    }
}
