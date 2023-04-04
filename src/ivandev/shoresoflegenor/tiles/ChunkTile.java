package ivandev.shoresoflegenor.tiles;

import java.awt.Rectangle;

@SuppressWarnings("serial")
public class ChunkTile extends PolyTile {

	private int mapX, mapY;
	private int size;

	public boolean isRendered;

	public ChunkTile(PolyTile poly, int mapX, int mapY, int size) {
		super(poly.corLeft, poly.corTop, poly.corRight, poly.corBottom);
		this.mapX = mapX;
		this.mapY = mapY;
		this.size = size;

		isRendered = false;
	}

	public int originX() {
		return mapX;
	}

	public int originY() {
		return mapY;
	}

	public int size() {
		return size;
	}

	public static ChunkTile getChunkFromMapTile(ChunkTile[][] chunkGrid, int tileX, int tileY) {
		for (ChunkTile[] chunks : chunkGrid) {
			for (ChunkTile chunk : chunks) {
				int xInChunk = tileX - chunk.mapX;
				int yInChunk = tileY - chunk.mapY;
				int cSize = chunk.size;
				if (xInChunk >= 0 && xInChunk < cSize && yInChunk >= 0 && yInChunk < cSize) {
					return chunk;
				}
			}
		}
		return null;
	}

	public MapTile[][] getTileMapFromChunk(MapTile[][] parentMap) {
		MapTile[][] map = new MapTile[size][size];
		Rectangle r = new Rectangle(mapX, mapY, size, size);
		for (int x = r.x; x < r.x + r.width; x++) {
			for (int y = r.y; y < r.y + r.height; y++) {
				/*
				 * If the current chunk border goes outside the bounds of the map, skip over the
				 * current tile. This may occur if the map size is not a multiple of the current
				 * chunk size.
				 */
				try {
					map[x - r.x][y - r.y] = parentMap[x][y];
				} catch (ArrayIndexOutOfBoundsException ex) {
					continue;
				}
			}
		}
		return map;
	}
}
