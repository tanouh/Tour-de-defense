package up.TowerDefense.model.character;

import up.TowerDefense.model.game.Game;
import up.TowerDefense.model.map.Path;
import up.TowerDefense.model.map.Pathfinding;
import up.TowerDefense.model.object.DestructibleObstacle;
import up.TowerDefense.model.object.EnemyProjectile;
import up.TowerDefense.model.object.PlaceableObstacle;
import up.TowerDefense.model.object.Position;
import up.TowerDefense.view.componentHandler.MapGenerator;

import static up.TowerDefense.model.character.Enemy.Type.FUNGUS;
import static up.TowerDefense.model.game.StaticFunctions.findTower;

public class Enemy extends Personnage{

	//type d'ennemi
	public enum Type{
		COVID,
        BACTERIUM,
        VIRUS,
        FUNGUS,
        PARASITE
    }

	/**
	 * Temps durant lequel l'image de l'ennemi sera remplacée par une image temporaire qui signifierait
	 * qu'il a été touché par une attaque
	 */
	public final long HITDELAY = 250;

	/**
	 * Correspond au nombre de coins rapportes une fois l'enemy mort.
	 */
	private int reward;
	
	/**
	 * Correspond a l'agressivite de l'enemy (va t'il plutot attaquer les tours ou etre plus agressif et attaquer les allies)
	 */
	private float agressiveness_degree;
	
	/**
	 * Correspond a la vitesse d'attaque de l'ennemi
	 */
	private float attackspeed;
	
	/**
	 * Correspond aux degats de l'ennemi
	 */
	private float damage;

	/**
	 * Désigne la portée d'attaque de l'ennemi
	 */
	private int range;
	/**
	 * Temps de repos avant le prochain attaque
	 */
	private long reloadTime;
	/**
	 * Temps du dernier attaque
	 */
	private long timeSinceLastAttack;
	/**
	 * Determine le type d'obstacle ciblee par l'ennemi
	 */
	private DestructibleObstacle.ObsType target;

	/**
	 * Détermine le type de l'ennemi
	 */
	private Enemy.Type ennemyType;

	/**
	 * Chemin suivi par l'enemi
	 */
	private Path path;

	/**
	 * Durée depuis laquelle l'enemi vit (ie qu'il est sur le plateau)
	 */
	private long lifeTime;

	/**
	 * Le temps durant lequel l'enemi a effectué un mouvement
	 */
	private long travelTime;

	/**
	 * Déclence HITDELAY
	 */
	protected long hitStart ;

	/**
	 * Signale si l'ennemi a reçu un coup
	 */
	protected boolean tookHit = false;


	/**
	 * Position de départ
	 */
	protected Position startingPos;

	private boolean alive;
	/**
	 * Pour distinguer si l'ennemi meurt suite à une attaque ou à l'atteinte de la zone cible
	 */
	private boolean killed = false;
	/**
	 * Lorsque l'ennemi gèle
	 */
	private boolean frozen;
	private long freezeStartTime;
	private long freezeDuration;
	private long totalTimeDelay = 0;

	/**
	 * Signale quand le chemin de l'ennemi est modifié
	 */
	private boolean gotNewPath;

	/**
	 * Pour signifier que l'ennemi est passé une fois sur une case Booster
	 */
	private boolean speedUp = false;

	private boolean isCurrentlyUpdatingPath = false;

	/**
	 * Construit un enemy a la position "position" a partir des informations d'un PresetEnemy
	 * 
	 * @param presetEnemy Contient toute les informations concernant l'enemy notament son degre d'agressivite ou sa vitesse.
	 * @param position Definit la position de l'enemy
	 */
	public Enemy(PresetEnemy presetEnemy, Position position) {
		super(position, presetEnemy.getSize(), presetEnemy.getResistance(), presetEnemy.getMaxHealth(),
				presetEnemy.getSpeed(), presetEnemy.imgName, presetEnemy.reloadImgName);
		this.reward = presetEnemy.getCoins();
		this.agressiveness_degree = presetEnemy.getAgressiv_Degree();
		this.attackspeed = presetEnemy.getAgressiv_Degree();
		this.damage = presetEnemy.getDamage();
		this.target = presetEnemy.getTarget();
		this.range= presetEnemy.getRange();
		this.startingPos = position;
		this.path = Pathfinding.FindPath(position, Game.getBoard().getNearestTargetPosition(position));
		Game.getBoard().addToListEnemy(this);

		this.ennemyType = presetEnemy.EnemyType;
		this.gotNewPath = false;
		reloadTime = presetEnemy.getReloadTime();



	}
	
	/**
	 * Fonction de mise a jour de la position de l'ennemi
	 * a chaque fois que l'interface graphique se met a jour cette fonction est appelee
	 */

	public void update_position(){
		if(frozen && System.currentTimeMillis() - freezeStartTime > freezeDuration){
			unfreeze();
			this.addToTotalTimeDelay(System.currentTimeMillis() - freezeStartTime);
		}else if (frozen) return;
		if (speedUp && !Game.getBoard().getTile(this.position).isBooster()){
			speedDown();
		}

		/**
		 * Quand l'ennemi passe aux environs d'une tour il ne s'arrête pas mais lance des projectiles tout en continuant
		 * Quant à lui, s'il accuse une attaque sa vitesse diminue
		 */

		if (gotNewPath){
			rebootEnemyTime();
		}
		Game.getBoard().getTile(this.position).setEnemy(null);
		travelTime = System.currentTimeMillis() - lifeTime - totalTimeDelay;

		this.position = path.GetPos(travelTime, this.speed*Game.getGameSpeed());
		Game.getBoard().getTile(this.position).setEnemy(this);

		if(Game.getBoard().getTile(this.position).isBooster()){
			if(this.ennemyType != FUNGUS)
				this.speedUp();
		}
		if (Game.getBoard().getTile((int)Math.round(position.x),(int)Math.round(position.y)).isTarget()){
			Game.setLives(-1);
			this.alive = false;
		}
	}

