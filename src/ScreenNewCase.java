// PEMS (Police Evidence Management System) Version 0.1
// Copyright 2015 - Jacob Jones and Andrew Rottier
// ScreenNewCase.java

import java.awt.event.*;
import javax.swing.*;

public class ScreenNewCase extends JPanel
{
	
	private FrameManager manager;
	private Box inputBox;
	private JLabel instructionsLabel;
	private JTextField caseNumField;
	private JButton continueButton;
	
	public ScreenNewCase(FrameManager manager) 
	{
		this.manager = manager;
		this.populateInputBox();
	}
	
	private void populateInputBox()
	{
		this.inputBox = Box.createVerticalBox();
		this.inputBox.add(Box.createVerticalStrut(20));
		this.constructInstructionsLabel();
		this.inputBox.add(Box.createVerticalStrut(60));
		this.constructCaseNumField();
		this.inputBox.add(Box.createVerticalStrut(80));
		this.constructContinueButton();
		this.add(this.inputBox);
	}
	
	private void constructInstructionsLabel()
	{
		this.instructionsLabel = new JLabel("Please enter the case number below:");
		this.instructionsLabel.setFont(this.manager.STANDARD_TEXT_FONT);
		this.instructionsLabel.setForeground(this.manager.STANDARD_TEXT_COLOR);
		this.instructionsLabel.setAlignmentX(CENTER_ALIGNMENT);
		this.inputBox.add(this.instructionsLabel);
	}
	
	private void constructCaseNumField()
	{
		this.caseNumField = new JTextField("Type here...");
		this.caseNumField.setAlignmentX(CENTER_ALIGNMENT);
		this.caseNumField.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				caseNumField.setText("");
			}
			public void focusLost(FocusEvent e)
			{
				return;
			}
		});
		this.inputBox.add(this.caseNumField);
	}
	
	private void constructContinueButton()
	{
		this.continueButton = new JButton("Continue");
	    this.continueButton.setAlignmentX(CENTER_ALIGNMENT);
	    this.continueButton.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed(ActionEvent e)
	    	{
	    		manager.pushPanel(new ScreenImport(manager), "PEMS - Import Images");
	    	}
	    });
		this.inputBox.add(this.continueButton);
	}
	
}