/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.srn.ipl.jvircam;

import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

/**
 *
 * @author sr
 */
public class Projection {

    private static final double EPS = 1.0e-6;
    private final Matrix3d K; // K transforms the points in the camera coordinates to the image plane
    private final Matrix3d R; // rotation of the world coordinates from the camera coordinates
    private final Vector3d T; // position of the world center in the camera coordinates
    private final Transform3D P; // P projects points in the world coordinates to image plane of the camera

    public Projection() {
        K = new Matrix3d();
        R = new Matrix3d();
        T = new Vector3d();
        P = new Transform3D();

        K.setIdentity();
        R.setIdentity();
        T.set(0, 0, 0);
        P.setIdentity();
    }

    public Projection(Matrix3d K, Matrix3d R, Vector3d T) {
        this.K = K;
        this.R = R;
        this.T = T;
        Matrix3d KR = new Matrix3d(K);
        KR.mul(R);
        Vector3d KT = new Vector3d(T);
        K.transform(KT);

        P = new Transform3D();
        P.setIdentity();
        P.setRotation(KR);
        P.setTranslation(KT);
    }

    public void set(Projection prj) {
        this.K.set(prj.K);
        this.R.set(prj.R);
        this.T.set(prj.T);
        this.P.set(prj.P);

    }

    public void transform(Point3d p) {
        Point3d res = this.transform(new Vector3d(p));
        p.set(res);
    }

    public Point3d transform(Vector3d p) {
        return this.transform(new Vector4d(p.x, p.y, p.z, 1.0));
    }

    public Point3d transform(Vector4d vec) {
        Vector4d res = new Vector4d();
        P.transform(vec, res);
        Point3d p = new Point3d(res.x, res.y, res.z);
        if (Math.abs(p.z) < EPS) { // point at infinity
            p.z = 0;
        } else if (p.z > 0) {
            p.scale(1.0 / p.z);
        }// else point is behind of camera
        return p;
    }

    public Point2d vanishingPoint(Vector3d vec) {
        Point3d p = this.transform(new Vector4d(vec.x, vec.y, vec.z, 0.0));
        if (p.z > 0) {
            return new Point2d(p.x, p.y);
        }
        return null;
    }

    public Matrix3d getK() {
        return K;
    }

    public Matrix3d getR() {
        return R;
    }

    public Vector3d getT() {
        return T;
    }

    public Matrix4d getP() {
        Matrix4d res = new Matrix4d();
        P.get(res);
        return res;
    }

    public void setP(Matrix4d p) {
        P.set(p);
    }

    @Override
    public String toString() {
        return P.toString();
    }
}
