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
package com.frojasg1.chesspdfbrowser.view.chess.multiwindowmanager;

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJFrame;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.view.chess.ChessTreeGameTextPane;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfiguration;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.util.Vector;
import java.util.function.Consumer;
import javax.swing.JPopupMenu;

/**
 *
 * @author Usuario
 */
public class DetachedGameWindow extends InternationalizedJFrame
{
	public static final String sa_configurationBaseFileName = "DetachedGameWindow";

	protected MultiwindowGameManager _manager;
	protected ChessTreeGameTextPane jTextPane1;

	protected ChessViewConfiguration _chessViewConfiguration = null;
	
	protected Consumer<DetachedGameWindow> _closeWindowManager = null;

	/**
	 * Creates new form DetachedGameWindow
	 */
	public DetachedGameWindow( MultiwindowGameManager manager,
								BaseApplicationConfiguration applicationConfiguration,
								Consumer<DetachedGameWindow> closeWindowManager )
	{
		super( applicationConfiguration );

		_closeWindowManager = closeWindowManager;

		initComponents();

		_manager = manager;
		
		initPanel( manager  );

		setWindowConfiguration( );

		if( applicationConfiguration instanceof ChessViewConfiguration )
			_chessViewConfiguration = ( ChessViewConfiguration ) applicationConfiguration;

		setAlwaysOnTop( true );
	}

	protected void initPanel( MultiwindowGameManager manager )
	{
		try
		{
			jTextPane1 = new ChessTreeGameTextPane( null, _manager.getChessGameController(), manager, false );
			jScrollPane1.setViewportView(jTextPane1);
			jTextPane1.setBounds( 0, 0, jScrollPane1.getWidth(), jScrollPane1.getHeight() );

			pack();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public void setChessGame( ChessGame cg )
	{
		setTitle( cg.getChessGameHeaderInfo().getDescriptionOfGame() );

		cg.setChessViewConfiguration(_chessViewConfiguration);
		getChessTreeGameTextPane().setChessGame(cg);
		boolean everyThing = true;
		update( everyThing );
	}

	public void update( boolean everyThing )
	{
		if( everyThing )
			getChessTreeGameTextPane().updateEverythingFully();
		else
			getChessTreeGameTextPane().update( everyThing );

		try
		{
			changeLanguage( getAppliConf().getLanguage() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	protected void setWindowConfiguration()
	{
		Vector<JPopupMenu> vectorJpopupMenus = new Vector<JPopupMenu>();
		vectorJpopupMenus.add( jTextPane1.getJPopupMenu() );

		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jScrollPane1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jTextPane1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
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
		boolean adjustSizeOfFrameToContents = true;
		boolean adjustMinSizeOfFrameToContents = true;
		double zoomFactor = getAppliConf().getZoomFactor();
		boolean activateUndoRedoForTextComponents = false;
		boolean enableTextPopupMenu = false;
		boolean switchToZoomComponents = true;
		boolean internationalizeFont = true;

		boolean hasToPutWindowPosition = false;
		Integer delayToInvokeCallback = null;

		createInternationalization( getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									sa_configurationBaseFileName,
									this,
									null,
									vectorJpopupMenus, hasToPutWindowPosition,
									mapRRCI,
									adjustSizeOfFrameToContents,
									adjustMinSizeOfFrameToContents,
									zoomFactor,
									activateUndoRedoForTextComponents,
									enableTextPopupMenu,
									switchToZoomComponents,
									internationalizeFont,
									delayToInvokeCallback);

		jTextPane1.setLanguageConfiguration( this );

///		setIcon( "com/frojasg1/chesspdfbrowser/resources/icons/App.icon.png" );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(416, 339));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(416, 339));
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        getContentPane().setLayout(null);

        jPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(220, 200));
        jPanel1.setLayout(null);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(0, 0));
        jScrollPane1.setName(""); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(220, 200));
        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(0, 0, 400, 300);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 400, 300);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:

		_manager.giveBackDetachedWindow( this );

    }//GEN-LAST:event_formWindowClosing

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:

		_manager.setLastFocusedOrMovedWindow(this);

    }//GEN-LAST:event_formComponentResized

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        // TODO add your handling code here:

		_manager.setLastFocusedOrMovedWindow(this);

    }//GEN-LAST:event_formComponentMoved

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        // TODO add your handling code here:

		_manager.setLastFocusedOrMovedWindow(this);

    }//GEN-LAST:event_formWindowGainedFocus

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
			java.util.logging.Logger.getLogger(DetachedGameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(DetachedGameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(DetachedGameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(DetachedGameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
        //</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
//				new DetachedGameWindow().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables


	public ChessTreeGameTextPane getChessTreeGameTextPane( )
	{
		return( jTextPane1 );
	}

	
	@Override
	public void setVisible( boolean value )
	{
		super.setVisible( value );

		if( !value )
			
			jTextPane1.setChessGame( null );
	}

	@Override
	public void changeLanguage( String language ) throws ConfigurationException, InternException
	{
		super.changeLanguage(language);
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jScrollPane1 = compMapper.mapComponent( jScrollPane1 );
	}

	@Override
	public void formWindowClosing( boolean closeWindow )
	{
		super.formWindowClosing( closeWindow );

		if( closeWindow && (_closeWindowManager != null) )
			_closeWindowManager.accept(this);
	}
}
