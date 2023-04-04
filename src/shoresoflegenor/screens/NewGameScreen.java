package shoresoflegenor.screens;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import shoresoflegenor.entities.units.TankUnit;
import shoresoflegenor.generators.NameStringGen;
import shoresoflegenor.generators.TerrainPreset;
import shoresoflegenor.generators.TerrainPreset.MapPreset;
import shoresoflegenor.maps.WorldMap;
import shoresoflegenor.player.BotPlayer;
import shoresoflegenor.player.GamePlayer;
import shoresoflegenor.player.BotPlayer.BotDifficulty;
import shoresoflegenor.tiles.AbstractMapTile;
import shoresoflegenor.util.InputUtil;
import shoresoflegenor.util.ScreenUtil;
import shoresoflegenor.util.InputUtil.MouseButtonListener;
import shoresoflegenor.world.GameData;

@SuppressWarnings("serial")
public class NewGameScreen extends MenuUI {

	private static final int WIDTH = 1000;
	private static final int HEIGHT = 600;

	private JTextField[] proceduralFields = new JTextField[22];
	private JTextField botAmountField = new JTextField("2");
	private JComboBox<String> botLevelSelect = new JComboBox<>();
	private JSpinner mapSizeSelect = new JSpinner();
	private JButton startButton = new JButton();
	private JLabel genErrorLabel = new JLabel();
	private JLabel oppErrorLabel = new JLabel();
	private JPanel mapContainer = new JPanel();

	private WorldMap prevMap = new WorldMap(false);

	public NewGameScreen() {
		super(WIDTH, HEIGHT);
		init();
		EventQueue.invokeLater(() -> {
			setLocationRelativeTo(null);
			setVisible(true);
		});
	}

