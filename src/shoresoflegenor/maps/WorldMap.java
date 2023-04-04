package shoresoflegenor.maps;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import shoresoflegenor.graphics.EntityGraphics;
import shoresoflegenor.graphics.GraphicsWorker;
import shoresoflegenor.graphics.WorldGraphics;
import shoresoflegenor.graphics.WorldGraphics.CacheType;
import shoresoflegenor.tiles.AbstractMapTile;
import shoresoflegenor.tiles.ChunkTile;
import shoresoflegenor.tiles.MapTile;
import shoresoflegenor.tiles.AbstractMapTile.TerrainType;
import shoresoflegenor.uifeatures.MapGridClickAdapter;
import shoresoflegenor.world.GameData;

/**
 * A map panel for displaying world tiles, entities, buildings, and other
 * gameplay elements.
 * <p>
 * DO NOT add generic components to this panel, unless it is a disposable popup
 * menu or tile animation. Resizing/redrawing of the map will remove ALL
 * components from the panel.
 */
public class WorldMap extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final int RELATIVE_SIZE_LOCKED = 4;
	public static final int RELATIVE_SIZE_BOUND_LOW = 32;
	public static final int RELATIVE_SIZE_DEFAULT = 64;
	public static final int RELATIVE_SIZE_BOUND_HIGH = 128;

	private static final Color GRID_LAND = new Color(100, 75, 50);
	private static final Color GRID_WATER = new Color(60, 75, 80);

	private AbstractMapTile[][] abstractGrid;
	private boolean dynamicRendering; // viewport/non-interactive maps -> FALSE

	public WorldGraphics mapGraphics;
	public EntityGraphics entityGraphics;

	public boolean chunkLock = false;

	private MapTile[][] grid;

	private int mapPxLen;
	private int mouseAnchX, mouseAnchY;

	public WorldMap(boolean dynamicRendering) {
		this.dynamicRendering = dynamicRendering;

		init();
	}

	public WorldMap(AbstractMapTile[][] abstractGrid, boolean dynamicRendering) {
		this.dynamicRendering = dynamicRendering;

		init();
		setAbstractMap(abstractGrid);
		setRelativeAndDraw(RELATIVE_SIZE_DEFAULT);
	}

	private void init() {
		setOpaque(false);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("EQUALS"), "zoom_in");
		getActionMap().put("zoom_in", new MapZoomInAction());
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("MINUS"), "zoom_out");
		getActionMap().put("zoom_out", new MapZoomOutAction());

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "close_frames");
		getActionMap().put("close_frames", new CloseFramesAction());

		addMouseListener(new MapAnchorClickAdapter());
		addMouseMotionListener(new MapMoveDragAdapter());

		if (dynamicRendering) {
			addMouseListener(new MapGridClickAdapter(this, GameData.gameWorld));
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (mapGraphics == null) {
			return;
		}

		// check if any chunks are unloaded
		if (!chunkLock) {
			Runnable runnable = () -> {
				Rectangle rect = getScreenBounds();
				rect.grow(100, 100);
				mapGraphics.chunkUpdate(rect);
			};

			if (dynamicRendering) {
				GraphicsWorker.performBackgroundRun(runnable);
			} else {
				runnable.run();
			}
		}

		int paintCycles = 2;
		int cycleWater = 0;
		int cycleLand = 1;

		for (int cyc = 0; cyc < paintCycles; cyc++) {
			if (cyc == cycleWater) {
				mapGraphics.renderFromCaches(g, CacheType.WATER);
			} else if (cyc == cycleLand) {
				mapGraphics.renderFromCaches(g, CacheType.LAND);
			}

			for (ChunkTile[] chunks : mapGraphics.chunkGrid) {
				for (ChunkTile chunk : chunks) {
					if (chunk.isRendered) {
						for (MapTile[] tiles : chunk.getTileMapFromChunk(grid)) {
							for (MapTile tile : tiles) {
								/*
								 * The tile map recieved from the chunk may have null tiles due to map size
								 * bounds, these may be checked via a NullPointerException.
								 */
								try {
									boolean isWater = tile.terrainType == TerrainType.WATER;

									if (tile.base.intersects(getScreenBounds())) {
										if (cyc == cycleWater && isWater) {
											g.setColor(GRID_WATER);
											g.drawPolygon(tile.base);
										} else if (cyc == cycleLand && !isWater) {
											g.setColor(GRID_LAND);
											g.drawPolygon(tile.base);
										}
									}
								} catch (NullPointerException ex) {
									continue;
								}
							}
						}
					}
				}
			}
		}

		if (dynamicRendering) {
			entityGraphics.renderEntityPool(g, GameData.gameWorld.worldBuildingPool);
			entityGraphics.renderEntityPool(g, GameData.gameWorld.worldUnitPool);
		}
		
	}

	/**
	 * Completely resets the <code>MapTile</code> grid as well as all graphics
	 * caches. This method can be resource intensive and should only be used when
	 * resizing or regenerating the entire map.
	 */
	public void redraw() {
		reGrid();
		int chunkSizeTarget = getScreenBounds().width / grid[0][0].getTileLength();
		int size = dynamicRendering ? chunkSizeTarget : getSubs();

		mapGraphics = new WorldGraphics(this, size);

		if (dynamicRendering) {
			entityGraphics = new EntityGraphics(this);
			GraphicsWorker.performBackgroundRun(() -> {
				mapGraphics.genCaches(getScreenBounds());
			});
		} else {
			mapGraphics.genCaches(getScreenBounds());
		}

		repaint();
	}

	/**
	 * Changes the terrain data of the map using a new <code>AbstractMapTile</code>
	 * grid to allow for changes in world generation.
	 * 
	 * @param map the new tile grid to be set onto the current map.
	 */
	public void setAbstractMap(AbstractMapTile[][] map) {
		abstractGrid = AbstractMapTile.copyMap(map);

	}

	/**
	 * Changes the length of the map to a new value relative to the current map grid
	 * size (each map tile will be roughly <code>relativeLength</code> pixels in
	 * length). Setting this to any value <code>RELATIVE_SIZE_BOUND_LOW</code> or
	 * <code>RELATIVE_SIZE_BOUND_HIGH</code> may result in map zooming issues.
	 * 
	 * @param relativeLength the pixel length of a single map tile
	 */
	public void setRelativeAndDraw(int relativeLength) {
		mapPxLen = getSubs() * relativeLength;
		setSize(mapPxLen, mapPxLen / 2);
		redraw();
	}

	/**
	 * Returns a copy of the <code>AbstractMapTile</code> grid containing terrain
	 * type data for all map spaces. The return value does not reference the same
	 * <code>AbstractMapTile[][]</code> object as in any map.
	 */
	public AbstractMapTile[][] getAbstractMap() {
		return AbstractMapTile.copyMap(abstractGrid);
	}

	/**
	 * Returns a copy of the <code>MapTile</code> grid, containing both graphical
	 * and terrain type data for all map spaces. The return value does not reference
	 * the same <code>MapTile[][]</code> object as in any map.
	 */
	public MapTile[][] getMap() {
		return grid != null ? grid : null;
	}

	/**
	 * Returns the pixel length of map (x axis).
	 */
	public int getLength() {
		return mapPxLen;
	}

	/**
	 * Returns the tile length of the current map.
	 */
	public int getSubs() {
		return abstractGrid.length;
	}

	/**
	 * Returns the <code>MapTile</code> containing a given point. Returns
	 * <code>null</code> if there is no such tile or if the tile is not contained
	 * within a currently rendered chunk.
	 * 
	 * @param locX the x coordinate relative to the map component of the point
	 * @param locY the y coordinate relative to the map component of the point
	 */
	public MapTile getTileFromLocation(int locX, int locY) {
		for (ChunkTile[] chunks : mapGraphics.chunkGrid) {
			for (ChunkTile chunk : chunks) {
				if (chunk.isRendered) {
					/*
					 * Tiles are checked in two cycles - first land and then water. This prevents
					 * selection of water tiles that are "tucked behind" elevated land tiles.
					 */
					int cycles = 2;
					int landCheck = 0;
					int waterCheck = 1;

					for (int cyc = 0; cyc < cycles; cyc++) {
						for (MapTile[] tiles : chunk.getTileMapFromChunk(grid)) {
							for (MapTile tile : tiles) {
								/*
								 * The tile map recieved from the chunk may have null tiles due to map size
								 * bounds, these may be checked via a NullPointerException.
								 */
								try {
									if (tile.base.contains(new Point(locX, locY))) {
										if ((tile.terrainType != TerrainType.WATER && cyc == landCheck)
												|| (tile.terrainType == TerrainType.WATER && cyc == waterCheck)) {
											return tile;
										}
									}
								} catch (NullPointerException ex) {
									continue;
								}
							}
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Zooms the map in or out, anchoring the map at the given point.
	 * 
	 * @param anchor the point on the map that will locked during resizing.
	 * @param in     true if you want the map to zoom in, false for out.
	 */
	public void doMapZoom(Point anchor, boolean in) {
		int zoomFactor = 8 * getSubs();
		int netZoom = 0;
		int ogLength = mapPxLen;

		int minMapSize = getSubs() * RELATIVE_SIZE_BOUND_LOW;
		int maxMapSize = getSubs() * RELATIVE_SIZE_BOUND_HIGH;

		int lockSize = getSubs() * RELATIVE_SIZE_LOCKED;

		if (mapPxLen != lockSize) {
			if (in) {
				if (mapPxLen < maxMapSize) {
					mapPxLen += zoomFactor;
					netZoom += zoomFactor;
				}
			} else {
				if (mapPxLen > minMapSize) {
					mapPxLen -= zoomFactor;
					netZoom -= zoomFactor;
				}
			}

			if (netZoom != 0) {
				setLocation((int) (getX() - netZoom * (anchor.x / (float) ogLength)),
						(int) (getY() - (netZoom / 2) * (anchor.y / (float) (ogLength / 2))));
				setSize(mapPxLen, mapPxLen / 2);
				removeAll(); // canels ongoing animations
				redraw();
				repaint();
			}
		}
	}

	/*
	 * Creates a new MapTile grid from the current AbstractMapTile grid, adjusted to
	 * fit the bounds of the current component size.
	 */
	private void reGrid() {
		grid = MapTile.createTileMap(mapPxLen, getSubs(), abstractGrid);
	}

	/*
	 * Attempts to return a rectangle representing the bounding box of the parent
	 * component. If no parent exists, the bounding box of the current TerrainMap is
	 * provided instead.
	 */
	private Rectangle getScreenBounds() {
		try {
			return new Rectangle(-getX(), -getY(), getParent().getWidth(), getParent().getHeight());
		} catch (NullPointerException ex) {
			return new Rectangle(0, 0, getWidth(), getHeight());
		}
	}

	/*
	 * Returns the current mouse pointer location relative to the current component.
	 */
	private Point getMouseLoc() {
		Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mouseLocation, this);
		return mouseLocation;
	}

	@SuppressWarnings("serial")
	private class MapZoomInAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			doMapZoom(getMouseLoc(), true);
		}
	}

	@SuppressWarnings("serial")
	private class MapZoomOutAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			doMapZoom(getMouseLoc(), false);
		}
	}

	@SuppressWarnings("serial")
	private class CloseFramesAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			removeAll();
			repaint();
		}
	}

	private class MapAnchorClickAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			mouseAnchX = e.getX();
			mouseAnchY = e.getY();
		}
	}

	private class MapMoveDragAdapter extends MouseMotionAdapter {
		@Override
		public void mouseDragged(MouseEvent e) {
			if (SwingUtilities.isMiddleMouseButton(e)) {
				Point point = getLocation();
				int x = point.x + (e.getX() - mouseAnchX);
				int y = point.y + (e.getY() - mouseAnchY);
				boolean moveY = y > getScreenBounds().height - (getLength() / 2) && y < 0;
				boolean moveX = x > getScreenBounds().width - getLength() && x < 0;

				setLocation(moveX ? x : getX(), moveY ? y : getY());
			}
		}
	}

}
