/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.mobs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.*;
import game.gameobject.GameObject;
import game.gameobject.entities.Mob;
import game.gameobject.stats.MobStats;
import game.place.Place;
import sprites.Animation;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Tongub extends Mob {

    private final Animation animation;

    public Tongub(int x, int y, Place place, short ID) {
        super(x, y, 1, 500, "Tongub", place, "tongub", true, ID);
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 23);
        appearance = animation;
        collision.setMobile(true);
        stats = new MobStats(this);
        stats.setStrength(10);
        stats.setDefence(1);
        stats.setWeight(20);
        stats.setMaxHealth(10000);
        stats.setHealth(10000);
    }

    @Override
    public void update() {
        if (isHurt()) {
            updateGettingHurt();
        } else {
            updateAnimation();
        }
        updateChangers();
        updateWithGravity();
        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
        brakeOthers();
    }

    /*
     <('-^<) PRZEMKO-SCIAGA (>^-')>
     animation.animateSingleInDirection(getDirection8Way(), 0); - poza neutralna
     animation.animateSingleInDirection(getDirection8Way(), 1); - zranienie
    
     ~30fps?
     animation.animateIntervalInDirection(getDirection8Way(), 2, 6); - chód
    
     animation.animateIntervalInDirection(getDirection8Way(), 7, 10); - atak
    
     animation.animateIntervalInDirection(getDirection8Way(), 11, 16); - wkopanie
     animation.animateIntervalInDirection(getDirection8Way(), 17, 22); - wykopanie
     */
    @Override
    public void getHurt(int knockbackPower, double jumpPower, GameObject attacker) {
        super.getHurt(knockbackPower, jumpPower, attacker);
    }

    private void updateGettingHurt() {
        setDirection8way(Methods.pointAngle8Directions(knockback.getXSpeed(),
                knockback.getYSpeed(), 0, 0));
        animation.animateSingleInDirection(getDirection8Way(), 1);
        brake(2);
    }

    private void updateAnimation() {
        animation.animateSingleInDirection(getDirection8Way(), 0);
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), getY(), 0);
            //Drawer.setColor(skinColor);
            animation.updateFrame();
            Drawer.setColor(JUMP_SHADOW_COLOR);
            Drawer.drawEllipse(0, 0, Methods.roundDouble((float) collision.getWidth() / 2), Methods.roundDouble((float) collision.getHeight() / 2), 15);
            Drawer.refreshColor();
            glTranslatef(0, (int) -floatHeight, 0);
            appearance.render();
            Drawer.refreshColor();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            /*if (map != null) {
             Drawer.renderString(name, 0, (int) -((animation.getHeight() * Place.getCurrentScale()) / 2), place.standardFont, map.getLightColor());
             }*/
            glPopMatrix();

//          renderPathPoints(xEffect, yEffect);
        }
    }
}
