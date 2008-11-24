package com.md5.viewer.selector.gui;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import com.md5.viewer.selector.gui.enumn.EButton;
import com.md5.viewer.selector.gui.handler.MD5MouseHandler;

/**
 * <code>AnimationSelectorGUI</code> defines the actual {@link Swing} GUI
 * components for the selector.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-24-2008 12:42 EST
 * @version Modified date: 11-24-2008 14:56 EST
 */
public class AnimationSelectorGUI extends JFrame {
    /**
	 * Serial version.
	 */
	private static final long serialVersionUID = -4933057493044539956L;
	/**
	 * The <code>MD5MouseHandler</code> instance.
	 */
	private final MD5MouseHandler handler;
	/**
	 * Various {@link Swing} widgets.
	 */
    private JButton btnAdd;
    private JButton btnBaseAnimLoad;
    private JButton btnHierarchyLoad;
    private JButton btnRemove;
    private JButton btnOK;
    private JButton btnCancel;
    private JScrollPane jScrollPane1;
    private JLabel lblAnimations;
    private JLabel lblBaseAnimation;
    private JLabel lblBodyHierarchy;
    private JList lstAnimations;
    private JTextField txtBaseAnimation;
    private JTextField txtHierarchyFile;
    /**
     * The <code>DefaultListModel</code> instance.
     */
    private DefaultListModel listModel;
    
    /**
     * Constructor of <code>AnimationSelectorGUI</code>.
     * @param handler The <code>MD5MouseHandler</code> instance.
     */
    public AnimationSelectorGUI(MD5MouseHandler handler) {
    	this.handler = handler;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AnimationSelectorGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(AnimationSelectorGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AnimationSelectorGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(AnimationSelectorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialize the GUI components.
     */
    public void initComponents() {
    	this.setResizable(false);
        jScrollPane1 = new JScrollPane();
        lstAnimations = new JList();
        lblBodyHierarchy = new JLabel();
        txtHierarchyFile = new JTextField();
        btnHierarchyLoad = new JButton();
        lblBaseAnimation = new JLabel();
        txtBaseAnimation = new JTextField();
        btnBaseAnimLoad = new JButton();
        btnAdd = new JButton();
        lblAnimations = new JLabel();
        btnRemove = new JButton();
        btnOK = new JButton();
        btnCancel = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.listModel = new DefaultListModel();
        lstAnimations.setBorder(BorderFactory.createEtchedBorder());
        lstAnimations.setModel(this.listModel);
        jScrollPane1.setViewportView(lstAnimations);

        lblBodyHierarchy.setLabelFor(txtHierarchyFile);
        lblBodyHierarchy.setText("Model Hierarchy");

        txtHierarchyFile.setEditable(false);
        txtHierarchyFile.setText("<Select A File>");

        btnHierarchyLoad.setText(EButton.SelectHierarchy.getText());
        btnHierarchyLoad.setToolTipText("Select a Model Hierarchy File");
        btnHierarchyLoad.setName(EButton.SelectHierarchy.name());
        btnHierarchyLoad.addMouseListener(this.handler);

        lblBaseAnimation.setText("Base Animation");

        txtBaseAnimation.setEditable(false);
        txtBaseAnimation.setText("<Select A File>");

        btnBaseAnimLoad.setText(EButton.SelectBaseAnimation.getText());
        btnBaseAnimLoad.setToolTipText("Select a base animation file");
        btnBaseAnimLoad.setName(EButton.SelectBaseAnimation.name());
        btnBaseAnimLoad.addMouseListener(this.handler);

        btnAdd.setText(EButton.AddAnimation.getText());
        btnAdd.setToolTipText("Add an animation for playback");
        btnAdd.setName(EButton.AddAnimation.name());
        btnAdd.addMouseListener(this.handler);

        lblAnimations.setText("Animations");

        btnRemove.setText(EButton.RemoveAnimation.getText());
        btnRemove.setToolTipText("Remove the selected animation");
        btnRemove.setName(EButton.RemoveAnimation.name());
        btnRemove.addMouseListener(this.handler);

        btnOK.setText(EButton.OK.getText());
        btnOK.setToolTipText("Enter the MD5 Viewer");
        btnOK.setName(EButton.OK.name());
        btnOK.addMouseListener(this.handler);

        btnCancel.setText(EButton.Cancel.getText());
        btnCancel.setToolTipText("Exit the application");
        btnCancel.setName(EButton.Cancel.name());
        btnCancel.addMouseListener(this.handler);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblBodyHierarchy)
                    .addComponent(lblBaseAnimation)
                    .addComponent(lblAnimations)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(txtHierarchyFile, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                            .addComponent(txtBaseAnimation, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(btnRemove)
                            .addComponent(btnAdd, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                            .addComponent(btnBaseAnimLoad, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                            .addComponent(btnHierarchyLoad, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnOK, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnOK, btnCancel});

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnAdd, btnBaseAnimLoad, btnHierarchyLoad, btnRemove});

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jScrollPane1, txtBaseAnimation, txtHierarchyFile});

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblBodyHierarchy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHierarchyFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHierarchyLoad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBaseAnimation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBaseAnimation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBaseAnimLoad))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAnimations)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemove))
                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }
    
    /**
	 * Add the given animation text to the chain list.
	 * @param text The <code>String</code> animation file text.
	 */
	public void addAnimation(String text) {
		this.listModel.addElement(text);
	}

	/**
	 * Remove the given animation text from the chain list.
	 * @param text The <code>String</code> animation file text.
	 */
	public void removeAnimation(String text) {
		this.listModel.removeElement(text);
	}

	/**
     * Set the text for the hierarchy.
     * @param text The <code>String</code> text.
     */
    public void setHierarchyText(String text) {
    	this.txtHierarchyFile.setText(text);
    }
    
    /**
     * Set the text for the base animation.
     * @param text The <code>String</code> text.
     */
    public void setBaseAnimText(String text) {
    	this.txtBaseAnimation.setText(text);
    }
    
    /**
     * Get the selected animation file name.
     * @return The selected <code>String</code> file name.
     */
    public String getSelectedAnim() {
    	return (String)this.listModel.get(this.lstAnimations.getSelectedIndex());
    }
}
