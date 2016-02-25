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
import org.newdawn.slick.util.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**¬B
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
            try {
                Audio sound = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream(file.getPath()));
                sounds.put(temp[0], new Sound(temp[0], sound));
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
                if (temp.length > 1 && temp[1].equals("ogg")) {
                    fileList.add(target);
                }
            }
        }
    }

    public Map<String, Sound> getSoundsMap() {
        return sounds;
    }
}
