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
package com.frojasg1.chesspdfbrowser.view.chess.editcomment;

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJFrame;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItemComponentResizedListener;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.io.notation.comment.CommentString;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNodeUtils;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.NAG;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertControllerInterface;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertPanel;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.view.chess.variantformat.impl.VariantFormatterForJTextPaneForCommentWindow;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;

/**
 *
 * @author Usuario
 */
public class EditCommentFrame extends InternationalizedJFrame
//								extends JFrame
								implements AcceptCancelRevertControllerInterface,
											ResizeRelocateItemComponentResizedListener
{
	protected static final String CONF_EDIT_COMMENT = "EDIT_COMMENT";



	public static final String sa_configurationBaseFileName = "EditCommentFrame";
	protected JFrame _parent = null;

	protected MoveTreeNode _moveTreeNode = null;
	protected ChessGame _chessGame = null;
	protected ChessGameControllerInterface _controller = null;

	protected List<NAG> _listOfNAGs = new ArrayList<NAG>();

	protected static final String BOLD_BLACK = "BOLD_BLACK";
	protected static final String DEFAULT_STYLE_RESIZED = "DEFAULT_STYLE_RESIZED";
	protected static final String GREEN = "GREEN";

//	protected double _currentZoomFactorForComponentResized = 1.0D;

	protected static EditCommentFrame _instance = null;

	protected AcceptCancelRevertPanel _acceptPanel = null;

	protected ChessViewConfiguration _chessViewConfiguration = null;

	protected Boolean _previousTypeOfCommentWasOfMove = null;
	protected String _comment = null;
	protected String _commentForVariant = null;

	protected Boolean _preferredTypeOfCommentIsOfMove = null;

	protected VariantFormatterForJTextPaneForCommentWindow _formatterForMoves = null;

	protected ResizeRelocateItem _rriTP_listOfMoves = null;

	public static  EditCommentFrame createInstance( JFrame parent,
													ApplicationConfiguration applicationConfiguration,
													ChessGameControllerInterface controller )
	{
		if( _instance == null )
			_instance = new EditCommentFrame( parent, applicationConfiguration, controller );

		return( _instance );
	}

	public static EditCommentFrame instance()
	{
		return( _instance );
	}

	/**
	 * Creates new form EditCommentFrame
	 */
	protected EditCommentFrame( JFrame parent,
								ApplicationConfiguration applicationConfiguration,
								ChessGameControllerInterface controller )
	{
		super( applicationConfiguration );

		_chessViewConfiguration = new ChessViewConfigurationNonCommented( applicationConfiguration );

		_parent = parent;
		_controller = controller;
		initComponents();

		initOwnComponents();

////		addStyles();

//		fillInComboBoxOfNAGs();

		updateAlwaysOnTopCheckBoxFromConfiguration();

		setWindowConfiguration();

//		postInitOwnComponents();
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

		fillInComboBoxOfNAGs();
		postInitOwnComponents();
	}

	protected VariantFormatterForJTextPaneForCommentWindow createFormatterForMoves()
	{
		VariantFormatterForJTextPaneForCommentWindow result = new VariantFormatterForJTextPaneForCommentWindow( jTP_listOfMoves );
		result.init();

		return( result );
	}

	protected void postInitOwnComponents()
	{
		_formatterForMoves = createFormatterForMoves();
		updateChessGame();
//		jTP_listOfMoves.setCaret( new SelectionPreservingCaret() );
//		addStyles();
	}

	protected void initOwnComponents()
	{
//		System.out.println( "EditCommentFrame" );
		_acceptPanel = new AcceptCancelRevertPanel( this );
//		_acceptPanel.setEraseButtonEnabled( false );
		_acceptPanel.setBounds( 0, 0, jPanel2.getWidth(), jPanel2.getHeight() );

		jPanel2.add( _acceptPanel );
	}

	protected void updateAlwaysOnTopCheckBoxFromConfiguration()
	{
		jCB_alwaysOnTop.setSelected( getAppliConf().getEditCommentWindowAlwaysOnTop() );
		updateAlwaysOnTop();
	}

	public void updateAlwaysOnTop()
	{
		setAlwaysOnTop( jCB_alwaysOnTop.isSelected() );
	}
/*
	protected void addStyles()
	{
		StyledDocument sd = jTP_listOfMoves.getStyledDocument();

		int fontSize = jTP_listOfMoves.getFont().getSize();

		Style defaultStyle = sd.getStyle(StyleContext.DEFAULT_STYLE);

		final Style defaultStyleResized = sd.addStyle(DEFAULT_STYLE_RESIZED, defaultStyle);
		StyleConstants.setFontSize(defaultStyle, fontSize );

		final Style bold = sd.addStyle(BOLD_BLACK, defaultStyleResized);
//		StyleConstants.setFontSize(bold, StyleConstants.getFontSize( defaultStyleResized ) );
		StyleConstants.setForeground(bold, Color.BLACK );
		StyleConstants.setBold(bold, true);
	}
*/
	protected void fillInComboBoxOfNAGs()
	{
		Vector<NAG> listOfAvailableNAGs = new Vector<NAG>();

		NAG selectedNAG = new NAG(1);

		for( int ii=0; ii<256; ii++ )
		{
			if( ii != 1 )
				listOfAvailableNAGs.add( new NAG( ii ) );
			else
				listOfAvailableNAGs.add( selectedNAG );
		}

		DefaultComboBoxModel<NAG> dcbm = new DefaultComboBoxModel<NAG>( listOfAvailableNAGs );
		jCB_NAGs.setModel( dcbm );
		jCB_NAGs.setSelectedItem( selectedNAG );
	}

	protected void setWindowConfiguration( )
	{
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem( );
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putResizeRelocateComponentItem( jScrollPane1, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jScrollPane2, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jP_NAG, ResizeRelocateItem.RESIZE_TO_RIGHT );
			_rriTP_listOfMoves = mapRRCI.putResizeRelocateComponentItem( jTP_listOfMoves, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem(_rriTP_listOfMoves);
			_rriTP_listOfMoves.addResizeRelocateItemComponentResizedListener( this );

			mapRRCI.putResizeRelocateComponentItem( jPanel2, 0 );
			mapRRCI.putResizeRelocateComponentItem( _acceptPanel, ResizeRelocateItem.RESIZE_TO_RIGHT +
																	ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putAll( _acceptPanel.getResizeRelocateInfo() );
			mapRRCI.putResizeRelocateComponentItem( jP_novelty, 0 );


//			mapRRCI.putResizeRelocateComponentItem( jLabel1, ResizeRelocateItem.RESIZE_TO_RIGHT );
//			mapRRCI.putResizeRelocateComponentItem( jLabel2, ResizeRelocateItem.RESIZE_TO_RIGHT );

//			HintForComponent hfc = new HintForComponent( jB_hide, "Esconder el formulario ..." );
			
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
									null,
									true,
									mapRRCI );

		registerInternationalString(CONF_EDIT_COMMENT, "Edit comment" );
		a_intern.setMaxWindowHeightNoLimit( false );

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

        bG_novelty = new javax.swing.ButtonGroup();
        bG_typeOfComment = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jL_move = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTP_listOfMoves = new javax.swing.JTextPane();
        jL_comment = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTP_comment = new javax.swing.JTextPane();
        jP_NAG = new javax.swing.JPanel();
        jB_addNAG = new javax.swing.JButton();
        jB_clearNAGs = new javax.swing.JButton();
        jCB_NAGs = new javax.swing.JComboBox();
        jL_pgnNAGs = new javax.swing.JLabel();
        jL_NAGstring = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jCB_alwaysOnTop = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jP_novelty = new javax.swing.JPanel();
        jRB_nothing = new javax.swing.JRadioButton();
        jRB_N = new javax.swing.JRadioButton();
        jRB_TN = new javax.swing.JRadioButton();
        jP_typeOfComment = new javax.swing.JPanel();
        jRB_ofMove = new javax.swing.JRadioButton();
        jRB_atStartOfVariant = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(476, 416));
        setName(""); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        jPanel1.setMinimumSize(new java.awt.Dimension(460, 380));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(460, 381));
        jPanel1.setLayout(null);

        jL_move.setText("Move :");
        jL_move.setName("jL_move"); // NOI18N
        jPanel1.add(jL_move);
        jL_move.setBounds(10, 20, 160, 16);

        jTP_listOfMoves.setEditable(false);
        jScrollPane1.setViewportView(jTP_listOfMoves);

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(0, 40, 460, 60);

        jL_comment.setText("Comment :");
        jL_comment.setName("jL_comment"); // NOI18N
        jPanel1.add(jL_comment);
        jL_comment.setBounds(10, 130, 90, 16);

        jTP_comment.setForeground(new java.awt.Color(0, 178, 0));
        jScrollPane2.setViewportView(jTP_comment);

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(0, 150, 460, 70);

        jP_NAG.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jP_NAG.setName("jP_NAG"); // NOI18N
        jP_NAG.setLayout(null);

        jB_addNAG.setText("Add NAG");
        jB_addNAG.setName("jB_addNAG"); // NOI18N
        jB_addNAG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_addNAGActionPerformed(evt);
            }
        });
        jP_NAG.add(jB_addNAG);
        jB_addNAG.setBounds(10, 47, 150, 32);

        jB_clearNAGs.setText("Clear NAGs");
        jB_clearNAGs.setName("jB_clearNAGs"); // NOI18N
        jB_clearNAGs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_clearNAGsActionPerformed(evt);
            }
        });
        jP_NAG.add(jB_clearNAGs);
        jB_clearNAGs.setBounds(10, 10, 150, 32);
        jP_NAG.add(jCB_NAGs);
        jCB_NAGs.setBounds(180, 50, 160, 26);

        jL_pgnNAGs.setText("PGN NAGs :");
        jL_pgnNAGs.setName("jL_pgnNAGs"); // NOI18N
        jP_NAG.add(jL_pgnNAGs);
        jL_pgnNAGs.setBounds(180, 30, 130, 16);

        jL_NAGstring.setText("NAG string :");
        jL_NAGstring.setName("jL_NAGstring"); // NOI18N
        jP_NAG.add(jL_NAGstring);
        jL_NAGstring.setBounds(180, 10, 130, 16);

        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel1.setMaximumSize(new java.awt.Dimension(2000, 2000));
        jLabel1.setPreferredSize(new java.awt.Dimension(16, 220));
        jP_NAG.add(jLabel1);
        jLabel1.setBounds(310, 10, 120, 16);

        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel2.setMaximumSize(new java.awt.Dimension(2000, 2000));
        jLabel2.setPreferredSize(new java.awt.Dimension(16, 220));
        jP_NAG.add(jLabel2);
        jLabel2.setBounds(310, 30, 120, 16);

        jPanel1.add(jP_NAG);
        jP_NAG.setBounds(0, 230, 460, 80);

        jCB_alwaysOnTop.setText("Always on top");
        jCB_alwaysOnTop.setName("jCB_alwaysOnTop"); // NOI18N
        jCB_alwaysOnTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_alwaysOnTopActionPerformed(evt);
            }
        });
        jPanel1.add(jCB_alwaysOnTop);
        jCB_alwaysOnTop.setBounds(260, 0, 200, 24);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 204)));
        jPanel2.setMinimumSize(new java.awt.Dimension(130, 50));
        jPanel2.setPreferredSize(new java.awt.Dimension(130, 50));
        jPanel2.setLayout(null);
        jPanel1.add(jPanel2);
        jPanel2.setBounds(260, 330, 130, 50);

        jP_novelty.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Novelty"));
        jP_novelty.setName("jP_novelty"); // NOI18N
        jP_novelty.setLayout(null);

        bG_novelty.add(jRB_nothing);
        jRB_nothing.setSelected(true);
        jRB_nothing.setText("nothing");
        jRB_nothing.setName("jRB_nothing"); // NOI18N
        jRB_nothing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_nothingActionPerformed(evt);
            }
        });
        jP_novelty.add(jRB_nothing);
        jRB_nothing.setBounds(20, 13, 150, 28);

        bG_novelty.add(jRB_N);
        jRB_N.setText("N");
        jP_novelty.add(jRB_N);
        jRB_N.setBounds(20, 33, 150, 28);

        bG_novelty.add(jRB_TN);
        jRB_TN.setText("TN");
        jP_novelty.add(jRB_TN);
        jRB_TN.setBounds(20, 53, 150, 28);

        jPanel1.add(jP_novelty);
        jP_novelty.setBounds(10, 310, 180, 85);

        jP_typeOfComment.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Type of comment"));
        jP_typeOfComment.setName("jP_typeOfComment"); // NOI18N
        jP_typeOfComment.setLayout(null);

        bG_typeOfComment.add(jRB_ofMove);
        jRB_ofMove.setSelected(true);
        jRB_ofMove.setText("of move");
        jRB_ofMove.setName("jRB_ofMove"); // NOI18N
        jRB_ofMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_ofMoveActionPerformed(evt);
            }
        });
        jP_typeOfComment.add(jRB_ofMove);
        jRB_ofMove.setBounds(10, 17, 140, 28);

        bG_typeOfComment.add(jRB_atStartOfVariant);
        jRB_atStartOfVariant.setText("at start of variant");
        jRB_atStartOfVariant.setName("jRB_atStartOfVariant"); // NOI18N
        jRB_atStartOfVariant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_atStartOfVariantActionPerformed(evt);
            }
        });
        jP_typeOfComment.add(jRB_atStartOfVariant);
        jRB_atStartOfVariant.setBounds(150, 17, 200, 28);

        jPanel1.add(jP_typeOfComment);
        jP_typeOfComment.setBounds(100, 100, 360, 50);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 460, 395);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jB_clearNAGsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_clearNAGsActionPerformed
        // TODO add your handling code here:

		_listOfNAGs.clear();
		updateNAGStrings();

    }//GEN-LAST:event_jB_clearNAGsActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:

		formWindowClosing();

    }//GEN-LAST:event_formWindowClosing

    private void jB_addNAGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_addNAGActionPerformed
        // TODO add your handling code here:

		NAG selectedNAG = (NAG) jCB_NAGs.getSelectedItem();

		if( selectedNAG != null )
			_listOfNAGs.add(selectedNAG);

		updateNAGStrings();

    }//GEN-LAST:event_jB_addNAGActionPerformed

    private void jCB_alwaysOnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_alwaysOnTopActionPerformed
        // TODO add your handling code here:

		setAlwaysOnTop( jCB_alwaysOnTop.isSelected() );

    }//GEN-LAST:event_jCB_alwaysOnTopActionPerformed

    private void jRB_nothingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_nothingActionPerformed
        // TODO add your handling code here:

		

    }//GEN-LAST:event_jRB_nothingActionPerformed

    private void jRB_ofMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_ofMoveActionPerformed
        // TODO add your handling code here:

		setTypeOfCommentIsOfMove( true );

    }//GEN-LAST:event_jRB_ofMoveActionPerformed

    private void jRB_atStartOfVariantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_atStartOfVariantActionPerformed
        // TODO add your handling code here:

		setTypeOfCommentIsOfMove( false );

    }//GEN-LAST:event_jRB_atStartOfVariantActionPerformed

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
			java.util.logging.Logger.getLogger(EditCommentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(EditCommentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(EditCommentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(EditCommentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
        //</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
//				new EditCommentFrame().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bG_novelty;
    private javax.swing.ButtonGroup bG_typeOfComment;
    private javax.swing.JButton jB_addNAG;
    private javax.swing.JButton jB_clearNAGs;
    private javax.swing.JComboBox jCB_NAGs;
    private javax.swing.JCheckBox jCB_alwaysOnTop;
    private javax.swing.JLabel jL_NAGstring;
    private javax.swing.JLabel jL_comment;
    private javax.swing.JLabel jL_move;
    private javax.swing.JLabel jL_pgnNAGs;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jP_NAG;
    private javax.swing.JPanel jP_novelty;
    private javax.swing.JPanel jP_typeOfComment;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRB_N;
    private javax.swing.JRadioButton jRB_TN;
    private javax.swing.JRadioButton jRB_atStartOfVariant;
    private javax.swing.JRadioButton jRB_nothing;
    private javax.swing.JRadioButton jRB_ofMove;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTP_comment;
    private javax.swing.JTextPane jTP_listOfMoves;
    // End of variables declaration//GEN-END:variables

	protected void updateChessGame()
	{
		setChessGame( getChessGame() );
	}

	protected ChessGame getChessGame()
	{
		return( _chessGame );
	}

	protected void setChessGame( ChessGame chessGame )
	{
		_chessGame = chessGame;
		if( ( jTP_listOfMoves != null ) && ( _formatterForMoves != null ) )
			_formatterForMoves.setChessGame( _chessGame );
	}

	public void setMove( ChessGame cg, MoveTreeNode mtn, Boolean isTypeOfCommentOfMove )
	{
		setChessGame( cg );
		setTitle();

		_moveTreeNode = mtn;
		if( isTypeOfCommentOfMove != null )
			_preferredTypeOfCommentIsOfMove = isTypeOfCommentOfMove;

		rollback();
	}

	protected void setTitle()
	{
		if( _chessGame != null )
			setTitle( getInternationalString( CONF_EDIT_COMMENT ) +
						" - " + _chessGame.getChessGameHeaderInfo().getDescriptionOfGame() );
		else
			setTitle( getInternationalString( CONF_EDIT_COMMENT ) );
	}

	protected void rollback()
	{
		resetListOfNAGs();

		updateWindow();
	}

	protected void commit()
	{
		if( _moveTreeNode.getMove() != null )
		{
			updateMoveTreeNode();
			_controller.newChessGameChosen( _chessGame, true );
		}
	}

	protected void resetListOfNAGs()
	{
		_listOfNAGs.clear();

		if( ( _moveTreeNode != null ) && ( _moveTreeNode.getMove() != null ) )
		{
			Iterator<NAG> it = _moveTreeNode.getMove().getNagIterator();
			if( _moveTreeNode.getMove().getMoveToken() != null )
				it = _moveTreeNode.getMove().getMoveToken().nagIterator();

			while( it.hasNext() )
				_listOfNAGs.add( it.next() );
		}
	}

	protected String getNAGStringsToShow( List<NAG> list )
	{
		StringBuilder sb = new StringBuilder();

		String separator = "";
		Iterator<NAG> it = list.iterator();
		while( it.hasNext() )
		{
			sb.append(separator);
			sb.append( it.next().getStringToShow() );
			separator = " ";
		}

		return( sb.toString() );
	}

	protected String getPgnNAGs( List<NAG> list )
	{
		StringBuilder sb = new StringBuilder();

		String separator = "";
		Iterator<NAG> it = list.iterator();
		while( it.hasNext() )
		{
			sb.append(separator);
			sb.append( it.next().getStringForPGNfile() );
			separator = " ";
		}

		return( sb.toString() );
	}

	public void updateWindow()
	{
		String listOfMovesString = "";
		String comment = "";
		if( ( _chessGame != null ) && ( _moveTreeNode != null ) )
		{
			try
			{
				listOfMovesString = _chessGame.getMoveListString( _moveTreeNode, _chessViewConfiguration );
			}
			catch( Throwable th )
			{}

			comment = setCommentInWindow( _moveTreeNode );
		}

		if( _formatterForMoves != null )
			_formatterForMoves.setListOfMoves( listOfMovesString );

		setComposedComment( comment );

		updateNAGStrings();
	}

	protected String setCommentInWindow( MoveTreeNode node )
	{
		String commentToShow = null;

		if( node != null )
		{
			jRB_ofMove.setEnabled(true);
			jRB_atStartOfVariant.setEnabled(true);

			jRB_atStartOfVariant.setEnabled(canHaveCommentsForVariant( _moveTreeNode ) );

			_previousTypeOfCommentWasOfMove = null;

			_comment = _moveTreeNode.getComment();
			_commentForVariant = _moveTreeNode.getCommentForVariant();
			commentToShow = setTypeOfCommentIsOfMove( getTypeOfCommentIsOfMove( _moveTreeNode ) );
		}

		return( commentToShow );
	}

	protected boolean getTypeOfCommentIsOfMove( MoveTreeNode node )
	{
		Boolean result = null;
		if( node != null )
		{
			if( _preferredTypeOfCommentIsOfMove != null )
			{
				result = _preferredTypeOfCommentIsOfMove;
				if( !_preferredTypeOfCommentIsOfMove && !this.canHaveCommentsForVariant(node) )
					result = null;
			}

			if( ( result == null ) || _preferredTypeOfCommentIsOfMove == null )
			{
				if( ( node.getComment() == null ) &&
							( node.getCommentForVariant() != null ) )
				{
					result = false;
				}
				else
				{
					result = true;
				}
			}
		}

		_preferredTypeOfCommentIsOfMove = null;

		return( result );
	}

	protected void updateInternalCommentsIfModified( Boolean previousTypeOfCommentWasOfMove )
	{
		String previousComment = jTP_comment.getText();
		if( previousTypeOfCommentWasOfMove == null )
		{
			
		}
		else if( previousTypeOfCommentWasOfMove )
		{
			_comment = getComposedComment( previousComment );
		}
		else
		{
			_commentForVariant = previousComment;
		}
	}

	protected String setTypeOfCommentIsOfMove( boolean newTypeOfCommentIsOfMove )
	{
		updateInternalCommentsIfModified( _previousTypeOfCommentWasOfMove );

		String comment = null;
		if( newTypeOfCommentIsOfMove )
		{
			jRB_ofMove.setSelected(true);
			comment = _comment;
		}
		else
		{
			jRB_atStartOfVariant.setSelected(true);
			comment = _commentForVariant;
		}

		setComposedComment( comment );

		_previousTypeOfCommentWasOfMove = newTypeOfCommentIsOfMove;

		return( comment );
	}

	protected boolean canHaveCommentsForVariant( MoveTreeNode node )
	{
		return( MoveTreeNodeUtils.instance().canHaveCommentsForVariant( node ) );
	}

	protected void updateNAGStrings( )
	{
		jLabel1.setText(getNAGStringsToShow( _listOfNAGs ) );
		jLabel2.setText( getPgnNAGs( _listOfNAGs ) );
	}
/*
	protected Range getLastMoveRange( String listOfMoves )
	{
		Range result = new Range(0,0);

		if( ( listOfMoves != null ) &&
			( listOfMoves.length() > 2 ) )
		{
			int end = listOfMoves.length() - 1;
			boolean found = false;

			while( ( end > -1 ) && !found )
			{
				int start = StringFunctions.instance().lastIndexOfAnyChar(listOfMoves, " .", end ) + 1;
				if( start < 0 )
					start = 0;

				if( start <= end )
				{
					found = isAMove( listOfMoves.substring( start, end + 1 ) );

					if( found )
						result = new Range( start, end );
				}

				end = start - 2;
			}
		}

		return( result );
	}

	protected void updateListOfMoves()
	{
		setListOfMoves( jTP_listOfMoves.getText() );
	}

	protected void setListOfMoves( String listOfMoves )
	{
		Range rangeOfLastMove = getLastMoveRange( listOfMoves );

		Style style = jTP_listOfMoves.getStyle( BOLD_BLACK );
		Style defaultStyleResized = jTP_listOfMoves.getStyle( DEFAULT_STYLE_RESIZED );

		jTP_listOfMoves.setText( listOfMoves );
		jTP_listOfMoves.getStyledDocument().setCharacterAttributes(0, listOfMoves.length(),
													defaultStyleResized, true);
		jTP_listOfMoves.getStyledDocument().setCharacterAttributes( rangeOfLastMove.getInitial(),
																	rangeOfLastMove.getFinal() - rangeOfLastMove.getInitial() + 1,
																	style, true);

		Component focusOwner = JFrameInternationalization.getFocusedComponent();
		jTP_listOfMoves.requestFocus();

		jTP_listOfMoves.setSelectionStart( rangeOfLastMove.getInitial() );
		jTP_listOfMoves.setSelectionEnd( rangeOfLastMove.getFinal() + 1 );
		jTP_listOfMoves.repaint();

		if( focusOwner != null )
			focusOwner.requestFocus();
	}
*/
	protected void setComposedComment( String comment )
	{
		if( comment == null )
			comment = "";

//		Style style = jTP_comment.getStyle( GREEN );

		CommentString cs = new CommentString( comment );

		jTP_comment.setText( cs.getSimpleComment() );

		if( cs.isNoveltyN() )
			jRB_N.setSelected( true );
		else if( cs.isNoveltyTN() )
			jRB_TN.setSelected( true );
		else
			jRB_nothing.setSelected( true );

//		jTP_comment.getStyledDocument().setCharacterAttributes( 0, comment.length(), style, true);
	}

	protected String getComposedComment( String simpleComment )
	{
		CommentString cs = new CommentString( simpleComment );

		if( jRB_N.isSelected() )
			cs.setIsNoveltyN();
		else if( jRB_TN.isSelected() )
			cs.setIsNoveltyTN();

		return( cs.getComposedComment() );
	}

	protected void updateMoveTreeNodeComments()
	{
		updateInternalCommentsIfModified( _previousTypeOfCommentWasOfMove );

		_preferredTypeOfCommentIsOfMove = jRB_ofMove.isSelected();

		_moveTreeNode.setComment( getComposedComment( _comment ) );
		_moveTreeNode.setCommentForVariant( _commentForVariant );
	}

	protected void updateMoveTreeNode()
	{
		if( _moveTreeNode != null )
		{
			updateMoveTreeNodeComments();

			_moveTreeNode.getMove().getMoveToken().clearNAGlist();
			_moveTreeNode.getMove().getMoveToken().addAllNAGs( _listOfNAGs.iterator() );
		}
	}

	@Override
	public EditCommentConfiguration getAppliConf()
	{
		return( (EditCommentConfiguration) super.getAppliConf() );
	}

	@Override
	public void formWindowClosing( boolean closeWindow )
	{
		getAppliConf().setEditCommentWindowAlwaysOnTop( jCB_alwaysOnTop.isSelected() );

//		super.formWindowClosing( closeWindow );
		saveInternalConfiguration();
		super.formWindowClosing( false );
		if( closeWindow )
			setVisible( false );
	}
/*
	protected boolean isAMove( String text )
	{
		boolean result = false;

		if( _chessGame != null )
		{
			String translatedText = null;
			
			translatedText = _chessGame.getChessViewConfiguration().getChessLanguageConfigurationToShow().translateMoveStringToEnglish(text, null);
			result = ChessMoveAlgebraicNotation.getInstance().isItAChessMoveString(translatedText);
		}

		return( result );
	}
*/
	protected void formWindowClosing()
	{
		setVisible( false );
		boolean closeWindow = false;

		formWindowClosing( closeWindow );
	}

	@Override
	public void accept(InformerInterface panel)
	{
		commit();
	}

	@Override
	public void cancel(InformerInterface panel)
	{
		formWindowClosing();
	}

	@Override
	public void revert(InformerInterface panel )
	{
		rollback();
	}

	@Override
	public void resizeRelocateItemComponentResized(Component comp, double newZoomFactor)
	{
		if( _formatterForMoves != null )
			_formatterForMoves.changeZoomFactor(newZoomFactor);
/*
		if( _currentZoomFactorForComponentResized != newZoomFactor )
		{
			addStyles();
			updateListOfMoves();
			_currentZoomFactorForComponentResized = newZoomFactor;
		}
*/
	}

	@Override
	public void changeLanguage( String language ) throws ConfigurationException, InternException
	{
		super.changeLanguage( language );

		setTitle();
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		bG_novelty = compMapper.mapComponent(bG_novelty);
		jB_addNAG = compMapper.mapComponent(jB_addNAG);
		jB_clearNAGs = compMapper.mapComponent(jB_clearNAGs);
		jCB_NAGs = compMapper.mapComponent(jCB_NAGs);
		jCB_alwaysOnTop = compMapper.mapComponent(jCB_alwaysOnTop);
		jL_NAGstring = compMapper.mapComponent(jL_NAGstring);
		jL_comment = compMapper.mapComponent(jL_comment);
		jL_move = compMapper.mapComponent(jL_move);
		jL_pgnNAGs = compMapper.mapComponent(jL_pgnNAGs);
		jL_pgnNAGs = compMapper.mapComponent(jL_pgnNAGs);
		jLabel1 = compMapper.mapComponent(jLabel1);
		jLabel2 = compMapper.mapComponent(jLabel2);
		jP_NAG = compMapper.mapComponent(jP_NAG);
		jP_novelty = compMapper.mapComponent(jP_novelty);
		jPanel1 = compMapper.mapComponent(jPanel1);
		jPanel2 = compMapper.mapComponent(jPanel2);
		jRB_N = compMapper.mapComponent(jRB_N);
		jRB_TN = compMapper.mapComponent(jRB_TN);
		jRB_nothing = compMapper.mapComponent(jRB_nothing);
		jScrollPane1 = compMapper.mapComponent(jScrollPane1);
		jScrollPane2 = compMapper.mapComponent(jScrollPane2);
		jTP_comment = compMapper.mapComponent(jTP_comment);
		jTP_listOfMoves = compMapper.mapComponent(jTP_listOfMoves);
	}

	@Override
	public void releaseResources()
	{
		_rriTP_listOfMoves.removeComponentResizedListener( this );

		super.releaseResources();
	}

	@Override
	public void changeZoomFactor(double zoomFactor)
	{
		super.changeZoomFactor( zoomFactor );
	}

	protected static class ChessViewConfigurationNonCommented implements ChessViewConfiguration
	{
		protected ChessViewConfiguration _cvc = null;
		
		public ChessViewConfigurationNonCommented( ChessViewConfiguration cvc )
		{
			_cvc = cvc;
		}
		
		@Override
		public boolean getHasToShowComments()
		{
			return( false );
		}

		@Override
		public void setHasToShowComments(boolean value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public boolean getHasToShowNAGs() {
			return( _cvc.getHasToShowNAGs() );
		}

		@Override
		public void setHasToShowNAGs(boolean value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public ChessLanguageConfiguration getChessLanguageConfigurationToShow() {
			return( _cvc.getChessLanguageConfigurationToShow() );
		}

		@Override
		public ChessLanguageConfiguration getChessLanguageConfigurationToParseTextFrom() {
			return( _cvc.getChessLanguageConfigurationToParseTextFrom() );
		}

		@Override
		public String getConfigurationOfChessLanguageToShow() {
			return( _cvc.getConfigurationOfChessLanguageToShow() );
		}

		@Override
		public String getConfigurationOfChessLanguageToParseTextFrom() {
			return( _cvc.getConfigurationOfChessLanguageToParseTextFrom() );
		}

		@Override
		public void setConfigurationOfChessLanguageToShow(String value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setConfigurationOfChessLanguageToParseTextFrom(String value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public boolean getDetachedGameWindowsAlwaysOnTop() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setDetachedGameWindowsAlwaysOnTop(boolean value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public boolean getHasToShowSegments() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public boolean isAutocompletionForRegexActivated() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setIsAutocompletionForRegexActivated(boolean value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public boolean getShowPdfGameWhenNewGameSelected() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public void setShowPdfGameWhenNewGameSelected(boolean value) {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	};
}
