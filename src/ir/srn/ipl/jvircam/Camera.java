/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.srn.ipl.jvircam;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;
import javax.vecmath.Point2i;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 *
 * @author sr
 */
public class Camera {

    // extrinsic parameters
    private Vector3d position; // m
    private Matrix3d rotation;
    // interinsic parameters
    private double focalLength; // mm
    private Point2d sensorSize; // mm
    private Point2i resolution; // pixels
    private Point2d center; // pixels
    private Projection projection;

    public Camera() {
        this.position = new Vector3d(0, 0, 0);

        this.rotation = new Matrix3d();
        this.rotation.setIdentity();

        this.focalLength = 10;
        this.sensorSize = new Point2d(22.3, 14.9);
        this.resolution = new Point2i(800, 600);//(5184, 3456);//
        this.center = new Point2d(resolution.x / 2.0, resolution.y / 2.0);

        this.projection = new Projection();
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public Matrix3d getRotation() {
        return this.rotation;
    }

    public double getFocalLength() {
        return focalLength;
    }

    public Point2i getResolution() {
        return resolution;
    }

    public Point2d getImageCenter() {
        return center;
    }

    public void setPosition(Vector3d p) {
        this.position = p;
    }

    public void setRotation(Matrix3d rot) {
        this.rotation = rot;
    }

    public void setFocalLength(double f) {
        if (f > 0) {
            this.focalLength = f;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setResolution(int rx, int ry) {
        if (rx > 0 && ry > 0) {
            this.resolution = new Point2i(rx, ry);
            this.center = new Point2d(resolution.x / 2.0, resolution.y / 2.0);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setImageCenter(double ox, double oy) {
        this.center = new Point2d(ox, oy);
    }

    public void translateCamera(Vector3d t) {
        this.position.add(t);
    }

    public void rotateCamera(Matrix3d r) {
        this.rotation.mul(r);
    }

    public void addFocalLength(double percent) {
        this.setFocalLength(focalLength + percent * focalLength);
    }

    public double getPanAngle() {
        Point3d p = new Point3d(0, 0, 1);
        rotation.transform(p);
        return Math.atan2(p.y, p.x);
    }

    public double getTiltAngle() {
        Point3d p = new Point3d(0, 0, 1);
        rotation.transform(p);
        double d = Math.sqrt(p.x * p.x + p.y * p.y);
        return Math.atan2(p.z, d);
    }

    public double getHorizontalFieldOfView() {
        return 2 * Math.atan2(sensorSize.x / 2, focalLength);
    }

    public double getVerticalFieldOfView() {
        return 2 * Math.atan2(sensorSize.y / 2, focalLength);
    }

//    public void pan(double angle) { // rotate around Z axis
//        Matrix3d ry = new Matrix3d();
//        ry.rotY(angle);
//        rotation.mul(ry);
//        //this.setRotation(rz);
//    }
//
//    public void tilt(double angle) { // rotate around x axis
//        Matrix3d rx = new Matrix3d();
//        rx.rotX(angle);
//        rotation.mul(rx);
//        //this.setRotation(rx);
//    }
    public void panTilt(double pan, double tilt) {
        if (pan > -Math.PI && pan < Math.PI && tilt > -Math.PI / 2 && tilt < Math.PI / 2) {
            Matrix3d ry = new Matrix3d();
            ry.rotY(-pan + Math.PI / 2);
            Matrix3d rx = new Matrix3d();
            rx.rotX(tilt);

            Matrix3d rot = new Matrix3d();
            rot.rotX(-Math.PI / 2);
            rot.mul(ry);
            rot.mul(rx);
            this.setRotation(rot);
        }
    }

    public Projection getProjection() {
        double fx = focalLength * resolution.x / sensorSize.x;
        double fy = focalLength * resolution.y / sensorSize.y;
        Matrix3d K = new Matrix3d(fx, 0, center.x, 0, fy, center.y, 0, 0, 1);
        //K.setIdentity();

        Matrix3d R = new Matrix3d(this.rotation);
        R.transpose();
        VirtualCamera.logFrame().log(R.toString());

        Vector3d T = new Vector3d(this.position);
        T.scale(-1);
        R.transform(T);

        projection.set(new Projection(K, R, T));
        return this.projection;
    }
}
