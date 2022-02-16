package up.TowerDefense.model.character;

public class PresetEnemy {
	
	private int coins_value;
	
	/**
	 * Correspond a l'agressivite de l'enemy (va t'il plutot attaquer les tours ou être plus agressif et attaquer les allies)
	 */
	private float agressiveness_degree;
	
	private float attackspeed;
	
	private float dammage;
	
	private boolean suicidal;
		
	/** 
	 * nombre de point de vie maximum du personnage
	 */
	private int maxHealth;
	
	/**
	 * nombre de point de vie actuel du personnage
	 */
	private int currentHealth;
	
	/**
	 * vitesse d'attaque du personnage
	 */
	private float speed;
	
	/**
	 * taille du personnage par rapport � une case
	 */
	private float size;
	
	/**
	 * resistance du personnage 
	 */
	private float resistance;
	
	/**
	 * Construit le preset d'un enemy
	 * 
	 * @param maxHealth vie maximale d'un enemy
	 * @param speed vitesse d'un enemy
	 * @param coins argent raporter par un enemy
	 * @param agressiveness_degree agressivite d'un enemy
	 * @param attackspeed vitesse d'attaque d'un enemy
	 * @param dammage degat d'un enemy
	 * @param suicid indique si l'enemy est suicidaire, si �a premiere attaque le tuera ou non
	 * @param size indique la taille de l'enemy par rapport a une case
	 * @param resistance indique la resistance de l'enemy contre une tour
	 */
	public PresetEnemy(int maxHealth, float speed, int coins, float agressiveness_degree, float attackspeed, float dammage, boolean suicid, float size, float resistance) {
		this.maxHealth = maxHealth;
		this.setSpeed(speed);
		this.setCoins_value(coins);
		this.attackspeed = attackspeed;
		this.dammage = dammage;
		this.suicidal = suicid;
		this.agressiveness_degree = agressiveness_degree;
		this.size = size;
		this.currentHealth = maxHealth;
		this.resistance = resistance;
	}
	
	/**
	 * Definit l'enemy Covid. 
	 * Cette enemy aura :
	 * 
	 * -100 points de vie maximale 
	 * -Une vitesse 1.25 fois sup�rieur a la normale
	 * -Il raportera 30 coins une fois tue
	 * -Une agressivite 5 fois superieur a la normale
	 * -Une vitesse d'attaque normale
	 * -Il fera 100 points de degats a chaque attaque
	 * -Il ne sera pas suicidaire
	 * -Sa taille corespondra a 0.5 fois une case
	 * -Sa resistance sera normale
	 * 
	 * @return Renvoie un objet PresetEnemy contenant toute ces informations afin de creer l'enemy
	 */
	public static PresetEnemy Covid() {
		return new PresetEnemy(100,1.25f,30,5.00f, 1.00f, 100.00f, false, 0.5f, 1.00f);
	}

	/**
	 * Definit l'enemy Bacterium. 
	 * Cette enemy aura :
	 * 
	 * -100 points de vie maximale 
	 * -Une vitesse 1.25 fois sup�rieur a la normale
	 * -Il raportera 15 coins une fois tue
	 * -Une agressivite normale
	 * -Une vitesse d'attaque normale
	 * -Il fera 10 points de degats a chaque attaque
	 * -Il ne sera pas suicidaire
	 * -Sa taille corespondra a 0.5 fois une case
	 * -Sa resistance sera normale
	 * 
	 * @return Renvoie un objet PresetEnemy contenant toute ces informations afin de creer l'enemy
	 */
	public static PresetEnemy Bacterium() {
		return new PresetEnemy(100, 1.25f, 15, 1.00f, 1.00f, 10.00f, false, 0.5f, 1.00f);
	}
	
	/**
	 * Definit l'enemy Virus. 
	 * Cette enemy aura :
	 * 
	 * -100 points de vie maximale 
	 * -Une vitesse normale
	 * -Il raportera 20 coins une fois tue
	 * -Une agressivite 1.5 fois superieur a la normale
	 * -Une vitesse d'attaque normale
	 * -Il fera 15 points de degats a chaque attaque
	 * -Il ne sera pas suicidaire
	 * -Sa taille corespondra a 0.5 fois une case
	 * -Sa resistance sera 1.25 fois superieur a la normale
	 * 
	 * @return Renvoie un objet PresetEnemy contenant toute ces informations afin de creer l'enemy
	 */
	public static PresetEnemy Virus() {
		return new PresetEnemy(100, 1.00f, 20, 1.50f, 1.00f, 15.00f, false, 0.5f, 1.25f);
	}

	/**
	 * Definit l'enemy Fungus. 
	 * Cette enemy aura :
	 * 
	 * -100 points de vie maximale 
	 * -Une vitesse 2 fois plus petite que la normale
	 * -Il raportera 5 coins une fois tue
	 * -Une agressivite 1.75 fois superieur a la normale
	 * -Une vitesse d'attaque normale
	 * -Il fera 5 points de degats a chaque attaque
	 * -Il ne sera pas suicidaire
	 * -Sa taille corespondra a 0.75 fois une case
	 * -Sa resistance sera 1.75 fois superieur a la normale
	 * 
	 * @return Renvoie un objet PresetEnemy contenant toute ces informations afin de creer l'enemy
	 */
	public static PresetEnemy Fungus() {
		return new PresetEnemy(100, 0.5f, 5, 1.75f, 1.00f, 5.00f, false, 0.75f, 1.75f);
	}
	
	/**
	 * Definit l'enemy Parasite. 
	 * Cette enemy aura :
	 * 
	 * -100 points de vie maximale 
	 * -Une vitesse 1.5 fois sup�rieur a la normale
	 * -Il raportera 40 coins une fois tue
	 * -Une agressivite 1.25 fois superieur a la normale
	 * -Une vitesse d'attaque normale
	 * -Il fera 20 points de degats a chaque attaque
	 * -Il ne sera pas suicidaire
	 * -Sa taille corespondra a 0.5 fois une case
	 * -Sa resistance sera 0.75 fois superieur a la normale
	 * 
	 * @return Renvoie un objet PresetEnemy contenant toute ces informations afin de creer l'enemy
	 */
	public static PresetEnemy Parasite() {
		return new PresetEnemy(100, 1.5f, 40, 1.25f, 1.00f, 20.00f, false, 0.5f, 0.75f);
	}
	
	public float getSize() {
		return this.size;
	}

	public float getAgressiv_Degree() {
		return this.agressiveness_degree;
	}

	public int getCoins() {
		return coins_value;
	}

	public void setCoins_value(int coins_value) {
		this.coins_value = coins_value;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getResistance() {
		return resistance;
	}

	public float getAttackspeed() {
		return attackspeed;
	}

	public float getDammage() {
		return dammage;
	}

	public boolean isSuicidal() {
		return suicidal;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}
	
}