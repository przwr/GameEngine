/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sounds;

import engine.utilities.ErrorHandler;
import engine.utilities.Methods;
import engine.utilities.RandomGenerator;
import game.Settings;
import game.gameobject.GameObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;
import org.newdawn.slick.openal.OggData;
import org.newdawn.slick.openal.OggDecoder;
import org.newdawn.slick.openal.WaveData;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * (SoundStore) Responsible for holding and playing the sounds used in the game.
 *
 * @author Kevin Glass
 * @author Rockstar setVolume cleanup
 *
 * Altered by
 * @author Wojtek
 */
public class SoundBase {

    /**
     * True if sound effects are turned on
     */
    private boolean sounds;
    /**
     * True if music is turned on
     */
    private boolean music;
    /**
     * True if sound initialisation succeeded
     */
    private boolean soundWorks;
    /**
     * The number of sound sources enabled - default 8
     */
    private int sourceCount;
    /**
     * The map of references to IDs of previously loaded sounds
     */
    private final HashMap loaded;
    private final ArrayList<Sound> playingSoundBase;
    /**
     * The OpenGL AL sound sources in use
     */
    private IntBuffer sources;
    /**
     * True if the sound system has been initialise
     */
    private boolean inited = false;
    /**
     * The global music volume setting
     */
    private float musicVolume = 1.0f;
    /**
     * The global sound fx volume setting
     */
    private float soundVolume = 1.0f;
    /**
     * The buffer used to set the velocity of a source
     */
    private final FloatBuffer sourceVel = BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
    /**
     * The buffer used to set the position of a source
     */
    private final FloatBuffer sourcePos = BufferUtils.createFloatBuffer(3);
    /**
     * The maximum number of sources
     */
    private int maxSources = 64;

    private final RandomGenerator random;

    /**
     * Create a new sound store
     */
    public SoundBase() {
        random = RandomGenerator.create();
        playingSoundBase = new ArrayList<>();
        loaded = new HashMap();
    }

    /**
     * Clear out the sound store contents
     */
    public void clear() {
        for (Sound sound : playingSoundBase) {
            sound.stop();
        }
        playingSoundBase.clear();
        sources.clear();
    }

    /**
     * Disable use of the Sound Store
     */
    public void disable() {
        inited = true;
    }

    /**
     * Set the music volume of the current playing music. Does NOT affect the
     * global volume
     *
     * @param volume The volume for the current playing music
     */
    public void setMusicVolume(float volume) {
        volume = Methods.interval(0, volume, 1);

        if (soundWorks) {
            musicVolume = volume;
            for (Sound sound : playingSoundBase) {
                if (sound.isItMusic()) {
                    System.out.print(sound.getVolume() + " " + sound.getTotalVolume() + " " + sound.getCurrentVolume());
                    sound.updateVolume();
                    System.out.println(" -> " + sound.getVolume() + " " + sound.getCurrentVolume());
                }
            }
        }
    }

    /**
     * Set the sound volume
     *
     * @param volume The volume for sound fx
     */
    public void setSoundVolume(float volume) {
        volume = Methods.interval(0, volume, 1);

        if (soundWorks) {
            soundVolume = volume;
            for (Sound sound : playingSoundBase) {
                if (!sound.isItMusic()) {
                    System.out.print(sound.getVolume() + " " + sound.getTotalVolume() + " " + sound.getCurrentVolume());
                    sound.updateVolume();
                    System.out.println(" -> " + sound.getVolume() + " " + sound.getCurrentVolume());
                }
            }
        }
    }

    /**
     * Check if sound works at all
     *
     * @return True if sound works at all
     */
    public boolean soundWorks() {
        return soundWorks;
    }

    /**
     * Check if music is currently enabled
     *
     * @return True if music is currently enabled
     */
    public boolean musicOn() {
        return music;
    }

    /**
     * Get the volume for sounds
     *
     * @return The volume for sounds
     */
    public float getSoundVolume() {
        return soundVolume;
    }

    /**
     * Get the volume for music
     *
     * @return The volume for music
     */
    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Get the ID of a given source
     *
     * @param index The ID of a given source
     * @return The ID of the given source
     */
    public int getSource(int index) {
        if (!soundWorks) {
            return -1;
        }
        if (index < 0) {
            return -1;
        }
        return sources.get(index);
    }

    /**
     * Indicate whether sound effects should be played
     *
     * @param sounds True if sound effects should be played
     */
    public void setSoundsOn(boolean sounds) {
        if (soundWorks) {
            this.sounds = sounds;
        }
    }

