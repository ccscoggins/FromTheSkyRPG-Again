/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import mygame.Inventory;
import mygame.Item;
import mygame.ngin.MyBaseAppState;
import java.util.ArrayList;

/**
 *
 * @author cameron
 */
public class InventoryMenu extends GenericMenuState<Inventory.InventoryEntry>{
    private boolean inBattle;
    private volatile ArrayList<Inventory.InventoryEntry> entries;
    
    private Item lastItem;
    
    public InventoryMenu(MyBaseAppState creator, Inventory inventory, boolean inBattle){
        super();
        super.creator = creator;
        this.inBattle = inBattle;
        
        entries = inventory.getEntriesList();
        this.populate(entries.toArray(new Inventory.InventoryEntry[entries.size()]));
        canClose = true;
    }
    
    
    @Override
    protected String getStringForType(Inventory.InventoryEntry ie) {
        StringBuilder sb = new StringBuilder();
        sb.append(ie.getItem().getName());
        sb.append(": ");
        sb.append(ie.getQuantity());
        return sb.toString();
    }

    @Override
    public void select(Inventory.InventoryEntry ie) {
        lastItem = ie.getItem();
        this.setEnabled(false);
    }
    
    public Item getLastSelectedItem(){
        Item ret = lastItem;
        lastItem = null;
        return ret;
    }
    
    
}
