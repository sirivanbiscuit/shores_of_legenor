package shoresoflegenor;

import shoresoflegenor.screens.TitleScreen;

public final class ShoresOfLegenor {

	/**
	 * Making an instance of this does little more than simply open a window with a
	 * POS desktop RTS game.
	 */
	public ShoresOfLegenor() {
		new TitleScreen();
	}

	public static void main(String[] args) {
		new ShoresOfLegenor();
	}

}