package game.text.fonts;

public class Character {

    private int id;
    private double xTextureCoord;
    private double yTextureCoord;
    private double xMaxTextureCoord;
    private double yMaxTextureCoord;
    private double xOffset;
    private double yOffset;
    private double xSize;
    private double ySize;
    private double xAdvance;

    protected Character(int id, double xTextureCoord, double yTextureCoord, double xTexSize, double yTexSize,
                        double xOffset, double yOffset, double xSize, double sizeY, double xAdvance) {
        this.id = id;
        this.xTextureCoord = xTextureCoord;
        this.yTextureCoord = yTextureCoord;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xSize = xSize;
        this.ySize = sizeY;
        this.xMaxTextureCoord = xTexSize + xTextureCoord;
        this.yMaxTextureCoord = yTexSize + yTextureCoord;
        this.xAdvance = xAdvance;
    }

    protected int getId() {
        return id;
    }

    protected double getXTextureCoord() {
        return xTextureCoord;
    }

    protected double getYTextureCoord() {
        return yTextureCoord;
    }

    protected double getXMaxTextureCoord() {
        return xMaxTextureCoord;
    }

    protected double getYMaxTextureCoord() {
        return yMaxTextureCoord;
    }

    protected double getXOffset() {
        return xOffset;
    }

    protected void setXOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    protected double getYOffset() {
        return yOffset;
    }

    protected double getXSize() {
        return xSize;
    }

    protected double getYSize() {
        return ySize;
    }

    protected double getXAdvance() {
        return xAdvance;
    }

}
