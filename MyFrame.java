package theGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import components.DrawComponent;
import components.DrawPanel;
import components.Gallery;
import components.Gallery;
import components.LineSpinner;

public class MyFrame extends JFrame
{
	public MyFrame()
	{
		this.setTitle("Drawer");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		
		DrawPanel drawPanel = new DrawPanel();
		this.add(drawPanel, BorderLayout.CENTER);
		
		JPanel jpanel = new JPanel(new FlowLayout());
		jpanel.setPreferredSize(new Dimension(100, 70));
		
		JPanel checkPanel = new JPanel();
		checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
			JCheckBox checkSectors = new JCheckBox("Show sectors");
			checkSectors.addItemListener(new ItemListener() {
											public void itemStateChanged(ItemEvent e)
											{
												DrawComponent dc = drawPanel.getDrawComponent();
												
												 if (e.getStateChange() == ItemEvent.DESELECTED)
												 {
													dc.setShowSectors(false);
												 }
												 else
												 {
													 dc.setShowSectors(true);
												 }
												 dc.repaint();
											}
											});
		checkPanel.add(checkSectors);
		
		JCheckBox checkReflect = new JCheckBox("Reflect in panel");
		checkReflect.addItemListener(new ItemListener() {
										public void itemStateChanged(ItemEvent e)
										{
											DrawComponent dc = drawPanel.getDrawComponent();
											
											 if (e.getStateChange() == ItemEvent.DESELECTED)
											 {
												dc.setReflect(false);
											 }
											 else
											 {
												 dc.setReflect(true);
											 }
										}
										});
		checkPanel.add(checkReflect);
		
		jpanel.add(checkPanel);
		
		jpanel.add(new LineSpinner(drawPanel.drawComponent));		
			
			JButton clearButton = new JButton("Clear");
			clearButton.addActionListener(new ActionListener() {
												public void actionPerformed(ActionEvent e)
												{
													drawPanel.getDrawComponent().setRedraw(true);
													drawPanel.getDrawComponent().setPaintDraw(true);
													drawPanel.getDrawComponent().clearDraw();
												}
											});
		jpanel.add(clearButton);
		
			JButton undoButton = new JButton("Undo");
			undoButton.addActionListener(new ActionListener() {								
												public void actionPerformed(ActionEvent e)
												{
													drawPanel.getDrawComponent().setRedraw(true);
													drawPanel.getDrawComponent().setPaintDraw(true);
													for (int i=0; i<drawPanel.getDrawComponent().getDrawLines().size(); i++)
													{
														if(drawPanel.getDrawComponent().getDrawLines().get(i).getLineIndex() == drawPanel.getDrawComponent().getLineIndex())
														{
														drawPanel.getDrawComponent().getDrawLines().remove(i);
														i--;
														}
													}
													drawPanel.getDrawComponent().setLineIndex(drawPanel.getDrawComponent().getLineIndex() - 1);
													drawPanel.getDrawComponent().repaint();
												}
											});
		jpanel.add(undoButton);
			
			JColorChooser drawCC = new JColorChooser();
			JButton drawingColorButton = new JButton("Drawing Color");
			drawingColorButton.addActionListener(new ActionListener() {
													public void actionPerformed(ActionEvent e)
													{
												        Color color = drawCC.showDialog(jpanel, "Choose Drawing Color", drawPanel.getDrawComponent().getDrawLinesColor() );
												        
												        if (color != null) 
												        	drawPanel.getDrawComponent().setDrawLinesColor(color);
													}
												}); 
		jpanel.add(drawingColorButton);
		
			JColorChooser sectorLinesCC = new JColorChooser();
			JButton sectorLinesColorButton = new JButton("Sector Lines Color");
			sectorLinesColorButton.addActionListener(new ActionListener() {
													public void actionPerformed(ActionEvent e)
													{
												        Color color = sectorLinesCC.showDialog(jpanel, "Choose Sector Lines Color", drawPanel.getDrawComponent().getDrawLinesColor() );
												        
												        if (color != null) 
												        {
												        	drawPanel.getDrawComponent().setSectorLinesColor(color);
												        	drawPanel.getDrawComponent().repaint();
												        }
													}
												}); 
		jpanel.add(sectorLinesColorButton);
		
			JSlider thickSlider = new JSlider(JSlider.HORIZONTAL, 0, 20, 1);
			thickSlider.addChangeListener(new ChangeListener() {
													public void stateChanged(ChangeEvent e) 
													{
														drawPanel.getDrawComponent().setStroke(thickSlider.getValue());
													}
												});
			thickSlider.setMajorTickSpacing(5);
			thickSlider.setMinorTickSpacing(1);
			thickSlider.setPaintTicks(true);
			thickSlider.setPaintLabels(true);
		jpanel.add(thickSlider);
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e)
											{
												String name = JOptionPane.showInputDialog(null,
										                "Name of the project",
										                "Enter the name of the project name",
										                JOptionPane.QUESTION_MESSAGE);
												if(name != null)
												{
													try {
														drawPanel.getDrawComponent().save(name);
													} catch (IOException e1) {
														System.err.println("Error saving the image.");
													}
												}
												else 
													System.out.println("Your image wasn't saved");
											}
										});
	jpanel.add(saveButton);
	
		JButton galleryButton = new JButton("Gallery");
		galleryButton.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e)
											{
											       SwingUtilities.invokeLater(new Runnable() 
											       {
											            public void run() 
											            {
											                Gallery app = new Gallery();
											                app.setVisible(true);
											            }
											        });
											}
										});
	jpanel.add(galleryButton);
		
	this.add(jpanel, BorderLayout.NORTH);
	this.pack();
	this.setMinimumSize(new Dimension(1025, 1025));
	this.setVisible(true);
	}
}
