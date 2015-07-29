/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import collision.CircleWeaponCollision;
import collision.WeaponCollision;
import sprites.Sprite;

/**
 *
 * @author przemek
 */
public class Weapon {

    public GameObject owner;
    Sprite sprite;
    WeaponCollision collision = new CircleWeaponCollision(32);

    public Weapon(GameObject owner) {
        this.owner = owner;
    }

    public void checkCollision(GameObject[] players) {
        for (GameObject object : players) {
            if (object != null) {
                isCollideThisWeapon((Player) object);
            }
        }
    }

    public void isCollideThisWeapon(Player player) {
        collision.updatePosition(owner.getCollision().getXEnd() + 32, owner.getCollision().getYEnd() - 10);
        collision.isCollide(player);
    }

    public int getXBegin() { // TO DO - zrobić porządnie!
        return owner.getXEnd();
    }

    public int getXEnd() {
        return owner.getXEnd() + sprite.getWidth();
    }

    public int getYBegin() {
        return owner.getYEnd() - sprite.getHeight();
    }

    public int getYEnd() {
        return owner.getYEnd();
    }
}
