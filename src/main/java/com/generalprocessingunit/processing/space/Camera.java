package com.generalprocessingunit.processing.space;

import com.generalprocessingunit.processing.MomentumVector;
import com.generalprocessingunit.processing.MomentumYawPitchRoll;
import processing.core.PGraphics;
import processing.core.PVector;

public class Camera extends EuclideanSpaceObject{

    public float fov = PI / 2.8f;

    public void camera(PGraphics pG) {
        PVector cam = getLocation();

        PVector lookat = getOrientation().zAxis();
        lookat.add(cam);
        PVector up = getOrientation().yAxis();
        up.mult(-1);

        pG.camera(
                cam.x, cam.y, cam.z,
                lookat.x, lookat.y, lookat.z,
                up.x, up.y, up.z
        );

        pG.perspective(fov, (float)pG.width / (float)pG.height, .01f, 10000.0f);

//        pG.scale(-1, 1);
    }

}
