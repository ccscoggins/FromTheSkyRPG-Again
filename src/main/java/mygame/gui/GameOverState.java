/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import com.jme3.ui.Picture;
import mygame.ngin.MyBaseAppState;

enum GameOverOption{
    //CONTINUE("Continue from Last Save"),
    QUIT("Return to Title Screen");
    private final String text;
    
    private GameOverOption(String text){
        this.text = text;
    }
    
    public String getText(){
        return text;
    }
}

/**
 *
 * @author cameron
 */
public class GameOverState extends GenericMenuState<GameOverOption> {
    private Picture gameOverScreen;
    private static final float goWidth = 900, goHeight = 480;
    
    public GameOverState(MyBaseAppState creator){
        super();
        populate(GameOverOption.values());
    }
    
    @Override
    protected void initialize(Application appl){
        super.initialize(appl);
        setPosition((sWidth - width) / 2, (sHeight - height) / 2);
        
        setTextBGElements(UIElement.DARK_TEXT, UIElement.DARK_BACKGROUND);
        
        gameOverScreen = new Picture("game over bg");
        gameOverScreen.setImage(sapp.getAssetManager(), "Textures/ui/gameover.png", true);
        gameOverScreen.setWidth(goWidth);
        gameOverScreen.setHeight(goHeight);
        gameOverScreen.setPosition((sWidth - goWidth) / 2, (2 * sHeight / 3) - (goHeight / 2));
        //System.out.println("posx: " + ((sWidth - goWidth) / 2) + "posy: " + ((sHeight / 3) - (goHeight / 2)));
        gameOverScreen.move(0, 0, UIElement.IMAGE.getZOrder());
        
        //System.out.println(gameOverScreen);
        
        otherUINode.attachChild(gameOverScreen);
        
        redrawMenu();
        
        sapp.getStateManager().detach(creator);
    }
    
    @Override
    protected void onDisable(){
        super.onDisable();
        sapp.getStateManager().detach(this);
    }
    
    @Override
    protected String getStringForType(GameOverOption type) {
        return type.getText();
    }

    @Override
    public void select(GameOverOption type) {
        switch(type){
            case QUIT:
                sapp.getStateManager().attach(new TitleScreenState());
                this.setEnabled(false);
                break;
            default:
                sapp.getStateManager().attach(new StatusMessage(this, "Not yet implemented"));
        }
    }
    
}
