package ivandev.shoresoflegenor.player;

import java.io.Serializable;

public class BotPlayer implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum BotDifficulty {
		POTATO, EASY, HARD, INFINITY
	}

	public BotDifficulty difficulty;

	public BotPlayer(BotDifficulty difficulty) {
		this.difficulty = difficulty;
	}
}
