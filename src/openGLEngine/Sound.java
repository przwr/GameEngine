/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openGLEngine;

import game.Methods;
import org.newdawn.slick.openal.Audio;

/**
 *
 * @author Wojtek
 */
public class Sound {

    private final Audio sndEff;
    private String name;
    private float position = 0.0f;
    private float pitch = 1.0f;
    private float gain = 1.0f;
    private boolean isLooped = true;
    private boolean fading = false;

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

    public void stop() {
        if (sndEff.isPlaying()) {
            position = 0;
            sndEff.stop();
        }
    }

    public void pause() {
        if (sndEff.isPlaying()) {
            position = sndEff.getPosition();
            sndEff.stop();
        }
    }

    public void fade(double time, boolean pause) {
        if (!fading && sndEff.isPlaying()) {
            fading = true;
            new Thread(new Fader(time, this, 50, true, pause)).start();
        }
    }

    public void smoothStart(double time) {  //Smooth start on already started sound
        if (!fading && sndEff.isPlaying()) {
            fading = true;
            new Thread(new Fader(time, this, 50, false, false)).start();
        }
    }

    public void setPitch(float a) {
        pause();
        pitch = Math.max(0, a);
        sndEff.playAsSoundEffect(pitch, gain, isLooped);
        sndEff.setPosition(position);
    }

    public void setGain(float a) {
        pause();
        gain = Methods.Interval(0, a, 1);
        sndEff.playAsSoundEffect(pitch, gain, isLooped);
        sndEff.setPosition(position);
    }

    public void addGain(float a) {
        setGain(gain + a);
    }

    public void addPitch(float a) {
        setPitch(pitch + a);
    }

    public float getPitch() {
        return pitch;
    }

    public float getGain() {
        return gain;
    }

    public int getBufferID() {
        return sndEff.getBufferID();
    }

    public boolean isPlaying() {
        return sndEff.isPlaying();
    }

    public int resume() {
        if (!sndEff.isPlaying()) {
            int temp = sndEff.playAsSoundEffect(pitch, gain, isLooped);
            sndEff.setPosition(position);
            return temp;
        } else {
            return 0;
        }
    }
    
    public int playAsSoundEffect(float f, float f1, boolean bln) {
        if (!sndEff.isPlaying()) {
            int temp = sndEff.playAsSoundEffect(f, f1, bln);
            pitch = Math.max(0, f);
            gain = Methods.Interval(0, f1, 1);
            isLooped = bln;
            sndEff.setPosition(position);
            return temp;
        } else {
            return 0;
        }
    }

    public int playAsSoundEffect(float f, float f1, boolean bln, float f2, float f3, float f4) {
        if (!sndEff.isPlaying()) {
            int temp = sndEff.playAsSoundEffect(f, f1, bln, f2, f3, f4);
            pitch = Math.max(0, f);
            gain = Methods.Interval(0, f1, 1);
            isLooped = bln;
            sndEff.setPosition(position);
            return temp;
        } else {
            return 0;
        }
    }

    public int playAsMusic(float f, float f1, boolean bln) {
        if (!sndEff.isPlaying()) {
            int temp = sndEff.playAsMusic(f, f1, bln);
            pitch = Math.max(0, f);
            gain = Methods.Interval(0, f1, 1);
            isLooped = bln;
            sndEff.setPosition(position);
            return temp;
        } else {
            return 0;
        }
    }

    public boolean setPosition(float f) {
        return sndEff.setPosition(f);
    }

    public float getPosition() {
        return sndEff.getPosition();
    }

    private class Fader implements Runnable {

        private double time;
        private Sound snd;
        private long T;
        private boolean fade;
        private boolean pause;

        public Fader(double time, Sound snd, int T, boolean fade, boolean pause) {
            this.time = time;
            this.snd = snd;
            this.T = (long) T;
            this.fade = fade;
            this.pause = pause;
        }

        private void end() {
            if (fade) {
                if (pause) {
                    snd.pause();
                } else {
                    snd.stop();
                }
            }
            fading = false;
        }

        @Override
        public void run() {
            float vol = fade ? 1.0f : 0.0f;
            float delta = (float) (1.0f / (1000 * time / T));
            delta = fade ? -delta : delta;

            while (vol >= 0 && vol <= 1) {
                try {
                    Thread.sleep(T);
                    vol += delta;
                    snd.setGain(vol);
                } catch (InterruptedException e) {
                    end();
                }
            }
            end();
        }

    }
}
