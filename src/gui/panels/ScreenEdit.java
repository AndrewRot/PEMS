// PEMS (Police Evidence Management System) Version 0.1
// Copyright 2015 - Jacob Jones and Andrew Rottier
// ScreenEdit.java

package gui.panels;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;
import tools.*;
import gui.*;

public class ScreenEdit extends JPanel implements ActionListener, MouseListener
{
	
	private FrameManager manager;
	private Stack<BufferedImage> selectedImageHistorySaved;
	private Stack<BufferedImage> selectedImageHistoryUndone;
	private ArrayList<Thumbnail> caseThumbnails;
	private BufferedImage selectedImage;
	private Box mainContainer;
	private Box selectedImageContainer;
	private Box caseThumbnailContainer;
	private JLabel selectedImageLabel;
	private JButton loadNextThumbnails;
	private JButton loadPrevThumbnails;
	private JButton continueButton;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu caseMenu;
	private JMenu imageMenu;
	private JMenuItem saveImageMenuItem;
	private JMenuItem renameImageMenuItem;
	private JMenuItem quitMenuItem;
	private JMenuItem undoMenuItem;
	private JMenuItem redoMenuItem;
	private JMenuItem removeImageMenuItem;
	private JMenuItem antiAliasMenuItem;
	private JMenuItem brightenMenuItem;
	private JMenuItem darkenMenuItem;
	private JMenuItem grayscaleMenuItem;
	private JMenuItem resizeMenuItem;
	private JMenuItem rotate90MenuItem;
	private JMenuItem rotate180MenuItem;
	private JMenuItem rotate270MenuItem;
	private String caseNum;
	private String selectedImagePath;
	private String selectedImageName;
	private String selectedImageExt;
	private boolean saved;
	private int caseThumbnailIndex;
	
	public ScreenEdit(FrameManager manager, String caseNum)
	{
		this.manager = manager;
		this.caseNum = caseNum;
		this.saved = true;
		this.caseThumbnailIndex = 0;
		this.selectedImageHistorySaved = new Stack<BufferedImage>();
		this.selectedImageHistoryUndone = new Stack<BufferedImage>();
		this.caseThumbnails = this.getCaseThumbnails();
		this.mainContainer = Box.createVerticalBox();
		this.selectedImageContainer = Box.createHorizontalBox();
		this.caseThumbnailContainer = Box.createHorizontalBox();
		this.caseThumbnailContainer.setBorder(BorderFactory.createLineBorder(Color.black));
		this.refreshCaseThumbnails(0);
		this.loadFirstImage();
		this.populateMainContainer();
		this.add(this.mainContainer);
		this.constructMenuBar();
		this.manager.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		this.revalidate();
		this.repaint();
	}
	
