package shoresoflegenor.util;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import shoresoflegenor.world.GameData;

public final class SoundUtil {
	
	private SoundUtil() {
	}

	private static void playSound(String name) {
		try {
			Clip clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(GameData.SOUND_PATH + name));
			clip.open(inputStream);
			clip.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void playClick() {
		playSound("button_click.wav");
	}
	
	public static void playHover() {
		playSound("button_hover.wav");
	}
}
