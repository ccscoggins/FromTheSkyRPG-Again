/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.battle;

import com.jme3.anim.AnimComposer;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import mygame.Main;
import org.json.JSONObject;

import mygame.Stat;
import mygame.ngin.JsonKey;
import java.util.EnumMap;
import org.json.JSONArray;
/**
 *
 * @author cameron
 */
public class EnemyCreature extends AbstractCreature {
    private static final String creatureDataFile = "GameData/creatureData.json";
    private static JSONObject creatureData;
    private static boolean creatureDataLoaded = false;
    
    String idleAnim;
    String setAnim;
    String currentAnim;
    boolean playAnim = false;
    
    //private Spatial battleModel;
    
    public static void loadCreatureData(AssetManager am){
        if(creatureDataLoaded)
            return;
        else{
            creatureData = am.loadAsset(new JsonKey(creatureDataFile));
            if(creatureData != null)
                creatureDataLoaded = true;
            else{
                System.err.println("creatureData.json could not be loaded");
            }
        }
    }
    
    
    public EnemyCreature(String name, AssetManager am){
        super.name = name;
        super.stat = new EnumMap<>(Stat.class);
        JSONObject thisCreature = creatureData.getJSONObject(super.name);
        if(thisCreature == null){
            throw new RuntimeException("Unable to load JSON data for " + super.name);
        }
        
        battleModel = am.loadModel(thisCreature.getString("model"));
        JSONArray offsetArray = thisCreature.getJSONArray("offset");
        Vector3f localOffset = new Vector3f(offsetArray.getNumber(0).floatValue(), offsetArray.getNumber(1).floatValue(), offsetArray.getNumber(2).floatValue());
        float localScale = thisCreature.getFloat("scale");
        battleModel.setLocalTranslation(localOffset);
        battleModel.setLocalScale(localScale);
        
        for (Stat s : Stat.values()){
            Double d = thisCreature.getDouble(s.getShortName());
            super.stat.put(s,d);
        }
        super.baseExp = thisCreature.getDouble("exp");
        super.baseCash = thisCreature.getDouble("money");
        super.currentHP = super.maxHP = thisCreature.getDouble("maxHP");
        //super.skills = new ArrayList<>(); //skills not yet implemented for enemy creatures
        super.dead = false;
        
        ac = Main.getNthChild(battleModel, 1).getControl(AnimComposer.class);
        
        setIdleAnimation(Creature.BattleAnims.BT_Idle.name());
        
    }
    
    public Spatial getBattleModel(){
        return battleModel;
    }
    
    @Override
    public boolean isPlayer(){
        return false;
    }
    

    /**
     * Defeat a target creature - this is a placeholder
     * @param other 
     */
    @Override
    public void defeat(Creature other) {
        bonus += 0.1; //FIXME this rate might change
        activeBattleScene.enqueueTickerText(name + " grew stronger...");
        
    }
    
    
    /**
     * Be defeated by another creature - this is also a placeholder
     * @param other 
     */
    @Override
    public void beDefeatedBy(Creature other){
        //write to ticker
        activeBattleScene.enqueueTickerText(name + " was defeated!");
        activeBattleScene.accumulate(this.getExpValue(), baseCash);
        this.setVisible(false);
    }
    
}
