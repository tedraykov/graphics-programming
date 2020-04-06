package edu.ktu.lab2;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arunas
 */
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import com.jogamp.common.nio.*;
import java.nio.FloatBuffer;

import javax.swing.JFrame;

public class VBODemo implements GLEventListener {

    private float angle = 0.0f;  //for angle of rotation

    private int vao[] = new int[1]; // OpenGL requires these values be specified in arrays
    private int vbo[] = new int[2];
    final int numLines = 2;

    private float qPositions[] = {
            -0.50f,0.50f,0.0f,
            0.50f,0.50f,0.0f,
            0.50f,-0.50f,0.0f,
            -0.50f,-0.50f,0.0f
    };

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();

        // Clear The Screen And The Depth Buffer
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();  // Reset The View

        // Rotation
        gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);

        // Draw arrays
        gl.glDrawArrays(GL2.GL_LINES, 0, numLines * 2); // Starting from point 0; total points is numLines * 2

        gl.glFlush();
        angle += 0.1f;  //assigning the angle

    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
        //method body
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // method body

        final GL2 gl = drawable.getGL().getGL2();
        gl.glGenVertexArrays(1, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(2, vbo, 0);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[0]);  // make the 0th buffer "active"
        FloatBuffer vBuf = Buffers.newDirectFloatBuffer(qPositions);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vBuf.limit() * 4, vBuf, GL2.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, false, 0, 0); // associate 0th vertex attribute with active buffer
        gl.glEnableVertexAttribArray(0); // enable the 0th vertex sttribute
    }

    @Override
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
        // method body
    }

    public static void main(String[] args) {

        //getting the capabilities object of GL2 profile
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // The canvas
        final GLCanvas glcanvas = new GLCanvas(capabilities);
        VBODemo l = new VBODemo();
        glcanvas.addGLEventListener(l);
        glcanvas.setSize(400, 400);

        //creating frame
        final JFrame frame = new JFrame("First Line");

        //adding canvas to frame
        frame.getContentPane().add(glcanvas);

        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);

        //Instantiating and Initiating Animator
        final FPSAnimator animator = new FPSAnimator(glcanvas, 300, true);
        animator.start();

    }//end of main

}
