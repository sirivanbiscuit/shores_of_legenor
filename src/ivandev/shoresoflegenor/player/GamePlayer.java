package ivandev.shoresoflegenor.player;

import java.io.Serializable;

public class GamePlayer implements Serializable {

	private static final long serialVersionUID = 1L;

	public String name;
	public BotPlayer botPlayer; // set to null for human

	public GamePlayer(String name, BotPlayer botPlayer) {
		this.name = name;
		this.botPlayer = botPlayer;
	}

}
