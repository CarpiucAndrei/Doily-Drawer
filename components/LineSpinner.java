package components;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import components.DrawComponent;

public class LineSpinner extends JSpinner
{
	private DrawComponent drawComponent;
	public LineSpinner(DrawComponent drawComponent)
	{
		this.drawComponent = drawComponent;
		this.setModel(new SpinnerNumberModel(12, 2, 1000000, 1) );
		this.addChangeListener(new ChangeListener() 
									{
									public void stateChanged(ChangeEvent e){
										JSpinner source = (JSpinner)e.getSource();
										drawComponent.setSectors( (int)source.getValue());
										drawComponent.setPaintDraw(true);
										drawComponent.setRedraw(true);
									}
								});
	}
	
	public DrawComponent getDrawComponent()
	{
		return drawComponent;
	}
}