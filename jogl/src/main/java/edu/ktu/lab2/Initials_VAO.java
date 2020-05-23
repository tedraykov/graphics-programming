import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.*;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

//import com.jogamp.common.nio.Buffers;
import javax.swing.JFrame;

public class Initials_VAO implements GLEventListener {
    private GLU glu = new GLU();

    private float rot;
    private static int textureId;
    private int programId;
    private int vao[] = new int[1];
    private int vbo[] = new int[2];

    static float[] vertices = {
            0.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            -1.0f, 0.8f, 0.0f,
            0.0f, 0.8f, 0.0f,
            0.0f, 1.0f, 0.5f,
            0.0f, 0.8f, 0.5f,
            -1.0f, 0.8f, 0.5f,
            -1.0f, 1.0f, 0.5f,
            -1.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.5f,
            -1.0f, 1.0f, 0.5f,
            -1.0f, 0.8f, 0.0f,
            0.0f, 0.8f, 0.0f,
            0.0f, 0.8f, 0.5f,
            -1.0f, 0.8f, 0.5f,
            -1.0f, 1.0f, 0.5f,
            -1.0f, 1.0f, 0.0f,
            -1.0f, 0.8f, 0.0f,
            -1.0f, 0.8f, 0.5f,
            0.0f, 1.0f, 0.5f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.8f, 0.0f,
            0.0f, 0.8f, 0.5f,
            0.0f, 0.2f, 0.0f,
            0.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.2f, 0.0f,
            0.0f, 0.2f, 0.5f,
            0.0f, 0.0f, 0.5f,
            -1.0f, 0.0f, 0.5f,
            -1.0f, 0.2f, 0.5f,
            -1.0f, 0.2f, 0.0f,
            0.0f, 0.2f, 0.0f,
            0.0f, 0.2f, 0.5f,
            -1.0f, 0.2f, 0.5f,
            -1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f,
            -1.0f, 0.0f, 0.5f,
            -1.0f, 0.2f, 0.5f,
            -1.0f, 0.2f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.5f,
            0.0f, 0.2f, 0.5f,
            0.0f, 0.2f, 0.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, -0.6f, 0.0f,
            -1.0f, -0.6f, 0.0f,
            -1.0f, -0.8f, 0.0f,
            0.0f, -0.8f, 0.0f,
            0.0f, -0.6f, 0.5f,
            0.0f, -0.8f, 0.5f,
            -1.0f, -0.8f, 0.5f,
            -1.0f, -0.6f, 0.5f,
            -1.0f, -0.6f, 0.0f,
            0.0f, -0.6f, 0.0f,
            0.0f, -0.6f, 0.5f,
            -1.0f, -0.6f, 0.5f,
            -1.0f, -0.8f, 0.0f,
            0.0f, -0.8f, 0.0f,
            0.0f, -0.8f, 0.5f,
            -1.0f, -0.8f, 0.5f,
            -1.0f, -0.6f, 0.5f,
            -1.0f, -0.6f, 0.0f,
            -1.0f, -0.8f, 0.0f,
            -1.0f, -0.8f, 0.5f,
            0.0f, -0.6f, 0.5f,
            0.0f, -0.6f, 0.0f,
            0.0f, -0.8f, 0.0f,
            0.0f, -0.8f, 0.5f,
            -0.8f, 0.8f, 0.0f,
            -1.0f, 0.8f, 0.0f,
            -1.0f, 0.2f, 0.0f,
            -0.8f, 0.2f, 0.0f,
            -0.8f, 0.8f, 0.5f,
            -0.8f, 0.2f, 0.5f,
            -1.0f, 0.2f, 0.5f,
            -1.0f, 0.8f, 0.5f,
            -1.0f, 0.8f, 0.0f,
            -0.8f, 0.8f, 0.0f,
            -0.8f, 0.8f, 0.5f,
            -1.0f, 0.8f, 0.5f,
            -1.0f, 0.2f, 0.0f,
            -0.8f, 0.2f, 0.0f,
            -0.8f, 0.2f, 0.5f,
            -1.0f, 0.2f, 0.5f,
            -1.0f, 0.8f, 0.5f,
            -1.0f, 0.8f, 0.0f,
            -1.0f, 0.2f, 0.0f,
            -1.0f, 0.2f, 0.5f,
            -0.8f, 0.2f, 0.5f,
            -0.8f, 0.2f, 0.0f,
            -0.8f, 0.8f, 0.0f,
            -0.8f, 0.8f, 0.5f,
            0.0f, 0.0f,0.0f,
            -0.2f, 0.0f,0.0f,
            -0.2f,-0.6f,0.0f,
            0.0f,-0.6f,0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f,-0.6f, 0.5f,
            -0.2f,-0.6f, 0.5f,
            -0.2f, 0.0f, 0.5f,
            -0.2f,0.0f,0.0f,
            0.0f,0.0f,0.0f,
            0.0f,0.0f,0.5f,
            -0.2f,0.0f,0.5f,
            -0.2f,-0.6f,0.0f,
            0.0f,-0.6f,0.0f,
            0.0f,-0.6f,0.5f,
            -0.2f,-0.6f,0.5f,
            0.0f,-0.6f,0.5f,
            0.0f,-0.6f,0.0f,
            0.0f, 0.0f,0.0f,
            0.0f, 0.0f,0.5f,
            -0.2f, 0.0f,0.5f,
            -0.2f, 0.0f,0.0f,
            -0.2f,-0.6f, 0.0f,
            -0.2f,-0.6f, 0.5f,
            1.5f, 1.0f, 0.0f,
            0.5f, 1.0f, 0.0f,
            0.5f, 0.8f, 0.0f,
            1.5f, 0.8f, 0.0f,
            1.5f, 1.0f, 0.5f,
            0.5f, 1.0f, 0.5f,
            0.5f, 0.8f, 0.5f,
            1.5f, 0.8f, 0.5f,
            0.5f, 1.0f, 0.0f,
            1.5f, 1.0f, 0.0f,
            1.5f, 1.0f, 0.5f,
            0.5f, 1.0f, 0.5f,
            0.5f, 0.8f, 0.0f,
            1.5f, 0.8f, 0.0f,
            1.5f, 0.8f, 0.5f,
            0.5f, 0.8f, 0.5f,
            0.5f, 1.0f, 0.5f,
            0.5f, 1.0f, 0.0f,
            0.5f, 0.8f, 0.0f,
            0.5f, 0.8f, 0.5f,
            1.5f, 1.0f, 0.5f,
            1.5f, 1.0f, 0.0f,
            1.5f, 0.8f, 0.0f,
            1.5f, 0.8f, 0.5f,
            1.5f, 0.2f, 0.0f,
            0.5f, 0.2f, 0.0f,
            0.5f, 0.0f, 0.0f,
            1.5f, 0.0f, 0.0f,
            0.5f, 0.2f, 0.5f,
            0.5f, 0.0f, 0.5f,
            1.5f, 0.0f, 0.5f,
            1.5f, 0.2f, 0.5f,
            0.5f, 0.2f, 0.0f,
            1.5f, 0.2f, 0.0f,
            1.5f, 0.2f, 0.5f,
            0.5f, 0.2f, 0.5f,
            0.5f, 0.0f, 0.0f,
            1.5f, 0.0f, 0.0f,
            1.5f, 0.0f, 0.5f,
            0.5f, 0.0f, 0.5f,
            0.5f, 0.2f, 0.5f,
            0.5f, 0.2f, 0.0f,
            0.5f, 0.0f, 0.0f,
            0.5f, 0.0f, 0.5f,
            1.5f, 0.2f, 0.5f,
            1.5f, 0.2f, 0.0f,
            1.5f, 0.0f, 0.0f,
            1.5f, 0.0f, 0.5f,
            1.5f, -0.6f, 0.0f,
            0.5f, -0.6f, 0.0f,
            0.5f, -0.8f, 0.0f,
            1.5f, -0.8f, 0.0f,
            0.5f, -0.6f, 0.5f,
            0.5f, -0.8f, 0.5f,
            1.5f, -0.8f, 0.5f,
            1.5f, -0.6f, 0.5f,
            0.5f, -0.6f, 0.0f,
            1.5f, -0.6f, 0.0f,
            1.5f, -0.6f, 0.5f,
            0.5f, -0.6f, 0.5f,
            0.5f, -0.8f, 0.0f,
            1.5f, -0.8f, 0.0f,
            1.5f, -0.8f, 0.5f,
            0.5f, -0.8f, 0.5f,
            0.5f, -0.6f, 0.5f,
            0.5f, -0.6f, 0.0f,
            0.5f, -0.8f, 0.0f,
            0.5f, -0.8f, 0.5f,
            1.5f, -0.6f, 0.5f,
            1.5f, -0.6f, 0.0f,
            1.5f, -0.8f, 0.0f,
            1.5f, -0.8f, 0.5f,
            0.3f, 1.0f, 0.0f,
            0.3f, -0.8f, 0.0f,
            0.5f, -0.8f, 0.0f,
            0.5f, 1.0f, 0.0f,
            0.3f, 1.0f, 0.5f,
            0.3f, -0.8f, 0.5f,
            0.5f, -0.8f, 0.5f,
            0.5f, 1.0f, 0.5f,
            0.3f, 1.0f, 0.0f,
            0.5f, 1.0f, 0.0f,
            0.5f, 1.0f, 0.5f,
            0.3f, 1.0f, 0.5f,
            0.3f, -0.8f, 0.0f,
            0.5f, -0.8f, 0.0f,
            0.5f, -0.8f, 0.5f,
            0.3f, -0.8f, 0.5f,
            0.3f, 1.0f, 0.5f,
            0.3f, 1.0f, 0.0f,
            0.3f, -0.8f, 0.0f,
            0.3f, -0.8f, 0.5f,
            0.5f, -0.8f, 0.5f,
            0.5f, -0.8f, 0.0f,
            0.5f, 1.0f, 0.0f,
            0.5f, 1.0f, 0.5f
    };

