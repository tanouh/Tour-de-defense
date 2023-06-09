package up.TowerDefense.model.game;

import up.TowerDefense.model.map.Board;
import up.TowerDefense.model.map.Tile;
import up.TowerDefense.model.object.Position;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Cette classe regroupe les fonctions qui pourront être utiliser par les différents objets
 * dans le modèle
 * */

public class StaticFunctions {

    /**
     * @param image chemin de l'image
     * @return l'image chargés s'il n'y a pas d'erreur sinon
     * renvoie null
     */
    public static BufferedImage loadImage(String image){
        BufferedImage img = null;
        try{
            img = ImageIO.read(StaticFunctions.class.getResourceAsStream(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }



    /**************   LES FONCTIONS RELIEES A LA CARTE  ****************/

    /**
     * Détecte les tours aux environs du personnage
     * @param src la position du personnage
     * @param range sa portée
     * @param board la carte de jeu
     * @return
     */
    public static Position findTower(Position src, double range, Board board){
        int posX = (int)Math.round(src.x);
        int posY = (int)Math.round(src.y);
        int _range = (int)Math.round(range);
        for (int i = -_range ;i < _range+1  ; i++){

           for (int j = -_range ;j < _range +1 ; j++){
               if ((i == 0 && j == 0) ||
                (posX+i < 0 || posY+j < 0 || posX+i >= board.worldX() || posY+j >= board.worldY())) continue;
               if (check_Tower(board, posX+i, posY+j)){
                   return new Position (posX+i , posY+j);
               }
            }
        }
        return null;
    }

    /**
     * Vérifie s'il y a une tour à la position
     * @param x et @param y de @param board
     */
    public static boolean check_Tower(Board board, int x, int y){
        return  board.getTile(x, y)!= null && board.getTile(x, y).hasATower();
    }

    /**
     * Vérifie s'il y a un ennemi dans la case
     */
    public static boolean check_Ennemy(Tile attainableTile){
        return  attainableTile != null && attainableTile.hasAnEnnemy();
    }

    /**
     * Verifie s'il y a un depot de fungus dans la case
     */
    public static boolean check_Fungus(Tile attainableTile){
        return attainableTile != null && attainableTile.isBooster();
    }
}
