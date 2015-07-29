/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package collision;

import game.gameobject.GameObject;
import game.gameobject.Weapon;

/**
 *
 * @author przemek
 */
public class PixelPerfectCollision {

    private static int weaponXStart, weaponXEnd, weaponYStart, weaponYEnd, objectXStart, objectXEnd, objectYStart, objectYEnd, top, bottom, left, right;

    ;

    public static boolean isColliding(GameObject object, Weapon weapon) {
        if (isOverlaping(object, weapon)) {
            findBoundsOfCheck();
            return true;
        }
        return false;
    }

    private static boolean isOverlaping(GameObject object, Weapon weapon) {
        weaponXStart = weapon.getXBegin();
        weaponXEnd = weapon.getXEnd();
        weaponYStart = weapon.getYBegin();
        weaponYEnd = weapon.getYEnd();
        objectXStart = object.getXSpriteBegin();
        objectXEnd = object.getXSpriteEnd();
        objectYStart = object.getYSpriteBegin();
        objectYEnd = object.getYSpriteEnd();
        return !(weaponYStart > objectYEnd || objectYStart > weaponYEnd || weaponXStart > objectXEnd || objectXStart > weaponXEnd);
    }

//    private static boolean colliding(Object OB1, Object OB2) {
//        int OB1top; //is the y coordinate of point A;
//        int OB1bot; //is the y coordinate of point B;
//        int OB1left; //is the x coordinate of point A;
//        int OB1right; //is the x coordinate of point B;
//
//        // Check the collision Vertically
//        if (OB1bot > OB2top) {
//            return false; /* this means that OB1 is above OB2,
//
//             far enough to guarantee not to be touching*/
//
//        }
//
//        if (OB2bot > OB1top) {
//            return false; /* this means that OB2 is above OB1 */
//
//        }
//
//        // Check the collision Horizontally
//        if (OB1left > OB2right) {
//            return false; /* this means that OB1 is to the right of OB2 */
//
//        }
//
//        if (OB2left > OB1right) {
//            return false; /* this means that OB2 is to the right of OB1 */
//
//        }
//
//        return true; /* this means that no object is way above the other
//
//         nor is to the right of the other meaning that the
//
//         bounding boxes are, int fact, overlapping.*/
//
//    }
    private static void findBoundsOfCheck() {
        top = (weaponYStart > objectYStart) ? weaponYStart : objectYStart;
        bottom = (weaponYEnd < objectYEnd) ? weaponYEnd : objectYEnd;
        left = (weaponXStart > objectXStart) ? weaponXStart : objectXStart;
        right = (weaponXEnd < objectXEnd) ? weaponXEnd : objectXEnd;
    }
}
