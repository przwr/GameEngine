/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine.inout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Wojtek
 */
public class Decrypter extends BufferedReader {

    private final int[] key = {2, 6, 1, 0, 3, 5};
    private final int keySize;

    public Decrypter(String file) throws FileNotFoundException {
        super(new FileReader(file));
        keySize = key.length;
    }

    @Override
    public String readLine() throws IOException {
        String line = super.readLine();
        System.out.println(line);
        if (line != null) {
            char[] input = line.toCharArray();
            for (int i = 0; i < input.length; i++) {
                if (input[i] > 32) {
                    input[i] -= key[i % keySize];
                }
            }
            return String.copyValueOf(input);
        }
        return null;
    }
}
