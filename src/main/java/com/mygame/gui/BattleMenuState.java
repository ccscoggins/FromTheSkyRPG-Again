/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.gui;

import com.mygame.battle.BattleAppState;
import com.mygame.ngin.MyBaseAppState;
import com.mygame.gui.MenuOptions.BattleOption;


/**
 *
 * @author cameron
 */
public class BattleMenuState extends GenericMenuState<BattleOption>{
    private StatusMessage sam;
    
    public BattleMenuState(MyBaseAppState creator){
        super(-1, 120);
        populate(BattleOption.values());
        super.creator = creator;
    }
    
    @Override
    protected String getStringForType(BattleOption type) {
        return type.name();
    }

    @Override
    public void select(BattleOption type) {
        ((BattleAppState)creator).setOption(type);
        this.setEnabled(false);
        /*switch(type){
            default:
                sam = new StatusMessage(this, "Nope, sorry chief");
                sapp.getStateManager().attach(sam);
        }*/
    }
    
    
    
}
