/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.battle;

import com.jme3.anim.AnimComposer;
import com.jme3.math.FastMath;
import com.mygame.Main;
import com.mygame.Stat;
import java.util.EnumMap;

/**
 * Encapsulates the battle data of a player character
 * @author cameron
 */
public class PlayerCharacter extends AbstractCreature{

    private double experience;
    private int level;
    PlayerClass characterClass;
    
    
    private static double getBaseEXPForLevel(int level){
        return (level + 80.0) * level;
    }
    
    private static int getLevelForEXP(double exp){
        return (int)FastMath.floor(-40 + FastMath.sqrt((float)(1600 + exp)));
    }
    
    
    public PlayerCharacter(String name, PlayerClass cls, int level){
        characterClass = cls;
        this.level = level;
        this.experience = getBaseEXPForLevel(level);
        this.name = name;
        stat = new EnumMap<>(Stat.class);
        // create stats from level here
        for(Stat s : Stat.values()){
            stat.put(s, cls.getBaseStat(s) + cls.rollStatLevelUp(s, level - 1));
            System.out.println("Populated " + s.name() + " for " + name);
        }
        
        
        battleModel = cls.getModel();
        ac = Main.getNthChild(battleModel, 1).getControl(AnimComposer.class);
        setIdleAnimation(Creature.BattleAnims.BT_Idle.name());
    }
    
    public PlayerClass getCharacterClass(){
        return characterClass;
    }
    
    @Override
    public boolean isPlayer(){
        return true;
    }
}
