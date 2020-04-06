package edu.ktu.lab2;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lab2 implements GLEventListener {
    private float rtri = 0;  //for angle of rotation
    private GLU glu = new GLU();

    private int vao[] = new int[1];
    private int vbo[] = new int[2];
    private static String vertexDataPath = "vertex_array.txt";
    private static float[] vertexData;

    final int numLines = 2;

    private float qPositions[] = {
            -0.50f,0.50f,0.0f,
            0.50f,0.50f,0.0f,
            0.50f,-0.50f,0.0f,
            -0.50f,-0.50f,0.0f
    };

    public static void main(String[] args) {
        vertexData = readVertexData();

        //getting the capabilities object of GL2 profile
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // The canvas
        final GLCanvas glcanvas = new GLCanvas(capabilities);
        Lab2 l = new Lab2();
        glcanvas.addGLEventListener(l);
        glcanvas.setSize(800, 600);

        //creating frame
        final JFrame frame = new JFrame("Lab 2 Initials");

        //adding canvas to frame
        frame.getContentPane().add(glcanvas);

        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);

        //Instantiating and Initiating Animator
        final FPSAnimator animator = new FPSAnimator(glcanvas, 300, true);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glGenVertexArrays(1, IntBuffer.wrap(vao));
        gl.glBindVertexArray(vao[0]);

        gl.glGenBuffers(2, IntBuffer.wrap(vbo));
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);

        FloatBuffer vBuf = Buffers.newDirectFloatBuffer(vertexData);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vBuf.limit() * 4, vBuf, GL2.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, false, 0, 0); // associate 0th vertex attribute with active buffer
        gl.glEnableVertexAttribArray(0); // enable the 0th vertex sttribute
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();

        // Clear The Screen And The Depth Buffer
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();  // Reset The View
        gl.glTranslatef(0, 10f, -50f);
        gl.glRotatef(this.rtri, 0f, 1.0f, 0f);

        // Draw arrays
        gl.glDrawArrays(GL2.GL_QUADS, 0, vertexData.length/3); // Starting from point 0; total points is numLines * 2

        gl.glFlush();
        rtri += 0.2f;  //assigning the angle

    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int arg1, int arg2, int width, int height) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        if (height <= 0)
            height = 1;

        final float aspect = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45.0f, aspect, 0.1f, 1000.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    private static float[] readVertexData() {
        List<Float> vertexData = new ArrayList<>();
        float[] primitiveVertexData = new float[0];
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            File file = new File(classLoader.getResource(vertexDataPath).getFile());
            Scanner scanner = new Scanner(file);

            while (scanner.hasNext()) {
                vertexData.add(scanner.nextFloat());
            }
            primitiveVertexData = new float[vertexData.size()];
            for (int i = 0; i < primitiveVertexData.length; i++) {
                primitiveVertexData[i] = vertexData.get(i);
            }
            return primitiveVertexData;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return primitiveVertexData;
    }
}
