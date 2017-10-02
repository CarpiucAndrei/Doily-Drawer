import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import theGUI.MyFrame;

//In order fir the gallery to work you must have a folder called images, where the images will be loaded and saved 

public class Main 
{
	public static void main(String[] args) 
	{	
		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run() 
			{
				JFrame testFrame = new MyFrame();
			}
		});
	}
}
/*Possible future improvements(speed efficiency) / functionality
 * 1)The Undo button -> have two buffered images one with a line behind and when the button is pressed
 * 						switch the one you are currently displaying and drawing and, in background have the other one 
 * 						go a step back.
 * 
 * 2)The ability to change the background color
 * 
 * 3)Loading from the gallery -> when saving, also save the sectorLines and drawLines lists and when loading
 * 								 change all the references in the DrawComponent class
 * 
 * 4)When resizeing or changing the number of sectors paint directly on the component and 
 * 	,at the same time, update the buffered image in background on a separete thred. 
 * 
 */
