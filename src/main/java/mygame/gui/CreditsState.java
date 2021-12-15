/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.asset.AssetManager;
import com.jme3.ui.Picture;
import mygame.ngin.JsonKey;
import java.util.ArrayList;
import org.json.JSONObject;

/**
 *
 * @author cameron
 */
public class CreditsState extends TextCrawl {
    private static JSONObject creditsData;
    private static boolean creditsLoaded = false;
    
    private ArrayList<Picture> logos;
    
    public static void loadCreditsFile(AssetManager am){
        if(creditsLoaded)
            return;
        else{
            creditsData = am.loadAsset(new JsonKey("GameData/credits.json"));
            if(creditsData!=null){
                creditsLoaded = true;
            }
            else{
                System.err.println("Could not load credits.");
            }
        }
    }
    
    private void populate(AssetManager am){
        
    }
    
    public CreditsState(AssetManager am){
        populate(am);
    }
    
    
    
    
}
