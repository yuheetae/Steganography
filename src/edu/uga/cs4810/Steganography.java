package edu.uga.cs4810;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Menu;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

public class Steganography extends JPanel{ //class start
	
	static int width;
	static int height;
	static BufferedImage image;
	static boolean paint = false;
	static String filePath;

	static JMenuBar menuBar;
	static JMenu mode;
	static JMenuItem decodeMode;
	static JFrame frame;
	
	static JMenuBar menuBar2;
	static JMenu mode2;
	static JMenuItem encodeMode;
	static JFrame frame2;

	public static void createAndShowGUI() {
		EncodeFrame encodeFrame = new EncodeFrame();

	}


	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});//start main
	}


}//class end

class EncodeFrame extends JFrame {
	static JMenuBar menuBar;
	static JMenu mode;
	static JMenuItem decodeMode;
	static JFrame frame;

	EncodeFrame() {
		menuBar = new JMenuBar();
		mode = new JMenu("Mode");
		menuBar.add(mode);
		decodeMode = new JMenuItem("Decode");
		mode.add(decodeMode);

		frame = new JFrame("Steganography Encoding");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLayout(new BorderLayout());

		OriginalImage originalImage = new OriginalImage();
		Message message = new Message();
		NewImage newImage = new NewImage();

		frame.setJMenuBar(menuBar);

		frame.add(originalImage, BorderLayout.LINE_START);
		frame.add(message, BorderLayout.CENTER);
		frame.add(newImage, BorderLayout.LINE_END);
		frame.setBounds(0, 0, 1200, 700);
		frame.setVisible(true);

		decodeMode.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				frame.dispose();

				DecodeFrame decodeFrame = new DecodeFrame();
			}
		});

	}
}

class DecodeFrame extends JFrame {
	static JFrame frame;
	DecodeFrame() {
		
		JMenuBar menuBar = new JMenuBar();
		JMenu mode = new JMenu("Mode");
		menuBar.add(mode);
		JMenuItem encodeMode = new JMenuItem("Encode");
		mode.add(encodeMode);
		
		frame = new JFrame("Steganography Decoding");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		DecodePanel decodePanel = new DecodePanel();
		DecodedMessage message = new DecodedMessage();
		frame.setLayout(new BorderLayout());
		frame.setJMenuBar(menuBar);
		frame.add(decodePanel, BorderLayout.LINE_START);
		frame.add(message, BorderLayout.LINE_END);
		frame.setBounds(0, 0, 800, 700);
		frame.setVisible(true);
		
		encodeMode.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				frame.dispose();

				EncodeFrame encodeFrame = new EncodeFrame();
			}
		});

	}
	
}

class DecodePanel extends JPanel {
	
	JLabel chooseImage;
	ImagePanel imagePanel;
	JButton openImage;
	static String filePath;
	
	DecodePanel() {
		chooseImage = new JLabel("Choose an Image");
		chooseImage.setFont(new Font("Serif", Font.BOLD, 17));
		imagePanel = new ImagePanel();
		imagePanel.setPreferredSize(new Dimension(369, 530));
		imagePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		openImage = new JButton("Open File");
		
		
		MigLayout mig = new MigLayout(
				"",
				"15[center]",
				"30[]15[]25[]0"
				);
		this.setLayout(mig);
		this.add(chooseImage, "cell 0 0");
		this.add(imagePanel, "cell 0 1 2");
		this.add(openImage, "cell 0 2");
		
		openImage.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					DecodePanel.setFilePath(fc.getSelectedFile().getAbsolutePath());
				}
				
				try {
					BufferedImage original = ImageIO.read(new File(DecodePanel.getFilePath()));
					
					int xBound = imagePanel.getWidth(), yBound = imagePanel.getHeight();
					int originalWidth = original.getWidth(), originalHeight = original.getHeight();
					int newWidth = originalWidth, newHeight = originalHeight;
					
					if (originalWidth > xBound) {
						newWidth = xBound;
						newHeight = (newWidth*originalHeight)/originalWidth;
					}
					if(newHeight > yBound) {
						newHeight = yBound;
						newWidth = (newHeight*originalWidth)/originalHeight;
					}
					
					imagePanel.setSize(newWidth, newHeight);
					
					BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, original.getType());
					Graphics2D g = scaledImage.createGraphics();
					g.drawImage(original, 0, 0, newWidth, newHeight, null);
					g.dispose();
					g.setComposite(AlphaComposite.Src);
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
					
