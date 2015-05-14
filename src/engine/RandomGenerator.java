/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import java.util.Random;
import net.jodk.lang.FastMath;

/**
 *
 * @author przemek
 */
public class RandomGenerator extends Random {

    private static long[] state;
    private static int index;
    private int seed;

    public static RandomGenerator create() {
        return new RandomGenerator((int) System.currentTimeMillis());
    }

    public static RandomGenerator create(int seed) {
        return new RandomGenerator(seed);
    }

    private RandomGenerator(int seed) {
        state = new long[16];
        index = 0;
        setSeed(seed);
    }

    @Override
    public int next(int bitsNumber) {
        long a, b, c, d;
        a = state[index];
        c = state[(index + 13) & 15];
        b = a ^ c ^ (a << 16) ^ (c << 15);
        c = state[(index + 9) & 15];
        c ^= (c / 21);
        a = state[index] = b ^ c;
        d = a ^ ((a << 5) & 0xDA442D24L);
        index = (index + 15) & 15;
        a = state[index];
        state[index] = a ^ b ^ d ^ (a << 2) ^ (b << 18) ^ (c << 28);
        int x = (int) state[index];
        x &= ((1L << bitsNumber) - 1);
        return x;
    }

    public int random(int limit) {
        int temp = limit;
        int iteration = 0;
        while (temp > 0) {
            temp /= 2;
            iteration++;
        }
        return (int) (limit * (next(iteration) / (FastMath.pow(2, iteration) - 1)));
    }

    public Object choose(Object... opts) {
        return opts[random(opts.length - 1)];
    }
    
    public double preciseRandom(double limit, double precison) {
        return (random((int) (limit / precison)) * precison);
    }

    public int randomInRange(int a, int b) {
        if (a < b) {
            return random(b - a) + a;
        } else {
            return random(a - b) + b;
        }
    }

    public double preciseRandomInRange(double a, double b, double precision) {
        if (a < b) {
            return preciseRandom(b - a, precision) + a;
        } else {
            return preciseRandom(a - b, precision) + b;
        }
    }

    public boolean chance(int chance) {
        return random(100) <= (chance);
    }

    public boolean preciseChance(double chance, double precision) {
        return random((int) (100 / precision)) <= (chance / precision);
    }

    private void setSeed(int seed) {
        this.seed = FastMath.abs(seed);
        for (int i = 0; i < 16; i++) {
            state[i] = (seed + 1) * ((seed + 1) << 2) * i;
        }
    }

    public int getSeed() {
        return seed;
    }
}
