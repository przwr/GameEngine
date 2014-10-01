/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openGLEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author Wojtek
 */
public class SoundBase {

    private ArrayList<Sound> list;

    public SoundBase() {
        list = new ArrayList<>();
    }

    public void init(String folder) {
        ArrayList<File> fileList = new ArrayList<>();
        search(new File(folder), fileList);
        for (File file : fileList) {
            String[] temp = file.getName().split("\\.");
            try {
                Audio dzwiek = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream(file.getPath()));
                list.add(new Sound(temp[0], dzwiek));
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }

    public Audio getSound(String name) {
        for (Sound snd : list) {
            //System.out.println(tex.podajNazwe());
            if (snd.getName().equals(name))
                return snd.getSound();
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
}
