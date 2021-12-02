package com.mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.mygame.gui.FieldMenuState;
import com.mygame.ngin.MyBaseAppState;

/**
 * Controls primary overworld gameplay.
 * @author cameron 
 */
public class OverworldAppState extends MyBaseAppState implements ActionListener {
    private final OverworldMap map;
    private volatile SimpleApplication sapp;
    private volatile BulletAppState bas;
    private volatile PhysicsSpace phys;
    
    private Node rootNode;
    private ChaseCamera chaseCam;
    DirectionalLight sun;
    private Node player;
    private OverworldCharacter playerCharacter;
    private Spatial characterModel;
    private FieldMenuState menuState;
    
    
    /**
     * Simple constructor, instatiates map object which contains data incl spatial and encounter tables
     * @param map 
     */
    public OverworldAppState(OverworldMap map){
        this.map = map;
    }
    
    /**
     * AppState initialize function; grabs map and player data and constructs player movement system, camera 
     * @param app 
     */
    @Override
    protected void initialize(Application app) {
        this.sapp = (SimpleApplication)app;
        rootNode = this.sapp.getRootNode();
        bas = sapp.getStateManager().getState(BulletAppState.class);
        phys = bas.getPhysicsSpace();
        
        AppStateManager asm = sapp.getStateManager();
        menuState = new FieldMenuState(this);
        asm.attach(menuState);
        menuState.setEnabled(false);
        
        InputManager im = sapp.getInputManager();
        
        //construct player spatial and offset vertically
        player = new Node(); //created to offset the collision from the model
        characterModel = sapp.getAssetManager().loadModel("Models/sci_fi_protagonist.j3o");
        characterModel.setLocalScale(0.4f);
        player.attachChild(characterModel);
        characterModel.setLocalTranslation(0, -4.0f, 0); //FIXME make values non-arbitrary
        
        //create control and attach to player, add input, attach construct to root node
        playerCharacter = new OverworldCharacter((Spatial)player);
        playerCharacter.setPhysicsLocation(map.getSpawnPoint());
        playerCharacter.setPhysicsSpace(phys);
        playerCharacter.addInputListeners(sapp.getInputManager());
        
        
        //borrowed heavily from a previous project
        chaseCam = new ChaseCamera(sapp.getCamera(), characterModel);//, sapp.getInputManager()
        chaseCam.registerWithInput(sapp.getInputManager());
        chaseCam.setMinDistance(20.0f);
        chaseCam.setMaxDistance(40.0f);
        chaseCam.setDefaultDistance(30.0f);
        chaseCam.setDefaultVerticalRotation(FastMath.PI / 6);
        chaseCam.setMinVerticalRotation(FastMath.PI / 24);
        chaseCam.setDownRotateOnCloseViewOnly(false);
        chaseCam.setMaxVerticalRotation((2 * FastMath.PI) / 6);
        chaseCam.setLookAtOffset(new Vector3f(0, 4, 0));
        chaseCam.setDragToRotate(false);
        chaseCam.setDefaultHorizontalRotation(FastMath.PI / 2 * 3); //start behind the player instead of in front
        
        playerCharacter.setCamera(sapp.getCamera());
        
        //palette sun code so we can actually see what we're doing
        /* A white, directional light source */ 
        sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        
        
        phys.addAll(map.getMapScene());
        
    }
    
    /**
     * AppState cleanup method, required but empty
     * @param app 
     */
    @Override
    protected void cleanup(Application app) {
        
    }
    
    /**
     * Attaches elements to the scene graph, then enables movement and physics
     */ 
    @Override
    protected void onEnable() {
        rootNode.attachChild(map.getMapScene()); //map.getMapScene() returns a spatial node tree with attached Minie rigidBodyControls
        rootNode.attachChild(player);
        rootNode.addLight(sun);
        
        this.setPaused(false);
    }
    
    /**
     * Disables movement and physics, then detaches elements from the scene graph
     */
    @Override
    protected void onDisable() {
        this.setPaused(true);
        InputManager im = sapp.getInputManager();
        
        //remove spatials
        rootNode.detachChild(map.getMapScene());
        rootNode.detachChild(player);
        
        //remove input listening
        im.removeListener(playerCharacter);
        
        //remove sun
        rootNode.removeLight(sun);
    }
    
    /**
     * Enables movement, physics, and the camera
     */
    @Override
    protected void onUnpause(){
        bas.setEnabled(true);
        sapp.getInputManager().addListener(this, Control.MENU_OPEN.name());
        chaseCam.setEnabled(true);
        chaseCam.setDragToRotate(false);
        playerCharacter.setEnabled(true);
    }
    
    /**
     * Disables movement, physics, and the camera
     */
    @Override
    protected void onPause(){
        playerCharacter.setEnabled(false);
        sapp.getInputManager().removeListener(this);
        chaseCam.setDragToRotate(true);
        chaseCam.setEnabled(false);
        //sapp.getInputManager().setCursorVisible(true);
        
        bas.setEnabled(false);
    }
    
    /**
     * Required override, unused
     * @param tpf 
     */
    @Override
    public void update(float tpf) {
       
    }

    /**
     * Listens for the menu to be opened.
     * @param name name of the action that's pressed
     * @param pressed true if pressed, false otherwise
     * @param arg2 time per frame, unused
     */
    @Override
    public void onAction(String name, boolean pressed, float arg2) {
        
        if(isEnabled() && name.equals(Control.MENU_OPEN.name()) && pressed == true){
            //System.out.println("Pressed menu_open");
            menuState.setEnabled(true);
        }
    }
}
