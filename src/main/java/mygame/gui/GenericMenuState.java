/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.gui;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.ui.Picture;
import mygame.Control;
import mygame.Main;
import mygame.ngin.MyBaseAppState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 *
 * @author cameron
 * @param <T> - the type of class used as the menu selector
 */
public abstract class GenericMenuState <T> extends MyBaseAppState implements ActionListener{
    protected volatile SimpleApplication sapp;
    protected volatile Node guiNode;
    protected volatile MyBaseAppState creator;
    
    protected float sWidth, sHeight;
    protected float xpos, ypos;
    protected float width, height;
    private boolean wrapSize = false;
    
    private static final float XPAD = 34, YPAD = 4;
    private static BitmapFont guiFont;
    
    protected List<T> items;
    private ArrayList<BitmapText> displayText;
    private EnumMap<Control, Boolean> controls;
    
    private Geometry background;
    private Material bgmat;
    
    private UIElement textElement = UIElement.TEXT;
    private UIElement bgElement = UIElement.BACKGROUND;
    
    private int rows;
    private final int cols;
    private static final float ROW_HEIGHT_FACTOR = 1.1f;
    private float colWidth, rowHeight = 0;
    
    protected Picture cursor;
    private int cursorCol = 0, cursorRow = 0;
    private static final float CURS_Y_OFFSET = 24;
    private static final float CURS_X_OFFSET = -26;
    
    private boolean fullyOpened, shouldClose;
    protected boolean canClose = false;
    
    protected Node menuNode;
    protected Node otherUINode;
    
    private static int zoffset = 0;
    
    private int thisZOffset;
    
    public GenericMenuState(){
        this(0f, 0f, 0f, 0f, 1);
        wrapSize = true;
    }
    
    public GenericMenuState(float width, float height){
        this(width, height, 0f, 0f, 1);
    }
    
    public GenericMenuState(float width, float height, float x, float y, int cols){
        this.width = width;
        this.height = height;
        xpos = x;
        ypos = y;
        this.cols = cols;
        controls = new EnumMap<>(Control.class);
    }
    
    protected abstract String getStringForType(T type);
    
    public abstract void select(T type);
    
    protected void populate(T[] contents){
        if(items != null)
            items.clear();
        items = new ArrayList<>(Arrays.asList(contents));
        if(menuNode != null){
            redrawMenu();
        }
    }
    
    protected void redrawMenu(){
        float longestLine = 0;
        for(BitmapText bmt : displayText){
            menuNode.detachChild(bmt);
        }
        displayText.clear();
        
        if(width < 0)
            width = sWidth;
        if(height < 0)
            height = sHeight;
        
        rows = (int)FastMath.ceil(((float)items.size())/cols);
        colWidth = FastMath.floor(width / cols);
        if(wrapSize){
            height = (rowHeight * rows * ROW_HEIGHT_FACTOR) + (2 * YPAD);
        }
        
        //System.out.println(items.size());
        for (int i = 0; i < items.size(); i++){
            T t = items.get(i);
            int ccol = i % cols;
            int crow = (int)FastMath.floor(((float)i) / cols); 
            
            
            BitmapText bmt = new BitmapText(guiFont);
            bmt.setText(getStringForType(t));
            bmt.setSize(guiFont.getCharSet().getRenderedSize());
            bmt.setColor(textElement.getColor());
            bmt.setLocalTranslation((colWidth * ccol) + XPAD,
                    (height) - ((crow * rowHeight) + YPAD),
                    textElement.getZOrder()
            );
            if(wrapSize && longestLine < bmt.getLineWidth())
                longestLine = bmt.getLineWidth();
            displayText.add(bmt);
            menuNode.attachChild(bmt);
        }
        
        
        if(cursorRow * cols + cursorCol >= items.size()){
            cursorRow = 0;
            cursorCol = 0;
            findCursorTranslation();
        }
        if(wrapSize){
            colWidth = longestLine;
            width = (colWidth * cols) + (2 * XPAD);
            if(background != null){
                background.setMesh(new Quad(width, height));
            }
        }
        if(bgmat != null){
            bgmat.setColor("Color", bgElement.getColor());
        }
    }
    
    private void findCursorTranslation(){
        float cursorYPixel = height - ((cursorRow * rowHeight) + YPAD + CURS_Y_OFFSET);
        float cursorXPixel = (cursorCol * colWidth) + XPAD + CURS_X_OFFSET;
        
        cursor.setLocalTranslation(cursorXPixel, cursorYPixel, UIElement.CURSOR.getZOrder());
        
    }
    
