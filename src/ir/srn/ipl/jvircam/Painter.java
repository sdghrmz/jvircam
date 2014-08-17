/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.srn.ipl.jvircam;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import java.awt.Graphics;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;
import javax.vecmath.Point2i;
import javax.vecmath.Point3d;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3d;

/**
 *
 * @author sr
 */
public class Painter {

    private Projection projection;

    public Painter(Projection projection) {
        this.projection = projection;
    }

    public void paintImage(Mesh mesh, Graphics g) {
        Point2i[] ar = new Point2i[mesh.getPoints().size()];
        for (int i = 0; i < ar.length; i++) {
            Point3d p = new Point3d(mesh.getPoint(i));
            projection.transform(p);
            ar[i] = new Point2i((int) p.x, (int) p.y);
        }
        g.setColor(Constants.Objects_Color);
        for (Point3i tri : mesh.getTriangles()) {
            g.drawLine(ar[tri.x].x, ar[tri.x].y, ar[tri.y].x, ar[tri.y].y);
            g.drawLine(ar[tri.x].x, ar[tri.x].y, ar[tri.z].x, ar[tri.z].y);
            g.drawLine(ar[tri.y].x, ar[tri.y].y, ar[tri.z].x, ar[tri.z].y);
        }
    }

    public void paintImage(Point3d[] points, Graphics g) {
        Point3d[] ar = new Point3d[points.length];
        for (int i = 0; i < points.length; i++) {
            ar[i] = new Point3d(points[i]);
            projection.transform(ar[i]);
        }
        g.setColor(Constants.Objects_Color);
        int oldX = 0, oldY = 0, firstX = 0, firstY = 0;
        for (int i = 0; i < ar.length; i++) {
            int x = (int) ar[i].x;//(W / 2 + F * ar[i].x);
            int y = (int) ar[i].y;//(H / 2 + F * ar[i].y);
            if (i == 0) {
                firstX = x;
                firstY = y;
            } else if (ar[i].z >= 0 && ar[i - 1].z >= 0) {
                g.drawLine(oldX, oldY, x, y);
            }
            oldX = x;
            oldY = y;
        }
        g.drawLine(oldX, oldY, firstX, firstY);
    }

    public void paintAxes(Graphics g) {
        double e = 5;//3000.0 / projection.getK().m00;
        Point3d orig = new Point3d(0, 0, 0);
        Point3d xaxis = new Point3d(e, 0, 0);
        Point3d yaxis = new Point3d(0, e, 0);
        Point3d zaxis = new Point3d(0, 0, e);

        projection.transform(orig);
        projection.transform(xaxis);
        projection.transform(yaxis);
        projection.transform(zaxis);

        final int R = 4;
        if (orig.z >= 0) {
            g.setColor(Constants.Origin_Color);
            g.fillOval((int) orig.x - R, (int) orig.y - R, 2 * R, 2 * R);
        }
        if (xaxis.z >= 0) {
            g.setColor(Constants.XAxis_Color);
            g.drawLine((int) orig.x, (int) orig.y, (int) xaxis.x, (int) xaxis.y);
        }
        if (yaxis.z >= 0) {
            g.setColor(Constants.YAxis_Color);
            g.drawLine((int) orig.x, (int) orig.y, (int) yaxis.x, (int) yaxis.y);
        }
        if (zaxis.z >= 0) {
            g.setColor(Constants.ZAxis_Color);
            g.drawLine((int) orig.x, (int) orig.y, (int) zaxis.x, (int) zaxis.y);
        }
    }

    public void paintXYZVanishingPoints(Graphics g) {
        Point2d vx = projection.vanishingPoint(new Vector3d(1, 0, 0));
        Point2d vy = projection.vanishingPoint(new Vector3d(0, 1, 0));
        Point2d vz = projection.vanishingPoint(new Vector3d(0, 0, 1));
        Point2d vxn = projection.vanishingPoint(new Vector3d(-1, 0, 0));
        Point2d vyn = projection.vanishingPoint(new Vector3d(0, -1, 0));
        Point2d vzn = projection.vanishingPoint(new Vector3d(0, 0, -1));

        VirtualCamera.logFrame().log("vx+ = " + vx);
        VirtualCamera.logFrame().log("vy+ = " + vy);
        VirtualCamera.logFrame().log("vz+ = " + vz);
        VirtualCamera.logFrame().log("vx- = " + vxn);
        VirtualCamera.logFrame().log("vy- = " + vyn);
        VirtualCamera.logFrame().log("vz- = " + vzn);

        final int R = 4;
        if (vx != null) {
            g.setColor(Constants.XAxis_Color);
            g.fillOval((int) vx.x - R, (int) vx.y - R, 2 * R, 2 * R);
        }
        if (vy != null) {
            g.setColor(Constants.YAxis_Color);
            g.fillOval((int) vy.x - R, (int) vy.y - R, 2 * R, 2 * R);
        }
        if (vz != null) {
            g.setColor(Constants.ZAxis_Color);
            g.fillOval((int) vz.x - R, (int) vz.y - R, 2 * R, 2 * R);
        }
        if (vxn != null) {
            g.setColor(Constants.XAxis_Color);
            g.drawOval((int) vxn.x - R, (int) vxn.y - R, 2 * R, 2 * R);
        }
        if (vyn != null) {
            g.setColor(Constants.YAxis_Color);
            g.drawOval((int) vyn.x - R, (int) vyn.y - R, 2 * R, 2 * R);
        }
        if (vzn != null) {
            g.setColor(Constants.ZAxis_Color);
            g.drawOval((int) vzn.x - R, (int) vzn.y - R, 2 * R, 2 * R);
        }
    }

