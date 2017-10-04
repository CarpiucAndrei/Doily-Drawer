package components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

import listeners.DragListener;

public class DrawComponent extends JComponent 
{
	private final ArrayList<Line> sectorLines = new ArrayList<Line>();
	public void clearSectors() {
		sectorLines.clear();
	}

	private final ArrayList<Line> drawLines = new ArrayList<Line>();
	public ArrayList<Line> getDrawLines() {
		return drawLines;
	}

	// The buffered image and its graphics object used for incremental painting
	private Image image;
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	private Graphics2D g2D;
	public Graphics2D getG2D() {
		return g2D;
	}

	// a counter used for the undo functionality
	private int lineIndex = 0;
	public int getLineIndex() {
		return lineIndex;
	}

	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}

	// the number of sectors
	private int sectors;

	public int getNumSectors() {
		return sectors;
	}

	// the stroke attribute
	private int stroke = 1;

	public int getStroke() {
		return stroke;
	}

	public void setStroke(int stroke) {
		this.stroke = stroke;
	}

	private JPanel panel;
	public JPanel getPanel() {
		return panel;
	}

	private Color sectorLinesColor = Color.BLACK;
	public Color getSecLinesColor() {
		return sectorLinesColor;
	}
	public void setSectorLinesColor(Color c) {
		sectorLinesColor = c;
		repaint();
	}

	private Color drawLinesColor = Color.BLACK;
	public Color getDrawLinesColor() {
		return drawLinesColor;
	}
	public void setDrawLinesColor(Color c) {
		drawLinesColor = c;
	}

	private double angle;
	public double getAngle() {
		return angle;
	}

	private boolean reflect = false;
	public boolean getReflect() {
		return reflect;
	}
	public void setReflect(boolean a) {
		reflect = a;
	}

	private boolean redraw = true;
	public void setRedraw(boolean a) {
		redraw = a;
	}

	boolean showSectors = false;
	public boolean getShowSectors() {
		return showSectors;
	}
	public void setShowSectors(boolean a) {
		showSectors = a;
	}

	private boolean paintDrawing = true;
	public void setPaintDraw(boolean a) {
		paintDrawing = a;
	}

	private DragListener dragListener = new DragListener(this);

	/*
	 * The inner class line. It contains all the attributes of a drawn line (its
	 * coordonates, stroke, color, index for undo and whether it is reflected or
	 * not)
	 */
	public class Line {
		final double x1;
		public double getX1() {
			return x1;
		}

		final double y1;
		public double getY1() {
			return y1;
		}

		final double x2;
		public double getX2() {
			return x2;
		}

		final double y2;
		public double getY2() {
			return y2;
		}

		private Color color = Color.BLACK;
		public Color getColor() {
			return color;
		}

		private boolean reflect = false;
		public boolean getReflect() {
			return reflect;
		}

		private int lineIndex;
		public int getLineIndex() {
			return lineIndex;
		}

		private int stroke;
		public int getStroke() {
			return stroke;
		}

		// the Line constructors
		public Line(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public Line(double x1, double y1, double x2, double y2, Color color, boolean reflect, int lineIndex,
				int stroke) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.color = color;
			this.reflect = reflect;
			this.lineIndex = lineIndex;
			this.stroke = stroke;
		}
	}

	public DrawComponent(JPanel panel) {
		this.panel = panel;
		this.setPreferredSize(new Dimension(100, 100));
		this.updateSectorLines(12);
		this.addMouseListener(dragListener);
		this.addMouseMotionListener(dragListener);
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				setPaintDraw(true);
				setRedraw(true);
				repaint();
			}
		});
	}

	// this methods update the number, position and size of the sector lines,
	// adding them to the arrayList
	public void setSectors(int numLine) {
		updateSectorLines(numLine);
		repaint();
	}

	public void updateSectorLines(int sectors) {
		this.sectors = sectors;
		clearSectors();
		double angle = Math.PI / 2;

		for (int i = 0; i < sectors; i++) {
			sectorLines.add(new Line(0, 0, (getHeight() + getWidth()) / 2 * Math.cos(angle),
					(getWidth() + getHeight()) / 2 * Math.sin(angle)));
			angle = angle + 2 * (Math.PI / sectors);
		}
	}

	// this methods manipulate the drawLines arrayList, adding new lines as they
	// are incrementally painted
	public void setDrawLines(double inX, double inY, double finalX, double finalY) {
		updateDrawLines(inX, inY, finalX, finalY);
		repaint();
	}

	public void updateDrawLines(double inX, double inY, double finalX, double finalY) {
		drawLines.add(new Line(inX, inY, finalX, finalY, drawLinesColor, reflect, lineIndex, stroke));
	}

	public void clearDraw() {
		drawLines.clear();
		repaint();
	}

	// this method save the drawn image as a .png in the images folder
	public void save(String name) throws IOException {
		ImageIO.write((RenderedImage) image, "png", new File(".\\images\\", name + ".png"));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		super.paintComponent(g2D);

		// if the flag was update create a new image that will be used for the
		// incremental painting and update g2D
		if (redraw) {
			image = createImage(getSize().width, getSize().height);

			g2D = (Graphics2D) image.getGraphics();
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			redraw = false;
		}

		this.updateSectorLines(sectors);

		// redraw every line in the drawLines list
		if (paintDrawing) {
			double angle;

			for (int i = 0; i < drawLines.size(); i++) {
				angle = 0;

				if (drawLines.get(i).reflect) {
					for (int j = 0; j < sectors; j++) {
						Graphics2D g3 = (Graphics2D) g2D.create();
						g3.setColor(drawLines.get(i).color);
						g3.setStroke(new BasicStroke(drawLines.get(i).stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, null, 0));
						
						Line2D.Double line = new Line2D.Double(drawLines.get(i).x1, drawLines.get(i).y1,
								drawLines.get(i).x2, drawLines.get(i).y2);
						Line2D.Double line2 = new Line2D.Double(this.getWidth() - drawLines.get(i).x1,
								drawLines.get(i).y1, this.getWidth() - drawLines.get(i).x2, drawLines.get(i).y2);
						
						g3.rotate(angle, this.getWidth() / 2, this.getHeight() / 2);
						g3.draw(line);
						g3.draw(line2);
						g3.dispose();

						angle = angle + 2 * Math.PI / (sectors);
					}
				} else {
					for (int j = 0; j < sectors; j++) {
						Graphics2D g3 = (Graphics2D) g2D.create();
						g3.setColor(drawLines.get(i).color);
						g3.setStroke(new BasicStroke(drawLines.get(i).stroke, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND, 10, null, 0));
						
						Line2D.Double line = new Line2D.Double(drawLines.get(i).x1, drawLines.get(i).y1, drawLines.get(i).x2, drawLines.get(i).y2);
						g3.rotate(angle, this.getWidth() / 2, this.getHeight() / 2);
						g3.draw(line);
						g3.dispose();

						angle = angle + 2 * Math.PI / (sectors);
					}
				}
			}
			setPaintDraw(false);
		}

		g.drawImage(image, 0, 0, null);

		// if the box is ticked show the sectors lines
		if (showSectors) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(sectorLinesColor);
			g2.translate(this.getWidth() / 2, this.getHeight() / 2);
			
			for (int i = 0; i < sectorLines.size(); i++) {
				g2.draw(new Line2D.Double(sectorLines.get(i).x1, sectorLines.get(i).y1, sectorLines.get(i).x2,
						sectorLines.get(i).y2));
			}
			g2.dispose();
		}
	}
}