	/**
	 * Lorsque l'ennemi passe sur une case booster
	 */
	private void speedUp() {
		if(!speedUp){
			this.update_paths();
			this.setGotNewPath(true);
			this.speed *= 2;
			speedUp = true;
		}
	}

	private void speedDown() {
		if (speedUp) {
			this.update_paths();
			this.setGotNewPath(true);
			this.speed /= 2;
			speedUp = false;
		}
	}

	/**
	 * Mise à jour de la trajectoire à chaque fois qu'un obstacle est placé
	 */
	public void update_paths(){
		if (isCurrentlyUpdatingPath) return;
		isCurrentlyUpdatingPath = true;
		this.path = Pathfinding.FindPath(this.position, Game.getBoard().getNearestTargetPosition(position));
		isCurrentlyUpdatingPath = false;
	}

	/**
	 * Cible la tour
	 */
	public void identifyTarget(){
		if(this.ennemyType == FUNGUS ){
			fungusAttack();
		}else{
		Position towerPos = findTower(this.position, this.range, Game.getBoard());
			if(towerPos != null){
				launchAttack((PlaceableObstacle) Game.getBoard().getOccupier(towerPos));
			}
		}
	}

	/**
	 * Envoie les projectiles
	 * @param target la tour cible
	 */
	private void launchAttack(PlaceableObstacle target) {
		EnemyProjectile projectile = new EnemyProjectile(this.position, target.position, this.damage, Game.getLevel(), target);
		MapGenerator.enemyProjectilesList.add(projectile);
		timeSinceLastAttack = System.currentTimeMillis();
	}

	/**
	 * L'expansion des cases booster par les champignons
	 */
	private void fungusAttack(){
		for(int i = -range; i < range ; i++){
			for(int j = -range ; j < range ; j++){
				if(Game.getBoard().getTile((int)(position.x + i) , (int)(position.y + j)) != null ){
					MapGenerator.boosterCase.add( Game.getBoard().getTile((int)(position.x + i) , (int)(position.y + j)) );
					Game.getBoard().getTile((int)(position.x + i) , (int)(position.y + j)).setBooster(true);
				}
			}
		}
		timeSinceLastAttack = System.currentTimeMillis();
	}

	/**
	 * Fonction d'upgrade selon le niveau du jeu
	 * @param level
	 */
	public void upgrade(int level) {
		if(level > 1){
			reward *= level;
			agressiveness_degree += (level/2);
			attackspeed += level;
			damage += (damage*level/2);
			if(this.ennemyType != FUNGUS)
				range+= level;
		}
	}


	/**
	 * Signale la mort de l'ennemi
	 */
	public void die(){
		this.alive = false;
		Game.getBoard().getTile(this.position).setEnemy(null);
		if (killed) Game.setCredits(this.reward);
	}


	/**
	 * Réinitialise tous les chronomètres reliés à un ennemi
	 */
	public void rebootEnemyTime() {
		this.totalTimeDelay = 0;
		this.lifeTime = System.currentTimeMillis();
		this.gotNewPath = false;
	}

	/**
	 * Lorsque l'ennemi gèle suite à l'attaque d'une tour de type Anticorps
	 */
	public void freeze() {
		this.frozen = true;
		freezeStartTime = System.currentTimeMillis();
	}
	public void unfreeze(){
		this.frozen = false;
	}

	public void setGotNewPath(boolean gotNew){
		this.gotNewPath = gotNew;
	}

	public void live(){
		alive = true;
		lifeTime = System.currentTimeMillis();
		timeSinceLastAttack = System.currentTimeMillis();
		travelTime = System.currentTimeMillis();
	}

	public boolean isAlive(){
		return alive;
	}

	public void takeDamage(double power){
		currentHealth = (int) Math.round(currentHealth - power/resistance);
		this.hitStart = System.currentTimeMillis();
		tookHit = true;
		if(currentHealth <= 0){
			alive = false;
			killed = true;

		}
	}

	@Override
	public boolean tookHit(){
		if (tookHit){
			if (System.currentTimeMillis() - hitStart > HITDELAY/Game.getGameSpeed()) {
				tookHit = false;
				hitStart = 2*System.currentTimeMillis();
			}
		}
		return tookHit;
	}

	public long getReloadTime() {
		return reloadTime;
	}

	public long getTimeSinceLastAttack() {
		return timeSinceLastAttack;
	}

	public long getTravelTime() {
		return travelTime;
	}

	public void setFreezeDuration(long i) {
		this.freezeDuration = i;
	}

	public void addToTotalTimeDelay(long timeDelay){
		totalTimeDelay += timeDelay;
	}

	public float getAgressiveness_degree() {
		return agressiveness_degree;
	}

	public Path getPath(){ return path;}


	@Override
	public String toString() {
		return "Enemy{" +
				"reward=" + reward +
				", agressiveness_degree=" + agressiveness_degree +
				", attackspeed=" + attackspeed +
				", damage=" + damage +
				", range=" + range +
				", speed=" + speed*Game.getGameSpeed() +
				", velocity=" + velocity +
				", resistance=" + resistance +
				'}';
	}
}
