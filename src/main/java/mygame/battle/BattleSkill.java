/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.battle;

import com.jme3.asset.AssetManager;
import mygame.ngin.JsonKey;
import org.json.JSONObject;

/**
 *
 * @author cameron
 */
public class BattleSkill {
    Effect effect;
    double baseMagnitude;
    
    private static boolean skillDataLoaded = false;
    private static JSONObject skillData;
    
    /**
     * Statically loads the data for all of the game's battle skills into memory from the provided data file
     * @param am 
     */
    public static void loadSkillData(AssetManager am){
        if(skillDataLoaded) return;
        skillData = am.loadAsset(new JsonKey("GameData/skillData.json"));
        if(skillData != null)
            skillDataLoaded = true;
        else
            System.err.println("Failed to load battle skill data.");
    }
    
    public BattleSkill(String name){
        
    }
    
    /**public BattleSkill(Effect effect, double mag){
        this.effect = effect;
        this.baseMagnitude = mag;
    }*/
    
    public boolean apply(Creature target, Creature user){
        effect.apply(target, user, baseMagnitude);
        if(target.isDead()){
            user.defeat(target);
            target.beDefeatedBy(user);
            return true;
        }
        else
            return false;
    }
}
