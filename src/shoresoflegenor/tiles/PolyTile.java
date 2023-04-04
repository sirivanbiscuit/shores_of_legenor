package shoresoflegenor.tiles;

import java.awt.Point;
import java.awt.Polygon;

@SuppressWarnings("serial")
public class PolyTile extends Polygon {

	public Point corLeft, corLeftUp, corLeftDown;
	public Point corTop;
	public Point corRight, corRightUp, corRightDown;
	public Point corBottom;

	public PolyTile(Point left, Point top, Point right, Point bottom) {
		super(new int[] { left.x, top.x, right.x, bottom.x }, new int[] { left.y, top.y, right.y, bottom.y }, 4);
		this.corLeft = left;
		this.corTop = top;
		this.corRight = right;
		this.corBottom = bottom;
	}

	public PolyTile(Point leftUp, Point top, Point rightUp, Point rightDown, Point bottom, Point leftDown) {
		super(new int[] { leftUp.x, top.x, rightUp.x, rightDown.x, bottom.x, leftDown.x },
				new int[] { leftUp.y, top.y, rightUp.y, rightDown.y, bottom.y, leftDown.y }, 6);
		this.corLeftUp = leftUp;
		this.corTop = top;
		this.corRightUp = rightUp;
		this.corRightDown = rightDown;
		this.corBottom = bottom;
		this.corLeftDown = leftDown;
	}

	public PolyTile getScalar(float factor) {
		int orgX = corLeft.x;
		int orgY = corLeft.y;
		float expans = factor - 1;

		return new PolyTile(corLeft,
				MapTile.newPt(corTop.x += expans * (corTop.x - orgX), corTop.y += expans * (corTop.y - orgY)),
				MapTile.newPt(corRight.x += expans * (corRight.x - orgX), corRight.y), MapTile.newPt(
						corBottom.x += expans * (corBottom.x - orgX), corBottom.y += expans * (corBottom.y - orgY)));
	}

	public PolyTile getTranslation(int tX, int tY) {
		return new PolyTile(MapTile.newPt(corLeft.x + tX, corLeft.y + tY), MapTile.newPt(corTop.x + tX, corTop.y + tY),
				MapTile.newPt(corRight.x + tX, corRight.y + tY), MapTile.newPt(corBottom.x + tX, corBottom.y + tY));
	}
	
	public PolyTile getClone() {
		return new PolyTile(MapTile.newPt(corLeft.x, corLeft.y ), MapTile.newPt(corTop.x, corTop.y ),
				MapTile.newPt(corRight.x, corRight.y ), MapTile.newPt(corBottom.x, corBottom.y ));
	}
}
