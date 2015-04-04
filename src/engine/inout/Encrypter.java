/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.inout;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author Wojtek
 */
public class Encrypter extends PrintWriter {

    private final int[] key = {2, 6, 1, 0, 3, 5};
    private final int keySize;

    public Encrypter(String fileName) throws FileNotFoundException {
        super(fileName);
        keySize = key.length;
    }

    @Override
    public void println(String text) {
        char[] output = text.toCharArray();
        for (int i = 0; i < output.length; i++) {
            if (output[i] > 32) {
                output[i] += key[i % keySize];
            }
        }
        super.println(String.valueOf(output));
    }
    
    @Override
    public void print(String text) {
        char[] output = text.toCharArray();
        int last = 0;
        int keyIndex = 0;
        for (int i = 0; i < output.length; i++, keyIndex++) {
            if (output[i] > 32) {
                output[i] += key[keyIndex % keySize];
            } else if (output[i] == '\n') {
                super.println(String.valueOf(output, last, i - last));
                last = i + 1;
                keyIndex = -1;
            }
        }
        super.println(String.valueOf(output, last, output.length - last));
    }

    @Override
    public void println(int num) {
        println("" + num);
    }

    @Override
    public void println(boolean l) {
        println("" + l);
    }
}
