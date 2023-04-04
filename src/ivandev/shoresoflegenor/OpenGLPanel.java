package ivandev.shoresoflegenor;

import javax.swing.JPanel;

import com.jogamp.nativewindow.NativeSurface;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.swt.GLCanvas;

public class OpenGLPanel extends GLJPanel implements GLEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GLContext context;

	public OpenGLPanel() {

		// Create a new GLProfile object to specify the version of OpenGL to use
		GLProfile profile = GLProfile.get(GLProfile.GL2);

		// Create a new GLCapabilities object to specify the capabilities of the OpenGL
		// context
		GLCapabilities capabilities = new GLCapabilities(profile);

		// Create a new GLDrawableFactory to create the OpenGL context
		GLDrawableFactory factory = GLDrawableFactory.getFactory(profile);

		// Create a new GLDrawable to hold the OpenGL context
		GLDrawable drawable = factory.createDummyDrawable(null, true, capabilities, null);

		// Create a new GLContext to hold the OpenGL state
		context = drawable.createContext(null);

		// Add the GLEventListener to the GLCanvas component
		addGLEventListener(this);

		setBounds(0, 0, 640, 480);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// Initialize the OpenGL state
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// Render the graphics
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glBegin(GL2.GL_TRIANGLES);
		gl.glVertex2f(-0.5f, -0.5f);
		gl.glVertex2f(0.5f, -0.5f);
		gl.glVertex2f(0.0f, 0.5f);
		gl.glEnd();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// Update the viewport
		GL2 gl = drawable.getGL().getGL2();
		gl.glViewport(0, 0, width, height);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// Clean up the OpenGL state
		GL2 gl = drawable.getGL().getGL2();
		gl.glDeleteBuffers(1, null, 0);
	}

	public void start() {
		// Make the OpenGL context current on the GLCanvas component
		context.makeCurrent();
	}
}
