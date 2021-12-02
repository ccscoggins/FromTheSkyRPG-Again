/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.battle;

/**
 *
 * @author cameron
 */
public class BattleSkill {
    BattleEffect effect;
    double baseMagnitude;
    
    public BattleSkill(BattleEffect effect, double mag){
        this.effect = effect;
        this.baseMagnitude = mag;
    }
    
    public boolean apply(Creature target, Creature user){
       return effect.applyEffect(target, user, baseMagnitude);
    }
}
