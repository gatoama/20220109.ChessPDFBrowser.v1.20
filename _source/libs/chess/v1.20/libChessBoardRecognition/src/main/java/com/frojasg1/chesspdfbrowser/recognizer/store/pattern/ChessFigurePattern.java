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
package com.frojasg1.chesspdfbrowser.recognizer.store.pattern;

import com.frojasg1.chesspdfbrowser.recognizer.store.set.ChessFigurePatternSet;
import com.frojasg1.chesspdfbrowser.recognizer.utils.ImageStatsUtils;
import com.frojasg1.chesspdfbrowser.recognizer.utils.RecognitionUtils;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;
import com.frojasg1.general.desktop.image.pixel.impl.PixelStats;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessFigurePattern
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected String _name = null;

	protected String _type = null;

	protected Set<String> _fensOk = null;
	protected Set<String> _fensNok = null;

	protected ChessFigurePatternSet _parent = null;

	protected BufferedImage _image = null;
	protected PixelComponents[][] _pixels = null;

	protected BufferedImage _summarizedImage = null;
	protected PixelComponents[][] _summarizedPixels = null;

	protected double _minimumMeanErrorFoundToOtherPatternCodes = 1000d;
	protected Double _meanErrorThreshold = null;

	// function for DefaultConstructorInitCopier
	public void ChessFigurePattern()
	{
		
	}

	// function for DefaultConstructorInitCopier
	public synchronized void init( ChessFigurePattern other )
	{
		_name = other._name;
		_type = other._type;
		setImage( other._image );
		setSummarizedImage( other._summarizedImage );

		_fensOk = _copier.copyCollection( other._fensOk );
		_fensNok = _copier.copyCollection( other._fensNok );
		_parent = other._parent;

		_minimumMeanErrorFoundToOtherPatternCodes = other._minimumMeanErrorFoundToOtherPatternCodes;
		_meanErrorThreshold = other._meanErrorThreshold;
	}

	public void init( String name, String type, ChessFigurePatternSet parent )
	{
		_name = name;
		_type = type;
		_image = null;
		_fensOk = new HashSet<>();
		_fensNok = new HashSet<>();
		setParent( parent );
	}

	public void setParent( ChessFigurePatternSet parent )
	{
		_parent = parent;
	}

	public ChessFigurePatternSet getParent()
	{
		return( _parent );
	}

	public void setMinimumMeanErrorFoundToOtherPatternCodes( double value )
	{
		_minimumMeanErrorFoundToOtherPatternCodes = value;
	}

	public double getMinimumMeanErrorFoundToOtherPatternCodes()
	{
		return( _minimumMeanErrorFoundToOtherPatternCodes );
	}

	public void setMeanErrorThreshold( Double value )
	{
		_meanErrorThreshold = value;
	}

	public Double getMeanErrorThreshold()
	{
		return( _meanErrorThreshold );
	}

	public void setImage( BufferedImage image )
	{
		_image = image;

		_pixels = createPixelComponents( _image );
	}

	public void setSummarizedImage( BufferedImage image )
	{
		_summarizedImage = image;

		if( _summarizedImage != null )
			_summarizedPixels = createPixelComponents( _summarizedImage );
	}

	public BufferedImage getSummarizedImage() {
		return _summarizedImage;
	}

	public PixelComponents[][] getSummarizedPixels() {
		return _summarizedPixels;
	}

	public String getWbCombination( )
	{
		return( RecognitionUtils.instance().getWbCombination( getName() ) );
	}

	protected PixelComponents[][] createPixelComponents( BufferedImage image )
	{
		boolean signedComponents = true;
		return( ImageFunctions.instance().getPixelComponents(image, signedComponents) );
	}

	public String getName() {
		return _name;
	}

	// piece code is one of the allowed to build a FEN string
	public String getPieceCode() {
		String result = null;

		if( ( _type != null ) && ! _type.equals( ChessFigurePatternSet.EMPTY_WHITE_SQUARE_TYPE ) &&
			! _type.equals( ChessFigurePatternSet.EMPTY_BLACK_SQUARE_TYPE ) )
		{
			result = _type.substring(0,1);
		}

		return( result );
	}

	public String getType() {
		return _type;
	}

	public BufferedImage getImage() {
		return _image;
	}

	public Set<String> getFensOk() {
		return _fensOk;
	}

	public Set<String> getFensNok() {
		return _fensNok;
	}

	public void addFenOk( String fenStr )
	{
		_fensOk.add( fenStr );
	}

	public void addFenNok( String fenStr )
	{
		_fensNok.add( fenStr );
	}

	public void setFensOk( Set<String> fens )
	{
		_fensOk = fens;
	}

	public void setFensNok( Set<String> fens )
	{
		_fensNok = fens;
	}

	public PixelComponents[][] getPixels()
	{
		return( _pixels );
	}

	public PixelStats calculateStandardDeviationOfSummaryImage()
	{
		int borderToSkipThick = 1;
		return( ImageStatsUtils.instance().calculateStandardDeviation( _pixels, borderToSkipThick ) );
	}
}
