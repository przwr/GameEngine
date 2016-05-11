
import collision.OpticProperties;
import engine.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * @author Wojtek
 */
public class Converter {    //Jakby ktoś chciał na szybko przekonwertować duże ilości plików <(^.^<)

    private static final String destination = "res/objects/demo/testMap.puz";
    private static final String extension = ".puz";
    private static final boolean openFolders = true;
    private static final boolean isThisOkayMommy = true;   //Trzeba uważać :D

    public static void main(String[] argv) {
        if (isThisOkayMommy) {
            File open = new File(destination);
            if (open.isDirectory()) {
                readFolder(open);
            } else {
                System.out.println("Reading " + open.getName());
                save(read(open), open);
                System.out.println(open.getName() + " saved");
            }
        } else {
            throw new RuntimeException("This is not OKEY!");
        }
    }

    private static void readFolder(File dict) {
        System.out.println("Reading folder: " + dict.getName());
        File[] files = dict.listFiles();
        for (File f : files) {
            if (f.getName().endsWith(extension)) {
                System.out.println("  Reading " + f.getName());
                save(read(f), f);
                System.out.println("  " + f.getName() + " saved");
            } else if (openFolders && f.isDirectory()) {
                readFolder(f);
            }
        }
    }

    private static Collection<String> read(File file) {
        /*ArrayList<String> buffer = new ArrayList<>();
        try {
            FileReader fl = new FileReader(file);
            BufferedReader read = new BufferedReader(fl);
            String line;
            String placer;
            String[] data;
            boolean spritesheet = true;

            line = read.readLine();
            data = line.split(";");
            spritesheet = data[1].equals("1");
            read.readLine();
            line = read.readLine();
            placer = line + ";" + (spritesheet ? "1" : "0");
            buffer.add(placer);
            
            while ((line = read.readLine()) != null) {
                buffer.add(line);
            }
            read.close();
            fl.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buffer;*/
        return readObjects(file);
    }

    private static void save(Collection<String> list, File file) {
        try (PrintWriter save = new PrintWriter(file)) {
            list.stream().forEach((s) -> {
                save.println(s);
            });
            save.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Collection<String> readObjects(File file) {
        ArrayList<String> buffer = new ArrayList<>();
        try {
            FileReader fl = new FileReader(file);
            BufferedReader read = new BufferedReader(fl);
            String line;
            String placer;
            String[] data;
            boolean analysing = true;
            while ((line = read.readLine()) != null) {
                /*if (line.startsWith("b")) {
                    analysing = false;
                }*/
                if (analysing && line.startsWith("o")) {
                    data = line.split(":");
                    placer = "o";
                    for (int i = 1; i < data.length; i++) {
                        if (i == 1) {
                            placer += ":" + (data[1].equals("Tree") ? "SimpleTree" : data[1]);
                        } else {
                            placer += ":" + data[i];
                        }
                    }
                    buffer.add(placer);
                } else {
                    buffer.add(line);
                }
            }
            read.close();
            fl.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buffer;
    }

}
