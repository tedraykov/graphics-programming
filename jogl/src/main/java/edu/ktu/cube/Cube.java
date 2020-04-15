package edu.ktu.cube;

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
import java.nio.FloatBuffer;

public class Cube implements GLEventListener {
    private GLU glu = new GLU();
    private static float rot = 0;
    private static float[] cubePos = {
            -1.0f, -1.0f, -1.0f, // triangle 1 : begin
            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f, // triangle 1 : end
            1.0f, 1.0f, -1.0f, // triangle 2 : begin
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f, // triangle 2 : end
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f
    };
    private float[] cubeTexCoord = {
            0f, 0f, 1f, 0f, 1f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f,
            0f, 0f, 1f, 0f, 1f, 1f
    };

    private int textureId;
    private int vao[] = new int[1];
    private int vbo[] = new int[2];
    private int renderProgram;
    private static GLCanvas glcanvas;
    private static int mvp_loc;
private static Matrix4 prMat;
    private static int err;

    public static void main(String[] args) {
        //getting the capabilities object of GL4 profile
        final GLProfile profile = GLProfile.get(GLProfile.GL4);
        GLCapabilities capabilities = new GLCapabilities(profile);

        // The canvas
        glcanvas = new GLCanvas(capabilities);
        Cube cube = new Cube();
        glcanvas.addGLEventListener(cube);
        glcanvas.setSize(800, 600);

        //creating frame
        final JFrame frame = new JFrame(" Colored Triangle");

        //adding canvas to it
        frame.getContentPane().add(glcanvas);
        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);

        final FPSAnimator animator = new FPSAnimator(glcanvas, 300, true);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        final GL4 gl = glAutoDrawable.getGL().getGL4();

        renderProgram = createShaderProgram();
        Texture texture = loadTexture("texture.jpg");
        textureId = texture.getTextureObject();

        err = gl.glGetError();

        gl.glGenVertexArrays(1, vao, 0);
        gl.glBindVertexArray(vao[0]);

        err = gl.glGetError();

        gl.glGenBuffers(2, vbo, 0);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer posBuf = Buffers.newDirectFloatBuffer(cubePos);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, posBuf.limit() * 4, posBuf, GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer texBuf = Buffers.newDirectFloatBuffer(cubeTexCoord);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf, GL4.GL_STATIC_DRAW);

        err = gl.glGetError();
        gl.glUseProgram(renderProgram);
        err = gl.glGetError();
        mvp_loc = gl.glGetUniformLocation(renderProgram, "u_mvpMatrix");
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL4 gl = glAutoDrawable.getGL().getGL4();

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, textureId);

        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

        prMat = new Matrix4();
        float aspect = (float) glcanvas.getWidth() / (float) glcanvas.getHeight();
        prMat.makePerspective(55.0f, aspect, 0.1f, 100.0f);

        Matrix4 mvMat = new Matrix4();
        mvMat.translate(0.0f, 0f, -4.0f);
        mvMat.rotate(rot, 0f, 1f, 0f);
        prMat.multMatrix(mvMat);
        FloatBuffer mvpMatrixBuffer = Buffers.newDirectFloatBuffer(prMat.getMatrix());
        gl.glUniformMatrix4fv(mvp_loc, 1, false, mvpMatrixBuffer);

        gl.glEnable(GL4.GL_DEPTH_TEST);
        gl.glDepthFunc(GL4.GL_LEQUAL);
        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, cubePos.length / 3);

        gl.glFlush();
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        gl.glDisableVertexAttribArray(0);
        rot += 0.01;
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int width, int height) {
    }

    private int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        String[] vshaderSource
                = {"#version 430 \n",
                "layout (location=0) in vec3 position;\n",
                "layout (location=1) in vec2 texCoord;\n",
                "out vec2 tc;\n",
                "uniform mat4 u_mvpMatrix;\n",
                "void main(void)\n",
                "{\n",
                "gl_Position = u_mvpMatrix  * vec4(position, 1.0);\n",
                "tc = texCoord;\n",
                "}\n"
        };
        String[] fshaderSource = {
                "#version 430 \n",
                "in vec2 tc;\n",
                "out vec4 color;\n",
                "layout (binding=0) uniform sampler2D samp;\n",
                "void main(void)\n",
                "{\n",
                "color = texture(samp, tc);\n",
//                "color = vec4(1.0, 1.0, 0.0, 1.0);\n",
                "}\n"
        };
        int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        gl.glShaderSource(vShader, 10, vshaderSource, null, 0);
        gl.glCompileShader(vShader);
        int glErr = gl.glGetError();
        int[] vertCompiled = new int[1];
        int[] fragCompiled = new int[1];
        GLU glu = new GLU();
        while (glErr != GL4.GL_NO_ERROR) {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            glErr = gl.glGetError();
        }

        gl.glGetShaderiv(vShader, GL4.GL_COMPILE_STATUS, vertCompiled, 0);
        if (vertCompiled[0] == 1) {
            System.out.println(". . . vertex compilation success.");
        } else {
            System.out.println(". . . vertex compilation failed.");
        }

        int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader, 8, fshaderSource, null, 0);
        gl.glCompileShader(fShader);
        glErr = gl.glGetError();
        while (glErr != GL4.GL_NO_ERROR) {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            glErr = gl.glGetError();
        }
        gl.glGetShaderiv(fShader, GL4.GL_COMPILE_STATUS, fragCompiled, 0);
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
        while (glErr != GL4.GL_NO_ERROR) {
            System.err.println("glError: " + glu.gluErrorString(glErr));
            glErr = gl.glGetError();
        }
        int[] linked = new int[1];
        gl.glGetProgramiv(vfprogram, GL4.GL_LINK_STATUS, linked, 0);

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

    private static File getResourceFile(String path) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return new File(classLoader.getResource(path).getFile());
    }
}
