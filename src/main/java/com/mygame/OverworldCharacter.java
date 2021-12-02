/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.EnumMap;


/**
 * Class that encapsulates the movement functions of the player character on the overworld.
 * @author cameron
 */
public class OverworldCharacter extends CharacterControl implements ActionListener{
    private EnumMap<Control, Boolean> controls = new EnumMap<>(Control.class);
    private AnimComposer ac;
    private Vector3f walkDirection = Vector3f.ZERO.clone();
    private Vector3f lastWalkDirection;
    private final float ACCEL = 0.273f; //arbitrary but it works
    
    private Camera cam;
    
    /**
     * Contains the discrete animation states possible for the player character on the overworld
     */
    private enum OverworldCharacterAction{
        OW_Run,
        OW_Idle;
    }
    
    private OverworldCharacterAction lastAction, nextAction;
    
    //movement values are arbitrary, can be adjusted
    private final float walkSpeed = 1.0f;
    private final float runMultiplier = 2.5f;
    //private final Vector3f forecast = Vector3f.UNIT_X.clone(); //will be used to check for interactable objects
    
    
    
    /**
     * Basic constructor, sets the player model
     * @param playerModel 
     */
    public OverworldCharacter(Spatial playerModel){
        super (new CapsuleCollisionShape(1.0f, 4.5f), 5);
        playerModel.addControl(this);
        ac = Main.getNthChild(playerModel, 2).getControl(AnimComposer.class);
        lastAction = OverworldCharacterAction.OW_Idle;
        ac.setCurrentAction(lastAction.name());
    }
    
    /**
     * Adds listeners for the controls that matter to the OverworldCharacter controller
     * (i.e. movement and interacting with objects (not yet implemented))
     * @param im 
     */
    public void addInputListeners(InputManager im){
        im.addListener(this, Control.FORWARD.name());
        im.addListener(this, Control.BACKWARD.name());
        im.addListener(this, Control.STRAFE_L.name());
        im.addListener(this, Control.STRAFE_R.name());
        im.addListener(this, Control.INTERACT.name());
        im.addListener(this, Control.RUN.name());
        
        controls.put(Control.FORWARD, false);
        controls.put(Control.BACKWARD, false);
        controls.put(Control.STRAFE_L, false);
        controls.put(Control.STRAFE_R, false);
        controls.put(Control.INTERACT, false);
        controls.put(Control.RUN, false);
    }
    
    /**
     * Marks that a control was pressed or released.
     * @param name - name of control, corresponds with "Control" enum (see addInputListeners for checked values)
     * @param pressed - true if pressed, false otherwise
     * @param tpf - unused but needed to match method signature
     */
    @Override
    public void onAction(String name, boolean pressed, float tpf) {
        if(isEnabled())
            controls.put(Control.valueOf(name), pressed);
        //System.out.println("Control " + name + " set to " + pressed);
    }
    
    /**
     * Sets the camera for easy access to calculate movement direction
     * @param cam 
     */
    public void setCamera(Camera cam){
        this.cam = cam;
    }
    
    /**
     * LERP interpolation for turning towards the new movement angle;
     *  uses a tertiary point if the player is turning ~180 degrees
     * @param newFaceAngle 
     */
    private void turnToward(Vector3f newFaceAngle){
        //normalize the view directors
        Vector3f faceAngle = this.getViewDirection(null).normalize();
        Vector3f newFace = newFaceAngle.normalize();
        
        if(faceAngle.negate().subtractLocal(newFace).length() < 0.1){ //estimate 180 degree turnaround
            //Tertiary point necessary, use 2d normal of the current facing vector
            Vector3f normal = new Vector3f(-1 * faceAngle.z, 0, faceAngle.x);
            newFace = FastMath.interpolateLinear(0.125f, FastMath.interpolateLinear(0.125f, faceAngle, normal), newFace);
        }
        else //no tertiary point needed, simply interpolate
            newFace = FastMath.interpolateLinear(0.25f, faceAngle, newFace);
        
        //Set view direction
        setViewDirection(newFace);
    }
    
    /**
     * Update method that does the movement relative to the camera.
     * /Should/ be called every frame, but is not unless manually called from OverworldAppState
     * @param tpf - time per frame
     */
    @Override
    public void update(float tpf){
        //System.out.println("OverworldCharacter updated");
        lastWalkDirection = walkDirection.clone();
        //I use a member vector to avoid creating new vectors every frame, but I'm not sure if that 'optimization' is worth the effort
        walkDirection.set(0,0,0);
        
        //Default vectors for camera adjustment, if camera is not set then player will move based on initial rotation
        Vector3f camAdjust_F = Vector3f.UNIT_Z;
        Vector3f camAdjust_L = Vector3f.UNIT_X;
        
        //If camera is set, get the camera's view vectors to adjust the player movement
        if (cam != null){
            Quaternion cameraRot = cam.getRotation();
            camAdjust_F = cameraRot.mult(Vector3f.UNIT_Z).multLocal(1,0,1);
            camAdjust_L = cameraRot.mult(Vector3f.UNIT_X);
        }
        
        //Not super proud of this, but it works.  No else because if player presses forward and backward at once then they will negate and do nothing rather than one having precedence.
        if(controls.get(Control.FORWARD))
            walkDirection.addLocal(camAdjust_F);
        if(controls.get(Control.BACKWARD))
            walkDirection.addLocal(camAdjust_F.negateLocal());
        if(controls.get(Control.STRAFE_L))
            walkDirection.addLocal(camAdjust_L);
        if(controls.get(Control.STRAFE_R))
            walkDirection.addLocal(camAdjust_L.negate());
        
        //Set y value of walk direction to 0 in case of rounding errors from above then normalize.
        walkDirection.setY(0.0f);
        walkDirection.normalizeLocal(); // to prevent the "fast diagonal" problem
        
        //Direction has been calculated and normalized.
        //If walking, turn toward the direction of the walk.
        if(! walkDirection.equals(Vector3f.ZERO)){
            turnToward(walkDirection);
            nextAction = OverworldCharacterAction.OW_Run;
        }
        else{
            nextAction = OverworldCharacterAction.OW_Idle;
        }
        
        //We don't want to set the action every frame, else it'll appear frozen at the first frame.  thus:
        if(nextAction != lastAction){
            ac.setCurrentAction(nextAction.name());
        }
        lastAction = nextAction;
        
        
        //multiply the walking speed, and multiply the running multiplier if run is held
        walkDirection.multLocal(walkSpeed);
        /*if(controls.get(Control.RUN))
            walkDirection.multLocal(runMultiplier);*/
        
        
        //LERP to get a more smooth transition for walking speed
        walkDirection = FastMath.interpolateLinear(ACCEL, lastWalkDirection, walkDirection);
        this.setWalkDirection(walkDirection);
        
        //Finally, call the superclass update to actually implement the walk, do physics, etc.
        super.update(tpf);
    }
    
}
