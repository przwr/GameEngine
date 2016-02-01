package engine;

import engine.utilities.ErrorHandler;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.GLSync;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import sprites.Sprite;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.glFlush;
import static org.lwjgl.opengl.GL32.GL_SYNC_GPU_COMMANDS_COMPLETE;
import static org.lwjgl.opengl.GL32.glFenceSync;

/**
 * Created by przemek on 30.01.16.
 */
public abstract class BackgroundLoader {

    private static final int seconds = 1000, secondsToUnload = 90;
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<String, Sprite> sprites = new HashMap<>();
    List<Sprite> toClear = new ArrayList<>();
    private GLSync fence;
    private Drawable drawable;
    private boolean running, firstActive, pause, useFences, firstLoaded, usingSprites, stopSpritesUsing;
    private ArrayList<Sprite> list1 = new ArrayList<>();
    private ArrayList<Sprite> list2 = new ArrayList<>();

    public BackgroundLoader() {
        running = true;
    }

    abstract Drawable getDrawable() throws LWJGLException;

    public void cleanup() {
        list1.clear();
        list2.clear();
        toClear.clear();
        running = false;
    }

    void start() throws LWJGLException {
        drawable = getDrawable();
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    drawable.makeCurrent();
                } catch (LWJGLException e) {
                    throw new RuntimeException(e);
                }
                useFences = GLContext.getCapabilities().OpenGL32;
                while (running) {
                    try {
                        loadTexture();
                    } catch (Exception exception) {
                        ErrorHandler.swallowLogAndPrint(exception);
                    }
                }
                drawable.destroy();
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private void loadTexture() {
        if (!pause) {
            List<Sprite> workingList = firstActive ? list1 : list2;
            if (!workingList.isEmpty()) {
                if (workingList != null) {
                    for (int i = 0; i < workingList.size(); i++) {
                        Sprite sprite = workingList.get(i);
                        if (sprite.getTextureID() == 0) {
                            InputStream stream = ResourceLoader.getResourceAsStream(sprite.getPath());
                            lock();
                            loadTexture(sprite, stream);
                            unlock();
                            toClear.add(sprite);
                        } else {
                            toClear.add(sprite);
                        }
                    }
                    workingList.removeAll(toClear);
                    addToSprites();
                }
            } else if (list1.isEmpty() && list2.isEmpty()) {
                unloadTextures();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            firstActive = !firstActive;
        }
    }

    private void addToSprites() {
        while (stopSpritesUsing) {
        }
        usingSprites = true;
        for (Sprite sprite : toClear) {
            if (sprites.get(sprite.getPath()) == null) {
                sprites.put(sprite.getPath(), sprite);
            }
        }
        usingSprites = false;
        toClear.clear();
    }

    private void unloadTextures() {
        usingSprites = true;
        if (!stopSpritesUsing) {
            if (firstLoaded) {
                long now = System.currentTimeMillis();
                for (String key : sprites.keySet()) {
                    Sprite sprite = sprites.get(key);
                    if (stopSpritesUsing) {
                        break;
                    }
                    if ((now - sprite.getLastUsed()) > secondsToUnload * seconds) {
                        if (sprite.getTextureID() != 0 || sprite.getTexture() != null) {
                            lock();
                            sprite.releaseTexture();
                            unlock();
                        }
                    }
                }
            }
        }
        usingSprites = false;
    }

    private void loadTexture(Sprite sprite, InputStream stream) {
        Texture tex = null;
        try {
            tex = TextureLoader.getTexture("png", stream, GL_NEAREST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sprite.setTexture(tex);
//        System.out.println("Texture loaded: " + sprite.path);
    }

    private void lock() {
        lock.lock();
    }

    private void unlock() {
        if (useFences)
            fence = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
        else
            glFlush();
        lock.unlock();
    }

    public synchronized void requestSprite(Sprite sprite) {
        pause = true;
        List<Sprite> workingList = firstActive ? list2 : list1;
        workingList.add(sprite);
        pause = false;
    }

    public boolean allLoaded() {
        boolean allLoaded = list1.isEmpty() && list2.isEmpty();
        if (!firstLoaded && allLoaded) {
            firstLoaded = true;
        }
        return allLoaded;
    }

    public boolean isFirstLoaded() {
        return firstLoaded;
    }

    public void resetFirstLoaded() {
        firstLoaded = false;
    }

    public synchronized void notifySprite(Sprite sprite) {
        while (usingSprites) {
        }
        stopSpritesUsing = true;
        if (sprites.get(sprite.getPath()) == null) {
            sprites.put(sprite.getPath(), sprite);
        }
        stopSpritesUsing = false;
    }
}