    /**
     * Check if sound effects are currently enabled
     *
     * @return True if sound effects are currently enabled
     */
    public boolean soundsOn() {
        return sounds;
    }

    /**
     * Set the maximum number of concurrent sound effects that will be attempted
     *
     * @param max The maximum number of sound effects/music to mix
     */
    public void setMaxSources(int max) {
        this.maxSources = max;
    }

    /**
     * Initialise the sound effects stored. This must be called before anything
     * else will work
     */
    public void init() {
        if (inited) {
            return;
        }
        Log.info("Initialising sounds..");
        inited = true;

        AccessController.doPrivileged((PrivilegedAction) () -> {
            try {
                AL.create();
                soundWorks = true;
                sounds = true;
                music = true;
                Log.info("- Sound works");
            } catch (Exception e) {
                Log.error("Sound initialisation failure.");
                Log.error(e);
                soundWorks = false;
                sounds = false;
                music = false;
            }

            return null;
        });

        if (soundWorks) {
            sourceCount = 0;
            sources = BufferUtils.createIntBuffer(maxSources);
            while (AL10.alGetError() == AL10.AL_NO_ERROR) {
                IntBuffer temp = BufferUtils.createIntBuffer(1);

                try {
                    AL10.alGenSources(temp);

                    if (AL10.alGetError() == AL10.AL_NO_ERROR) {
                        sourceCount++;
                        sources.put(temp.get(0));
                        if (sourceCount > maxSources - 1) {
                            break;
                        }
                    }
                } catch (OpenALException e) {
                    // expected at the end
                    break;
                }
            }
            Log.info("- " + sourceCount + " OpenAL source available");

            if (AL10.alGetError() != AL10.AL_NO_ERROR) {
                sounds = false;
                music = false;
                soundWorks = false;
                Log.error("- AL init failed");
            } else {
                FloatBuffer listenerOri = BufferUtils.createFloatBuffer(6).put(
                        new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});
                FloatBuffer listenerVel = BufferUtils.createFloatBuffer(3).put(
                        new float[]{0.0f, 0.0f, 0.0f});
                FloatBuffer listenerPos = BufferUtils.createFloatBuffer(3).put(
                        new float[]{0.0f, 0.0f, 0.0f});
                listenerPos.flip();
                listenerVel.flip();
                listenerOri.flip();
                AL10.alListener(AL10.AL_POSITION, listenerPos);
                AL10.alListener(AL10.AL_VELOCITY, listenerVel);
                AL10.alListener(AL10.AL_ORIENTATION, listenerOri);

                Log.info("- Sounds source generated");
            }
        }
    }

    float getRandom(float delta) {
        return random.nextFloat() * delta;
    }

    float getRandomInterval(float delta) {
        return delta * (2 * random.nextFloat() - 1);
    }

    float getSoundDistance(GameObject soundOwner) {
        GameObject listener = Settings.players[0];
        return (float) Methods.pointDifference(soundOwner.getX(), soundOwner.getY(), listener.getX(), listener.getY()) / 1024;
    }

    /**
     * Stop a particular sound source
     *
     * @param index The index of the source to stop
     */
    void stopSource(int index) {
        AL10.alSourceStop(sources.get(index));
    }

    void pauseSource(int index) {
        AL10.alSourcePause(sources.get(index));
    }

    boolean isSourcePaused(int index) {
        return AL10.alGetSourcei(sources.get(index), AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
    }

    void resumeSource(int index) {
        if (AL10.alGetSourcei(sources.get(index), AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED) {
            AL10.alSourcePlay(sources.get(index));
        }
    }

    public void update3DSounds() {
        for (Sound sound : playingSoundBase) {
            if (sound instanceof Sound3D) {
                ((Sound3D) sound).update3DSound();
            }
        }
    }
    
    public void pauseAllSounds() {
        for (Sound sound : playingSoundBase) {
            sound.pause();
        }
    }

    public void resumeAllSounds() {
        for (Sound sound : playingSoundBase) {
            sound.resume();
        }
    }

    int playSound(Sound sound, float pitch, float gain, boolean loop, float x, float y, float z) {
        if (!playingSoundBase.contains(sound)) {
            playingSoundBase.add(sound);
            sound.setIndexUsable(true);
            return playSound(sound.getBufferID(), pitch, gain, loop, x, y, z, -1);
        } else {
            return playSound(sound.getBufferID(), pitch, gain, loop, x, y, z, sound.getIndex());
        }
    }

    /**
     * Play the specified buffer as a sound effect with the specified pitch and
     * gain.
     *
     * @param buffer The ID of the buffer to play
     * @param pitch The pitch to play at
     * @param gain The gain to play at
     * @param loop True if the sound should loop
     * @param x The x position to play the sound from
     * @param y The y position to play the sound from
     * @param z The z position to play the sound from
     * @return source The source that will be used
     */
    private int playSound(int buffer, float pitch, float gain, boolean loop, float x, float y, float z, int forceIndex) {
        gain *= soundVolume;
        if (gain == 0) {
            gain = 0.001f;
        }
        if (soundWorks) {
            if (sounds) {
                int nxSource;
                if (forceIndex == -1) {
                    nxSource = findFreeSource();
                    if (nxSource == -1) {
                        return -1;
                    }
                } else {
                    nxSource = forceIndex;
                }

                AL10.alSourceStop(sources.get(nxSource));

                AL10.alSourcei(sources.get(nxSource), AL10.AL_BUFFER, buffer);
                AL10.alSourcef(sources.get(nxSource), AL10.AL_PITCH, pitch);
                AL10.alSourcef(sources.get(nxSource), AL10.AL_GAIN, gain);
                AL10.alSourcei(sources.get(nxSource), AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);

                sourcePos.clear();
                sourceVel.clear();
                sourceVel.put(new float[]{0, 0, 0});
                sourcePos.put(new float[]{x, y, z});
                sourcePos.flip();
                sourceVel.flip();
                AL10.alSource(sources.get(nxSource), AL10.AL_POSITION, sourcePos);
                AL10.alSource(sources.get(nxSource), AL10.AL_VELOCITY, sourceVel);

                AL10.alSourcePlay(sources.get(nxSource));

                return nxSource;
            }
        }

        return -1;
    }

    /**
     * Check if a particular source is playing
     *
     * @param index The index of the source to check
     * @return True if the source is playing
     */
    boolean isSourcePlaying(int index) {
        int state = AL10.alGetSourcei(sources.get(index), AL10.AL_SOURCE_STATE);

        return (state == AL10.AL_PLAYING);
    }

    void changePitch(int index, float pitch) {
        AL10.alSourcef(sources.get(index), AL10.AL_PITCH, pitch);
    }

    void changeGain(int index, float gain) {
        AL10.alSourcef(sources.get(index), AL10.AL_GAIN, gain);
    }

    float getCurrentGain(int index) {
        return AL10.alGetSourcef(sources.get(index), AL10.AL_GAIN);
    }

    /**
     * Find a free sound source
     *
     * @return The index of the free sound source
     */
    private int findFreeSource() {
        for (int i = 0; i < sourceCount - 1; i++) {
            int state = AL10.alGetSourcei(sources.get(i), AL10.AL_SOURCE_STATE);

            if ((state != AL10.AL_PLAYING) && (state != AL10.AL_PAUSED)) {
                Sound toRemove = null;
                for (Sound sound : playingSoundBase) {
                    if (sound.getIndex() == i) {
                        toRemove = sound;
                        break;
                    }
                }
                if (toRemove != null) {
                    playingSoundBase.remove(toRemove);
                    toRemove.setIndexUsable(false);
                }
                return i;
            }
        }

        return -1;
    }

    public static String fullFolderPath(String folder) {
        if (folder.isEmpty()) {
            return "res/sound/";
        } else {
            if (folder.startsWith("res/sound")) {
                return folder + (folder.endsWith("/") ? "" : "/");
            } else {
                return "res/sound/" + folder + (folder.endsWith("/") ? "" : "/");
            }
        }
    }

    public Sound3D get3DSoundEffect(String name, GameObject owner) {
        return get3DSound(name, "se/", false, owner);
    }

    public Sound3D get3DSoundEffect(String name, String folder, GameObject owner) {
        return get3DSound(name, "se/" + folder, false, owner);
    }

    public Sound3D get3DBGSound(String name, GameObject owner) {
        return get3DSound(name, "bg/", true, owner);
    }

    public Sound3D get3DBGSound(String name, String folder, GameObject owner) {
        return get3DSound(name, "bg/" + folder, true, owner);
    }

    public Sound getSoundEffect(String name) {
        return getSound(name, "se/", false);
    }

    public Sound getSoundEffect(String name, String folder) {
        return getSound(name, "se/" + folder, false);
    }

    public Sound getBGSound(String name) {
        return getSound(name, "bg/", true);
    }

    public Sound getBGSound(String name, String folder) {
        return getSound(name, "bg/" + folder, true);
    }

    private Sound getSound(String name, String folder, boolean music) {
        try {
            int buffer = -1;
            folder = fullFolderPath(folder);
            if (name.endsWith(".wav")) {
                buffer = loadWAV(name, ResourceLoader.getResourceAsStream(folder + name));
            } else if (name.endsWith(".ogg")) {
                buffer = loadOgg(name, ResourceLoader.getResourceAsStream(folder + name));
            }
            if (buffer != -1) {
                return new Sound(name, this, buffer, music);
            }
        } catch (IOException e) {
            ErrorHandler.error("Sound file '" + folder + name + "' cannot be loaded!");
        }
        return null;
    }

    private Sound3D get3DSound(String name, String folder, boolean music, GameObject owner) {
        try {
            int buffer = -1;
            folder = fullFolderPath(folder);
            if (name.endsWith(".wav")) {
                buffer = loadWAV(name, ResourceLoader.getResourceAsStream(folder + name));
            } else if (name.endsWith(".ogg")) {
                buffer = loadOgg(name, ResourceLoader.getResourceAsStream(folder + name));
            }
            if (buffer != -1) {
                return new Sound3D(name, this, buffer, music, owner);
            }
        } catch (IOException e) {
            ErrorHandler.error("Sound file '" + folder + name + "' cannot be loaded!");
        }
        return null;
    }

    /**
     * Get the Sound based on a specified WAV file
     *
     * @param ref The reference to the WAV file in the classpath
     * @param in The stream to the WAV to load
     * @return The Sound read from the WAV file
     * @throws IOException Indicates a failure to load the WAV
     */
    public int loadWAV(String ref, InputStream in) throws IOException {
        if (!soundWorks) {
            return -1;
        }
        if (!inited) {
            throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
        }

        int buffer = -1;

        if (loaded.get(ref) != null) {
            buffer = ((Integer) loaded.get(ref));
        } else {
            try {
                IntBuffer buf = BufferUtils.createIntBuffer(1);

                WaveData data = WaveData.create(in);
                AL10.alGenBuffers(buf);
                AL10.alBufferData(buf.get(0), data.format, data.data, data.samplerate);

                loaded.put(ref, buf.get(0));
                buffer = buf.get(0);
            } catch (Exception e) {
                Log.error(e);
                throw new IOException("Failed to load: " + ref, e);
            }
        }

        if (buffer == -1) {
            throw new IOException("Unable to load: " + ref);
        }

        return buffer;
    }

    /**
     * Get the Sound based on a specified OGG file
     *
     * @param ref The reference to the OGG file in the classpath
     * @param in The stream to the OGG to load
     * @return The Sound read from the OGG file
     * @throws IOException Indicates a failure to load the OGG
     */
    public int loadOgg(String ref, InputStream in) throws IOException {
        if (!soundWorks) {
            return -1;
        }
        if (!inited) {
            throw new RuntimeException("Can't load sounds until SoundStore is init(). Use the container init() method.");
        }

        int buffer = -1;

        if (loaded.get(ref) != null) {
            buffer = ((Integer) loaded.get(ref));
        } else {
            try {
                IntBuffer buf = BufferUtils.createIntBuffer(1);

                OggDecoder decoder = new OggDecoder();
                OggData ogg = decoder.getData(in);

                AL10.alGenBuffers(buf);
                AL10.alBufferData(buf.get(0), ogg.channels > 1 ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, ogg.data, ogg.rate);

                loaded.put(ref, buf.get(0));

                buffer = buf.get(0);
            } catch (Exception e) {
                Log.error(e);
                Sys.alert("Error", "Failed to load: " + ref + " - " + e.getMessage());
                throw new IOException("Unable to load: " + ref);
            }
        }

        if (buffer == -1) {
            throw new IOException("Unable to load: " + ref);
        }

        return buffer;
    }

    /**
     * Stop a playing sound identified by the ID returned from playing. This
     * utility method should only be used when needing to stop sound effects
     * that may have been played more than once and need to be explicitly
     * stopped.
     *
     * @param id The ID of the underlying OpenAL source as returned from
     * playAsSoundEffect
     */
    public void stopSoundEffect(int id) {
        AL10.alSourceStop(id);
    }

    /**
     * Retrieve the number of OpenAL sound sources that have been determined at
     * initialisation.
     *
     * @return The number of sources available
     */
    public int getSourceCount() {
        return sourceCount;
    }
}
