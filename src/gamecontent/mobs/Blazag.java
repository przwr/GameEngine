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
public class Blazag extends Mob {

    private final Animation animation;

    public Blazag(int x, int y, Place place, short ID) {
        super(x, y, 1, 500, "Blazag", place, "blazag", true, ID);
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 29);
        appearance = animation;
        collision.setMobile(true);
        stats = new MobStats(this);
        stats.setStrength(10);
        stats.setDefence(1);
        stats.setWeight(50);
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
    animation.animateSingleInDirection(getDirection8Way(), 0); - siedzenie
    animation.animateSingleInDirection(getDirection8Way(), 1); - stanie
    animation.animateSingleInDirection(getDirection8Way(), 2); - zranienie
    animation.animateSingleInDirection(getDirection8Way(), 3); - blok
    
    animation.animateIntervalInDirection(getDirection8Way(), 4, 18); - bieg
    
    animation.animateSingleInDirection(getDirection8Way(), 19); - przygotowanie do skoku
    animation.animateSingleInDirection(getDirection8Way(), 20); - skok 1/2
    animation.animateSingleInDirection(getDirection8Way(), 21); - skok 2/2
    animation.animateSingleInDirection(getDirection8Way(), 22); - lądowanie
    
    animation.animateSingleInDirection(getDirection8Way(), 23); - skok z atakiem 1/2
    animation.animateSingleInDirection(getDirection8Way(), 24); - skok z atakiem 2/2
    animation.animateSingleInDirection(getDirection8Way(), 25); - atak po skoku
    
    animation.animateIntervalInDirectionOnce(getDirection8Way(), 26, 28); - ciachnięcie
    */
    
    @Override
    public void getHurt(int knockbackPower, double jumpPower, GameObject attacker) {
        super.getHurt(knockbackPower, jumpPower, attacker);
    }

    private void updateGettingHurt() {
        setDirection8way(Methods.pointAngle8Directions(knockback.getXSpeed(),
                knockback.getYSpeed(), 0, 0));
        animation.animateSingleInDirection(getDirection8Way(), 2);
        brake(2);
    }

    private void updateAnimation() {
        animation.animateSingleInDirection(getDirection8Way(), 1);
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
