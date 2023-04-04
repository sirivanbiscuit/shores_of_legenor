package shoresoflegenor.tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import shoresoflegenor.tiles.AbstractMapTile.TerrainType;
import shoresoflegenor.util.ScreenUtil;

public class GraphicsTile {

	private MapTile mapTile;
	private boolean isSurface;

	private int x, y;
	private int width, height;
	private Image img;
	
	public static Color[] TILE_COLORS = new Color[] { // TILE COLOR DIRECTORY
			new Color(30, 60, 80), // water tile
			new Color(115, 160, 60), // grass tile
			new Color(90, 130, 50), // forest tile
			new Color(110, 130, 110) // mountain tile
	};
	public static Color[] ELEVATION_COLORS = new Color[] { null, // ELEVATION COLOR DIRECTORY
			new Color(100, 75, 50), // grass tile
			new Color(100, 75, 50), // forest tile
			new Color(70, 80, 70) // mountain tile
	};

	public GraphicsTile(MapTile mapTile, boolean isSurface) {
		this.mapTile = mapTile;
		this.isSurface = isSurface;

		convertFromMap();
	}

	private void convertFromMap() {
		x = mapTile.base.corLeft.x;
		y = isSurface ? mapTile.base.corTop.y : mapTile.base.corLeft.y;
		width = mapTile.base.corRight.x - x;
		height = (mapTile.base.corBottom.y - y) * (isSurface ? 1 : 2);

		String tileName = mapTile.terrainType.toString().toLowerCase();
		boolean isElevated = !(isSurface || mapTile.terrainType == TerrainType.WATER);
		img = ScreenUtil.getImage("tiles/" + tileName + (isElevated ? "_elevation" : "_tile") + ".png");
	}

	public void drawGraphicsTile(Graphics g) {
		g.drawImage(img, x, y, width, height, null);
	}
}
