package engine.particles;


import org.lwjgl.util.vector.Vector3f;

public class Particle implements Comparable {

    protected boolean dead;
    float lifeLength;
    float elapsedTime;
    private Vector3f position = new Vector3f();
    private Vector3f velocity = new Vector3f();


    public Particle(int x, int y, int floatHeight, float xVelocity, float yVelocity, float heightVelocity, float lifeLength) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = floatHeight;
        this.velocity.x = xVelocity;
        this.velocity.y = yVelocity;
        this.velocity.z = heightVelocity;
        this.lifeLength = lifeLength;
    }

    public void set(int x, int y, int floatHeight, float xVelocity, float yVelocity, float heightVelocity, float lifeLength) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = floatHeight;
        this.velocity.x = xVelocity;
        this.velocity.y = yVelocity;
        this.velocity.z = heightVelocity;
        this.lifeLength = lifeLength;
        this.elapsedTime = 0;
        this.dead = false;
    }

    protected boolean update(double gravity, double drag, double timeChange) {
        elapsedTime += timeChange;
        if (elapsedTime > lifeLength) {
            dead = true;
            return true;
        }
        if (position.z > 0 || velocity.z > 0) {
            position.z += velocity.z * timeChange;
            velocity.z -= gravity * timeChange;
            if (position.z < 0) {
                position.z = 0;
            }
            if (velocity.x != 0) {
                position.x += velocity.x * timeChange;
                velocity.x -= Math.signum(velocity.x) * drag * timeChange;
                if (Math.abs(velocity.x) < drag * timeChange) {
                    velocity.x = 0;
                }
            }
            if (velocity.y != 0) {
                position.y += velocity.y * timeChange;
                velocity.y -= Math.signum(velocity.y) * drag * timeChange;
                if (Math.abs(velocity.y) < drag * timeChange) {
                    velocity.y = 0;
                }
            }
        }
        return false;
    }


    public int getX() {
        return (int) position.x;
    }

    public void setX(int x) {
        this.position.x = x;
    }

    public int getYWithFloatHeight() {
        return (int) (position.y - position.z);
    }

    public int getY() {
        return (int) position.y;
    }

    public void setY(int y) {
        this.position.y = y;
    }

    public int getFloatHeight() {
        return (int) position.z;
    }

    public void setFloatHeight(float floatHeight) {
        this.position.z = floatHeight;
    }

    public float getXVelocity() {
        return velocity.x;
    }

    public void setXVelocity(float xVelocity) {
        this.velocity.x = xVelocity;
    }

    public float getYVelocity() {
        return velocity.y;
    }

    public void setYVelocity(float yVelocity) {
        this.velocity.y = yVelocity;
    }

    public float getHeightVelocity() {
        return velocity.z;
    }

    public void setHeightVelocity(float heightVelocity) {
        this.velocity.z = heightVelocity;
    }

    public float getLifePercent() {
        return elapsedTime / lifeLength;
    }

    @Override
    public int compareTo(Object o) {
        return getYWithFloatHeight() - ((Particle) o).getYWithFloatHeight();
    }
}

