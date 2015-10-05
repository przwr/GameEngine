/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.mobs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.*;
import game.gameobject.entities.Mob;
import game.gameobject.stats.MobStats;
import game.place.Place;
import sprites.Animation;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

/**
 * @author przemek
 */
public class BrainlessShen extends Mob {

    private final Animation animation;
    private Color color;
    private RandomGenerator rand;
    private boolean isHit;

    public BrainlessShen(int x, int y, Place place, short ID) {
        super(x, y, 1, 500, "Shen", place, "shen", true, ID);
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 15);
        appearance = animation;
        collision.setMobile(true);
        rand = RandomGenerator.create();
        color = new Color(rand.random(255), rand.random(255), rand.random(255));
        stats = new MobStats(this);
        stats.setStrength(10);
        stats.setDefence(3);
        stats.setWeight(70);
        stats.setMaxHealth(10000);
        stats.setHealth(10000);
    }

    @Override
    public void update() {
        if (isHurt()) {
            if (!isHit) {
                color.r = (float) rand.random(100) / 100;
                color.g = (float) rand.random(100) / 100;
                color.b = (float) rand.random(100) / 100;
                isHit = true;
            }
            updateGettingHurt();
        } else {
            isHit = false;
            updateAnimation();
        }
        updateChangers();
        updateWithGravity();
        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
        brakeOthers();
    }

    private void updateGettingHurt() {
        setDirection8way(Methods.pointAngle8Directions(knockback.getXSpeed(),
                knockback.getYSpeed(), 0, 0));
        animation.animateSingleInDirection(getDirection8Way(), 6);
        brake(2);
    }

    private void updateAnimation() {
        if (Math.abs(xSpeed) >= 0.1 || Math.abs(ySpeed) >= 0.1) {
            pastDirections[currentPastDirection++] = Methods.pointAngle8Directions(0, 0, xSpeed, ySpeed);
            if (currentPastDirection > 1) {
                currentPastDirection = 0;
            }
            if (pastDirections[0] == pastDirections[1]) {
                setDirection(pastDirections[0] * 45);
            }
            if (target == null) {
                animation.setFPS(7);
                animation.animateIntervalInDirection(getDirection8Way(), 0, 5);
            } else {
                animation.setFPS(30);
                animation.animateIntervalInDirection(getDirection8Way(), 12, 14);
            }
        } else {
            if (stats.isProtectionState()) {
                animation.setFPS(15);
                animation.animateIntervalInDirection(getDirection8Way(), 7, 12);
                animation.setStopAtEnd(true);
//                collision.setWidthAndHeight(32, 23);
            } else {
                animation.animateSingleInDirection(getDirection8Way(), 0);
            }
        }
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
            Drawer.setColor(color);
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
