package ivandev.shoresoflegenor.entities.buildings;

import ivandev.shoresoflegenor.entities.GameEntity;
import ivandev.shoresoflegenor.player.GamePlayer;

public abstract class BuildingEntity extends GameEntity {

	private static final long serialVersionUID = 1L;

	public BuildingEntity(GamePlayer owner, String name, String texPath, int xPos, int yPos) {
		super(owner, name, texPath, xPos, yPos);
	}

}
