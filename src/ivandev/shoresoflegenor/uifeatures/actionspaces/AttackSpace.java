package ivandev.shoresoflegenor.uifeatures.actionspaces;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import ivandev.shoresoflegenor.entities.units.UnitEntity;
import ivandev.shoresoflegenor.tiles.MapTile;
import ivandev.shoresoflegenor.world.GameData;

@SuppressWarnings("serial")
public class AttackSpace extends ActionSpace {

	public AttackSpace(UnitEntity sourceEntity, MapTile orgTile) {
		super(sourceEntity, orgTile, SpaceAction.ATTACK);

		initSpace();
	}

	private void initSpace() {
		addMouseListener(new AttackEntityAdapter());
	}

	private class AttackEntityAdapter extends MouseAdapter {

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