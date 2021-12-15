/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;
import com.jme3.asset.AssetManager;
import mygame.battle.Creature;
import mygame.battle.EnemyCreature;
import mygame.battle.Placement;
import java.util.Collection;

import java.util.EnumMap;
import java.util.ArrayList;
import org.json.JSONObject;

/**
 * Encapsulates randomly selectable encounter data, including enemies, their positions in the battle screen,
 *   and the chance that this encounter will be selected out of possible encounters.
 * @author cameron
 */
public class EncounterData {
    private EnumMap<Placement, Creature> enemyData = new EnumMap<>(Placement.class);
    private static final String defaultBattleMusic = "Audio/to_the_front.ogg";
    private static final String defaultVictoryMusic = "";
    private static final String defaultEncounterText = "Enemies appeared!";
    private String battleMusic;
    private float chance;
    private String encounterText;
    
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
        if(partyData.has("music"))
            battleMusic = partyData.getString("music");
        else
            battleMusic = defaultBattleMusic;
        
        if(partyData.has("encounter_text"))
            encounterText = partyData.getString("encounter_text");
        else
            encounterText = defaultEncounterText;
    }
    
    /**
     * Resets the creatures in an encounter to nominal stats (i.e. full health).
     *   This allows one encounter data to be reused for all like encounters
     *   (that is to say, any time you fight three robots, you're fighting the same three robots).
     */
    public void resetEncounterData(){
        for(Placement p : Placement.values()){
            if(enemyData.containsKey(p)){
                Creature current = enemyData.get(p);
                current.resetHP();
                current.clearAnimation();
                current.setVisible(true);
            }
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
        ArrayList<Creature> values = new ArrayList<>(enemyData.values());
        for(Creature c : enemyData.values()){
            if(c.isDead())
                values.remove(c);
        }
        
        return values;
    }
    
    /**
     * Returns the path of the battle music.
     * @return the path of the battle music
     */
    public String getBattleMusic(){
        return battleMusic;
    }
    
    public String getVictoryMusic(){
        return defaultVictoryMusic; //may implement per-battle victory music later
    }
    
    /**
     * Returns the encounter chance, for building the random encounter table for each area.
     * @return the (non-normalized) chance of an encounter, relative to other encounters in the table
     */
    public float getEncounterRate(){
        return chance;
    }
    
    public String getEncounterText(){
        return encounterText;
    }
}
