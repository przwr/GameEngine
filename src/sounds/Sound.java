/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sounds;

import engine.utilities.Methods;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

/**
 * (SlickAudioImpl) A sound that can be played through OpenAL
 *
 * @author Kevin Glass
 * @author Nathan Sweet <misc@n4te.com>
 *
 * Altered by
 * @author Wojtek
 */
public class Sound {

    private final String name;
    /**
     * The store from which this sound was loaded
     */
    final SoundBase store;
    /**
     * The buffer containing the sound
     */
    private final int buffer;
    /**
     * The index of the source being used to play this sound
     */
    private int index = -1;
    private boolean isIndexUsable;
    /**
     * The length of the audio
     */
    private float length;
    private boolean isMusic;
    private float gain = 1f, pitch = 1f, randomDelta;
    private boolean looped, fading = false, randomized;

    public Sound(String name, SoundBase store, int buffer, boolean isMusic) {
        this.store = store;
        this.buffer = buffer;
        this.isMusic = isMusic;
        this.name = name;
        looped = isMusic;
        setBuffer(buffer);
    }

    public Sound(Sound other) {
        name = other.name;
        store = other.store;
        buffer = other.buffer;
        isMusic = other.isMusic;
        length = other.length;
        gain = other.gain;
        pitch = other.pitch;
        looped = other.looped;
        fading = other.fading;
        randomDelta = other.randomDelta;
        randomized = other.randomized;
    }

    private void setBuffer(int buffer) {
        if (buffer != -1) {
            int bytes = AL10.alGetBufferi(buffer, AL10.AL_SIZE);
            int bits = AL10.alGetBufferi(buffer, AL10.AL_BITS);
            int channels = AL10.alGetBufferi(buffer, AL10.AL_CHANNELS);
            int freq = AL10.alGetBufferi(buffer, AL10.AL_FREQUENCY);

            int samples = bytes / (bits / 8);
            length = (samples / (float) freq) / channels;
        }
    }

    public void setRandomized(float delta) {
        randomized = true;
        randomDelta = delta;
    }

    public void setNotRandomized() {
        randomized = false;
    }

    public int getBufferID() {
        return buffer;
    }

    public void setVolume(float volume) {
        this.gain = Methods.interval(0, volume, 1);
    }

    public void setVolumeAndUpdate(float volume) {
        this.gain = Methods.interval(0, volume, 1);
        updateVolume();
    }

    void updateVolume() {
        if (isIndexUsable) {
            store.changeGain(index, getTotalVolume());
        }
    }

    public float getTotalVolume() {
        return gain * (isMusic ? store.getMusicVolume() : store.getSoundVolume());
    }

    public float getVolume() {
        return gain;
    }

    public float getCurrentVolume() {
        return isIndexUsable ? store.getCurrentGain(index) : 0;
    }

    public boolean isPlaying() {
        return isIndexUsable && store.isSourcePlaying(index);
    }

    public Sound play() {
        return play(1f, looped, 0, 0, 0);
    }

    public Sound play(float pitch, boolean loop) {
        return play(pitch, loop, 0, 0, 0);
    }

    public Sound play(float pitch, boolean loop, float x, float y, float z) {
        if (buffer != -1) {
            if (isPlaying()) {
                if (!isMusic) {
                    Sound copy = new Sound(this);
                    copy.index = playSound(copy, pitch, loop, x, y, z);
                    return copy;
                } else {
                    return this;
                }
            } else {
                index = playSound(this, pitch, loop, x, y, z);
                return this;
            }
        } else {
            return this;
        }
    }

    private int playSound(Sound sound, float pitch, boolean loop, float x, float y, float z) {
        float volume = sound.getTotalVolume();
        pitch = pitch * sound.pitch;
        if (randomized) {
            pitch += store.getRandomInterval(randomDelta);
            volume += store.getRandomInterval(randomDelta);
        }
        return store.playSound(sound, pitch, volume, loop, x, y, z);
    }

    public void stop() {
        if (isIndexUsable) {
            AL10.alSourceStop(store.getSource(index));
        }
    }

    public void pause() {
        if (isIndexUsable) {
            AL10.alSourcePause(store.getSource(index));
        }
    }

    public boolean isPaused() {
        return isIndexUsable && AL10.alGetSourcei(store.getSource(index), AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
    }

    public void resume() {
        if (isIndexUsable) {
            store.resumeSource(index);
        }
    }

    void setIndexUsable(boolean usable) {
        this.isIndexUsable = usable;
    }

    int getIndex() {
        return index;
    }

    public boolean setPosition(float position) {
        if (isIndexUsable) {
            position = position % length;

            AL10.alSourcef(store.getSource(index), AL11.AL_SEC_OFFSET, position);
            return AL10.alGetError() == 0;
        }
        return false;
    }

    public float getPosition() {
        if (isIndexUsable) {
            return AL10.alGetSourcef(store.getSource(index), AL11.AL_SEC_OFFSET);
        }
        return 0;
    }

    public float getLength() {
        return length;
    }

    public boolean isItMusic() {
        return isMusic;
    }

    public void setToMusic(boolean isMusic) {
        this.isMusic = isMusic;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setPitchNow(float pitch) {
        this.pitch = pitch;
        if (isIndexUsable) {
            AL10.alSourcef(store.getSource(index), AL10.AL_PITCH, pitch);
        }
    }

    public boolean isLooped() {
        return looped;
    }

    public void setLooped(boolean looped) {
        this.looped = looped;
    }

    public String getName() {
        return name;
    }

    public boolean isFading() {
        return fading;
    }

    void setFading(boolean fading) {
        this.fading = fading;
    }
}
