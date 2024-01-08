/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.chesspdfbrowser.view.chess.edittags;

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJFrame;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertControllerInterface;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertPanel;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.general.desktop.generic.DesktopGenericFunctions;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.view.ViewComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author Usuario
 */
public class EditTAGsJFrame extends InternationalizedJFrame
//						extends JFrame
							implements AcceptCancelRevertControllerInterface,
										SelectTAG_controller_interface
{
	public static final String CONF_EDIT_TAGS_WINDOW = "EDIT_TAGS_WINDOW";

	protected static final String _resourcePath = "com/frojasg1/generic/resources/acceptcancel";
	public static final String sa_configurationBaseFileName = "EditTAGsJFrame";

	protected AcceptCancelRevertPanel _acceptPanel = null;

	protected ShowAndFilterTAGsPanel _filterOfTAGsPanel = null;

	protected ChessGame _chessGame = null;
	protected ChessGameHeaderInfo _headerTemporalCopy = null;

	protected ChessGameControllerInterface _controller = null;

	protected Component _parent = null;

	protected static EditTAGsJFrame _instance = null;

	protected String _lastSelectedTagKey = null;

	protected AbstractAction _actionForContent = null;

	protected ViewComponent _viewOfTagContent = null;

	public static  EditTAGsJFrame createInstance( Component parent,
													FilterTAGconfiguration applicationConfiguration,
													ChessGameControllerInterface controller )
	{
		if( _instance == null )
			_instance = new EditTAGsJFrame( parent, applicationConfiguration, controller, null );

		return( _instance );
	}

	public static EditTAGsJFrame instance()
	{
		return( _instance );
	}

	/**
	 * Creates new form EditTAGsJFrame
	 */
	public EditTAGsJFrame( Component parent,
							FilterTAGconfiguration configuration,
							ChessGameControllerInterface controller,
							ChessGame chessGame )
	{
		super( configuration );

		setTitle( "Edit TAGs Frame" );
		
		_parent = parent;
		_controller = controller;

		initComponents();

		initOwnComponents();

		addStyles();

		updateAlwaysOnTopCheckBoxFromConfiguration();

		setChessGame( chessGame );

		setWindowConfiguration();

		_viewOfTagContent = DesktopGenericFunctions.instance().getViewFacilities().createViewComponent( jTP_tagContent );

		SwingUtilities.invokeLater( () -> setTitle() );
//		initNavigatorButtons();
//		this.setAlwaysHighlightFocus( true );
	}

	protected void addStyles()
	{
/*
		StyledDocument sd = jTP_tagContent.getStyledDocument();

		Style defaultStyle = sd.getStyle(StyleContext.DEFAULT_STYLE);

		StyleConstants.setForeground(defaultStyle, Color.BLUE );
		StyleConstants.setBold(defaultStyle, true);
*/
		jTP_tagContent.setForeground(Color.BLUE);
		Font font = jTP_tagContent.getFont();
		Font boldFont = font.deriveFont( Font.BOLD );

		jTP_tagContent.setFont( boldFont );
	}

	protected void updateAlwaysOnTopCheckBoxFromConfiguration()
	{
		jCB_alwaysOnTop.setSelected( getAppliConf().getEditTAGwindowAlwaysOnTop() );
		updateAlwaysOnTop();
	}

	public void updateAlwaysOnTop()
	{
		setAlwaysOnTop( jCB_alwaysOnTop.isSelected() );
	}

	protected void initOwnComponents()
	{
//		System.out.println( "EditTAGsJFrame" );
		_acceptPanel = new AcceptCancelRevertPanel( this );
//		_acceptPanel.setEraseButtonEnabled( false );

		jPanel3.add( _acceptPanel );
		_acceptPanel.setBounds( 0, 0, jPanel3.getWidth(), jPanel3.getHeight() );
/*
		Icon icon = _acceptPanel.getCancelButton().getIcon();
		BufferedImage bi = new BufferedImage(
			icon.getIconWidth(),
			icon.getIconHeight(),
			BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		// paint the Icon to the BufferedImage.
		icon.paintIcon(null, g, 0,0);
		g.dispose();
		setOverlappedImage( bi, new Point( 50, 50 ) );
*/		
		_filterOfTAGsPanel = new ShowAndFilterTAGsPanel( this, null, getAppliConf() );

		jPanel1.add( _filterOfTAGsPanel );
		_filterOfTAGsPanel.setBounds( 0, 0, jPanel1.getWidth(), jPanel1.getHeight() );
		
		jTP_tagContent.getActionMap().put(jTP_tagContent.getInputMap().get(KeyStroke.getKeyStroke("ENTER")), new EnterAction() );
	}
/*
	protected String getResourceName( String shortResourceName )
	{
		return( _resourcePath + "/" + shortResourceName );
	}

	protected void initNavigatorButtons()
	{
		Insets insets = new Insets( 2, 2, 2, 2 );
		ViewFunctions.instance().addImageToButton( jB_cancelHeader, getResourceName( "cancel.png" ), insets );
		ViewFunctions.instance().addImageToButton( jB_acceptHeader, getResourceName( "accept.png" ), insets );
		ViewFunctions.instance().addImageToButton( jB_revertHeader, getResourceName( "revert.png" ), insets );
	}
*/
	protected void setWindowConfiguration( )
	{
		Vector<JPopupMenu> vectorJpopupMenus = new Vector<JPopupMenu>();
		vectorJpopupMenus.add( _filterOfTAGsPanel.getPopupMenu() );

		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem( );
		try
		{
			mapRRCI.putResizeRelocateComponentItem(jP_headerOfGame, ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jPanel4, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jCB_alwaysOnTop, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem(jP_editTAG, ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( _filterOfTAGsPanel, ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putAll( _filterOfTAGsPanel.getResizeRelocateInfo() );

			mapRRCI.putResizeRelocateComponentItem( jL_tagToEdit, ResizeRelocateItem.MOVE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jTF_tagToEdit, //ResizeRelocateItem.RESIZE_TO_RIGHT +
																	ResizeRelocateItem.MOVE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jL_tagContent, ResizeRelocateItem.MOVE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jScrollPane2, ResizeRelocateItem.MOVE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jTP_tagContent, ResizeRelocateItem.MOVE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.MOVE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( _acceptPanel, ResizeRelocateItem.RESIZE_TO_RIGHT +
																	ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putAll( _acceptPanel.getResizeRelocateInfo() );

			mapRRCI.putResizeRelocateComponentItem( jB_removeAllTagsFromHeader, ResizeRelocateItem.MOVE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jB_cancelHeader, ResizeRelocateItem.MOVE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jB_acceptHeader, ResizeRelocateItem.MOVE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jB_revertHeader, ResizeRelocateItem.MOVE_TO_BOTTOM );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
/*
		a_intern = new JFrameInternationalization(	ApplicationConfiguration.sa_MAIN_FOLDER,
													ApplicationConfiguration.sa_APPLICATION_NAME,
													ApplicationConfiguration.sa_CONFIGURATION_GROUP,
													ApplicationConfiguration.sa_PATH_PROPERTIES_IN_JAR,
													a_configurationBaseFileName,
													this,
													parent,
													a_vectorJpopupMenus,
													false,
													mapRRCI );
*/
		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									sa_configurationBaseFileName,
									this,
									_parent,
									vectorJpopupMenus,
									true,
									mapRRCI );

		registerInternationalString(CONF_EDIT_TAGS_WINDOW, "Edit TAGs window" );

		_filterOfTAGsPanel.setLanguageConfiguration( this );
		a_intern.setMaxWindowWidthNoLimit(false );

//		setIcon( "com/frojasg1/chesspdfbrowser/resources/icons/App.icon.png" );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jP_headerOfGame = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jCB_alwaysOnTop = new javax.swing.JCheckBox();
        jP_editTAG = new javax.swing.JPanel();
        jL_tagToEdit = new javax.swing.JLabel();
        jTF_tagToEdit = new javax.swing.JTextField();
        jL_tagContent = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTP_tagContent = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        jB_removeAllTagsFromHeader = new javax.swing.JButton();
        jB_acceptHeader = new javax.swing.JButton();
        jB_revertHeader = new javax.swing.JButton();
        jB_cancelHeader = new javax.swing.JButton();
        jB_tagsExtractor = new javax.swing.JButton();
        jL_tagsExtractor = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(655, 2147483647));
        setMinimumSize(new java.awt.Dimension(655, 552));
        setName(""); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        jP_headerOfGame.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Header of game"));
        jP_headerOfGame.setMinimumSize(new java.awt.Dimension(630, 510));
        jP_headerOfGame.setName("jP_headerOfGame"); // NOI18N
        jP_headerOfGame.setLayout(null);

        jPanel4.setLayout(null);

        jCB_alwaysOnTop.setText("Always on top");
        jCB_alwaysOnTop.setName("jCB_alwaysOnTop"); // NOI18N
        jCB_alwaysOnTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_alwaysOnTopActionPerformed(evt);
            }
        });
        jPanel4.add(jCB_alwaysOnTop);
        jCB_alwaysOnTop.setBounds(430, 0, 180, 24);

        jP_headerOfGame.add(jPanel4);
        jPanel4.setBounds(10, 20, 610, 20);

        jP_editTAG.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Edit tag"));
        jP_editTAG.setName("jP_editTAG"); // NOI18N
        jP_editTAG.setLayout(null);

        jL_tagToEdit.setText("Tag Key :");
        jL_tagToEdit.setName("jL_tagToEdit"); // NOI18N
        jP_editTAG.add(jL_tagToEdit);
        jL_tagToEdit.setBounds(10, 300, 110, 16);

        jTF_tagToEdit.setEditable(false);
        jTF_tagToEdit.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTF_tagToEdit.setForeground(new java.awt.Color(102, 102, 102));
        jP_editTAG.add(jTF_tagToEdit);
        jTF_tagToEdit.setBounds(120, 300, 270, 22);

        jL_tagContent.setText("Tag content :");
        jL_tagContent.setName("jL_tagContent"); // NOI18N
        jP_editTAG.add(jL_tagContent);
        jL_tagContent.setBounds(10, 323, 210, 16);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 204)));
        jPanel3.setPreferredSize(new java.awt.Dimension(130, 50));
        jPanel3.setLayout(null);
        jP_editTAG.add(jPanel3);
        jPanel3.setBounds(450, 340, 130, 50);

        jTP_tagContent.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTP_tagContentFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(jTP_tagContent);

        jP_editTAG.add(jScrollPane2);
        jScrollPane2.setBounds(10, 340, 420, 50);

        jPanel1.setLayout(null);
        jP_editTAG.add(jPanel1);
        jPanel1.setBounds(10, 20, 590, 270);

        jP_headerOfGame.add(jP_editTAG);
        jP_editTAG.setBounds(10, 40, 610, 400);

        jB_removeAllTagsFromHeader.setText("Remove all tags");
        jB_removeAllTagsFromHeader.setName("jB_removeAllTagsFromHeader"); // NOI18N
        jB_removeAllTagsFromHeader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_removeAllTagsFromHeaderActionPerformed(evt);
            }
        });
        jP_headerOfGame.add(jB_removeAllTagsFromHeader);
        jB_removeAllTagsFromHeader.setBounds(20, 445, 230, 32);

        jB_acceptHeader.setMaximumSize(new java.awt.Dimension(100, 100));
        jB_acceptHeader.setName("name=jB_acceptHeader,icon=com/frojasg1/generic/resources/acceptcancel/accept.png"); // NOI18N
        jB_acceptHeader.setPreferredSize(new java.awt.Dimension(50, 50));
        jB_acceptHeader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_acceptHeaderActionPerformed(evt);
            }
        });
        jP_headerOfGame.add(jB_acceptHeader);
        jB_acceptHeader.setBounds(460, 450, 50, 50);

        jB_revertHeader.setMaximumSize(new java.awt.Dimension(100, 100));
        jB_revertHeader.setName("name=jB_revertHeader,icon=com/frojasg1/generic/resources/acceptcancel/revert.png"); // NOI18N
        jB_revertHeader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_revertHeaderActionPerformed(evt);
            }
        });
        jP_headerOfGame.add(jB_revertHeader);
        jB_revertHeader.setBounds(530, 450, 50, 50);

        jB_cancelHeader.setMaximumSize(new java.awt.Dimension(100, 100));
        jB_cancelHeader.setName("name=jB_cancelHeader,icon=com/frojasg1/generic/resources/acceptcancel/cancel.png"); // NOI18N
        jB_cancelHeader.setPreferredSize(new java.awt.Dimension(50, 50));
        jB_cancelHeader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_cancelHeaderActionPerformed(evt);
            }
        });
        jP_headerOfGame.add(jB_cancelHeader);
        jB_cancelHeader.setBounds(390, 450, 50, 50);

        jB_tagsExtractor.setMaximumSize(new java.awt.Dimension(20, 20));
        jB_tagsExtractor.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_tagsExtractor.setName("name=jB_tagsExtractor,icon=com/frojasg1/generic/resources/othericons/replace.png"); // NOI18N
        jB_tagsExtractor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_tagsExtractorActionPerformed(evt);
            }
        });
        jP_headerOfGame.add(jB_tagsExtractor);
        jB_tagsExtractor.setBounds(230, 475, 20, 20);

        jL_tagsExtractor.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jL_tagsExtractor.setText("Tags extractor configuration");
        jL_tagsExtractor.setName("jL_tagsExtractor"); // NOI18N
        jP_headerOfGame.add(jL_tagsExtractor);
        jL_tagsExtractor.setBounds(20, 480, 205, 16);

        getContentPane().add(jP_headerOfGame);
        jP_headerOfGame.setBounds(0, 0, 630, 510);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCB_alwaysOnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_alwaysOnTopActionPerformed
        // TODO add your handling code here:

        setAlwaysOnTop( jCB_alwaysOnTop.isSelected() );
    }//GEN-LAST:event_jCB_alwaysOnTopActionPerformed

    private void jB_acceptHeaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_acceptHeaderActionPerformed
        // TODO add your handling code here:

		acceptHeader();

		setVisible( false );
		boolean closeWindow = false;
		formWindowClosing( closeWindow );
