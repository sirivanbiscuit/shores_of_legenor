package removedfeatures;

public abstract class TerrainRenderer implements Runnable {
	/*
	 * private MapTile[][] grid; private Graphics g; private boolean is3D;
	 * 
	 * private static final int DRAW_CYCLES = 3;
	 * 
	 * private static final String CYCLE_WATER = "gen-0"; private static final
	 * String CYCLE_ELEVATION = "gen-1"; private static final String CYCLE_LAND =
	 * "gen-2";
	 * 
	 * private static final Color GRID_LAND = new Color(100, 75, 50); private static
	 * final Color GRID_WATER = new Color(60, 75, 80);
	 * 
	 * public TerrainRenderer(MapTile[][] grid, Graphics g, boolean is3D) {
	 * this.grid = grid; this.g = g; this.is3D = is3D;
	 * 
	 * render(); }
	 * 
	 * @Override public void run() { String thread =
	 * Thread.currentThread().getName(); System.out.println(thread);
	 * 
	 * for (MapTile[] tiles : grid) { for (MapTile tile : tiles) { boolean
	 * isWaterTile = tile.terrainId == AbstractMapTile.ID_WATER; GraphicsTile gT =
	 * new GraphicsTile(tile, thread != CYCLE_ELEVATION);
	 * 
	 * if (thread == CYCLE_WATER && isWaterTile) { GraphicsTile.drawGraphicsTile(g,
	 * gT); g.setColor(GRID_WATER); g.drawPolygon(tile.base); } else if (thread ==
	 * CYCLE_ELEVATION && !isWaterTile && is3D) { GraphicsTile.drawGraphicsTile(g,
	 * gT); } else if (thread == CYCLE_LAND && !isWaterTile) {
	 * GraphicsTile.drawGraphicsTile(g, gT); g.setColor(GRID_LAND);
	 * g.drawPolygon(tile.base); } } } }
	 * 
	 * private void render() { for (int cyc = 0; cyc < DRAW_CYCLES; cyc++) {
	 * 
	 * new Thread(this, "gen-" + cyc).start(); } }
	 */
}
