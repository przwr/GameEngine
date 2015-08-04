/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codespeedtester;

import engine.Methods;
import net.jodk.lang.FastMath;

/**
 * @author przemek
 */
public class CodeSpeedTester {

    private static final int runs = 1000000;   // znajdź najmniejszą liczbę dla której puste testy nadal pokazują wyniki bliskie zero.
    private static final int precision = 10;  // z jaką dokładnością wyświetla się wynik 10 - jedno miejsce po przecinku, 100 - dwa
    // przygotuj zmienne potrzebne do testowania kodu
    private static final double angle = 70;
    private static final double rad = 180;
    private static double zeroTime;

    private static void firstCodeToTest() {

        double result = FastMath.sin(FastMath.toRadians(angle)) * rad;
    }

    private static void secondCodeToTest() {
        double x = 100;
        double x2 = 100;
//        System.out.println(Math.signum(x-x2));

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        setZero();
        testFirst();
        testSecond();
    }

    private static void setZero() {
        testZero(runs);
        zeroTime = testZero(runs);
        System.out.println("Zero Time: " + zeroTime);
    }

    private static void testFirst() {
        testFirst(runs, zeroTime);
        double first = testFirst(runs, zeroTime);
        System.out.println("FIRST tested code average time: " + getNiceLookingValue(first, precision) + " ns");
    }

    private static void testSecond() {
        testSecond(runs, zeroTime);
        double second = testSecond(runs, zeroTime);
        System.out.println("SECOND tested code average time: " + getNiceLookingValue(second, precision) + " ns");
    }

    private static double testZero(double runs) {
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            zeroCodeToTest();
        }
        long end = System.nanoTime();
        return (end - start) / runs;
    }

    private static double testFirst(double runs, double zeroTime) {
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            firstCodeToTest();
        }
        long end = System.nanoTime();
        return (((end - start) / runs) - zeroTime);
    }

    private static double testSecond(double runs, double zeroTime) {
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            secondCodeToTest();
        }
        long end = System.nanoTime();
        return (((end - start) / runs) - zeroTime);
    }

    private static void zeroCodeToTest() {
        // Ten test ma zostać pusty. Jest po to by zmierzyć czas pustego przebiegu - metod nanoTime i wchodzenia do kodu testującego.
    }

    private static double getNiceLookingValue(double number, double precision) {
        return (Methods.roundDouble(number * precision)) / precision;
    }

}
