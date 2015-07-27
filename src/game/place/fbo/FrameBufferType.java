/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.fbo;

/**
 *
 * @author Domi
 */
public interface FrameBufferType {

    public void activate(int frameBufferObject);

    public void deactivate();

    public void deactivate(int frameBufferObjectMultisample, int frameBufferObject, int width, int height);

    public void makeTexture(int texture, int frameBufferObject, int width, int height);

    public void makeMultiSample(int samplesCount, int multiSampleTexture, int width, int height, int frameBufferObjectMultiSample);
}
