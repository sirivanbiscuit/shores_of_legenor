package ivandev.shoresoflegenor.graphics;

import javax.swing.SwingWorker;

public class GraphicsWorker extends SwingWorker<Void, Void> {

	private Runnable runnable;

	private GraphicsWorker(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	protected Void doInBackground() {
		runnable.run();
		return null;
	}
	
	/**
	 * 
	 * @param run
	 * @param type
	 */
	public static void performBackgroundRun(Runnable run) {
		GraphicsWorker renderer = new GraphicsWorker(run);
		renderer.execute();
		System.gc();
	}
}
