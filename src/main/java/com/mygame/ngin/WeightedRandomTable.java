/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.ngin;

import java.util.ArrayList;

/**
 * Generic implementation of weighted random item selection from a provided list
 * @author cameron
 * @param <RollType> - the type of the weighted random roll's result
 */

public class WeightedRandomTable<RollType> {
    
    /**
     * List entry with a corresponding weight
     * @param <RollType> - the type of the weighted random roll's result
     */
    private class WeightedEntry<RollType>{
        RollType result;
        double weight;
        
        /**
         * Constructor - associates the result entry and the weight value
         * @param result
         * @param weight 
         */
        public WeightedEntry(RollType result, double weight){
            this.result = result;
            this.weight = weight;
        }
        
        /**
         * Returns the result entry
         * @return 
         */
        public RollType getResult(){
            return result;
        }
        
        /**
         * Returns the weight value for the result
         * @return 
         */
        public double getWeight(){
            return weight;
        }
    }
    
    private final ArrayList<WeightedEntry<RollType>> rollTable;
    
    /**
     * Creates an empty weighted table
     */
    public WeightedRandomTable(){
        rollTable = new ArrayList<>();
    }
    
    /**
     * Adds an entry-weight to the table
     * @param entry
     * @param value 
     */
    public void addElement(RollType entry, double value){
        rollTable.add(new WeightedEntry<>(entry, value));
    }
    
    /**
     * Returns one weighted random result from the table with a 100% chance of occurance.
     * @return one entry from the table
     */
    public RollType pullEntry(){
        return pullEntry(1.0);
    }
    
    /**
     * As above, but with a definable chance of occurance.
     * @param totalChance - chance of returning a non-null result
     * @return either an entry from the table or null
     */
    public RollType pullEntry(double totalChance){
        double weightSum = rollTable.stream().mapToDouble(WeightedEntry::getWeight).sum();
        double roll = Math.random() * weightSum * (1 / (totalChance == 0 ? 1 : totalChance));
        
        for (WeightedEntry<RollType> e : rollTable){
            roll -= e.getWeight();
            if(roll < 0)
                return e.getResult();
        }
        // else return null
        return null;
    }
    
}
