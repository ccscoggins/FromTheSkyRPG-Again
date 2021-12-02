/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.ngin;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;

/**
 *  Extension of BaseAppState class that adds an additional 'pause/unpause' mechanic similar to but distinct
 *  from enable/disable.
 * @author cameron 
 */
public abstract class MyBaseAppState extends BaseAppState {
    boolean paused = false;
    
    /**
     * Public method to set the 'paused' flag and run the onPause and onUnpause methods
     * @param pause 
     */
    public void setPaused(boolean pause){
        paused = pause;
        if(pause)
            onPause();
        else
            onUnpause();
        
    }
    
    /**
     * @return the paused state flag 
     */
    public boolean isPaused(){
        return paused;
    }
    
    protected abstract void onPause();
    
    protected abstract void onUnpause();
    
    
}
