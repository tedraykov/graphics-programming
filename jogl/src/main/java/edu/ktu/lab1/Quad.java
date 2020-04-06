package edu.ktu.lab1;

import com.jogamp.opengl.GL2;

import static java.lang.System.arraycopy;

class Quad {
    private float[][] coordinates = new float[4][3];
    private float[][] textureVertex = {
            {0f,0f},
            {1f,0f},
            {1f,1f},
            {0f,1f}
    };
    Quad(float[][] coordinates) {
        arraycopy(coordinates, 0, this.coordinates, 0, 4);
    }

    float[][] getCoordinates() {
        return coordinates;
    }

    void glVertex(GL2 gl) {
        for (int i = 0; i < 4; i++) {
            gl.glTexCoord2f(this.textureVertex[i][0], this.textureVertex[i][1]);
            gl.glVertex3f(this.coordinates[i][0], this.coordinates[i][1], this.coordinates[i][2]);
        }
    }

    Quad translate(float x, float y, float z) {
        float[][] newCoordinates = new float[4][3];
        for (int i = 0; i < 4; i++) {
            newCoordinates[i] = new float[]{
                    this.coordinates[i][0] + x,
                    this.coordinates[i][1] + y,
                    this.coordinates[i][2] + z
            };
        }
        return new Quad(newCoordinates);
    }
}
