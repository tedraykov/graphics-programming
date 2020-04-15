package edu.ktu.lab2;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lab2 implements GLEventListener {
    private static int initialsTextureId;
    private float rtri = 0;  //for angle of rotation

    GLU glu = new GLU();
    private int renderProgram;
    private int vao[] = new int[1];
    private int vbo[] = new int[2];
    private static String vertexDataPath = "vertex_array.txt";
    private static float[] vertexData;
    private static float[] textureData;
    private static GLCanvas glcanvas;
    private static int mvp_loc;

    public static void main(String[] args) {
        vertexData = readVertexData();
        textureData = getTextureData(vertexData);

        //getting the capabilities object of GL2 profile
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // The canvas
        glcanvas = new GLCanvas(capabilities);
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

    private static float[] getTextureData(float[] vertexData) {
        float[] textureData = new float[(vertexData.length / 3) * 4];
        for (int i = 0; i <= (vertexData.length / 3) / 4; i += 8) {
            textureData[i] = 0f;
            textureData[i + 1] = 1f;
            textureData[i + 2] = 1f;
            textureData[i + 3] = 1f;
            textureData[i + 4] = 1f;
            textureData[i + 5] = 0f;
            textureData[i + 6] = 0f;
            textureData[i + 7] = 0f;
        }
        return textureData;
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();
        renderProgram = createShaderProgram();
        // Load texture image
        Texture texture = loadTexture("texture.jpg");
        initialsTextureId = texture.getTextureObject();

        gl.glGenVertexArrays(1, vao, 0);
        gl.glBindVertexArray(vao[0]);

        gl.glGenBuffers(2, vbo, 0);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer posBuf = Buffers.newDirectFloatBuffer(vertexData);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, posBuf.limit() * 4, posBuf, GL2.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, false, 0, 0);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer texBuf = Buffers.newDirectFloatBuffer(textureData);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf, GL2.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 2, GL2.GL_FLOAT, false, 0, 0);

        gl.glUseProgram(renderProgram);
        mvp_loc = gl.glGetUniformLocation(renderProgram, "u_mvpMatrix");
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[0]);
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, false, 0, 0);
        float aspect = (float) glcanvas.getWidth() / (float) glcanvas.getHeight();
        gl.glViewport(0, 0, glcanvas.getWidth(), glcanvas.getHeight());
        Matrix4 prMat = new Matrix4();
        prMat.makePerspective((float) (Math.PI / 3f), aspect, 0.1f, 100f);

        Matrix4 mvMat = new Matrix4();
        mvMat.translate(0.0f, 10f, -40.0f);
        mvMat.rotate(rtri, 0f, 1f, 0f);
        prMat.multMatrix(mvMat);
        FloatBuffer mvpMatrixBuffer = Buffers.newDirectFloatBuffer(prMat.getMatrix());
        gl.glUniformMatrix4fv(mvp_loc, 1, false, mvpMatrixBuffer);

        gl.glActiveTexture(GL2.GL_TEXTURE0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, initialsTextureId);
        // Clear The Screen And The Depth Buffer
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);

        // Draw arrays
        gl.glDrawArrays(GL2.GL_QUADS, 0, vertexData.length/3); // Starting from point 0; total points is numLines * 2

        gl.glFlush();
        rtri += 0.01f;  //assigning the angle

    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int arg1, int arg2, int width, int height) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();
    }

    private static float[] readVertexData() {
        List<Float> vertexData = new ArrayList<>();
        float[] primitiveVertexData = new float[0];
        try {
            Scanner scanner = new Scanner(getResourceFile(vertexDataPath));

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

    private static File getResourceFile(String path) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return new File(classLoader.getResource(path).getFile());
    }

    private static Texture loadTexture(String textureFileName) {
        Texture tex = null;
        try {
            File file = getResourceFile(textureFileName);
            tex = TextureIO.newTexture(file, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tex;
    }

    private int createShaderProgram() {
        GL2 gl = (GL2) GLContext.getCurrentGL();
        String vshaderSource[]
                = {"#version 430 \n",
                "layout (location=0) in vec3 position;\n",
                "layout (location=1) in vec2 texCoord;\n",
                "out vec2 tc;\n",
                "uniform mat4 u_mvpMatrix;\n",
                "void main(void)\n",
                "{\n",
                "gl_Position = u_mvpMatrix * vec4(position, 1.0);\n",
                "tc = texCoord;\n",
                "}\n"
        };
        String fshaderSource[]
                = {"#version 430 \n",
                "in vec2 tc;\n",
                "out vec4 color;\n",
                "layout (binding=0) uniform sampler2D samp;\n",
                "void main(void)\n",
                "{\n",
                "color = texture(samp, tc);\n",
//                "color = vec4(1.0, 1.0, 0.0, 1.0);\n",
                "}\n"
        };
        int vShader = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
        gl.glShaderSource(vShader, 10, vshaderSource, null, 0);
        gl.glCompileShader(vShader);
        int glErr = gl.glGetError();
        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];
        GLU glu = new GLU();
        while (glErr != GL2.GL_NO_ERROR) {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            glErr = gl.glGetError();
        }

        gl.glGetShaderiv(vShader, GL2.GL_COMPILE_STATUS, vertCompiled, 0);
        if (vertCompiled[0] == 1) {
            System.out.println(". . . vertex compilation success.");
        } else {
            System.out.println(". . . vertex compilation failed.");
        }

        int fShader = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader, 8, fshaderSource, null, 0);
        gl.glCompileShader(fShader);
        glErr = gl.glGetError();
        while (glErr != GL2.GL_NO_ERROR) {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            glErr = gl.glGetError();
        }
        gl.glGetShaderiv(fShader, GL2.GL_COMPILE_STATUS, fragCompiled, 0);
        if (fragCompiled[0] == 1) {
            System.out.println(". . . fragment compilation success.");
        } else {
            System.out.println(". . . fragment compilation failed.");
        }
        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);
        glErr = gl.glGetError();
        while (glErr != GL2.GL_NO_ERROR) {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            glErr = gl.glGetError();
        }
        int[] linked = new int[1];
        gl.glGetProgramiv(vfprogram, GL2.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 1) {
            System.out.println(". . . linking succeeded.");
        } else {
            System.out.println(". . . linking failed.");
            //printProgramLog(vfprogram);
        }
        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }
}