	private void init() {
		getContentPane().setBackground(Color.BLACK);

		JButton continentsButtton = new JButton();
		continentsButtton.setBounds(170, 35, 200, 40);
		continentsButtton.setIcon(ScreenUtil.getImageIcon("gui/menus/presets_continents_button.png"));
		continentsButtton.addMouseListener(new ContinentsButtonListener());
		add(continentsButtton);

		JButton inlandSeasButtton = new JButton();
		inlandSeasButtton.setBounds(170, 85, 200, 40);
		inlandSeasButtton.setIcon(ScreenUtil.getImageIcon("gui/menus/presets_inland_seas_button.png"));
		inlandSeasButtton.addMouseListener(new InlandSeasButtonListener());
		add(inlandSeasButtton);

		JButton dryDesertButton = new JButton();
		dryDesertButton.setBounds(170, 135, 200, 40);
		dryDesertButton.setIcon(ScreenUtil.getImageIcon("gui/menus/presets_dry_desert_button.png"));
		dryDesertButton.addMouseListener(new DryDesertButtonListener());
		add(dryDesertButton);

		JButton previewButton = new JButton();
		previewButton.setBounds(170, 215, 90, 40);
		previewButton.setIcon(ScreenUtil.getImageIcon("gui/menus/preview_button.png"));
		previewButton.addMouseListener(new PreviewButtonListener());
		add(previewButton);

		JButton backButton = new JButton();
		backButton.setBounds(14, 559, 60, 30);
		backButton.setIcon(ScreenUtil.getImageIcon("gui/menus/back_button.png"));
		backButton.addMouseListener(new BackButtonListener());
		add(backButton);

		startButton.setBounds(270, 215, 100, 40);
		startButton.setIcon(ScreenUtil.getImageIcon("gui/menus/start_button.png"));
		startButton.addMouseListener(new StartButtonListener());
		startButton.setEnabled(false);
		add(startButton);

		botAmountField.setBounds(502, 207, 30, 14);
		add(botAmountField);

		int fXS = 30, fYS = 14;
		Rectangle[] fieldBounds = new Rectangle[] { // BOUNDS OF ALL FIELDS
				rect(657, 39, fXS, fYS), // 0 continent roots
				rect(519, 60, fXS, fYS), // 1 land spread
				rect(652, 60, fXS, fYS), // 2 land decay
				rect(760, 60, fXS, fYS), // 3 land % gen rate
				rect(486, 81, fXS, fYS), // 4 forests
				rect(657, 81, fXS, fYS), // 5 mountains
				rect(754, 81, fXS, fYS), // 6 rivers
				rect(486, 102, fXS, fYS), // 7 forest length
				rect(603, 102, fXS, fYS), // 8 forest depth
				rect(688, 102, fXS, fYS), // 9 forest % depth weigth
				rect(773, 102, fXS, fYS), // 10 forest % flow
				rect(486, 123, fXS, fYS), // 11 mountain length
				rect(603, 123, fXS, fYS), // 12 mountain depth
				rect(688, 123, fXS, fYS), // 13 mountain % depth weight
				rect(773, 123, fXS, fYS), // 14 mountain % flow
				rect(486, 144, fXS, fYS), // 15 river length
				rect(603, 144, fXS, fYS), // 16 river depth
				rect(688, 144, fXS, fYS), // 17 river % depth weight
				rect(773, 144, fXS, fYS), // 18 river % flow
				rect(500, 165, fXS, fYS), // 19 smoothing phases
				rect(608, 165, fXS, fYS), // 20 land % smooth rate
				rect(726, 165, fXS, fYS) // 21 water % smooth rate
		};
		for (int i = 0; i < proceduralFields.length; i++) {
			proceduralFields[i] = new JTextField();
			proceduralFields[i].setBounds(fieldBounds[i]);
			proceduralFields[i].setOpaque(false);
			add(proceduralFields[i]);
		}

		mapSizeSelect.setBounds(490, 39, 40, 14);
		mapSizeSelect.setValue(100);
		add(mapSizeSelect);

		botLevelSelect.setBounds(515, 228, 80, 14);
		botLevelSelect.setFont(new Font(botLevelSelect.getFont().getName(), botLevelSelect.getFont().getStyle(), 10));
		botLevelSelect.addMouseListener(new InputUtil.MouseButtonListener());
		for (BotDifficulty d : BotDifficulty.values()) {
			botLevelSelect.addItem(d.name());
		}
		botLevelSelect.setSelectedIndex(1);
		add(botLevelSelect);

		genErrorLabel.setForeground(Color.RED.darker());
		genErrorLabel.setBounds(423, 186, 300, 14);
		add(genErrorLabel);

		oppErrorLabel.setForeground(Color.RED.darker());
		oppErrorLabel.setBounds(423, 249, 300, 14);
		add(oppErrorLabel);

		JLabel background = new JLabel();
		background.setBounds(0, 0, WIDTH, HEIGHT);
		background.setIcon(ScreenUtil.getImageIcon("gui/menus/new_game_screen_art.png"));
		add(background);

		mapContainer = new JPanel();
		mapContainer.setBounds(125, 300, 750, 300);
		mapContainer.setOpaque(false);
		mapContainer.add(prevMap);
		add(mapContainer);

		pack();
	}

	private Point getPrevCenter() {
		return new Point(mapContainer.getWidth() / 2 - prevMap.getLength() / 2,
				mapContainer.getHeight() / 2 - prevMap.getHeight() / 2);
	}

	private void showProceduralData(Object[] values) {
		for (int i = 0; i < values.length; i++) {
			proceduralFields[i].setText(values[i].toString());
		}
	}

	private void getMapFromPreset(MapPreset preset) {
		int subs = (int) mapSizeSelect.getValue();
		subs = (subs < 25) ? 25 : (subs > 400 ? 400 : subs);
		mapSizeSelect.setValue(subs);

		TerrainPreset terrainPreset = new TerrainPreset(preset, subs);
		showProceduralData(terrainPreset.getPresetValues());
		prevMap.setAbstractMap(terrainPreset.buildPreset());

		resetMapView();
	}