    static float[] texture = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f
    };

    public static void main(String[] args) {
        final GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);

        final GLCanvas glcanvas = new GLCanvas(capabilities);
        Initials_VAO l = new Initials_VAO();
        glcanvas.addGLEventListener(l);
        glcanvas.setSize(800, 600);

        final JFrame frame = new JFrame("Simona Eneva Initials with VAO");
        frame.getContentPane().add(glcanvas);
        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final FPSAnimator animator = new FPSAnimator(glcanvas, 300, true);
        animator.start();
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();

        programId = createShaderProgram();

        textureId = loadTexture("texture.jpg").getTextureObject(gl);

        gl.glGenVertexArrays(1, IntBuffer.wrap(vao));
        gl.glBindVertexArray(vao[0]);

        gl.glGenBuffers(2, IntBuffer.wrap(vbo));

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer posBuf = Buffers.newDirectFloatBuffer(vertices);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, posBuf.limit() * 4, posBuf, GL2.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, false, 0, 0);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer texBuf = Buffers.newDirectFloatBuffer(texture);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf, GL2.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 2, GL2.GL_FLOAT, false, 0, 0);


        gl.glUseProgram(programId);
        gl.glLoadIdentity();
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        final GL2 gl = glAutoDrawable.getGL().getGL2();
        int mvp = gl.glGetUniformLocation(programId, "u_mvpMatrix");

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[0]);
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL2.GL_FLOAT, false, 0, 0);
        float aspect = (float) 800 / (float) 600;
        gl.glViewport(0, 0, 800, 600);
        Matrix4 prMat = new Matrix4();
        prMat.makePerspective((float) (Math.PI / 3f), aspect, 0.1f, 100f);

        Matrix4 mvMat = new Matrix4();
        mvMat.translate(0.25f, 0.2f, -2.0f);
        mvMat.rotate(rot, 0f, 1f, 0f);
        prMat.multMatrix(mvMat);
        FloatBuffer mvpMatrixBuffer = Buffers.newDirectFloatBuffer(prMat.getMatrix());
        gl.glUniformMatrix4fv(mvp, 1, false, mvpMatrixBuffer);

        gl.glActiveTexture(GL2.GL_TEXTURE0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId);
        // Clear The Screen And The Depth Buffer
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);

        // Draw arrays
        gl.glDrawArrays(GL2.GL_QUADS, 0, vertices.length/3); // Starting from point 0; total points is numLines * 2

        gl.glFlush();
        rot += 0.01f;  //assigning the angle
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {

    }

    private Texture loadTexture(String textureFileName) {
        Texture texture = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File (classLoader.getResource(textureFileName).getFile());
            texture = TextureIO.newTexture(file, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return texture;
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
        }

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }

}
