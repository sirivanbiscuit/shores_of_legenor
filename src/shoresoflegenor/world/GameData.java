package shoresoflegenor.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import shoresoflegenor.entities.units.UnitEntity;
import shoresoflegenor.maps.WorldMap;

public final class GameData {

	public static final String SOUND_PATH = "res/sounds/";
	public static final String IMAGE_PATH = "res/images/";
	public static final String FONT_PATH = "res/fonts/";

	public static final String SAVE_PATH = "data/saves/";

	public static GameWorld gameWorld = new GameWorld();

	private static WorldMap activeMap;
	
	private GameData() {
	}

	public static void saveWorld() throws IOException, FileNotFoundException {
		// reset cooldowns on exit
		for (UnitEntity[] ents : gameWorld.worldUnitMap) {
			for (UnitEntity ent : ents) {
				if (ent != null) {
					ent.onCooldown = false;
				}
			}
		}
		gameWorld.syncEntityPools();

		FileOutputStream file = new FileOutputStream(new File(SAVE_PATH + "autosave.shores"));
		ObjectOutputStream stream = new ObjectOutputStream(file);
		stream.writeObject(gameWorld);
		stream.close();
		file.close();
	}

	public static void loadWorld() throws IOException, ClassNotFoundException {
		FileInputStream file = new FileInputStream(new File(SAVE_PATH + "autosave.shores"));
		ObjectInputStream stream = new ObjectInputStream(file);
		gameWorld = (GameWorld) stream.readObject();
		stream.close();
		file.close();
	}

	public static void setActiveWorld(WorldMap worldMap) {
		activeMap = worldMap;
	}

	public static void refreshActiveGraphics() {
		if (activeMap != null) {
			activeMap.repaint();
		}
	}
	
	public static void refreshActiveCanvas() {
		if (activeMap != null) {
			activeMap.removeAll();
		}
	}

}