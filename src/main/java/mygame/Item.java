/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import mygame.battle.Creature;
import mygame.battle.Effect;
import mygame.ngin.JsonKey;
import org.json.JSONObject;

/**
 * Contains the definition for usable items.
 * @author cameron
 */
public class Item {
    public enum ItemUsability{
        NONE,
        EQUIP,
        CONSUMABLE
    }
    
    private static JSONObject itemData;
    private static boolean itemDataLoaded = false;
    
    private String name;
    private ItemUsability useCases;
    private Effect effect;
    //private FieldScript fieldScript;
    private double magnitude;
    private int price;
    
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
    
    public Item(String name){
        this.name = name;
        
        JSONObject thisItemData = itemData.getJSONObject(name);
        useCases = ItemUsability.valueOf(thisItemData.getString("usability"));
        effect = Effect.valueOf(thisItemData.getString("effect"));
        magnitude = thisItemData.getNumber("magnitude").doubleValue();
        price = thisItemData.getNumber("price").intValue();
        
        
    }
    
    public String getName(){
        return name;
    }
    
    public boolean equals(Item i){
        return this.name.equals(i.name);
    }
    
    public void use(Creature target, boolean isInBattle){
        effect.apply(target, null, magnitude);
    }
    
}
