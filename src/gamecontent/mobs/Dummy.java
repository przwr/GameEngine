/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.mobs;

import collision.Block;
import collision.OpticProperties;
import collision.Rectangle;
import engine.utilities.Drawer;
import engine.utilities.Methods;
import engine.utilities.RandomGenerator;
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
public class Dummy extends Mob {

    private Animation animation;
    private RandomGenerator rand;
    private int power;

    public Dummy() {
    }

    public Dummy(int x, int y, Place place, short ID) {
        super(x, y, 1, 500, "Dummy", place, "dummy", true, ID);
        setUp();
    }

    private void setUp() {
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        animation = Animation.createDirectionalAnimation((SpriteSheet) appearance, 0, 15);
        appearance = animation;
        collision.setMobile(true);
        rand = RandomGenerator.create();
        stats = new MobStats(this);
        stats.setStrength(10);
        stats.setDefence(1);
        stats.setMaxHealth(10000);
        stats.setHealth(10000);
    }

    @Override
    public void initialize(int x, int y, Place place, short ID) {
        super.initialize(x, y, 1, 500, "Dummy", place, "dummy", true, ID);
        setUp();
    }

    @Override
    public void update() {
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
        knockBack.setFrames(30);
        knockBack.setAttackerDirection(attacker.getDirection());
        int attackerX = attacker.getX() + (attacker instanceof Block ? attacker.getCollision().getWidthHalf() : 0);
        int attackerY = attacker.getY() + (attacker instanceof Block ? attacker.getCollision().getHeightHalf() : 0);
        setDirection8way(Methods.pointAngle8Directions(attackerX, attackerY, x, y));
        knockBack.setSpeed(0, 0);
        knockBack.start();
        power = knockBackPower;
        addChanger(knockBack);
        stats.setHealth(10000);
    }

    private void updateGettingHurt() {
        animation.animateSingle(1);
        brake(2);
    }

    private void updateAnimation() {
        animation.animateSingle(0);
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (appearance != null) {
            glPushMatrix();
            glTranslatef(xEffect, yEffect, 0);
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            glTranslatef(getX(), getY(), 0);
            animation.updateFrame();
            Drawer.setColor(JUMP_SHADOW_COLOR);
            Drawer.drawEllipse(0, 0, Methods.roundDouble((float) collision.getWidthHalf()), Methods.roundDouble((float) collision.getHeightHalf()), 15);
            Drawer.refreshColor();
            glTranslatef(0, (int) -floatHeight, 0);
            if (isHurt()) {
                glTranslatef(rand.randomInRange(-power, power), rand.randomInRange(-power, power), 0);
                power /= 1.2;
            }
            appearance.render();
            glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
            glPopMatrix();
        }
    }
}
