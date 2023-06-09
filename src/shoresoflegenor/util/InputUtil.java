package shoresoflegenor.util;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class InputUtil {
	
	private InputUtil() {
	}

	public static class MouseButtonListener extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent e) {
			SoundUtil.playHover();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			SoundUtil.playClick();
		}
	}
}
