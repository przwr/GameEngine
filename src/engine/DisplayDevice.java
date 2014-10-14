/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 *
 * @author przemek
 */
public class DisplayDevice {

    private final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private final int width;
    private final int height;
    private final int freq;

    public DisplayDevice() {
        this.width = device.getDisplayMode().getWidth();
        this.height = device.getDisplayMode().getHeight();
        this.freq = device.getDisplayMode().getRefreshRate();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFreq() {
        return height;
    }
}
