/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author przemek
 */
public class IO {

    public static void ReadFile(File f) {
        try {
            BufferedReader r = new BufferedReader(new FileReader(f));
            String s = null;            
            while ((s = r.readLine()) != null) {
                Analizer.AnalizeSetting(s);
                System.out.println("Przeczytano: " + s);
            }
        } catch (IOException e) {
            System.out.println(e);
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
    }

//    public static void SaveFile(File f, Controler controler, Grid g, GridCRS gcrs, int gen) {
//        String[] str = g.GetStr();
//        try {
//            FileWriter out;
//            if (f != null) {
//                out = new FileWriter(f.toString() + "_gen_" + gen);
//            } else {
//                out = new FileWriter("edited_gen_" + gen);
//            }
//            int in = 0;
//            out.write(str[in]);
//            in++;
//            for (; in < g.GetLinesNr(); in++) {
//                out.write("\n" + str[in]);
//            }
//            for (int i = 0; i < g.GetRow(); i++) {
//                int j = gcrs.GetRowPtr(i) - 1;
//                int next = gcrs.GetRowPtr(i + 1) - 1;
//                for (; j < next; j++) {
//                    char state = gcrs.GetCellFromIndex(j + 1).GetState();
//                    int row = i + 1;
//                    int col = gcrs.GetColInd(j);
//                    if (state == 'H') {
//                        out.write("\nHead " + col + " " + row);
//                    } else if (state == 'T') {
//                        out.write("\nTail " + col + " " + row);
//                    }
//                }
//            }
//            out.close();
//        } catch (IOException e) {
//            controler.Error(e.getMessage());
//        }
//    }
}
