/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.srn.ipl.jvircam;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JPanel;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 *
 * @author sr
 */
public class SideViewPanel extends JPanel {

    private Camera camera;
    private double scale = 1;

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = Math.min(Math.max(scale, 0.05), 100);
    }

    public void addScale(double percent) {
        if (percent > -1 && percent < 1) {
            setScale(scale + percent * scale);
        }
    }

    public Point getWorldCenter() {
        int w = this.getWidth();
        int h = this.getHeight();
        int ox = (int) Math.round(w * 0.90);
        int oy = (int) Math.round(h / 2.0);
        return new Point(ox, oy);
    }

    public Point getCameraLocation() {
        Point o = this.getWorldCenter();
        Vector3d p = camera.getPosition();
        double d = Math.sqrt(p.x * p.x + p.y * p.y);
        int cx = o.x - (int) Math.round(d * scale);
        int cy = o.y - (int) Math.round(p.z * scale);
        return new Point(cx, cy);
    }

    public Point getCameraTilt() {
        Point o = this.getWorldCenter();
        Point3d p = new Point3d(0, 0, 1);
        camera.getRotation().transform(p);
        p.add(camera.getPosition());
        double d = Math.sqrt(p.x * p.x + p.y * p.y);
        int px = o.x - (int) Math.round(d * scale);
        int py = o.y - (int) Math.round(p.z * scale);
        return new Point(px, py);
    }

    void setCameraLocation(Point p) {
        Point o = this.getWorldCenter();
        if (p.x < o.x) {
            Vector3d cp = camera.getPosition();
            double d1 = Math.sqrt(cp.x * cp.x + cp.y * cp.y);
            double d2 = (o.x - p.x) / scale;
            cp.x *= d2 / d1;
            cp.y *= d2 / d1;
            cp.z = (o.y - p.y) / scale;
        }
    }

    private double cameraMinAngle() {
        double teta = camera.getPanAngle() - Math.PI / 2;
        if (teta < -Math.PI) {
            teta += 2 * Math.PI;
        }
        return teta;
    }

    private double cameraMaxAngle() {
        double teta = camera.getPanAngle() + Math.PI / 2;
        if (teta > Math.PI) {
            teta -= 2 * Math.PI;
        }
        return teta;
    }

    private boolean isInFrontOfCamera(double angle) {
        double min = this.cameraMinAngle();
        double max = this.cameraMaxAngle();
        if (min < max) {
            return angle > min && angle < max;
        }
        double twoP = 2 * Math.PI;
        if (angle > min && angle < max + twoP) {
            return true;
        }
        if (angle < max && angle > min - twoP) {
            return true;
        }
        return false;
    }

    public void setCameraTilt(Point v) {
        Point c = this.getCameraLocation();
        double tilt = Math.atan2(c.y - v.y, Math.abs(v.x - c.x));
        if (tilt > -Math.PI * 5 / 12 && tilt < Math.PI * 5 / 12) {
            boolean inFront = isInFrontOfCamera(Math.atan2(-camera.getPosition().y, -camera.getPosition().x));
            if ((inFront && v.x > c.x) || (!inFront && v.x < c.x)) {
                camera.panTilt(camera.getPanAngle(), tilt);
            }
        }
    }

    public boolean isOverCamera(Point p) {
        Point c = this.getCameraLocation();
        if (p.distance(c) <= Constants.Camera_Radius) {
            return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        Point o = this.getWorldCenter();
        // z axis
        g.setColor(Constants.ZAxis_Color);
        g.drawLine(o.x, o.y, o.x, 0);

        g.setColor(Constants.Origin_Color);
        g.drawOval(o.x - Constants.Origin_Radius, o.y - Constants.Origin_Radius, 2 * Constants.Origin_Radius, 2 * Constants.Origin_Radius);

        if (camera != null) {
            Point c = this.getCameraLocation();
            g.setColor(Constants.Camera_Color);
            g.drawOval(c.x - Constants.Camera_Radius, c.y - Constants.Camera_Radius, 2 * Constants.Camera_Radius, 2 * Constants.Camera_Radius);

            double vfv = camera.getVerticalFieldOfView() / 2;
            double tilt = camera.getTiltAngle();
            boolean inFront = isInFrontOfCamera(Math.atan2(-camera.getPosition().y, -camera.getPosition().x));
            if (!inFront) {
                tilt = Math.PI - tilt;
            }
            Point3d p1 = new Point3d(this.getWidth(), 0, 0);
            Point3d p2 = new Point3d(this.getWidth(), 0, 0);
            Matrix3d rot = new Matrix3d();
            rot.rotZ(tilt + vfv);
            rot.transform(p1);
            rot.rotZ(tilt - vfv);
            rot.transform(p2);
            g.setColor(Constants.FOV_Color);
            g.drawLine(c.x, c.y, c.x + (int) p1.x, c.y - (int) p1.y);
            g.drawLine(c.x, c.y, c.x + (int) p2.x, c.y - (int) p2.y);
        }
    }
}
