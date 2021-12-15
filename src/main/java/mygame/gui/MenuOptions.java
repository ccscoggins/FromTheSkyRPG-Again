/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

/**
 * Solely exists to encapsulate different menu option enums in a publicly available space.  Bad OOP but it works.
 * @author cameron
 */
public class MenuOptions {
    /**
    * 
    */
    public enum BattleOption{
        Fight,
        Guard,
        Run
    }
   
    /**
     * 
     */
    public enum FieldMenuOption{
        Status
        //Skills,
        //Items
        //Equipment,
        //Save
    }
}
