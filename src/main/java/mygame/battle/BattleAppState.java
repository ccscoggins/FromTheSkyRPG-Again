/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.battle;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import mygame.EncounterData;
import mygame.Main;
import mygame.OverworldAppState;
import mygame.PlayerData;
import mygame.Stat;
import mygame.gui.BattleMenuState;
import mygame.gui.BattleUI;
import mygame.gui.CreatureMenuState;
import mygame.gui.MenuOptions;
import mygame.gui.StatusMessage;
import mygame.ngin.CountdownQueue;
import mygame.ngin.MyBaseAppState;
import mygame.ngin.TransitionState;
import java.util.ArrayDeque;
import java.util.List;

/**
 *
 * @author cameron
 */
public class BattleAppState extends MyBaseAppState{
    private Spatial arenaModel;
    private AudioNode music;
    
    private volatile SimpleApplication sapp;
    private Node battleNode;
    private BattleMenuState bms;
    private volatile MyBaseAppState creator;
    private int pauseWait = 0;
    private BattleUI battleUI;
    
    private Camera battleCam;
    
    private double expAward = 0;
    private double cashAward = 0;
    private BattleArena arena;
    private volatile PlayerData pd;
    private EncounterData enc;
    private boolean scripted;
    
    
    private MenuOptions.BattleOption lastChoice;
    private Creature selectedCreature;
    
    private CreatureMenuState cms;
    private CountdownQueue<Creature> turnQueue;
    private List<Creature> currentTurnCreatures;
    private Creature currentCreature;
    private boolean battleOver;
    private boolean gameOver;
    
    private ArrayDeque<String> tickerQueue;
    
    private float avgEnemyAgi;
    private int runTries = 0;
    
    public void enqueueTickerText(String newText){
        tickerQueue.addLast(newText);
    }
    
    
    public BattleAppState(BattleArena ba, EncounterData enc, MyBaseAppState creator){
        this(ba, enc, creator, false);
    }
    
    public BattleAppState(BattleArena ba, EncounterData enc, MyBaseAppState creator, boolean scripted){
        arena = ba;
        this.enc = enc;
        this.creator = creator;
        
        turnQueue = new CountdownQueue<>();
        tickerQueue = new ArrayDeque<String>();
        
        this.scripted = scripted;
    }
    
    /**
     * Temporary solution for calculating turn order
     */
    private void populateQueue(){
        int min = 9999;
        for(Creature c : enc.getCreatureSet()){
            int turn = c.rollInitiative();
            turnQueue.addToQueue(c, turn);
            if(turn < min)
                min = turn;
        }
        for(int i=0; i<3; i++){
            PlayerCharacter pc = pd.getPartyMember(i);
            if(pc != null){
                int turn = pc.rollInitiative();
                turnQueue.addToQueue(pc, turn);
                if(turn < min)
                    min = turn;
            }
        }
        
        List<Creature> shouldBeEmpty = turnQueue.countdownSetNumber(min - 1);
        
        //if it broke, just stick the bleed back onto the front
        for(int i=0; i<shouldBeEmpty.size(); i++){
            turnQueue.addToQueue(shouldBeEmpty.get(i), i);
        }
    }
    
