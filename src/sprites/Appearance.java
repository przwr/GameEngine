/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import game.gameobject.Player;

/**
 * @author przemek
 */
public interface Appearance {

    void bindCheck();

    void render();

    void renderMirrored();

    void renderPart(int partXStart, int partXEnd);

    void renderPartMirrored(int partXStart, int partXEnd);

    void updateTexture(Player owner);

    void updateFrame();

    int getCurrentFrameIndex();

    int getWidth();

    int getHeight();

    int getXStart();

    int getYStart();

    int getActualWidth();

    int getActualHeight();

    int getXOffset();

    int getYOffset();


}
