package up.TowerDefense.model.object;

import up.TowerDefense.model.character.Enemy;
import up.TowerDefense.model.game.Game;
import up.TowerDefense.model.map.Tile;

import java.util.ArrayList;

import static up.TowerDefense.model.game.StaticFunctions.check_Ennemy;

public class Tower extends PlaceableObstacle{


    //mettre en abstract ?
    public enum Type{
        TOWERTEST,
        ANTI_CHAMPIS,
        LEUCOCYTE_T,
        ANTICORPS
        //type de tour
        //selon les types de tours size peut etre predefini
    }

    /**
     * Represente la porte d'attaque de la Tour.
     */
    protected double range;
    
    /**
     * Represente la puissance de la Tour (le nombre de degats quelle fait).
     */
    protected double power;
    
    /**
     * Represente le niveau de la Tour (En augmentant certain de ses attributs augmentent).
     */
    protected int level = 1;
    
    /**
     * Represente le coefficient d'augmentation de la portee et de la puissance lors d'une amelioration.
     */
    private double modifierIncrease = 1.5;
    
    /**
     * Represente le niveau maximal de la Tour.
     */
    protected final static int MAX_LEVEL = 5 ; //à déterminer
    
    /**
     * Represente le cout de l'amelioration.
     */
    protected double upgradeCost;
    
    /**
     * Represente le nombre de point de vie initiale de la Tour.
     */
    protected final static int STARTING_HEALTH = 5 ; //à voir
    
    /**
     * Represente le temps d'attente entre chaque attaque de la Tour.
     */
    protected double reloadTime; // temps de charge avant de pouvoir attaquer de nouveau
    
    /**
     * Represente le moment de la derniere attaque de la Tour (En fonction du temps de jeu au moments de l'attaque).
     */
    protected long lastAttackTime;
    
    /**
     * Represente le type de la Tour.
     */
    protected Type towerType;
    
    /**
     * Represente le prix la tour lors de l'achat d'une nouvelle Tour.
     */
    private double price;

    /**
     * Represente l'ennemi que la tour cible actuellement
     */
    private Enemy target;

    private ArrayList<Tile> attainableTiles = new ArrayList<Tile>();

    /**
     * Construit une Tour de taille "size" a la position determin�e par "x" et "y".
     * 
     * @param x Position horizontale de la Tour sur la carte
     * @param y Position verticale de la Tour sur la carte
     * @param size Taille de Tour par rapport a une case
     * @param buyingCost Prix de la Tour
     * @param range Porte d'attaque de la Tour 
     * @param power Nombre de degat de la Tour
     * @param upgradeCost Prix de l'amelioration de la Tour
     * @param reloadTime Temps d'attaque entre chaque attaque
     * @param lastAttackTime Temps lors de la derniere attaque de la Tour
     * @param twType Type de la Tour
     * @param image Image de la Tour qui s'affichera sur la carte
     */
    public Tower(double x, double y, int size, double buyingCost, double range, double power, int upgradeCost,
                 double reloadTime, long lastAttackTime, Type twType, String image) {
        super(x, y, size, STARTING_HEALTH, STARTING_HEALTH, ObsType.TOWER, buyingCost,image);
        this.range=range;
        this.power=power;
        this.upgradeCost=upgradeCost;
        this.reloadTime=reloadTime;
        this.lastAttackTime = lastAttackTime;
        this.towerType=twType;
        Game.getBoard().addToListTowers(this);
        setAttainableTiles();
    }

    /**
     * onstruit une Tour a la position "position" a partir des informations d'un PresetTower
     * 
     * @param presetTower Contient toute les informations concernant la Tour notament son prix et les degats qu'elle fait.
     * @param position Definit la position de la Tour.
     */
    public Tower(PresetTower presetTower, Position position) {
    	super(position.x, position.y, presetTower.getSize(), PresetTower.STARTING_HEALTH, PresetTower.STARTING_HEALTH, ObsType.TOWER, presetTower.price, presetTower.imgName);
    	this.range = presetTower.getRange();
    	this.power = presetTower.getPower();
    	this.upgradeCost = presetTower.getUpgradeCost();
    	this.reloadTime = presetTower.getReloadTime();
    	this.lastAttackTime = presetTower.getLastAttackTime();
    	this.towerType = presetTower.getTowerType();
        Game.getBoard().addToListTowers(this);
        setAttainableTiles();
    }
    
    /**
     * Amelioration de la Tour.
     * 
     * @return true if the tower upgraded successfully
     */
    public boolean upgrade(){
        if (level < MAX_LEVEL){
            //augmentation de la taille ?
            level++;
            power*=getModifier();
            range*=getModifier();
            upgradeCost*=getModifier();
            setRefundValue(upgradeCost);
            Game.setCredits(-1*upgradeCost);
            return true;
        }
        return false;
    }
    
    /**
     * Determine la capacite de la tour a lancer une attaque
     * apres un temps de recouvrement (qui depend de chaque tour)
     * 
     * @return renvoie True si la Tour peux attaqu�e False sinon 
     */
    public boolean canAttack(){
        return (System.currentTimeMillis()-lastAttackTime>=reloadTime);
    }

    public void setLastAttackTime() {
        this.lastAttackTime = System.currentTimeMillis();
    }
    public double getModifier(){
        return (modifierIncrease*(level -1));
    }

    /**
     * @return la portee de la tour
     */
    public double getRange() {
        return range;
    }

    /**
     * @return la puissance de la tour
     */
    public double getPower() {
        return power;
    }

    /**
     * @return le niveau de la tour
     */
    public int getLevel() {
        return level;
    }

    public static int getMaxLevel() {
        return MAX_LEVEL;
    }

    public double getUpgradeCost() {
        return upgradeCost;
    }

    public static int getStartingHealth() {
        return STARTING_HEALTH;
    }

    public Type getTowerType() {
        return towerType;
    }

    public void setAttainableTiles(){
        for (int i = -(int)range ; i != 0 && i < range+1  ; i++) {
            for (int j = -(int)range; j != 0 && j < range + 1; j++) {
                attainableTiles.add(Game.getBoard().getTile((int)position.y + j, (int)position.x + i));
                System.out.println(Game.getBoard().getTile((int)position.y + j, (int)position.x + i).getPos().x+" "
                        +Game.getBoard().getTile((int)position.y + j, (int)position.x + i).getPos().y);
            }
        }
    }

    public void launchAttack(){
        for (Tile attainableTile : attainableTiles){
            if (check_Ennemy(attainableTile)){
                System.out.println("tower found on : " + attainableTile.getPos().x + "-" + attainableTile.getPos().y);
                Position ennemyPos = attainableTile.getPos();
                break;
            }
        }
    }
}
