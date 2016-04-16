/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sounds;

/**
 *
 * @author Wojtek
 */
class Fader implements Runnable {

    private final static long period = 100;

    private final int time;
    private final Sound sound;
    private final float startVolume, goalVolume;
    private final boolean pause;

    static Fader createFading(Sound sound, int time, boolean pausing) {
        return new Fader(time, sound, sound.getVolume(), 0f, pausing);
    }
    
    static Fader createResuming(Sound sound, int time, float endVolume) {
        return new Fader(time, sound, 0f, endVolume, false);
    }
    
    private Fader(int time, Sound snd, float startVolume, float goalVolume, boolean pause) {
        this.time = time;
        this.sound = snd;
        this.startVolume = startVolume;
        this.goalVolume = goalVolume;
        this.pause = pause;
    }

    private void end() {
        if (goalVolume == 0f) {
            if (pause) {
                sound.pause();
            } else {
                sound.stop();
            }
        }
        sound.setFading(false);
    }

    @Override
    public void run() {
        if (!sound.isPlaying()) {
            sound.play();
        }
        int periods = (int) (time / period);
        float delta = (float) (goalVolume - startVolume) / periods;
        sound.setFading(true);
        for (int i = 0; i < periods; i++) {
            try {
                Thread.sleep(period);
                sound.setVolumeAndUpdate(startVolume + i * delta);
            } catch (InterruptedException e) {
                end();
                break;
            }
        }
        end();
    }
}
