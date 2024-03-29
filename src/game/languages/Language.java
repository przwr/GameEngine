/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.languages;

/**
 * @author przemek
 */
public class Language {

    public final Menu menu = new Menu();
    public final GUI gui = new GUI();
    public String lang;

    //Menu && Errors
    public class Menu {

        public final String[] Actions = new String[36];
        public String Menu;
        public String Start;
        public String LocalGame;
        public String OnlineGame;
        public String RunServer;
        public String JoinServer;
        public String ServerIP;
        public String Port;
        public String FindServer;
        public String Searching;
        public String Found;
        public String NotFound;
        public String UnableToConnect;
        public String Disconnected;
        public String FullServer;
        public String Options;
        public String End;
        public String Quit;
        public String Number_Of_Players;
        public String SplitScreen;
        public String Vertical;
        public String Horizontal;
        public String Brightness;
        public String SoundVolume;
        public String MusicVolume;
        public String Gamma;
        public String Resolution;
        public String FullScreen;
        public String On;
        public String Off;
        public String Resume;
        public String Apply;
        public String EndGame;
        public String StartGame;
        public String Language;
        public String Controls;
        public String Default;
        public String WSAD;
        public String Arrows;
        public String Gamepad;
        public String Player1;
        public String Player2;
        public String Player3;
        public String Player4;
        public String PushButton;
        public String Unchangeable;
        public String VSync;
        public String ShadowOff;
        public String SmoothShadows;
        public String JoinSS;
        public String FBOError;
        public String Unsupported;
        public String MustBeClose;
        public String Empty;
        public String Loading;
        public String Video;
        public String Gameplay;
        public String Text;
        public String FramesLimit;
        public String FramesAdjustment;
        public String Sound;
    }

    public class GUI {
        public String Equipment;
        public String Outfit;
    }
}
