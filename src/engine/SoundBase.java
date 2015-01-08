/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import game.Settings;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author Wojtek
 */
public class SoundBase {

    private final ArrayList<Sound> list;

    public SoundBase() {
        list = new ArrayList<>();
    }

    public void init(String folder, Settings settings) {
        ArrayList<File> fileList = new ArrayList<>();
        search(new File(folder), fileList);
        for (File file : fileList) {
            String[] temp = file.getName().split("\\.");
            try {
                Audio dzwiek = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream(file.getPath()));
                //System.out.println(temp[0]);
                list.add(new Sound(temp[0], dzwiek, settings));
            } catch (IOException e) {
                Methods.Error(e.toString());
            }
        }
        settings.sounds = this;
    }

    public Sound getSound(String name) {
        for (Sound snd : list) {
            //System.out.println(tex.podajNazwe());
            if (snd.getName().equals(name)) {
                return snd;
            }
        }
        return null;
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

    public ArrayList<Sound> getSoundsList() {
        return list;
    }
}
