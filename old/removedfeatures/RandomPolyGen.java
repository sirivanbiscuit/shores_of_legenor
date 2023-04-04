package removedfeatures;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class RandomPolyGen extends JFrame {

	private int xS = 600;
	private int yS = 600;

	private int verts = 10;

	private Polygon poly = new Polygon();
	private GeneralPath polyPath = new GeneralPath();

	private Panel p;

	public RandomPolyGen() {
		this.init();
	}

	private void init() {

		Button b = new Button("Generate");
		int bX = 100;
		int bY = 30;
		b.setBounds((int) (xS * 0.5 - bX * 0.5), yS - 85, bX, bY);
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				polyPath = pathGen(verts, (int) (xS * 0.1), (int) (xS * 0.9), (int) (yS * 0.1), (int) (yS * 0.85));
				p.repaint();
			}
		});

		Button bR = new Button("-->");
		bR.setBounds((int) (xS * 0.75 - bX * 0.5), yS - 85, bX, bY);
		bR.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				p.setLocation(p.getX() + 5, p.getY());
			}
		});

		Button bL = new Button("<--");
		bL.setBounds((int) (xS * 0.25 - bX * 0.5), yS - 85, bX, bY);
		bL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				p.setLocation(p.getX() - 5, p.getY());
			}
		});

		p = new Panel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.fill3DRect(100, 100, 50, 50, true);
				// g.setColor(Color.BLACK);
				// g.fillPolygon(poly);
				Graphics2D g2d = (Graphics2D) g;
				g2d.fill(polyPath);
			}
		};
		p.setBounds(0, 0, xS, (int) (yS * 0.85));

		this.setTitle("PolyGen");
		this.add(b);
		this.add(bR);
		this.add(bL);
		this.add(p);
		this.setMinimumSize(new Dimension(xS, yS));
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		this.pack();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			(new RandomPolyGen()).setVisible(true);
		});
	}

	private int intGen(int min, int max) {
		return (int) (Math.random() * (max - min) + min);
	}

	private Polygon polyGen(int sides, int minX, int maxX, int minY, int maxY, int lag) {

		int[][] pts = new int[2][sides];

		int orgMinX = minX;
		int ptFinal = sides - 1;
		int vertGens = 0;
		for (int pId = 0; pId < sides; pId++) {
			/*
			 * Adds new vertice. For pId>0 this creates a line between pId and pId-1. For
			 * pId=(sides-1) this creates a line between pId and 0. To ensure that a line
			 * between pId and 0 is possible, no point (except the final one) will be
			 * permitted to fall left of the first (origin) pId=0 point.
			 */
			minX = (pId == ptFinal) ? orgMinX : minX;
			pts[0][pId] = intGen(minX, maxX);
			pts[1][pId] = intGen(minY, maxY);
			minX = (pId == 0) ? pts[0][0] : minX;

			/*
			 * Check that new line does not intersect any others. Only needed for sqaures
			 * and higher (4+ sides). If no possible vertices are available the polygon will
			 * be recreated from scratch to prevent an infinite loop.
			 */
			if (pId >= 3) {
				for (int prevPt = 1; prevPt <= pId - 2; prevPt++) {
					if (Line2D.linesIntersect(pts[0][pId], pts[1][pId], pts[0][pId - 1], pts[1][pId - 1],
							pts[0][prevPt], pts[1][prevPt], pts[0][prevPt - 1], pts[1][prevPt - 1])) {
						pId--;
						vertGens++;
						break;
					}
					if (pId == ptFinal && Line2D.linesIntersect(pts[0][pId], pts[1][pId], pts[0][0], pts[1][0],
							pts[0][prevPt], pts[1][prevPt], pts[0][prevPt + 1], pts[1][prevPt + 1])) {
						pId--;
						vertGens++;
						break;
					}
				}
			}
			if (vertGens > lag) {
				return polyGen(sides, orgMinX, maxX, minY, maxY, lag);
			}
		}

		return new Polygon(pts[0], pts[1], sides);
	}

	private GeneralPath pathGen(int vertices, int minX, int maxX, int minY, int maxY) {
		GeneralPath path = new GeneralPath();
		Point origin = new Point((maxX + minX) / 2, (maxY + minY) / 2);
		Point v1 = new Point();
		for (int ang = 0; ang < 360; ang += 360.0 / vertices * (Math.random() * (0.8 - 0.2) + 0.2)) {
			int mag = (int) ((Math.random() * (0.6 - 0.4) + 0.4) * (maxX - origin.x));
			Point p = new Point((int) (origin.x + Math.sin(Math.toRadians(ang)) * mag),
					(int) (origin.y - Math.cos(Math.toRadians(ang)) * mag) / 2 + yS / 4);
			if (ang > 0) {
				path.lineTo(p.x, p.y);
			} else {
				path.moveTo(p.x, p.y);
				v1 = p;
			}
		}
		path.lineTo(v1.x, v1.y);
		return path;
	}

}
