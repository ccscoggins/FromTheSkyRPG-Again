/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.battle;

import com.jme3.scene.Spatial;
import mygame.Stat;

/**
 *
 * @author cameron
 */
public interface Creature {
    
    public enum BattleAnims{
        BT_Idle,
        BT_Attack,
        BT_Hit,
        // the following are all player only
        BT_Defeat,
        BT_DefeatIdle,
        BT_StartGuard,
        BT_GuardIdle,
        BT_GuardHit
    }
    
    public String getName();
    public double getStat(Stat s);
    
    public void attack(Creature other);
    public void useSkill(Creature other, BattleSkill skill);
    
    public void defeat(Creature other);
    public void beDefeatedBy(Creature other);
    
    public double getExpValue();
    public double getHPValue();
    public double getHPMaxValue();
    
    public boolean applyDamage(double damage);
    public boolean isDead();
    public void resetHP();
    
    public boolean isGuarding();
    public void setGuard(boolean guarding);
    
    public void setIdleAnimation(String animName);
    public void playAnimation(String animName);
    public void clearAnimation();
    public void updateAnimation();
    public void setVisible(boolean visible);
    public Spatial getBattleModel();
    
    public void registerBattleState(BattleAppState bas);
    public int rollInitiative();
    
    public boolean isPlayer();
}
