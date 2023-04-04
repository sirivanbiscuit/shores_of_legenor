package ivandev.shoresoflegenor.generators;

public final class NameStringGen {

	public static String rndPlayerName() {
		String[] names = { "" };

		return names[(int) (Math.random() * names.length)];
	}

}
