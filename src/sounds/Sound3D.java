/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sounds;

import game.gameobject.GameObject;

/**
 *
 * @author Wojtek
 */
public class Sound3D extends Sound {

    private final GameObject owner;
    private float allSoundRange, noSoundRange, deltaRange;
    private float distanceGain;

    public Sound3D(String name, SoundBase store, int buffer, boolean isMusic, GameObject owner) {
        super(name, store, buffer, isMusic);
        this.owner = owner;
        allSoundRange = 0.1f;
        noSoundRange = 1f;
        deltaRange = 1f;
        distanceGain = 1f;
    }

    public Sound3D(Sound3D sound) {
        super(sound);
        owner = sound.owner;
        allSoundRange = sound.allSoundRange;
        noSoundRange = sound.noSoundRange;
        deltaRange = sound.deltaRange;
    }

    public void update3DSound() {
        if (isPlaying()) {
            float distance = store.getSoundDistance(owner);
            if (distance < allSoundRange) {
                distanceGain = 1;
                updateVolume();
            } else if (distance < noSoundRange) {
                distanceGain = (deltaRange + allSoundRange - distance) / deltaRange;
                updateVolume();
            } else if (distance < noSoundRange * 3) {
                distanceGain = 0;
                updateVolume();
                //TODO CHECK AREA!!!
            } else {
                stop();
            }
        }
    }

    @Override
    public float getTotalVolume() {
        return super.getTotalVolume() * distanceGain;
    }
    
    @Override
    public Sound play(float pitch, boolean loop, float x, float y, float z) {
        if (store.getSoundDistance(owner) < noSoundRange) {
            return super.play(pitch, loop, x, y, z);
        }
        return this;
    }
    
    public void setSoundRanges(float allSoundRange, float noSoundRange) {
        this.allSoundRange = allSoundRange;
        this.noSoundRange = noSoundRange;
        deltaRange = Math.abs(noSoundRange - allSoundRange);
    }

    public float getAllSoundRange() {
        return allSoundRange;
    }

    public float getNoSoundRange() {
        return noSoundRange;
    }
}
