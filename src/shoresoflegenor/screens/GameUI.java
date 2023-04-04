package shoresoflegenor.screens;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JFrame;

import shoresoflegenor.util.ScreenUtil;

@SuppressWarnings("serial")
public abstract class GameUI extends JFrame {
	
	protected GroupLayout layout = new GroupLayout(getContentPane());

	public GameUI(int width, int height, String title) {
		setLayout(layout);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(width + ScreenUtil.WINDOW_OFFSET_X, height + ScreenUtil.WINDOW_OFFSET_Y));
		setTitle(title);
		setIconImage(ScreenUtil.getImage("gui/misc/logo.png"));
	}
}
