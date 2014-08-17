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

/**
 *
 * @author sr
 */
public class TopViewPanel extends JPanel {

    private Camera camera;
    private double scale = 1.0;

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
//
//    public Vector2d getLocation(int x, int y) {
//        int w = this.getWidth();
//        int h = this.getHeight();
//        int ox = (int) Math.round(w / 2.0);
//        int oy = (int) Math.round(w / 2.0);
//        return new Vector2d((x - ox) / scale, (y - oy) / scale);
//    }

    public Point getWorldCenter() {
        int w = this.getWidth();
        int h = this.getHeight();
        int ox = (int) Math.round(w / 2.0);
        int oy = (int) Math.round(h / 2.0);
        return new Point(ox, oy);
    }

    public Point getCameraLocation() {
        Point o = this.getWorldCenter();
        int cx = o.x + (int) Math.round(camera.getPosition().x * scale);
        int cy = o.y - (int) Math.round(camera.getPosition().y * scale);
        return new Point(cx, cy);
    }

    public Point getCameraPan() {
        Point o = this.getWorldCenter();
        Point3d pan = new Point3d(0, 0, 1);
        camera.getRotation().transform(pan);
        pan.add(camera.getPosition());
        //double scale = 20 / Math.sqrt(pan.x * pan.x + pan.y * pan.y);//this.getWidth() / 2.0
        int px = o.x + (int) Math.round(pan.x * scale);
        int py = o.y - (int) Math.round(pan.y * scale);
        return new Point(px, py);
    }

    public void setCameraLocation(Point p) {
        Point o = this.getWorldCenter();
        camera.getPosition().x = (p.x - o.x) / scale;
        camera.getPosition().y = (o.y - p.y) / scale;
    }

    public void setCameraPan(Point v) {
        Point c = this.getCameraLocation();
        double pan2 = Math.atan2(-(v.y - c.y), v.x - c.x);
        camera.panTilt(pan2, camera.getTiltAngle());
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
        // x axis
        g.setColor(Constants.XAxis_Color);
        g.drawLine(o.x, o.y, this.getWidth(), o.y);
        // y axis
        g.setColor(Constants.YAxis_Color);
        g.drawLine(o.x, o.y, o.x, 0);

        g.setColor(Constants.Origin_Color);
        g.drawOval(o.x - Constants.Origin_Radius, o.y - Constants.Origin_Radius, 2 * Constants.Origin_Radius, 2 * Constants.Origin_Radius);

        if (camera != null) {
            Point c = this.getCameraLocation();
            g.setColor(Constants.Camera_Color);
            g.drawOval(c.x - Constants.Camera_Radius, c.y - Constants.Camera_Radius, 2 * Constants.Camera_Radius, 2 * Constants.Camera_Radius);

            double hfv = camera.getHorizontalFieldOfView() / 2;
            double pan = camera.getPanAngle();
            Point3d p1 = new Point3d(this.getWidth(), 0, 0);
            Point3d p2 = new Point3d(this.getWidth(), 0, 0);
            Matrix3d rot = new Matrix3d();
            rot.rotZ(pan + hfv);
            rot.transform(p1);
            rot.rotZ(pan - hfv);
            rot.transform(p2);
            g.setColor(Constants.FOV_Color);
            g.drawLine(c.x, c.y, c.x + (int) p1.x, c.y - (int) p1.y);
            g.drawLine(c.x, c.y, c.x + (int) p2.x, c.y - (int) p2.y);
        }
    }
}
