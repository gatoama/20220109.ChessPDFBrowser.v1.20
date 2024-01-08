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
package com.frojasg1.general.desktop.view.newversion;

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.name.ComponentNameComponents;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.application.version.DesktopApplicationVersion;
import com.frojasg1.general.desktop.queries.InetQueryException;
import com.frojasg1.general.desktop.queries.newversion.NewVersionQueryResult;
import com.frojasg1.general.desktop.view.labels.UrlJLabel;
import com.frojasg1.general.desktop.view.labels.UrlLabelListener;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Component;
import java.util.Objects;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

/**
 *
 * @author fjavier.rojas
 */
public class NewVersionFoundJDialog extends InternationalizedJDialog {

	public static final String sa_configurationBaseFileName = "newVersionFoundJDialog";
	protected static final String sa_languagePropertiesInJarLocation = "com/frojasg1/generic/prop/intern";

	protected static final String CONF_STABLE_TEXT = "STABLE_TEXT";
	protected static final String CONF_EXPERIMENTAL_TEXT = "EXPERIMENTAL_TEXT";
	protected static final String CONF_DOWNLOAD = "DOWNLOAD";
	protected static final String CONF_WITH_ERROR = "WITH_ERROR";

	protected NewVersionQueryResult _newVersionQueryResult;

	protected UrlJLabel _downloadLabel = null;

	protected boolean _urlClicked = false;

	protected DesktopApplicationVersion _applicationVersion = null;

	/**
	 * Creates new form NewVersionFoundJDialog
	 */
	public NewVersionFoundJDialog(java.awt.Frame parent, boolean modal,
									BaseApplicationConfigurationInterface applicationConfiguration ) {
		super(parent, modal, applicationConfiguration);
	}

	public void init( NewVersionQueryResult newVersionQueryResult )
	{
		if( _applicationVersion == null )
			_applicationVersion = getDefaultApplicationVersion();

		_newVersionQueryResult = newVersionQueryResult;

		initComponents();

		setNewVersionQueryResult( _newVersionQueryResult );

		setWindowConfiguration();
	}

	protected DesktopApplicationVersion getDefaultApplicationVersion()
	{
		return( DesktopApplicationVersion.instance() );
	}

	public void setApplicationVersion( DesktopApplicationVersion applicationVersion )
	{
		_applicationVersion = applicationVersion;
	}

	protected void addComponentToMap( MapResizeRelocateComponentItem mapRRCI, Component comp ) throws InternException
	{
		if( ( comp != null ) && ( comp.getParent() != null ) )
			mapRRCI.putResizeRelocateComponentItem(comp, ResizeRelocateItem.FILL_WHOLE_PARENT );
	}

	protected void setWindowConfiguration( )
	{
		Vector<JPopupMenu> vectorJpopupMenus = null;

		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem( );
		try
		{
//			boolean postpone_initialization = true;
//			mapRRCI.putResizeRelocateComponentItem(jP_newVersionAvailable, ResizeRelocateItem.RESIZE_TO_RIGHT +
//															ResizeRelocateItem.RESIZE_TO_BOTTOM, postpone_initialization );
			addComponentToMap( mapRRCI, jP_newVersionAvailable );
			addComponentToMap( mapRRCI, jP_noNewVersionAvailable );
			addComponentToMap( mapRRCI, jP_serverReturnedError );
			addComponentToMap( mapRRCI, jP_errorInQuery );
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
									sa_languagePropertiesInJarLocation,
									sa_configurationBaseFileName,
									this,
									null,
									vectorJpopupMenus,
									true,
									mapRRCI );

		registerInternationalString(CONF_STABLE_TEXT, "It is a stable version." );
		registerInternationalString(CONF_EXPERIMENTAL_TEXT, "It is an experimental version." );
		registerInternationalString(CONF_DOWNLOAD, "DOWNLOAD [ $1 ]" );
		registerInternationalString(CONF_WITH_ERROR, "With error $1" );
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

		if( _newVersionQueryResult.getException() != null )
		{
			setException( _newVersionQueryResult.getException() );
		}
		else if( _newVersionQueryResult.isSuccessful() && _newVersionQueryResult.thereIsANewVersion() )
		{
			setStable(jL_stable, _newVersionQueryResult.isAFinalVersion() );

			if( _downloadLabel != null )
			{
				_downloadLabel.setText( createCustomInternationalString(CONF_DOWNLOAD,
											_newVersionQueryResult.getNewDownloadResource() ) );

				_downloadLabel.setUnderlineFont(true);
				_downloadLabel.addListenerGen( createUrlLabelListener() );
			}
		}
	}