	public void actionPerformed(ActionEvent e)
	{ 
		if (e.getSource() == this.loadNextThumbnails)
		{
        	if (this.caseThumbnailIndex + 8 < this.caseThumbnails.size())
        	{
        		this.refreshCaseThumbnails(this.caseThumbnailIndex + 8);
        	}
		}
		else if (e.getSource() == this.loadPrevThumbnails)
		{
          	if (this.caseThumbnailIndex >= 8)
        	{
        		this.refreshCaseThumbnails(this.caseThumbnailIndex - 8);
        	}
		}
		else if (e.getSource() == this.saveImageMenuItem)
		{
			this.saveImage();
		}
		else if (e.getSource() == this.renameImageMenuItem)
		{
			this.manager.displayRenameDialogue(this);
		}
		else if (e.getSource() == this.quitMenuItem)
		{
			if (this.saved)
			{
				System.exit(0);
			}
			else
			{
				this.manager.displayQuitWarningDialogue(this);
			}
		}
		else if (e.getSource() == this.undoMenuItem)
		{
			this.undo();
		}
		else if (e.getSource() == this.redoMenuItem)
		{
			this.redo();
		}
		else if (e.getSource() == this.removeImageMenuItem)
		{
			this.manager.displayRemoveWarningDialogue(this);
		}
	    else if (e.getSource() == this.antiAliasMenuItem)
		{
	    	this.antiAlias();
		}
		else if (e.getSource() == this.brightenMenuItem)
		{
			this.brighten();
		}
		else if (e.getSource() == this.darkenMenuItem)
		{
			this.darken();
		}
		else if (e.getSource() == this.grayscaleMenuItem)
		{
			this.grayscale();
		}
		else if (e.getSource() == this.resizeMenuItem)
		{
			this.manager.displayResizeDialogue(this, this.selectedImage.getWidth(), this.selectedImage.getHeight());
		}
		else if (e.getSource() == this.rotate90MenuItem)
		{
			this.rotate90();
		}
		else if (e.getSource() == this.rotate180MenuItem)
		{
			this.rotate180();
		}
		else if (e.getSource() == this.rotate270MenuItem)
		{
			this.rotate270();
		}
		else if (e.getSource() == this.continueButton)
		{
			this.manager.removeMenuBar();
			this.manager.closeRenameDialogue();
			this.manager.closeResizeDialogue();
			this.manager.closeQuitWarningDialogue();
			this.manager.closeRemoveWarningDialogue();
			this.manager.closeSwitchWarningDialogue();
        	this.manager.pushPanel(new ScreenFinish(manager, caseNum), "PEMS - Finish");
		}
	}
	
	public void mouseClicked(MouseEvent e) 
	{
		if (e.getSource() instanceof Thumbnail)
		{
			Thumbnail selectedThumbnail = (Thumbnail)e.getSource();
			this.selectedImage = null;
			try 
			{      
				this.selectedImage = ImageIO.read(new File(selectedThumbnail.getFilePath()));
			} 
			catch (IOException e1)
			{
				System.out.println("Error - Unable to import selected image");
				e1.printStackTrace();
				return;
			}
			this.selectedImagePath = selectedThumbnail.getFilePath();
			this.selectedImageName = selectedThumbnail.getFileName();
			this.selectedImageExt = selectedThumbnail.getFileExt();
			this.selectedImageHistorySaved.clear();
			this.selectedImageHistoryUndone.clear();
			this.selectedImageHistorySaved.add(this.selectedImage);
			this.refreshSelectedImage();
		}
	}

	public void mousePressed(MouseEvent e) 
	{
		return;
	}

	public void mouseReleased(MouseEvent e) 
	{
		return;
	}

	public void mouseEntered(MouseEvent e) 
	{
		return;
	}

	public void mouseExited(MouseEvent e) 
	{
		return;
	}
	
	private void refreshSelectedImage()
	{
		this.selectedImageContainer.removeAll();
		if (this.selectedImage.getHeight() > 500)
		{
			this.selectedImageLabel = ComponentGenerator.generateLabel(ImageEditor.resizeImage(this.selectedImage, 500), CENTER_ALIGNMENT);
		}
		else if (this.selectedImage.getWidth() > 1000)
		{
			this.selectedImageLabel = ComponentGenerator.generateLabel(ImageEditor.resizeImage(this.selectedImage, 1000), CENTER_ALIGNMENT);
		}
		else
		{
			this.selectedImageLabel = ComponentGenerator.generateLabel(this.selectedImage, CENTER_ALIGNMENT);
		}
		this.selectedImageContainer.setMaximumSize(new Dimension(1000, 500));
		this.selectedImageContainer.setMinimumSize(new Dimension(1000, 500));
		this.selectedImageContainer.add(Box.createHorizontalGlue());
		this.selectedImageContainer.add(Box.createVerticalStrut(500));
		this.selectedImageContainer.add(this.selectedImageLabel);
		this.selectedImageContainer.add(Box.createVerticalStrut(500));
		this.selectedImageContainer.add(Box.createHorizontalGlue());
		this.revalidate();
		this.repaint();
	}
	
