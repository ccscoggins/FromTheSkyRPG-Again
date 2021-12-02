/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.gui;

import com.jme3.app.Application;
import com.mygame.gui.MenuOptions.FieldMenuOption;
import com.mygame.ngin.MyBaseAppState;


/**
 *
 * @author cameron
 */
public class FieldMenuState extends GenericMenuState <FieldMenuOption> {
    private StatusMessage sam;
    
    public FieldMenuState(MyBaseAppState creator){
        super();
        populate(FieldMenuOption.values());
        super.creator = creator;
    }
    
    @Override
    protected void initialize(Application arg0){
        super.initialize(arg0);
        setPosition(sWidth - width, sHeight - (2 * height));
    }
    
    @Override
    protected String getStringForType(FieldMenuOption type) {
        return type.name();
    }

    @Override
    public void select(FieldMenuOption type) {
        switch(type){
            default:
                sam = new StatusMessage(this, "Feature not yet implemented.");
                sapp.getStateManager().attach(sam = new StatusMessage(this, "Feature not yet implemented."));
        }
    }
}
