/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import mygame.battle.PlayerCharacter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Holds the data for the current player state.  Will be saveable to a file, but currently not yet supported.
 * @author cameron
 * @fixme convert to a single-instance object/singleton
 * @fixme implement saving and loading
 */
public class PlayerData {
    private volatile PlayerCharacter party[] = new PlayerCharacter[3]; //FIXME make this more secure later
    private volatile HashMap<String, Integer> questFlags;
    private volatile Inventory playerInv;
    
    public PlayerData(){
        questFlags = new HashMap<>();
        playerInv = new Inventory();
    }
    
    public void setPartyMember(PlayerCharacter pc, int index){
        if(index >= 0 && index < party.length)
            party[index] = pc;
        else
            throw new RuntimeException("Index " + index + " out of range for player party.");
    }
    
    public PlayerCharacter getPartyMember(int index){
        if(index >= 0 && index < party.length && party[index] != null){
            return party[index];
        }
        else return null;
    }
    
    public int getPartySize(){
        int size = 0;
        for(int i=0; i<3; i++){
            if(party[i] != null)
                ++size;
        }
        return size;
    }
    
    public Inventory getInventory(){
        return playerInv;
    }
    
    public int getQuestFlag(String flagID){
        Integer ret = questFlags.get(flagID);
        if(ret == null)
            return -1;
        else
            return ret;
    }
    
    public void setQuestFlag(String flagID, int value){
        questFlags.put(flagID, value);
    }
    
    public List<String> getPartyStatusPrintout(){
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder builder;
        for(int i=0; i<3; i++){
            if(party[i] != null){
                PlayerCharacter pc = party[i];
                builder = new StringBuilder();
                builder.append(pc.getName());
                builder.append(":\t");
                
                builder.append("Class: ");
                builder.append(pc.getCharacterClass().getName());
                
                builder.append("\tLevel: ");
                builder.append(pc.getLevel());
                
                builder.append("\tNext: ");
                builder.append(pc.getEXPtoNext());
                
                builder.append("\n");
                
                int counter = 0;
                for(Stat s : Stat.values()){
                    builder.append(s.getShortName());
                    builder.append(": ");
                    builder.append((int)(pc.getStat(s)));
                    if(++counter != 6){
                        builder.append("\t");
                    }
                }
                ret.add(builder.toString());
            }
        }
        
        return ret;
    }
    
}
