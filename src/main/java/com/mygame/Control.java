/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame;
import com.jme3.input.KeyInput;

/**
 * Encapsulates definitions for user input controls, as well as default key associations.
 * @author cameron
 */
public enum Control {
    //Overworld Controls
    FORWARD(KeyInput.KEY_W),
    BACKWARD(KeyInput.KEY_S),
    STRAFE_L(KeyInput.KEY_A),
    STRAFE_R(KeyInput.KEY_D),
    INTERACT(KeyInput.KEY_SPACE),
    RUN(KeyInput.KEY_LSHIFT),
    //UI Controls
    MENU_OPEN(KeyInput.KEY_RETURN),
    CONFIRM(KeyInput.KEY_SPACE),
    BACK(KeyInput.KEY_BACK),
    MENU_UP(KeyInput.KEY_W),
    MENU_DOWN(KeyInput.KEY_S),
    MENU_LEFT(KeyInput.KEY_A),
    MENU_RIGHT(KeyInput.KEY_D);
    //This is defined above for each enum member; it makes setting up the inputs initially easy, but
    // the inputs are not currently remappable.  This might be fixed if we have time.
    private final int defaultKey;
    
    private Control(int ki){
        defaultKey = ki;
    }
    
    /**
     * Returns the default key mapping for the selected action.
     * @return KeyInput value corresponding to the key mapping.
     */
    public int getDefaultKey(){
        return defaultKey;
    }
}
