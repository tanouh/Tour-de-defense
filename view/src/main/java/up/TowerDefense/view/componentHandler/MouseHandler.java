package up.TowerDefense.view.componentHandler;

import up.TowerDefense.view.mainComponent.ScreenPanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static up.TowerDefense.view.mainComponent.ScreenPanel.*;

public class MouseHandler implements MouseListener {

    public int mouseX;
    public int mouseY;
    public boolean mousePressed;
    private ScreenPanel screenPanel;

    public MouseHandler(ScreenPanel screenPanel){
        this.screenPanel = screenPanel;
    }

    private void update(int x , int y){
        mouseX = (x + screenPanel.camera.worldX - screenPanel.camera.screenX )/tileSize;
        mouseY = (y + screenPanel.camera.worldY - screenPanel.camera.screenY)/tileSize;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        mousePressed = true;
        int x  = e.getX();
        int y = e.getY();
        update(x,y);

        try {
            screenPanel.mapGen.addObstacle(mouseX, mouseY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed =false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mousePressed = false;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mousePressed = false;
    }

}
