package shoresoflegenor.world;

import java.io.Serializable;
import java.util.ArrayList;

import shoresoflegenor.entities.buildings.BuildingEntity;
import shoresoflegenor.entities.units.UnitEntity;
import shoresoflegenor.player.GamePlayer;
import shoresoflegenor.tiles.AbstractMapTile;

public class GameWorld implements Serializable {

	private static final long serialVersionUID = 1L;

	public AbstractMapTile[][] worldLandscapeMap;
	public UnitEntity[][] worldUnitMap;
	public BuildingEntity[][] worldBuildingMap;

	public GamePlayer[] worldPlayers;

	/*
	 * There is an ArrayList "pool" for each entity type that contains unordered
	 * instances of all present entities on the map. This helps in rendering
	 * quicker, since most of the map (as well as the above arrays) will typically
	 * be empty of entities.
	 * 
	 * For sucessful rendering this way, the pools MUST be synced every time the
	 * above arrays are altered in any way.
	 */
	public ArrayList<UnitEntity> worldUnitPool;
	public ArrayList<BuildingEntity> worldBuildingPool;

	/*
	 * This constructor only exists for easier data transfer between save files and
	 * the rest of the code framework.
	 */
	public GameWorld() {
		worldUnitPool = new ArrayList<>();
		worldBuildingPool = new ArrayList<>();
	}

	public void initEntityWorld(int worldSize) {
		worldUnitMap = new UnitEntity[worldSize][worldSize];
		worldBuildingMap = new BuildingEntity[worldSize][worldSize];
	}

	public void spawnUnitEntity(UnitEntity entity) {
		if (worldUnitMap[entity.xPos][entity.yPos] == null) {
			worldUnitMap[entity.xPos][entity.yPos] = entity;
			syncEntityPools();
		}
	}

	public void syncEntityPools() {
		// clear and reset the unit pool
		worldUnitPool.clear();
		UnitEntity[][] uMap = worldUnitMap;
		for (int x = 0; x < uMap.length; x++) {
			for (int y = uMap.length - 1; y >= 0; y--) {
				if (uMap[x][y] != null) {
					worldUnitPool.add(uMap[x][y]);
				}
			}
		}

		// clear and reset the building pool
		worldBuildingPool.clear();
		BuildingEntity[][] bMap = worldBuildingMap;
		for (int x = 0; x < bMap.length; x++) {
			for (int y = bMap.length - 1; y >= 0; y--) {
				if (bMap[x][y] != null) {
					worldBuildingPool.add(bMap[x][y]);
				}
			}
		}

		// redraw the locked changes
		GameData.refreshActiveGraphics();
	}
}
