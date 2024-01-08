/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.paint;

import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PaintTextContext
{
	protected Supplier<String> _textGetter;
	protected Font _font = new Font( "Lucida", Font.BOLD, 16 );
	protected int _hundredPercentSize = 16;
	protected Color _notInvertedForegroundColor = Color.BLACK;
	protected Color _foregroundColor = Color.BLACK;

	protected Color _notInvertedBackgroundColor = null;
	protected Color _backgroundColor = null;

	public Supplier<String> getTextGetter() {
		return _textGetter;
	}

	public void setTextGetter(Supplier<String> _textGetter) {
		this._textGetter = _textGetter;
	}

	public Font getFont() {
		return _font;
	}

	public void setFont(Font _font) {
		this._font = _font;
	}

	public int getHundredPercentSize() {
		return _hundredPercentSize;
	}

	public void setHundredPercentSize(int _hundredPercentSize) {
		this._hundredPercentSize = _hundredPercentSize;
	}

	public Color getNotInvertedForegroundColor() {
		return _notInvertedForegroundColor;
	}

	public void setNotInvertedForegroundColor(Color _notInvertedColor) {
		this._notInvertedForegroundColor = _notInvertedColor;
	}

	public Color getForegroundColor() {
		return _foregroundColor;
	}

	public Color getNotInvertedBackgroundColor() {
		return _notInvertedBackgroundColor;
	}

	public void setNotInvertedBackgroundColor(Color _notInvertedBackgroundColor) {
		this._notInvertedBackgroundColor = _notInvertedBackgroundColor;
	}

	public Color getBackgroundColor() {
		return _backgroundColor;
	}

	protected FrameworkComponentFunctions getFrameworkCompFun()
	{
		return( FrameworkComponentFunctions.instance() );
	}

	public String getText()
	{
		String result = null;
		Supplier<String> textGetter = this.getTextGetter();
		if( textGetter != null )
			result = textGetter.get();

		return( result );
	}

	public void update( Component comp )
	{
		if( getFrameworkCompFun().isDarkMode(comp) )
			invertColors(comp);
		else
			setOriginalColors();

		setFont( zoomFont( comp ) );
	}

	protected void setOriginalColors()
	{
		_foregroundColor = getNotInvertedForegroundColor();
		_backgroundColor = getNotInvertedBackgroundColor();
	}

	protected void invertColors(Component comp)
	{
		ColorInversor ci = getFrameworkCompFun().getColorInversor(comp);

		_foregroundColor = ci.invertColor( getNotInvertedForegroundColor() );
		_backgroundColor = ci.invertColor( getNotInvertedBackgroundColor() );
	}

	protected Font zoomFont( Component comp )
	{
		return( getStringOverTableFont( getFontSize( comp ) ) );
	}

	protected int getFontSize( Component comp )
	{
		return( FrameworkComponentFunctions.instance().zoomValue(comp,
							getHundredPercentSize()) );
	}

	protected Font getStringOverTableFont( int size )
	{
		Font result = getFont();
		if( result.getSize() != size )
			result = FontFunctions.instance().getResizedFont( result, size);

		return( result );
	}
}
