/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.srn.ipl.jvircam;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point3d;
import javax.vecmath.Point3i;

/**
 *
 * @author sr
 */
public class Mesh {

    private List<Point3d> points;
    private List<Point3i> triangles;

    public Mesh() {
        this.points = new ArrayList<>();
        this.triangles = new ArrayList<>();
    }

    public int addPoint(Point3d p) {
        if (p != null) {
            return this.addPoint(p.x, p.y, p.z);
        }
        return -1;
    }

    public int addPoint(double x, double y, double z) {
        this.points.add(new Point3d(x, y, z));
        return this.points.size() - 1;
    }

    public void addTriangle(int a, int b, int c) {
        this.triangles.add(new Point3i(a, b, c));
    }

    public List<Point3d> getPoints() {
        return points;
    }

    public List<Point3i> getTriangles() {
        return triangles;
    }

    public Point3d getPoint(int i) {
        return this.points.get(i);
    }

    public Point3i getTriangle(int i) {
        return this.triangles.get(i);
    }
}
