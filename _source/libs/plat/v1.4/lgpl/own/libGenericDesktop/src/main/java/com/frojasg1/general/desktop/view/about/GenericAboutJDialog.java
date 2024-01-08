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
package com.frojasg1.general.desktop.view.about;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.desktop.view.pdf.ImageJPanel;
import com.frojasg1.desktop.liblens.graphics.lens.Lens;
import com.frojasg1.desktop.liblens.graphics.lens.LensJPanel;
import com.frojasg1.general.desktop.files.DesktopResourceFunctions;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.general.desktop.application.version.DesktopApplicationVersion;
import com.frojasg1.general.desktop.DesktopStreamFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.lib3d.components.api.about.animation.AnimationForAbout;
import com.frojasg1.general.lib3d.components.api.about.animation.AnimationForAboutFactory;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.language.file.LanguageFile;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

/**
 *
 * @author Usuario
 */
public class GenericAboutJDialog extends InternationalizedJDialog
								implements AboutJPanelControllerInterface
{
	protected static final String CONF_SEND_EMAIL_TO = "SEND_EMAIL_TO";
	protected static final String CONF_RELEASED_ON = "RELEASED_ON";
	protected static final String CONF_TITLE = "TITLE";
	protected static final String BASE_OF_CONF_ABOUT_ = "ABOUT_";

	protected static final int LENS_RADIUS = 125;
	protected static final int LENS_BORDER_THICK = 4;
	protected static final int LENS_BORDER_EXTERNAL_COLOR=0xFF000000;	// solid black
	protected static final int LENS_BORDER_INTERNAL_COLOR=0xFF000000;	// solid black

//	protected static final String DEFAULT_LANGUAGE = "EN";

	protected GenericAboutJPanel _aboutJPanel = null;
	protected LensJPanel _lensJPanel = null;

	protected Object _mutex = new Object();

	protected String _configurationBaseFileName = "JDial_about";

	protected String _emailAddress = null;
//	protected String _homeWebPage = null;

	protected SelectionThread _selectionThread = null;
	protected String _releaseDateInSpanishFormat = null;

	protected int _oldScrollYYValue = -1;

	protected LanguageFile _languageFile = null;

	protected AnimationForAbout _animation = null;

	protected boolean _alreadySetVisibleBefore = false;

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper) {
		jPanel1 = compMapper.mapComponent(jPanel1);
	}

	protected class SelectionThread extends Thread
	{
		protected boolean _hasToStart = false;
		protected boolean _hasToStop = false;
		protected static final long sa_sleepTime = 33;
		protected static final int sa_increment = 1;

		public SelectionThread(  )
		{
			super();
		}

		public void M_start()
		{
			_hasToStart = true;
		}
		
		public void M_stop()
		{
			_hasToStop = true;
		}

		public void run()
		{
			try
			{
				while( ! _hasToStart && ! _hasToStop )
				{
					try
					{
						sleep( sa_sleepTime );
					}
					catch( Throwable th )
					{
						th.printStackTrace();
					}
				}

				while( ! _hasToStop )
				{
					for( int ii=0; (ii<getText().length()-2-sa_increment) && !_hasToStop; ii = ii + sa_increment )
					{
						synchronized( _mutex )
						{
							int position = IntegerFunctions.min( ii, getText().length()-2-sa_increment );

							_oldScrollYYValue = -1;

							JScrollPane jsp = null;
							if( _aboutJPanel.getJTextComponent().getParent().getParent() instanceof JScrollPane )
							{
								jsp = (JScrollPane) _aboutJPanel.getJTextComponent().getParent().getParent();
								_oldScrollYYValue = jsp.getVerticalScrollBar().getValue();
							}
							
							_aboutJPanel.getJTextComponent().setCaretPosition( position + 2 );
							setSelectionStart( position );
							setSelectionEnd( position + 2 );

//							System.out.println( "position: " + position );

							_aboutJPanel.nextStepForAnimation();

//							System.out.println( "position: " + position );

							java.awt.EventQueue.invokeLater(new Runnable() {
								public void run() {
									try
									{
										_aboutJPanel.changeUrlCursor(_oldScrollYYValue);
										
									}
									catch( Throwable th )
									{

									}
								} } );

							if( isVisible() )
								repaint();
						}

						try
						{
							sleep( sa_sleepTime );
						}
						catch( Throwable th )
						{
							th.printStackTrace();
						}
					}
				}
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
	}

	public GenericAboutJDialog(java.awt.Frame parent, boolean modal,
								BaseApplicationConfigurationInterface applicationConfiguration,
								String imageResourceName, String configurationBaseFileName,
								String rtfSingleFileName )
	{
		this(parent, modal, applicationConfiguration,
			imageResourceName,
			configurationBaseFileName,
			rtfSingleFileName,
			(AnimationForAboutFactory) null,
			(Consumer<InternationalizationInitializationEndCallback>) null);
	}

	public GenericAboutJDialog(java.awt.Frame parent, boolean modal,
								BaseApplicationConfigurationInterface applicationConfiguration,
								String imageResourceName, String configurationBaseFileName,
								String rtfSingleFileName,
								AnimationForAboutFactory animFactory,
								Consumer<InternationalizationInitializationEndCallback> initializationEndCallback )
	{
		this(parent, modal, applicationConfiguration,
			imageResourceName == null ? null : DesktopResourceFunctions.instance().loadResourceImage( imageResourceName ),
			configurationBaseFileName,
			rtfSingleFileName,
			animFactory, initializationEndCallback);
	}

	/**
	 * Creates new form GenericAboutJDialog
	 */
	public GenericAboutJDialog(java.awt.Frame parent, boolean modal,
								BaseApplicationConfigurationInterface applicationConfiguration,
								BufferedImage image, String configurationBaseFileName,
								String rtfSingleFileName,
								AnimationForAboutFactory animFactory,
								Consumer<InternationalizationInitializationEndCallback> initializationEndCallback )
	{
		super(parent, modal, applicationConfiguration, null,
			initializationEndCallback, true);

		_animation = createAnimationForAbout( animFactory );

		_languageFile = createLanguageFile( rtfSingleFileName,
											applicationConfiguration.getInternationalPropertiesPathInJar(),
											applicationConfiguration.getDefaultLanguageBaseConfigurationFolder() );

		if( configurationBaseFileName != null )
			_configurationBaseFileName =  configurationBaseFileName;

		initComponents();

		initOwnComponents( image );

		SwingUtilities.invokeLater( this::setWindowConfiguration );

		if( modal )
			setAlwaysOnTop( true );
	}

	public GenericAboutJDialog(java.awt.Frame parent, boolean modal,
								BaseApplicationConfigurationInterface applicationConfiguration,
								String imageResourceName, String configurationBaseFileName,
								String rtfSingleFileName,
								String languageFolderResourceName,
								String languageFolderDiskFileName )
	{
		this(parent, modal, applicationConfiguration,
			imageResourceName,
			configurationBaseFileName,
			rtfSingleFileName,
			languageFolderResourceName,
			languageFolderDiskFileName,
			(AnimationForAboutFactory) null,
			(Consumer<InternationalizationInitializationEndCallback>) null);
	}

	/**
	 * Creates new form GenericAboutJDialog
	 */
	public GenericAboutJDialog(java.awt.Frame parent, boolean modal,
								BaseApplicationConfigurationInterface applicationConfiguration,
								String imageResourceName, String configurationBaseFileName,
								String rtfSingleFileName,
								String languageFolderResourceName,
								String languageFolderDiskFileName,
								AnimationForAboutFactory animFactory,
								Consumer<InternationalizationInitializationEndCallback> initializationEndCallback )
	{
		this(parent, modal, applicationConfiguration,
			imageResourceName == null ? null : DesktopResourceFunctions.instance().loadResourceImage( imageResourceName ),
			configurationBaseFileName,
			rtfSingleFileName,
			languageFolderResourceName,
			languageFolderDiskFileName,
			animFactory, initializationEndCallback);
	}

	/**
	 * Creates new form GenericAboutJDialog
	 */
	public GenericAboutJDialog(java.awt.Frame parent, boolean modal,
								BaseApplicationConfigurationInterface applicationConfiguration,
								BufferedImage image, String configurationBaseFileName,
								String rtfSingleFileName,
								String languageFolderResourceName,
								String languageFolderDiskFileName,
								AnimationForAboutFactory animFactory,
								Consumer<InternationalizationInitializationEndCallback> initializationEndCallback )
	{
		super(parent, modal, applicationConfiguration, null, initializationEndCallback,
			true);

		_animation = createAnimationForAbout( animFactory );

		_languageFile = createLanguageFile( rtfSingleFileName,
											languageFolderResourceName,
											languageFolderDiskFileName );

		if( configurationBaseFileName != null )
			_configurationBaseFileName =  configurationBaseFileName;

		initComponents();

		initOwnComponents( image );

		SwingUtilities.invokeLater( this::setWindowConfiguration );

		if( modal )
			setAlwaysOnTop( true );
	}

	protected AnimationForAbout createAnimationForAbout( AnimationForAboutFactory animFactory )
	{
		animFactory.setColors( invertColorsIfNecessary( animFactory.getBrightModeColors() ) );
		AnimationForAbout result = animFactory.createAnimationForAbout();
		return( result );
	}

	protected LanguageFile createLanguageFile( String rtfSingleFileName,
								String languageFolderResourceName,
								String languageFolderDiskFileName )
	{
		return( new LanguageFile( rtfSingleFileName,
									languageFolderResourceName,
									languageFolderDiskFileName ) );
	}

	protected void setForegroundColorOfHomeWebPage( Color color )
	{
		_aboutJPanel.setForegroundColorOfHomeWebPage( color );
	}
	
	protected void setData()
	{
		String authorEmailAddress = getAppliConf().getAuthorEmailAddress();
		SwingUtilities.invokeLater( () -> {
			setData( authorEmailAddress );
		});
	}

	public void setData( String emailAddress )
	{
		_emailAddress = emailAddress;

		setAnimationSize( new Dimension( 150, 150 ) );

		initComponentContents();
	}

	protected BufferedImage getImage( String pathInJarForApplicationImage )
	{
		BufferedImage result = null;
		try
		{
			result = DesktopResourceFunctions.instance().loadResourceImage(pathInJarForApplicationImage);
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		return( result );
	}

	protected String getApplicationVersion()
	{
		return( DesktopApplicationVersion.instance().getDownloadFile() );
	}

	protected GenericAboutJPanel createGenericAboutJPanel( BufferedImage image )
	{
		return( new GenericAboutJPanel( this, image,
										getApplicationVersion(),
										getAppliConf().getZoomFactor() ) );
	}

	protected void initOwnComponents( BufferedImage image )
	{
		_aboutJPanel = createGenericAboutJPanel( image );
		_aboutJPanel.setPreferredSize( jPanel1.getSize() );

		int lensRadius = IntegerFunctions.roundToInt( LENS_RADIUS * getAppliConf().getZoomFactor() );
		_lensJPanel = new LensJPanel( _aboutJPanel, lensRadius, Lens.SA_MODE_AMPLIFY, true, _mutex );
		_lensJPanel.setBorderOfLens(LENS_BORDER_EXTERNAL_COLOR, LENS_BORDER_INTERNAL_COLOR,
									IntegerFunctions.zoomValueInt( LENS_BORDER_THICK, getAppliConf().getZoomFactor() ) );
		_aboutJPanel.setLensJPanel( _lensJPanel );

		jPanel1.add( _lensJPanel );
		_lensJPanel.setBounds( 0, 0, jPanel1.getWidth(), jPanel1.getHeight() );

		_aboutJPanel.getJTextComponent().requestFocusInWindow();

		_aboutJPanel.setAnimation( _animation );
	}

	protected void initComponentContents()
	{
		setTitle( getInternationalString(CONF_TITLE) );

//		String text = createAboutText();
//		_aboutJPanel.setText( text );

		_aboutJPanel.setSendEmailButtonText(getInternationalString(CONF_SEND_EMAIL_TO) + " " +
												_emailAddress );	
//		_aboutJPanel.setHomePageLabelText( _homeWebPage );
	}

	protected void setWindowConfiguration( )
	{
		boolean adjustSizeOfFrameToContents = true;
		boolean adjustMinSizeOfFrameToContents = true;
		boolean switchToZoomComponents = true;
		boolean internationalizeFont = true;
		MapResizeRelocateComponentItem mapRRCI = null;
		Integer delayToInvokeCallback = null;

		createInternationalization(getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									_configurationBaseFileName,
									this,
									getParent(),
									null,
									true,
									mapRRCI,
									adjustSizeOfFrameToContents,
									adjustMinSizeOfFrameToContents,
									getAppliConf().getZoomFactor(),
									false,
									false,
									switchToZoomComponents,
									internationalizeFont,
									delayToInvokeCallback);
//									getAppliConf().hasToEnableUndoRedoForTextComponents(),
//									getAppliConf().hasToEnableTextCompPopupMenus() );

		registerInternationalString(CONF_SEND_EMAIL_TO, "Send e-mail to:" );
		registerInternationalString(CONF_RELEASED_ON, "Released on" );
		registerInternationalString(CONF_TITLE, "About" );

//		setIcon( "com/frojasg1/chesspdfbrowser/resources/icons/App.icon.png" );
	}

	protected String getText()
	{
		String result = "";

		if( ( _aboutJPanel != null ) && ( _aboutJPanel.getJTextComponent() != null ) )
		{
			int length = _aboutJPanel.getJTextComponent().getDocument().getLength();
			try
			{
				result = _aboutJPanel.getJTextComponent().getDocument().getText( 0, length );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

		return( result );
	}

	public void setSelectionStart( int position )
	{
		_aboutJPanel.getJTextComponent().setSelectionStart( position );
	}

	public void setSelectionEnd( int position )
	{
		_aboutJPanel.getJTextComponent().setSelectionEnd( position );
	}

	public void setRtfDocument( Document doc )
	{
		synchronized( _mutex )  // because of the setSelectionStart and End in the setRtfDocument of _aboutJPanel, and may be the changes in the view.
		{
			_aboutJPanel.setRtfDocument(doc);
		}
	}

	public void setRtfDocument( String language )
	{
		try( InputStream is = _languageFile.getInputStream(language ); )
		{
			Document doc = DesktopStreamFunctions.instance().loadAndZoomRtfInputStream(is, getAppliConf().getZoomFactor() );
			setRtfDocument( doc );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
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

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About");
        setMinimumSize(new java.awt.Dimension(650, 440));
        setName("AboutJDialog"); // NOI18N
        setPreferredSize(new java.awt.Dimension(650, 440));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        jPanel1.setMinimumSize(new java.awt.Dimension(650, 440));
        jPanel1.setPreferredSize(new java.awt.Dimension(650, 405));
        jPanel1.setLayout(null);
        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 650, 435);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:

		exit( null );

    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

	public void setFrameWidth( int value )
	{
		_aboutJPanel.setFrameThick(value);
	}
	
	public void setOriginalFontForTextLines( Font font )
	{
		_aboutJPanel.setOriginalFontForTextLines(font);
	}

	public void setForegroundColorForTextLines( Color color )
	{
		_aboutJPanel.setForegroundColorForTextLines(color);
	}
	
	public void addTextLine( ImageJPanel.TextLine textLine )
	{
		_aboutJPanel.addTextLine(textLine);
	}

	public void clearTextLines()
	{
		_aboutJPanel.clearTextLines();
	}

	public Dimension getSizeOfOriginalImageJPanel()
	{
		return( _aboutJPanel.getSizeOfOriginalImageJPanel() );
	}
	
	@Override
	public void exit( Object origin )
	{
		if( _selectionThread != null )	_selectionThread.M_stop();

		while( _selectionThread.isAlive() )
		{
			try
			{
				Thread.sleep( 100 );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
		_selectionThread = null;

		boolean closeWindow = true;
		formWindowClosing( closeWindow );
	}

	@Override
	public void sendEmailAddress()
	{
		GenericFunctions.instance().getSystem().mailTo(_emailAddress);
	}

/*	@Override
	public void visitHomeWebPage()
	{
		GenericFunctions.instance().getSystem().browse(_homeWebPage);
	}
*/

	@Override
	public synchronized void setVisible( boolean visible )
	{
		if( visible )
		{
			if( getAlreadyInitializedCallback() && ! _alreadySetVisibleBefore )
			{
				_alreadySetVisibleBefore = true;
				_selectionThread = new SelectionThread();
				_selectionThread.start();

				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						try
						{
							_selectionThread.M_start();
						}
						catch( Throwable th )
						{
						}
					} } );

//				SwingUtilities.invokeLater( () -> super.setVisible( visible ) );
				super.setVisible( visible );
			}
		}
		else
			super.setVisible( visible );
	}

	protected Date getReleaseDate()
	{
		Date result = null;

		SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy" );
		try
		{
			result = sdf.parse( _releaseDateInSpanishFormat );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
/*
		Calendar calendar = new GregorianCalendar();
		calendar.set( 2016, Calendar.JANUARY, 30 );
		Date result = calendar.getTime();
*/
		return( result );
	}
/*
	protected String createAboutText()
	{
		StringBuilder sb = new StringBuilder();
		
		String tmpStr = _applicationName + " " + _version + "\n";
		sb.append( tmpStr );

		tmpStr = getInternationalString(CONF_RELEASED_ON) + " " +
							DateFunctions.instance().formatDate_yyyy( getReleaseDate(), DateFormat.SHORT ) + " \n";

		String baseLabel = BASE_OF_CONF_ABOUT_;
		int index=1;
		while( tmpStr != null )
		{
			sb.append( tmpStr + "\n" );
			String label = String.format( "%s%d", baseLabel, index );
			tmpStr = getInternationalString( label );
			index++;
		}

		tmpStr = " " + _emailAddress + "\n";
		sb.append( tmpStr );

		tmpStr = "                                                  ";
		sb.append( tmpStr + "\n" );
		sb.append( tmpStr );

		return( sb.toString() );
	}
*/
	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

		_animation.animationSetEnclosingDimension( getSize() );
		_animation.animationSetMovingSpeedFactor( getAppliConf().getZoomFactor() );

		setData();
	}

	public void setImageBackground( Color color )
	{
		_aboutJPanel.setImageBackground(color);
	}

	public AnimationForAbout getAnimation()
	{
		return( _animation );
	}

	public void setAnimationSize( Dimension dimen )
	{
		if( getAnimation() != null )
			getAnimation().animationSetDimension(
				ViewFunctions.instance().getNewDimension(dimen,
					getAppliConf().getZoomFactor() ) );
	}

	protected String getVersion()
	{
		return( getAppliConf().getApplicationVersion() );
	}

	protected String getApplicationName()
	{
		return( getAppliConf().getApplicationName() );
	}
}
