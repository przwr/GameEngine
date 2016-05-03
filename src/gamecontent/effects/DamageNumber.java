/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.effects;

import collision.Figure;
import engine.utilities.Delay;
import engine.utilities.Methods;
import engine.utilities.RandomGenerator;
import game.Settings;
import game.gameobject.entities.Entity;
import game.gameobject.entities.Player;
import game.place.Place;
import game.text.fonts.TextMaster;
import game.text.fonts.TextPiece;
import net.packets.Update;
import org.newdawn.slick.Color;
import sounds.Sound3D;

/**
 * @author Wojtek
 */
public class DamageNumber extends Entity {

    public static TextPiece text;
    private static int size = 20;
    private final Color color;
    private final int damage;
    private final Delay time;
    private Sound3D normalHit;
    public DamageNumber(int damage, int health, int x, int y, int height, Place place) {
        initialize("damage", x, y);
        initializeSounds();
        this.place = place;
        this.floatHeight = height;
        this.damage = damage;
        RandomGenerator rand = RandomGenerator.create();
        int direction = rand.random(360);
        int speed = Math.min(rand.randomInRange(damage / 20, damage / 10) + 1, 5);
        setUpForce(Math.min(rand.randomInRange(damage / 20, damage / 10) + 1, 2));
        setGravity(0.1);
        this.xSpeed = Methods.xRadius(direction, speed);
        this.ySpeed = -Methods.yRadius(direction, speed);
        time = Delay.createInMilliseconds((rand.random(5) + 5) * 100);
        time.start();
        onTop = true;
        setDirection(direction);
        float percent = (float) damage / health * 100;
        if (percent <= 1) {
            color = Color.lightGray;
            normalHit.setVolume(0.4f);
        } else if (percent <= 3) {
            color = Color.white;
            normalHit.setVolume(0.6f);
        } else if (percent <= 10) {
            color = Color.yellow;
            normalHit.setVolume(0.8f);
        } else if (percent <= 21) {
            color = Color.orange;
            normalHit.setVolume(0.9f);
        } else if (percent <= 50) {
            color = Color.red;
            normalHit.setVolume(0.95f);
        } else {
            color = Color.black;
            normalHit.setVolume(1f);
        }
        if (text == null) {
            text = new TextPiece("Menu", size, place.game.font, (int) (size * 5 * Settings.nativeScale), true);
        }
        normalHit.play();
    }

    public final void initializeSounds() {
        if (normalHit == null) {
            normalHit = Settings.sounds.get3DSoundEffect("slash.wav", this);
            normalHit.setSoundRanges(0.8f, 1.1f);
            normalHit.setRandomized(0.1f);
        }
    }

    @Override
    public void updateOnline() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void updateRest(Update update) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean isCollided(double xMagnitude, double yMagnitude) {
        return false;
    }

    @Override
    protected Player getCollided(double xMagnitude, double yMagnitude) {
        return null;
    }

    @Override
    public void update() {
        moveIfPossibleWithoutSliding(xSpeed + xEnvironmentalSpeed, ySpeed + yEnvironmentalSpeed);
        xSpeed /= 1.1;
        ySpeed /= 1.1;
        if (time.isOver()) {
            delete();
        }
        updateWithGravity();
    }

    @Override
    public void render() {
        if (text != null && text.getFont() != null) {
            text.setText("" + damage);
            text.setColor(color);
            TextMaster.renderOnce(text, (int) ((getX() - Place.currentCamera.getXStart()) * Place.getCurrentScale())
                    - (int) (size * 2.5 * Settings.nativeScale), (int) ((getY() - floatHeight - Place.currentCamera.getYStart()) * Place.getCurrentScale())
                    - (int) (size * Settings.nativeScale));
        }
    }

    @Override
    public void renderShadowLit(Figure figure) {
    }

    @Override
    public void renderShadow(Figure figure) {
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
    }

}
