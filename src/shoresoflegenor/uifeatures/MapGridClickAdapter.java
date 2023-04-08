package shoresoflegenor.uifeatures;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import shoresoflegenor.entities.units.UnitEntity;
import shoresoflegenor.maps.WorldMap;
import shoresoflegenor.tiles.MapTile;
import shoresoflegenor.uifeatures.actionspaces.ActionSpace;
import shoresoflegenor.uifeatures.actionspaces.AttackSpace;
import shoresoflegenor.uifeatures.actionspaces.MovementSpace;
import shoresoflegenor.util.SoundUtil;
import shoresoflegenor.world.GameData;
import shoresoflegenor.world.GameWorld;

public class MapGridClickAdapter extends MouseAdapter {

	private WorldMap map;
	private GameWorld world;

	private Color graphicsC;

	public MapGridClickAdapter(WorldMap map, GameWorld world) {
		this.map = map;
		this.world = world;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isMiddleMouseButton(e)) {
			return;
		}

		// get the main map graphics context
		Graphics graphics = map.getGraphics();

		MapTile t = map.getTileFromLocation(e.getX(), e.getY());
		if (t != null) {
			if (t.base.contains(e.getPoint())) {
				GameData.refreshActiveCanvas();

				graphicsC = ActionSpace.UNDEFINED;

				UnitEntity ent = world.worldUnitMap[t.gridX][t.gridY];
				boolean hasUnit = ent != null;
				boolean openStat = hasUnit && SwingUtilities.isRightMouseButton(e);
				boolean openAction = hasUnit && SwingUtilities.isLeftMouseButton(e);

				// unit info panel
				if (openStat) {
					SoundUtil.playClick();
					map.add(new UnitStatFrame(ent, t));
					graphicsC = ActionSpace.INFO;
				}

				// action spaces
				else if (openAction) {
					if (ent.owner.botPlayer == null && !ent.onCooldown) {
						SoundUtil.playClick();
						graphicsC = ActionSpace.ACTION;

						// movement spaces
						for (int oSX = -1; oSX <= 1; oSX++) {
							for (int oSY = -1; oSY <= 1; oSY++) {
								try {
									if (!(oSX == 0 && oSY == 0)) {
										Point targ = new Point(t.gridX + oSX, t.gridY + oSY);
										MapTile targTile = map.getMap()[targ.x][targ.y];

										if (world.worldUnitMap[targ.x][targ.y] == null
												&& ent.canMoveOnTerrainType(targTile.terrainType)) {
											map.add(new MovementSpace(ent, targTile));
										}
									}
								} catch (ArrayIndexOutOfBoundsException ex) {
									continue;
								}
							}
						}

						// attack spaces
						for (int oSX = -ent.data.rng; oSX <= ent.data.rng; oSX++) {
							for (int oSY = -ent.data.rng; oSY <= ent.data.rng; oSY++) {
								try {
									Point targPt = new Point(t.gridX + oSX, t.gridY + oSY);
									MapTile targTile = map.getMap()[targPt.x][targPt.y];
									UnitEntity targEnt = world.worldUnitMap[targPt.x][targPt.y];

									if (targEnt != null) {
										if (targEnt.owner != ent.owner) {
											map.add(new AttackSpace(ent, targTile));
										}
									}
								} catch (ArrayIndexOutOfBoundsException ex) {
									continue;
								}
							}
						}
					}
				}

				map.repaint();
				EventQueue.invokeLater(() -> {
					graphics.setColor(graphicsC);
					graphics.drawPolygon(t.base);

					graphics.dispose();
				});
			}
		}
	}

}