	private void resetMapView() {
		prevMap.setRelativeAndDraw(WorldMap.RELATIVE_SIZE_LOCKED);
		prevMap.setLocation(getPrevCenter());

		genErrorLabel.setText("");
		startButton.setEnabled(true);
	}

	private void setupWorldAndRun() {
		// lock in the new generated map
		GameData.gameWorld.worldLandscapeMap = prevMap.getAbstractMap();
		GameData.gameWorld.initEntityWorld(prevMap.getSubs());

		// setup players/bots
		try {
			// fill world player data array with bots
			int opponents = Integer.parseInt(botAmountField.getText());
			GameData.gameWorld.worldPlayers = new GamePlayer[opponents + 1]; // leave room for human player (id=0)
			for (int i = 1; i <= opponents; i++) {
				GameData.gameWorld.worldPlayers[i] = new GamePlayer(NameStringGen.rndPlayerName(),
						new BotPlayer(BotDifficulty.valueOf(botLevelSelect.getSelectedItem().toString())));
			}

			// setup the player start
			GameData.gameWorld.worldPlayers[0] = new GamePlayer("Player", null);
			for (int i = 0; i < GameData.gameWorld.worldPlayers.length; i++) {
				GameData.gameWorld.spawnUnitEntity(new TankUnit(GameData.gameWorld.worldPlayers[i],
						i + "th Armor Corps", (int) (Math.random() * 10 + 10), (int) (Math.random() * 10 + 10)));
			}
		}

		// invalid opponent selection error
		catch (NumberFormatException ex) {
			oppErrorLabel.setText("Those opponent settings are invalid.");
			return;
		}

		// attempt to run a new game window
		try {
			Thread.sleep(200);
			dispose();
			Thread.sleep(1200);
			new GameMasterScreen();
		} catch (InterruptedException ex) {
			oppErrorLabel.setText("An error occured loading the game");
		}

	}

	private static Rectangle rect(int x, int y, int width, int height) {
		return new Rectangle(x, y, width, height);
	}

	private class ContinentsButtonListener extends MouseButtonListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			getMapFromPreset(MapPreset.CONTINENTS);
		}
	}

	private class InlandSeasButtonListener extends MouseButtonListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			getMapFromPreset(MapPreset.INLAND_SEAS);
		}
	}

	private class DryDesertButtonListener extends MouseButtonListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			getMapFromPreset(MapPreset.DRY_DESERT);
		}
	}

	private class PreviewButtonListener extends MouseButtonListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);

			float[] input = new float[proceduralFields.length];
			boolean valid = true;
			for (int i = 0; i < proceduralFields.length; i++) {
				try {
					input[i] = Float.parseFloat(proceduralFields[i].getText());
				} catch (NumberFormatException ex) {
					valid = false;
					break;
				}
			}

			if (valid) {
				int subs = (int) mapSizeSelect.getValue();
				prevMap.setAbstractMap(AbstractMapTile.genQuickMap( // QUICK MAP
						subs, (int) input[0], // continents
						(int) input[1], input[3], // land drying
						(int) input[2], input[3], // land erosion
						(int) input[4], (int) input[7], (int) input[8], input[9], input[10], // forests
						(int) input[5], (int) input[11], (int) input[12], input[13], input[14], // mountains
						(int) input[6], (int) input[15], (int) input[16], input[17], input[18], // rivers
						(int) input[19], input[20], input[21] // cleaning
				));
				resetMapView();

			} else {
				genErrorLabel.setText("That gen configuration is invalid.");
				startButton.setEnabled(false);
			}
		}
	}

	private class StartButtonListener extends MouseButtonListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);

			setupWorldAndRun();
		}
	}

	private class BackButtonListener extends MouseButtonListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			dispose();
			new TitleScreen();
		}
	}
}
