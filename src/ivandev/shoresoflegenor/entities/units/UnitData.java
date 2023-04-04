package ivandev.shoresoflegenor.entities.units;

import java.io.Serializable;

public class UnitData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public int htpMax;
	public int dmg;
	public int spd;
	public int rng;
	
	public int htpRem;
	
	public float org;
	
	public UnitData(int htpMax, int dmg, int spd, int rng) {
		this.htpMax = htpMax;
		this.dmg=dmg;
		this.spd=spd;
		this.rng=rng;
		
		htpRem = htpMax;
		org = 1f;
	}

}