					imagePanel.setImage(scaledImage);
					
					imagePanel.repaint();
					
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
	}
	
	 static String getFilePath() {
		 
		 return filePath; 
	 }
	 
	 public static void setFilePath(String path) {
		 filePath = path;
	 }
	
}

class DecodedMessage extends JPanel {
	String filePath;
	JLabel message;
	JTextArea textArea;
	JButton decode;
	
	DecodedMessage() {
		filePath = DecodePanel.getFilePath();
		message = new JLabel("Decoded Message");
		message.setFont(new Font("Serif", Font.BOLD, 17));
		textArea = new JTextArea(10, 25);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		decode = new JButton("Decode");
		textArea.setLineWrap(true);
		
		MigLayout mig = new MigLayout(
				"",
				"40[center]",
				"30[]15[]25[]"
				);
		this.setLayout(mig);
		
		this.add(message, "cell 0 0");
		this.add(scrollPane, "cell 0 1");
		this.add(decode, "cell 0 2");
		
		decode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(DecodePanel.getFilePath());
				filePath = Coder.decode(DecodePanel.getFilePath());
				textArea.setText(filePath);
			}
		});
	}
	
}
 
class OriginalImage extends JPanel {
	static JLabel chooseImage;
	static JButton openImage;
	static String filePath = null;
	ImagePanel imagePanel = new ImagePanel();
	static int width;
	static int height;
	static BufferedImage image;
	static boolean paint = false;
	
	 OriginalImage() {
		 image = null;
		setPreferredSize(new Dimension(400, 700));
		chooseImage = new JLabel("Choose an Image");
		chooseImage.setFont(new Font("Serif", Font.BOLD, 17));
	    openImage = new JButton("Open File");
	    
		//imagePanel = new JPanel();
		imagePanel.setPreferredSize(new Dimension(400, 530));
		imagePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		
		MigLayout mig = new MigLayout(
				"",
				"15[center]",
				"30[]15[]25[]0"
				);
		this.setLayout(mig);
		this.add(chooseImage, "cell 0 0");
		this.add(imagePanel, "cell 0 1 2");
		this.add(openImage, "cell 0 2");
		
		
		openImage.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					OriginalImage.setFilePath(fc.getSelectedFile().getAbsolutePath());
					//System.out.println(getFilePath());
				}
				
				try {
					BufferedImage original = ImageIO.read(new File(OriginalImage.getFilePath()));
					
					int xBound = imagePanel.getWidth(), yBound = imagePanel.getHeight();
					int originalWidth = original.getWidth(), originalHeight = original.getHeight();
					int newWidth = originalWidth, newHeight = originalHeight;
					
					if (originalWidth > xBound) {
						newWidth = xBound;
						newHeight = (newWidth*originalHeight)/originalWidth;
					}
					if(newHeight > yBound) {
						newHeight = yBound;
						newWidth = (newHeight*originalWidth)/originalHeight;
					}
					
					imagePanel.setSize(newWidth, newHeight);
					
					BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, original.getType());
					Graphics2D g = scaledImage.createGraphics();
					g.drawImage(original, 0, 0, newWidth, newHeight, null);
					g.dispose();
					g.setComposite(AlphaComposite.Src);
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
					
					imagePanel.setImage(scaledImage);
					
					imagePanel.repaint();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
	}
	 
	 static String getFilePath() {
		 
		 return filePath; 
	 }
	 
	 public static void setFilePath(String path) {
		 filePath = path;
	 }
}

class ImagePanel extends JPanel {
	static JPanel imagePanel;
	static int width, height;
	static BufferedImage image;
	
	ImagePanel() {
		image = null;
		imagePanel = new JPanel();
		imagePanel.setPreferredSize(new Dimension(400, 530));
		imagePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	}
	
