/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.gui;

import com.mygame.battle.BattleAppState;
import com.mygame.battle.Creature;
import com.mygame.battle.EnemyCreature;
import com.mygame.ngin.MyBaseAppState;
import java.util.Collection;

/**
 *
 * @author cameron
 */
public class CreatureMenuState extends GenericMenuState<Creature> {
    
    public CreatureMenuState(MyBaseAppState creator, Collection<Creature> enemies){
        super();
        super.creator = creator;
        populate(enemies.toArray(new Creature[enemies.size()]));
        for(int i=items.size() - 1; i >= 0; i--){
            if(items.get(i).isDead())
                items.remove(items.get(i));
        }
    }
    
    @Override
    protected String getStringForType(Creature type) {
        return type.getName();
    }

    @Override
    public void select(Creature type) {
        ((BattleAppState)creator).setSelectedCreature(type);
        this.setEnabled(false);
    }
    
    @Override
    public void onDisable(){
        super.onDisable();
        sapp.getStateManager().detach(this); //because creature lists can change rapidly
    }
    
    
    
    
}
