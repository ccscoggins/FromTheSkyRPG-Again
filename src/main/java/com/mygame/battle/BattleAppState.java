/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.battle;

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
import com.mygame.EncounterData;
import com.mygame.Main;
import com.mygame.PlayerData;
import com.mygame.Stat;
import com.mygame.gui.BattleMenuState;
import com.mygame.gui.CreatureMenuState;
import com.mygame.gui.MenuOptions;
import com.mygame.gui.StatusMessage;
import com.mygame.ngin.CountdownQueue;
import com.mygame.ngin.MyBaseAppState;
import java.util.ArrayDeque;
import java.util.List;

/**
 *
 * @author cameron
 */
public class BattleAppState extends MyBaseAppState{
    private double expAward = 0;
    private double cashAward = 0;
    private BattleArena arena;
    private volatile PlayerData pd;
    private EncounterData enc;
    
    private Spatial arenaModel;
    private AudioNode music;
    
    private Camera battleCam;
    
    private SimpleApplication sapp;
    //private Node rootNode;
    private Node battleNode;
    private BattleMenuState bms;
    
    private MenuOptions.BattleOption lastChoice;
    private Creature selectedCreature;
    
    private CountdownQueue<Creature> turnQueue;
    private List<Creature> currentTurnCreatures;
    private Creature currentCreature;
    
    private ArrayDeque<String> tickerQueue;
    
    public void enqueueTickerText(String newText){
        tickerQueue.addLast(newText);
    }
    
    
    public BattleAppState(BattleArena ba, EncounterData enc){
        arena = ba;
        this.enc = enc;
        
        turnQueue = new CountdownQueue<>();
        tickerQueue = new ArrayDeque<String>();
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
        for(PlayerCharacter pc : pd.party){
            if(pc != null){
                System.out.println("Looking up Agility for " + pc.getName());
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
        
        
        System.out.println("Initializing BattleAppState");
        
        battleNode = new Node("Battle Scene");
        
        try{
            arenaModel = sapp.getAssetManager().loadModel(arena.getModelPath());
        }
        catch(AssetNotFoundException ae){
            System.out.println("Unable to load intended arena: " + arena.getModelPath());
            arenaModel = sapp.getAssetManager().loadModel("Models/battleTestArena.j3o");
        }
        
        battleNode.attachChild(arenaModel);
        
        try{
            music = new AudioNode(sapp.getAssetManager(), enc.getBattleMusic(), AudioData.DataType.Buffer);
            music.setDirectional(false);
            music.setPositional(false);
            music.setLooping(true);
        }
        catch(AssetNotFoundException ae){
            System.out.println("Could not locate music asset: " + enc.getBattleMusic());
        }
        
        StringBuilder location = new StringBuilder(Placement.getEnemyString());
        for(Placement P : Placement.values()){
            EnemyCreature ec = (EnemyCreature)enc.getCreatureInLocation(P);
            if(ec != null){
                location.setLength(Placement.getEnemyString().length());
                location.append(".");
                location.append(P.name());
                Node target = (Node)battleNode.getChild(location.toString());
                //System.out.println("Placing " + ec.getName() + " at " + location.toString());
                //System.out.println(target);
                target.attachChild(ec.getBattleModel());
                ec.registerBattleState(this);
            }
        }
        
        pd = ((Main)sapp).getPlayerData();
        for(int i=0; i<3; i++){
            PlayerCharacter pc = pd.party[i];
            if(pc != null){
                Spatial playerCharacterModel = pc.getCharacterClass().getModel(); //fixme this doesn't allow for battle animations at the moment
                //In future, set up proper character model controllers
                location.setLength(0);
                location.append(Placement.getPlayerString());
                location.append(".");
                location.append(Placement.values()[i].name());
                System.out.println(location);
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
        
        bms = new BattleMenuState(this);
        bms.setEnabled(false);
        sapp.getStateManager().attach(bms);
    }

    @Override
    protected void cleanup(Application arg0) {
    }

    @Override
    protected void onEnable() {
        if (music != null)
            music.play();
        sapp.getRootNode().attachChild(battleNode);
        this.setPaused(false);
    }

    @Override
    protected void onDisable() {
        this.setPaused(true);
        if (music != null)
            music.pause();
        sapp.getRootNode().detachChild(battleNode);
    }
    
    @Override
    protected void onUnpause() {
        
    }

    @Override
    protected void onPause() {
        
    }
    
    @Override
    public void update(float tpf){
        for(Placement P : Placement.values()){
            Creature creature = enc.getCreatureInLocation(P);
            if(creature != null && !creature.isDead())
                creature.updateAnimation();
        }
        for(PlayerCharacter pc : pd.party){
            if(pc != null)
                pc.updateAnimation();
        }
        
        if(!isPaused()){
            if(!tickerQueue.isEmpty()){
                sapp.getStateManager().attach(new StatusMessage(this, tickerQueue.pop()));
                return;
            }
            
            if(currentCreature == null){
                if(currentTurnCreatures == null || currentTurnCreatures.isEmpty())
                    currentTurnCreatures = turnQueue.countdown();

                currentCreature = currentTurnCreatures.get(0);
                currentTurnCreatures.remove(currentCreature); //pop off the beginning, essentially - yeah it's not efficient sue me
            }
            
            if(currentCreature.isPlayer()){
                if(lastChoice == null)
                    bms.setEnabled(true);
                else{
                    switch(lastChoice){
                        case Fight:
                            if(selectedCreature == null){
                                sapp.getStateManager().attach(new CreatureMenuState(this, enc.getCreatureSet()));
                            }
                            else{
                                currentCreature.attack(selectedCreature);
                                if(selectedCreature.isDead()){
                                    turnQueue.remove(selectedCreature);
                                }
                                turnQueue.addToQueue(currentCreature, currentCreature.rollInitiative());
                                currentCreature = null;
                                selectedCreature = null;
                            }
                            break;
                        default:
                            sapp.getStateManager().attach(new StatusMessage(this, "Option not implemented"));
                    }
                }
            }
            else{
                Creature randomPlayerCharacter = pd.party[((int)Math.random() * 3) % pd.party.length];
                currentCreature.attack(randomPlayerCharacter);
                turnQueue.addToQueue(currentCreature, currentCreature.rollInitiative());
                currentCreature = null;
            }
        }
        //check victory/game over here
    }
    
    public void accumulateExp(double experience){
        expAward += experience;
    }
    
    public void accumulateCash(double cash){
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
