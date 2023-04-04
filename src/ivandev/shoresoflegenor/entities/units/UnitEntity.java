package ivandev.shoresoflegenor.entities.units;

import ivandev.shoresoflegenor.entities.GameEntity;
import ivandev.shoresoflegenor.graphics.GraphicsWorker;
import ivandev.shoresoflegenor.graphics.UnitAnimCache;
import ivandev.shoresoflegenor.player.GamePlayer;
import ivandev.shoresoflegenor.tiles.AbstractMapTile.TerrainType;
import ivandev.shoresoflegenor.world.GameData;

public abstract class UnitEntity extends GameEntity {

	public enum UnitType {
		GENERIC, NAVAL, SCOUT
	}

	private static final long serialVersionUID = 1L;

	public UnitAnimCache animCache;
	public UnitType unitType;
	public UnitData data;

	public boolean onCooldown;

	public UnitEntity(GamePlayer owner, String name, UnitType unitType, UnitAnimCache animCache, UnitData data,
			int xPos, int yPos) {
		super(owner, name, animCache.defaultImagePath(), xPos, yPos);

		this.unitType = unitType;
		this.animCache = animCache;
		this.data = data;
	}

	public void moveEntity(int destX, int destY) {
		int travelX = destX - xPos;
		int travelY = destY - yPos;

		// change the texture based on direction change
		if (Math.abs(travelX) > Math.abs(travelY)) {
			texPath = travelX > 0 ? animCache.pathIdleE : animCache.pathIdleW;
		} else {
			texPath = travelY > 0 ? animCache.pathIdleN : animCache.pathIdleS;
		}

		// move the entity and draw
		UnitEntity[][] unitMap = GameData.gameWorld.worldUnitMap;
		if (unitMap[destX][destY] == null) {
			unitMap[destX][destY] = this;
			unitMap[xPos][yPos] = null;
			xPos = destX;
			yPos = destY;
			onCooldown = true;
			GameData.gameWorld.syncEntityPools();
			GraphicsWorker.performBackgroundRun(() -> {
				try {
					Thread.currentThread();
					Thread.sleep(1000 * (long) (10 / (data.spd * data.org)));
					onCooldown = false;
					GameData.gameWorld.syncEntityPools();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			});
		}
	}

	public boolean canMoveOnTerrainType(TerrainType type) {
		switch (type) {
		case WATER: {
			return unitType == UnitType.NAVAL;
		}
		case MOUNTAIN: {
			return unitType == UnitType.SCOUT;
		}
		case GRASS, FOREST: {
			return unitType == UnitType.SCOUT || unitType == UnitType.GENERIC;
		}
		}
		return false;
	}

}