	protected void setUrlClicked( boolean value )
	{
		_urlClicked = value;
	}

	public boolean wasUrlClicked()
	{
		return( _urlClicked );
	}

	protected UrlLabelListener createUrlLabelListener()
	{
		return( (obs) -> urlLabelClicked() );
	}

	protected void urlLabelClicked()
	{
		setUrlClicked(true);

		boolean closeWindow = true;
		formWindowClosing(closeWindow);
	}

	protected void setStable( JLabel label, Boolean isStable )
	{
		String text = "";
		if( isStable != null )
		{
			if( isStable )
				text = this.getInternationalString( CONF_STABLE_TEXT );
			else
				text = this.getInternationalString( CONF_EXPERIMENTAL_TEXT );
		}

		label.setText(text);
	}

	protected void setNewVersionAvailable( NewVersionQueryResult newVersionQueryResult )
	{
		setDownloadLabel(jL_download, newVersionQueryResult );

		setHint( jTextPane1, newVersionQueryResult.getHintForDownload() );

		setFormattedNumber(jL_amountOfDownloadsOfNewVersion, newVersionQueryResult.getNumberOfDownloadsOfLatestVersion() );
		setFormattedNumber( jL_totalAmountOfDownloadsOfApplication, newVersionQueryResult.getTotalNumberOfDownloadsOfApplication() );

		jCB_ignoreThisVersion.setSelected( isIgnored( newVersionQueryResult ) );
	}

	protected void setNoNewVersionAvailable( NewVersionQueryResult newVersionQueryResult )
	{
	}

	protected void setServerReturnedError( NewVersionQueryResult newVersionQueryResult )
	{
		setText( jTextPane3, newVersionQueryResult.getErrorString() );
	}

	protected void onlyOne( JPanel[] panels, JPanel visiblePanel )
	{
		for( JPanel panel: panels )
		{
			if( panel != visiblePanel )
				getContentPane().remove( panel );
			else
				panel.setLocation( 0, 0 );
		}
	}

	protected void setException( Exception exception )
	{
		if( exception instanceof InetQueryException )
		{
			setInetQueryException( ( InetQueryException ) exception );
		}
		else
		{
			setException( -1, exception.getMessage() );
		}
	}

	protected void setException( Integer resultCode, String message )
	{
		jL_particularError.setText( this.createCustomInternationalString(CONF_WITH_ERROR, resultCode ) );

		setText( jTextPane4, message );
	}

	protected void setInetQueryException( InetQueryException exception )
	{
		setException( exception.getResultCode(), exception.getMessage() );
	}