	private ArrayList<Thumbnail> getCaseThumbnails()
	{
		ArrayList<Thumbnail> thumbnailList = new ArrayList<Thumbnail>();
	    File directory = new File("cases", "/" + this.caseNum + "/");
		String[] fileNames = directory.list();
		for (int i = 0; i < fileNames.length; i++)
		{
			String currentFileName = fileNames[i].substring(0, fileNames[i].indexOf('.')).toLowerCase();
			String currentExtension = fileNames[i].substring(fileNames[i].indexOf('.'), fileNames[i].length()).toLowerCase();
			if ((currentExtension.equalsIgnoreCase(".png") || currentExtension.equalsIgnoreCase(".jpg") || currentExtension.equalsIgnoreCase(".jpeg")))
			{
				BufferedImage currentImage = null;
				String currentPath = "cases/" + this.caseNum + "/" + fileNames[i];
			    try 
			    {   
			    	 currentImage = ImageIO.read(new File(currentPath));
			    }
			    catch (IOException e)
			    {
			    	System.out.println("Error - Unable to read image into memory");
			    	e.printStackTrace();
				    return null;
			    }	    	
			    Thumbnail currentThumb = ComponentGenerator.generateThumbnail(ImageEditor.resizeThumbnail(currentImage, 80), currentPath, currentFileName, currentExtension);
				currentThumb.setAlignmentX(CENTER_ALIGNMENT);
			    currentThumb.addMouseListener(this);
			    thumbnailList.add(currentThumb);
			}
		}
	    return thumbnailList;
	}
	
	private void refreshCaseThumbnails(int caseThumbnailIndex)
	{
		this.loadNextThumbnails = ComponentGenerator.generateButton("Next     >", this);
		this.loadPrevThumbnails = ComponentGenerator.generateButton("<     Prev", this); 
		this.caseThumbnailContainer.removeAll();
		this.caseThumbnailIndex = caseThumbnailIndex;
		this.caseThumbnailContainer.add(this.loadPrevThumbnails);
		for (int i = 0; i < 8; i++)
		{
			Box col = Box.createVerticalBox();
			col.setMinimumSize(new Dimension(100, 100));
			col.setMaximumSize(new Dimension(100, 100));
			if (this.caseThumbnailIndex < this.caseThumbnails.size())
			{
				col.add(Box.createVerticalGlue());
				col.add(Box.createHorizontalStrut(100));
				col.add(this.caseThumbnails.get(this.caseThumbnailIndex));
				col.add(Box.createHorizontalStrut(100));
				col.add(Box.createVerticalGlue());
			}
			else
			{
				col.add(Box.createVerticalGlue());
				col.add(Box.createHorizontalStrut(100));
				col.add(Box.createVerticalGlue());
			}
			this.caseThumbnailContainer.add(col);
			this.caseThumbnailIndex++;
		}
		this.caseThumbnailContainer.add(this.loadNextThumbnails);
		this.caseThumbnailContainer.add(Box.createVerticalStrut(100));
		this.caseThumbnailIndex = caseThumbnailIndex;
		this.revalidate();
		this.repaint();
	}
	
