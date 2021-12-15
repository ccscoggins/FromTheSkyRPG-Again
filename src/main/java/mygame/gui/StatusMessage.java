/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import mygame.Control;
import mygame.Main;
import mygame.ngin.MyBaseAppState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 *
 * @author cameron
 */
public class StatusMessage extends MyBaseAppState implements ActionListener {
    private volatile MyBaseAppState creator;
    private volatile SimpleApplication sapp;
    private volatile Node guinode;
    
    private float sWidth, sHeight;
    
    private volatile BitmapFont defaultUIFont;
    
    private EnumMap<Control, Boolean> controls;
    
    private Node messageBox;
    private float height;
    
    private Geometry background;
    private static final int defaultUIBackgroundColor = 0xa0c0f080;
    
    
    private List<String> messageData;
    private BitmapText messageDisplay;
    private int displayIndex;
    private float displayLengthRaw;
    private int displayLength;
    private float displaySpeed; //the speed to display one full line of text in seconds.
    private static final float DEFAULT_DISPLAY_SPEED = 0.25f;
    
    private Boolean fullyDisplayed = false, advance = false;
    
    private static int statusOffset = 0;
    
    
    public StatusMessage(MyBaseAppState creator, String message){
        this.creator = creator;
        this.messageData = new ArrayList<String>();
        displaySpeed = DEFAULT_DISPLAY_SPEED;
        messageData.add(message);
    }
    
    public StatusMessage(MyBaseAppState creator, String [] message){
        this(creator, message, DEFAULT_DISPLAY_SPEED);
    }
    
    public StatusMessage(MyBaseAppState creator, String [] message, float displaySpeed){
        this.creator = creator;
        this.messageData = Arrays.asList(message);
        this.displaySpeed = displaySpeed; 
    }
    
    @Override
    protected void initialize(Application arg0) {
        sapp = (SimpleApplication)arg0;
        guinode = sapp.getGuiNode();
        
        statusOffset += 100;
        
        defaultUIFont = ((Main)sapp).getGuiFont();
        
        sWidth = ((Main)sapp).getSettings().getWidth();
        sHeight = ((Main)sapp).getSettings().getHeight();
        
        messageBox = new Node("Message Box");
        
        messageDisplay = new BitmapText(defaultUIFont);
        messageDisplay.setSize(defaultUIFont.getCharSet().getRenderedSize());
        messageDisplay.setColor(new ColorRGBA().fromIntRGBA(0x0b0f20ff)); //FIXME define constant
        messageDisplay.setLineWrapMode(LineWrapMode.Word);
        messageDisplay.setBox(new Rectangle(2, height-2, sWidth-16, height-4));
        messageDisplay.setAlignment(BitmapFont.Align.Left);
        
        height = messageDisplay.getLineHeight() * 3.3f;
        messageDisplay.setLocalTranslation(8,
                messageDisplay.getLineHeight()*3.2f,
                UIElement.TEXT.getZOrder()
        );
        
        messageBox.attachChild(messageDisplay);
        
        background = new Geometry("Background", new Quad(sWidth, height));
        Material bgmat = new Material(sapp.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        bgmat.setColor("Color", new ColorRGBA().fromIntRGBA(defaultUIBackgroundColor));
        background.setMaterial(bgmat);
        background.setLocalTranslation(0, 0, UIElement.BACKGROUND.getZOrder());
        messageBox.attachChild(background);
        
        messageBox.setLocalTranslation(0, 0, statusOffset); //put in front of previous ui elements just in case
    }

    @Override
    protected void cleanup(Application arg0) {
        sapp.getInputManager().removeListener(this);
        statusOffset -= 100;
    }

    @Override
    protected void onEnable() {
        creator.setPaused(true);
        guinode.attachChild(messageBox);
        sapp.getInputManager().addListener(this, Control.CONFIRM.name());
        advance = false;
        fullyDisplayed = false;
        displayLengthRaw = 0;
        displayIndex = 0;
    }

    @Override
    protected void onDisable() {
        guinode.detachChild(messageBox);
        sapp.getInputManager().removeListener(this);
        creator.setPaused(false);
        sapp.getStateManager().detach(this);
    }
    
    @Override
    protected void onUnpause() {
        sapp.getInputManager().addListener(this, Control.CONFIRM.name());
    }
    
    @Override
    protected void onPause(){
        sapp.getInputManager().removeListener(this);
    }
    
    
    @Override
    public void onAction(String name, boolean pressed, float tpf) {
        if(name.equals(Control.CONFIRM.name()) && pressed == true){
            if(!fullyDisplayed){
                fullyDisplayed = true;
            }
            else{
                advance = true;
            }
        }
    }
    
    @Override
    public void update(float tpf){
        super.update(tpf);
        int len = messageData.get(displayIndex).length();
        
        if(advance){
            if(displayIndex >= messageData.size()-1){
                this.setEnabled(false);
            }
            else{
                advance = false;
                displayIndex++;
                fullyDisplayed = false;
                displayLengthRaw = 0;
                displayLength = 0;
            }
        }
        
        if(!fullyDisplayed && displayLengthRaw < len){
            displayLengthRaw += ((tpf * len) / displaySpeed);
            displayLength = (int)FastMath.floor(displayLengthRaw);
            if(displayLength > len) displayLength = len;
        }
        else if (!fullyDisplayed && displayLengthRaw >= len){
            fullyDisplayed = true;
        }
        else if (fullyDisplayed && displayLengthRaw < len){
            displayLengthRaw = messageData.get(displayIndex).length();
            displayLength = (int) displayLengthRaw;
        }
        
        messageDisplay.setText(messageData.get(displayIndex).substring(0, displayLength));
        
    }
    
    
    
}
