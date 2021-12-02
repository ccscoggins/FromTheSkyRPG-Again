/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.mygame.battle.PlayerCharacter;
import java.io.IOException;

/**
 * Holds the data for the current player state.  Will be saveable to a file, but currently not yet supported.
 * @author cameron
 * @fixme convert to a single-instance object/singleton
 * @fixme implement saving and loading
 */
public class PlayerData {
    public PlayerCharacter party[] = new PlayerCharacter[3]; //FIXME make this more secure later
    
    
    
}
