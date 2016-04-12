/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import game.gameobject.GameObject;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * @author przemek
 */
public interface Appearance {

    Matrix4f transformationMatrix = new Matrix4f();
    Vector3f translationVector = new Vector3f();
    Vector4f vectorModifier = new Vector4f(1f, 1f, 1f, 1f);
    Vector4f ZERO_VECTOR = new Vector4f(0, 0, 0, 0);

    boolean bindCheck();

    void render();

    void renderShadow(float color);

    void renderPart(int partXStart, int partXEnd);

    void renderShadowPart(int partXStart, int partXEnd, float color);

    void renderStaticShadow(GameObject object, float x, float y);

    void updateTexture(GameObject owner); //Potrzebne tutaj?

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
