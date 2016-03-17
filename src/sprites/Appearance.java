/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import game.gameobject.entities.Player;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author przemek
 */
public interface Appearance {

    Matrix4f transformationMatrix = new Matrix4f();
    Vector3f translationVector = new Vector3f();
//    Vector4f colorModifier = new Vector4f(1f, 1f, 1f, 1f);

    boolean bindCheck();

    void render();

    void renderPart(int partXStart, int partXEnd);

    void updateTexture(Player owner); //Potrzebne tutaj?

    void updateFrame(); //Potrzebne tutaj?

    int getCurrentFrameIndex(); //Potrzebne tutaj?

    int getWidth();

    int getHeight();

    int getXStart();

    int getYStart();

    int getActualWidth();

    int getActualHeight();

    int getXOffset();

    int getYOffset();

}
