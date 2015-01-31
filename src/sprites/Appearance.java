/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

/**
 *
 * @author przemek
 */
public abstract class Appearance {
    
    public abstract void bindCheck();

    public abstract void render();

    public abstract void renderMirrored();

    public abstract void renderPart(int partXStart, int partXEnd);

    public abstract void renderPartMirrored(int partXStart, int partXEnd);
}
