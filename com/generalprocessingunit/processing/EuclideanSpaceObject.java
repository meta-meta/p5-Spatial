package com.generalprocessingunit.processing;

import processing.core.PMatrix3D;
import processing.core.PVector;

import java.util.HashSet;
import java.util.Set;

public class EuclideanSpaceObject extends MathsHelpers {
    private Orientation orientation;
    private final PVector location;

    private EuclideanSpaceObject parent = null;
    private Set<EuclideanSpaceObject> progeny = new HashSet<>();
    private Set<EuclideanSpaceObject> children = new HashSet<>();


    public Set<EuclideanSpaceObject> getAllChildren() {
        Set<EuclideanSpaceObject> c = new HashSet<>();
        for (EuclideanSpaceObject child : children) {
            c.addAll(child.getAllChildren());
        }

        return c;
    }

    public void addChild(EuclideanSpaceObject child, PVector locationWRTParent, YawPitchRoll rotationWRTParent) {

        child.locationWRTParent = locationWRTParent;
        child.rotationWRTParent = rotationWRTParent;

        child.location.set(getTranslationWRTObjectCoords(locationWRTParent));

//        System.out.println("x: " + orientation.xAxis() + "  y: " + orientation.yAxis() + " z: " + orientation.zAxis());
//        System.out.println("L: " + location + "  CL: " + child.location);

        child.orientation = orientation.rotateFrom(rotationWRTParent);

        child.parent = this;
        addProgeny(child);
        addProgeny(child.progeny);

        if (parent != null) {
            parent.addProgeny(progeny);
        }
    }

    public PVector getTranslationWRTObjectCoords(PVector locationWRTParent) {
        return add(
                getLocationRelativeToThisObject(locationWRTParent),
                location
        );
    }

    private PVector getLocationRelativeToThisObject(PVector locationWRTParent) {
        return add(
                PVector.mult(orientation.xAxis(), locationWRTParent.x),
                PVector.mult(orientation.yAxis(), locationWRTParent.y),
                PVector.mult(orientation.zAxis(), locationWRTParent.z)
        );
    }

    public YawPitchRoll getDeltaYawPitchRollFromThisObject(Orientation orientation) {
        return new YawPitchRoll(
                PVector.angleBetween(this.orientation.yAxis, orientation.yAxis),
                PVector.angleBetween(this.orientation.xAxis, orientation.xAxis),
                PVector.angleBetween(this.orientation.zAxis, orientation.zAxis)
        );
    }
    /**
     * places the object relative to this object's local coordinates without adding it as a child
     * @param obj
     */
    public void translateAndRotateObjectWRTObjectCoords(EuclideanSpaceObject obj) {
        obj.setLocation(getTranslationWRTObjectCoords(obj.location));

        obj.setOrientation(orientation.rotateFrom(obj.orientation));
    }

    private void addProgeny(EuclideanSpaceObject child) {
        progeny.add(child);
    }

    private void addProgeny(Set<EuclideanSpaceObject> children) {
        for (EuclideanSpaceObject child : children) {
            addProgeny(child);
        }
    }

    public EuclideanSpaceObject(PVector location, Orientation orientation) {
        this.orientation = orientation;
        this.location = location;
    }

    public EuclideanSpaceObject() {
        this(new PVector(), new Orientation());
    }

    public void translateWRTObjectCoords(PVector translation) {
        translate(getLocationRelativeToThisObject(translation));
    }

    public void translate(PVector translation) {
        location.add(translation);

        for (EuclideanSpaceObject p : progeny) {
            p.location.add(translation);
        }
    }

    public void setLocation(PVector v) {
        if (progeny.size() > 0) {
            // translate all progeny by the same amount
            PVector t = PVector.sub(v, location);
            for (EuclideanSpaceObject p : progeny) {
                p.location.add(t);
            }
        }

        //TODO: what if this object has a parent?
//        if(null != parent) {
//            location.set(add(
//                    PVector.mult(parent.orientation.xAxis(), locationWRTParent.x),
//                    PVector.mult(parent.orientation.yAxis(), locationWRTParent.y),
//                    PVector.mult(parent.orientation.zAxis(), locationWRTParent.z),
//                    v
//            ));
//
//            orientation = parent.orientation.rotateFrom(rotationWRTParent);
//        }

        location.set(v);
    }

    public void setLocation(float x, float y, float z) {
        setLocation(new PVector(x, y, z));
    }

    public void yaw(float theta) {
        revolveProgeny(theta, orientation.yAxis);
        orientation.yaw(theta);
    }

    public void pitch(float theta) {
        revolveProgeny(theta, orientation.xAxis);
        orientation.pitch(theta);
    }

    public void roll(float theta) {
        revolveProgeny(theta, orientation.zAxis);
        orientation.roll(theta);
    }

    private void revolveProgeny(float theta, PVector axis) {
        for (EuclideanSpaceObject p : progeny) {
            Quaternion q = Quaternion.fromAxis(theta, axis);
            p.orientation.orientation = q.mult(p.orientation.orientation);
            p.orientation.xAxis = q.rotateVector(p.orientation.xAxis);
            p.orientation.yAxis = q.rotateVector(p.orientation.yAxis);
            p.orientation.zAxis = q.rotateVector(p.orientation.zAxis);

            PVector relLocation = PVector.sub(p.location, location);
            relLocation = q.rotateVector(relLocation);
            p.location.set(add(location, relLocation));
        }
    }

    public void rotate(YawPitchRoll rotation) {
        yaw(rotation.yaw());
        pitch(rotation.pitch());
        roll(rotation.roll());
    }

    public void rotate(Quaternion rotation) {
        orientation.rotate(rotation);
    }

    public PVector getLocation() {
        return location.get();
    }

    public float x() {
        return location.x;
    }

    public float y() {
        return location.y;
    }

    public float z() {
        return location.z;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    private PVector locationWRTParent;
    private YawPitchRoll rotationWRTParent;

    public void setOrientation(YawPitchRoll rotation) {
        //TODO:   set progeny new orientation
        orientation = new Orientation(rotation);


        // TODO: THIS IS A HACK. It does not preserve the progeny's orientation
        for(EuclideanSpaceObject p : progeny) {

            p.location.set(getTranslationWRTObjectCoords(p.locationWRTParent));

            p.orientation = orientation.rotateFrom(p.rotationWRTParent);
        }
        
    }

    public void setOrientation(Orientation o) {
        orientation = o;

        // TODO: THIS IS A HACK It does not preserve the progeny's orientation
        for(EuclideanSpaceObject p : progeny) {

            p.location.set(getTranslationWRTObjectCoords(p.locationWRTParent));

            p.orientation = orientation.rotateFrom(p.rotationWRTParent);
        }

    }

    public AxisAngle getAxisAngle() {
        return orientation.getOrientation();
    }

    public Quaternion getOrientationQuat() {
        return orientation.getOrientationQuat();
    }
}