	protected void setNewVersionQueryResult( NewVersionQueryResult newVersionQueryResult )
	{
		try
		{
			JPanel[] panels = new JPanel[]{jP_newVersionAvailable, jP_noNewVersionAvailable, jP_serverReturnedError, jP_errorInQuery};

			if( newVersionQueryResult.getException() != null )
			{
				onlyOne( panels, jP_errorInQuery );
//				setInetQueryException( newVersionQueryResult.getException() );
			}
			else if( newVersionQueryResult.isSuccessful() )
			{
				if( newVersionQueryResult.thereIsANewVersion() )
				{
					onlyOne( panels, jP_newVersionAvailable );
					setNewVersionAvailable( newVersionQueryResult );
				}
				else
				{
					onlyOne( panels, jP_noNewVersionAvailable );
					setNoNewVersionAvailable( newVersionQueryResult );
				}
			}
			else
			{
				onlyOne( panels, jP_serverReturnedError );
				setServerReturnedError( newVersionQueryResult );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected String formatNumber( long number )
	{
		return( IntegerFunctions.formatNumber( number, getOutputLocale() ) );
	}

	protected void setFormattedNumber( JLabel label, int number )
	{
		label.setText( formatNumber( number ) );
	}

	protected void setDownloadLabel( JLabel label, NewVersionQueryResult result )
	{
		setUrl( label, result.getLink() );
	}

	protected void setUrl( JLabel label, String url )
	{
		ComponentNameComponents cnc = new ComponentNameComponents( label.getName() );
		cnc.setComponent( ComponentNameComponents.URL_COMPONENT, url);

		label.setName( cnc.getCompoundNameForComponentName() );
	}

	protected void setText( JTextComponent jtc, String text )
	{
		if( text != null )
		{
			jtc.setText( text );
		}
	}

	protected void setHint( JTextComponent jtc, String hint )
	{
		setText( jtc, hint );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jP_newVersionAvailable = new javax.swing.JPanel();
        jL_download = new javax.swing.JLabel();
        jL_totalNumberOfDownloadsOfApplication = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jL_stable = new javax.swing.JLabel();
        jL_newVersionAvailable = new javax.swing.JLabel();
        jL_notes = new javax.swing.JLabel();
        jL_numberOfDownloadsOfNewVersion = new javax.swing.JLabel();
        jL_totalAmountOfDownloadsOfApplication = new javax.swing.JLabel();
        jL_amountOfDownloadsOfNewVersion = new javax.swing.JLabel();
        jCB_ignoreThisVersion = new javax.swing.JCheckBox();
        jP_noNewVersionAvailable = new javax.swing.JPanel();
        jL_noNewVersionAvailable = new javax.swing.JLabel();
        jP_errorInQuery = new javax.swing.JPanel();
        jL_errorInQuery = new javax.swing.JLabel();
        jL_particularError = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextPane4 = new javax.swing.JTextPane();
        jL_error1 = new javax.swing.JLabel();
        jP_serverReturnedError = new javax.swing.JPanel();
        jL_errorAtServer = new javax.swing.JLabel();
        jL_error = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane3 = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);

        jP_newVersionAvailable.setLayout(null);

        jL_download.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        jL_download.setForeground(new java.awt.Color(51, 102, 255));
        jL_download.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jL_download.setText("DOWNLOAD");
        jL_download.setName(""); // NOI18N
        jP_newVersionAvailable.add(jL_download);
        jL_download.setBounds(0, 250, 700, 23);

        jL_totalNumberOfDownloadsOfApplication.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jL_totalNumberOfDownloadsOfApplication.setText("Total number of downloads of the application:");
        jL_totalNumberOfDownloadsOfApplication.setName("jL_totalNumberOfDownloadsOfApplication"); // NOI18N
        jP_newVersionAvailable.add(jL_totalNumberOfDownloadsOfApplication);
        jL_totalNumberOfDownloadsOfApplication.setBounds(50, 220, 360, 20);

        jTextPane1.setEditable(false);
        jScrollPane1.setViewportView(jTextPane1);

        jP_newVersionAvailable.add(jScrollPane1);
        jScrollPane1.setBounds(50, 100, 580, 90);

        jL_stable.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jL_stable.setText("It is an experimental version");
        jP_newVersionAvailable.add(jL_stable);
        jL_stable.setBounds(50, 50, 410, 20);

        jL_newVersionAvailable.setFont(new java.awt.Font("Tahoma", 1, 26)); // NOI18N
        jL_newVersionAvailable.setForeground(new java.awt.Color(0, 204, 102));
        jL_newVersionAvailable.setText("There is a new version of the application available");
        jL_newVersionAvailable.setName("jL_newVersionAvailable"); // NOI18N
        jP_newVersionAvailable.add(jL_newVersionAvailable);
        jL_newVersionAvailable.setBounds(20, 10, 670, 30);

        jL_notes.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jL_notes.setText("Notes:");
        jL_notes.setName("jL_notes"); // NOI18N
        jP_newVersionAvailable.add(jL_notes);
        jL_notes.setBounds(50, 80, 410, 20);

        jL_numberOfDownloadsOfNewVersion.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jL_numberOfDownloadsOfNewVersion.setText("Number of downloads of the new version:");
        jL_numberOfDownloadsOfNewVersion.setName("jL_numberOfDownloadsOfNewVersion"); // NOI18N
        jP_newVersionAvailable.add(jL_numberOfDownloadsOfNewVersion);
        jL_numberOfDownloadsOfNewVersion.setBounds(50, 200, 360, 20);

        jL_totalAmountOfDownloadsOfApplication.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jL_totalAmountOfDownloadsOfApplication.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jL_totalAmountOfDownloadsOfApplication.setText("0000");
        jP_newVersionAvailable.add(jL_totalAmountOfDownloadsOfApplication);
        jL_totalAmountOfDownloadsOfApplication.setBounds(420, 220, 70, 20);

        jL_amountOfDownloadsOfNewVersion.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jL_amountOfDownloadsOfNewVersion.setForeground(new java.awt.Color(0, 204, 51));
        jL_amountOfDownloadsOfNewVersion.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jL_amountOfDownloadsOfNewVersion.setText("0000");
        jL_amountOfDownloadsOfNewVersion.setToolTipText("");
        jP_newVersionAvailable.add(jL_amountOfDownloadsOfNewVersion);
        jL_amountOfDownloadsOfNewVersion.setBounds(420, 200, 70, 20);

        jCB_ignoreThisVersion.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jCB_ignoreThisVersion.setText("Ignore this version during start");
        jCB_ignoreThisVersion.setName("jCB_ignoreThisVersion"); // NOI18N
        jCB_ignoreThisVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_ignoreThisVersionActionPerformed(evt);
            }
        });
        jP_newVersionAvailable.add(jCB_ignoreThisVersion);
        jCB_ignoreThisVersion.setBounds(30, 280, 620, 29);

