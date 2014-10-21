/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.util.Random;

/**
 *
 * @author przemek
 */
public class RandomGen extends Random { //Metody precyzyjne stosować tylko wtedy kiedy trzeba!

    private static long[] state;
    private static int index;
    private int seed;

    public RandomGen() {
        this((int) System.currentTimeMillis());
    }

    public RandomGen(int seed) {
        state = new long[16];
        index = 0;

        setSeed(seed);
    }
    
    public void setSeed(int seed) {
        this.seed = seed;
        seed = Math.abs(seed);
        for (int i = 0; i < 16; i++) {
            state[i] = (seed + 1) * ((seed + 1) << 2) * i;
        }
    }
    
    public int getSeed() {
        return seed;
    }

    @Override
    public int next(int nbits) {
        long a, b, c, d;
        a = state[index];
        c = state[(index + 13) & 15];
        b = a ^ c ^ (a << 16) ^ (c << 15);
        c = state[(index + 9) & 15];
        c ^= (c >> 11);
        a = state[index] = b ^ c;
        d = a ^ ((a << 5) & 0xDA442D24L);
        index = (index + 15) & 15;
        a = state[index];
        state[index] = a ^ b ^ d ^ (a << 2) ^ (b << 18) ^ (c << 28);
        int x = (int) state[index];
        x &= ((1L << nbits) - 1);
        return x;
    }

    public int random(int limit) {
        int temp = limit;
        int i = 0;
        while (temp > 0) {
            temp /= 2;
            i++;
        }
        return (int) ((double) limit * (next(i) / (Math.pow(2, i) - 1)));
    }

    public double preciseRandom(double limit, double prec) {
        return (double) (random((int) ((double) limit / prec)) * prec);
    }
    
    public int randomRange(int a, int b) { //generuje x losowe: a < x < b lub b < x < a
        if (a < b) {
            return random(b - a) + a;
        } else {
            return random(a - b) + b;
        }
    }

    public double preciseRandomRange(double a, double b, double prec) {
        if (a < b) {
            return preciseRandom(b - a, prec) + a;
        } else {
            return preciseRandom(a - b, prec) + b;
        }
    }
    
    public boolean chance(int chance) {
        return random(100) <= (chance);
    }

    public boolean preciseChance(double chance, double prec) { //precyzja - (np. 0.1, 0.0001) nie może być 0 
        return random((int) ((double) 100 / prec)) <= (chance / prec);  //stosować tylko gdy potrzeba np. 81.345% czegoś
    }
}
