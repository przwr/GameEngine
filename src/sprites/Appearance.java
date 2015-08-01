/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

/**
 * @author przemek
 */
public interface Appearance {

    void bindCheck();

    void render();

    void renderMirrored();

    void renderPart(int partXStart, int partXEnd);

    void renderPartMirrored(int partXStart, int partXEnd);
}
