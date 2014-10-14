/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import game.Methods;
import game.Settings;
import org.newdawn.slick.openal.Audio;

/**
 *
 * @author Wojtek
 */
public class Sound {

    private final Settings settings;
    private final Audio sndEff;
    private final String name;
    private boolean paused;         //czy jest zastopowany
    private boolean notPlaying;      //przy przejściu do menu zaznacza, żeby wiedzieć, które dźwięki wznowić, a które nie.
    private float position = 0.0f;
    private float pitch = 1.0f;
    private float gain;                  //zmieniany pośrednio przez ogólną głośność z ustawień i gainModifier
    private float gainModifier = 1.0f;   //tutaj można ustawić głośność konkretnego dźwięku
    private float savedGainModifier;     //potrzebne, by wznowił do takiej głośności, jaka była poprzednio, wcześniej było ustawione sztywno na 1
    private boolean isLooped = true;
    private boolean fading = false;

    public Sound(String name, Audio sndEff, Settings settings) {
        this.settings = settings;
        this.sndEff = sndEff;
        this.name = name;
        this.gain = settings.volume * gainModifier;
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
            paused = false;
        }
    }

    public void pause() {
        if (sndEff.isPlaying()) {
            position = sndEff.getPosition();
            sndEff.stop();
            paused = true;
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
        if (sndEff.isPlaying()) {
            pause();
            pitch = Math.max(0, a);
            resume();
        } else {
            pitch = Math.max(0, a);
        }
    }

    public void updateGain() {
        if (sndEff.isPlaying()) {
            pause();
            gain = Methods.Interval(0, settings.volume * gainModifier, 1);
            resume();
        } else {
            gain = Methods.Interval(0, settings.volume * gainModifier, 1);
        }
    }

    public void setGainModifier(float a) {
        if (sndEff.isPlaying()) {
            pause();
            gainModifier = a;
            gain = Methods.Interval(0, settings.volume * gainModifier, 1);
            resume();
        } else {
            gainModifier = a;
            gain = Methods.Interval(0, settings.volume * gainModifier, 1);
        }
    }

    public void addGainModifier(float a) {
        setGainModifier(gainModifier + a);
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
            paused = false;
            return temp;
        } else {
            return 0;
        }
    }

    public int playAsSoundEffect(float f, float f1, boolean bln) {
        if (!sndEff.isPlaying()) {
            gainModifier = f1;
            int temp = sndEff.playAsSoundEffect(f, settings.volume * gainModifier, bln);
            pitch = Math.max(0, f);
            gain = Methods.Interval(0, settings.volume * gainModifier, 1);
            isLooped = bln;
            sndEff.setPosition(position);
            return temp;
        } else {
            return 0;
        }
    }

    public int playAsSoundEffect(float f, float f1, boolean bln, float f2, float f3, float f4) {
        if (!sndEff.isPlaying()) {
            gainModifier = f1;
            int temp = sndEff.playAsSoundEffect(f, settings.volume * gainModifier, bln, f2, f3, f4);
            pitch = Math.max(0, f);
            gain = Methods.Interval(0, settings.volume * gainModifier, 1);
            isLooped = bln;
            sndEff.setPosition(position);
            return temp;
        } else {
            return 0;
        }
    }

    public int playAsMusic(float f, float f1, boolean bln) {
        if (!sndEff.isPlaying()) {
            int temp = sndEff.playAsMusic(f, settings.volume * gainModifier, bln);
            pitch = Math.max(0, f);
            gain = Methods.Interval(0, settings.volume * gainModifier, 1);
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

    public boolean isPaused() {
        return paused;
    }

    public boolean werePaused() {
        return notPlaying;
    }

    public void setNotPlaying(boolean wasPaused) {
        this.notPlaying = wasPaused;
    }

    private class Fader implements Runnable {

        private final double time;
        private final Sound snd;
        private final long T;
        private final boolean fade;
        private final boolean pause;

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
            float vol = fade ? gainModifier : 0.0f;
            float delta = (float) (gainModifier / (1000 * time / T));
            delta = fade ? -delta : delta;
            if (!fade) {
                savedGainModifier = gainModifier;
            }
            float targetVol = savedGainModifier;
            while (vol >= 0 && vol <= targetVol) {
                try {                   
                    Thread.sleep(T);
                    vol += delta;
                    snd.setGainModifier(vol);
                } catch (InterruptedException e) {
                    end();
                }
            }
            end();
        }
    }
}
