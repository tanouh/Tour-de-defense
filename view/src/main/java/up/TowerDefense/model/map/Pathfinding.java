package up.TowerDefense.model.map;
import up.TowerDefense.model.game.Game;
import up.TowerDefense.model.object.Position;
import java.util.*;

public class Pathfinding {

    public static Path FindPath(Position startPos, Position targetPos) {
        Node startNode = new Node(Board.map.getTile(startPos));
        Node targetNode = new Node(Board.map.getTile(targetPos));

        Board.map.getTile(targetPos).setTarget(true);

        List<Node> openSet = new ArrayList<Node>();
        HashSet<Node> closedSet = new HashSet<Node>();
        openSet.add(startNode);

        long a = System.currentTimeMillis();

        while (openSet.size() > 0) {
            //Cherchez le meilleur node dans openSet et l'ajouter à closedSet
            Node node = Node.BestNode(openSet);
            openSet.remove(node);
            closedSet.add(node);

            //Cas où node est le final
            if (node.isTarget(targetPos)){
                return RetracePath(startNode,node);
            }

            Node[] neighbours = node.Neighbours();

            for (Node neighbour : neighbours) {
                //Annuler si le voisin est une case bloquée ou déjà traitée
                if (!neighbour.tile.isEmpty() || closedSet.contains(neighbour)) continue;

                //Calcul du coup de chaque voisin puis ajout du voisin à openSet
                int newCostToNeighbour = node.gCost + GetDistance(node, neighbour);
                if (newCostToNeighbour < neighbour.gCost || !openSet.contains(neighbour)) {
                    neighbour.gCost = newCostToNeighbour;
                    neighbour.hCost = GetDistance(neighbour, targetNode);
                    neighbour.parent = node;
                    if (neighbour.tile.isTarget){
                        closedSet.add(neighbour);
                        return RetracePath(startNode, neighbour);
                    }
                    if (!openSet.contains(neighbour))
                        openSet.add(neighbour);
                }
            }
            if(System.currentTimeMillis() -a > 200) {
                Game.getBoard().getTargetZone().remove(targetPos);
                return FindPath(startPos, Game.getBoard().getNearestTargetPosition(startPos));
            }
        }
        return null;
    }

    private static Path RetracePath(Node startNode, Node endNode) {
        List<Node> path = new ArrayList<Node>();
        Node currentNode = endNode;
        //Partir du endNode et remonter jusqu'au premier
        while (currentNode != startNode) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }path.add(startNode);
        //Inverser la suite pour obtenir une liste de tuiles du début à la fin
        Tile[] tilePath = new Tile[path.size()];
        for(int i = 0; i < tilePath.length; i++){
            tilePath[i] = path.get(tilePath.length - i - 1).tile;
        }
        return new Path(tilePath);
    }

    //La distance est particulière et considère qu'un déplacement vertical à un coup de 14
    private static int GetDistance(Node nodeA, Node nodeB) {
        int dstX = (int)Math.round(Math.abs(nodeA.tile.getPos().x - nodeB.tile.getPos().x));
        int dstY = (int)Math.round(Math.abs(nodeA.tile.getPos().y - nodeB.tile.getPos().y));

        if (dstX > dstY)
            return 14*dstY + 10* (dstX-dstY);
        return 14*dstX + 10 * (dstY-dstX);
    }
}
