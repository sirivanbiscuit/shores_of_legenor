package shoresoflegenor.tiles;

import java.io.Serializable;
import java.util.ArrayList;

public class AbstractMapTile implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TerrainType {
		WATER, GRASS, FOREST, MOUNTAIN;
	}

	public enum LandCondition {
		WATERLOCKED, LANDLOCKED, NEAR_LAND, NEAR_WATER, PENINSULA, GULF
	}

	public enum GenProcedure {
		DRY, ERODE
	}

	public TerrainType terrainType;

	public AbstractMapTile(TerrainType terrainType) {
		this.terrainType = terrainType;
	}
	
	public static AbstractMapTile[][] copyMap(AbstractMapTile[][] map) {
		int l = map.length;
		AbstractMapTile[][] dupMap = new AbstractMapTile[l][l];

		for (int x = 0; x < l; x++) {
			for (int y = 0; y < l; y++) {
				dupMap[x][y] = newTile(map[x][y].terrainType);
			}
		}

		return dupMap;
	}

	private static AbstractMapTile newTile(TerrainType type) {
		return new AbstractMapTile(type);
	}

	public static boolean isMapCondition(AbstractMapTile[][] map, LandCondition condition, int x, int y) {
		ArrayList<TerrainType> typeList = new ArrayList<>();

		for (int xPos = x - 1; xPos <= x + 1; xPos++) {
			for (int yPos = y - 1; yPos <= y + 1; yPos++) {
				try {
					if (!(xPos == x && yPos == y)) {
						typeList.add(map[xPos][yPos].terrainType);
					}
				} catch (ArrayIndexOutOfBoundsException ex) {
					continue;
				}
			}
		}

		switch (condition) {

		case WATERLOCKED: {
			// completed surrounded by water
			for (TerrainType t : typeList) {
				if (t != TerrainType.WATER) {
					return false;
				}
			}
			return true;
		}
		case LANDLOCKED: {
			// completed surrounded by land
			for (TerrainType t : typeList) {
				if (t == TerrainType.WATER) {
					return false;
				}
			}
			return true;
		}
		case NEAR_WATER: {
			// at least 1 adjacent water tile
			return !isMapCondition(map, LandCondition.LANDLOCKED, x, y);
		}
		case NEAR_LAND: {
			// at least one adjacent land tile
			return !isMapCondition(map, LandCondition.WATERLOCKED, x, y);
		}
		case PENINSULA: {
			// at least 6 water tiles adjacent to a land tile
			int adjWater = 0;
			for (TerrainType t : typeList) {
				adjWater += (t == TerrainType.WATER) ? 1 : 0;
			}
			return map[x][y].terrainType != TerrainType.WATER && adjWater >= 6;
		}
		case GULF: {
			// at least 6 land tiles adjacent to a water tile
			int adjLand = 0;
			for (TerrainType t : typeList) {
				adjLand += (t != TerrainType.WATER) ? 1 : 0;
			}
			return map[x][y].terrainType == TerrainType.WATER && adjLand >= 6;
		}

		default: {
			return false;
		}
		}
	}

	/**
	 * Automates a number of map generation steps, beginning with a basic continent
	 * root map. Forests and mountains will be generated in between land
	 * drying/eroding stages. Rivers will spawn after such, but before the final
	 * cleaning stage. An additional single cycle erosion cycle will occur after
	 * river generation in order to thicken and further clean them. In addition,
	 * grass paths may generate within forests to lessen the possibility and
	 * unattractiveness of continent or world spanning forest biomes (which may
	 * spawn during quick generation).
	 */
	public static AbstractMapTile[][] genQuickMap(int subdivs, int continents, int landGenDryCycles,
			float landGenDryPower, int landGenErodeCycles, float landGenErodePower, int forestCycles, int forestDepth,
			int forestLength, float forestWeight, float forestFlow, int mountainCycles, int mountainDepth,
			int mountainLength, float mountainWeight, float mountainFlow, int riverCycles, int riverDepth,
			int riverLength, float riverWeight, float riverFlow, int landCleanCycles, float landCleanLand,
			float landCleanWater) {
		// generate continent map
		AbstractMapTile[][] map = new AbstractMapTile[subdivs][subdivs];
		map = AbstractMapTile.genContinentMap(subdivs, continents);

		// landGen drying
		map = AbstractMapTile.exeLandGen(map, GenProcedure.DRY, landGenDryCycles, landGenDryPower);

		// biomeStrip forests/mountains (double-sided)
		map = AbstractMapTile.exeBiomeStrip(map, forestCycles, TerrainType.FOREST, forestDepth, forestLength,
				forestWeight, forestFlow, true);
		map = AbstractMapTile.exeBiomeStrip(map, mountainCycles, TerrainType.MOUNTAIN, mountainDepth, mountainLength,
				mountainWeight, mountainFlow, true);

		// make minor forest "trails" to clean up possible super-forests
		map = AbstractMapTile.exeBiomeStrip(map, forestCycles / 4, TerrainType.GRASS, forestDepth / 2, forestLength / 8,
				forestWeight, (4 + forestFlow) / 5, true);

		// landGen erosion
		map = AbstractMapTile.exeLandGen(map, GenProcedure.ERODE, landGenErodeCycles, landGenErodePower);

		// biomeStrip rivers (single-sided)
		map = AbstractMapTile.exeBiomeStrip(map, riverCycles, TerrainType.WATER, riverDepth, riverLength, riverWeight,
				riverFlow, false);
		map = AbstractMapTile.exeLandGen(map, GenProcedure.DRY, 1, landGenErodePower / 2);

		// landClean cleaning
		map = AbstractMapTile.exeLandClean(map, landCleanCycles, landCleanLand, landCleanWater);

		return map;
	}

	/**
	 * Generates a new reference map with grass and water tiles randomly arranged
	 * across a grid.
	 * <p>
	 * <code>landDensity</code> values should be in range <code>[0,1)</code>. Values
	 * greater or equal to than <code>1</code> will produce a map of solid grass.
	 * Values less than <code>0</code> will produce a map of solid water.
	 * 
	 * @param subdivs     the length/width of the grid.
	 * @param landDensity the approximate portion of grid squares which should
	 *                    become grass
	 */
	public static AbstractMapTile[][] genRandomMap(int subdivs, float landDensity) {
		AbstractMapTile[][] map = new AbstractMapTile[subdivs][subdivs];

		for (int x = 0; x < subdivs; x++) {
			for (int y = 0; y < subdivs; y++) {
				map[x][y] = Math.random() < landDensity ? newTile(TerrainType.GRASS) : newTile(TerrainType.WATER);
			}
		}

		return map;
	}

	/**
	 * Generates a new reference map covered entirely with water and a given number
	 * of randomly placed grass tiles.
	 * 
	 * @param subdivs    the length/width of the grid
	 * @param continents the number of grass tiles to be randomly placed in the grid
	 */
	public static AbstractMapTile[][] genContinentMap(int subdivs, int continents) {
		AbstractMapTile[][] map = new AbstractMapTile[subdivs][subdivs];

		for (int x = 0; x < subdivs; x++) {
			for (int y = 0; y < subdivs; y++) {
				map[x][y] = newTile(TerrainType.WATER);
			}
		}

		int contsPlaced = 0;
		while (contsPlaced < continents) {
			int randX = (int) Math.floor(Math.random() * subdivs);
			int randY = (int) Math.floor(Math.random() * subdivs);
			map[randX][randY] = newTile(TerrainType.GRASS);
			contsPlaced++;
		}

		return map;
	}

	/**
	 * Generates a reference map from one premade. During each cycle approximately
	 * <code>power*100</code> percent of valid tiles will undergo the given
	 * procedure.
	 * <p>
	 * <code>DRY_PROCEDURE</code>: Turns water tiles bordering land tiles into new
	 * land tiles. The premade map needs to have at least one land tile and one
	 * water tile for this to take effect.
	 * <p>
	 * <code>ERODE_PROCEDURE</code>: Turns land tiles bordering water tiles into new
	 * water tiles. The premade map needs to have at least one land tile and one
	 * water tile for this to take effect.
	 * <p>
	 * <code>power</code> values should be in range <code>[0,1)</code>.
	 * 
	 * @param map         the premade reference map to be dried
	 * @param procedure   the id of the procedure to be executed on the map
	 * @param cycles      the number of times the procedure should be executed
	 * @param dryingPower the approximate protion of valid tiles to be checked
	 *                    during each cycle
	 */
	public static AbstractMapTile[][] exeLandGen(AbstractMapTile[][] map, GenProcedure procedure, int cycles,
			float power) {
		AbstractMapTile[][] mapCopy;

		for (int cyc = 0; cyc < cycles; cyc++) {
			mapCopy = copyMap(map);
			for (int x = 0; x < map.length; x++) {
				for (int y = 0; y < map[x].length; y++) {
					boolean canDry = procedure == GenProcedure.DRY && mapCopy[x][y].terrainType == TerrainType.WATER
							&& isMapCondition(mapCopy, LandCondition.NEAR_LAND, x, y) && Math.random() < power;
					boolean canErode = procedure == GenProcedure.ERODE && mapCopy[x][y].terrainType != TerrainType.WATER
							&& isMapCondition(mapCopy, LandCondition.NEAR_WATER, x, y)
							&& Math.random() < (mapCopy[x][y].terrainType == TerrainType.MOUNTAIN ? power / 2 : power);

					if (canDry) {
						map[x][y].terrainType = TerrainType.GRASS;
					} else if (canErode) {
						map[x][y].terrainType = TerrainType.WATER;
					}
				}
			}
		}

		return map;
	}

	/**
	 * Generates a reference map from one premade. During each cycle approximately
	 * <code>land*100</code> percent of peninsulas (land tiles surrounded by at
	 * least <code>6</code> water tiles) and single tile islands will be turned to
	 * water tiles. Approximately <code>water*100</code> percent of gulfs (water
	 * tiles surrounded by at least <code>6</code> land tiles) and single tile lakes
	 * will be turned to land tiles.
	 * <p>
	 * <code>land</code> values should be in range <code>[0,1)</code>.
	 * <p>
	 * <code>water</code> values should be in range <code>[0,1)</code>.
	 * 
	 * @param map    the premade reference map to be cleaned
	 * @param cycles the number of times the cleaning procedure should be executed
	 * @param land   the approximate portion of peninsulas/islands to be cleaned
	 * @param water  the approximate portion of gulfs/lakes to be cleaned
	 */
	public static AbstractMapTile[][] exeLandClean(AbstractMapTile[][] map, int cycles, float land, float water) {
		AbstractMapTile[][] mapCopy;

		for (int cyc = 0; cyc < cycles; cyc++) {
			mapCopy = copyMap(map);
			for (int x = 0; x < map.length; x++) {
				for (int y = 0; y < map[x].length; y++) {
					boolean sinkablePeninsula = isMapCondition(mapCopy, LandCondition.PENINSULA, x, y)
							&& Math.random() < land;
					boolean sinkableIsland = mapCopy[x][y].terrainType != TerrainType.WATER
							&& isMapCondition(mapCopy, LandCondition.WATERLOCKED, x, y) && Math.random() < land;
					boolean dryableGulf = isMapCondition(mapCopy, LandCondition.GULF, x, y) && Math.random() < water;
					boolean dryableLake = mapCopy[x][y].terrainType == TerrainType.WATER
							&& isMapCondition(mapCopy, LandCondition.LANDLOCKED, x, y) && Math.random() < water;

					if (sinkablePeninsula || sinkableIsland) {
						map[x][y].terrainType = TerrainType.WATER;
					} else if (dryableGulf || dryableLake) {
						map[x][y].terrainType = TerrainType.GRASS;
					}
				}
			}
		}

		return map;
	}

	/**
	 * Generates a reference map from one premade. During each cycle a random grass
	 * tile in the premade map will be turned to the given <code>terrain</code>
	 * type. A random angle will be chosen as a "spread path" and the new terrain
	 * tile will spread across a "strip" of land tiles along a line at said angle.
	 * This strip has a length of a random value in between <code>length*0.5</code>
	 * and <code>length</code>. Each tile along the strip may additionally spread to
	 * other land tiles up to <code>depth</code> tiles away. Approximately
	 * <code>weight*100</code> surrounding tiles may transform this way.
	 * Approximately <code>flow*100</code> of tiles along the original strip may
	 * also change the "spread path" to a new angle. If the generator is
	 * <code>doubleSided</code>, the tile strip will expand in two direction
	 * simulataneously in oppositite angles (the length will be doubled).
	 * <p>
	 * <code>weight</code> values should be in range <code>[0,1)</code>.
	 * <p>
	 * <code>flow</code> values should be in range <code>[0,1)</code>.
	 * 
	 * @param map         the premade map to have a biome strip added
	 * @param cycles      the number of biome strips to be made
	 * @param terrain     the terrain tile id of the biome strip
	 * @param depth       the thickness of the biome strip
	 * @param length      the maximum length of the biome strip
	 * @param weight      the density of the biome strip
	 * @param flow        the straightness of the biome strip
	 * @param doubleSided should the strip extend in opposite directions
	 */
	public static AbstractMapTile[][] exeBiomeStrip(AbstractMapTile[][] map, int cycles, TerrainType terrain, int depth,
			int length, float weight, float flow, boolean doubleSided) {
		int startX, startY;
		int shiftX = 0;
		int shiftY = 0;
		int mountPathAng;

		length = (int) (length * (Math.random() * 0.5 + 0.5));

		for (int cyc = 0; cyc < cycles; cyc++) {
			do {
				startX = (int) (Math.random() * map.length);
				startY = (int) (Math.random() * map.length);
			} while (map[startX][startY].terrainType != TerrainType.GRASS);

			do {
				mountPathAng = (int) (Math.random() * 360);
			} while (mountPathAng % 90 < 15 || mountPathAng % 90 > 75);
			float flowX = (float) Math.sin(Math.toRadians(mountPathAng));
			float flowY = (float) Math.cos(Math.toRadians(mountPathAng));

			for (int dist = 0; dist < length; dist++) {
				int sideCycle = 0;

				while (sideCycle < (doubleSided ? 2 : 1)) {
					sideCycle++;

					try {
						int plX = sideCycle == 1 ? startX + shiftX : startX - shiftX;
						int plY = sideCycle == 1 ? startY + shiftY : startY - shiftY;

						if (map[plX][plY].terrainType != TerrainType.WATER) {
							map[plX][plY].terrainType = terrain;
						}
						for (int offX = -depth; offX <= depth; offX++) {
							for (int offY = -depth; offY <= depth; offY++) {
								if (offX == plX && offY == plY) {
									continue;
								}
								int x = plX + offX;
								int y = plY + offY;
								if (map[x][y].terrainType != TerrainType.WATER && Math.random() < weight) {
									map[x][y].terrainType = terrain;
								}
							}
						}
					} catch (ArrayIndexOutOfBoundsException ex) {
						continue;
					}
				}

				if (Math.random() > (4 + flow) / 5) {
					do {
						mountPathAng = (int) (Math.random() * 360);
					} while (mountPathAng % 90 < 15 || mountPathAng % 90 > 75);
					flowX = (float) Math.sin(Math.toRadians(mountPathAng));
					flowY = (float) Math.cos(Math.toRadians(mountPathAng));
				}

				shiftX += Math.random() < Math.abs(flowX) ? (flowX > 0 ? 1 : -1) : 0;
				shiftY += Math.random() < Math.abs(flowY) ? (flowY > 0 ? 1 : -1) : 0;
			}
		}

		return map;
	}

}
