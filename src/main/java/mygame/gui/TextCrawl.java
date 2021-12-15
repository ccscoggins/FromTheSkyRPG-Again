/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.ui.Picture;
import mygame.Control;
import mygame.Main;
import mygame.ngin.MyBaseAppState;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cameron
 */
public class TextCrawl extends MyBaseAppState implements ActionListener {
    protected volatile SimpleApplication sapp;
    
    protected ArrayList<String> text;
    protected ArrayList<BitmapText> displayText;
    
    private UIElement textElement = UIElement.DARK_TEXT;
    private UIElement bgElement = UIElement.DARK_BACKGROUND;
    
    protected float sWidth, sHeight;
    protected float yOffset; //top of scroll
    protected float speed;
    private float textHeight = 0;
    
    
    protected Node crawlNode;
    protected Node textNode;
    
    private static BitmapFont guiFont;
    
    protected Geometry background;
    protected Material bgmat;
    
    private boolean skipDisplayed = false;
    private boolean skip = false;
    private Picture skipPicture;
    private final float SKIP_WIDTH = 320, SKIP_HEIGHT = 192;
    private final float zoffset = 50;
    
    protected void redraw(float tpf){
    }
    
    public void setSpeed(float speed){
        this.speed = speed;
    }
    
    public void populate(List<String> text){
        sWidth = Main.getSettings().getWidth();
        sHeight = Main.getSettings().getHeight();
        
        
        for( String s : text ){
            BitmapText bmt = new BitmapText(guiFont);
            bmt.setText(s);
            bmt.setSize(guiFont.getCharSet().getRenderedSize());
            BitmapText last = null;
            if(! displayText.isEmpty()){
                last = displayText.get(displayText.size() - 1);
            }
            float yoffset = (last == null ? 0 : last.getLocalTranslation().getY() + last.getHeight());
            
            bmt.setLocalTranslation(
                    (sWidth - bmt.getLineWidth())/2,
                    -1 * yoffset,
                    textElement.getZOrder());
            
            textHeight = yoffset + bmt.getHeight();
            displayText.add(bmt);
        }
    }
    
    protected void setupInput(InputManager im){
        im.addListener(this, Control.CONFIRM.name(), Control.MENU_OPEN.name());
        
    }

    @Override
    protected void initialize(Application arg0) {
        sapp = (SimpleApplication)arg0;
        AssetManager am = sapp.getAssetManager();
        
        textNode = new Node("crawl text");
        crawlNode = new Node("crawl container");
        
        if(guiFont == null){
            guiFont = am.loadFont("Textures/ui/Liberation-Serif.fnt");
        }
        
        for(BitmapText bmt : displayText){
            textNode.attachChild(bmt);
        }
        
        background = new Geometry("crawlBG", new Quad(sWidth, sHeight));
        bgmat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        bgmat.setColor("Color", bgElement.getColor());
        background.setMaterial(bgmat);
        background.move(0, 0, bgElement.getZOrder());
        crawlNode.attachChild(background);
        
        skipPicture = new Picture("skip indicator");
        skipPicture.setImage(am, "Textures/ui/skip.png", true);
        skipPicture.setCullHint(Spatial.CullHint.Always);
        skipPicture.move(sWidth - SKIP_WIDTH, SKIP_HEIGHT, UIElement.CURSOR.getZOrder());
        crawlNode.attachChild(skipPicture);
        
        crawlNode.attachChild(textNode);
        crawlNode.move(0, 0, zoffset);
    }

    @Override
    protected void cleanup(Application arg0) {
        
    }

    @Override
    protected void onEnable() {
        sapp.getGuiNode().attachChild(crawlNode);
        setPaused(false);
    }

    @Override
    protected void onDisable() {
        setPaused(true);
        sapp.getGuiNode().detachChild(crawlNode);
        
    }
    
    @Override
    protected void onUnpause() {
        
    }

    @Override
    protected void onPause() {
        
    }
    
    @Override
    public void update(float tpf){
        
        if (!isPaused()){
            textNode.move(0, speed * tpf, 0);
            System.out.println("node y: " + textNode.getLocalTranslation().getY() + "; target: " + sHeight + textHeight);
            if(textNode.getLocalTranslation().getY() > sHeight + textHeight){
                this.setEnabled(false);
            }
            
        }
        
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(!isPaused()){
            if(name.equals(Control.CONFIRM.name()) && isPressed && skipDisplayed == false){
                skipDisplayed = true;
                skipPicture.setCullHint(Spatial.CullHint.Inherit);
            }
            else if (name.equals(Control.MENU_OPEN.name()) && isPressed && skipDisplayed == true){
                this.setEnabled(false);
            }
        }
    }
    
}
