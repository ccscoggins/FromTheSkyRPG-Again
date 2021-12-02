/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.battle;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.action.Action;
import com.jme3.scene.Spatial;
import com.mygame.Stat;
import java.util.EnumMap;

/**
 * A partial implementation of the Creature interface.  Missing elements that differ between player characters
 *  and enemies.
 * @author cameron
 */
public abstract class AbstractCreature implements Creature {
    protected String name;
    protected double baseExp;
    protected double baseCash;
    protected double bonus = 1.0;
    protected double currentHP, maxHP;
    protected EnumMap<Stat, Double> stat;
    protected BattleSkill baseAttack;
    //protected ArrayList<BattleSkill> skills;
    protected boolean dead;
    
    protected BattleAppState activeBattleScene;
    
    protected AnimComposer ac; //NOT SET BY THIS CLASS'S METHODS,I know it's bad form but the location differs
    protected Spatial battleModel;
    private String idleAnim, setAnim, currentAnim;
    private boolean playAnim;
    private double lastTime = 0;
    
    /**
     * @return the creature's name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the value a creature has in a given stat
     * @param s - the statistic requested
     * @return the value corresponding to s
     */
    @Override
    public double getStat(Stat s) {
        Double ret = stat.get(s);
        if(ret == null){
            System.out.println("Error: creature " + this.getName() + " has no value for " + s.getShortName());
            return 0.0; //simple default value
        }
        else return ret * bonus;
    }

    /**
     * Deals damage to the other creature
     * @param other - the target creature
     */
    @Override
    public void attack(Creature other) {
        if(baseAttack != null){
            baseAttack.apply(other, this);
        }
        else{
            //Print a message to the ticker
            
            this.playAnimation(Creature.BattleAnims.BT_Attack.name());
            other.playAnimation(Creature.BattleAnims.BT_Hit.name());
            
            double baseDamage = this.getStat(Stat.STRENGTH) - other.getStat(Stat.VITALITY);
            double realDamage = baseDamage < 1 ? 1 : baseDamage; //minimize damage to 1
            activeBattleScene.enqueueTickerText(name + " attacks " + other.getName());
            boolean didKill = other.applyDamage(realDamage); 
            if(didKill){
                this.defeat(other);
                other.beDefeatedBy(this);
            }
        }
            
    }

    /**
     * Use a particular skill on the target creature
     * @param other - the target creature
     * @param skill - the skill to use
     */
    @Override
    public void useSkill(Creature other, BattleSkill skill) {
        skill.apply(other, this);
    }

    /**
     * Defeat a target creature - this is a placeholder
     * @param other 
     */
    @Override
    public void defeat(Creature other) {
        bonus += 0.1; //FIXME this rate might change
    }
    
    /**
     * Be defeated by another creature - this is also a placeholder
     * @param other 
     */
    @Override
    public void beDefeatedBy(Creature other){
        //write to ticker
        activeBattleScene.enqueueTickerText(name + " was defeated!");
        activeBattleScene.accumulateExp(this.getExpValue());
    }

    /**
     * Find the experience value of this creature
     * @return the adjusted exp value, after applying the bonus
     */
    @Override
    public double getExpValue() {
        return baseExp * bonus;
    }

    /**
     * @return the current HP value of the creature
     */
    @Override
    public double getHPValue() {
        return currentHP;
    }

    /**
     * @return the maximum HP value of the creature 
     */
    @Override
    public double getHPMaxValue() {
        return maxHP;
    }
    
    /**
     * apply incoming damage.  Negative values are translated as healing, while zero values are ignored.
     * @param damage
     * @return whether the incoming damage defeated this creature.
     */
    public boolean applyDamage(double damage){
        if(damage == 0){
            return false;
        }
        else if(damage < 0){
            if(currentHP + damage > maxHP)
                damage = (maxHP - currentHP);
            else damage *= -1;
            //display healing indicator
            currentHP += damage;
        }
        else{
            this.currentHP -= damage;
            activeBattleScene.enqueueTickerText(name + " took " + damage + " damage.");
            //display damage indicator
        }
        if(currentHP < 0){
            dead = true;
        }
        return dead;
    }
    
    /**
     * @return true if this creature is dead, false otherwise. 
     */
    @Override
    public boolean isDead(){
        return dead;
    }
    
    public void resetHP(){
        this.currentHP = this.maxHP;
        dead = false;
    }
    
    public void registerBattleState(BattleAppState bas){
        activeBattleScene = bas;
    }
    
    /**
     * Sets the idle animation, which plays any time there's no currently playing animation.
     * @param arg0 
     */
    @Override
    public void setIdleAnimation(String arg0) {
        idleAnim = arg0;
    }

    /**
     * Sets the current animation to play
     * @param arg0 
     */
    @Override
    public void playAnimation(String arg0) {
        setAnim = arg0;
        lastTime = 0;
        playAnim = true;
    }

    /**
     * sets whether the creature is visible
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {
        System.out.println("Setting visible on " + name + " to " + visible);
        if(visible)
            battleModel.setCullHint(Spatial.CullHint.Always);
        else
            battleModel.setCullHint(Spatial.CullHint.Inherit);
    }
    
    /**
     * Should be called in the battle state's update method to ensure that the animation runs.
     */
    @Override
    public void updateAnimation(){
        if(ac == null)
            return;
        boolean change = false;
        Action curr = ac.getCurrentAction();
        
        if(playAnim == true && !setAnim.equals(currentAnim)){
            currentAnim = setAnim;
            change = true;
        }
        else if(playAnim == false && !idleAnim.equals(currentAnim)){
            currentAnim = idleAnim;
            change = true;
        }
        else if(playAnim == true && ac.getTime() < lastTime){
            playAnim = false;
            currentAnim = idleAnim;
            change = true;
        }
        
        lastTime = ac.getTime();
        
        if(change){
            ac.setCurrentAction(currentAnim);
            ac.setTime(0);
        }
    }
    
    @Override
    public Spatial getBattleModel(){
        return battleModel;
    }
    
    @Override
    public int rollInitiative(){
        return 999 - (int)(getStat(Stat.AGILITY) + (Math.random() * 20));
    }
}
