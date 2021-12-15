/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.battle;

import mygame.ngin.MyBaseAppState;
import mygame.ngin.TriConsumer;

/**
 * Defines special battle effects for unique types of skills, attacks, and other actions.
 *  Some effects may be usable outside of battle, such as healing
 * @author cameron
 */
public enum Effect {
    Heal(
        (target, self, magnitude) -> {
            target.applyDamage(-1 * magnitude);
        }
    );
    
    
    private static String lastEffect;
    public static String getLastEffect(){
        return lastEffect;
    }
    
    private final TriConsumer<Creature, Creature, Double> effect;
    private Effect(TriConsumer <Creature, Creature, Double> effect){
        this.effect = effect;
    }
    
    public void apply(Creature target, Creature self, Double magnitude){
        effect.apply(target, self, magnitude);
    }
    
}
