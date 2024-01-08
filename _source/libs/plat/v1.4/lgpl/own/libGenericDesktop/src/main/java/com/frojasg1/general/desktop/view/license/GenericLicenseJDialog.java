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
package com.frojasg1.general.desktop.view.license;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.generic.GenericFunctions;
import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.general.desktop.DesktopStreamFunctions;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoomfactor.ComboBoxZoomFactorManager;
import com.frojasg1.general.language.file.LanguageFile;
import com.frojasg1.general.view.ViewComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.text.Document;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class GenericLicenseJDialog extends InternationalizedJDialog
{

	protected boolean a_initializing = true;

	protected boolean _acceptedLicensesAndStart = false;

	protected SimpleRtfDocJPanel _rtfDocJPanel = null;

	protected boolean _showToAcceptTheLicense = false;

	protected String[] _arrayOfLanguages = null;

	protected LanguageFile _languageFile = null;

	protected ViewComponent _cbAcceptLicenseVC = null;

	protected String _baseConfigurationFileName = null;

	protected ComboBoxZoomFactorManager _zoomFactorManager = null;

	/**
	 * Creates new form LicenseJFrame
	 */

	public GenericLicenseJDialog( JFrame parent, BaseApplicationConfigurationInterface appConf,
									Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
									String singleRtfFileName,
									boolean showToAcceptTheLicense,
									String baseConfigurationFileName )
	{
		this( parent, appConf, initializationEndCallBack,
			singleRtfFileName, showToAcceptTheLicense, baseConfigurationFileName, false );
	}

	public GenericLicenseJDialog( JFrame parent, BaseApplicationConfigurationInterface appConf,
									Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
									String singleRtfFileName,
									boolean showToAcceptTheLicense,
									String baseConfigurationFileName,
									boolean useAlwaysJar)
	{
		this( parent, appConf, initializationEndCallBack, singleRtfFileName,
				appConf.getInternationalPropertiesPathInJar(),
				appConf.getDefaultLanguageBaseConfigurationFolder(),
				showToAcceptTheLicense, baseConfigurationFileName,
				useAlwaysJar);
	}

	public GenericLicenseJDialog( JFrame parent, BaseApplicationConfigurationInterface appConf,
									Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
									String singleRtfFileName,
									String languageFolderResourceName,
									String languageFolderDiskFileName,
									boolean showToAcceptTheLicense,
									String baseConfigurationFileName,
									boolean useAlwaysJar)
	{
		super( parent, true, appConf, null, initializationEndCallBack, true );

		_baseConfigurationFileName = baseConfigurationFileName;
		_showToAcceptTheLicense = showToAcceptTheLicense;

		_languageFile = createLanguageFile( singleRtfFileName,
											languageFolderResourceName,
											languageFolderDiskFileName,
											useAlwaysJar);

		initComponents();

		initOwnComponents();

		a_initializing = true;
		M_fillLanguageComboBox();
		a_initializing = false;

		initializeLanguage( appConf.getLanguage() );

		setWindowConfiguration();
	}

	public GenericLicenseJDialog( JDialog parent, BaseApplicationConfigurationInterface appConf,
									Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
									String singleRtfFileName,
									boolean showToAcceptTheLicense,
									String baseConfigurationFileName )
	{
		this( parent, appConf, initializationEndCallBack,
			singleRtfFileName, showToAcceptTheLicense, baseConfigurationFileName, false );
	}

	public GenericLicenseJDialog( JDialog parent, BaseApplicationConfigurationInterface appConf,
									Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
									String singleRtfFileName,
									boolean showToAcceptTheLicense,
									String baseConfigurationFileName,
									boolean useAlwaysJar)
	{
		this( parent, appConf, initializationEndCallBack,
				singleRtfFileName,
				appConf.getInternationalPropertiesPathInJar(),
				appConf.getDefaultLanguageBaseConfigurationFolder(),
				showToAcceptTheLicense, baseConfigurationFileName,
				useAlwaysJar);
	}

	public GenericLicenseJDialog( JDialog parent, BaseApplicationConfigurationInterface appConf,
									Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
									String singleRtfFileName,
									String languageFolderResourceName,
									String languageFolderDiskFileName,
									boolean showToAcceptTheLicense,
									String baseConfigurationFileName,
									boolean useAlwaysJar)
	{
		super( parent, true, appConf, null, initializationEndCallBack, true );

		_baseConfigurationFileName = baseConfigurationFileName;
		_showToAcceptTheLicense = showToAcceptTheLicense;

		_languageFile = createLanguageFile( singleRtfFileName,
											languageFolderResourceName,
											languageFolderDiskFileName,
											useAlwaysJar);

		initComponents();

		initOwnComponents();

		a_initializing = true;
		M_fillLanguageComboBox();
		a_initializing = false;

		initializeLanguage( appConf.getLanguage() );

		setWindowConfiguration();
	}

	protected void initializeLanguage( String language )
	{
		System.out.println( "initializeLanguage" );
		try
		{
			changeLanguage( language );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected LanguageFile createLanguageFile( String singleRtfFileName,
									String languageFolderResourceName,
									String languageFolderDiskFileName,
									boolean useAlwaysJar)
	{
		return( new LanguageFile( singleRtfFileName,
									languageFolderResourceName,
									languageFolderDiskFileName,
									useAlwaysJar) );
	}

	@Override
	public void setVisible( boolean value )
	{
		if( value )
			_acceptedLicensesAndStart = false;

		super.setVisible( value );
	}

	protected ComboBoxZoomFactorManager createZoomFactorManager()
	{
		return( new ComboBoxZoomFactorManager( jCB_zoomFactor ) );
	}

	protected void initOwnComponents()
	{
		_zoomFactorManager = createZoomFactorManager();
/*		_zoomFactorManager.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( ! _zoomFactorManager.wasChangedByProgram() )
					getAppliConf().serverChangeZoomFactor( _zoomFactorManager.getSelectedZoomFactor() ) ;
			}
		});
*/
		_zoomFactorManager.init( getAppliConf() );

		if( ! _showToAcceptTheLicense )
		{
			int extraHeight = jPanel2.getHeight();
			jPanel2.setVisible( false );
			jPanel1.setSize( jPanel1.getWidth(), jPanel1.getHeight() + extraHeight );
		}

		_rtfDocJPanel = new SimpleRtfDocJPanel();
		jPanel1.add( _rtfDocJPanel );

		_rtfDocJPanel.setBounds( 0, 0, jPanel1.getWidth(), jPanel1.getHeight() );
	}

	public SimpleRtfDocJPanel getRtfDocPanel()
	{
		return( _rtfDocJPanel );
	}

	public boolean getLicensesHaveBeenAcceptedAndWeHaveToStart()
	{
		return( _acceptedLicensesAndStart );
	}

	protected void setWindowConfiguration( )
	{
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( _rtfDocJPanel, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putAll( _rtfDocJPanel.getMapResizeRelocateComponentItem() );

			mapRRCI.putResizeRelocateComponentItem( jPanel2, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.MOVE_TO_BOTTOM );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		boolean enableUndoRedoForTextComponents = false;
		boolean adjustSizeOfFrameToContents = true;
		boolean adjustMinSizeOfFrameToContents = true;
		boolean switchToZoomComponents = true;
		boolean internationalizeFont = true;
		Integer delayToInvokeCallback = null;
		createInternationalization(getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									getBaseConfFileName(),
									this,
									getParent(),
									null,
									true,
									mapRRCI,
									adjustSizeOfFrameToContents,
									adjustMinSizeOfFrameToContents,
									getAppliConf().getZoomFactor(),
									enableUndoRedoForTextComponents,
									getAppliConf().hasToEnableTextCompPopupMenus(),
									switchToZoomComponents,
									internationalizeFont,
									delayToInvokeCallback);

		a_intern.setMaxWindowWidthNoLimit( false );

//		setIcon( "com/frojasg1/chesspdfbrowser/resources/icons/App.icon.png" );
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();
		
		_cbAcceptLicenseVC = GenericFunctions.instance().getViewFacilities().createViewComponent(jCB_accept);
	}

	protected void M_fillLanguageComboBox()
	{
		_arrayOfLanguages = GenericFunctions.instance().getObtainAvailableLanguages().getTotalArrayOfAvailableLanguages();

		if( (!a_initializing) && ( jCb_language.getItemCount() == _arrayOfLanguages.length ) )
		{}
		else
		{
			int selectedIndex = -1;

			String language = null;
			if( a_initializing )
				language = getAppliConf().getLanguage();
			else
				language = (String) jCb_language.getSelectedItem();

			selectedIndex = ArrayFunctions.instance().getFirstIndexOfEquals(_arrayOfLanguages, language );

			jCb_language.setModel(new javax.swing.DefaultComboBoxModel(_arrayOfLanguages));

			if( selectedIndex >= 0 ) jCb_language.setSelectedIndex(selectedIndex);
		}
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
        jPanel2 = new javax.swing.JPanel();
        jL_language = new javax.swing.JLabel();
        jCb_language = new javax.swing.JComboBox();
        jCB_accept = new javax.swing.JCheckBox();
        jB_startApp = new javax.swing.JButton();
        jL_zoomFactor = new javax.swing.JLabel();
        jCB_zoomFactor = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(700, 5000));
        setMinimumSize(new java.awt.Dimension(460, 511));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(517, 670));
        getContentPane().setLayout(null);

        jPanel3.setLayout(null);

        jPanel2.setLayout(null);

        jL_language.setText("Language :");
        jL_language.setName("jL_language"); // NOI18N
        jPanel2.add(jL_language);
        jL_language.setBounds(10, 16, 80, 16);

        jCb_language.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCb_language.setMinimumSize(new java.awt.Dimension(65, 20));
        jCb_language.setPreferredSize(new java.awt.Dimension(65, 20));
        jCb_language.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCb_languageActionPerformed(evt);
            }
        });
        jPanel2.add(jCb_language);
        jCb_language.setBounds(100, 10, 70, 20);

        jCB_accept.setText("Accept all licenses");
        jCB_accept.setName("jCB_accept"); // NOI18N
        jPanel2.add(jCB_accept);
        jCB_accept.setBounds(210, 10, 200, 24);

        jB_startApp.setText("Accept all licenses and start");
        jB_startApp.setMinimumSize(new java.awt.Dimension(191, 20));
        jB_startApp.setName("jB_startApp"); // NOI18N
        jB_startApp.setPreferredSize(new java.awt.Dimension(191, 20));
        jB_startApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_startAppActionPerformed(evt);
            }
        });
        jPanel2.add(jB_startApp);
        jB_startApp.setBounds(210, 40, 280, 20);

        jL_zoomFactor.setText("Zoom factor :");
        jL_zoomFactor.setName("jL_zoomFactor"); // NOI18N
        jPanel2.add(jL_zoomFactor);
        jL_zoomFactor.setBounds(10, 40, 90, 16);

        jCB_zoomFactor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jCB_zoomFactor.setMinimumSize(new java.awt.Dimension(65, 20));
        jCB_zoomFactor.setPreferredSize(new java.awt.Dimension(65, 20));
        jPanel2.add(jCB_zoomFactor);
        jCB_zoomFactor.setBounds(100, 40, 70, 20);

        jPanel3.add(jPanel2);
        jPanel2.setBounds(0, 550, 500, 80);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(null);
        jPanel3.add(jPanel1);
        jPanel1.setBounds(10, 10, 480, 530);

        getContentPane().add(jPanel3);
        jPanel3.setBounds(0, 0, 500, 630);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCb_languageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCb_languageActionPerformed
        // TODO add your handling code here:

		updateDocument();

    }//GEN-LAST:event_jCb_languageActionPerformed

    private void jB_startAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_startAppActionPerformed
        // TODO add your handling code here:

        if( !jCB_accept.isSelected() )
        {

			jCB_accept.requestFocusInWindow();
			highlightComponent(_cbAcceptLicenseVC);
/*
			SwingUtilities.invokeLater( new Runnable(){ 
					@Override
					public void run() {
						setAlwaysHighlightFocus(true);
				}
			});
*/
		}
        else
        {
            _acceptedLicensesAndStart = true;
            getAppliConf().setLicensesHaveBeenAccepted(true);
			setVisible(false);
        }
    }//GEN-LAST:event_jB_startAppActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_startApp;
    private javax.swing.JCheckBox jCB_accept;
    private javax.swing.JComboBox<String> jCB_zoomFactor;
    private javax.swing.JComboBox jCb_language;
    private javax.swing.JLabel jL_language;
    private javax.swing.JLabel jL_zoomFactor;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables


	protected void updateDocument()
	{
		try
		{
			if( ! a_initializing )
				getAppliConf().changeLanguage( (String) jCb_language.getSelectedItem() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public String getBaseConfFileName( )
	{
		return( _baseConfigurationFileName );
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper) {
		jB_startApp = compMapper.mapComponent(jB_startApp);
		jCB_accept = compMapper.mapComponent(jCB_accept);
		jCb_language = compMapper.mapComponent(jCb_language);
		jL_language = compMapper.mapComponent(jL_language);
		jPanel1 = compMapper.mapComponent(jPanel1);
		jPanel2 = compMapper.mapComponent(jPanel2);
		jPanel3 = compMapper.mapComponent(jPanel3);
		_rtfDocJPanel = compMapper.mapComponent(_rtfDocJPanel);
	}

	public void setRtfDocument()
	{
		String language = (String) jCb_language.getSelectedItem();
		setRtfDocument( language );
	}

	protected InputStream getInputStreamOfRtfFile( String language ) throws FileNotFoundException
	{
		return( _languageFile.getInputStream(language) );
	}

	public boolean documentExists()
	{
		return( getRtfDocument( "EN" ) != null );
	}

	public Document getRtfDocument( String language )
	{
		Document result = null;
		try( InputStream is = getInputStreamOfRtfFile( language ); )
		{
			result = DesktopStreamFunctions.instance().loadAndZoomRtfInputStream(is, getAppliConf().getZoomFactor() );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	public void setRtfDocument( String language )
	{
		try
		{
			Document doc = getRtfDocument( language );
			_rtfDocJPanel.setRtfDocument( doc );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void formWindowClosingEvent( )
	{
		// we do nothing, because we need to check after the closure if the licenses have been accepted and we have to start.
		setVisible(false);
	}

	@Override
	public void changeLanguage( String language ) throws ConfigurationException, InternException
	{
		super.changeLanguage( language );

		setRtfDocument( language );
	}

	public void closeAndReleaseWindow()
	{
		formWindowClosing( true );
	}

	@Override
	public void changeZoomFactor(double zoomFactor)
	{
		if( ( _zoomFactorManager != null ) && ! _zoomFactorManager.wasChangedByProgram() )
			updateDocument();

		super.changeZoomFactor(zoomFactor);
	}
}
