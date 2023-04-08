package shoresoflegenor.uifeatures.actionspaces;

import shoresoflegenor.entities.units.UnitEntity;
import shoresoflegenor.tiles.MapTile;

@SuppressWarnings("serial")
public class AttackSpace extends ActionSpace {

	public AttackSpace(UnitEntity sourceEntity, MapTile orgTile) {
		super(sourceEntity, orgTile, SpaceAction.ATTACK, () -> {
		});
	}

}