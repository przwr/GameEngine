package engine.particles;

import engine.systemcommunication.Time;
import engine.utilities.Drawer;
import engine.utilities.RandomGenerator;
import sprites.SpriteSheet;

import java.util.Arrays;

/**
 * Created by przemek on 27.04.16.
 */
public class ParticleSource {

    private static final int INITIAL_POINT_COUNT = 100;

    public static RandomGenerator random = RandomGenerator.create();
    private SpriteSheet spriteSheet;
    private Particle[] particles;
    private int particlesCount;
    private float ppf;
    private float xSpread = 1f, ySpread = 1f;
    private int frames = 1;
    private float gravity, drag;
    private float speed;
    private float lifeLength;
    private float xDirectionFactor = 1f;
    private float yDirectionFactor = 1f;
    private float xDirectionBalance;
    private float yDirectionBalance;

    public ParticleSource(float ppf, float speed, float gravity, float drag, float lifeLength, SpriteSheet spiteSheet) {
        this.ppf = ppf;
        this.speed = speed;
        this.drag = drag;
        this.gravity = gravity;
        this.lifeLength = lifeLength;
        this.particles = new Particle[INITIAL_POINT_COUNT];
        this.spriteSheet = spiteSheet;
        frames = spiteSheet.getSize();
    }

    public void add(int x, int y, int floatHeight, float xVelocity, float yVelocity, float heightVelocity, float lifeLength) {
        if (particles[particlesCount] != null) {
            particles[particlesCount++].set(x, y, floatHeight, xVelocity, yVelocity, heightVelocity, lifeLength);
        } else {
            particles[particlesCount++] = new Particle(x, y, floatHeight, xVelocity, yVelocity, heightVelocity, lifeLength);
        }
    }

    private void ensureCapacity(int capacity) {
        if (particlesCount + capacity > particles.length) {
            int newSize = (int) (1.5 * particles.length);
            if (newSize < particlesCount + capacity) {
                newSize = particlesCount + (int) (capacity * 1.5);
            }
            Particle[] tempParticles = new Particle[newSize];
            System.arraycopy(particles, 0, tempParticles, 0, particles.length);
            particles = tempParticles;
        }
    }

    public void clear() {
        for (int i = 0; i < particles.length; i++) {
            particles[i] = null;
        }
        particlesCount = 0;
    }

    public int size() {
        return particlesCount;
    }

    private void emitParticle(int position, int x, int y, int floatHeight) {
        float dirX = (random.randomInRange(-10000, 10000) / 10000f);
        float dirY = (random.randomInRange(-10000, 10000) / 10000f);
        float dirH = (random.randomInRange(0, 10000) / 10000f);
        particles[position].set(x + (int) (dirX * xSpread), y + (int) (dirY * ySpread), floatHeight, (xDirectionBalance + dirX) * speed / xDirectionFactor,
                (yDirectionBalance + dirY) * speed / yDirectionFactor, (0.75f + dirH) * speed,
                lifeLength * (0.75f + (random.randomInRange(0, 10000) / 20000f)));
    }

    private void emitParticle(int x, int y, int floatHeight) {
        float dirX = (random.randomInRange(-10000, 10000) / 10000f);
        float dirY = (random.randomInRange(-10000, 10000) / 10000);
        float dirH = (random.randomInRange(0, 10000) / 10000f);
        add(x + (int) (dirX * xSpread), y + (int) (dirY * ySpread), floatHeight, (xDirectionBalance + dirX) * speed / xDirectionFactor,
                (yDirectionBalance + dirY) * speed / yDirectionFactor, (0.75f + dirH) * speed,
                lifeLength * (0.75f + (random.randomInRange(0, 10000) / 20000f)));
    }

    public void updateParticles(int x, int y, int floatHeight) {
        float delta = Time.getDelta();
        int count = (int) (ppf * delta);
        ensureCapacity(count);
        int size = particlesCount;
        for (int i = 0; i < size; i++) {
            boolean dead = particles[i].update(gravity, drag, delta);
            if (dead && count > 0) {
                emitParticle(i, x, y, floatHeight);
                count--;
            }
        }
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                emitParticle(x, y, floatHeight);
            }
        }
        Arrays.sort(particles, 0, particlesCount);
    }

    public void render(int x, int y) {
        Drawer.particleShader.start();
        Drawer.streamVertexData.clear();
        Drawer.streamColorData.clear();
        Drawer.streamIndexData.clear();
        float stage;
        int index = 0;
        for (int i = 0; i < particlesCount; i++) {
            if (!particles[i].dead) {
                stage = (frames * particles[i].getLifePercent());
                if (stage > frames - 1)
                    stage = frames - 1;
                Drawer.streamVertexData.add(
                        particles[i].getX(), particles[i].getYWithFloatHeight(),
                        particles[i].getX(), particles[i].getYWithFloatHeight() + 8,
                        particles[i].getX() + 8, particles[i].getYWithFloatHeight() + 8,
                        particles[i].getX() + 8, particles[i].getYWithFloatHeight()
                );
                Drawer.streamColorData.add(stage, 2, stage, 0, stage, 1, stage, 3);
                Drawer.streamIndexData.add(index, index + 1, index + 3, index + 2, index + 3, index + 1);
                index += 4;
            }
        }
        Drawer.particleShader.translate(x, y);
        Drawer.streamVBO.updateAll(Drawer.streamVertexData.toArray(), Drawer.streamColorData.toArray(), Drawer.streamIndexData.toArray());
        Drawer.particleShader.loadFrames(spriteSheet.getXLimit(), spriteSheet.getYLimit());
        Drawer.particleShader.loadColorModifier(Drawer.getCurrentColor().r, Drawer.getCurrentColor().g, Drawer.getCurrentColor().b, Drawer.getCurrentColor().a);
        spriteSheet.bindCheck();
        Drawer.streamVBO.renderTexturedTriangles(0, Drawer.streamVBO.getVertexCount());
        Drawer.regularShader.start();
    }

    public float getXSpread() {
        return xSpread;
    }

    public void setXSpread(float xSpread) {
        this.xSpread = xSpread;
    }

    public float getPPF() {
        return ppf;
    }

    public void setPPF(float ppf) {
        this.ppf = ppf;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public float getDrag() {
        return drag;
    }

    public void setDrag(float drag) {
        this.drag = drag;
    }

    public float getLifeLength() {
        return lifeLength;
    }

    public void setLifeLength(float lifeLength) {
        this.lifeLength = lifeLength;
    }

    public float getYSpread() {
        return ySpread;
    }

    public void setYSpread(float ySpread) {
        this.ySpread = ySpread;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public float getXDirectionFactor() {
        return xDirectionFactor;
    }

    public void setXDirectionFactor(float xDirectionFactor) {
        this.xDirectionFactor = xDirectionFactor;
    }

    public float getYDirectionFactor() {
        return yDirectionFactor;
    }

    public void setYDirectionFactor(float yDirectionFactor) {
        this.yDirectionFactor = yDirectionFactor;
    }

    public float getXDirectionBalance() {
        return xDirectionBalance;
    }

    public void setXDirectionBalance(float xDirectionBalance) {
        this.xDirectionBalance = xDirectionBalance;
    }

    public float getYDirectionBalance() {
        return yDirectionBalance;
    }

    public void setYDirectionBalance(float yDirectionBalance) {
        this.yDirectionBalance = yDirectionBalance;
    }
}
