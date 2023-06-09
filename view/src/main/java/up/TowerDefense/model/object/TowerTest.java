package up.TowerDefense.model.object;


public class TowerTest extends Tower{

    private static final int newSize = 2;
    // variables à déterminer
    private static double newBuyingCost=100;
    private static double startingRange = 5.0;
    private static double startingPower = 2.0;
    private static int startingUpgradeCost = 100;
    private static double startingReloadTime = 1000;
    private static long newLastAttackTime = 0;
    private static Type towerType = Tower.Type.TOWERTEST;
    private static String image = "/tour";
    private static String reloadImage = "/tour_touche";

    public TowerTest(double x, double y) {
        super(x, y, newSize, newBuyingCost,100,startingRange, startingPower, false, false, startingUpgradeCost,
                startingReloadTime,  newLastAttackTime, towerType,image,reloadImage);
    }





}
