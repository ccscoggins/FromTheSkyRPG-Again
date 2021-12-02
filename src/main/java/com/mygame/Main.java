package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetLoadException;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickConnectionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.mygame.battle.BattleAppState;
import com.mygame.battle.EnemyCreature;
import com.mygame.battle.PlayerCharacter;
import com.mygame.battle.PlayerClass;

/**
 * Main class
 * @author normenhansen <- shout outs for the gradle template
 */
public class Main extends SimpleApplication {
    private AppStateManager asm;
    private OverworldAppState overworldState;
    private OverworldMap mapdata; //relocalize when possible
    private static final AppSettings settings = new AppSettings(true);
    private PlayerData playerData;
    
    /**
     * Statically configures the application settings
     * @return 
     */
    private static AppSettings configureSettings(){
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("Title", "Untitled RPG");
        settings.setVSync(true);
        settings.setFrequency(60);
        return settings;
    }
    
    /**
     * Configures default controls for all inputs defined in the Control enum.
     *   Additionally disables the 'flycam' default controls.
     */
    private void configureInput(){
        InputManager im = this.getInputManager();
        /*im.addJoystickConnectionListener(new JoystickConnectionListener(){
            @Override
            public void onConnected(Joystick arg0) {
                System.out.println("Joystick connected: " + arg0.getName());
            }

            @Override
            public void onDisconnected(Joystick arg0) {
                System.out.println("Joystick disconnected: " + arg0.getName());
            }
        });*/
        for(Control c : Control.values()){
            im.addMapping(c.name(), new KeyTrigger(c.getDefaultKey()));
        }
        im.setCursorVisible(true);
        
        flyCam.setEnabled(false);
    }
    
    /**
     * Main application runner.  Creates the app, configures settings, runs the app.
     * @param args ignored
     */
    public static void main(String[] args) {
        Main app = new Main();
        app.setSettings(configureSettings());
        app.showSettings = false;
        app.start();
    }

    /**
     * Default constructor.  Finds the state manager to make later work easier.
     */
    public Main(){
        asm = this.getStateManager();
    }
    
    /**
     * Encapsulate loading all of the game data (stored in .json files).
     */
    private void initializeData(){
        this.getAssetManager().registerLoader(com.mygame.ngin.JsonDataLoader.class, "json");
        OverworldMap.loadMapData(this.getAssetManager());
        EnemyCreature.loadCreatureData(this.getAssetManager());
        Item.loadItemData(this.getAssetManager());
        PlayerClass.loadPlayerClassData(this.getAssetManager());
        
        
        try{
            guiFont = getAssetManager().loadFont("Textures/ui/Liberation-Serif.fnt");
        } catch (AssetLoadException ale){
            System.err.println("Unable to load custom font, some gui may look wrong.");
        }
    }
    
    /**
     * Initialize the application using JME hooks.  Calls configuration methods, creates the physics space,
     *  and alternatingly calls either the overworld or battle state depending on which is being used
     * @fixme Add a title screen
     */
    @Override
    public void simpleInitApp() {
        initializeData();
        configureInput();
        BulletAppState bas = new BulletAppState();
        //bas.setDebugEnabled(true);
        stateManager.attach(bas);
        mapdata = new OverworldMap("testisle", this.getAssetManager());
        //startOverworldState();
        startBattleState();
    }
    
    /**
     * Configures and starts the battle state
     */
    private void startBattleState(){
        System.out.println("Starting battle state");
        setupDemoPlayerData();
        BattleAppState battle = new BattleAppState(mapdata.getBattleArena(), mapdata.rollEncounter(1.0));
        asm.attach(battle);
        //flyCam.setEnabled(true);
        battle.enqueueTickerText("Wolves attacked!");
        battle.setEnabled(true);
        
    }
    
    /**
     * Configures and starts the overworld state
     */
    private void startOverworldState(){
        overworldState =  new OverworldAppState(mapdata);
        asm.attach(overworldState);
        overworldState.setEnabled(true);
    }
    
    /**
     * Sets up demo player data
     * @fixme modify to set up new game player data
     */
    private void setupDemoPlayerData(){
        PlayerCharacter demochar = new PlayerCharacter("Zeke", new PlayerClass("DemoPilot", this.getAssetManager()), 1);
        playerData = new PlayerData();
        playerData.party[0] = demochar;
    }
    
    /**
     * Empty method required to extend SimpleApplication
     * @param tpf 
     */
    @Override
    public void simpleUpdate(float tpf) {
        //System.out.println("location: " + cam.getLocation() + "; rotation: " + cam.getRotation());
    }
    
    /**
     * Empty method required to extend SimpleApplication
     * @param rm 
     */
    @Override
    public void simpleRender(RenderManager rm) {
        
    }
    
    /**
     * returns the gui font
     * @return the gui font
     * @fixme Modify other code so this becomes obsolete
     */
    public BitmapFont getGuiFont(){
        return super.guiFont;
    }
    
    /**
     * returns the active player data
     * @return the active player data
     * @fixme this implementation is also bad, figure out a way to make it a singleton or smth
     */
    public PlayerData getPlayerData(){
        return playerData;
    }
    
    /**
     * returns the application settings
     * @return the application settings
     */
    public static AppSettings getSettings(){
        return settings;
    }
    
    /**
     * Navigate the tree recursively, entering the first child node on each generation, until the desired
     *  generation depth is reached.
     * @param parent
     * @param generation
     * @return the spatial at the requested generation
     * @fixme this is incredibly inelegant and prone to misuse, and this isn't even a good place for this
     */
    public static Spatial getNthChild(Spatial parent, int generation){
        if(generation == 0)
            return parent;
        return getNthChild(((Node)parent).getChild(0), generation-1);
    }
}
