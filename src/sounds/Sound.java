/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sounds;

import engine.utilities.Methods;
import game.Settings;
import net.jodk.lang.FastMath;
import org.newdawn.slick.openal.Audio;

/**
 * @author Wojtek
 */
public class Sound {

    private final Audio soundEffect;
    private final String name;
    private boolean paused, stopped;
    private float gain, savedGainModifier, gainModifier = 1.0f, position = 0.0f, pitch = 1.0f;
    private boolean looped = true, fading = false;

    public Sound(String name, Audio soundEffect) {
        this.soundEffect = soundEffect;
        this.name = name;
        gain = Settings.volume * gainModifier;
    }

    public int playAsSoundEffect(float pitch, float gainModifier, boolean looped) {
        if (!soundEffect.isPlaying()) {
            this.gainModifier = gainModifier;
            this.pitch = FastMath.max(0, pitch);
            this.looped = looped;
            gain = Methods.interval(0, Settings.volume * gainModifier, 1);
            soundEffect.setPosition(position);
            return soundEffect.playAsSoundEffect(pitch, Settings.volume * gainModifier, looped);
        } else {
            return 0;
        }
    }

    public int playAsSoundEffect(float pitch, float gainModifier, boolean looped, float f0, float f1, float f2) {
        if (!soundEffect.isPlaying()) {
            this.gainModifier = gainModifier;
            this.pitch = FastMath.max(0, pitch);
            this.looped = looped;
            gain = Methods.interval(0, Settings.volume * gainModifier, 1);
            soundEffect.setPosition(position);
            return soundEffect.playAsSoundEffect(pitch, Settings.volume * gainModifier, looped, f0, f1, f2);
        } else {
            return 0;
        }
    }

    public int playAsMusic(float pitch, float gainModifier, boolean looped) {
        if (!soundEffect.isPlaying()) {
            this.pitch = FastMath.max(0, pitch);
            this.looped = looped;
            gain = Methods.interval(0, Settings.volume * gainModifier, 1);
            soundEffect.setPosition(position);
            return soundEffect.playAsMusic(pitch, Settings.volume * gainModifier, looped);
        } else {
            return 0;
        }
    }

    public void smoothStart(double time) {  //Smooth start on already started sound
        if (!fading && soundEffect.isPlaying()) {
            fading = true;
            new Thread(new Fade(time, this, 50, false, false)).start();
        }
    }

    public int resume() {
        if (!soundEffect.isPlaying()) {
            int temp = soundEffect.playAsSoundEffect(pitch, gain, looped);
            soundEffect.setPosition(position);
            paused = false;
            return temp;
        } else {
            return 0;
        }
    }

    public void stop() {
        if (soundEffect.isPlaying()) {
            position = 0;
            soundEffect.stop();
            paused = false;
        }
    }

    public void pause() {
        if (soundEffect.isPlaying()) {
            position = soundEffect.getPosition();
            soundEffect.stop();
            paused = true;
        }
    }

    public void fade(double time, boolean pause) {
        if (!fading && soundEffect.isPlaying()) {
            fading = true;
            new Thread(new Fade(time, this, 50, true, pause)).start();
        }
    }

    public void updateGain() {
        if (soundEffect.isPlaying()) {
            pause();
            gain = Methods.interval(0, Settings.volume * gainModifier, 1);
            resume();
        } else {
            gain = Methods.interval(0, Settings.volume * gainModifier, 1);
        }
    }

    public void addPitch(float a) {
        setPitch(pitch + a);
    }

    public boolean setPosition(float f) {
        return soundEffect.setPosition(f);
    }

    private void setGainModifier(float a) {
        if (soundEffect.isPlaying()) {
            pause();
            gainModifier = a;
            gain = Methods.interval(0, Settings.volume * gainModifier, 1);
            resume();
        } else {
            gainModifier = a;
            gain = Methods.interval(0, Settings.volume * gainModifier, 1);
        }
    }

    public void addGainModifier(float a) {
        setGainModifier(gainModifier + a);
    }

    public boolean isPlaying() {
        return soundEffect.isPlaying();
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public float getPosition() {
        return soundEffect.getPosition();
    }

    public String getName() {
        return name;
    }

    public Audio getSound() {
        return soundEffect;
    }

    public float getPitch() {
        return pitch;
    }

    private void setPitch(float a) {
        if (soundEffect.isPlaying()) {
            pause();
            pitch = FastMath.max(0, a);
            resume();
        } else {
            pitch = FastMath.max(0, a);
        }
    }

    public float getGain() {
        return gain;
    }

    public int getBufferID() {
        return soundEffect.getBufferID();
    }

    private class Fade implements Runnable {

        private final double time;
        private final Sound sound;
        private final long period;
        private final boolean fade, pause;

        private Fade(double time, Sound snd, int period, boolean fade, boolean pause) {
            this.time = time;
            this.sound = snd;
            this.period = period;
            this.fade = fade;
            this.pause = pause;
        }

        private void end() {
            if (fade) {
                if (pause) {
                    sound.pause();
                } else {
                    sound.stop();
                }
            }
            fading = false;
        }

        @Override
        public void run() {
            float vol = fade ? gainModifier : 0.0f;
            float delta = (float) (gainModifier / (1000 * time / period));
            delta = fade ? -delta : delta;
            if (!fade) {
                savedGainModifier = gainModifier;
            }
            float targetVol = savedGainModifier;
            while (vol >= 0 && vol <= targetVol) {
                try {
                    Thread.sleep(period);
                    vol += delta;
                    sound.setGainModifier(vol);
                } catch (InterruptedException e) {
                    end();
                }
            }
            end();
        }
    }
}
