package shoresoflegenor.entities.units;

import shoresoflegenor.graphics.UnitAnimCache;
import shoresoflegenor.player.GamePlayer;

public class TankUnit extends UnitEntity {

	private static final long serialVersionUID = 1L;

	private static final UnitAnimCache ANIM_CACHE = new UnitAnimCache( // ALL TANK ASSETS
			"entities/tanks/tank_idle_w.png", // idle facing W
			"entities/tanks/tank_idle_n.png", // idle facing N
			"entities/tanks/tank_idle_e.png", // idle facing E
			"entities/tanks/tank_idle_s.png", // idle facing S
			"entities/tanks/tank_idle_w.png", // attack facing W
			"entities/tanks/tank_idle_n.png", // attack facing N
			"entities/tanks/tank_idle_e.png", // attack facing E
			"entities/tanks/tank_idle_s.png" // attack facing S
	);

	private static final UnitData DATA = new UnitData(100, 10, 5, 2);

	public TankUnit(GamePlayer owner, String name, int xPos, int yPos) {
		super(owner, name, UnitType.GENERIC, ANIM_CACHE, DATA, xPos, yPos);
	}

}
