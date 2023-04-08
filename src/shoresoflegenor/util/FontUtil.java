package shoresoflegenor.util;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;

import shoresoflegenor.world.GameData;

public final class FontUtil {
	
	private FontUtil() {
	}

	public static Font getFont(String path, float size, int style) {
		try {
			FileInputStream file = new FileInputStream(new File(GameData.FONT_PATH + path));
			return Font.createFont(style, file).deriveFont(size);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
}
