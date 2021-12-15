/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.ngin;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import mygame.Main;

/**
 *
 * @author cameron
 */
public class TransitionState extends MyBaseAppState {
    private enum TransitionProgress{
        OUT,
        DWELL,
        IN
    }
    
    private volatile SimpleApplication sapp;
    private volatile Node guiNode;
    
    private Geometry whiteScreen;
    private Material whiteMaterial;
    private int opacity = 0x00;
    private static final float rate = 1f;
    private static final float stay = 0.5f;
    private float currentTimer;
    
    private TransitionProgress progress;
    
    private MyBaseAppState from;
    private MyBaseAppState to;
    private boolean disableAfter;
    
    public TransitionState(MyBaseAppState from, MyBaseAppState to, boolean disableAfter){
        this.from = from;
        this.to = to;
        this.disableAfter = disableAfter;
    }
    
    @Override
    protected void initialize(Application appl){
        sapp = (SimpleApplication)appl;
        progress = TransitionProgress.OUT;
        
        float sWidth = Main.getSettings().getWidth();
        float sHeight = Main.getSettings().getHeight();
        
        whiteScreen = new Geometry("screenCover", new Quad(sWidth, sHeight));
        whiteMaterial = new Material(sapp.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        whiteMaterial.setColor("Color", new ColorRGBA().fromIntRGBA(0xffffff00));
        whiteScreen.setMaterial(whiteMaterial);
        whiteScreen.move(0, 0, 1000);
        
        guiNode = sapp.getGuiNode();
        
    }
    
    @Override
    protected void cleanup(Application appl){
        
    }
    
    @Override
    protected void onEnable(){
        guiNode.attachChild(whiteScreen);
        from.setPaused(true);
        to.setPaused(true);
        onUnpause();
    }
    
    @Override
    protected void onDisable(){
        onPause();
        guiNode.detachChild(whiteScreen);
        sapp.getStateManager().detach(this);
    }
    
    @Override
    protected void onUnpause(){
        currentTimer = 0;
    }
    
    @Override
    protected void onPause(){
        
    }
    
    @Override
    public void update(float tpf){
        currentTimer += tpf;
        
        int opacity = (int)(0xff * ((currentTimer / rate) > 1 ? 1 : (currentTimer / rate)));
        //System.out.println("opacity: " + opacity + "; progress: " + progress.name());
        switch(progress){
            case OUT:
                whiteMaterial.setColor("Color", new ColorRGBA().fromIntRGBA(0xffffff00 | opacity));
                
                if(currentTimer > rate){
                    progress = TransitionProgress.DWELL;
                    currentTimer = 0;
                    from.setEnabled(false);
                }
                break;
            case DWELL:
                if(currentTimer > stay){
                    progress = TransitionProgress.IN;
                    if(disableAfter)
                        sapp.getStateManager().detach(from);
                    if(sapp.getStateManager().hasState(to)){
                        to.setEnabled(true);
                    }
                    else{
                        sapp.getStateManager().attach(to);
                    }
                    currentTimer = 0;
                }
                break;
            case IN:
                whiteMaterial.setColor("Color", new ColorRGBA().fromIntRGBA(0xffffff00 | (0xff - opacity)));
                
                if(currentTimer > rate){
                    this.setEnabled(false);
                }
                break;
        }
    }
    
}
