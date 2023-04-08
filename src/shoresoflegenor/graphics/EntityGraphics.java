package shoresoflegenor.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JLabel;

import shoresoflegenor.entities.GameEntity;
import shoresoflegenor.entities.units.UnitEntity;
import shoresoflegenor.graphics.UnitAnimCache.UnitAnim;
import shoresoflegenor.maps.WorldMap;
import shoresoflegenor.tiles.ChunkTile;
import shoresoflegenor.tiles.PolyTile;
import shoresoflegenor.util.ScreenUtil;

public class EntityGraphics {

	private static final Color SHADOW_FRIENDLY = new Color(100, 100, 100, 90);
	private static final Color SHADOW_ENEMY = new Color(200, 30, 30, 90);
	private static final Color COOLDOWN_OVERLAY = new Color(200, 200, 200, 150);

	private static final Color ORG_POSITIVE = new Color(0, 200, 0);
	private static final Color ORG_NEGATIVE = new Color(200, 0, 0);

	private WorldMap entityWorld;

	public EntityGraphics(WorldMap entityWorld) {
		this.entityWorld = entityWorld;
	}

	public void renderEntityPool(Graphics g, ArrayList<? extends GameEntity> entityMap) {
		for (GameEntity entity : entityMap) {
			if (ChunkTile.getChunkFromMapTile(entityWorld.mapGraphics.chunkGrid, entity.xPos, entity.yPos).isRendered) {
				Rectangle rect = getEntityBoundingBox(entity);
				PolyTile overlay = entityWorld.getMap()[entity.xPos][entity.yPos].base;
				Rectangle ovBounds = overlay.getBounds();
				
				g.setColor(entity.owner.botPlayer == null ? SHADOW_FRIENDLY : SHADOW_ENEMY);
				g.fillPolygon(overlay);

				if (entity instanceof UnitEntity) {
					if (((UnitEntity) entity).onCooldown) {
						g.setColor(COOLDOWN_OVERLAY);
						g.fillPolygon(overlay);
					}
				}
				g.drawImage(ScreenUtil.getImage(entity.texPath), rect.x, rect.y, rect.width, rect.height, null);
			}
		}
	}

	public void animateEntity(UnitEntity unit, UnitAnim anim) {
		JLabel animFrame = new JLabel();
		Rectangle rect = getEntityBoundingBox(unit);

		animFrame.setBounds(rect);
		animFrame.setIcon(ScreenUtil.getScaledImageIcon(anim.asset, rect.width, rect.height));
		entityWorld.add(animFrame);

		GraphicsWorker.performBackgroundRun(() -> {
			try {
				Thread.currentThread();
				Thread.sleep(anim.animTime);
				entityWorld.remove(animFrame);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	private Rectangle getEntityBoundingBox(GameEntity entity) {
		return entityWorld.getMap()[entity.xPos][entity.yPos].base.getBounds();
	}
}
