package shoresoflegenor.tiles;

import java.awt.Point;

import shoresoflegenor.tiles.AbstractMapTile.TerrainType;

public class MapTile {

	public PolyTile base;
	public PolyTile elevation;
	public TerrainType terrainType;
	public int gridX, gridY;

	public MapTile(PolyTile base, PolyTile elevation, TerrainType terrainType, int gridX, int gridY) {
		this.base = base;
		this.elevation = elevation;
		this.terrainType = terrainType;
		this.gridX = gridX;
		this.gridY = gridY;
	}

	public MapTile(PolyTile base, PolyTile elevation, TerrainType terrainType) {
		this.base = base;
		this.elevation = elevation;
		this.terrainType = terrainType;
	}

	public MapTile getTranslatedCopy(int tX, int tY) {
		PolyTile tile = new PolyTile(newPt(base.corLeft.x + tX, base.corLeft.y + tY),
				newPt(base.corTop.x + tX, base.corTop.y + tY), newPt(base.corRight.x + tX, base.corRight.y + tY),
				newPt(base.corBottom.x + tX, base.corBottom.y + tY));

		if (elevation == null) {
			return new MapTile(tile, null, terrainType);
		} else {
			return new MapTile(tile,
					new PolyTile(newPt(elevation.corLeftUp.x + tX, elevation.corLeftUp.y + tY),
							newPt(elevation.corTop.x + tX, elevation.corTop.y + tY),
							newPt(elevation.corRightUp.x + tX, elevation.corRightUp.y + tY),
							newPt(elevation.corRightDown.x + tX, elevation.corRightDown.y + tY),
							newPt(elevation.corBottom.x + tX, elevation.corBottom.y + tY),
							newPt(elevation.corLeftDown.x + tX, elevation.corLeftDown.y + tY)),
					terrainType);
		}
	}

	public PolyTile getAbsoluteBase() {
		if (elevation == null) {
			return base.getClone();
		} else {
			int oS = (base.corRight.x - base.corLeft.x) / 4;
			return new PolyTile(newPt(base.corLeft.x, base.corLeft.y + oS), newPt(base.corTop.x, base.corTop.y + oS),
					newPt(base.corRight.x, base.corRight.y + oS), newPt(base.corBottom.x, base.corBottom.y + oS));
		}
	}

	public int getTileLength() {
		return base.corRight.x - base.corLeft.x;
	}

	public static Point newPt(int x, int y) {
		return new Point(x, y);
	}

	public static MapTile[][] createTileMap(int length, int subdivs, AbstractMapTile[][] abstrMap) {
		MapTile[][] map = new MapTile[subdivs][subdivs];

		int tileLength = length / subdivs;
		int tileRadH = tileLength / 2;
		int tileRadV = tileLength / 4;

		/*
		 * Note that the x-axis in the map faces the top corner, and the y-axis faces
		 * the bottom corner (due to the map being diamond shaped).
		 */
		Point mapOrigin = newPt(0, length / 4); // the left corner of the map
		for (int x = 0; x < subdivs; x++) {
			for (int y = 0; y < subdivs; y++) {
				int orgX = mapOrigin.x + (x + y) * tileRadH;
				int orgY = mapOrigin.y + (x - y) * tileRadV;
				boolean elevatedTile = abstrMap[x][y].terrainType != TerrainType.WATER;
				int oS = elevatedTile ? tileLength / 4 : 0;

				PolyTile baseTile = new PolyTile(newPt(orgX, orgY - oS), // left corner
						newPt(orgX + tileRadH, orgY - tileRadV - oS), // top corner
						newPt(orgX + tileLength, orgY - oS), // right corner
						newPt(orgX + tileRadH, orgY + tileRadV - oS)); // bottom corner

				if (elevatedTile) {
					PolyTile elevationTile = new PolyTile(baseTile.corLeft, // top left corner
							baseTile.corTop, // top corner
							baseTile.corRight, // top right corner
							newPt(baseTile.corRight.x, baseTile.corRight.y + oS), // bottom right corner
							newPt(baseTile.corBottom.x, baseTile.corBottom.y + oS), // bottom corner
							newPt(baseTile.corLeft.x, baseTile.corLeft.y + oS)); // bottom left corner
					map[x][y] = new MapTile(baseTile, elevationTile, abstrMap[x][y].terrainType, x, y);

				} else {
					map[x][y] = new MapTile(baseTile, null, abstrMap[x][y].terrainType, x, y);
				}

			}
		}

		return map;
	}
}
