/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import mygame.gui.InventoryMenu;
import java.util.ArrayList;

/**
 *
 * @author cameron
 */
public class Inventory {
    
    public class InventoryEntry{
        private Item item;
        private int qty;
        
        /**
         * Private constructor for entry of single item, single quantity
         * @param item 
         */
        private InventoryEntry(Item item){
            this(item, 1);
        }
        
        /**
         * Private constructor for entry of single item, known quantity.
         *  The constructors are private so that only the Inventory can
         *  construct inventory entries, but other classes (esp. inventory
         *  menu) can access their methods.
         * @param item
         * @param qty 
         */
        private InventoryEntry(Item item, int qty){
            this.item = item;
            this.qty = qty;
        }
        
        /**
         * Returns a reference to the item data
         * @return 
         */
        public Item getItem(){
            return item;
        }
        
        /**
         * Returns the number of a particular item that exist in this inventory
         * @return 
         */
        public int getQuantity(){
            return qty;
        }
        
        /**
         * Sets the quantity of an item in the inventory to a new amount
         * @param newQuantity 
         */
        public void setQuantity(int newQuantity){
            qty = newQuantity;
        }
        
    }
    
    private volatile ArrayList<InventoryEntry> items;
    private double money;
    
    /**
     * Basic default constructor, initializes an empty inventory
     */
    public Inventory(){
        items = new ArrayList<>();
    }
    
    /**
     * Search for an entry in the inventory based on the item reference.
     * @param newItem - the item reference to look for
     * @return the entry for the given item, or null if no such entry exists
     */
    private InventoryEntry getEntry(Item newItem){
        for(int i=0; i<items.size(); i++){
            if(items.get(i).getItem() == newItem){
                return items.get(i);
            }
        }
        return null;
    }
    
    /**
     * 
     * @param newItem
     * @param quantity
     * @return 
     */
    public boolean changeItemQuantity(Item newItem, int quantity){
        InventoryEntry entry = getEntry(newItem);
        if (quantity == 0){
            return true;
        }
        else if(entry != null){
            int currQuant = entry.getQuantity();
            if(currQuant >= (-1 * quantity)){
                entry.setQuantity(currQuant + quantity);
                if(entry.getQuantity() <= 0){
                    items.remove(entry);
                }
                return true;
            }
            else
                return false;
        }
        else if (quantity > 0){
            items.add(new InventoryEntry(newItem, quantity));
            return true;
        }
        
        return false;
    }
    
    /**
     * Sets the quantity of a given item in the inventory.
     *   If the quantity is less than 0, an error is thrown.
     *   If the quantity is 0, either the entry for that item is removed (if present)
     *     or none is created.
     *   If the quantity is greater than 0 and the entry does not exist, a new
     *     entry is created with that quantity.
     *   In all other cases, the quantity of the related entry is set to the new value.
     * @param newItem the reference to the item
     * @param quantity the number of this item to be set (>= 0)
     * @return 
     */
    public boolean setItemQuantity(Item newItem, int quantity){
        InventoryEntry entry = getEntry(newItem);
        
        if(quantity < 0){
            throw new RuntimeException("Tried to set an inventory item's quantity to a negative value");
        }
        
        if(entry == null && quantity > 0){
            items.add(new InventoryEntry(newItem, quantity));
        }
        else if (quantity > 0){
            entry.setQuantity(quantity);
        }
        else{
            items.remove(entry);
        }
        
        return true;
    }
    
    /**
     * Returns the quantity of the respective item, or 0 if no entry for that item exists
     * @param item
     * @return 
     */
    public int getQuantity(Item item){
        InventoryEntry entry = getEntry(item);
        if(entry == null)
            return 0;
        else
            return entry.getQuantity();
    }

    /**
     * Returns the nth item in the inventory, or null if the index is out of bounds.
     * @param index
     * @return 
     */
    public Item getNthInventoryItem(int index){
        if(index < items.size())
            return items.get(index).getItem();
        else return null;
    }
    
    /**
     * Gets the size (i.e. number of valid entries) of the inventory
     * @return 
     */
    public int getSize(){
        return items.size();
    }
    
    public ArrayList<InventoryEntry> getEntriesList(){
        return items;
    }
    
    public double getMoney(){
        return money;
    }
    
    public void modMoney(double difference){
        money += difference;
        if (money < 0)
            money = 0;
    }
    
}
