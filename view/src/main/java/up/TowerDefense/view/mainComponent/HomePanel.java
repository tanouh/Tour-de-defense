package up.TowerDefense.view.mainComponent;

import up.TowerDefense.view.secondaryComponent.Button;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class HomePanel extends JPanel{
    private JLabel title = new JLabel("project Covid Defense");
    private JPanel body = new JPanel();
    private JPanel buttons = new JPanel(new GridLayout(2,1, 0, 10));
    private JPanel footer = new JPanel(new BorderLayout());
    private Button startGame = new Button();
    private Button leaveGame = new Button();
    private Button options = new Button();

    private GameWindow gameWindow;

    //stockage des options choisies (stockage dans une List ?) :
    private static int nbLevel = 5;
    private static int numberWaves = 5;
    private static int zoom = 1;
    private static int backgroundMusic = 5;
    private static int gameSound = 5;
    private static int gameSpeed = 1;
    //...

    public HomePanel(GameWindow gameWindow){
        this.gameWindow = gameWindow;
        gameWindow.setTitle("project Covid Defense");

        startGame.startButton(gameWindow, this);
        leaveGame.leaveButton(gameWindow);
        options.optionButton(gameWindow, this, null);

        this.setLayout(new BorderLayout());
        this.setBackground(GameWindow.background);
        this.setBorder(new LineBorder(GameWindow.foreground, 5));
        body.setBackground(GameWindow.background);
        buttons.setBackground(GameWindow.background);
        footer.setBackground(GameWindow.background);

        this.add(title, BorderLayout.NORTH);
        this.add(body, BorderLayout.CENTER);
        this.add(footer, BorderLayout.SOUTH);

        title.setPreferredSize(new Dimension(gameWindow.getWidth(), gameWindow.getHeight()/5));
        footer.setPreferredSize(new Dimension(gameWindow.getWidth(), gameWindow.getHeight()/7));
        title.setFont(new Font(GameWindow.font,Font.BOLD, GameWindow.widthScreen/20));
        title.setForeground(GameWindow.foreground);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setVerticalAlignment(JLabel.CENTER);

        buttons.setPreferredSize(new Dimension(100, 50));
        startGame.setHorizontalAlignment(JButton.CENTER);
        buttons.add(startGame);
        buttons.add(leaveGame);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(Box.createVerticalGlue());
        body.add(buttons);
        body.add(Box.createVerticalGlue());

        footer.add(options, BorderLayout.EAST);
    }

    /**
     *Modifie les parametres de jeu en fonction
     * des choix faits dans le OptionPanel
     */
    public void applyOptions(int nbLevel, int numberWaves, int zoom, int backgroundMusic, int gameSound, int gameSpeed){
        if (numberWaves != 0){
            this.nbLevel = nbLevel;
            this.numberWaves = numberWaves;
            this.zoom = zoom;
        }
        this.backgroundMusic = backgroundMusic;
        this.gameSound = gameSound;
        this.gameSpeed = gameSpeed;
    }

    public static int[] getOptions(){
        return new int[]{nbLevel, numberWaves, zoom, backgroundMusic, gameSound, gameSpeed};
    }
}