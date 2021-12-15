/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.ngin;

import com.jme3.asset.AssetKey;
import org.json.JSONObject;

/**
 *
 * @author cameron
 */
public class JsonKey extends AssetKey<JSONObject> {
    
    public JsonKey(){
        super();
    }
    
    public JsonKey(String name){
        super(name);
    }
}