    @Override
    protected void initialize(Application arg0) {
        sapp = (SimpleApplication)arg0;
        
        
        battleNode = new Node("Battle Scene");
        
        try{
            arenaModel = sapp.getAssetManager().loadModel(arena.getModelPath());
        }
        catch(AssetNotFoundException ae){
            System.err.println("Unable to load intended arena: " + arena.getModelPath());
            arenaModel = sapp.getAssetManager().loadModel("Models/battleTestArena.j3o");
        }
        
        battleNode.attachChild(arenaModel);
        
        try{
            music = new AudioNode(sapp.getAssetManager(), enc.getBattleMusic(), AudioData.DataType.Buffer);
            music.setDirectional(false);
            music.setPositional(false);
            music.setLooping(true);
            music.setVolume(0.6f);
        }
        catch(AssetNotFoundException ae){
            System.err.println("Could not locate music asset: " + enc.getBattleMusic());
        }
        
        StringBuilder location = new StringBuilder(Placement.getEnemyString());
        for(Placement P : Placement.values()){
            EnemyCreature ec = (EnemyCreature)enc.getCreatureInLocation(P);
            if(ec != null){
                location.setLength(Placement.getEnemyString().length());
                location.append(".");
                location.append(P.name());
                Node target = (Node)battleNode.getChild(location.toString());
                target.attachChild(ec.getBattleModel());
                ec.registerBattleState(this);
            }
        }
        
        pd = ((Main)sapp).getPlayerData();
        for(int i=0; i<3; i++){
            PlayerCharacter pc = pd.getPartyMember(i);
            if(pc != null){
                Spatial playerCharacterModel = pc.getCharacterClass().getModel(); //fixme this doesn't allow for battle animations at the moment
                //In future, set up proper character model controllers
                location.setLength(0);
                location.append(Placement.getPlayerString());
                location.append(".");
                location.append(Placement.values()[i].name());
                Node target = (Node)battleNode.getChild(location.toString());
                pc.registerBattleState(this);

                target.attachChild(playerCharacterModel);
            }
            
        }
        
        battleNode.attachChild(sapp.getAssetManager().loadModel("Models/skybox.j3o"));
        
        battleCam = sapp.getCamera();
        battleCam.setLocation(new Vector3f(-5, 3, 7));
        battleCam.lookAt(arenaModel.getWorldTranslation().add(0,1.4f,0), Vector3f.UNIT_Y);
        
            /** A white, directional light source */ 
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        battleNode.addLight(sun);
        
        populateQueue();
        
        enqueueTickerText(enc.getEncounterText());
        
        battleUI = new BattleUI();
        sapp.getStateManager().attach(battleUI);
        
        cms = new CreatureMenuState(this, enc.getCreatureSet());
        cms.setEnabled(false);
        sapp.getStateManager().attach(cms);
        
        bms = new BattleMenuState(this);
        bms.setEnabled(false);
        sapp.getStateManager().attach(bms);
        
        runTries = 0;
    }

    @Override
    protected void cleanup(Application arg0) {
        sapp.getStateManager().detach(battleUI);
        sapp.getStateManager().detach(cms);
        sapp.getStateManager().detach(bms);
    }

    @Override
    protected void onEnable() {
        if (music != null)
            music.play();
            creator.setEnabled(false);
        //if(creator != null)
        //    creator.setEnabled(false);
        sapp.getRootNode().attachChild(battleNode);
        this.setPaused(false);
    }

    @Override
    protected void onDisable() {
        this.setPaused(true);
        if (music != null)
            music.pause();
        sapp.getRootNode().detachChild(battleNode);
        //if(creator != null)
        //    creator.setEnabled(true);
        sapp.getStateManager().detach(this);
    }
    
    @Override
    protected void onUnpause() {
        
    }

    @Override
    protected void onPause() {
        
    }
    
    private void doEndOfBattle(){
        pd.getInventory().modMoney(cashAward);
        enqueueTickerText(
                "You Win!\n" +
                "Obtained " + (int)cashAward + " gold!\n" +
                "Obtained " + (int)expAward + " experience!"
        );
        for(int i = 0; i < pd.getPartySize(); i++){
            boolean levelUp = pd.getPartyMember(i).gainExperience(expAward);
            if(levelUp)
                enqueueTickerText(pd.getPartyMember(i).getName() + " has leveled up!");
        }
    }
    
    private void updateBattleStatus(){
        boolean defeat = true, victory = true;
        
        int validCreatures = 0;
        avgEnemyAgi = 0;
        
        for(Placement P : Placement.values()){
            Creature creature = enc.getCreatureInLocation(P);
            if(creature != null && ! creature.isDead()){
                creature.updateAnimation();
                victory = false;
                validCreatures++;
                avgEnemyAgi += creature.getStat(Stat.AGILITY);
            }
        }
        
        avgEnemyAgi /= (validCreatures < 1 ? 1 : validCreatures);
        
        for(int i=0; i<3; i++){
            PlayerCharacter pc = pd.getPartyMember(i);
            if(pc != null){
                pc.updateAnimation();
                if(!pc.isDead()){
                    defeat = false;
                }
            }
        }
        if(defeat && !gameOver){
            gameOver = true;
            enqueueTickerText("The party was defeated.");
        } else if(victory && !battleOver){
            doEndOfBattle();
            battleOver = true;
        }
    }
    
