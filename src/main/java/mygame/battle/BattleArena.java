/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.battle;

/**
 * Enumerates the battle 'arena' background models
 * @author cameron
 */
public enum BattleArena {
    ARENA_TEST("battleTestArena.j3o");
    private final String mapModelPath;
    
    private BattleArena(String path){
        mapModelPath = path;
    }
    
    public String getModelPath(){
        return "Models/" + mapModelPath;
    }
    
}
