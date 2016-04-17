/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sounds;

import java.util.ArrayList;

/**
 *
 * @author Wojtek
 */
class Fader {

    private final static long period = 10;
    private Thread thread;
    private boolean running, waiting;

    private final ArrayList<FadeData> fadingSounds;
    private final ArrayList<FadeData> soundsToClear;

    void fadeSound(Sound sound, int time, boolean pausing) {
        fadingSounds.add(new FadeData(sound, time, sound.getVolume(), 0f, pausing));
        if (waiting) {
            thread.interrupt();
        }
    }

    void resumeSound(Sound sound, int time, float endVolume) {
        fadingSounds.add(new FadeData(sound, time, 0f, endVolume, false));
        if (waiting) {
            thread.interrupt();
        }
    }

    Fader() {
        fadingSounds = new ArrayList<>();
        soundsToClear = new ArrayList<>();
    }

    public void start() {
        running = true;
        waiting = false;
        thread = new Thread(() -> {
            while (running) {
                waiting = false;
                if (running) {
                    try {
                        Thread.sleep(period);
                    } catch (InterruptedException ex) {
                    }
                }
                fadeSounds();
                if (running && !isWorkLeft()) {
                    try {
                        waiting = true;
                        Thread.sleep(3600000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    public void stop() {
        running = false;
        thread.interrupt();
        while (thread.isAlive()) {
        }
        fadingSounds.clear();
        soundsToClear.clear();
    }

    private synchronized void fadeSounds() {
        for (FadeData fd : fadingSounds) {
            fd.refresh();
            if (fd.isDone()) {
                fd.end();
                soundsToClear.add(fd);
            }
        }
        if (!soundsToClear.isEmpty()) {
            for (FadeData fd : soundsToClear) {
                fadingSounds.remove(fd);
            }
            soundsToClear.clear();
        }
    }

    public boolean isSoundFading(Sound sound) {
        for (FadeData fd : fadingSounds) {
            if (fd.sound == sound) {
                return true;
            }
        }
        return false;
    }

    private boolean isWorkLeft() {
        return !fadingSounds.isEmpty();
    }

    private class FadeData {

        Sound sound;
        int time, periods, current;
        float startVolume, goalVolume, delta;
        boolean pause;

        FadeData(Sound sound, int time, float startVolume, float goalVolume, boolean pause) {
            this.sound = sound;
            this.time = time;
            this.periods = (int) (time / period);
            this.startVolume = startVolume;
            this.goalVolume = goalVolume;
            this.delta = (float) (goalVolume - startVolume) / periods;
            this.pause = pause;
            current = 0;
        }

        void end() {
            if (sound.getCurrentVolume() <= 0.01f) {
                if (pause) {
                    sound.pause();
                } else {
                    sound.stop();
                }
            }
        }

        void refresh() {
            if (current <= periods) {
                sound.setVolumeAndUpdate(startVolume + current * delta);
                current++;
            }
        }

        boolean isDone() {
            return current > periods;
        }
    }
}
