/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import mygame.battle.BattleAppState;
import mygame.ngin.MyBaseAppState;
import mygame.gui.MenuOptions.BattleOption;


/**
 *
 * @author cameron
 */
public class BattleMenuState extends GenericMenuState<BattleOption>{
    private StatusMessage sam;
    
    public BattleMenuState(MyBaseAppState creator){
        super();
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
    }
    
    
    
}
