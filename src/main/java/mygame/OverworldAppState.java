package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
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
import com.jme3.ui.Picture;
import mygame.battle.BattleAppState;
import mygame.gui.FieldMenuState;
import mygame.gui.GameOverState;
import mygame.gui.StatusMessage;
import mygame.ngin.MyBaseAppState;
import mygame.ngin.TransitionState;
import java.util.List;

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
    private DirectionalLight sun;
    private DirectionalLight offSun;
    private AudioNode music;
    
    private Node player;
    private OverworldCharacter playerCharacter;
    private Spatial characterModel;
    private String spawnPointID;
    
    private FieldMenuState menuState;
    
    private EncounterData nextEncData;
    private boolean doNextEncounter = false;
    private boolean doGameOver = false;
    
    private String nextScript;
    private boolean doNextScript = false;
    
    private String nextMap;
    private String nextMapSpawn;
    private boolean doNextMap = false;
    
    private float encTimer;
    
    
    /**
     * Simple constructor, instatiates map object which contains data incl spatial and encounter tables
     * @param map 
     */
    public OverworldAppState(OverworldMap map){
        this(map, "SPAWNPOINT");
    }
    
    public OverworldAppState(OverworldMap map, String spawnPointNodeName){
        this.map = map;
        spawnPointID = spawnPointNodeName;
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
        
        map.findSpawn(spawnPointID);
        
        //construct player spatial and offset vertically
        player = new Node(); //created to offset the collision from the model
        characterModel = sapp.getAssetManager().loadModel("Models/sci_fi_protagonist.j3o");
        characterModel.setLocalScale(0.4f);
        player.attachChild(characterModel);
        characterModel.setLocalTranslation(0, -4.0f, 0); //FIXME make values non-arbitrary
        
        //create control and attach to player, add input, attach construct to root node
        playerCharacter = new OverworldCharacter((Spatial)player, this);
        playerCharacter.setPhysicsLocation(map.getSpawnPoint());
        playerCharacter.setPhysicsSpace(phys);
        playerCharacter.addInputListeners(sapp.getInputManager());
        
        
        //borrowed heavily from a previous project
        chaseCam = new ChaseCamera(sapp.getCamera(), characterModel);//, sapp.getInputManager()
        chaseCam.registerWithInput(sapp.getInputManager());
        chaseCam.setMinDistance(5.0f);
        chaseCam.setMaxDistance(40.0f);
        chaseCam.setDefaultDistance(30.0f);
        chaseCam.setDefaultVerticalRotation(FastMath.PI / 6);
        chaseCam.setMinVerticalRotation(FastMath.PI / 24);
        chaseCam.setDownRotateOnCloseViewOnly(false);
        chaseCam.setMaxVerticalRotation((2 * FastMath.PI) / 6);
        chaseCam.setLookAtOffset(new Vector3f(0, 7.25f, 0));
        chaseCam.setDragToRotate(false);
        chaseCam.setDefaultHorizontalRotation(FastMath.PI / 2 * 3); //start behind the player instead of in front
        
        playerCharacter.setCamera(sapp.getCamera());
        
        //palette sun code so we can actually see what we're doing
        /* A white, directional light source */ 
        sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        offSun = new DirectionalLight();
        offSun.setDirection(new Vector3f(0, -1, 0).normalizeLocal());
        offSun.setColor(ColorRGBA.White);
        
        phys.addAll(map.getMapScene());
        
        encTimer = map.nextEncounterTime();
        
        music = new AudioNode(sapp.getAssetManager(),map.getBGMPath(), AudioData.DataType.Buffer);
        music.setDirectional(false);
        music.setPositional(false);
        music.setLooping(true);
        music.setVolume(0.3f);
        
        float sWidth = Main.getSettings().getWidth();
        float sHeight = Main.getSettings().getHeight();
        
        
        
        
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
        
        playerCharacter.addInputListeners(sapp.getInputManager());
        
        music.play();
        
        this.setPaused(false);
    }
    
    /**
     * Disables movement and physics, then detaches elements from the scene graph
     */
    @Override
    protected void onDisable() {
        this.setPaused(true);
        
        music.stop();
        
        //remove spatials
        rootNode.detachChild(map.getMapScene());
        rootNode.detachChild(player);
        
        sapp.getInputManager().removeListener(playerCharacter);
        
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
        if(doGameOver){
            sapp.getStateManager().attach(new GameOverState(this));
            this.setEnabled(false);
            return;
        }
        else if(doNextMap){
            openMap(nextMap, nextMapSpawn);
            doNextMap = false;
            this.setPaused(true);
        }
        else if(doNextScript){
            startScript(nextScript);
            doNextScript = false;
        }
        else if(doNextEncounter){
            startEncounter(nextEncData);
            doNextEncounter = false;
        }
        if(playerCharacter.isWalking() == true && map.inEncounterArea(player.getLocalTranslation())){
            encTimer -= tpf;
            if(encTimer <= 0){
                EncounterData encdata = map.rollEncounter();
                if(encdata != null){
                    encdata.resetEncounterData();
                    prepStartEncounter(encdata);
                }
                encTimer = map.nextEncounterTime();
            }
        }
        
    }

    /**
     * Listens for the menu to be opened.
     * @param name name of the action that's pressed
     * @param pressed true if pressed, false otherwise
     * @param arg2 time per frame, unused
     */
    @Override
    public void onAction(String name, boolean pressed, float arg2) {
        if(!isPaused() && name.equals(Control.MENU_OPEN.name()) && pressed == true){
            menuState.setEnabled(true);
        }
    }
    
    public void prepStartEncounter(EncounterData encData){
        nextEncData = encData;
        doNextEncounter = true;
    }
    
    public void prepScript(String script){
        nextScript = script;
        doNextScript = true;
    }
    
    public void prepMap(String mapName, String spawnPoint){
        nextMap = mapName;
        nextMapSpawn = spawnPoint;
        doNextMap = true;
    }
    
    public void notifyGameOver(){
        doGameOver = true;
    }
    
    public void setPlayerLocation(Vector3f newLocation){
        playerCharacter.setPhysicsLocation(newLocation);
    }
    
    private void startEncounter(EncounterData encdata){
        BattleAppState bas = new BattleAppState(map.getBattleArena(), encdata, this);
        sapp.getStateManager().attach(new TransitionState(this, bas, false));
    }
    
    private void openMap(String map, String spawnPoint){
        OverworldMap nextMap = new OverworldMap(map, sapp.getAssetManager());
        OverworldAppState nextOas = new OverworldAppState(nextMap, spawnPoint);
        sapp.getStateManager().attach(new TransitionState(this, nextOas, true));
    }
    
    private void startScript(String script){
        sapp.getStateManager().attach(new OverworldScriptParser(this, script));
    }
    
    public void showStatusMessage(String message){
        sapp.getStateManager().attach(new StatusMessage(this, message));
    }
}
