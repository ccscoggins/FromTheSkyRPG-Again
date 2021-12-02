/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.battle;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import com.mygame.Stat;
import com.mygame.ngin.JsonKey;
import java.util.EnumMap;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Encapsulates the characteristics of a class of player character, including stat growths and battle model.
 * @author cameron
 */
public class PlayerClass {
    private static JSONObject classData;
    private static boolean classDataLoaded = false;
    
    private final EnumMap<Stat, Double> baseStat;
    private final EnumMap<Stat, Double> levelupMinima;
    private final EnumMap<Stat, Double> levelupMaxima;
    //private HashMap<Integer, BattleSkill> skills;
    
    private Spatial model;
    
    public static void loadPlayerClassData(AssetManager am){
        if(classDataLoaded) return;
        classData = am.loadAsset(new JsonKey("GameData/playerClassData.json"));
        if(classData != null)
            classDataLoaded = true;
        else
            System.err.println("An error occurred while loading player class data");
    }
    
    public PlayerClass(String name, AssetManager am){
        levelupMinima = new EnumMap<>(Stat.class);
        levelupMaxima = new EnumMap<>(Stat.class);
        baseStat = new EnumMap<>(Stat.class);
        JSONObject thisClass = classData.getJSONObject(name);
        if(thisClass != null){
            for(Stat s : Stat.values()){
                
                baseStat.put(s, thisClass.getJSONObject("base_stats").getNumber(s.getShortName()).doubleValue());
                
                JSONArray statPair = thisClass.getJSONObject("growths").getJSONArray(s.getShortName());
                levelupMinima.put(s, statPair.getNumber(0).doubleValue());
                levelupMaxima.put(s, statPair.getNumber(1).doubleValue());
            }
            model = am.loadModel(thisClass.getString("model"));
            model.setLocalScale(thisClass.getNumber("scale").floatValue());
        }
    }
    
    public double getBaseStat(Stat s){
        return baseStat.get(s);
    }
    
    public double rollStatLevelUp(Stat s, int levels){
        double sum = 0;
        for(int i=0; i < levels; i++){
            sum += (int)((Math.random() * levelupMaxima.get(s) - levelupMinima.get(s)) + levelupMinima.get(s));
        }
        return sum;
    }
    
    public double rollStatLevelUp(Stat s){
        return rollStatLevelUp(s, 1);
    }
    
    public Spatial getModel(){
        return model;
    }
    
    
    
    
    
    
}
