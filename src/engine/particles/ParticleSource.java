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
    private static RandomGenerator random = RandomGenerator.create();
    private float ppf;
    private int particlesCount;
    private float speed;
    private float gravity, drag;
    private float lifeLength;
    private Particle[] particles;
    private SpriteSheet spriteSheet;

    public ParticleSource(float ppf, float speed, float gravity, float drag, float lifeLength, SpriteSheet spiteSheet) {
        this.ppf = ppf;
        this.speed = speed;
        this.drag = drag;
        this.gravity = gravity;
        this.lifeLength = lifeLength;
        this.particles = new Particle[INITIAL_POINT_COUNT];
        this.spriteSheet = spiteSheet;
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
        float dirH = (random.randomInRange(-10000, 10000) / 10000f);
        particles[position].set(x + (int) dirX, y + (int) dirY, floatHeight + (int) dirH, dirX * speed, dirY * speed, dirH * speed,
                lifeLength * (0.75f + (random.randomInRange(0, 10000) / 20000f)));
    }

    private void emitParticle(int x, int y, int floatHeight) {
        float dirX = (random.randomInRange(-10000, 10000) / 10000f);
        float dirY = (random.randomInRange(-10000, 10000) / 10000f);
        float dirH = (random.randomInRange(-10000, 10000) / 10000f);
        add(x + (int) dirX, y + (int) dirY, floatHeight + (int) dirH, dirX * speed, dirY * speed, dirH * speed,
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

        int size = spriteSheet.getSize();
        float stage;
//        TODO unroll the loop
//        TODO wczytywanie uniforms
        int index = 0;
        for (int i = 0; i < particlesCount; i++) {
            if (!particles[i].dead) {
                stage = (size * particles[i].getLifePercent());
                if (stage > size - 1)
                    stage = size - 1;
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
        spriteSheet.bindCheck();
        Drawer.streamVBO.renderTexturedTriangles(0, Drawer.streamVBO.getVertexCount());
        Drawer.regularShader.start();
    }

}
