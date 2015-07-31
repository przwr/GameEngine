/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.fbo;

/**
 * @author Domi
 */
public interface FrameBufferType {

    void activate(int frameBufferObject);

    void deactivate();

    void deactivate(int frameBufferObjectMultiSample, int frameBufferObject, int width, int height);

    void makeTexture(int texture, int frameBufferObject, int width, int height);

    void makeMultiSample(int samplesCount, int multiSampleTexture, int width, int height, int frameBufferObjectMultiSample);
}
