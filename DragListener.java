package listeners;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

import components.DrawComponent;

public class DragListener extends MouseAdapter
{
	private DrawComponent drawComponent;
    private MouseEvent pressed;

	public DragListener(DrawComponent drawComponent) 
	{
		this.drawComponent = drawComponent;
	}
	
	//draw on the image a line and rotate it in each sector
    public void draw(MouseEvent e)
    {
    	double angle = 0;
		for(int i=0; i<drawComponent.getNumSectors(); i++)
		{
			Graphics2D g3 = (Graphics2D) drawComponent.getG2D().create();
			g3.setColor(drawComponent.getDrawLinesColor());
			g3.setStroke(new BasicStroke(drawComponent.getStroke(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, null, 0));
			g3.rotate(angle, drawComponent.getWidth() / 2, drawComponent.getHeight() / 2);
			g3.drawLine(pressed.getX(), pressed.getY(), e.getX(), e.getY());

			if (drawComponent.getReflect())
				g3.drawLine(drawComponent.getWidth() - pressed.getX(), pressed.getY(), drawComponent.getWidth() - e.getX(), e.getY());

			angle = angle + 2 * Math.PI / (drawComponent.getNumSectors());
			g3.dispose();
		}
		drawComponent.setDrawLines(pressed.getX(), pressed.getY(), e.getX(), e.getY());
    }
    
    public void mousePressed(MouseEvent e)
    {
        pressed = e;
        drawComponent.setLineIndex(drawComponent.getLineIndex() + 1);
        draw(e);
    }
    public void mouseDragged(MouseEvent e)
    {
    	draw(e);
		pressed = e;
    }
    

}

