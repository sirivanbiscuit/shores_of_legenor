package ivandev.shoresoflegenor.graphics;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import ivandev.shoresoflegenor.maps.WorldMap;
import ivandev.shoresoflegenor.tiles.AbstractMapTile.TerrainType;
import ivandev.shoresoflegenor.tiles.ChunkTile;
import ivandev.shoresoflegenor.tiles.GraphicsTile;
import ivandev.shoresoflegenor.tiles.PolyTile;

public class WorldGraphics {

	public enum CacheType {
		WATER, LAND
	}

	private WorldMap refMap;
	private int chunkSize;

	private BufferedImage[][][] chunkCaches;
	public ChunkTile[][] chunkGrid;

	private int gridSize;

	public WorldGraphics(WorldMap refMap, int chunkSize) {
		this.refMap = refMap;
		this.chunkSize = chunkSize;

		initCache();
		createChunkGrid();
	}

	private void initCache() {
		gridSize = refMap.getMap().length / chunkSize + (refMap.getMap().length % chunkSize == 0 ? 0 : 1);
		chunkCaches = new BufferedImage[CacheType.values().length][gridSize][gridSize];
	}

	/**
	 * Divides the given <code>refMap</code> into 32x32 regions ("chunks") stored as
	 * <code>ChunkTile</code>s (this will help to show the renderer where to draw
	 * images).
	 */
	public void createChunkGrid() {
		chunkGrid = new ChunkTile[gridSize][gridSize];

		for (int x = 0; x < gridSize; x++) {
			for (int y = 0; y < gridSize; y++) {
				/*
				 * Expanding the chunk by 1 tile in each direction ensures smooth texture
				 * transitions between bordering chunks.
				 */

				int tileOrgX = x * chunkSize;
				int tileOrgY = y * chunkSize;
				int size = chunkSize + 2;

				PolyTile p = refMap.getMap()[tileOrgX][tileOrgY].getAbsoluteBase();
				p = p.getTranslation(-p.getBounds().width, 0);
				p = p.getScalar(size);

				chunkGrid[x][y] = new ChunkTile(p, tileOrgX - 1, tileOrgY - 1, size);
			}
		}
	}

	/**
	 * Generates a new image cache for rendering. All map chunks that either
	 * intersect or are contained within the given <code>bounds</code> will be
	 * generated as images and cached.
	 * 
	 * @param bounds the bounding box of the portion of the map to be cached
	 */
	public void genCaches(Rectangle bounds) {
		for (int x = 0; x < gridSize; x++) {
			for (int y = 0; y < gridSize; y++) {
				genCaches(bounds, x, y);
			}
		}
	}

	/**
	 * 
	 * @param bounds
	 * @param chunkGridX
	 * @param chunkGridY
	 */
	public void genCaches(Rectangle bounds, int chunkGridX, int chunkGridY) {
		int x = chunkGridX;
		int y = chunkGridY;

		ChunkTile chunk = chunkGrid[x][y];
		int len = chunk.corRight.x - chunk.corLeft.x;

		boolean nullInCache = chunkCaches[0][x][y] == null;
		boolean renderable = chunk.intersects(bounds);

		if (renderable && nullInCache) {
			BufferedImage bImgWater = new BufferedImage(len, len / 2, BufferedImage.TYPE_4BYTE_ABGR);
			BufferedImage bImgLand = new BufferedImage(len, len / 2, BufferedImage.TYPE_4BYTE_ABGR);

			Graphics gW = bImgWater.createGraphics();
			Graphics gL = bImgLand.createGraphics();

			drawChunk(gW, gL, chunk);

			chunkCaches[getCacheTypeId(CacheType.WATER)][x][y] = bImgWater;
			chunkCaches[getCacheTypeId(CacheType.LAND)][x][y] = bImgLand;
		}

	}

	/**
	 * Renders all cached chunk images to the given graphics context.
	 * 
	 * @param g    the graphics context for rendering
	 * @param type the graphics layer to render
	 */
	public void renderFromCaches(Graphics g, CacheType type) {
		int cacheId = getCacheTypeId(type);

		for (int x = 0; x < gridSize; x++) {
			for (int y = gridSize - 1; y >= 0; y--) {
				if (chunkCaches[cacheId][x][y] != null) {
					ChunkTile chunk = chunkGrid[x][y];
					BufferedImage img = chunkCaches[cacheId][x][y];

					g.drawImage(img, chunk.corLeft.x, chunk.corTop.y, img.getWidth(), img.getHeight(), null);
				}
			}
		}
	}

	/**
	 * Checks if any unloaded chunks require loading. If any are present, a cache
	 * generator will run, in turn filling in said chunks.
	 * 
	 * @param bounds the rectangle enclosing the area to check for chunks
	 */
	public void chunkUpdate(Rectangle bounds) {
		for (int x = 0; x < gridSize; x++) {
			for (int y = 0; y < gridSize; y++) {
				boolean inBounds = chunkGrid[x][y].intersects(bounds);

				if (inBounds) {
					if (chunkCaches[0][x][y] == null) {
						genCaches(bounds, x, y);
						refMap.repaint();
					}
					chunkGrid[x][y].isRendered = true;
				} else {
					for (CacheType t : CacheType.values()) {
						chunkCaches[getCacheTypeId(t)][x][y] = null;
						chunkGrid[x][y].isRendered = false;
					}
				}
			}
		}

	}

	/*
	 * Takes a portion of the refMapGrid and renders in onto a certain graphics
	 * context. Both land and water are rendered simulataneously into seperate
	 * contexts. Note the origin point is the refMapGrid id of the left-most point
	 * and NOT the actual component coordinates.
	 */
	private void drawChunk(Graphics gWater, Graphics gLand, ChunkTile chunk) {

		int drawCycles = 3;
		int cycleWater = 0;
		int cycleElevation = 1;
		int cycleLand = 2;

		for (int cyc = 0; cyc < drawCycles; cyc++) {
			for (int x = chunk.originX(); x < chunk.originX() + chunk.size(); x++) {
				for (int y = chunk.originY() + chunk.size() - 1; y >= 0; y--) {
					/*
					 * If the current chunk border goes outside the bounds of the map, skip over the
					 * current tile. This may occur if the map size is not a multiple of the current
					 * chunk size.
					 */
					try {

						boolean isWaterTile = refMap.getMap()[x][y].terrainType == TerrainType.WATER;
						GraphicsTile graphicsTile = new GraphicsTile(
								refMap.getMap()[x][y].getTranslatedCopy(-chunk.corLeft.x, -chunk.corTop.y),
								cyc != cycleElevation);

						if (cyc == cycleWater && isWaterTile) {
							graphicsTile.drawGraphicsTile(gWater);
						} else if (cyc == cycleElevation && !isWaterTile) {
							graphicsTile.drawGraphicsTile(gLand);
						} else if (cyc == cycleLand && !isWaterTile) {
							graphicsTile.drawGraphicsTile(gLand);
						}

					} catch (ArrayIndexOutOfBoundsException ex) {
						continue;
					}
				}
			}
		}

		gWater.dispose();
		gLand.dispose();
	}

	/*
	 * 
	 */
	private int getCacheTypeId(CacheType type) {
		switch (type) {
		case WATER:
			return 0;
		case LAND:
			return 1;
		}
		return -1;
	}
}