        getContentPane().add(jP_newVersionAvailable);
        jP_newVersionAvailable.setBounds(0, 0, 700, 320);

        jP_noNewVersionAvailable.setLayout(null);

        jL_noNewVersionAvailable.setFont(new java.awt.Font("Tahoma", 1, 23)); // NOI18N
        jL_noNewVersionAvailable.setForeground(new java.awt.Color(255, 102, 0));
        jL_noNewVersionAvailable.setText("There is NOT a new version of the application available");
        jL_noNewVersionAvailable.setName("jL_noNewVersionAvailable"); // NOI18N
        jP_noNewVersionAvailable.add(jL_noNewVersionAvailable);
        jL_noNewVersionAvailable.setBounds(20, 10, 670, 30);

        getContentPane().add(jP_noNewVersionAvailable);
        jP_noNewVersionAvailable.setBounds(0, 320, 700, 50);

        jP_errorInQuery.setLayout(null);

        jL_errorInQuery.setFont(new java.awt.Font("Tahoma", 1, 26)); // NOI18N
        jL_errorInQuery.setForeground(new java.awt.Color(255, 0, 0));
        jL_errorInQuery.setText("Error in new version query");
        jL_errorInQuery.setName("jL_errorInQuery"); // NOI18N
        jP_errorInQuery.add(jL_errorInQuery);
        jL_errorInQuery.setBounds(20, 10, 400, 30);

        jL_particularError.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jL_particularError.setText("With error 404");
        jL_particularError.setName(""); // NOI18N
        jP_errorInQuery.add(jL_particularError);
        jL_particularError.setBounds(430, 20, 260, 20);

        jTextPane4.setEditable(false);
        jScrollPane3.setViewportView(jTextPane4);

        jP_errorInQuery.add(jScrollPane3);
        jScrollPane3.setBounds(50, 60, 580, 70);

        jL_error1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jL_error1.setText("Error :");
        jL_error1.setName("jL_error1"); // NOI18N
        jP_errorInQuery.add(jL_error1);
        jL_error1.setBounds(50, 40, 410, 20);

        getContentPane().add(jP_errorInQuery);
        jP_errorInQuery.setBounds(0, 510, 700, 140);

        jP_serverReturnedError.setLayout(null);

        jL_errorAtServer.setFont(new java.awt.Font("Tahoma", 1, 26)); // NOI18N
        jL_errorAtServer.setForeground(new java.awt.Color(255, 0, 0));
        jL_errorAtServer.setText("Server returned Error");
        jL_errorAtServer.setName("jL_errorAtServer"); // NOI18N
        jP_serverReturnedError.add(jL_errorAtServer);
        jL_errorAtServer.setBounds(20, 10, 670, 30);

        jL_error.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jL_error.setText("Error :");
        jL_error.setName("jL_error"); // NOI18N
        jP_serverReturnedError.add(jL_error);
        jL_error.setBounds(50, 40, 410, 20);

        jTextPane3.setEditable(false);
        jScrollPane2.setViewportView(jTextPane3);

        jP_serverReturnedError.add(jScrollPane2);
        jScrollPane2.setBounds(50, 60, 580, 70);

        getContentPane().add(jP_serverReturnedError);
        jP_serverReturnedError.setBounds(0, 370, 700, 140);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCB_ignoreThisVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_ignoreThisVersionActionPerformed
        // TODO add your handling code here:

