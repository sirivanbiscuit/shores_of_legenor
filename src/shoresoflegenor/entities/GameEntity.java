package shoresoflegenor.entities;

import java.io.Serializable;

import shoresoflegenor.player.GamePlayer;

public abstract class GameEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public GamePlayer owner;
	public String name;
	public String texPath;
	public int xPos;
	public int yPos;

	public GameEntity(GamePlayer owner, String name, String texPath, int xPos, int yPos) {
		this.owner = owner;
		this.name = name;
		this.texPath = texPath;
		this.xPos = xPos;
		this.yPos = yPos;
	}
}