//		System.out.println( getSize() );
		
    }//GEN-LAST:event_jB_acceptHeaderActionPerformed

    private void jB_revertHeaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_revertHeaderActionPerformed
        // TODO add your handling code here:

		setChessGame( _chessGame );

    }//GEN-LAST:event_jB_revertHeaderActionPerformed

    private void jB_removeAllTagsFromHeaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_removeAllTagsFromHeaderActionPerformed
        // TODO add your handling code here:

		_headerTemporalCopy.clear();
		_filterOfTAGsPanel.setHeader( _headerTemporalCopy );

    }//GEN-LAST:event_jB_removeAllTagsFromHeaderActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:

		setVisible( false );
		boolean closeWindow = false;
		formWindowClosing( closeWindow );

    }//GEN-LAST:event_formWindowClosing

    private void jTP_tagContentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTP_tagContentFocusLost
        // TODO add your handling code here:

//		System.out.println( "Focus lost" );
		setAlwaysHighlightFocus( false );
		
    }//GEN-LAST:event_jTP_tagContentFocusLost

    private void jB_cancelHeaderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_cancelHeaderActionPerformed
        // TODO add your handling code here:

		setVisible( false );
		boolean closeWindow = false;
		formWindowClosing( closeWindow );

    }//GEN-LAST:event_jB_cancelHeaderActionPerformed

    private void jB_tagsExtractorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_tagsExtractorActionPerformed
        // TODO add your handling code here:

        boolean openTagRegexConfiguration = true;
        _controller.openConfiguration( openTagRegexConfiguration,
										getProfileModel( _headerTemporalCopy ),
										this);
    }//GEN-LAST:event_jB_tagsExtractorActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(EditTAGsJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(EditTAGsJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(EditTAGsJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(EditTAGsJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
        //</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
