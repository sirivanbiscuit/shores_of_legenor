package ivandev.shoresoflegenor.screens;

import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;

import ivandev.shoresoflegenor.util.InputUtil.MouseButtonListener;
import ivandev.shoresoflegenor.util.ScreenUtil;
import ivandev.shoresoflegenor.world.GameData;

@SuppressWarnings("serial")
public class TitleScreen extends MenuUI {

	private static final int WIDTH = 400;
	private static final int HEIGHT = 500;

	public TitleScreen() {
		super(WIDTH, HEIGHT);
		init();
		EventQueue.invokeLater(() -> {
			setLocationRelativeTo(null);
			setVisible(true);
		});
	}

	private void init() {
		JButton newGameButton = new JButton();
		newGameButton.setBounds(25, 260, 350, 85);
		newGameButton.setIcon(ScreenUtil.getImageIcon("gui/menus/new_game_button.png"));
		newGameButton.addMouseListener(new NewGameMouseListener());
		add(newGameButton);

		JButton loadButton = new JButton();
		loadButton.setBounds(25, 355, 170, 85);
		loadButton.setIcon(ScreenUtil.getImageIcon("gui/menus/load_button.png"));
		loadButton.addMouseListener(new LoadButtonListener());
		add(loadButton);

		JButton quitButton = new JButton();
		quitButton.setBounds(205, 355, 170, 85);
		quitButton.setIcon(ScreenUtil.getImageIcon("gui/menus/quit_button.png"));
		quitButton.addMouseListener(new QuitButtonListener());
		add(quitButton);

		JLabel background = new JLabel();
		background.setBounds(0, 0, WIDTH, HEIGHT);
		background.setIcon(ScreenUtil.getImageIcon("gui/menus/title_screen_art.png"));
		add(background);

		pack();
	}

	private class NewGameMouseListener extends MouseButtonListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			dispose();
			new NewGameScreen();
		}
	}

	private class LoadButtonListener extends MouseButtonListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			try {
				GameData.loadWorld();
				Thread.sleep(200);
				dispose();
				Thread.sleep(1200);
				new GameMasterScreen();
			} catch (IOException | ClassNotFoundException | InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class QuitButtonListener extends MouseButtonListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			System.exit(0);
		}
	}
}
