/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.ngin;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author cameron
 */
public class JsonDataLoader implements AssetLoader {

    @Override
    public Object load(AssetInfo arg0) throws IOException {
        JSONTokener tokener = new JSONTokener(arg0.openStream());
        JSONObject ret = new JSONObject(tokener);
        
        return ret;
    }
    
}
