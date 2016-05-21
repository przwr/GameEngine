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

/**
 * @author przemek
 */
public class BrainlessShen extends Mob {

    private Color color;
    private RandomGenerator rand;
    private float colorHue;

    public BrainlessShen() {
    }

    public BrainlessShen(int x, int y, Place place) {
        super(x, y, 1, 500, "Shen", place, "shen", true, place.getNextMobID());
        setUp();
    }

    private void setUp() {
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        setUpDirectionalAnimation(0, 17);
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
    public void initialize(int x, int y, Place place) {
        super.initialize(x, y, 1, 500, "Shen", place, "shen", true, place.getNextMobID());
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
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            Drawer.setColorStatic(color);
            appearance.render();
//          renderPathPoints(xEffect, yEffect);
        }
    }
}
