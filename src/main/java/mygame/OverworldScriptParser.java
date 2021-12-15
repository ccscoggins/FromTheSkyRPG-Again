/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import mygame.EncounterData;
import mygame.Item;
import mygame.Main;
import mygame.OverworldAppState;
import mygame.PlayerData;
import mygame.gui.StatusMessage;
import mygame.ngin.JsonKey;
import mygame.ngin.MyBaseAppState;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cameron
 */
public class OverworldScriptParser extends MyBaseAppState{
    private enum ScriptAction{
        MESSAGE,
        MAP,
        MOVE,
        ITEMCHECK,
        ITEMADD,
        ITEMREMOVE,
        FLAGCHECK,
        FLAGSET,
        BATTLE
    }
    
    private enum Comparator{
        EQ,
        NE,
        LT,
        GT
    }
    
    
    private volatile SimpleApplication sapp;
    private volatile PlayerData pd;
    
    OverworldAppState creator;
    
    private static JSONObject scriptData;
    private static boolean scriptDataLoaded = false;
    
    private JSONArray thisScript;
    private int currentScriptIndex;
    private JSONObject currentScriptEvent;
    private boolean endScript;
    
    public static void loadScriptData(AssetManager am){
        if(scriptDataLoaded)
            return;
        else{
            scriptData = am.loadAsset(new JsonKey("GameData/worldScripts.json"));
            if(scriptData != null){
                scriptDataLoaded = true;
            }
            else{
                System.err.println("Unable to load script data.");
            }
        }
    }
    
    public OverworldScriptParser(OverworldAppState creator, String scriptName){
        this.creator = creator;
        thisScript = scriptData.getJSONArray(scriptName);
        
    }
    
    
    @Override
    protected void initialize(Application arg0) {
        sapp = (SimpleApplication)arg0;
        currentScriptIndex = 0;
        pd = ((Main)sapp).getPlayerData();

    }

    @Override
    protected void cleanup(Application arg0) {
        
    }

    @Override
    protected void onEnable() {
        creator.setPaused(true);
        this.setPaused(false);
    }

    @Override
    protected void onDisable() {
        this.setPaused(true);
        creator.setPaused(false);
        sapp.getStateManager().detach(this);
    }

    @Override
    protected void onUnpause() {
        
    }
    
    @Override
    protected void onPause() {
        
    }
    
    @Override
    public void update(float tpf){
        
        if(!isPaused()){
            System.out.println("current index: " + currentScriptIndex + "; length: " + thisScript.length());
            if(endScript || currentScriptIndex >= thisScript.length()){
                this.setEnabled(false);
                return;
            }
            currentScriptEvent = thisScript.getJSONObject(currentScriptIndex);
            ScriptAction action = ScriptAction.valueOf(currentScriptEvent.getString("action"));
            JSONArray args = currentScriptEvent.getJSONArray("arguments");
            
            switch (action){
                case MESSAGE:{
                    ArrayList<String> message = new ArrayList<>();
                    for(Object o : args){
                        message.add(o.toString());
                    }
                    sapp.getStateManager().attach(new StatusMessage(this, message.toArray(new String[0])));
                    
                    break;
                }
                case MAP:{
                    String mapTarget = args.getString(0);
                    String tgtNode = null;
                    if(args.length() > 1){
                        tgtNode = args.getString(1);
                    }
                    creator.prepMap(mapTarget, (tgtNode == null) ? "SPAWNPOINT" : tgtNode);
                    this.setEnabled(false);
                    break;
                }
                case MOVE:{
                    float x = args.getNumber(0).floatValue();
                    float y = args.getNumber(1).floatValue();
                    float z = args.getNumber(2).floatValue();
                    creator.setPlayerLocation(new Vector3f(x, y, z));
                    break;
                }
                case ITEMCHECK:{
                    String itemName = args.getString(0);
                    boolean ret = pd.getInventory().getQuantity(new Item(itemName)) > 0;
                    if(!ret){
                        creator.prepScript(args.getString(1));
                        this.setEnabled(false);
                    }
                    break;
                }
                case ITEMADD:{
                    String itemName = args.getString(0);
                    int quantity = args.getNumber(1).intValue();
                    pd.getInventory().changeItemQuantity(new Item(itemName), quantity);
                    break;
                }
                case ITEMREMOVE:{
                    String itemName = args.getString(0);
                    int quantity = args.getNumber(1).intValue();
                    pd.getInventory().changeItemQuantity(new Item(itemName), -1 * quantity);

                    break;
                }
                case FLAGCHECK:{
                    String flagName = args.getString(0);
                    Comparator cp = Comparator.valueOf(args.getString(1));
                    int stage = args.getNumber(2).intValue();
                    boolean ret;
                    switch(cp){
                        case EQ:
                            ret = pd.getQuestFlag(flagName) == stage;
                            break;
                        case NE:
                            ret = pd.getQuestFlag(flagName) != stage;
                            break;
                        case LT:
                            ret = pd.getQuestFlag(flagName) < stage;
                            break;
                        case GT:
                            ret = pd.getQuestFlag(flagName) > stage;
                            break;
                        default:
                            ret = false;
                    }
                    
                    if(!ret){
                        creator.prepScript(args.getString(3));
                        this.setEnabled(false);
                    }
                    break;
                }
                case FLAGSET:{
                    String flagName = args.getString(0);
                    int stage = args.getNumber(1).intValue();
                    pd.setQuestFlag(flagName, stage);
                    break;
                }
                case BATTLE:
                    JSONObject enc = args.getJSONObject(0);
                    creator.prepStartEncounter(new EncounterData(enc, sapp.getAssetManager()));
                    break;
                default:
                    sapp.getStateManager().attach(new StatusMessage(this, "Not yet implemented"));
            }
            ++currentScriptIndex;
            
            
        }
        
    }
    
}
