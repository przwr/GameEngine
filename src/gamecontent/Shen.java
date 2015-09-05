/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Drawer;
import engine.Methods;
import game.gameobject.Mob;
import game.gameobject.MobStats;
import game.place.Place;
import navmeshpathfinding.PathFindingModule;
import org.newdawn.slick.Color;
import sprites.Animation;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class Shen extends Mob {

    private final Animation animation;
    private Color skinColor;

    public Shen(int x, int y, Place place, short ID) {
        super(x, y, 1, 500, "Shen", place, "shen", true, ID);
        setCollision(Rectangle.create(32, 23, OpticProperties.NO_SHADOW, this));
        animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 15);
        appearance = animation;
        //RandomGenerator r = RandomGenerator.create();
        //skinColor = Color.getHSBColor(r.nextFloat(), 1, 1);
        collision.setMobile(true);
        setPathStrategy(PathFindingModule.GET_CLOSE, 250);
        stats = new MobStats(this);
        stats.setStrength(1);
//        addInteractive(new Interactive(this, Interactive.ALWAYS, new LineInteractiveCollision(0, 64, -32, 40, 20), Interactive.HURT, 0.5f));
    }

    //animation.animateSingleInDirection(direction / 45, 6); - ANIMACJA ZRANIENIA!

    @Override
    public void update() {
        if (target != null && (!(target instanceof MyPlayer) || ((MyPlayer) target).isInGame())) {
            if (Methods.pointDistance(getX(), getY(), getTarget().getX(), getTarget().getY()) > range * 1.5 || getTarget().getMap() != map) {
                target = null;
                pathData.clearPath();
            } else {
                chase(target);
            }
        } else {
            look(place.players);
            brake(2);
        }
        animation.setStopAtEnd(false);
        if (xSpeed != 0 || ySpeed != 0) {
            direction = (int) Methods.pointAngleCounterClockwise(0, 0, (int) xSpeed, (int) ySpeed);
            animation.setFPS(7);
            animation.animateIntervalInDirection(direction / 45, 0, 5);
            stats.setProtectionState(false);
        } else {
            if (target == null) {
                animation.animateSingleInDirection(direction / 45, 0);
            } else {
                animation.setFPS(15);
                animation.setStopAtEnd(true);
                animation.animateIntervalInDirection(direction / 45, 7, 12);
                stats.setProtectionState(true);
            }
        }
        moveWithSliding(xEnvironmentalSpeed + xSpeed, yEnvironmentalSpeed + ySpeed);
        brakeOthers();
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
            appearance.render();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
        }
        if (map != null) {
            Drawer.renderString(name, 0, (int) -((animation.getHeight() * Place.getCurrentScale()) / 2), place.standardFont,
                    map.getLightColor());
        }
        glPopMatrix();
    }
}
