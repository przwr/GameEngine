/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import engine.utilities.Drawer;
import game.gameobject.GUIObject;
import game.place.Place;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Wojtek
 */
public class MyGUI extends GUIObject {

    private final SpriteSheet attackIcons;
    private final Color color;
    private int attackType;
    private float alpha;

    public MyGUI(String name, Place place) {
        super(name, place);
        color = new Color(Color.white);
        alpha = 0f;
        attackType = 0;
        attackIcons = place.getSpriteSheet("attackIcons", "");
    }

    public void changeAttackIcon(int index) {
        attackType = index;
        alpha = 5f;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (alpha > 0) {
            int tile = Place.tileSize;
            glPushMatrix();
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getRelativePlayersX() - 2f * tile, getRelativePlayersY() - tile, 0);
            color.a = alpha;
            alpha -= 0.06f;
            Drawer.setColor(color);
            attackIcons.renderPiece(attackType);
            Drawer.refreshColor();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            glPopMatrix();
        }
    }

}
