package shoresoflegenor.uifeatures;

import java.awt.Graphics;

import javax.swing.JPanel;

import shoresoflegenor.util.ScreenUtil;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {

	private String imagePath;

	public ImagePanel(String imagePath) {
		this.imagePath = imagePath;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(ScreenUtil.getImage(imagePath), 0, 0, null);
	}
}
