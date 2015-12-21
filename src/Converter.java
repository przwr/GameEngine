
import engine.Main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
    private static final boolean isThisOkayMommy = false;   //Trzeba uważać :D

    public static void main(String[] argv) {
        if (isThisOkayMommy) {
            File[] files = new File(folder).listFiles();
            for (File f : files) {
                if (f.getName().endsWith(extension)) {
                    System.out.println("Reading " + f.getName());
                    save(read(f), f);
                    System.out.println(f.getName() + " saved");
                }
            }
        } else {
            throw new RuntimeException("This is not OKEY!");
        }
    }

    private static Collection<String> read(File file) {
        ArrayList<String> buffer = new ArrayList<>();
        try (BufferedReader read = new BufferedReader(new FileReader(file))) {
            String line;
            String placer;
            String[] data;
            int num;
            while ((line = read.readLine()) != null) {
                if (line.startsWith("ft")) {
                    data = line.split(":");
                    num = Integer.parseInt(data[3]) * 2;
                    placer = "ft";
                    for (int i = 1; i < data.length; i++) {
                        placer += ":" + (i != 3 ? data[i] : num);
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
