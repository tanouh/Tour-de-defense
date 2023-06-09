package up.TowerDefense.model.object;

import up.TowerDefense.model.character.Enemy;
import up.TowerDefense.model.game.Game;
import up.TowerDefense.model.map.Tile;
import up.TowerDefense.view.componentHandler.MapGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import static up.TowerDefense.model.game.StaticFunctions.check_Ennemy;
import static up.TowerDefense.model.game.StaticFunctions.check_Fungus;

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
     * Temps durant lequel l'image de la tour sera remplacée par une image temporaire qui signifierait
     * qu'elle a été touchée par la cible d'un ennemi
     */
    public final long HITDELAY = 250;

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
    protected final static int MAX_LEVEL = 4 ; //à déterminer
    
    /**
     * Represente le cout de l'amelioration.
     */
    protected double upgradeCost;
    
    /**
     * Represente le nombre de point de vie initiale de la Tour.
     */
    protected final static int STARTING_HEALTH = 10 ; //à voir
    
    /**
     * Represente le temps d'attente entre chaque attaque de la Tour.
     */
    protected double reloadTime; // temps de charge avant de pouvoir attaquer de nouveau

    /**
     * Represente le temps eoule depuis la derniere attaque de la Tour.
     */
    protected double timeSinceLastAttack;

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
    protected double price;

    /**
     * Represente l'ennemi que la tour cible actuellement
     */
    protected Enemy target;

    /**
     * Image temporaire signifiant que la tour a été touchée par la cible d'un ennemi
     */
    protected BufferedImage reloadImage;

    /**
     * Chemin vers l'image de la tour
     */
    protected String imgName;

    /**
     * Chemin vers l'image de la tour touchée par la cible d'un ennemi
     */
    protected String reloadImgName;

    /**
     * Déclence HITDELAY
     */
    protected long hitStart ;

    /**
     * Signale si la tour a reçu un coup venant d'un ennemi
     */
    protected boolean tookHit = false;

    /**
     * Détermine si les attaques de la tour freeze les ennemis ou pas
     */
    protected boolean freezingAttack;

    /**
     * Determine si les attaques de la tour detruisent les depots de fungus ou pas
     */
    protected boolean fungusAttack;

    protected ArrayList<Tile> attainableTiles = new ArrayList<Tile>();

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
    public Tower(double x, double y, int size, double buyingCost, int startingHealth, double range, double power, boolean freezing,
                 boolean fungus, int upgradeCost, double reloadTime, long lastAttackTime, Type twType,
                 String image, String reloadImage) {
        super(x, y, size, startingHealth, startingHealth, ObsType.TOWER, buyingCost,image,reloadImage);
        this.range=range;
        this.power=power;
        this.upgradeCost=upgradeCost;
        this.reloadTime=reloadTime;
        this.lastAttackTime = lastAttackTime;
        this.towerType=twType;
        this.image = loadImage(image);
        this.fungusAttack = fungus;
        this.freezingAttack = freezing;
        Game.getBoard().addToListTowers(this);
        setAttainableTiles();
    }

    /**
     * Construit une Tour a la position "position" a partir des informations d'un PresetTower
     * 
     * @param presetTower Contient toute les informations concernant la Tour notament son prix et les degats qu'elle fait.
     * @param position Definit la position de la Tour.
     */
    public Tower(PresetTower presetTower, Position position) {
    	super(position.x, position.y, presetTower.getSize(), presetTower.startingHealth, presetTower.startingHealth,
                ObsType.TOWER, presetTower.price, presetTower.imgName,presetTower.reloadImage);
    	this.range = presetTower.getRange();
    	this.power = presetTower.getPower();
    	this.upgradeCost = presetTower.getUpgradeCost();
    	this.reloadTime = presetTower.getReloadTime();
    	this.lastAttackTime = presetTower.getLastAttackTime();
    	this.towerType = presetTower.getTowerType();
        this.fungusAttack = presetTower.isFungusAttack();
        this.freezingAttack = presetTower.isFreezingAttack();
        this.imgName = presetTower.imgName;
        this.reloadImgName = presetTower.reloadImage;
        Game.getBoard().addToListTowers(this);
        setAttainableTiles();
    }
    
    /**
     * Amelioration de la Tour.
     * 
     * @return true if the tower upgraded successfully
     */
    public boolean upgrade(){
        if (level < MAX_LEVEL && Game.getCredits() > upgradeCost){
            //augmentation de la taille ?
            Game.setCredits(-1*upgradeCost);
            level++;
            image = loadImage(imgName +"_n"+level+".png");
            reloadImage = loadImage(reloadImgName+"_n"+level+".png");
            this.setReloadImage(reloadImage);
            power*=getModifier();
            range*=getModifier();
            upgradeCost+=100;
            setRefundValue(upgradeCost/2);
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

    public double getReloadTime() {
        return reloadTime;
    }

    public double getTimeSinceLastAttack() {
        return timeSinceLastAttack;
    }
    @Override
    public BufferedImage getImage(){
        if (tookHit && System.currentTimeMillis() - hitStart > HITDELAY/Game.getGameSpeed()){
            if (System.currentTimeMillis() - hitStart > HITDELAY/Game.getGameSpeed()) tookHit = false;
        }
        return this.image;
    }

    @Override
    public void takeDamage(double damage) {
        this.hitStart = System.currentTimeMillis();
        tookHit = true;
        setCurrentHealth((int)(currentHealth - damage));
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

    public void remove(){
        for (int i = 0; i < this.getSize(); i++) {
            for (int j = 0; j < this.getSize(); j++) {
                Tile t = Game.getBoard().getTile((int)Math.round(this.position.x)+i,
                        (int)Math.round(this.position.y)+j);
                t.setEmpty(true);
            }
        }
    }

    public BufferedImage loadImage(String image){
        BufferedImage img = null;
        try{
            img = ImageIO.read(getClass().getResourceAsStream(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    public void setAttainableTiles(){
        for (int i = (int)-range ; i < (int)range+1 ; i++) {
            for (int j = (int)-range; j < (int)range+1; j++) {
                attainableTiles.add(Game.getBoard().getTile((int)Math.round(position.x) +i, (int)Math.round(position.y) + j));
            }
        }
    }

    public void launchAttack(){
        for (Tile attainableTile : attainableTiles){
            if (fungusAttack){
                if (check_Fungus(attainableTile)){
                    FungusProjectile projectile = new FungusProjectile(this.position, attainableTile.getPos(), this.power,
                            Game.getLevel(), attainableTile);
                    MapGenerator.towerProjectilesList.add(projectile);
                    timeSinceLastAttack = System.currentTimeMillis();
                    return;
                }
            }
        }
        for (Tile attainableTile : attainableTiles){
            if (check_Ennemy(attainableTile)){
                target = attainableTile.getEnemy();
                TowerProjectile projectile = new TowerProjectile(this.position, target.position, this.power,
                        Game.getLevel(), target, freezingAttack);
                MapGenerator.towerProjectilesList.add(projectile);
                timeSinceLastAttack = System.currentTimeMillis();
                break;
            }
        }
    }
}
