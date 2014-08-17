/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.srn.ipl.jvircam;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.vecmath.Point3d;

/**
 *
 * @author sr
 */
public class ImagePanel extends JPanel {

    private BufferedImage image = null;
    private Projection projection;
    private boolean[] vanish = new boolean[3];
    private final static int Z = 0;
    private final static int D = 10;
    private Point3d[] points = new Point3d[]{
        new Point3d(-D, -D, Z),
        new Point3d(-D, D, Z),
        new Point3d(D, D, Z),
        new Point3d(D, -D, Z),
        //
        new Point3d(D, -D, Z + D),
        new Point3d(D, D, Z + D),
        new Point3d(-D, D, Z + D),
        new Point3d(-D, -D, Z + D)
    };
    private Mesh mesh;

    public ImagePanel() {
        this.mesh = new Mesh();
        this.mesh.addPoint(-10, -10, -10);
        this.mesh.addPoint(-10, 10, -10);
        this.mesh.addPoint(10, 10, -10);
        this.mesh.addPoint(10, -10, -10);
        this.mesh.addPoint(0, 0, 10);
        this.mesh.addTriangle(0, 1, 4);
        this.mesh.addTriangle(1, 2, 4);
        this.mesh.addTriangle(2, 3, 4);
        this.mesh.addTriangle(3, 0, 4);

//        int mx = 20;
//        int my = 20;
//        for (int i = 0; i < mx; i++) {
//            for (int j = 0; j < my; j++) {
//                this.mesh.addPoint(i, j, 0);
//            }
//        }
//        for (int i = 0; i < mx - 1; i++) {
//            for (int j = 0; j < my - 1; j++) {
//                this.mesh.addTriangle(pos2ind(i, j, my), pos2ind(i + 1, j, my), pos2ind(i + 1, j + 1, my));
//                this.mesh.addTriangle(pos2ind(i, j, my), pos2ind(i, j + 1, my), pos2ind(i + 1, j + 1, my));
//            }
//        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Projection getProjection() {
        return projection;
    }

    public void setProjection(Projection projection) {
        this.projection = projection;
    }

    public boolean[] getVanish() {
        return vanish;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //System.out.println("paint" + System.nanoTime());
        if (projection != null) {
            Painter painter = new Painter(projection);
            painter.paintAxes(g);
            //painter.paintImage(points, g);
            painter.paintImage(mesh, g);
            if (vanish[0]) {
                painter.paintXYZVanishingPoints(g);
            }
            if (vanish[1]) {
                painter.paintSphericalVanishingPoints(g);
            }
            if (vanish[2]) {
                painter.paintCubicVanishingPoints(g);
            }
        }
        //g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
    }
}
