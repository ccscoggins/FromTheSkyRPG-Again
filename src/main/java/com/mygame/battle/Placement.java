/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.battle;

/**
 * Enumeration for party placement.  The player's party does not use BACK_LEFT or BACK_RIGHT
 * @author cameron
 */
public enum Placement {
    FRONT_CENTER,
    FRONT_LEFT,
    FRONT_RIGHT,
    BACK_LEFT,
    BACK_RIGHT;
    private static final String enemyString = "enemyTarget";
    private static final String playerString = "playerTarget";
    
    public static String getPlayerString(){
        return playerString;
    }
    
    public static String getEnemyString(){
        return enemyString;
    }
}
