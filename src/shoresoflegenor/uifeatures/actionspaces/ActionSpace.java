package shoresoflegenor.uifeatures.actionspaces;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import shoresoflegenor.entities.units.UnitEntity;
import shoresoflegenor.tiles.MapTile;
import shoresoflegenor.tiles.PolyTile;

@SuppressWarnings("serial")
public abstract class ActionSpace extends JComponent {

	public static final Color INFO = Color.YELLOW;
	public static final Color ACTION = Color.CYAN;
	public static final Color ARGO = Color.RED;
	public static final Color UNDEFINED = Color.LIGHT_GRAY;

	public enum SpaceAction {
		ATTACK, MOVE
	}

	private UnitEntity sourceEntity;
	private MapTile orgTile;
	private SpaceAction action;
	private Runnable actionRun;

	private PolyTile poly;

	public ActionSpace(UnitEntity sourceEntity, MapTile orgTile, SpaceAction action, Runnable actionRun) {
		this.sourceEntity = sourceEntity;
		this.orgTile = orgTile;
		this.action = action;
		this.actionRun = actionRun;

		initButton();
	}

	private void initButton() {
		poly = orgTile.base.getClone();
		poly = poly.getScalar(0.5f);
		poly = poly.getTranslation(poly.getBounds().width / 2, 0);

		setBounds(poly.corLeft.x, poly.corTop.y, poly.getBounds().width, poly.getBounds().height);
		repaint();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				actionRun.run();
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		PolyTile p = poly.getTranslation(-poly.corLeft.x, -poly.corTop.y);
		Color c = getSpaceColor();
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 50));
		g.fillPolygon(p);
		g.setColor(c);
		g.drawPolygon(p);
	}

	private Color getSpaceColor() {
		switch (action) {
		case ATTACK: {
			return ARGO;
		}
		case MOVE: {
			return ACTION;
		}
		}
		return null;
	}

	public UnitEntity getSource() {
		return sourceEntity;
	}

	public MapTile getOrigin() {
		return orgTile;
	}
	
}
