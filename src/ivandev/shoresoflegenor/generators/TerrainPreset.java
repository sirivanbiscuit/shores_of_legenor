package ivandev.shoresoflegenor.generators;

import ivandev.shoresoflegenor.tiles.AbstractMapTile;

public class TerrainPreset {

	public enum MapPreset {
		CONTINENTS, INLAND_SEAS, DRY_DESERT
	}

	// {forest, mountain, river}
	private static final float[] BIOME_WEIGHTS = { 0.1f, 0.3f, 0.4f };
	private static float[] BIOME_FLOWS = { 0.4f, 0.8f, 0.1f };
	private static int[] BIOME_DEPTHS = { 3, 1, 1 };

	// {land, water}
	private static float[] SMOOTHING_POWS = { 0.9f, 0.6f };

	private static final float GEN_POW = 0.1f;
	private static final int SMOOTHING_PHASES = 3;

	private MapPreset preset;
	private int subs;

	private int continents;
	private int forests;
	private int mountains;
	private int mountainLength;
	private int rivers;
	private int dryPhases;
	private int erodePhases;

	public TerrainPreset(MapPreset preset, int subs) {
		this.preset = preset;
		this.subs = subs;
		initPresets();
	}

	private void initPresets() {
		int acres = subs * subs / 100;

		switch (preset) {
		case CONTINENTS: {
			continents = (int) Math.pow(subs / 25, 1.5);
			forests = acres / 8 + 1;
			mountains = acres / 100 + 1;
			mountainLength = subs / 2;
			rivers = subs / 6 + 1;
			dryPhases = 75 + (subs / 8);
			erodePhases = dryPhases / 5;
			break;
		}
		case INLAND_SEAS: {
			continents = 2 * (int) Math.pow(subs / 25, 1.5);
			forests = acres / 5 + 1;
			mountains = acres / 100 + 1;
			mountainLength = subs / 4;
			rivers = subs / 8 + 1;
			dryPhases = 75 + (subs / 8);
			erodePhases = dryPhases / 5;
			break;
		}
		case DRY_DESERT: {
			continents = 4 * (int) Math.pow(subs / 25, 1.5);
			forests = acres / 5+1;
			mountains = acres / 50 + 1;
			mountainLength = subs / 2;
			rivers = subs / 50;
			dryPhases = 85 + (subs / 8);
			erodePhases = dryPhases / 8;
			break;
		}
		}
	}

	public Object[] getPresetValues() {
		return new Object[] { continents, dryPhases, erodePhases, GEN_POW, forests, mountains, rivers, BIOME_DEPTHS[0],
				subs, BIOME_WEIGHTS[0], BIOME_FLOWS[0], BIOME_DEPTHS[1], mountainLength, BIOME_WEIGHTS[1],
				BIOME_FLOWS[1], BIOME_DEPTHS[2], subs, BIOME_WEIGHTS[2], BIOME_FLOWS[2], SMOOTHING_PHASES,
				SMOOTHING_POWS[0], SMOOTHING_POWS[1] };
	}

	public AbstractMapTile[][] buildPreset() {
		return AbstractMapTile.genQuickMap( // QUICK MAP
				subs, continents, // continents
				dryPhases, GEN_POW, // land drying
				erodePhases, GEN_POW, // land erosion
				forests, BIOME_DEPTHS[0], subs, BIOME_WEIGHTS[0], BIOME_FLOWS[0], // forests
				mountains, BIOME_DEPTHS[1], mountainLength, BIOME_WEIGHTS[1], BIOME_FLOWS[1], // mountains
				rivers, BIOME_DEPTHS[2], subs, BIOME_WEIGHTS[2], BIOME_FLOWS[2], // rivers
				SMOOTHING_PHASES, SMOOTHING_POWS[0], SMOOTHING_POWS[1] // cleaning
		);
	}
}
