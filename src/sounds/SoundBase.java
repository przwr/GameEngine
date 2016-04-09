/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sounds;

import engine.utilities.ErrorHandler;
import game.Settings;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.lwjgl.openal.AL;

/**
 * ¬B
 *
 * @author Wojtek
 */
public class SoundBase {

    private final HashMap<String, Sound> sounds;

    public SoundBase() {
        sounds = new HashMap<>();
    }

    public void initialize(String folder) {
        ArrayList<File> fileList = new ArrayList<>();
        search(new File(folder), fileList);
        fileList.stream().forEach((file) -> {
            String[] temp = file.getName().split("\\.");
            try (InputStream stream = ResourceLoader.getResourceAsStream(file.getPath())) {
                Audio sound = null;
                switch (temp[1]) {
                    case "ogg":
                        sound = AudioLoader.getAudio("OGG", stream);
                        break;
                    case "wav":
                        sound = AudioLoader.getAudio("WAV", stream);
                        break;
                    default:
                        System.err.println("Unknown audio file format : '" + temp[1] + "'! in file '" + temp[0] + "'!");
                }
                if (sound != null) {
                    sounds.put(temp[0], new Sound(temp[0], sound));
                    System.out.println("Loaded sound : " + temp[0]);
                }
            } catch (IOException e) {
                ErrorHandler.error(e.toString());
            }
        });
        Settings.sounds = this;
    }

    public Sound getSound(String name) {
        return sounds.get(name);
    }

    private void search(File folder, ArrayList<File> fileList) {
        for (File target : folder.listFiles()) {
            if (target.isDirectory()) {
                search(target, fileList);
            } else {
                String[] temp = target.getName().split("\\.");
                if (temp.length > 1) {
                    fileList.add(target);
                }
            }
        }
    }

    public Map<String, Sound> getSoundsMap() {
        return sounds;
    }

    public void cleanUp() {
        Iterator it = sounds.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Sound sound = (Sound) pair.getValue();
            sound.stop();
        }
        sounds.clear();
        SoundStore.get().clear();
        AL.destroy();
    }
}
