package shoresoflegenor.uifeatures.actionspaces;

import shoresoflegenor.entities.units.UnitEntity;
import shoresoflegenor.tiles.MapTile;
import shoresoflegenor.world.GameData;

@SuppressWarnings("serial")
public class MovementSpace extends ActionSpace {

	public MovementSpace(UnitEntity sourceEntity, MapTile orgTile) {
		super(sourceEntity, orgTile, SpaceAction.MOVE, () -> {
			GameData.refreshActiveCanvas();
			sourceEntity.moveEntity(orgTile.gridX, orgTile.gridY);
		});
	}
}