		String downloadFileNameToIgnore = "";
		if( jCB_ignoreThisVersion.isSelected() )
		{
			downloadFileNameToIgnore = _newVersionQueryResult.getNewDownloadResource();
		}

		getAppliConf().setDownloadFileToIgnore( downloadFileNameToIgnore );

    }//GEN-LAST:event_jCB_ignoreThisVersionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCB_ignoreThisVersion;
    private javax.swing.JLabel jL_amountOfDownloadsOfNewVersion;
    private javax.swing.JLabel jL_download;
    private javax.swing.JLabel jL_error;
    private javax.swing.JLabel jL_error1;
    private javax.swing.JLabel jL_errorAtServer;
    private javax.swing.JLabel jL_errorInQuery;
    private javax.swing.JLabel jL_newVersionAvailable;
    private javax.swing.JLabel jL_noNewVersionAvailable;
    private javax.swing.JLabel jL_notes;
    private javax.swing.JLabel jL_numberOfDownloadsOfNewVersion;
    private javax.swing.JLabel jL_particularError;
    private javax.swing.JLabel jL_stable;
    private javax.swing.JLabel jL_totalAmountOfDownloadsOfApplication;
    private javax.swing.JLabel jL_totalNumberOfDownloadsOfApplication;
    private javax.swing.JPanel jP_errorInQuery;
    private javax.swing.JPanel jP_newVersionAvailable;
    private javax.swing.JPanel jP_noNewVersionAvailable;
    private javax.swing.JPanel jP_serverReturnedError;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTextPane jTextPane3;
    private javax.swing.JTextPane jTextPane4;
    // End of variables declaration//GEN-END:variables

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper) {
		jP_newVersionAvailable = compMapper.mapComponent(jP_newVersionAvailable);
		jTextPane1 = compMapper.mapComponent(jTextPane1);
		jScrollPane1 = compMapper.mapComponent(jScrollPane1);
		jL_totalNumberOfDownloadsOfApplication = compMapper.mapComponent(jL_totalNumberOfDownloadsOfApplication);
		jL_numberOfDownloadsOfNewVersion = compMapper.mapComponent(jL_numberOfDownloadsOfNewVersion);
		jL_amountOfDownloadsOfNewVersion = compMapper.mapComponent(jL_amountOfDownloadsOfNewVersion);
		jL_totalAmountOfDownloadsOfApplication = compMapper.mapComponent(jL_totalAmountOfDownloadsOfApplication);
		jL_notes = compMapper.mapComponent(jL_notes);
		jL_newVersionAvailable = compMapper.mapComponent(jL_newVersionAvailable);
		jL_stable = compMapper.mapComponent(jL_stable);
		jL_download = compMapper.mapComponent(jL_download);
		
		jL_noNewVersionAvailable = compMapper.mapComponent(jL_noNewVersionAvailable);
		jL_errorAtServer = compMapper.mapComponent(jL_errorAtServer);
		jL_error = compMapper.mapComponent(jL_error);
		jScrollPane2 = compMapper.mapComponent(jScrollPane2);
		jTextPane3 = compMapper.mapComponent(jTextPane3);
		jP_noNewVersionAvailable = compMapper.mapComponent(jP_noNewVersionAvailable);
		jP_serverReturnedError = compMapper.mapComponent(jP_serverReturnedError);
		jP_errorInQuery = compMapper.mapComponent(jP_errorInQuery);
		jL_errorInQuery = compMapper.mapComponent(jL_errorInQuery);
		jL_particularError = compMapper.mapComponent(jL_particularError);

		jL_error1 = compMapper.mapComponent(jL_error1);
		jScrollPane3 = compMapper.mapComponent(jScrollPane3);
		jTextPane4 = compMapper.mapComponent(jTextPane4);

		jCB_ignoreThisVersion = compMapper.mapComponent(jCB_ignoreThisVersion);

		if( jL_download instanceof UrlJLabel )
			_downloadLabel = (UrlJLabel) jL_download;
	}

	protected boolean isIgnored( NewVersionQueryResult nvqr )
	{
		boolean result = false;

		if( nvqr.getNewDownloadResource() != null )
		{
			result = Objects.equals(nvqr.getNewDownloadResource(), getAppliConf().getDownloadFileToIgnore() );
		}

		return( result );
	}
}
