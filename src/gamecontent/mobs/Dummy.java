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

/**
 * @author przemek
 */
public class Dummy extends Mob {

    private Animation animation;
    private RandomGenerator rand;
    private int power;
    private float lastRandomX, lastRandomY;

    public Dummy() {
    }

    public Dummy(int x, int y, Place place, short ID) {
        super(x, y, 1, 500, "Dummy", place, "dummy", true, ID);
        setUp();
    }

    private void setUp() {
        setCollision(Rectangle.create(48, 34, OpticProperties.NO_SHADOW, this));
        animation = Animation.createSimpleAnimation((SpriteSheet) appearance, 1);
        appearance = animation;
        collision.setMobile(true);
        hasStaticShadow = true;
        rand = RandomGenerator.create();
        stats = new MobStats(this);
        stats.setStrength(10);
        stats.setDefence(2);
        stats.setStartHealth(1000);
        setTargetable(false);
    }

    @Override
    public void initialize(int x, int y, Place place, short ID) {
        super.initialize(x, y, 1, 500, "Dummy", place, "dummy", true, ID);
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
        knockBack.setAttackerDirection(attacker.getDirection());
        int attackerX = attacker.getX() + (attacker instanceof Block ? attacker.getCollision().getWidthHalf() : 0);
        int attackerY = attacker.getY() + (attacker instanceof Block ? attacker.getCollision().getHeightHalf() : 0);
        setDirection8way(Methods.pointAngle8Directions(attackerX, attackerY, x, y));
        knockBack.setSpeed(0, 0);
        knockBack.start();
        power = knockBackPower;
        addChanger(knockBack);
        stats.setHealth(stats.getMaxHealth());
    }

    private void updateGettingHurt() {
        animation.animateSingle(1);
        brake(2);
    }

    private void updateAnimation() {
        animation.animateSingle(0);
    }

    @Override
    public void render() {
        if (appearance != null) {
            Drawer.regularShader.translate(getX(), (int) (getY() - floatHeight));
            if (isHurt()) {
                lastRandomX = rand.randomInRange(-power, power);
                lastRandomY = rand.randomInRange(-power, power);
                power /= 1.2;
            } else {
                lastRandomX = lastRandomY = 0;
            }
            appearance.render();
        }
    }

    public int getX() {
        return (int) (x + lastRandomX);
    }

    public int getY() {
        return (int) (y + lastRandomY);
    }
}