    public void paintSphericalVanishingPoints(Graphics g) {
        final double EPS = 0.1;
        final int R = 1;
        g.setColor(Constants.Vanish_Color);
        Matrix3d RZ = new Matrix3d();
        Matrix3d RY = new Matrix3d();
        for (double rz = 0; rz < 2 * Math.PI; rz += EPS) {
            RZ.rotZ(rz);
            for (double ry = -Math.PI / 2; ry < Math.PI / 2; ry += EPS) {
                RY.rotY(ry);
                Vector3d v = new Vector3d(1, 0, 0);
                RY.transform(v);
                RZ.transform(v);
                Point2d vp = projection.vanishingPoint(v);
                if (vp != null) {
                    g.fillOval((int) vp.x - R, (int) vp.y - R, 2 * R, 2 * R);
                }
            }
        }
    }

//    private void paintUniformSphereVanishingPoints(Graphics g) {
//        final double EPS = 0.2;
//        final int R = 1;
//        g.setColor(Constants.Vanish_Color);
//        double[] norm = new double[(int) ((1.0 / EPS + 1) * (1.0 / EPS + 1))];
//        int k = 0;
//        for (double i = 0; i <= 1; i += EPS) {
//            for (double j = 0; j <= 1; j += EPS) {
//                norm[k++] = Math.sqrt(-2.0 * Math.log(i)) * Math.cos(2 * Math.PI * j);
//            }
//        }
//        for (double x : norm) {
//            if (x > -10 && x < 10) {
//                for (double y : norm) {
//                    if (y > -10 && y < 10) {
//                        for (double z : norm) {
//                            if (z > -10 && z < 10) {
//                                Vector3d v = new Vector3d(x, y, z);
//                                v.normalize();
//                                Point2d vp = this.vanishingPoint(v);
//                                if (vp != null) {
//                                    g.fillOval((int) vp.x - R, (int) vp.y - R, 2 * R, 2 * R);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
    public void paintCubicVanishingPoints(Graphics g) {
        final double EPS = 0.1;
        final int R = 1;
        g.setColor(Constants.Vanish_Color);
        for (double i = -1; i <= 1; i += EPS) {
            for (double j = -1; j <= 1; j += EPS) {
                for (int k = -1; k <= 1; k += 2) {
                    Vector3d v = new Vector3d(i, j, k);
                    Point2d vp = projection.vanishingPoint(v);
                    if (vp != null) {
                        g.fillOval((int) vp.x - R, (int) vp.y - R, 2 * R, 2 * R);
                    }
                }
            }
        }
        for (double i = -1; i <= 1; i += EPS) {
            for (double j = -1; j <= 1; j += EPS) {
                for (int k = -1; k <= 1; k += 2) {
                    Vector3d v = new Vector3d(i, k, j);
                    Point2d vp = projection.vanishingPoint(v);
                    if (vp != null) {
                        g.fillOval((int) vp.x - R, (int) vp.y - R, 2 * R, 2 * R);
                    }
                }
            }
        }
        for (double i = -1; i <= 1; i += EPS) {
            for (double j = -1; j <= 1; j += EPS) {
                for (int k = -1; k <= 1; k += 2) {
                    Vector3d v = new Vector3d(k, i, j);
                    Point2d vp = projection.vanishingPoint(v);
                    if (vp != null) {
                        g.fillOval((int) vp.x - R, (int) vp.y - R, 2 * R, 2 * R);
                    }
                }
            }
        }
    }

    public ImagePlus takePhoto(Point3d[] points, Point2i resolution) {
        Point3d[] ar = new Point3d[points.length];
        for (int i = 0; i < points.length; i++) {
            ar[i] = new Point3d(points[i]);
            projection.transform(ar[i]);
        }
        ByteProcessor bp = new ByteProcessor(resolution.x, resolution.y);
        bp.setColor(128);
        int oldX = 0, oldY = 0, firstX = 0, firstY = 0;
        for (int i = 0; i < ar.length; i++) {
            int x = (int) ar[i].x;//(W / 2 + F * ar[i].x);
            int y = (int) ar[i].y;//(H / 2 + F * ar[i].y);
            if (i == 0) {
                firstX = x;
                firstY = y;
            } else if (ar[i].z >= 0 && ar[i - 1].z >= 0) {
                bp.drawLine(oldX, oldY, x, y);
            }
            oldX = x;
            oldY = y;
        }
        bp.drawLine(oldX, oldY, firstX, firstY);
        //bp.set(W / 2, H / 2, 255);
        ImagePlus img = new ImagePlus("camera", bp);
        return img;
    }
}
