/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.image.builders;

import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.image.builders.helpers.ImageBuilderHelper;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.string.StringFunctions;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RingImageBuilder {
	
	
	protected static class LazyHolder
	{
		public static final RingImageBuilder INSTANCE = new RingImageBuilder();
	}

	public static RingImageBuilder instance()
	{
		return( LazyHolder.INSTANCE );
	}

	public BufferedImage build( Context context )
	{
		BufferedImage result = ImageFunctions.instance().createImage(context.getWidth(), context.getWidth());

		Graphics grp = result.createGraphics();

		drawRing( context, result );
		drawText( context, grp );

		grp.dispose();

		return( result );
	}
/*
	protected void drawRing( Context context, Graphics grp )
	{
		grp.setColor( context.getRingColor() );
		int ovalWidth = context.getExternalDiameter();
		int xx = ( context.getWidth() - ovalWidth ) / 2;
		grp.drawOval(xx, xx, ovalWidth, ovalWidth);

		grp.setColor( new Color( 0xff000000 ) );
		ovalWidth = context.getInternalDiameter();
		xx = ( context.getWidth() - ovalWidth ) / 2;
		grp.drawOval(xx, xx, ovalWidth, ovalWidth);
	}
*/
	protected void drawRing( Context context, BufferedImage result )
	{
		int cx = context.getWidth() / 2;
		int cy = cx;
		int extRadius = context.getExternalDiameter() / 2;
		int intRadius = context.getInternalDiameter() / 2;

		ImageBuilderHelper.instance().drawRing(result, cx, cy,
			extRadius, intRadius, context.getRingColor() );
	}

	protected void drawText( Context context, Graphics grp )
	{
		String text = context.getText();
		if( text != null )
			drawText( context, split( text ), grp );
	}

	protected void drawText( Context context, String[] splittedText, Graphics grp )
	{
		Font font = new Font( "Lucida", Font.BOLD, context.getTextSize() );
		if( splittedText.length == 1 )
			drawText( context, grp, splittedText[0], font, -0.125f );
		else if( splittedText.length == 2 )
		{
			drawText( context, grp, splittedText[0], font, -0.65f );
			drawText( context, grp, splittedText[1], font, 0.4f );
		}
	}

	protected void drawText( Context context, Graphics grp, String text, Font font,
							float verticalOffset )
	{
		Color color = context.getTextColor();
		int center = context.getWidth() / 2;
		Point point = new Point( center, center + IntegerFunctions.zoomValueCeil(font.getSize(), verticalOffset ) );

		ImageFunctions.instance().paintStringCentered(grp, font, text, color, point);
	}

	protected String[] split( String text )
	{
		String[] result = null;
		
		String[] array = text.split( "\\s" );
		if( array.length == 1 )
			result = array;
		else if( array.length > 1 )
			result = split( array );

		return( result );
	}

	protected String[] split( String[] array )
	{
		String[] result = new String[2];

		int total = Arrays.stream(array).reduce( -1, (tot, str) -> ( tot + str.length() + 1 ),
												(t, e) -> ( t + e ) );
		int half = total / 2;

		int accum = -1;
		int prev;
		String first = "";
		int ii = 0;
		int lastAddedIndex = 0;
		String elem;
		while( accum < half )
		{
			elem = array[ii++];
			prev = accum;
			accum += elem.length() + 1;
			if( diff(total, prev) > diff(total, accum) )
			{
				first += elem;
				lastAddedIndex++;
			}
		}

		result[0] = first;
		result[1] = StringFunctions.instance().join(array, lastAddedIndex++, array.length, " ");

		return( result );
	}

	protected int diff( int total, int current )
	{
		return( Math.abs( total - 1 - 2 * current ) );
	}

	public static class Context
	{
		protected int _width;

		protected int _externalDiameter;
		protected int _internalDiameter;
		protected Color _ringColor;

		protected int _textSize;
		protected String _text;
		protected Color _textColor;

		public Context(int _width, int _externalDiameter, int _internalDiameter, Color _ringColor,
						int _textSize, String _text, Color _textColor) {
			this._width = _width;
			this._externalDiameter = _externalDiameter;
			this._internalDiameter = _internalDiameter;
			this._ringColor = _ringColor;

			this._textSize = _textSize;
			this._text = _text;
			this._textColor = _textColor;
		}

		public int getWidth() {
			return _width;
		}

		public int getExternalDiameter() {
			return _externalDiameter;
		}

		public int getInternalDiameter() {
			return _internalDiameter;
		}

		public Color getRingColor() {
			return _ringColor;
		}

		public int getTextSize() {
			return _textSize;
		}

		public String getText() {
			return _text;
		}

		public Color getTextColor() {
			return _textColor;
		}
	}
}
