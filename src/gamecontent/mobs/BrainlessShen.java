/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.mobs;

import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.RandomGenerator;
import game.gameobject.GameObject;
import game.gameobject.entities.Mob;
import game.gameobject.stats.MobStats;
import game.place.Place;
import org.newdawn.slick.Color;
import sprites.Animation;
import sprites.SpriteSheet;

import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * @author przemek
 */
public class BrainlessShen extends Mob {

    private Animation animation;
    private Color color;
    private RandomGenerator rand;
    private float colorHue;

    public BrainlessShen() {
    }

    public BrainlessShen(int x, int y, Place place, short ID) {
        super(x, y, 1, 500, "Shen", place, "shen", true, ID);
        setUp();
    }

    private void setUp() {
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 15);
        appearance = animation;
        collision.setMobile(true);
        rand = RandomGenerator.create();
        colorHue = rand.random(360);
        color = Methods.createHSVColor(colorHue, 1, 1);
        stats = new MobStats(this);
        stats.setStrength(10);
        stats.setDefence(3);
        stats.setWeight(70);
        stats.setMaxHealth(10000);
        stats.setHealth(10000);
    }

    @Override
    public void initialize(int x, int y, Place place, short ID) {
        super.initialize(x, y, 1, 500, "Shen", place, "shen", true, ID);
        setUp();
    }

    @Override
    public void update() {
        animation.updateFrame();
        if (animation.isUpToDate()) {
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
    }

    @Override
    public void getHurt(int knockBackPower, double jumpPower, GameObject attacker) {
        super.getHurt(knockBackPower, jumpPower, attacker);
        colorHue += knockBackPower + 1;
        Methods.changeColorWithHSV(color, colorHue, 1, 1);
    }

    private void updateGettingHurt() {
        setDirection8way(Methods.pointAngle8Directions(knockBack.getXSpeed(), knockBack.getYSpeed(), 0, 0));
        animation.animateSingleInDirection(getDirection8Way(), 6);
        brake(2);
    }

    private void updateAnimation() {
        animation.animateSingleInDirection(getDirection8Way(), 0);
    }


    @Override
    public void render() {
        if (appearance != null) {
            glTranslatef(getX(), getY() - (int) floatHeight, 0);
            Drawer.setColorStatic(color);
            appearance.render();
            if (map != null) {
                glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
                Drawer.renderStringCentered(name, (int) ((collision.getWidth() * Place.getCurrentScale()) / 2),
                        (int) ((collision.getHeight() * Place.getCurrentScale()) / 2), place.standardFont,
                        Drawer.getCurrentColor());
            }
//          renderPathPoints(xEffect, yEffect);
            glTranslatef(-getX(), -(int) (getY() - floatHeight), 0);
        }
    }
}
