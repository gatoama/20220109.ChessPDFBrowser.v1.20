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
package com.frojasg1.chesspdfbrowser.recognizer.store.set.loader;

import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;
import com.frojasg1.chesspdfbrowser.recognizer.store.pattern.ChessFigurePattern;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ChessFigurePatternSet;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ComponentsStats;
import com.frojasg1.chesspdfbrowser.recognizer.utils.RecognitionUtils;
import com.frojasg1.general.structures.Pair;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.persistency.loader.impl.ModelToXmlBase;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessFigurePatternSetToXml extends ModelToXmlBase<ChessFigurePatternSet>
{
	protected ChessFigurePatternSet _model = null;

	@Override
	public XmlElement build( ChessFigurePatternSet model )
	{
		_model = model;

		XmlElement result = createElement( "chesspatternset" );

		XmlElement chesspatterntypes = createElement( "chesspatterntypes" );

		XmlElement nameXe = createElement( "name" );
		nameXe.setText( model.getSingleFolderName() );

		XmlElement edgelengthXe = createElement( "edgelength" );
		edgelengthXe.setText( String.valueOf( model.getEdgeLength() ) );

		XmlElement emptySquareColorStatsXe = createEmptySquareComponentStatsElement( model.getEmptySquaresComponentsStats() );

		XmlElement imageSizeGridListXe = createImageSizeGridListElement( model.getListOfPairsImageSizeGrid() );


		result.addChild( nameXe );
		result.addChild( edgelengthXe );
		result.addChild( emptySquareColorStatsXe );
		result.addChild( imageSizeGridListXe );

		result.addChild( chesspatterntypes );

		translatePatternTypes( model.getMap(), chesspatterntypes );

		return( result );
	}

	protected XmlElement createImageSizeGridListElement( List<Pair<Dimension, ChessBoardGridResult> > list )
	{
		XmlElement result = createElement( "chessframes" );

		for( Pair<Dimension, ChessBoardGridResult> pair: list )
			result.addChild( createImageSizeGridPairElement( pair ) );

		return( result );
	}

	protected XmlElement createImageSizeGridPairElement( Pair<Dimension, ChessBoardGridResult> pair )
	{
		XmlElement result = createElement( "pair" );

		result.addChild( createImageSizeElement( pair.getKey() ) );
		result.addChild( createGridElement( pair.getValue() ) );

		return( result );
	}

	protected XmlElement createImageSizeElement( Dimension imageSize )
	{
		XmlElement result = createElement( "imagesize" );

		XmlElement widthXe = createElement( "width" );
		widthXe.setText( String.valueOf( (long) imageSize.getWidth() ) );

		XmlElement heightXe = createElement( "height" );
		heightXe.setText( String.valueOf( (long) imageSize.getWidth() ) );

		result.addChild(widthXe);
		result.addChild(heightXe);

		return( result );
	}

	protected XmlElement createGridElement( ChessBoardGridResult grid )
	{
		XmlElement result = createElement( "chessframe" );

		Rectangle rect = grid.getBoxBoundsInsideImage(1, 8);

		result.addChild( createLongElement( rect.width, "edgewidth" ) );
		result.addChild( createLongElement( rect.height, "edgeheight" ) );
		result.addChild( createLongElement( rect.x, "left" ) );
		result.addChild( createLongElement( rect.y, "upper" ) );

		return( result );
	}

	protected XmlElement createEmptySquareComponentStatsElement( Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares )
	{
		XmlElement result = createElement( "emptysquarecolorstats" );

		result.addChild( createComponentsStatsElement( componentStatsForEmptySquares.getKey(), "white" ) );
		result.addChild( createComponentsStatsElement( componentStatsForEmptySquares.getValue(), "black" ) );

		return( result );
	}

	protected XmlElement createComponentsStatsElement( ComponentsStats css, String tagName )
	{
		XmlElement result = createElement( tagName );

		result.addChild( createComponentStatsElement( css.getComponentStats( ComponentsStats.LUMINANCE ), "luminance" ) );
		result.addChild( createComponentStatsElement( css.getComponentStats( ComponentsStats.RED ), "red" ) );
		result.addChild( createComponentStatsElement( css.getComponentStats( ComponentsStats.GREEN ), "green" ) );
		result.addChild( createComponentStatsElement( css.getComponentStats( ComponentsStats.BLUE ), "blue" ) );

		return( result );
	}

	protected XmlElement createLongElement( long value, String tagName )
	{
		XmlElement result = createElement( tagName );
		result.setText( String.valueOf( value ) );

		return( result );
	}

	protected XmlElement createComponentStatsElement( ComponentsStats.ComponentStats cs, String tagName )
	{
		XmlElement result = createElement( tagName );

		result.addChild( createLongElement( cs.getAverage(), "average" ) );
		result.addChild( createLongElement( cs.getStandardDeviation(), "standarddeviation" ) );

		return( result );
	}

	protected void translatePatternTypes( Map<String, List<ChessFigurePattern>> map,
									XmlElement chesspatterntypes )
	{
		for( List<ChessFigurePattern> list: map.values() )
		{
			XmlElement patternTypeElem = createChessPatternTypeElem( list );

			XmlElement imagesXe = createElement( "chesspatternimages" );
			chesspatterntypes.addChild( patternTypeElem );
			for( ChessFigurePattern pattern: list )
			{
				imagesXe.addChild( createPattern( pattern ) );
			}

			patternTypeElem.addChild(imagesXe);
		}
	}

	protected XmlElement createChessPatternTypeElem( List<ChessFigurePattern> list )
	{
		XmlElement result = createElement( "chesspatterntype" );

		XmlElement typeXe = createElement( "type" );
		typeXe.setText( _model.getType( list ) );

		result.addChild( typeXe );

		return( result );
	}

	protected Double getMeanErrorThreshold( ChessFigurePattern pattern )
	{
		Double result = pattern.getMeanErrorThreshold();
//		if( result == null )
//			result = RecognitionUtils.MAX_MEAN_ERROR_FOR_SUMMARIZED_IMAGE_MATCH;

		return( result );
	}

	protected XmlElement createPattern( ChessFigurePattern pattern )
	{
		XmlElement result = createElement( "chesspatternimage" );

		XmlElement nameXe = createElement( "name" );
		nameXe.setText( pattern.getName() );

		XmlElement meanerrorthresholdXe = createElement( "meanerrorthreshold" );
		Double meanErrorThreshold = getMeanErrorThreshold( pattern );
		if( meanErrorThreshold != null )
			meanerrorthresholdXe.setText( format( meanErrorThreshold ) );

		XmlElement fenOkXe = createFenListElement( "ok", pattern.getFensOk() );
		XmlElement fenNokXe = createFenListElement( "nok", pattern.getFensNok() );

		result.addChild( nameXe );
		if( meanErrorThreshold != null )
			result.addChild( meanerrorthresholdXe );
		result.addChild( fenOkXe );
		result.addChild( fenNokXe );

		return( result );
	}

	protected XmlElement createFenListElement( String tagName, Set<String> fenList )
	{
		XmlElement result = createElement( tagName );

		for( String fen: fenList )
		{
			XmlElement fenXe = createElement( "fen" );
			fenXe.setText( fen );

			result.addChild( fenXe );
		}

		return( result );
	}
}
