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
package com.frojasg1.chesspdfbrowser.view.chess.regex.xmlfilevalidation;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.model.regex.whole.items.ListOfRegexWholeFiles;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertControllerInterface;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertPanel;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.string.StringFunctions;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class XmlFileNameValidationJDialog extends InternationalizedJDialog
	implements AcceptCancelRevertControllerInterface
{
	protected final static String a_configurationBaseFileName = "XmlFileNameValidationJDialog";

	protected static final String CONF_FILE_NAME_ALREADY_EXISTS = "FILE_NAME_ALREADY_EXISTS";
	protected static final String CONF_FILE_NAME_CANNOT_BE_EMPTY = "FILE_NAME_CANNOT_BE_EMPTY";

	protected AcceptCancelRevertPanel _acceptPanel = null;

	protected ListOfRegexWholeFiles _listOfRegexFiles = null;

	protected String _resultFileName = null;

	protected String _initialFileName = null;

	/**
	 * Creates new form XmlFileNameValidationJDialog
	 */
	public XmlFileNameValidationJDialog( JFrame parent, boolean modal )
	{
		super(parent, modal, ApplicationConfiguration.instance() );
	}

	public XmlFileNameValidationJDialog( JDialog parent, boolean modal )
	{
		super(parent, modal, ApplicationConfiguration.instance() );
	}

	public void init( String initialXmlFileName,
						ListOfRegexWholeFiles listOfRegexFiles )
	{
		_listOfRegexFiles = listOfRegexFiles;
		_initialFileName = initialXmlFileName;
		initComponents();

		initOwnComponents();

		initContents();

		setWindowConfiguration();
	}

	protected AcceptCancelRevertPanel createAcceptCancelRevertPanel()
	{
		return( new AcceptCancelRevertPanel( this ) );
	}

	protected void initOwnComponents()
	{
		_acceptPanel = createAcceptCancelRevertPanel();
		jPanel4.add( _acceptPanel );
		ContainerFunctions.instance().addComponentToCompletelyFillParent( jPanel4, _acceptPanel );
	}

	protected void revertContents()
	{
		initContents();
	}

	protected String getInitialFileNameText()
	{
		return( _initialFileName == null ? "" : _initialFileName );
	}

	protected void initContents()
	{
		jTF_fileName.setText( getInitialFileNameText() );
	}

	protected void setWindowConfiguration( )
	{
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putResizeRelocateComponentItem( jTF_fileName, ResizeRelocateItem.RESIZE_TO_RIGHT );

			mapRRCI.putResizeRelocateComponentItem( jPanel4, 0 );
			mapRRCI.putResizeRelocateComponentItem( _acceptPanel, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putAll( _acceptPanel.getResizeRelocateInfo() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		Vector<JPopupMenu> vectorJpopupMenus = null;
		boolean adjustSizeOfFrameToContents = true;
		boolean adjustMinSizeOfFrameToContents = true;
		double zoomFactor = getAppliConf().getZoomFactor();
		boolean activateUndoRedoForTextComponents = true;
		boolean enableTextPopupMenu = true;
		boolean switchToZoomComponents = true;
		boolean internationalizeFont = true;
		Integer delayToInvokeCallback = null;

		createInternationalization( getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									a_configurationBaseFileName,
									this,
									getParent(),
									vectorJpopupMenus,
									true,
									mapRRCI,
									adjustSizeOfFrameToContents,
									adjustMinSizeOfFrameToContents,
									zoomFactor,
									activateUndoRedoForTextComponents,
									enableTextPopupMenu,
									switchToZoomComponents,
									internationalizeFont,
									delayToInvokeCallback);
/*
		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									a_configurationBaseFileName,
									this,
									getParent(),
									vectorJpopupMenus,
									true,
									mapRRCI );
*/

		registerInternationalString( CONF_FILE_NAME_ALREADY_EXISTS, "File name already exists. Try another one." );
		registerInternationalString( CONF_FILE_NAME_CANNOT_BE_EMPTY, "File name cannot be empty." );
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jL_newFileName = new javax.swing.JLabel();
        jTF_fileName = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel3.setLayout(null);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 204)));
        jPanel4.setLayout(null);
        jPanel3.add(jPanel4);
        jPanel4.setBounds(185, 45, 140, 50);

        jL_newFileName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_newFileName.setText("New xml file name :");
        jL_newFileName.setName("jL_newFileName"); // NOI18N
        jPanel3.add(jL_newFileName);
        jL_newFileName.setBounds(15, 15, 170, 14);

        jTF_fileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTF_fileNameActionPerformed(evt);
            }
        });
        jPanel3.add(jTF_fileName);
        jTF_fileName.setBounds(185, 10, 305, 20);

        getContentPane().add(jPanel3);
        jPanel3.setBounds(0, 0, 495, 105);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTF_fileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTF_fileNameActionPerformed
        // TODO add your handling code here:

		accept(null);

    }//GEN-LAST:event_jTF_fileNameActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jL_newFileName;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTextField jTF_fileName;
    // End of variables declaration//GEN-END:variables


	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		_acceptPanel = compMapper.mapComponent( _acceptPanel );
	}

	@Override
	public void revert(InformerInterface panel)
	{
		revertContents();
	}

	protected void validateNewFileName() throws ValidationException
	{
		String newFileName = getFileName();
		if( _listOfRegexFiles.get( newFileName ) != null )
			throw( new ValidationException( getInternationalString( CONF_FILE_NAME_ALREADY_EXISTS ), jTF_fileName ) );

		if( StringFunctions.instance().isEmpty( newFileName ) )
			throw( new ValidationException( getInternationalString( CONF_FILE_NAME_CANNOT_BE_EMPTY ), jTF_fileName ) );
	}

	@Override
	protected void validateFormChild() throws ValidationException
	{
		validateNewFileName();
	}

	protected String getInitialFileName()
	{
		return( _initialFileName );
	}

	public String getFileName()
	{
		String fileName = jTF_fileName.getText();
		String result = FileFunctions.instance().addExtension( fileName, "xml" );

		return( result );
	}

	public String getResultFileName()
	{
		return( _resultFileName );
	}

	protected void applyChanges()
	{
		_resultFileName = getFileName();
	}

	@Override
	public void accept(InformerInterface panel)
	{
		validateForm();
		if( wasSuccessful() )
		{
			applyChanges();

			formWindowClosing(true);
		}
	}

	@Override
	public void cancel(InformerInterface panel)
	{
		revert( null );

		setWasSuccessful( false );

		formWindowClosing(true);
	}

	@Override
	public void releaseResources()
	{
		super.releaseResources();
	}
}
