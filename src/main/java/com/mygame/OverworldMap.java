 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame;

import com.mygame.ngin.WeightedRandomTable;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.mygame.battle.BattleArena;
import com.mygame.ngin.JsonKey;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Encapsulates the data for an overworld map (which will be read in from a JSON file)
 * @author cameron
 */
public class OverworldMap {
    private static final String mapDataFile = "GameData/mapData.json";
    private static JSONObject mapData;
    private static boolean mapDataLoaded = false;
    
    private final String mapName;
    
    private Node mapNode;
    private Spatial mapScene;
    private Vector3f spawnPoint;
    private String BGMPath;
    private BattleArena arena;
    private WeightedRandomTable<EncounterData> encounterTable;
    
    /**
     * Statically loads the map data from the provided JSON files
     * @param am 
     */
    public static void loadMapData(AssetManager am){
        if(mapDataLoaded)
            return;
        else{
            mapData = am.loadAsset(new JsonKey(mapDataFile));
            if(mapData != null)
                mapDataLoaded = true;
            else{
                System.out.println("mapData.json not found - your install is broken");
            }
        }
    }
    
    /**
     * Obtains the spawn location using a specially named node in the map file.  If no such node is found,
     *  the spawn location is set to 10 units above (y-axis) the center of the map.
     */
    private void findSpawn(){
        Spatial spawnSpatial = ((Node)mapScene).getChild("SPAWNPOINT");
        if(spawnSpatial != null){
            spawnPoint = spawnSpatial.getLocalTranslation();
            spawnSpatial.setCullHint(Spatial.CullHint.Always);
        }
        else
            spawnPoint = Vector3f.ZERO.clone().addLocal(0,10,0);
    }
    
    
    /**
     * Constructor.  Uses the provided map name to locate the map data from the statically loaded JSON data.
     * @param name the internal name of the map, used to locate the data
     * @param am the asset manager, used to load the map file.
     */
    public OverworldMap(String name, AssetManager am){
        mapName = name;
        //load it from JSON
        JSONObject thisMapData = mapData.getJSONObject(mapName);
        if(thisMapData == null){
            throw new RuntimeException("Unable to load JSON data for " + mapName);
        }
        
        //Load the model, find spawn, attach to the scene graph
        this.mapScene = am.loadModel(thisMapData.getString("map"));
        findSpawn();
        mapNode = new Node("Map");
        mapNode.attachChild(mapScene);
        
        mapNode.attachChild(am.loadModel("Models/skybox.j3o"));
        
        this.arena = BattleArena.valueOf(thisMapData.getString("arena"));
        
        //Determine which parts of the map need collision ("are solid")
        JSONArray solid = thisMapData.getJSONArray("solid");
        for(Object s : solid){
            String str = (String)s;
            Spatial newCollision = mapNode.getChild(str);
            if(newCollision != null){
                MeshCollisionShape collision = CollisionShapeFactory.createMergedMeshShape(newCollision);
                RigidBodyControl rbc = new RigidBodyControl(collision, 0);
                newCollision.addControl(rbc);
            }
            else{
                System.err.println("[WARN] Could not add collision for " + str + " in map " + mapName);
            }
        }
        
        //Determine which parts of the map should be culled ("are not visible")
        JSONArray invisible = thisMapData.getJSONArray("invisible");
        for(Object s : invisible){
            String str = (String)s;
            Spatial invis = mapNode.getChild(str);
            if(invis != null){
                invis.setCullHint(Spatial.CullHint.Always);
            }
            else
                System.err.println("[WARN] Could not make " + str + " invisible in map " + mapName);
        }
        
        /*
        Spatial encounterArea = mapNode.getChild("EncounterArea");
        if(encounterArea != null){
            RandomEncounterControl rec = new RandomEncounterControl();
            
            encounterArea.addControl(rec);
            
        }*/
        //Load up the encounter data
        encounterTable = new WeightedRandomTable<>();
        
        JSONArray encounterParties = thisMapData.getJSONArray("encounters");
        for(Object o : encounterParties){
            JSONObject party = (JSONObject)o;
            EncounterData ed = new EncounterData(party, am);
            encounterTable.addElement(ed, ed.getEncounterRate());
        }
        
    }
    
    /**
     * @return the map scene node 
     */
    public Spatial getMapScene(){
        return mapNode;
    }
    
    /**
     * @return the spawn point, as a Vector3f 
     */
    public Vector3f getSpawnPoint(){
        return spawnPoint;
    }
    
    /**
     * Rolls an encounter.  The provided value is a percentile chance that there will be an encounter (from never to always)
     * @param chance the chance of an encounter - should be bounded between 0.0 and 1.0
     * @return the rolled encounter, or null if there was no encounter rolled.
     */
    public EncounterData rollEncounter(double chance){
        return encounterTable.pullEntry(chance);
    }
    
    /**
     * @return the enumerated battle arena 
     */
    public BattleArena getBattleArena(){
        return arena;
    }
    
}
