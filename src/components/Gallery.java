package components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

public class Gallery extends JFrame 
{
	// The images will not appear in real time in the gallery
	// if you save an image while the gallery is open you will have to close it
	// and reopen in again.
	private static final String imagedir = System.getProperty("user.dir") + File.separator + "images" + File.separator;

	private JLabel photographLabel = new JLabel();
	private JToolBar buttonBar = new JToolBar();
	private ArrayList<String> imageFileNames = new ArrayList<String>();
	private DeleteButton deleteButton;
	private MissingIcon placeholderIcon = new MissingIcon();

	public class DeleteButton extends JButton {
		int imageIndex;

		public DeleteButton(String string) {
			super(string);
		}
	}

	/*
	 * Default constructor for the demo.
	 */
	public Gallery() {
		deleteButton = new DeleteButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Path path = Paths.get(imagedir + imageFileNames.get(deleteButton.imageIndex - 1));
				try {
					Files.delete(path);
				} catch (NoSuchFileException x) {
					System.err.format("%s: no such" + " file or directory%n", path);
				} catch (DirectoryNotEmptyException x) {
					System.err.format("%s not empty%n", path);
				} catch (IOException x) {
					// File permission problems are caught here.
					System.err.println(x);
				}
				imageFileNames.remove(deleteButton.imageIndex - 1);
				buttonBar.remove(deleteButton.imageIndex);
				buttonBar.revalidate();
				buttonBar.repaint();
			}
		});

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Gallery: Please Select an Image");

		// A label for displaying the pictures
		photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
		photographLabel.setHorizontalTextPosition(JLabel.CENTER);
		photographLabel.setHorizontalAlignment(JLabel.CENTER);
		photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// We add two glue components. Later in process() we will add thumbnail
		// buttons
		// to the toolbar inbetween thease glue compoents. This will center the
		// buttons in the toolbar.
		buttonBar.add(Box.createGlue());
		buttonBar.add(Box.createGlue());

		JPanel jp1 = new JPanel(new FlowLayout());
		jp1.add(deleteButton);
		add(jp1, BorderLayout.NORTH);

		JScrollPane sc = new JScrollPane(buttonBar);
		sc.setPreferredSize(new Dimension(50, 67));
		add(sc, BorderLayout.SOUTH);

		add(photographLabel, BorderLayout.CENTER);

		setSize(1000, 900);

		// this centers the frame on the screen
		setLocationRelativeTo(null);

		// start the image loading SwingWorker in a background thread
		loadimages.execute();
	}

	/*
	 * SwingWorker class that loads the images a background thread and calls
	 * publish when a new one is ready to be displayed.
	 *
	 * We use Void as the first SwingWroker param as we do not need to return
	 * anything from doInBackground().
	 */
	private SwingWorker<Void, ThumbnailAction> loadimages = new SwingWorker<Void, ThumbnailAction>() {

		/*
		 * Creates full size and thumbnail versions of the target image files.
		 */
		@Override
		protected Void doInBackground() throws Exception {
			imageFileNames.clear();

			File folder = new File(imagedir);
			String[] list = folder.list();

			for (int i = 0; i < list.length; i++) {
				if (list[i].endsWith(".png")) {
					imageFileNames.add(list[i]);
				}
			}

			for (int i = 0; i < list.length; i++) {
				ImageIcon icon;
				icon = createImageIcon(imagedir + imageFileNames.get(i), imageFileNames.get(i));

				ThumbnailAction thumbAction;
				if (icon != null) {
					ImageIcon thumbnailIcon = new ImageIcon(getScaledImage(icon.getImage(), 32, 32));
					thumbAction = new ThumbnailAction(icon, thumbnailIcon, imageFileNames.get(i));
				} else {
					// the image failed to load for some reason
					// so load a placeholder instead
					thumbAction = new ThumbnailAction(placeholderIcon, placeholderIcon, imageFileNames.get(i));
				}
				publish(thumbAction);
			}
			// unfortunately we must return something, and only null is valid to
			// return when the return type is void.
			return null;
		}

		/**
		 * Process all loaded images.
		 */
		@Override
		protected void process(List<ThumbnailAction> chunks) {
			for (ThumbnailAction thumbAction : chunks) {
				JButton thumbButton = new JButton(thumbAction);
				// add the new button BEFORE the last glue
				// this centers the buttons in the toolbar
				buttonBar.add(thumbButton, buttonBar.getComponentCount() - 1);
			}
		}
	};

	/**
	 * Creates an ImageIcon if the path is valid.
	 * 
	 * @param String
	 *            - resource path
	 * @param String
	 *            - description of the file
	 */
	protected ImageIcon createImageIcon(String path, String description) {
		if (path != null) {
			return new ImageIcon(path, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Resizes an image using a Graphics2D object backed by a BufferedImage.
	 * 
	 * @param srcImg
	 *            - source image to scale
	 * @param w
	 *            - desired width
	 * @param h
	 *            - desired height
	 * @return - the new resized image
	 */
	private Image getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		return resizedImg;
	}

	/*
	 * Action class that shows the image specified in it's constructor.
	 */
	private class ThumbnailAction extends AbstractAction {

		/*
		 * The icon if the full image we want to display.
		 */
		private Icon displayPhoto;

		/*
		 * @param Icon - The full size photo to show in the button.
		 * 
		 * @param Icon - The thumbnail to show in the button.
		 * 
		 * @param String - The descriptioon of the icon.
		 */
		public ThumbnailAction(Icon photo, Icon thumb, String desc) {
			displayPhoto = photo;

			// The short description becomes the tooltip of a button.
			putValue(SHORT_DESCRIPTION, desc);

			// The LARGE_ICON_KEY is the key for setting the
			// icon when an Action is applied to a button.
			putValue(LARGE_ICON_KEY, thumb);
		}

		/*
		 * Shows the full image in the main area and sets the application title.
		 */
		public void actionPerformed(ActionEvent e) {
			photographLabel.setIcon(displayPhoto);
			setTitle("Gallery: " + getValue(SHORT_DESCRIPTION).toString());

			deleteButton.imageIndex = buttonBar.getComponentIndex((Component) e.getSource());
		}
	}
}
