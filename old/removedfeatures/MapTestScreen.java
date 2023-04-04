package removedfeatures;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import ivandev.shoresoflegenor.maps.WorldMap;
import ivandev.shoresoflegenor.tiles.AbstractMapTile;
import ivandev.shoresoflegenor.util.ScreenUtil;

public class MapTestScreen {

	private final int xSize = 900;
	private final int ySize = 900;

	private JFrame game = new JFrame();
	private WorldMap t;

	public MapTestScreen() {
		this.init();
		EventQueue.invokeLater(() -> {
			game.setLocationRelativeTo(null);
			game.setVisible(true);
		});
	}

	public void init() {
		game.setLayout(null);
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setPreferredSize(new Dimension(xSize + ScreenUtil.WINDOW_OFFSET_X, ySize + ScreenUtil.WINDOW_OFFSET_Y));
		game.setTitle("RandomMapGen");
		game.setIconImage(ScreenUtil.getImage("gui/misc/logo.png"));
		game.getContentPane().setBackground(Color.BLACK);
		game.setResizable(true);

		// t = new TerrainMap();
		t.addMouseListener(new MouseClicker());
		t.setBackground(Color.BLACK);
		game.add(t);

		game.pack();
	}

	public static void main(String[] args) {
		new MapTestScreen();
	}

	private class MouseClicker extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.isAltDown()) {
				t.setAbstractMap(AbstractMapTile.genQuickMap( // NEW SAMPLE MAP
						t.getAbstractMap().length, 45, // continents
						85, 0.1f, // land drying
						20, 0.1f, // land erosion
						15, 3, 100, 0.05f, 0.4f, // forests
						5, 1, 100, 0.4f, 0.8f, // mountains
						10, 1, 100, 0.3f, 0.1f, // rivers
						3, 0.9f, 0.6f // cleaning
				));
				t.redraw();
			}
		}
	}

}
