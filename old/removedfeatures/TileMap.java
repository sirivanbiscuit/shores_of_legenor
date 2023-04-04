package removedfeatures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TileMap extends JPanel {

	public static Color OCEAN_COLOR = new Color(0, 0, 150);
	public static Color LAND_COLOR = new Color(200, 255, 0);
	public static Color BORDER_COLOR = new Color(100, 100, 0);

	private int subs;
	private int size;
	private int minX = 0, minY = 0;

	private int initX, initY;

	private GeneralPath path;

	private boolean[][] grid;

	// SET THESE VALUES TO CHANGE ISLAND GEN/SHAPE
	// all float values should be in range [0,1)
	private final float landSize = 0.95f; // the relative radius of the landmass to the screen
	private final float landRoughness = 0.05f; // the deviation of the landmass radius
	private final float landRoundness = 0.25f; // how close the land is to a regular polygon
	private final float lakeSpawnChance = 0.005f; // portion of inland tiles that will be water
	private final float terrainFlatness = 0.99f; // portion of single-tile islands/lakes to be removed
	private final float errosionPower = 0.99f; // portion of adj. tiles to which lakes expand into
	private final int errosionTime = 2; // higher values make lakes spread more and get larger
	private final int shoreDensity = 10; // higher values makes the landmass have more sides

	public TileMap(int size, int subs) {
		this.setBounds(0, 0, size, size);
		this.size = size;
		this.subs = subs;

		this.init();
	}

	private void init() {
		this.setBackground(OCEAN_COLOR);
		this.setFocusable(true);
		this.requestFocus();
		this.genLandMass();

		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getPreciseWheelRotation() > 0) {
					size -= 20;
				} else {
					size += 20;
				}
				setBounds(getX(), getY(), size, size);
				repaint();
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				initX = e.getX();
				initY = e.getY();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1) {
					genLandMass();
					repaint();
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point point = getLocation();
				setLocation(point.x + e.getX() - initX, point.y + e.getY() - initY);
				repaint();
			}
		});
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int kC = e.getKeyCode();
				int scrollSpeed = 8;
				Point point = getLocation();
				if (kC == KeyEvent.VK_UP) {
					setLocation(point.x, point.y + scrollSpeed);
				} else if (kC == KeyEvent.VK_DOWN) {
					setLocation(point.x, point.y - scrollSpeed);
				} else if (kC == KeyEvent.VK_LEFT) {
					setLocation(point.x + scrollSpeed, point.y);
				} else if (kC == KeyEvent.VK_RIGHT) {
					setLocation(point.x - scrollSpeed, point.y);
				}
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		this.removeAll();

		int tileStep = size / subs;
		for (int x = 0; x < grid.length; x++) {
			for (float y = 0; y < grid[x].length; y += 0.5) {

				// origin points for primary tiles
				int xOrg = (int) (minX + x * tileStep);
				int yOrg = (int) (minY + y * tileStep);
				int[] xPts1 = { xOrg + tileStep / 2, xOrg + tileStep, xOrg + tileStep / 2, xOrg };
				int[] yPts1 = { yOrg, yOrg + tileStep / 4, yOrg + tileStep / 2, yOrg + tileStep / 4 };

				// paint primary tiles
				if (grid[x][(int) Math.floor(y)]) {
					g.setColor(LAND_COLOR);
					g.fillPolygon(xPts1, yPts1, 4);
				}
				g.setColor(BORDER_COLOR);
				g.drawPolygon(xPts1, yPts1, 4);

				// origin points for filler tiles
				int[] xPts2 = { xOrg + tileStep, xOrg + 3 * tileStep / 2, xOrg + tileStep, xOrg + tileStep / 2 };
				int[] yPts2 = { yOrg + tileStep / 4, yOrg + tileStep / 2, yOrg + 3 * tileStep / 4,
						yOrg + tileStep / 2 };

				// paint filler tiles
				if (grid[x][(int) Math.floor(y)]) {
					g.setColor(LAND_COLOR);
					g.fillPolygon(xPts2, yPts2, 4);
					g.setColor(BORDER_COLOR);
					g.drawPolygon(xPts2, yPts2, 4);
				}

				JLabel j = new JLabel(x + ", " + y);
				j.setBounds(xOrg, yOrg, tileStep, tileStep / 2);
				j.setHorizontalAlignment(JLabel.CENTER);
				j.setForeground(LAND_COLOR);
				this.add(j);

				j = new JLabel((x + 0.5) + ", " + y);
				j.setBounds(xOrg, yOrg, 2 * tileStep, tileStep);
				j.setHorizontalAlignment(JLabel.CENTER);
				j.setForeground(LAND_COLOR);
				this.add(j);

			}
		}
	}

	private void genLandMass() {
		// create general landmass polygon outline
		path = pathGen(shoreDensity, minX, minX + size, minY, minY + size);

		/*
		 * Creates a boolean matrix for land/ocean data. The screen is divided into a
		 * grid with area (subs^2). All grid squares that fall entirely in the
		 * GeneralPath will be set as land (true). Random patches of ocean (false) will
		 * be sprinkled over the island. The percentage of the island randomly turned
		 * this way is equal to (lakeSpawnChance*100).
		 */
		grid = new boolean[subs][subs];
		int tileStep = size / subs;
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[x].length; y++) {
				Rectangle2D rect = (Rectangle2D) new Rectangle(minX + x * tileStep, minY + y * tileStep, tileStep,
						tileStep);
				boolean canLakeSpawn = Math.random() < lakeSpawnChance;
				grid[x][y] = path.contains(rect) && !canLakeSpawn;
			}
		}

		/*
		 * "Errodes" land that is next to ocean tiles. During each errosion "phase" all
		 * land tiles that are in contact with ocean tiles have a (errosionPower*100)
		 * percent chance of turning into ocean tiles themselves. The number of phases
		 * executed is equal to (errosionTime). Lakes/coastlines will be smoother the
		 * higher this number, but setting it too high may turn the island into an
		 * archipelago, or may sink it entirely.
		 */
		for (int phase = 0; phase < errosionTime; phase++) {
			for (int x = 0; x < grid.length; x++) {
				for (int y = 0; y < grid[x].length; y++) {
					int tempX = x;
					int tempY = y;
					x = Math.random() < Math.random() ? grid.length - x - 1 : x;
					y = Math.random() < Math.random() ? grid[x].length - y - 1 : y;
					if (grid[x][y]) {
						boolean isLandSurrounded = grid[x - 1][y] && grid[x + 1][y] && grid[x][y - 1] && grid[x][y + 1];
						boolean errosionSuccessful = Math.random() < errosionPower;
						grid[x][y] = isLandSurrounded || !errosionSuccessful;
					}
					x = tempX;
					y = tempY;
				}
			}
		}

		/*
		 * Clean stage in which undesirable islands and lakes are removed. The map as a
		 * whole will be more visually appealing if there is a limited number of
		 * single-square lakes or islands. (terrainFlatness*100) percent of all
		 * single-square features will be purged from the map.
		 */
		for (int x = 1; x < grid.length - 1; x++) {
			for (int y = 1; y < grid[x].length - 1; y++) {
				boolean isNearOcean = !(grid[x - 1][y] && grid[x + 1][y] && grid[x][y - 1] && grid[x][y + 1]);
				boolean isNearLand = grid[x - 1][y] || grid[x + 1][y] || grid[x][y - 1] || grid[x][y + 1];
				boolean immuneToFlattening = Math.random() > terrainFlatness;
				grid[x][y] = grid[x][y] ? isNearLand || immuneToFlattening : !(isNearOcean || immuneToFlattening);
			}
		}

	}

	private GeneralPath pathGen(int vertices, int minX, int maxX, int minY, int maxY) {
		/*
		 * Sets an origin point in the centerpoint of the given coordinates, then
		 * selects a random point along the line between the origin and a random point
		 * along the edge of the screen. These points will be connected in a circular
		 * fashion to form a (vertices) sided shape.
		 */
		GeneralPath path = new GeneralPath();
		Point origin = new Point((maxX + minX) / 2, (maxY + minY) / 2);
		Point v1 = new Point();
		float magLow = landSize - landRoughness;
		float magHigh = landSize + landRoughness;
		float roundLow = landRoundness / 2;
		float roundHigh = roundLow - landRoundness + 1;
		for (int ang = 0; ang < 360; ang += 360.0 / vertices * (Math.random() * (roundHigh - roundLow) + roundLow)) {
			int mag = (int) ((Math.random() * (magHigh - magLow) + magLow) * (maxX - origin.x));
			Point p = new Point((int) (origin.x - Math.sin(Math.toRadians(ang)) * mag),
					(int) (origin.y + Math.cos(Math.toRadians(ang)) * mag));
			if (ang > 0) {
				path.lineTo(p.x, p.y);
			} else {
				path.moveTo(p.x, p.y);
				v1 = p;
			}

		}

		// close path and return
		path.lineTo(v1.x, v1.y);
		return path;
	}

}
