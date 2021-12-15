/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.battle;

import com.jme3.anim.AnimComposer;
import com.jme3.math.FastMath;
import mygame.Main;
import mygame.Stat;
import java.util.EnumMap;

/**
 * Encapsulates the battle data of a player character
 * @author cameron
 */
public class PlayerCharacter extends AbstractCreature{

    private double experience;
    private int level;
    private PlayerClass characterClass;
    
    private static double getBaseEXPForLevel(int level){
        return (level + 80.0) * level;
    }
    
    private static int getLevelForEXP(double exp){
        return (int)FastMath.floor(-40 + FastMath.sqrt((float)(1600 + exp)));
    }
    
    private void calcHP(){
        maxHP = stat.get(Stat.VITALITY) * 1.5;
        currentHP = maxHP;
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
        }
        calcHP() ;
        
        battleModel = cls.getModel();
        ac = Main.getNthChild(battleModel, 1).getControl(AnimComposer.class);
        setIdleAnimation(Creature.BattleAnims.BT_Idle.name());
    }
    
    public PlayerClass getCharacterClass(){
        return characterClass;
    }
    
    public boolean gainExperience(double modifier){
        experience += (modifier < 1) ? 1 : modifier;
        
        if(getLevelForEXP(experience) > level){
            for (Stat s : Stat.values()){
                stat.put(s, stat.get(s) + characterClass.rollStatLevelUp(s, getLevelForEXP(experience) - level));
            }
            calcHP();
            level = getLevelForEXP(experience);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isPlayer(){
        return true;
    }
    
    @Override
    public void defeat(Creature other){
        // do nothing
    }
    
    @Override
    public void beDefeatedBy(Creature other){
        activeBattleScene.enqueueTickerText(name + " was defeated!");
        playAnimation(Creature.BattleAnims.BT_Defeat.name());
        setIdleAnimation(Creature.BattleAnims.BT_DefeatIdle.name());
    }
    
    @Override
    public void setGuard(boolean guard){
        super.setGuard(guard);
        if(guard){
            this.playAnimation(Creature.BattleAnims.BT_StartGuard.name());
            this.setIdleAnimation(Creature.BattleAnims.BT_GuardIdle.name());
        }
        else{
            this.setIdleAnimation(Creature.BattleAnims.BT_Idle.name());
        }
    }
    
    public int getLevel(){
        return level;
    }
    
    public int getEXPtoNext(){
        return (int)(getBaseEXPForLevel(level + 1) - experience);
    }
    
}
