package ivandev.shoresoflegenor.util;

import java.awt.Image;

import javax.swing.ImageIcon;

import ivandev.shoresoflegenor.world.GameData;

public final class ScreenUtil {

	public static final int WINDOW_OFFSET_X = 16;
	public static final int WINDOW_OFFSET_Y = 39;

	public static Image getImage(String name) {
		return getImageIcon(name).getImage();
	}

	public static ImageIcon getImageIcon(String name) {
		return new ImageIcon(GameData.IMAGE_PATH + name);
	}

	public static ImageIcon getScaledImageIcon(String name, int width, int height) {
		return new ImageIcon(getImage(name).getScaledInstance(width, height, 0));
	}

	public static ImageIcon getScaledImageIcon(ImageIcon icon, int width, int height) {
		return new ImageIcon(icon.getImage().getScaledInstance(width, height, 0));
	}
}
