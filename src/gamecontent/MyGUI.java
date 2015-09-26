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
 * @author Wojtek
 */
public class MyGUI extends GUIObject {

    private final SpriteSheet attackIcons;
    private final Color color;
    private int firstAttackType, secondAttackType;
    private float alpha;
    private int emptySlot;

    public MyGUI(String name, Place place) {
        super(name, place);
        color = new Color(Color.white);
        alpha = 0f;
        emptySlot = 5;
        firstAttackType = emptySlot;
        secondAttackType = emptySlot;
        attackIcons = place.getSpriteSheet("attackIcons", "");
    }

    public void changeAttackIcon(int first, int second) {
        if (first < 0) {
            first = emptySlot;
        }
        if (second < 0) {
            second = emptySlot;
        }
        firstAttackType = first;
        secondAttackType = second;
        alpha = 5f;
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (alpha > 0) {
            glPushMatrix();
            glTranslatef((int) ((player.getX()) * Place.getCurrentScale() + xEffect), (int) ((player.getY() - player.getFloatHeight()) * Place.getCurrentScale() + yEffect), 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);

            color.a = alpha;
            alpha -= 0.06f;
            Drawer.setColor(color);
            glTranslatef(-2 * Place.tileSize, -Place.tileSize, 0);
            attackIcons.renderPiece(firstAttackType);
            glTranslatef(0, -Place.tileSize, 0);
            attackIcons.renderPiece(secondAttackType);

            Drawer.refreshColor();
            glPopMatrix();
        }
    }

}
