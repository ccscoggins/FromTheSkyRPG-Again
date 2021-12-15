/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.ui.Picture;
import mygame.Main;

enum MainMenuOption{
    NEWGAME("Start Demo"),
    //CREDITS("Credits"),
    EXIT("Quit to Desktop");
    private final String text;
    
    private MainMenuOption(String text){
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
public class TitleScreenState extends GenericMenuState<MainMenuOption> {
    Picture titleBG;
    
    AudioNode music;
    
    public TitleScreenState(){
        super();
        populate(MainMenuOption.values());
    }
    
    @Override
    protected void initialize(Application appl){
        super.initialize(appl);
        
        titleBG = new Picture("titlebg");
        titleBG.setImage(sapp.getAssetManager(), "Textures/ui/titleBG.png", false);
        titleBG.setWidth(sWidth);
        titleBG.setHeight(sHeight);
        titleBG.move(0, 0, UIElement.BACKGROUND.getZOrder() - 5);
        
        
        otherUINode.attachChild(titleBG);
        
        menuNode.setLocalTranslation((sWidth - width) / 2, (sHeight / 3)-(height / 2), 0);
        
        music = new AudioNode(sapp.getAssetManager(), "Audio/voyage.ogg", AudioData.DataType.Buffer);
        music.setDirectional(false);
        music.setPositional(false);
        music.setLooping(true);
        music.setVolume(0.6f);
        
    }
    
    @Override
    protected void onEnable(){
        super.onEnable();
        
        music.play();
    }
    
    @Override
    protected void onDisable(){
        super.onDisable();
        
        music.stop();
    }
    
    @Override
    protected String getStringForType(MainMenuOption type) {
        return type.getText();
    }

    @Override
    public void select(MainMenuOption type) {
        switch (type){
            case NEWGAME:
                ((Main)sapp).startNewGame();
                sapp.getStateManager().detach(this);
                break;
            /*case CREDITS:
                sapp.getStateManager().attach(new CreditsState(this));
                break;*/
            case EXIT:
                ((Main)sapp).exitGame();
                break;
            default:
                sapp.getStateManager().attach(new StatusMessage(this, "Not yet implemented"));
        }
    }
    
    
}
