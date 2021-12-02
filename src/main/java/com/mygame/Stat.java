/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame;

/**
 * Defines the basic statistics that govern the combat system.
 * @author cameron
 */
public enum Stat {
    STRENGTH("STRN"),
    AGILITY("AGIL"),
    PERCEPTION("PERC"),
    WILLPOWER("WILL"),
    VITALITY("VITL"),
    CONNECTION("CONN");
    
    private final String shortName;
    
    private Stat(String shortName){
        this.shortName = shortName;
    }
    
    /**
     * @return the stat's short name, used in data files and for brief displays 
     */
    public String getShortName(){
        return this.shortName;
    }
}
