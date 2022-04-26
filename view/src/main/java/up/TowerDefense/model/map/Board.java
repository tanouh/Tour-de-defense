package up.TowerDefense.model.map;

import up.TowerDefense.model.character.Enemy;
import up.TowerDefense.model.game.Game;
import up.TowerDefense.model.object.Obstacle;
import up.TowerDefense.model.object.PlaceableObstacle;
import up.TowerDefense.model.object.Position;
import up.TowerDefense.model.object.Tower;

import java.util.ArrayList;
import java.util.Collections;

public class Board {
    private Tile[][] tiles;
    public static Board map;
    private static ArrayList<Tower> listTowers;
    private static ArrayList<Enemy> listEnemy;
    private static ArrayList<Tower> toRemoveTowerList;
    private static ArrayList<Enemy> toRemoveEnemyList;
    public ArrayList<Position> spawnPoint;

    /* Stocke les positions des cases qu'occupent la cible principale*/
    private ArrayList<Position> targetZone = new ArrayList<>();

    public Board() {
        listTowers = new ArrayList<Tower>();
        listEnemy = new ArrayList<Enemy>();
        toRemoveTowerList = new ArrayList<Tower>();
        toRemoveEnemyList = new ArrayList<Enemy>();
        map = this;
    }

    public int worldX(){
        return tiles[0].length;
    }
    public int worldY(){
        return tiles.length;
    }
    public int sizeX() {
        return tiles.length;
    }

    public int sizeY() {
        return tiles[0].length;
    }

    public boolean isEmpty(int x, int y) {
        return tiles[y][x].isEmpty();
    }

    public boolean isEmpty(Position pos) {
        return isEmpty((int) Math.round(pos.x), (int) Math.round(pos.y));
    }

    public ArrayList<Position> getTargetZone(){
        return targetZone;
    }

    public void setTile(int x, int y) {
        tiles = new Tile[x][y];
    }

    public Tile[][] getTiles(){ return tiles; }

    public Tile getTile(int x, int y) { // x = col , y = row

        if (y < 0 || y >= tiles.length || x < 0 || x >= tiles[0].length){
            return null;
        }

        return tiles[y][x];
    }
    public Tile getTile(Position pos) {
        return getTile((int)Math.round(pos.x),(int)Math.round(pos.y));
    }

    public void addToListTowers(Tower tower){
        listTowers.add(tower);
    }

    public void addToListEnemy(Enemy enemy){
        listEnemy.add(enemy);
    }

    public ArrayList<Tower> getListTowers(){
        return listTowers;
    }

    public ArrayList<Enemy> getListEnemy(){
        return listEnemy;
    }

    public Obstacle getOccupier(Position position) {
        return getTile(position).getOccupier();
    }



    public void initTile(int x, int y, Tile tile, boolean isATargetZone) {
        tiles[x][y] = tile;
        if (isATargetZone) {
            targetZone.add(new Position(y, x));
            tiles[x][y].isTarget = true;
            tiles[x][y].isEmpty = true;
        }

    }

    public void setOccupier(Obstacle obstacle, int x, int y) {
        for (int i = 0; i < obstacle.getSize(); i++) {
            for (int j = 0; j < obstacle.getSize(); j++) {
                Tile t = getTile(x+i, y+j);
                t.setOccupier(obstacle);
            }
        }
    }

    /**
     * Ajoute les obstacles dans la grille de map
     *
     * @param obstacle
     * @param posX
     * @param posY
     */
    public boolean addObstacle(PlaceableObstacle obstacle, int posX, int posY) {
        if (getTile(posX, posY).isEmpty
                && legalPlacement(obstacle, posX, posY)
                && obstacle.getBuyingCost() <= Game.getCredits()) {

            setOccupier(obstacle, posX, posY);
            Game.setCredits(-obstacle.getBuyingCost());
            return true;
        } else {
            Exception e = new Exception("Action denied");
            e.printStackTrace();
            return false;
        }


    }

    /**
     * Retourne vrai si les cases occupées par l'obstacle sont toutes libres
     */
    private boolean legalPlacement(PlaceableObstacle obstacle, int posX, int posY) {
        for (int i = 0; i < obstacle.getSize(); i++)
            for (int j = 0; j < obstacle.getSize(); j++)
                if (!getTile(posX + i, posY + j).isEmpty) return false;
        return true;
    }

    /**
     * Retourne la position dans la zone ciblée qui est la plus proche de
     *
     * @param startPos
     * @return
     */
    public Position getNearestTargetPosition(Position startPos) {
        double distMin = 100;
        Position res = null;
        for (Position pos : targetZone) {
            if (distMin > startPos.Distance(pos)) {
                distMin = startPos.Distance(pos);
                res = pos;
            }
        }
        return res;
    }



    /**
     * Renvoie les points de frai
     */
    public void setSpawnablePoint() {
        spawnPoint = new ArrayList<>();

        int i;
        int j;

        for (i = 0 ; i < sizeX(); i++){ // on parcourt les bords latéraux
            for(j = 0; j < sizeY() ; j+=sizeY() -1){  // en hauteur
                if (tiles[i][j].isEmpty)
                    spawnPoint.add(new Position (j,i));
            }
        }

        for(j = 0 ; j <sizeY() ; j+=1){ // on parcourt en largeur
            for (i = 0; i < sizeX(); i+=sizeX()-1){ // les bords haut et bas
                if (tiles[i][j].isEmpty)
                    spawnPoint.add(new Position (j,i));
            }
        }
    }

    public void shuffleSpawnList(){
        Collections.shuffle(spawnPoint);
    }

    public static void launchAllAttacks(){
        for (Enemy enemy : listEnemy){
            if(System.currentTimeMillis() - enemy.getTimeSinceLastAttack() > enemy.getReloadTime())
                if (enemy.isAlive()) enemy.identifyTarget();
                else toRemoveEnemyList.add(enemy);
        }
        for (Tower tower : listTowers){
            if(System.currentTimeMillis() - tower.getTimeSinceLastAttack() > tower.getReloadTime())
                if (tower.isAlive()) tower.launchAttack();
                else toRemoveTowerList.add(tower);
        }
        listEnemy.removeAll(toRemoveEnemyList);
        listTowers.removeAll(toRemoveTowerList);
        toRemoveTowerList = new ArrayList<>();
        toRemoveEnemyList = new ArrayList<>();

    }


}
