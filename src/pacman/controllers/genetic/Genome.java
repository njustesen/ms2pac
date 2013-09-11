package pacman.controllers.genetic;

public class Genome {

	// State transitions
	/*
	private int fleeingDistance;
	private int safeDistance;
	*/
	
	// Path value in EAT state
	private int pillValue;
	private double pillMultiplier;
	private int powerPillValue;
	private int ghostValue;
	private int deathValue;
	private int winValue;
	private int stepValue;
	private int dangerDistance;
	private int killDistance;
	
	// Path value in FLEE state
	/*
	private int distanceToGhostsValue;
	private int pathDistanceValue;
	*/
	
	public Genome(
			int pillValue,
			double pillMultiplier, 
			int powerPillValue, 
			int ghostValue, 
			int deathValue, 
			int winValue, 
			int stepValue,
			int dangerDistance,
			int killDistance) {
		
		super();
		this.pillValue = pillValue;
		this.pillMultiplier = pillMultiplier;
		this.powerPillValue = powerPillValue;
		this.ghostValue = ghostValue;
		this.deathValue = deathValue;
		this.winValue = winValue;
		this.stepValue = stepValue;
		this.dangerDistance = dangerDistance;
		this.killDistance = killDistance;
	}
	
	public int getDangerDistance() {
		return dangerDistance;
	}

	public void setDangerDistance(int dangerDistance) {
		this.dangerDistance = dangerDistance;
	}

	public int getPillValue() {
		return pillValue;
	}

	public void setPillValue(int pillValue) {
		this.pillValue = pillValue;
	}

	public double getPillMultiplier() {
		return pillMultiplier;
	}

	public void setPillMultiplier(double pillMultiplier) {
		this.pillMultiplier = pillMultiplier;
	}

	public int getPowerPillValue() {
		return powerPillValue;
	}

	public void setPowerPillValue(int powerPillValue) {
		this.powerPillValue = powerPillValue;
	}

	public int getGhostValue() {
		return ghostValue;
	}

	public void setGhostValue(int ghostValue) {
		this.ghostValue = ghostValue;
	}

	public int getDeathValue() {
		return deathValue;
	}

	public void setDeathValue(int deathValue) {
		this.deathValue = deathValue;
	}

	public int getWinValue() {
		return winValue;
	}

	public void setWinValue(int winValue) {
		this.winValue = winValue;
	}

	public int getStepValue() {
		return stepValue;
	}

	public void setStepValue(int stepValue) {
		this.stepValue = stepValue;
	}

	public int getKillDistance() {
		return killDistance;
	}

	public void setKillDistance(int killDistance) {
		this.killDistance = killDistance;
	}

	public static Genome randomGenome() {
		
		int pillValue = (int) (-1000 + Math.random() * 2000);
		double pillMultiplier = -10.0 + Math.random() * 10.0;
		int powerPillValue = (int) (-1000 + Math.random() * 2000);
		int ghostValue = (int) (-10000 + Math.random() * 20000);
		int deathValue = (int) (-10000 + Math.random() * 20000);
		int winValue = (int) (-10000 + Math.random() * 20000);
		int stepValue = (int) (-1000 + Math.random() * 2000);
		int dangerDistance = (int) (Math.random() * 100);
		int killDistance = (int) (Math.random() * 100);
		
		return new Genome(
				pillValue, 
				pillMultiplier,
				powerPillValue,
				ghostValue,
				deathValue,
				winValue,
				stepValue,
				dangerDistance,
				killDistance);
	}

	public static Genome breedChild(Genome parentA, Genome parentB) {
		int pillValue = getRandomParent(parentA, parentB).getPillValue();
		double pillMultiplier = getRandomParent(parentA, parentB).getPillMultiplier();
		int powerPillValue = getRandomParent(parentA, parentB).getPowerPillValue();
		int ghostValue = getRandomParent(parentA, parentB).getGhostValue();
		int deathValue = getRandomParent(parentA, parentB).getDeathValue();
		int winValue = getRandomParent(parentA, parentB).getWinValue();
		int stepValue = getRandomParent(parentA, parentB).getStepValue();
		int dangerDistance = getRandomParent(parentA, parentB).getDangerDistance();
		int killDistance = getRandomParent(parentA, parentB).getKillDistance();
		
		return new Genome(
				pillValue, 
				pillMultiplier,
				powerPillValue,
				ghostValue,
				deathValue,
				winValue,
				stepValue,
				dangerDistance,
				killDistance);
		
	}

	private static Genome getRandomParent(Genome parentA, Genome parentB) {
		if (Math.random() >= 0.5)
			return parentA;
		return parentB;
	}

	public void mutate() {
		
		Genome randomGenome = Genome.randomGenome();
		int x = (int) (Math.random() * 9);
		switch(x){
		case 0 : pillValue = randomGenome.getPillValue(); break;
		case 1 : pillMultiplier = randomGenome.getPillMultiplier(); break;
		case 2 : powerPillValue = randomGenome.getPowerPillValue(); break;
		case 3 : ghostValue = randomGenome.getGhostValue(); break;
		case 4 : deathValue = randomGenome.getDeathValue(); break;
		case 5 : winValue = randomGenome.getWinValue(); break;
		case 6 : stepValue = randomGenome.getStepValue(); break;
		case 7 : dangerDistance = randomGenome.getDangerDistance(); break;
		case 8 : killDistance = randomGenome.getKillDistance(); break;
		}
		
	}
	
	
	
}
