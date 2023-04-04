package shoresoflegenor.uifeatures.actionspaces;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import shoresoflegenor.entities.units.UnitEntity;
import shoresoflegenor.tiles.MapTile;
import shoresoflegenor.world.GameData;

@SuppressWarnings("serial")
public class MovementSpace extends ActionSpace {

	public MovementSpace(UnitEntity sourceEntity, MapTile orgTile) {
		super(sourceEntity, orgTile, SpaceAction.MOVE);

		initSpace();
	}

	private void initSpace() {
		addMouseListener(new MoveEntityAdapter());
	}

	private class MoveEntityAdapter extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				MapTile org = getOrigin();
				GameData.refreshActiveCanvas();
				getSource().moveEntity(org.gridX, org.gridY);
			}
		}
	}

}