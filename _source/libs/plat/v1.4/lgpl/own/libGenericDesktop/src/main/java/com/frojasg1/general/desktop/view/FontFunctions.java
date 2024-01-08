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
package com.frojasg1.general.desktop.view;

import com.frojasg1.general.desktop.lookAndFeel.ToolTipLookAndFeel;
import com.frojasg1.general.locale.LocaleFunctions;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.AttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FontFunctions
{
	protected static FontFunctions _instance;

	public static final String MULTILANGUAGE_FONT_NAME = "Arial Unicode MS";

	public static void changeInstance( FontFunctions inst )
	{
		_instance = inst;
	}

	public static FontFunctions instance()
	{
		if( _instance == null )
			_instance = new FontFunctions();
		return( _instance );
	}

	public Dimension getSizeOfText( Graphics grp, Font font, String text )
	{
		Dimension result = null;
		try
		{
			if( (grp != null) && ( font != null ) )
			{
				Rectangle2D rect = grp.getFontMetrics(font).getStringBounds( text, grp );
				result = new Dimension( (int) rect.getWidth(), (int) rect.getHeight() );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	public int getZoomedFontSize( int originalFontSize, double zoomFactor )
	{
		return( (int) Math.floor( originalFontSize * zoomFactor ) );
	}

	public Font createFontUIResource( Font originalFont, Font newFont )
	{
		Font result = newFont;

		if( ( originalFont instanceof FontUIResource ) &&
			( newFont != null ) &&
			!( newFont instanceof FontUIResource ) )
		{
			result = new FontUIResource( newFont );
		}

		return( result );
	}

	public Font getResizedFont( Font originalFont, float newSize )
	{
		Font result = null;

		if( originalFont != null )
		{
			float newFontSize = (float) Math.floor( newSize );
			if( newFontSize > 0 )
			{
				result = originalFont.deriveFont( newFontSize );
			}

			result = createFontUIResource( originalFont, result );
		}

		return( result );
	}

	public Font getZoomedFont( Font originalFont, double zoomFactor )
	{
		Font result = null;

		if( originalFont != null )
		{
			int newSize = getZoomedFontSize( originalFont.getSize(), zoomFactor);
			result = getResizedFont( originalFont, newSize );
		}

		return( result );
	}

	public Font getStyledFont( Font originalFont, int newStyle )
	{
		Font result = null;

		if( originalFont != null )
		{
			result = originalFont.deriveFont( newStyle );

			result = createFontUIResource( originalFont, result );
		}

		return( result );
	}

	public Font getUnderlinedFont( Font originalFont )
	{
		Font result = null;

		if( originalFont != null )
		{
			if( originalFont.getAttributes().get( TextAttribute.UNDERLINE ) == TextAttribute.UNDERLINE_ON )
			{
				result = originalFont;
			}
			else
			{
				Hashtable<TextAttribute, Object> map =
						new Hashtable<TextAttribute, Object>();

				map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
				result = originalFont.deriveFont(map);
			}
		}

		return( result );
	}

	public Font getUnunderlinedFont( Font originalFont )
	{
		Font result = null;

		if( originalFont != null )
		{
			Map<TextAttribute, ?> map = originalFont.getAttributes();
			if( map.get( TextAttribute.UNDERLINE ) == null )
			{
				result = originalFont;
			}
			else
			{
				map.remove(TextAttribute.UNDERLINE );
				result = originalFont.deriveFont(map);
			}
		}

		return( result );
	}

	public boolean isMonospaced( Font font )
	{
		boolean result = false;

		if( font != null )
		{
			Map< TextAttribute, ? > map = font.getAttributes();
			result = map.containsKey( Font.MONOSPACED );
		}

		return( result );
	}

	public boolean isMultilanguageFont( Font font )
	{
		boolean result = false;

		if( font != null )
		{
			result = true;

			Map< String, Locale > map = LocaleFunctions.instance().getMapOfLanguageLocales();
			Iterator< Map.Entry< String, Locale > > it = map.entrySet().iterator();
			while( result && it.hasNext() )
			{
				Map.Entry<String, Locale> entry = it.next();

				String languageIso = entry.getKey();
				String localeLanguageName = LocaleFunctions.instance().getLanguageName( entry.getValue(), languageIso );

				int pos = font.canDisplayUpTo( localeLanguageName );

				result = ( ( pos < 0 ) || ( pos >= localeLanguageName.length() ) );
			}
		}

		return( result );
	}

	public Font deriveFont( Font originalFont, String newFontName )
	{
		Font result = null;

		if( originalFont != null )
		{
			result = new Font( newFontName, originalFont.getStyle(), originalFont.getSize() );
			result.deriveFont( originalFont.getAttributes() );
		}

		return( result );
	}

	public void internationalizeFont( Component comp, String language )
	{
		Locale locale = GenericFunctions.instance().getObtainAvailableLanguages().getLocaleOfLanguage( language );
		if( locale != null )
			standarizeFont( MULTILANGUAGE_FONT_NAME, comp, locale );
	}

	protected Font changeFont( Font originalFont, String newFontName, Locale locale )
	{
		Font result = null;

		if( ( originalFont != null ) && ! originalFont.getName().equals(newFontName ) )
		{
			boolean change = ( locale == null );
			if( !change )
			{
				String languageIso = LocaleFunctions.instance().getLanguageIsoCode(locale);
				String languageName = LocaleFunctions.instance().getLanguageName(locale, languageIso);

				if( languageName != null )
					change = ( originalFont.canDisplayUpTo( languageName ) > -1 );
			}

			if( change )
				result = deriveFont( originalFont, newFontName );
		}

		return( result );
	}

	public void standarizeFont( String fontName, Component comp )
	{
		standarizeFont( fontName, comp, null );
	}

	protected void standarizeFontSimple( String fontName, Component comp, Locale locale )
	{
		if( comp != null )
		{
			Font newFont = changeFont( comp.getFont(), fontName, locale );

			if( comp instanceof JEditorPane )
			{
				JEditorPane pane = (JEditorPane) comp;
				pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);	// to switch default font in JEditorPane

				if( newFont == null )
					pane.setFont( comp.getFont() );
			}

			if( newFont != null )
				comp.setFont( newFont );

			if( comp instanceof JComponent )
			{
				JComponent jcomp = (JComponent) comp;
				Border border = jcomp.getBorder();

				if( border instanceof TitledBorder )
				{
					TitledBorder tb = (TitledBorder) border;

					newFont = changeFont( tb.getTitleFont(), fontName, locale );
					if( newFont != null )
						tb.setTitleFont(newFont);
				}
			}
		}
	}

	protected void standarizeFont( String fontName, Component comp, Locale locale )
	{
		standarizeFontSimple( fontName, comp, locale );
		
		if( comp instanceof Container )
		{
			Container cont = (Container) comp;
			
			if( cont instanceof JFrame )
			{
				JFrame frame = (JFrame) cont;
				standarizeFont( fontName, frame.getJMenuBar(), locale );
			}
			else if( cont instanceof JTabbedPane )
			{
				JTabbedPane tpane = (JTabbedPane) cont;

				for( int ii=0; ii<tpane.getTabCount(); ii++ )
				{
					Component tab = tpane.getComponentAt( ii );
					standarizeFont( fontName, tab, locale );
				}
			}
			else if( cont instanceof JMenu )
			{
				JMenu jm = (JMenu) cont;
				standarizeFont( fontName, jm.getPopupMenu(), locale );
			}

			if( !( cont instanceof JDesktopPane ) )
			{
				for( int ii=0; ii<cont.getComponentCount(); ii++ )
				{
					standarizeFont( fontName, cont.getComponent(ii), locale );
				}
			}
		}
	}

	public FontUIResource getOriginalToolTipFont()
	{
		return( ToolTipLookAndFeel.instance().getOriginalToolTipFont() );
	}

	public static void main( String[] args )
	{
		System.out.println( "Fully multilanguage fonts: " );

		String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

		for( int ii=0; ii<fonts.length; ii++ )
		{
			Font font = new Font( fonts[ii], 0, 16 );

			if( instance().isMultilanguageFont( font ) )
			{
				System.out.println( "multilanguage font: \t" + font.getName() + ", isMonospaced: " + instance().isMonospaced(font) );
			}
		}
	}
}
