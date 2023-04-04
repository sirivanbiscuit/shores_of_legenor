package ivandev.shoresoflegenor.uifeatures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ivandev.shoresoflegenor.entities.units.UnitEntity;
import ivandev.shoresoflegenor.tiles.GraphicsTile;
import ivandev.shoresoflegenor.tiles.MapTile;
import ivandev.shoresoflegenor.tiles.PolyTile;
import ivandev.shoresoflegenor.util.FontUtil;
import ivandev.shoresoflegenor.util.ScreenUtil;

@SuppressWarnings("serial")
public class UnitStatFrame extends JPanel {

	private static final int WIDTH = 200;
	private static final int HEIGHT = 300;

	private static final Font INFO_FONT = FontUtil.getFont("dhurjati.ttf", 20f, Font.PLAIN);

	private UnitEntity unit;
	private MapTile originTile;

	private MapTile displayTile;
	private Image entityImage, background;

	public UnitStatFrame(UnitEntity unit, MapTile originTile) {
		this.unit = unit;
		this.originTile = originTile;

		initPanel();
		initUnitInfo();
	}

	private void initPanel() {
		Rectangle rect = originTile.base.getBounds();
		setBounds(rect.x + rect.width / 2 - WIDTH / 2, rect.y - HEIGHT, WIDTH, HEIGHT);
		setOpaque(false);
	}

	private void initUnitInfo() {
		JLabel unitTitle = new JLabel(unit.name);
		unitTitle.setBounds(15, 110, 170, 25);
		unitTitle.setForeground(Color.WHITE);
		unitTitle.setFont(INFO_FONT.deriveFont(Font.BOLD));
		add(unitTitle);

		add(new StatLabel("HTP ..... " + unit.data.htpRem + "/" + unit.data.htpMax, 15, 150, 170, 20));
		add(new StatLabel("ORG ..... " + unit.data.org, 15, 170, 120, 20));
		add(new StatLabel("DMG ..... " + unit.data.dmg, 15, 190, 120, 20));
		add(new StatLabel("RNG ..... " + unit.data.rng, 15, 210, 120, 20));
		add(new StatLabel("SPD ..... " + unit.data.spd, 15, 230, 120, 20));

		displayTile = new MapTile(new PolyTile(MapTile.newPt(15, 55), MapTile.newPt(100, 10), MapTile.newPt(185, 55),
				MapTile.newPt(100, 95)), null, originTile.terrainType);

		entityImage = ScreenUtil.getImage(unit.texPath);
		background = ScreenUtil.getImage("gui/game/entity_stat_frame.png");
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(background, 0, 0, WIDTH, HEIGHT, null);

		new GraphicsTile(displayTile, true).drawGraphicsTile(g);
		g.drawImage(entityImage, 15, 10, null);
	}

	private class StatLabel extends JLabel {

		private StatLabel(String statText, int x, int y, int width, int height) {
			super(statText);

			setBounds(x, y, width, height);
			setForeground(Color.WHITE);
			setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
		}
	}
}
