/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author cameron
 */
public class ScriptControl extends AbstractControl {
    private String scriptName;
    private boolean solid; // if yes, must press interact; if no, must walk into trigger
    private boolean visible;

    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager arg0, ViewPort arg1) {
        
    }
    
    public String getScript(){
        return scriptName;
    }
    
    
}