    private void endTurn(Creature currentCreature){
        turnQueue.addToQueue(currentCreature, currentCreature.rollInitiative());
        this.currentCreature = null;
        selectedCreature = null;
        lastChoice = null;
    }
    
    @Override
    public void update(float tpf){
        updateBattleStatus();
        
        if(!isPaused()){
            if(pauseWait > 0){
                pauseWait--;
                return;
            }
            if(!tickerQueue.isEmpty()){
                sapp.getStateManager().attach(new StatusMessage(this, tickerQueue.pop()));
                return;
            }
            if(gameOver){
                ((OverworldAppState)creator).notifyGameOver();
                sapp.getStateManager().attach(new TransitionState(this, creator, false));
            }
            else if(battleOver){
                sapp.getStateManager().attach(new TransitionState(this, creator, false));
            }
            else{
                if(currentCreature == null){
                    if(currentTurnCreatures == null || currentTurnCreatures.isEmpty())
                        currentTurnCreatures = turnQueue.countdown();

                    currentCreature = currentTurnCreatures.get(0);
                    currentTurnCreatures.remove(currentCreature); //pop off the beginning, essentially - yeah it's not efficient sue me
                }

                if(currentCreature.isPlayer()){
                    if(currentCreature.isGuarding()){
                        currentCreature.setGuard(false);
                    }
                    if(lastChoice == null){
                        bms.setEnabled(true);
                    }
                    else{
                        switch(lastChoice){
                            case Fight:
                                selectedCreature = cms.getLastSelectedCreature();
                                if(selectedCreature == null){
                                    cms.setupCreatures(enc.getCreatureSet());
                                    cms.setEnabled(true);
                                }
                                else{
                                    currentCreature.attack(selectedCreature);
                                    if(selectedCreature.isDead()){
                                        turnQueue.remove(selectedCreature);
                                    }
                                    endTurn(currentCreature);
                                }
                                break;
                            case Guard:
                                currentCreature.setGuard(true);
                                enqueueTickerText(currentCreature.getName() + " is guarding.");
                                endTurn(currentCreature);
                                break;
                            case Run:{
                                enqueueTickerText(currentCreature.getName() + " tried to run for it");
                                float runChance = (float)(currentCreature.getStat(Stat.AGILITY) / avgEnemyAgi) * 0.5f + (0.1f * runTries);
                                boolean success = (!scripted && Math.random() < runChance);
                                if(success){
                                    enqueueTickerText("The party ran successfully!");
                                    battleOver = true;
                                }
                                else{
                                    enqueueTickerText("The party was unable to get away!");
                                    runTries++;
                                }
                                endTurn(currentCreature);
                                break;
                            }
                            default:
                                sapp.getStateManager().attach(new StatusMessage(this, "Option not implemented"));
                                lastChoice = null;
                        }
                    }
                }
                else{
                    Creature randomPlayerCharacter = pd.getPartyMember(((int)Math.random() * 3) % pd.getPartySize());
                    currentCreature.attack(randomPlayerCharacter);
                    if(randomPlayerCharacter.isDead()){
                        turnQueue.remove(selectedCreature);
                    }
                    turnQueue.addToQueue(currentCreature, currentCreature.rollInitiative());
                    currentCreature = null;
                }
            }
        }
    }
    
    public void accumulate(double experience, double cash){
        expAward += experience;
        cashAward += cash;
    }
    
    public void setOption(MenuOptions.BattleOption batop){
        lastChoice = batop;
    }
    
    public void setSelectedCreature(Creature ec){
        if(enc.getCreatureSet().contains(ec))
            selectedCreature = ec;
    }
}
