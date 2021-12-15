/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.math.ColorRGBA;

/**
 *
 * @author cameron
 */
public enum UIElement{
    BACKGROUND(-10, 0xa0c0f080),
    IMAGE(-5, 0),
    TEXT(0, 0x0b0f20ff),
    CURSOR(5, 0),
    DARK_BACKGROUND(-10, 0X000000ff),
    DARK_TEXT(0, 0xffffffff);
    
    private final int zOrder;
    private final int colorRGBA;
    
    private UIElement(int z, int color){
        zOrder = z;
        colorRGBA = color;
    }
    
    public int getZOrder(){
        return zOrder;
    }
    
    public int getColorInteger(){
        return colorRGBA;
    }
    
    public ColorRGBA getColor(){
        return new ColorRGBA().fromIntRGBA(colorRGBA);
    }
}
