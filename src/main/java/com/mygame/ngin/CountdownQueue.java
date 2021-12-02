/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.ngin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author cameron
 * @param <T>
 */
public class CountdownQueue<T> {
    ArrayList<CountdownEntry<T>> queue = new ArrayList<>();
    
    private class CountdownEntry<T> implements Comparable<CountdownEntry<T>>{
        T entry;
        int counter;
        
        public CountdownEntry(T t, int counter){
            this.entry = t;
            this.counter = counter;
        }
        
        public boolean zero(){
            return counter <= 0;
        }
        
        
        public int getCount(){
            return counter;
        }
        
        /**
         * Set the counter to a new value.
         * @param newCount - the new counter value.
         */
        public void setCount(int newCount){
            counter = newCount;
        }
        
        /**
         * Get the generic object related to this count.
         * @return 
         */
        public T getEntry(){
            return entry;
        }

        /**
         * Implements comparable so that the collection in the queue can be kept sorted
         * @param other
         * @return 
         */
        @Override
        public int compareTo(CountdownEntry<T> other) {
            return counter - other.counter;
        }
    }
    
    public CountdownQueue(){
        queue = new ArrayList<>();
    }
    
    public void addToQueue(T t, int startCount){
        queue.add(new CountdownEntry<>(t, startCount));
    }
    
    protected int getIndex(T toFind){
        for(CountdownEntry<T> cde : queue){
            if(cde.getEntry() == toFind)
                return queue.indexOf(cde);
        }
        return -1;
    }
    
    public boolean remove(T toRemove){
        int index = getIndex(toRemove);
        if(index == -1)
            return false;
        queue.remove(index);
        return true;
    }
    
    public List<T> countdown(){
        Collections.sort(queue, Collections.reverseOrder());
        int count = queue.get(queue.size() - 1).getCount();
        
        return countdownSetNumber(count);
    }
    
    public List<T> countdownSetNumber(int count){
        ArrayList<T> ret = new ArrayList<>();
        
        for(int i = queue.size() - 1; i >= 0; i--){
            CountdownEntry<T> cde = queue.get(i);
            cde.setCount(cde.getCount() - count);
            
            if(cde.zero()){
                ret.add(cde.getEntry());
                queue.remove(cde);
            }
        }
        
        return ret;
    }
    
}
