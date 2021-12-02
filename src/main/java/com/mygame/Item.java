/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame;

import com.jme3.asset.AssetManager;
import com.mygame.ngin.JsonKey;
import org.json.JSONObject;

/**
 * Contains the definition for usable items.
 * @author cameron
 */
public class Item {
    public enum ItemUsability{
        NONE,
        EQUIP,
        BATTLE,
        OVERWORLD,
        BOTH
    }
    
    private static JSONObject itemData;
    private static boolean itemDataLoaded = false;
    
    /**
     * Statically loads the data for all of the game's items into memory from the provided data file
     * @param am 
     */
    public static void loadItemData(AssetManager am){
        if(itemDataLoaded) return;
        itemData = am.loadAsset(new JsonKey("GameData/itemData.json"));
        if(itemData != null)
            itemDataLoaded = true;
        else
            System.err.println("Failed to load item data.");
    }
    
}
