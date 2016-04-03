/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject.items;

import engine.utilities.Drawer;

/**
 * @author Wojtek
 */
public class ArrowTail extends TailEffect {
    private final Arrow arrow;

    public ArrowTail(int length, float width, Arrow arrow) {
        super(length, width);
        this.arrow = arrow;
    }

    @Override
    public void innerRender() {
        Drawer.regularShader.resetTransformationMatrix();
        Drawer.setColorBlended(color);
        Drawer.regularShader.setUseTexture(false);
        if (length >= 3 && tail[2] != null) {
            float vertices[] = new float[8 + last * 4];
            vertices[0] = tail[0].x;
            vertices[1] = tail[0].y - tail[0].height;
            vertices[2] = tail[1].getX(width, true);
            vertices[3] = tail[1].getY(width, true);
            vertices[4] = tail[1].getX(width, false);
            vertices[5] = tail[1].getY(width, false);
            int i;
            for (i = 1; i < last; i++) {
                vertices[2 + i * 4] = tail[i].getX(calcWidth(i), true);
                vertices[2 + i * 4 + 1] = tail[i].getY(calcWidth(i), true);
                vertices[2 + i * 4 + 2] = tail[i].getX(calcWidth(i), false);
                vertices[2 + i * 4 + 3] = tail[i].getY(calcWidth(i), false);
            }
            vertices[2 + i * 4] = tail[i - 1].getX(calcWidth(i - 1), true);
            vertices[2 + i * 4 + 1] = tail[i - 1].getY(calcWidth(i - 1), true);
            vertices[2 + i * 4 + 2] = tail[i - 1].getX(calcWidth(i - 1), false);
            vertices[2 + i * 4 + 3] = tail[i - 1].getY(calcWidth(i - 1), false);
            vertices[2 + i * 4 + 4] = tail[i].x;
            vertices[2 + i * 4 + 5] = tail[i].y - tail[i].height;
            Drawer.streamVBO.renderTriangleStripStream(vertices);
        } else {
            Joint tmp = new Joint((tail[0].x + tail[0].x) / 2,
                    (tail[0].y + tail[0].y) / 2,
                    (tail[0].height + tail[0].height) / 2, tail[0].direction);
            float vertices[] = new float[12];
            vertices[0] = tail[0].x;
            vertices[1] = tail[0].y - tail[0].height;
            vertices[2] = tmp.getX(width, true);
            vertices[3] = tmp.getY(width, true);
            vertices[4] = tmp.getX(width, false);
            vertices[5] = tmp.getY(width, false);

            vertices[6] = tmp.getX(calcWidth(1), true);
            vertices[7] = tmp.getY(calcWidth(1), true);
            vertices[8] = tmp.getX(calcWidth(1), false);
            vertices[9] = tmp.getY(calcWidth(1), false);
            vertices[10] = tail[1].x;
            vertices[11] = tail[1].y - tail[1].height;
            Drawer.streamVBO.renderTriangleStream(vertices);
            Drawer.regularShader.setUseTexture(true);
        }
    }
}
