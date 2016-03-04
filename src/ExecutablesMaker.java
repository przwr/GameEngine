/**
 * Created by przemek on 17.08.15.
 */

import org.ninjacave.jarsplice.core.ShellScriptSplicer;
import org.ninjacave.jarsplice.core.Splicer;
import org.ninjacave.jarsplice.core.WinExeSplicer;

public class ExecutablesMaker {
    public static void main(String[] args) {
//        UWAGA! Gra musi być najpierw skompilowana do .jar do folderu root (patrz niżej). NetBeans robi to automatycznie.


        ShellScriptSplicer shellScriptSplicer = new ShellScriptSplicer();
        WinExeSplicer winExeSplicer = new WinExeSplicer();
        Splicer splicer = new Splicer();

        String root = "dist/";
        String[] jars = {root + "GameEngine.jar", root + "lib/jinput.jar",
                root + "lib/jodk.jar", root + "lib/jogg-0.0.7.jar",
                root + "lib/jorbis-0.0.15.jar", root + "lib/kryonet-2.21-all.jar",
                root + "lib/lwjgl-natives.jar", root + "lib/lwjgl.jar",
                root + "lib/lwjgl_util.jar", root + "lib/slick-util.jar"};
        String[] natives = new String[0];
        String output = "data";
        String mainClass = "engine.Launcher";
        String vmArgs = "";

        try {
            splicer.createFatJar(jars, natives, output + ".crj", mainClass, vmArgs);
//            shellScriptSplicer.createFatJar(jars, natives, output + ".sh", mainClass, vmArgs);
//            winExeSplicer.createFatJar(jars, natives, output + ".exe", mainClass, vmArgs);
        } catch (Exception e) {
            System.out.println("Nie udało się stworzyć plików wykonywalnych:(\nBŁĘDY:");
            e.printStackTrace();
            return;
        }

        String currentDirectory = System.getProperty("user.dir");
        String gameFiles = currentDirectory + "/" + output;
//        System.out.println("Pliki wykonywalne gry stworzono! <('_')>\nPLIKI:\n" + gameFiles + ".sh\n" + gameFiles + ".exe");
        System.out.println("Plik wykonywalny gry stworzono! <('_')>\nPLIKI:\n" + gameFiles + ".crj");
    }
}