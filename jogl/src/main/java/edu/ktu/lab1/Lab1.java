package edu.ktu.lab1;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lab1 implements GLEventListener {
    private GLU glu = new GLU();
    private final float frontPlaneDepth = -0.2f;
    private final float letterDepth = 2f;
    private float rtri = 0;  //for angle of rotation
    private int brickTexture;

    private static String vertexExportFilename = "vertex_array.txt";
    private double[][] vertexData = {
            // T rects
            {0.0, 0.0},
            {0.0, 3.18},
            {14.26, 3.18},
            {14.26, 0.0},

            {5.03, 3.18},
            {9.14, 3.18},
            {9.14, 17.06},
            {5.03, 17.06},

            // R rects
            {16.01, 0.0},
            {20.12, 0.0},
            {20.12, 17.06},
            {16.01, 17.06},

            {20.12, 0.0},
            {27.47, 0.0},
            {24.08, 3.18},
            {20.12, 3.18},

            {24.08, 3.18},
            {27.47, 0.0},
            {29.18, 5.16},
            {25.08, 5.53},

            {25.08, 5.53},
            {29.18, 5.16},
            {27.47, 10.01},
            {24.08, 7.86},

            {24.08, 7.86},
            {27.47, 10.01},
            {26.2, 10.01},
            {20.12, 7.86},

            {20.12, 7.86},
            {26.2, 10.01},
            {22.35, 11.04},
            {20.12, 11.04},

            {22.35, 11.04},
            {26.2, 10.01},
            {29.94, 17.06},
            {25.36, 17.06}
    };

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();  // Reset The View
        gl.glTranslatef(0, 10f, -50f);
        gl.glRotatef(this.rtri, 0f, 1.0f, 0f);

        // Front face
        List<Quad> frontQuads = readQuads();
        frontQuads = frontQuads.stream()
                .map(x -> x.translate(-15f, 0, 0))
                .collect(Collectors.toList());

        // Back face
        List<Quad> backQuads = frontQuads.stream()
                .map(x -> x.translate(0, 0, this.letterDepth))
                .collect(Collectors.toList());

        // All sides
        List<Quad> sideQuads = getSideQuads(frontQuads, -this.letterDepth);


        gl.glBindTexture(GL2.GL_TEXTURE_2D, this.brickTexture);
        // Drawing Using Quads
        gl.glBegin(GL2.GL_QUADS);

        for (Quad quad : backQuads) {
//            gl.glColor3f(0.0f, 0.0f, 1.0f);
            quad.glVertex(gl);
        }

        for (Quad quad : frontQuads) {
//            gl.glColor3f(1.0f, 0.0f, 0.0f);
            quad.glVertex(gl);
        }

        for (Quad quad : sideQuads) {
//            gl.glColor3f(0.0f, 1.0f, 0.0f);
            quad.glVertex(gl);
        }
        gl.glEnd();
        gl.glFlush();

        rtri += 0.2f;
        List<Quad> mergedQuads = Stream.of(frontQuads, backQuads, sideQuads)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        generateVertexArray(mergedQuads);

//        gl.glEnable( GL2.GL_LIGHTING );
//        gl.glEnable( GL2.GL_LIGHT0 );
//        gl.glEnable( GL2.GL_NORMALIZE );
//
//        // multicolor diffuse
//        float[] diffuseLight = { 1f,1f,1f,0f };
//        gl.glLightfv( GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0 );
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
        //method body
    }

    @Override
    public void init(GLAutoDrawable arg0) {
        GL2 gl = arg0.getGL().getGL2();
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL2.GL_DEPTH_TEST);

        gl.glEnable(GL2.GL_TEXTURE_2D);

        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            File im = new File(Objects.requireNonNull(classLoader.getResource("texture.jpg")).getFile());
            Texture t = TextureIO.newTexture(im, true);
            this.brickTexture = t.getTextureObject(gl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

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

    public static void main(String[] args) {

        //getting the capabilities object of GL2 profile
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // The canvas
        final GLCanvas glcanvas = new GLCanvas(capabilities);
        Lab1 triangle = new Lab1();
        glcanvas.addGLEventListener(triangle);
        glcanvas.setSize(800, 600);

        //creating frame
        final JFrame frame = new JFrame(" Colored Triangle");

        //adding canvas to it
        frame.getContentPane().add(glcanvas);
        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);

        final FPSAnimator animator = new FPSAnimator(glcanvas, 300, true);
        animator.start();

    } //end of main

    private List<Quad> readQuads() {
        List<Quad> quads = new ArrayList<Quad>();
        for (int i = 0; i < vertexData.length; i += 4) {
            float[][] currentCoordinates = new float[4][3];
            for (int j = 0; j < 4; j++) {
                currentCoordinates[j] = new float[]{
                        (float) vertexData[i + j][0],
                        (float) vertexData[i + j][1] * -1,
                        (float) frontPlaneDepth};
            }
            quads.add(new Quad(currentCoordinates));
        }
        return quads;
    }

    private List<Quad> getSideQuads(List<Quad> faceQuads, float letterDepth) {
        List<Quad> sideQuads = new ArrayList<Quad>();
        for (Quad quad : faceQuads) {
            float[][] faceCoordinates = quad.getCoordinates();
            for (int i = 0; i < 4; i++) {
                float[][] currSideQuad = new float[4][3];
                currSideQuad[0] = Arrays.copyOf(faceCoordinates[i], 3);
                currSideQuad[1] = Arrays.copyOf(faceCoordinates[(i + 1) % 4], 3);
                currSideQuad[2] = Arrays.copyOf(faceCoordinates[(i + 1) % 4], 3);
                currSideQuad[3] = Arrays.copyOf(faceCoordinates[i], 3);
                currSideQuad[2][2] -= letterDepth;
                currSideQuad[3][2] -= letterDepth;

                sideQuads.add(new Quad(currSideQuad));
            }
        }
        return sideQuads;
    }

    private static void generateVertexArray(List<Quad> quads) {
        List<Float> arrayBuffer = new ArrayList<>();
        for (Quad quad :
                quads) {
            float[][] coordinates = quad.getCoordinates();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    arrayBuffer.add(coordinates[i][j]);
                }
            }
        }

        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            File file = new File(classLoader.getResource(vertexExportFilename).getFile());
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

            arrayBuffer.stream().forEach(x -> {
                try {
                    outputWriter.write((float)Math.round(x*100f)/100f + "");
                    outputWriter.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            outputWriter.flush();
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
