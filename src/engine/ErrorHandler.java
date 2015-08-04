/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

import javax.swing.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author przemek
 */
public class ErrorHandler {

    private static File file;

    public static void swallowLogAndPrint(Exception exception) {
        String error = exception + "\n";
        for (StackTraceElement stackTrace : exception.getStackTrace()) {
            error += stackTrace + "\n";
        }
        logAndPrint(error);
    }

    public static void logAndPrint(String string) {
        System.err.print(string);
        if (Main.LOG) {
            errorToFile(string);
        }
    }

    public static void logToFile(String string) {
        file = new File("logs/log_" + Main.STARTED_DATE + ".txt");
        if (file.exists() && !file.isDirectory()) {
            log(string);
        } else {
            try {
                try (final FileWriter writer = new FileWriter("logs/log_" + Main.STARTED_DATE + ".txt")) {
                    writer.write(string);
                }
            } catch (IOException ex) {
                Logger.getLogger(ErrorHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void exception(Exception exception) {
        String error = exception + "\n";
        for (StackTraceElement stackTrace : exception.getStackTrace()) {
            error += stackTrace + "\n";
        }
        Main.addMessage(error);
        logAndPrint("\n" + error + "\n");
    }

    private static void log(String string) {
        try {
            try (final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"))) {
                writer.append(string);
            }
        } catch (IOException ex) {
            Logger.getLogger(ErrorHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void errorToFile(String string) {
        file = new File("logs/error_" + Main.STARTED_DATE + ".txt");
        if (file.exists() && !file.isDirectory()) {
            log(string);
        } else {
            try {
                try (final FileWriter writer = new FileWriter("logs/error_" + Main.STARTED_DATE + ".txt")) {
                    writer.write(string);
                }
            } catch (IOException ex) {
                Logger.getLogger(ErrorHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void javaException(Exception exception) {
        String error = exception + "\n";
        for (StackTraceElement stackTrace : exception.getStackTrace()) {
            error += stackTrace + "\n";
        }
        JOptionPane.showMessageDialog(null, error, "Problem!", 0);
        logAndPrint("\n" + error + "\n");
    }

    public static void error(String message) {
        Main.addMessage(message);
        logAndPrint("\n" + message + "\n");
    }

    public static void javaError(String message) {
        JOptionPane.showMessageDialog(null, message, "Problem!", 0);
        logAndPrint("\n" + message + "\n");
    }

    public static void warring(String string, Object object) {
        System.out.println(string + " in CLASS " + object.getClass().getName() + " AT METHOD " + object.getClass().getEnclosingMethod().getName());
    }
}
