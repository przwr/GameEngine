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
 *
 * @author Wojtek
 */
public class Converter {    //Jakby ktoś chciał na szybko przekonwertować duże ilości plików <(^.^<)

    private static final String folder = "res/objects";
    private static final String extension = ".puz";
    private static final boolean openFolders = true;
    private static final boolean isThisOkayMommy = false;   //Trzeba uważać :D

    public static void main(String[] argv) {
        if (isThisOkayMommy) {
            readFolder(new File(folder));
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
        ArrayList<String> buffer = new ArrayList<>();
        try (BufferedReader read = new BufferedReader(new FileReader(file))) {
            String line;
            String placer;
            String[] data;
            while ((line = read.readLine()) != null) {
                if (line.startsWith("ft")) {
                    data = line.split(":");
                    placer = "ft:" + data[1] + ":" + data[2] + ":" + data[3] + ":" + data[4]
                            + ":" + (data[5].equals("1") ? OpticProperties.FULL_SHADOW : OpticProperties.TRANSPARENT)
                            + ":" + data[6] + ":" + data[7] + ":" + data[5];
                    for (int i = 8; i < data.length; i++) {
                        placer += ":" + data[i];
                    }
                    buffer.add(placer);
                } else {
                    buffer.add(line);
                }
            }
            read.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buffer;
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

}
