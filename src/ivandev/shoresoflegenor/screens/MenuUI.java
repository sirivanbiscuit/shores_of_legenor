package ivandev.shoresoflegenor.screens;

import java.awt.Dimension;

@SuppressWarnings("serial")
public abstract class MenuUI extends GameUI {

	public MenuUI(int width, int height) {
		super(width, height, "Shores of Legenor");
		setUndecorated(true);
		setPreferredSize(new Dimension(width, height));
	}
}