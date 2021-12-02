/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame.battle;

/**
 *
 * @author cameron
 */
@FunctionalInterface
public interface BattleEffect {
    public boolean applyEffect(Creature target, Creature user, double magnitude);
}