	private void constructMenuBar()
	{
		this.menuBar = new JMenuBar();
		this.fileMenu = new JMenu("File");
		this.editMenu = new JMenu("Edit");
		this.caseMenu = new JMenu("Case");
		this.imageMenu = new JMenu("Image");
		this.saveImageMenuItem = ComponentGenerator.generateMenuItem("Save Image", this, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		this.renameImageMenuItem = ComponentGenerator.generateMenuItem("Rename Image", this);
		this.quitMenuItem = ComponentGenerator.generateMenuItem("Quit", this, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		this.undoMenuItem = ComponentGenerator.generateMenuItem("Undo", this, KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		this.redoMenuItem = ComponentGenerator.generateMenuItem("Redo", this, KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		this.removeImageMenuItem = ComponentGenerator.generateMenuItem("Remove Image from Case", this);
		this.antiAliasMenuItem = ComponentGenerator.generateMenuItem("Apply Anti Aliasing", this);
		this.brightenMenuItem = ComponentGenerator.generateMenuItem("Brighten by 10%", this);
		this.darkenMenuItem = ComponentGenerator.generateMenuItem("Darken by 10%", this);
		this.grayscaleMenuItem = ComponentGenerator.generateMenuItem("Convert to Grayscale", this);
		this.resizeMenuItem = ComponentGenerator.generateMenuItem("Resize Image", this);
		this.rotate90MenuItem = ComponentGenerator.generateMenuItem("Rotate Image 90\u00b0 Right", this);
		this.rotate180MenuItem = ComponentGenerator.generateMenuItem("Rotate Image 180\u00b0 Right", this);
		this.rotate270MenuItem = ComponentGenerator.generateMenuItem("Rotate Image 270\u00b0 Right", this);
		this.fileMenu.add(this.saveImageMenuItem);
		this.fileMenu.add(this.renameImageMenuItem);
		this.fileMenu.addSeparator();
		this.fileMenu.add(this.quitMenuItem);
		this.editMenu.add(this.undoMenuItem);
		this.editMenu.add(this.redoMenuItem);
		this.caseMenu.add(this.removeImageMenuItem);
		this.imageMenu.add(this.antiAliasMenuItem);
		this.imageMenu.addSeparator();
		this.imageMenu.add(this.brightenMenuItem);
		this.imageMenu.add(this.darkenMenuItem);
		this.imageMenu.addSeparator();
		this.imageMenu.add(this.grayscaleMenuItem);
		this.imageMenu.addSeparator();
		this.imageMenu.add(this.resizeMenuItem);
		this.imageMenu.addSeparator();
		this.imageMenu.add(this.rotate90MenuItem);
		this.imageMenu.add(this.rotate180MenuItem);
		this.imageMenu.add(this.rotate270MenuItem);
		this.menuBar.add(this.fileMenu);
		this.menuBar.add(this.editMenu);
		this.menuBar.add(this.caseMenu);
		this.menuBar.add(this.imageMenu);
		this.manager.setMenuBar(this.menuBar);
	}
	
	private String getImageFileType(String extension)
	{
		if (extension.equalsIgnoreCase(".png"))
		{
			return "png";
		}
		else if (extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".jpeg"))
		{
			return "jpg";
		}
		return null;
	}
	
	private void populateMainContainer()
	{
		this.continueButton = ComponentGenerator.generateButton("Continue", this, CENTER_ALIGNMENT);
		this.mainContainer.add(this.selectedImageContainer);
		this.mainContainer.add(Box.createVerticalStrut(40));
		this.mainContainer.add(this.caseThumbnailContainer);
		this.mainContainer.add(Box.createVerticalStrut(10));
		this.mainContainer.add(this.continueButton);
	}
	
	private void loadFirstImage()
	{
		Thumbnail selectedThumbnail = this.caseThumbnails.get(0);
		this.selectedImage = null;
		try 
		{      
			this.selectedImage = ImageIO.read(new File(selectedThumbnail.getFilePath()));
		} 
		catch (IOException e)
		{
			System.out.println("Error - Unable to import selected image");
			e.printStackTrace();
			return;
		}
		this.selectedImagePath = selectedThumbnail.getFilePath();
		this.selectedImageName = selectedThumbnail.getFileName();
		this.selectedImageExt = selectedThumbnail.getFileExt();
		this.selectedImageHistorySaved.push(this.selectedImage);
		this.refreshSelectedImage();
	}
	
	private void saveImage()
	{
		int backupIncrement = 0;
		if (Files.exists(Paths.get("backups/" + this.caseNum + "/" +  this.selectedImageName + this.selectedImageExt)))
		{
			backupIncrement++;
			while (Files.exists(Paths.get("backups/" + this.caseNum + "/" +  this.selectedImageName + " (" + backupIncrement + ")" + this.selectedImageExt)))
			{
				backupIncrement++;
			}
		}
		try 
		{
			if (backupIncrement == 0)
			{
				ImageIO.write(this.selectedImage, this.getImageFileType(this.selectedImageExt), new File("backups/" + this.caseNum + "/" + this.selectedImageName + this.selectedImageExt));
			}
			else
			{
				ImageIO.write(this.selectedImage, this.getImageFileType(this.selectedImageExt), new File("backups/" + this.caseNum + "/" + this.selectedImageName + " (" + backupIncrement + ")" + this.selectedImageExt));
			}
		    ImageIO.write(this.selectedImage, this.getImageFileType(this.selectedImageExt), new File("cases/" + this.caseNum + "/" + this.selectedImageName + this.selectedImageExt));
		} 
		catch (IOException e) 
		{
		    System.out.println("Error - Save failed");
		    e.printStackTrace();
		    return;
		}
		this.saved = true;
	}
	
	public void renameImage(String newName)
	{
		File oldFile = new File(this.selectedImagePath);
		File newFile = new File("cases/" + this.caseNum + "/" + newName + this.selectedImageExt);
		oldFile.renameTo(newFile);
	}
	
	private void undo()
	{
		if (this.selectedImageHistorySaved.size() > 1)
		{
			this.selectedImageHistoryUndone.push(this.selectedImageHistorySaved.pop());
			this.selectedImage = this.selectedImageHistorySaved.peek();
			this.refreshSelectedImage();
			this.saved = false;
		}
	}
	
	private void redo()
	{
		if (this.selectedImageHistoryUndone.size() > 0)
		{
			this.selectedImageHistorySaved.push(this.selectedImageHistoryUndone.pop());
			this.selectedImage = this.selectedImageHistorySaved.peek();
			this.refreshSelectedImage();
			this.saved = false;
		}
	}
	
	private void antiAlias()
	{
		this.selectedImageHistoryUndone.clear();
		this.selectedImage = ImageEditor.applyAntiAliasing(this.selectedImage);
		this.selectedImageHistorySaved.push(this.selectedImage);
		this.refreshSelectedImage();
		this.saved = false;
	}
	
	private void brighten()
	{
		this.selectedImageHistoryUndone.clear();
		this.selectedImage = ImageEditor.brightenImage(this.selectedImage);
		this.selectedImageHistorySaved.push(this.selectedImage);
		this.refreshSelectedImage();
		this.saved = false;
	}
	
	private void darken()
	{  
		this.selectedImageHistoryUndone.clear();
		this.selectedImage = ImageEditor.darkenImage(this.selectedImage);
		this.selectedImageHistorySaved.push(this.selectedImage);
		this.refreshSelectedImage();
		this.saved = false;
	}
	
	private void grayscale()
	{
		this.selectedImageHistoryUndone.clear();
		this.selectedImage = ImageEditor.toGrayscale(this.selectedImage);
		this.selectedImageHistorySaved.push(this.selectedImage);
		this.refreshSelectedImage();
		this.saved = false;
	}
	
	public void resizeImage(int newWidth, int newHeight)
	{
		this.selectedImageHistoryUndone.clear();
		this.selectedImage = ImageEditor.resizeImage(this.selectedImage, newWidth, newHeight);
		this.selectedImageHistorySaved.push(this.selectedImage);
		this.refreshSelectedImage();
		this.saved = false;
	}
	
	private void rotate90()
	{
		this.selectedImageHistoryUndone.clear();
		this.selectedImage = ImageEditor.rotateRight90(this.selectedImage);
		this.selectedImageHistorySaved.push(this.selectedImage);
		this.refreshSelectedImage();
		this.saved = false;
	}
	
	private void rotate180()
	{
		this.selectedImageHistoryUndone.clear();
		this.selectedImage = ImageEditor.rotateRight180(this.selectedImage);
		this.selectedImageHistorySaved.push(this.selectedImage);
		this.refreshSelectedImage();
		this.saved = false;
	}
	
	private void rotate270()
	{
		this.selectedImageHistoryUndone.clear();
		this.selectedImage = ImageEditor.rotateRight270(this.selectedImage);
		this.selectedImageHistorySaved.push(this.selectedImage);
		this.refreshSelectedImage();
		this.saved = false;
	}

}
