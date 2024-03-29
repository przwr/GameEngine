package engine;

import engine.utilities.ErrorHandler;
import game.Game;
import game.Settings;
import gamecontent.environment.Bush;
import gamecontent.environment.GrassClump;
import gamecontent.environment.Tree;
import java.io.File;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.GLSync;
import org.newdawn.slick.util.ResourceLoader;
import sprites.Sprite;
import sprites.SpriteBase;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.lwjgl.opengl.GL11.glFlush;
import static org.lwjgl.opengl.GL32.GL_SYNC_GPU_COMMANDS_COMPLETE;
import static org.lwjgl.opengl.GL32.glFenceSync;
import sounds.SoundBase;

/**
 * Created by przemek on 30.01.16.
 */
public abstract class BackgroundLoader {

    private static final int seconds = 1000, secondsToUnload = 90;
    public static SpriteBase base;
    private final ReentrantLock lock = new ReentrantLock();
    private final Map<String, Sprite> sprites = new HashMap<>();
    private GLSync fence;
    private Drawable drawable;
    private boolean running, pause, useFences, firstLoaded;
    private boolean firstSpriteActive, usingSprites, stopSpritesUsing;
    private ArrayList<Sprite> spritesList1 = new ArrayList<>();
    private ArrayList<Sprite> spritesList2 = new ArrayList<>();
    private ArrayList<Sprite> spritesToClear = new ArrayList<>();
    private Thread thread;
    private Game game;

    public BackgroundLoader() {
        running = true;
    }

    public List<Sprite> getSpritesToClear() {
        return spritesToClear;
    }

    abstract Drawable getDrawable() throws LWJGLException;

    public void cleanup() {
        running = false;
        spritesList1.clear();
        spritesList2.clear();
        spritesToClear.clear();
        for (String key : sprites.keySet()) {
            Sprite sprite = sprites.get(key);
            if (sprite.getTextureID() != 0 || sprite.getTexture() != null) {
                lock();
                sprite.releaseTexture();
                unlock();
            }
        }
        sprites.clear();
        base = null;
    }

    public void start() throws LWJGLException {
        drawable = getDrawable();
        thread = new Thread(() -> {
            try {
                drawable.makeCurrent();
            } catch (LWJGLException e) {
                throw new RuntimeException(e);
            }
            useFences = GLContext.getCapabilities().OpenGL32;
            while (running) {
                try {
                    loadTextures();
                    loadSounds();
                    if (spritesList1.isEmpty() && spritesList2.isEmpty()/*) && Settings.sounds != null*/) {
                        try {
                            Thread.sleep(3600000);
                        } catch (InterruptedException e) {
                        }
                    }
                } catch (Exception exception) {
                    ErrorHandler.swallowLogAndPrint(exception);
                }
            }
            drawable.destroy();
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private void loadTextures() {
        if (!pause) {
            List<Sprite> workingList = firstSpriteActive ? spritesList1 : spritesList2;
            if (!workingList.isEmpty()) {
                if (workingList != null) {
                    for (int i = 0; i < workingList.size(); i++) {
                        Sprite sprite = workingList.get(i);
                        if (sprite.getTextureID() == 0) {
                            InputStream stream = ResourceLoader.getResourceAsStream(sprite.getPath());
                            lock();
                            loadTexture(sprite, stream);
                            try {
                                stream.close();
                            } catch (IOException e) {
                            }
                            unlock();
                        }
                        spritesToClear.add(sprite);
                    }
                    workingList.removeAll(spritesToClear);
                    addToSprites();
                }
            } else if (spritesList1.isEmpty() && spritesList2.isEmpty()/* && Settings.sounds != null*/) {
                unloadTextures();
            }
            firstSpriteActive = !firstSpriteActive;
        }
    }

    private void addToSprites() {
        while (stopSpritesUsing) {
        }
        usingSprites = true;
        for (Sprite sprite : spritesToClear) {
            if (sprites.get(sprite.getPath()) == null) {
                sprites.put(sprite.getPath(), sprite);
            }
        }
        usingSprites = false;
        spritesToClear.clear();
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
                    if (sprite.getLastUsed() > 0 && (now - sprite.getLastUsed()) > secondsToUnload * seconds) {
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

    public void unloadAllTextures() {
        while (usingSprites) {
        }
        usingSprites = true;
        if (!stopSpritesUsing) {
            if (base != null) {
                Iterator it = base.getSprites().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    Sprite sprite = (Sprite) pair.getValue();
                    if (sprite.getTextureID() != 0 || sprite.getTexture() != null) {
                        lock();
                        sprite.releaseTexture();
                        unlock();
                    }
                }
                base.getSprites().clear();
            }
            spritesList1.clear();
            spritesList2.clear();
            spritesToClear.clear();
            for (String key : sprites.keySet()) {
                Sprite sprite = sprites.get(key);
                if (sprite.getTextureID() != 0 || sprite.getTexture() != null) {
                    lock();
                    sprite.releaseTexture();
                    unlock();
                }
            }
            sprites.clear();
            base = null;
        }
        usingSprites = false;
    }

    private void loadTexture(Sprite sprite, InputStream stream) {
        sprite.setTexture(SpriteBase.loadTextureFromStream(stream, sprite.AA));
//        System.out.println("Texture loaded: " + sprite.path);
    }

    private void lock() {
        lock.lock();
    }

    private void unlock() {
        if (useFences) {
            fence = glFenceSync(GL_SYNC_GPU_COMMANDS_COMPLETE, 0);
        } else {
            glFlush();
        }
        lock.unlock();
    }

    public synchronized void requestSprite(Sprite sprite) {
        pause = true;
        List<Sprite> workingList = firstSpriteActive ? spritesList2 : spritesList1;
        workingList.add(sprite);
        thread.interrupt();
        pause = false;
    }

    private void loadSounds() {
        if (!Settings.sounds.isInited() && spritesList1.isEmpty() && spritesList2.isEmpty() && game != null && game.getPlace() != null) {
            //Settings.sounds.init();
        }
    }

    public boolean allLoaded() {
        boolean allLoaded = spritesList1.isEmpty() && spritesList2.isEmpty() && Tree.allGenerated() && Bush.allGenerated() && GrassClump.allGenerated();
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

    public void setGame(Game game) {
        this.game = game;
    }
}
