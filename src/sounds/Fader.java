/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sounds;

import java.util.ArrayList;

/**
 * @author Wojtek
 */
class Fader {

    private final static long period = 10;
    private final ArrayList<FadeData> fadingSounds;
    private final ArrayList<FadeData> soundsToClear;
    private Thread thread;
    private boolean running, waiting;

    Fader() {
        fadingSounds = new ArrayList<>();
        soundsToClear = new ArrayList<>();
    }

    void fadeSound(Sound sound, int time, boolean pausing) {
        fadingSounds.add(new FadeData(sound, time, sound.getVolume(), 0f, pausing, false));
        if (waiting) {
            thread.interrupt();
        }
    }

    void resumeSound(Sound sound, int time, float endVolume) {
        for (FadeData fd : fadingSounds) {
            if (fd.sound == sound && fd.goalVolume == 0f) {
                fd.goalVolume = 1f;
            }
        }
        fadingSounds.add(new FadeData(sound, time, 0f, endVolume, false, true));
        if (waiting) {
            thread.interrupt();
        }
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
        for (FadeData fd : fadingSounds) {
            fd.unlockSound();
        }
        fadingSounds.clear();
        for (FadeData fd : soundsToClear) {
            fd.unlockSound();
        }
        soundsToClear.clear();
    }

    private synchronized void fadeSounds() {
        for (FadeData fd : fadingSounds) {
            fd.lockSound();
            fd.refresh();
            if (fd.isDone()) {
                fd.end();
                soundsToClear.add(fd);
            }
        }
        if (!soundsToClear.isEmpty()) {
            for (FadeData fd : soundsToClear) {
                fd.unlockSound();
                fadingSounds.remove(fd);
            }
            soundsToClear.clear();
        }
    }

    private boolean isWorkLeft() {
        return !fadingSounds.isEmpty();
    }

    private class FadeData {

        Sound sound;
        int time, periods, current;
        float startVolume, goalVolume, delta;
        boolean pause, start;

        FadeData(Sound sound, int time, float startVolume, float goalVolume, boolean pause, boolean start) {
            this.sound = sound;
            this.time = time;
            this.periods = (int) (time / period);
            this.startVolume = startVolume;
            this.goalVolume = goalVolume;
            this.delta = (goalVolume - startVolume) / periods;
            this.pause = pause;
            this.start = start;
            current = 0;
        }

        void end() {
            if (goalVolume == 0f) {
                if (pause) {
                    sound.pause();
                } else {
                    sound.stop();
                }
            }
        }

        void refresh() {
            if (isWorking()) {
                sound.addVolumeAndUpdate(delta);
                current++;
            }
        }

        void unlockSound() {
            if (start) {
                sound.setFadingToResume(false);
            } else {
                sound.setFadingToStop(false);
            }
        }
        
        void lockSound() {
            if (start) {
                sound.setFadingToResume(true);
            } else {
                sound.setFadingToStop(true);
            }
        }
        
        boolean isWorking() {
            return current <= periods || (goalVolume == 0f && sound.getVolume() > 0);
        }
        
        boolean isDone() {
            return !isWorking();
        }
    }
}