//				new EditTAGsJFrame().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_acceptHeader;
    private javax.swing.JButton jB_cancelHeader;
    private javax.swing.JButton jB_removeAllTagsFromHeader;
    private javax.swing.JButton jB_revertHeader;
    private javax.swing.JButton jB_tagsExtractor;
    private javax.swing.JCheckBox jCB_alwaysOnTop;
    private javax.swing.JLabel jL_tagContent;
    private javax.swing.JLabel jL_tagToEdit;
    private javax.swing.JLabel jL_tagsExtractor;
    private javax.swing.JPanel jP_editTAG;
    private javax.swing.JPanel jP_headerOfGame;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTF_tagToEdit;
    private javax.swing.JTextPane jTP_tagContent;
    // End of variables declaration//GEN-END:variables

	@Override
	public void accept(InformerInterface panel)
	{
		if( _headerTemporalCopy != null )
		{
			_headerTemporalCopy.put( jTF_tagToEdit.getText(), jTP_tagContent.getText() );
			_filterOfTAGsPanel.updateTable();
		}
	}

	@Override
	public void cancel(InformerInterface panel)
	{
		if( _headerTemporalCopy != null )
		{
			_headerTemporalCopy.remove( jTF_tagToEdit.getText() );
			_filterOfTAGsPanel.updateTable();
		}
	}

	@Override
	public void revert(InformerInterface panel)
	{
		selectTAG( jTF_tagToEdit.getText() );
	}

	@Override
	public void selectTAG(String newTAG)
	{
		String tag = "";
		String content = "";

		if( ( _headerTemporalCopy != null ) && ( newTAG != null ) )
		{
			tag = newTAG;
			content = _headerTemporalCopy.get( newTAG );
			if( content == null )
				content = "";
		}

		if( tag.equals( ChessGameHeaderInfo.SETUP_TAG ) ||
			tag.equals( ChessGameHeaderInfo.FEN_TAG ) )
		{
			enableEditionOfTag( false );
		}
		else
		{
			enableEditionOfTag( true );
		}

		jTF_tagToEdit.setText( tag );
		jTP_tagContent.setText( content );

		if( (tag.length() > 0 ) &&
//			!tag.equals( _lastSelectedTagKey ) &&
			this.isAncestorOf( JFrameInternationalization.getFocusedComponent() ) )
		{
//			System.out.println( "setting focus" );

			highlightComponent( _viewOfTagContent );
/*
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run()
				{
					jTP_tagContent.requestFocus();
					setAlwaysHighlightFocus( true );
				}
			});
*/
			_lastSelectedTagKey = tag;
		}
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

		_filterOfTAGsPanel.internationalizationInitializationEndCallback();
	}

	protected void enableEditionOfTag( boolean value )
	{
		jTP_tagContent.setEditable( value );
		_acceptPanel.setEraseButtonEnabled(value);
		_acceptPanel.setAcceptButtonEnabled(value);
		_acceptPanel.setRevertButtonEnabled(value);
	}

	@Override
	public FilterTAGconfiguration getAppliConf()
	{
		return( (FilterTAGconfiguration) super.getAppliConf() );
	}

	public void updateConfiguration()
	{
		getAppliConf().setEditTagWindowAlwaysOnTop( jCB_alwaysOnTop.isSelected() );
		_filterOfTAGsPanel.updateConfiguration();
	}
	
	@Override
	public void formWindowClosing( boolean closeWindow )
	{
		updateConfiguration();

//		super.formWindowClosing( closeWindow );
		super.formWindowClosing( false );

		if( closeWindow )
			setVisible( false );
	}

	public ChessGame getChessGame()
	{
		return( _chessGame );
	}

	protected ProfileModel getProfileModel( ChessGameHeaderInfo headerInfo )
	{
		ProfileModel result = null;

		if( headerInfo != null )
		{
			result = headerInfo.getProfileModel();
		}

		return( result );
	}

	protected boolean enableTagsExtractorConfiguration( ChessGameHeaderInfo headerInfo )
	{
		boolean result = ( getProfileModel( headerInfo ) != null );

		return( result );
	}

	public void changeView( ChessGameHeaderInfo headerInfo )
	{
		boolean enableTagsExtractorConfiguration = enableTagsExtractorConfiguration( headerInfo );

		jL_tagsExtractor.setEnabled( enableTagsExtractorConfiguration );
		jB_tagsExtractor.setEnabled( enableTagsExtractorConfiguration );
	}

	public void setChessGame( ChessGame cg )
	{
		_chessGame = cg;

		_headerTemporalCopy = null;

		setTitle();
		if( _chessGame != null )
		{
			_headerTemporalCopy = new ChessGameHeaderInfo( _chessGame.getChessGameHeaderInfo() );
		}

		changeView( _headerTemporalCopy );
		_filterOfTAGsPanel.setHeader( _headerTemporalCopy );
	}

	protected void setTitle()
	{
		if( _chessGame != null )
		{
			setTitle( getInternationalString( CONF_EDIT_TAGS_WINDOW ) +
						" - " + _chessGame.getChessGameHeaderInfo().getDescriptionOfGame() );
		}
		else
		{
			setTitle( getInternationalString( CONF_EDIT_TAGS_WINDOW ) );
		}
	}

	protected void acceptHeader()
	{
		if( _chessGame != null )
		{
			ChessGameHeaderInfo header = _chessGame.getChessGameHeaderInfo();
			header.copy( _headerTemporalCopy );
			
			if( _controller != null )
				_controller.newChessGameChosen(_chessGame, true);
		}
	}

	public void releaseResources()
	{
		_filterOfTAGsPanel.releaseResources();
		
		super.releaseResources();
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		jB_acceptHeader = compMapper.mapComponent( jB_acceptHeader );
		jB_cancelHeader = compMapper.mapComponent( jB_cancelHeader );
		jB_removeAllTagsFromHeader = compMapper.mapComponent( jB_removeAllTagsFromHeader );
		jB_revertHeader = compMapper.mapComponent( jB_revertHeader );
		jCB_alwaysOnTop = compMapper.mapComponent( jCB_alwaysOnTop );
		jL_tagContent = compMapper.mapComponent( jL_tagContent );
		jL_tagToEdit = compMapper.mapComponent( jL_tagToEdit );
		jP_editTAG = compMapper.mapComponent( jP_editTAG );
		jP_headerOfGame = compMapper.mapComponent( jP_headerOfGame );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel3 = compMapper.mapComponent( jPanel3 );
		jPanel4 = compMapper.mapComponent( jPanel4 );
		jScrollPane2 = compMapper.mapComponent( jScrollPane2 );
		jTF_tagToEdit = compMapper.mapComponent( jTF_tagToEdit );
		jTP_tagContent = compMapper.mapComponent( jTP_tagContent );

//		_filterOfTAGsPanel.translateMappedComponents(compMapper);
	}

	protected class EnterAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent ae)
		{
			jTP_tagContent.setText( StringFunctions.instance().removeAllCharacters( jTP_tagContent.getText(), "\n\r" ) );
			accept( null );
		}
	}

	@Override
	public void changeLanguage( String language ) throws ConfigurationException, InternException
	{
		super.changeLanguage( language );
		
		setTitle();
	}
}
