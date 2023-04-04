package shoresoflegenor.graphics;

import java.io.Serializable;

import javax.swing.ImageIcon;

import shoresoflegenor.util.ScreenUtil;

public class UnitAnimCache implements Serializable {

	private static final long serialVersionUID = 1L;

	public String pathIdleW, pathIdleN, pathIdleE, pathIdleS;
	public UnitAnim animAtkW, animAtkN, animAtkE, animAtkS;

	public UnitAnimCache(String pathIdleW, String pathIdleN, String pathIdleE, String pathIdleS, String pathAtkW,
			String pathAtkN, String pathAtkE, String pathAtkS) {
		this.pathIdleW = pathAtkW;
		this.pathIdleN = pathAtkN;
		this.pathIdleE = pathAtkE;
		this.pathIdleS = pathAtkS;

		int atkTime = 1000;
		animAtkW = new UnitAnim(ScreenUtil.getImageIcon(pathAtkW), atkTime);
		animAtkN = new UnitAnim(ScreenUtil.getImageIcon(pathAtkN), atkTime);
		animAtkE = new UnitAnim(ScreenUtil.getImageIcon(pathAtkE), atkTime);
		animAtkS = new UnitAnim(ScreenUtil.getImageIcon(pathAtkS), atkTime);
	}

	public String defaultImagePath() {
		return pathIdleE;
	}

	public static class UnitAnim implements Serializable {

		private static final long serialVersionUID = 1L;

		public ImageIcon asset;
		public int animTime;

		public UnitAnim(ImageIcon asset, int animTime) {
			this.asset = asset;
			this.animTime = animTime;
		}
	}

}
