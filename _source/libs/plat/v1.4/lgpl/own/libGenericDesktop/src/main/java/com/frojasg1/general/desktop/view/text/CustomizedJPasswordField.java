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
package com.frojasg1.general.desktop.view.text;

import com.frojasg1.applications.common.components.hints.HintConfiguration;
import com.frojasg1.applications.common.components.hints.HintForComponent;
import com.frojasg1.applications.common.components.internationalization.window.ComponentWithOverlappedImage;
import com.frojasg1.applications.common.components.name.ComponentNameComponents;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageClientInterface;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageServerInterface;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorClientInterface;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.general.CallStackFunctions;
import com.frojasg1.general.desktop.GenericDesktopConstants;
import com.frojasg1.general.desktop.generic.keyboard.KeyboardFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.text.Document;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class CustomizedJPasswordField extends JPasswordField
										implements ChangeLanguageClientInterface,
													ChangeZoomFactorClientInterface,
													FocusListener,
													KeyListener
{
	public static final String sa_configurationBaseFileName = "CustomizedJPasswordField.properties";
	protected static final String sa_PROPERTIES_PATH_IN_JAR = GenericDesktopConstants.sa_PROPERTIES_PATH_IN_JAR;

	HintForComponent _hintForComponent = null;
	HintConfiguration _hintConf = null;

	ChangeLanguageServerInterface _languageServer = null;
	ChangeZoomFactorServerInterface _zoomServer = null;

	CustomizedJPasswordFieldConfiguration _textsLanguageConfiguration = null;

	public static boolean gethasToChange( String name )
	{
		boolean result = false;

		ComponentNameComponents cnc = new ComponentNameComponents( name );
		String hintCaps = cnc.getComponent( ComponentNameComponents.HINT_CAPS_COMPONENT );

		if( ( hintCaps != null ) && ( hintCaps.equalsIgnoreCase( "true" ) ) )
			result = true;

		return( result );
	}

	public static CustomizedJPasswordField createCustomizedJPasswordField( JPasswordField original )
	{
		CustomizedJPasswordField result = null;

		if( gethasToChange( original.getName() ) )
		{
			result = new CustomizedJPasswordField( );
		}

		return( result );
	}

	/**
     * Constructs a new <code>JPasswordField</code>,
     * with a default document, <code>null</code> starting
     * text string, and 0 column width.
     */
    public CustomizedJPasswordField() {
        this(null,null,0);
    }

    /**
     * Constructs a new <code>JPasswordField</code> initialized
     * with the specified text.  The document model is set to the
     * default, and the number of columns to 0.
     *
     * @param text the text to be displayed, <code>null</code> if none
     */
    public CustomizedJPasswordField(String text) {
        this(null, text, 0);
    }

    /**
     * Constructs a new empty <code>JPasswordField</code> with the specified
     * number of columns.  A default model is created, and the initial string
     * is set to <code>null</code>.
     *
     * @param columns the number of columns &gt;= 0
     */
    public CustomizedJPasswordField(int columns) {
        this(null, null, columns);
    }

    /**
     * Constructs a new <code>JPasswordField</code> initialized with
     * the specified text and columns.  The document model is set to
     * the default.
     *
     * @param text the text to be displayed, <code>null</code> if none
     * @param columns the number of columns &gt;= 0
     */
    public CustomizedJPasswordField(String text, int columns) {
        this(null, text, columns);
    }

    /**
     * Constructs a new <code>JPasswordField</code> that uses the
     * given text storage model and the given number of columns.
     * This is the constructor through which the other constructors feed.
     * The echo character is set to '*', but may be changed by the current
     * Look and Feel.  If the document model is
     * <code>null</code>, a default one will be created.
     *
     * @param doc  the text storage to use
     * @param txt the text to be displayed, <code>null</code> if none
     * @param columns  the number of columns to use to calculate
     *   the preferred width &gt;= 0; if columns is set to zero, the
     *   preferred width will be whatever naturally results from
     *   the component implementation
     */
    public CustomizedJPasswordField(Document doc, String txt, int columns) {
        super(doc, txt, columns);
	}

	public void init()
	{
		_hintForComponent = createHintForComponent();
		_hintConf = _hintForComponent.getHintConfiguration();

		addKeyListener(this);
		addFocusListener(this);
	}

	@Override
	public void setFont( Font font )
	{
		super.setFont( font );
		System.out.println( "Font: " + font );
//		CallStackFunctions.instance().dumpCallStack( String.format( "setFont: %s", font ) );
	}

	protected HintForComponent createHintForComponent()
	{
		HintForComponent result = new HintForComponent( this, "", HintConfigurationForCustomizedJPasswordField.instance() ) {
					@Override
					protected Point getOptimalPositionForHint( ComponentWithOverlappedImage cwoi,
												BufferedImage image,
												Point mouseLocation )
					{
						return( getOptimalPositionForHint_customizedJPasswordField( cwoi, image, mouseLocation ) );
					}
		};

		return( result );
	}

	protected PointResult getPointCorrection( int xxCandidate, int yyCandidate,
										int minimumXX, int minimumYY,
										int frameWidth, int frameHeight,
										int imageWidth, int imageHeight,
										Point absoluteLocationOfJPasswordField )
	{
		int xx = (int) Math.max( minimumXX, Math.min( xxCandidate, frameWidth - imageWidth - 1 ) );
		int yy = (int) Math.max( minimumYY, Math.min( yyCandidate, frameHeight - imageHeight - 1 ) );

		int bottomOfComponent = absoluteLocationOfJPasswordField.y + getHeight();

		boolean overlaps = !(
								( absoluteLocationOfJPasswordField.y >= minimumYY ) &&
								( absoluteLocationOfJPasswordField.y >= ( yy + imageHeight ) ) ||
								( bottomOfComponent <= frameHeight ) &&
								( bottomOfComponent <= yy )
							);

		PointResult result = new PointResult( new Point( xx, yy ), !overlaps );

		return( result );
	}

	protected Point getOptimalPositionForHint_customizedJPasswordField( ComponentWithOverlappedImage cwoi,
								BufferedImage image,
								Point mouseLocation )
	{
		int yyToSubstract = (int) ( image.getHeight() + 5 );
		int xxToAdd = 5;

		Point locationOfParent = cwoi.getLocationOnScreen_forOverlappingImage();
		Point locationOfJPasswordField = this.getLocationOnScreen();

		Point absoluteLocation = new Point( (int) ( locationOfJPasswordField.getX() - locationOfParent.getX() ),
											(int) ( locationOfJPasswordField.getY() - locationOfParent.getY() ) );

		int minimumYY = 0;
		if( cwoi instanceof JFrame )
			minimumYY = 33;

		Insets insets = null;
		if( cwoi instanceof Component )
		{
			insets = ViewFunctions.instance().getBorders( (Component) cwoi );

			minimumYY = insets.top;
		}

		int frameWidth = cwoi.getWidth_forOverlappingImage();
		int frameHeight = cwoi.getHeight_forOverlappingImage();
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		int minimumXX = 0;
		
		int xx = (int) ( absoluteLocation.getX() + xxToAdd );
		int yy = (int) ( absoluteLocation.getY() - yyToSubstract );

		Point result = null;
		PointResult pr = getPointCorrection( xx, yy,
										minimumXX, minimumYY,
										frameWidth, frameHeight,
										imageWidth, imageHeight,
										absoluteLocation );

		if( pr.fulFills() )
			result = pr.getPoint();

		if( result == null )
		{
			int yyToAdd = ( getHeight() + 5 );
			
			xx = (int) ( absoluteLocation.getX() + xxToAdd );
			yy = (int) ( absoluteLocation.getY() + yyToAdd );

			pr = getPointCorrection( xx, yy,
										minimumXX, minimumYY,
										frameWidth, frameHeight,
										imageWidth, imageHeight,
										absoluteLocation );
			result = pr.getPoint();
		}

		return( result );
	}

	protected CustomizedJPasswordFieldConfiguration createTextsLanguageConfiguration( BaseApplicationConfigurationInterface conf )
	{
		CustomizedJPasswordFieldConfiguration result = null;
		if( conf != null )
			result = new CustomizedJPasswordFieldConfiguration( conf.getConfigurationMainFolder(),
																conf.getApplicationName(),
																conf.getApplicationGroup(),
																sa_configurationBaseFileName,
																sa_PROPERTIES_PATH_IN_JAR );
		return( result );
	}

	public void setBaseApplicationConfiguration( BaseApplicationConfigurationInterface conf )
	{
		if( conf != null )
		{
			if( _textsLanguageConfiguration == null )
				_textsLanguageConfiguration = createTextsLanguageConfiguration( conf );

			registerToChangeLanguageAsObserver( conf );
			registerToChangeZoomFactorAsObserver( conf );
		}
	}

	@Override
	public String getLanguage()
	{
		String result = null;
		if( _languageServer != null )
			result = _languageServer.getLanguage();
		return( result );
	}

	@Override
	public void changeLanguage(String newLanguage) throws Exception
	{
		try
		{
			_textsLanguageConfiguration.changeLanguage(newLanguage);

			_hintForComponent.setHint( _textsLanguageConfiguration.getHint() );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void unregisterFromChangeLanguageAsObserver()
	{
		if( _languageServer != null )
			_languageServer.unregisterChangeLanguageObserver(this);
	}

	@Override
	public void registerToChangeLanguageAsObserver(ChangeLanguageServerInterface conf)
	{
		unregisterFromChangeLanguageAsObserver();

		_languageServer = conf;
		if( _languageServer != null )
		{
			_languageServer.registerChangeLanguageObserver(this);
			
			try
			{
				this.changeLanguage( _languageServer.getLanguage() );
			}
			catch( Exception ex )
			{}
		}
	}

	protected void setHintConfiguration( HintConfiguration hc )
	{
		_hintConf = hc;
		_hintForComponent.setHintConfiguration( _hintConf );
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		if( KeyboardFunctions.isCapsOn() )
			_hintForComponent.showHint();
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		_hintForComponent.hideHint();
	}

	@Override
	public double getZoomFactor()
	{
		double result = 1.0D;
		if( _zoomServer != null )
			result = _zoomServer.getZoomFactor();

		return( result );
	}

	@Override
	public void changeZoomFactor(double zoomFactor) 
	{
		_hintConf.changeZoomFactor( zoomFactor );
	}

	@Override
	public void changeZoomFactor_centerMousePointer(double zoomFactor)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void unregisterFromChangeZoomFactorAsObserver()
	{
		if( _zoomServer != null )
			_zoomServer.unregisterZoomFactorObserver(this);
	}

	@Override
	public void registerToChangeZoomFactorAsObserver(ChangeZoomFactorServerInterface conf)
	{
		unregisterFromChangeZoomFactorAsObserver();
		_zoomServer = conf;
		if( _zoomServer != null )
		{
			_zoomServer.registerZoomFactorObserver(this);
			this.changeZoomFactor( _zoomServer.getZoomFactor() );
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if( KeyboardFunctions.isCapsOn() )
			_hintForComponent.showHint();
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if( ! KeyboardFunctions.isCapsOn() )
			_hintForComponent.hideHint();
	}

	protected class PointResult
	{
		protected Point _point;
		protected boolean _fulFills;

		public PointResult( Point point, boolean fulFills )
		{
			_point = point;
			_fulFills = fulFills;
		}

		public Point getPoint()
		{
			return( _point );
		}

		public boolean fulFills()
		{
			return( _fulFills );
		}
	}
}
