/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import mygame.Main;
import mygame.PlayerData;
import mygame.gui.MenuOptions.FieldMenuOption;
import mygame.ngin.MyBaseAppState;


/**
 *
 * @author cameron
 */
public class FieldMenuState extends GenericMenuState <FieldMenuOption> {
    private StatusMessage sam;
    private InventoryMenu invmenu;
    private PlayerData pd;
    
    public FieldMenuState(MyBaseAppState creator){
        super();
        populate(FieldMenuOption.values());
        super.creator = creator;
        canClose = true;
    }
    
    @Override
    protected void initialize(Application arg0){
        super.initialize(arg0);
        setPosition(sWidth - width, sHeight - (2 * height));
        pd = ((Main)sapp).getPlayerData();
    }
    
    @Override
    protected String getStringForType(FieldMenuOption type) {
        return type.name();
    }

    @Override
    public void select(FieldMenuOption type) {
        switch(type){
            case Status:
                sapp.getStateManager().attach(new StatusMessage(this, pd.getPartyStatusPrintout().toArray(new String[0])));
                break;
            default:
                sapp.getStateManager().attach(new StatusMessage(this, "Feature not yet implemented."));
        }
    }
}