	public void setImage(BufferedImage i) {
		image = i;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setSize(int w, int h) {
		width = w;
		height = h;
	}
	
	public int getW() {
		return width;
	}
	
	public int getH() {
		return height;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int x = (369 - getW()) / 2;
		int y = (530 - getH()) / 2;
		
		g.drawImage(getImage(), x, y, this);
		}
}

class Message extends JPanel {
	static String filePath = null;
	static JTextArea userText;
	static String hiddenMessage;
	static BufferedImage image, resizedImage;
	Message() {
		setPreferredSize(new Dimension(400, 700));
		//setBackground(Color.GRAY);
		JLabel hiddenMessage = new JLabel("Hidden Message");
		hiddenMessage.setFont(new Font("Serif", Font.BOLD, 17));
		userText = new JTextArea(10, 25);
		JScrollPane scrollPane = new JScrollPane(userText);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		JButton hideText = new JButton("Hide Text");
		userText.setLineWrap(true);
		
		
		MigLayout mig = new MigLayout(
				"",
				"40[center]",
				"30[]15[]25[]"
				);
		this.setLayout(mig);
		
		this.add(hiddenMessage, "cell 0 0");
		this.add(scrollPane, "cell 0 1");
		this.add(hideText, "cell 0 2");
		
		
		hideText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setMessage(userText.getText());
				setFilePath(OriginalImage.filePath);
				try {
					setImage(Coder.encode(getFilePath(), getMessage()));
					image = Message.getImage();
					System.out.println("fnjdafndsjaflnsda:  " + image.getWidth()*image.getHeight());
					
					if(((getMessage().length()*8)+8) > (image.getWidth()*image.getHeight())) {
						JOptionPane.showMessageDialog(null,
							    "Message size is too big",
							    "Inane error",
							    JOptionPane.ERROR_MESSAGE);
					}
					else {
					
					int xBound = NewImage.imagePanel.getWidth(), yBound = NewImage.imagePanel.getHeight();
					int originalWidth = image.getWidth(), originalHeight = image.getHeight();
					int newWidth = originalWidth, newHeight = originalHeight;
					
					if (originalWidth > xBound) {
						newWidth = xBound;
						newHeight = (newWidth*originalHeight)/originalWidth;
					}
					if(newHeight > yBound) {
						newHeight = yBound;
						newWidth = (newHeight*originalWidth)/originalHeight;
					}
					
					NewImage.imagePanel.setSize(newWidth, newHeight);
					
					BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, image.getType());
					Graphics2D g = scaledImage.createGraphics();
					g.drawImage(image, 0, 0, newWidth, newHeight, null);
					g.dispose();
					g.setComposite(AlphaComposite.Src);
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
					g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
					
					NewImage.imagePanel.setImage(scaledImage);
					
					NewImage.imagePanel.repaint();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
	}

	public static BufferedImage getImage() {
		return image;
	}
	
	private void setImage(BufferedImage i) {
		image = i;
	}
	
	private String getMessage() {
		return hiddenMessage;
	}
	
	public void setMessage(String message) {
		hiddenMessage = message;
	}
	
	 private String getFilePath() {
		 
		 return filePath;
	 }
	 
	 static void setFilePath(String path) {
		 filePath = path;
	 }
}

class NewImage extends JPanel {
	static JLabel newImage;
	static JButton save;
	static ImagePanel imagePanel = new ImagePanel();
	
	NewImage() {
		setPreferredSize(new Dimension(400, 700));
		//setBackground(Color.GRAY);
		newImage = new JLabel("New Encoded Steg Image");
		newImage.setFont(new Font("Serif", Font.BOLD, 17));
		imagePanel.setPreferredSize(new Dimension(400, 530));
		imagePanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		save = new JButton("Save Image");

		
		MigLayout mig = new MigLayout(
				"",
				"15[center]",
				"30[]15[]25[]0"
				);
		this.setLayout(mig);
		this.add(newImage, "cell 0 0");
		this.add(imagePanel, "cell 0 1");
		this.add(save, "cell 0 2");
		
		save.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) {
				
				

				
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showSaveDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					//OriginalImage.setFilePath(fc.getSelectedFile().getAbsolutePath());
					File fileToSave = fc.getSelectedFile();
					if(!fc.getSelectedFile().getAbsolutePath().endsWith(".png")){
					    fileToSave = new File(fc.getSelectedFile() + ".png");
					}
					try {
						ImageIO.write(Message.getImage(), "png", fileToSave);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		});
		
	}
}

