    private void setupInputs(InputManager im){
        im.addListener(this, Control.MENU_UP.name());
        im.addListener(this, Control.MENU_DOWN.name());
        im.addListener(this, Control.MENU_LEFT.name());
        im.addListener(this, Control.MENU_RIGHT.name());
        im.addListener(this, Control.MENU_OPEN.name());
        im.addListener(this, Control.CONFIRM.name());
        im.addListener(this, Control.BACK.name());

        controls.put(Control.MENU_UP, false);
        controls.put(Control.MENU_DOWN, false);
        controls.put(Control.MENU_LEFT, false);
        controls.put(Control.MENU_RIGHT, false);
        controls.put(Control.MENU_OPEN, false);
        controls.put(Control.CONFIRM, false);
        controls.put(Control.BACK, false);
    }
    
    @Override
    protected void initialize(Application arg0) {
        sapp = (SimpleApplication)arg0;
        guiNode = sapp.getGuiNode();
        
        menuNode = new Node("Menu");
        otherUINode = new Node("MenuWrapper");
        
        sWidth = Main.getSettings().getWidth();
        sHeight = Main.getSettings().getHeight();
        
        
        AssetManager am = sapp.getAssetManager();
        
        if(guiFont == null){
            guiFont = am.loadFont("Textures/ui/Liberation-Serif.fnt");
        }
        rowHeight = guiFont.getCharSet().getLineHeight();
        
        rows = (int)FastMath.ceil(((float)items.size())/cols);
        colWidth = FastMath.floor(width / cols);
        
        if(wrapSize){
            height = (rowHeight * rows * ROW_HEIGHT_FACTOR) + (2 * YPAD);
        }
        
        cursor = new Picture("Menu Cursor");
        cursor.setImage(am, "Textures/ui/ui_selector.png", true);
        cursor.setWidth(16);
        cursor.setHeight(16);
        findCursorTranslation();
        menuNode.attachChild(cursor);
        
        displayText = new ArrayList<>(items.size());
        
        redrawMenu();
        
        background = new Geometry("MenuBG", new Quad(width, height));
        bgmat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        bgmat.setColor("Color", UIElement.BACKGROUND.getColor());
        background.setMaterial(bgmat);
        background.move(0, 0, UIElement.BACKGROUND.getZOrder());
        menuNode.attachChild(background);
        zoffset += 40;
        thisZOffset = zoffset;
        //System.out.println("menu zoffset = " + thisZOffset);
        menuNode.setLocalTranslation(xpos, ypos, 0);
        
        otherUINode.attachChild(menuNode);
        otherUINode.setLocalTranslation(0, 0, zoffset);
        
        fullyOpened = false;
    }

    @Override
    protected void cleanup(Application arg0) {
        displayText.clear();
        zoffset -= 40;
    }
    
    @Override
    protected void onEnable() {
        guiNode.attachChild(otherUINode);
        this.setPaused(false);
        shouldClose = false;
        
        //System.out.println("menu with zoffset " + thisZOffset + " enabled");
        
        if(creator != null && !creator.isPaused())
            creator.setPaused(true);
    }

    @Override
    protected void onDisable() {
        this.setPaused(true);
        guiNode.detachChild(otherUINode);
        fullyOpened = false;
        
        //System.out.println("menu with zoffset " + thisZOffset + " disabled");
        
        if(creator != null && creator.isPaused())
            creator.setPaused(false);
    }

    @Override
    protected void onUnpause() {
        setupInputs(sapp.getInputManager());
    }
    
    @Override
    protected void onPause() {
        sapp.getInputManager().removeListener(this);
    }

    @Override
    public void onAction(String name, boolean pressed, float tpf){
        controls.put(Control.valueOf(name), pressed);
    }
    
    @Override
    public void update(float tpf){
        if(!fullyOpened){
            fullyOpened = true;
        }
        else{
            if(controls.get(Control.CONFIRM)){
                select(items.get(cursorRow * cols + cursorCol));
            }
            else if((canClose && (controls.get(Control.BACK) || controls.get(Control.MENU_OPEN))) || shouldClose){
                this.setEnabled(false);
            }
            else{
                int shiftx = 0, shifty = 0;
                if(controls.get(Control.MENU_UP))
                    shifty -= 1;
                if (controls.get(Control.MENU_DOWN))
                    shifty += 1;
                if (controls.get(Control.MENU_LEFT))
                    shiftx -= 1;
                if (controls.get(Control.MENU_RIGHT))
                    shiftx += 1;
                
                cursorCol = (cursorCol + shiftx + cols) % cols;
                cursorRow = (cursorRow + shifty + rows) % rows; //redundant add because java modulus can return negative numbers
                
                findCursorTranslation();
            }
        }
        for(Control c : controls.keySet()){
            controls.put(c, false);
        }
    }
    
    public void setPosition(float x, float y){
        xpos = x;
        ypos = y;
        menuNode.setLocalTranslation(x, y, 0);
    }
    
    protected void setTextBGElements(UIElement text, UIElement bg){
        textElement = text;
        bgElement = bg;
    }
}
