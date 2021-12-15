/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import mygame.Main;
import mygame.PlayerData;
import mygame.battle.PlayerCharacter;
import mygame.ngin.MyBaseAppState;
import java.util.ArrayList;

/**
 *
 * @author cameron
 */
public class BattleUI extends MyBaseAppState{
    private volatile PlayerData pd;
    private volatile SimpleApplication sapp;
    
    private static BitmapFont guiFont;
    
    private float sWidth;
    private static final float XPAD = 2;
    private static final float YPAD = 2;
    
    private Node buiNode;
    private float width, height;
    private ArrayList<BitmapText> statusRead;
    private Geometry background;
    
    private String constructStatus(PlayerCharacter pc){
        StringBuilder sb = new StringBuilder();
        
        sb.append(pc.getName());
        sb.append("\tHP: ");
        
        sb.append(pc.getHPValue());
        sb.append("/");
        sb.append(pc.getHPMaxValue());
        
        return sb.toString();
    }
    
    @Override
    protected void initialize(Application arg0) {
        sapp = (SimpleApplication)arg0;
        pd = ((Main)sapp).getPlayerData();
        
        sWidth = Main.getSettings().getWidth();
        
        if(guiFont == null)
            guiFont = sapp.getAssetManager().loadFont("Textures/ui/Liberation-Serif.fnt");
        
        buiNode = new Node("Battle UI");
        
        width = sWidth - 300;
        height = guiFont.getCharSet().getLineHeight() * 3.3f + (2 * YPAD);
        
        background = new Geometry("statusbg", new Quad(width, height));
        Material bgmat = new Material(sapp.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        bgmat.setColor("Color", UIElement.BACKGROUND.getColor());
        background.setMaterial(bgmat);
        background.move(0, 0, UIElement.BACKGROUND.getZOrder());
        buiNode.attachChild(background);
        
        statusRead = new ArrayList<>();
        
        for(int i=0; i<pd.getPartySize(); i++){
            BitmapText bmt = new BitmapText(guiFont);
            bmt.setSize(guiFont.getCharSet().getRenderedSize());
            bmt.setColor(UIElement.TEXT.getColor());
            bmt.move(
                    XPAD,
                    ((3-i)* 1.1f * bmt.getLineHeight() + YPAD),
                    UIElement.TEXT.getZOrder()
            );
            statusRead.add(bmt);
            buiNode.attachChild(bmt);
        }
        
        buiNode.setLocalTranslation(sWidth - width, 0, 40);
    }

    @Override
    protected void cleanup(Application arg0) {
        
    }

    @Override
    protected void onEnable() {
        sapp.getGuiNode().attachChild(buiNode);
        this.setPaused(false);
    }

    @Override
    protected void onDisable() {
        this.setPaused(true);
        
        sapp.getGuiNode().detachChild(buiNode);
    }
    
    @Override
    protected void onUnpause() {
        
    }
    
    @Override
    protected void onPause() {
        
    }
    
    @Override
    public void update(float tpf){
        
        if(!isPaused()){
            for(int i = 0; i < pd.getPartySize(); i++){
                statusRead.get(i).setText(constructStatus(pd.getPartyMember(i)));
            }
        }
    }
    
}
