/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame;
import com.jme3.asset.AssetManager;
import com.mygame.battle.Creature;
import com.mygame.battle.EnemyCreature;
import com.mygame.battle.Placement;
import java.util.Collection;

import java.util.EnumMap;
import org.json.JSONObject;

/**
 * Encapsulates randomly selectable encounter data, including enemies, their positions in the battle screen,
 *   and the chance that this encounter will be selected out of possible encounters.
 * @author cameron
 */
public class EncounterData {
    private EnumMap<Placement, Creature> enemyData = new EnumMap<>(Placement.class);
    private static final String defaultBattleMusic = "";
    private String battleMusic;
    private float chance;
    
    /**
     * Constructs encounter data from the JSON data loaded from the map data
     * @param partyData
     * @param am
     */
    public EncounterData(JSONObject partyData, AssetManager am){
        for(Placement p : Placement.values()){
            if(partyData.has(p.toString())){
                enemyData.put(p, new EnemyCreature(partyData.getString(p.toString()), am));
            }
        }
        chance = partyData.getFloat("rate");
        battleMusic = partyData.getString("music");
    }
    
    /**
     * Resets the creatures in an encounter to nominal stats (i.e. full health).
     *   This allows one encounter data to be reused for all like encounters
     *   (that is to say, any time you fight three robots, you're fighting the same three robots).
     */
    public void resetEncounterData(){
        for(Placement p : Placement.values()){
            if(enemyData.containsKey(p))
                enemyData.get(p).resetHP();
        }
    }
    
    /**
     * Returns the creature in the placement position.  This can return null.
     * @param p - The position to look at, of the five possible enumerated positions
     * @return the creature in the position, or null if no such creature is present.
     */
    public Creature getCreatureInLocation(Placement p){
        return enemyData.get(p);
    }
    
    public Collection<Creature> getCreatureSet(){
        return enemyData.values();
    }
    
    /**
     * Returns the path of the battle music.
     * @return the path of the battle music
     */
    public String getBattleMusic(){
        return battleMusic;
    }
    
    /**
     * Returns the encounter chance, for building the random encounter table for each area.
     * @return the (non-normalized) chance of an encounter, relative to other encounters in the table
     */
    public float getEncounterRate(){
        return chance;
    }
}
