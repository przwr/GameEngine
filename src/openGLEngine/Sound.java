/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openGLEngine;

import org.newdawn.slick.openal.Audio;
/**
 *
 * @author Wojtek
 */
public class Sound {

    private Audio sndEff;
    String name;
    
    public Sound(String name, Audio sndEff) {
        this.sndEff = sndEff;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public Audio getSound() {
        return sndEff;
    }
}
