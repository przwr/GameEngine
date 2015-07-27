/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.PixelPerfectCollision;
import collision.Rectangle;
import engine.Drawer;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import sprites.Sprite;

/**
 *
 * @author przemek
 */
public class Weapon {

    public GameObject owner;
    int bottom, top; // granice broni od podłoża (np 10 pixeli od ziemii najniższy punkt - bottom, najwyższy - top)
    Sprite sprite;
    Rectangle collision = Rectangle.createTileRectangle(64, 20);

    public Weapon(GameObject owner, Sprite sprite) {
        this.owner = owner;
        this.sprite = sprite;
    }

    public void updatePosition() {

    }

    public void render() {
        Drawer.setCentralPoint();

        Drawer.setColor(Color.black);
        //Przejście do miejsca rozpoczęcia się kolizji właściciela
        GL11.glTranslatef(-owner.getX() - owner.getSprite().getXStart(), -owner.getY() - owner.getSprite().getYStart(), 0);
        Drawer.drawRectangleInBlack(collision.getXStart(), collision.getYStart(), collision.getWidth(), collision.getHeight());
        Drawer.refreshColor();

        GL11.glTranslatef(0, -sprite.getHeight() + collision.getHeight() / 2, 0);
        Drawer.drawRectangleBorder(0, 0, sprite.getWidth(), sprite.getHeight());

        sprite.render();

        Drawer.returnToCentralPoint();
    }

    public void checkCollision(GameObject[] players) {
        for (GameObject object : players) {
            if (object != null) {
                isCollideThisWeapon((Player) object);
            }
        }
    }

    public void isCollideThisWeapon(Player player) {
        if (player.isInGame()) {
            collision.setXStart(owner.getCollision().getXEnd());
            collision.setYStart(owner.getCollision().getY());
            if (collision.isCollideSingle(0, 0, player.getCollision())) { // Sprawdzenie, czy rzut z góry - prostokąt broni koliduje z graczem.        
                if (PixelPerfectCollision.isColliding(player, this)) {
//                    System.out.println("Ałć " + System.nanoTime());
                }
            }

        }
    }

    public int getXSpriteBegin() { // TO DO - zrobić porządnie!
        return owner.getXEnd();
    }

    public int getXSpriteEnd() {
        return owner.getXEnd() + sprite.getWidth();
    }

    public int getYSpriteBegin() {
        return owner.getYEnd() - collision.getHeight() / 2 - sprite.getHeight();
    }

    public int getYSpriteEnd() {
        return owner.getYEnd() - collision.getHeight() / 2;
    }
}
