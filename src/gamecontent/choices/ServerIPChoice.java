/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.choices;

import engine.inout.AnalizerSettings;
import game.Settings;
import game.gameobject.menu.MenuChoice;
import game.place.Menu;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author przemek
 */
public class ServerIPChoice extends MenuChoice {

    private Thread thread;
    private final Runnable run;
    private char[] temp;
    private String val;
    private int position;
    private boolean[] keys = new boolean[25];

    public ServerIPChoice(String label, Menu menu) {
        super(label, menu);
        temp = new char[15];
        val = new String(temp);
        run = () -> {
            while (true) {
                char c;
                if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                    end();
                    break;
                }
                if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
                    if (!keys[22]) {
                        keys[22] = true;
                        Settings.serverIP = val.replace(" ", "");
                        AnalizerSettings.update();
                        end();
                        break;
                    }
                } else {
                    keys[22] = false;
                }
                if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_NUMPADENTER)) {
                    Settings.serverIP = val.replace(" ", "");
                    AnalizerSettings.update();
                    end();
                    break;
                }
                if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
                    if (!keys[23]) {
                        keys[23] = true;
                        if (position > 0) {
                            temp[--position] = ' ';
                            val = new String(temp);
                        }
                    }
                } else {
                    keys[23] = false;
                }
                if (position != 15) {
                    c = checkDigitsKeys();
                    if (c != ' ') {
                        temp[position++] = c;
                        val = new String(temp);
                    }
                }
            }
        };
    }

    @Override

    public void action() {
        for (int i = 0; i < 15; i++) {
            temp[i] = ' ';
        }
        val = new String(temp);
        position = 0;
        keys[22] = true;
        menu.isMapping = true;
        thread = new Thread(run);
        thread.start();
    }

    @Override
    public String getLabel() {
        if (thread != null) {
            return label + val;
        } else {
            return label + Settings.serverIP;
        }
    }

    private void end() {
        thread = null;
        menu.delay.start();
        menu.isMapping = false;
    }

    private char checkDigitsKeys() {
        for (int k = 2; k < 11; k++) {
            if (Keyboard.isCreated() && Keyboard.isKeyDown(k)) {
                if (!keys[k - 2]) {
                    keys[k - 2] = true;
                    return Character.forDigit(k - 1, 10);
                }
            } else {
                keys[k - 2] = false;
            }
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_0)) {
            if (!keys[9]) {
                keys[9] = true;
                return '0';
            }
        } else {
            keys[9] = false;
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0)) {
            if (!keys[10]) {
                keys[10] = true;
                return '0';
            }
        } else {
            keys[10] = false;
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1)) {
            if (!keys[11]) {
                keys[11] = true;
                return '1';
            }
        } else {
            keys[11] = false;
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)) {
            if (!keys[12]) {
                keys[12] = true;
                return '2';
            }
        } else {
            keys[12] = false;
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD3)) {
            if (!keys[13]) {
                keys[13] = true;
                return '3';
            }
        } else {
            keys[13] = false;
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)) {
            if (!keys[14]) {
                keys[14] = true;
                return '4';
            }
        } else {
            keys[14] = false;
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)) {
            if (!keys[15]) {
                keys[15] = true;
                return '5';
            }
        } else {
            keys[15] = false;
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6)) {
            if (!keys[16]) {
                keys[16] = true;
                return '6';
            }
        } else {
            keys[16] = false;
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD7)) {
            if (!keys[17]) {
                keys[17] = true;
                return '7';
            }
        } else {
            keys[17] = false;
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD8)) {
            if (!keys[18]) {
                keys[18] = true;
                return '8';
            }
        } else {
            keys[18] = false;
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_NUMPAD9)) {
            if (!keys[19]) {
                keys[19] = true;
                return '9';
            }
        } else {
            keys[19] = false;
        }
        if (Keyboard.isCreated() && Keyboard.isKeyDown(Keyboard.KEY_PERIOD)) {
            if (!keys[21]) {
                keys[21] = true;
                return '.';
            }
        } else {
            keys[21] = false;
        }
        return ' ';
    }
}
