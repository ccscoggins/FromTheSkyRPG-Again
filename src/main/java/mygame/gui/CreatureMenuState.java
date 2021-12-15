/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import mygame.battle.BattleAppState;
import mygame.battle.Creature;
import mygame.ngin.MyBaseAppState;
import java.util.Collection;

/**
 *
 * @author cameron
 */
public class CreatureMenuState extends GenericMenuState<Creature> {
    private Creature selected;
    
    
    public CreatureMenuState(MyBaseAppState creator, Collection<Creature> creatures){
        super();
        super.creator = creator;
        setupCreatures(creatures);
    }
    
    @Override
    protected String getStringForType(Creature type) {
        return type.getName();
    }
    
    @Override
    protected void initialize(Application appl){
        super.initialize(appl);
        //System.out.println("Initialized creature menu");
    }
    
    
    @Override
    protected void cleanup(Application appl){
        super.initialize(appl);
        //System.out.println("Cleaned up creature menu");
    }
    
    @Override
    protected void onEnable(){
        super.onEnable();
        //System.out.println("Enabled creature menu");
    }
    
    @Override
    protected void onDisable(){
        super.onDisable();
        //System.out.println("Disabled creature menu");
    }

    @Override
    public void select(Creature type) {
        selected = type;
        this.setEnabled(false);
    }
    
    public void setupCreatures(Collection<Creature> creatures){
        populate(creatures.toArray(new Creature[creatures.size()]));
    }
    
    public Creature getLastSelectedCreature(){
        Creature ret = selected;
        selected = null;
        return ret;
    }
    
}
