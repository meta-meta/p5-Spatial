package com.generalprocessingunit.hid;

import com.generalprocessingunit.processing.YawPitchRoll;
import processing.core.PApplet;
import processing.core.PVector;
import procontroll.ControllButton;
import procontroll.ControllDevice;
import procontroll.ControllIO;
import procontroll.ControllSlider;

public class SpaceNavigator {
    public PVector translation = new PVector();
    public YawPitchRoll rotation = new YawPitchRoll();

    private ControllIO controll;
    private ControllDevice device; // my SpaceNavigator
    private ControllSlider sliderXpos; // Positions
    private ControllSlider sliderYpos;
    private ControllSlider sliderZpos;
    private ControllSlider sliderXrot; // Rotations
    private ControllSlider sliderYrot;
    private ControllSlider sliderZrot;
    private ControllButton button1; // Buttons
    private ControllButton button2;

    private boolean spaceNavigatorPresent = true;

    public SpaceNavigator(PApplet p5) {
        controll = ControllIO.getInstance(p5);
        try {
            device = controll.getDevice("SpaceNavigator");
            device.setTolerance(1.00f);

            sliderXpos = device.getSlider(2);
            sliderYpos = device.getSlider(0);
            sliderZpos = device.getSlider(1);
            sliderXrot = device.getSlider(5);
            sliderYrot = device.getSlider(3);
            sliderZrot = device.getSlider(4);
            button1 = device.getButton(0);
            button2 = device.getButton(1);
            sliderXpos.setMultiplier(0.01f); // sensitivities
            sliderYpos.setMultiplier(-0.01f);
            sliderZpos.setMultiplier(-0.01f);
            sliderXrot.setMultiplier(-0.0001f);
            sliderYrot.setMultiplier(0.0001f);
            sliderZrot.setMultiplier(0.0001f);
        } catch (Exception e) {
            spaceNavigatorPresent = false;
            p5.println(e.getMessage());
        }
    }

    public void poll() {
        if(!spaceNavigatorPresent) {
            PApplet.println("No SpaceNavigator Found");
            return;
        }

        translation.set(
                sliderXpos.getValue(),
                sliderYpos.getValue(),
                sliderZpos.getValue()
        );


        rotation.set(
                sliderYrot.getValue(),
                sliderXrot.getValue(),
                sliderZrot.getValue()
        );
    }
}
