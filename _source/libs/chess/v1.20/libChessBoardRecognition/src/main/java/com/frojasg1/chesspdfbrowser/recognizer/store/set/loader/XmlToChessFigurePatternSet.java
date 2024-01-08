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
import com.frojasg1.chesspdfbrowser.recognizer.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.recognizer.store.pattern.ChessFigurePattern;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ChessFigurePatternSet;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ComponentsStats;
import com.frojasg1.chesspdfbrowser.recognizer.utils.RecognitionUtils;
import com.frojasg1.general.number.DoubleFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.structures.Pair;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.XmlElementList;
import com.frojasg1.general.xml.persistency.loader.impl.XmlToModelBase;
import java.awt.Dimension;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class XmlToChessFigurePatternSet extends XmlToModelBase<ChessFigurePatternSet>
{
	public static final String GLOBAL_CONF_FILE_NAME = "XmlToChessFigurePatternSet.properties";

	protected static final String CONF_XML_WITHOUT_CHESSPATTERNSET = "XML_WITHOUT_CHESSPATTERNSET";


	ChessFigurePatternSet _result = null;

	public XmlToChessFigurePatternSet()
	{
	}

	protected void init( String languageGlobalConfFileName,
							String propertiesPathInJar )
	{
		throw( new RuntimeException( "Non usable init function" ) );
	}

	public void init()
	{
		super.init( GLOBAL_CONF_FILE_NAME, LibConstants.sa_PROPERTIES_PATH_IN_JAR );
	}

	protected ChessFigurePatternSet createEmptyChessFigurePatternSet()
	{
		_result = new ChessFigurePatternSet();

		return( _result );
	}

	@Override
	public ChessFigurePatternSet build( XmlElement xmlElement )
	{
		ChessFigurePatternSet result = null;

		if( xmlElement != null )
		{
			result = createEmptyChessFigurePatternSet();

			XmlElement rootXe = xmlElement;
			assertXmlElementName( rootXe, "chesspatternset" );

			loadChessFigurePatternSet( result, rootXe );
		}

		return( result );
	}

	protected void loadChessFigurePatternSet( ChessFigurePatternSet result, XmlElement xmlElement )
	{
		if( xmlElement == null )
			throw( new RuntimeException( getInternationalString( CONF_XML_WITHOUT_CHESSPATTERNSET ) ) );

		XmlElement nameXe = getMandatoryChild( xmlElement, "name", childNotPresentErrorMessage( "PatternSet", "name" ) );
		result.init( nameXe.getText() );

		XmlElement edgeLengthXe = getMandatoryChild( xmlElement, "edgelength", childNotPresentErrorMessage( "PatternSet", "edgeLength" ) );
		result.setEdgeLength( IntegerFunctions.parseInt( edgeLengthXe.getText() ) );

		XmlElement emptySquareComponentStatsXe = getMandatoryChild( xmlElement, "emptysquarecolorstats", childNotPresentErrorMessage( "PatternSet", "emptysquarecolorstats" ) );
		Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares = getEmptySquareLuminances( emptySquareComponentStatsXe );
		result.setEmptySquaresComponentsStats(componentStatsForEmptySquares);

		XmlElement imageSizeGridListXe = getMandatoryChild( xmlElement, "chessframes", childNotPresentErrorMessage( "PatternSet", "chessframes" ) );
		addImageSizeGridList( imageSizeGridListXe, result );

		XmlElement xe = xmlElement.getChild( "chesspatterntypes" );
		XmlElementList xel = xe.getChildrenByName( "chesspatterntype" );
		if( xel != null )
		{
			for( XmlElement patternXe: xel.getCollection() )
			{
				loadPatternType( patternXe, result );
/*
				List<ChessFigurePattern> list = loadPatternType( patternXe, result );
				if( ( list != null ) && !list.isEmpty() )
				{
					String type = _result.getType( list );
					result.getMap().put( type, list );
				}
*/
			}
		}
	}

	protected void addImageSizeGridList( XmlElement xmlElement, ChessFigurePatternSet result )
	{
		XmlElementList xel = xmlElement.getChildrenByName( "pair" );
		if( xel != null )
		{
			for( XmlElement pairXe: xel.getCollection() )
				addImageSizeGridPair( pairXe, result );
		}
	}

	protected void addImageSizeGridPair( XmlElement xmlElement, ChessFigurePatternSet result )
	{
		Dimension imageSize = getImageSize( xmlElement );
		ChessBoardGridResult grid = getGrid( xmlElement );

		result.addGrid(imageSize, grid);
	}

	protected Dimension getImageSize( XmlElement xmlElement )
	{
		XmlElement xe = getMandatoryChild( xmlElement, "imagesize", childNotPresentErrorMessage( "PatternSet", "imagesize" ) );

		int width = getInt( xe, "width", "PatternSet" );
		int height = getInt( xe, "height", "PatternSet" );
		Dimension result = new Dimension( width, height );

		return( result );
	}

	protected ChessBoardGridResult getGrid( XmlElement xmlElement )
	{
		XmlElement xe = getMandatoryChild( xmlElement, "chessframe", childNotPresentErrorMessage( "PatternSet", "chessframe" ) );

		int edgeWidth = getInt( xe, "edgewidth", "PatternSet" );
		int edgeHeight = getInt( xe, "edgeheight", "PatternSet" );
		int left = getInt( xe, "left", "PatternSet" );
		int upper = getInt( xe, "upper", "PatternSet" );

		ChessBoardGridResult result = createChessBoardGridResult( edgeWidth, edgeHeight, left, upper );

		return( result );
	}

	protected ChessBoardGridResult createChessBoardGridResult( int edgeWidth, int edgeHeight, int left, int upper )
	{
		ChessBoardGridResult result = new ChessBoardGridResult();
		result.init( edgeWidth );

		for( int jj=0; jj<ChessBoardGridResult.NUM_ELEMENTS; jj++ )
		{
			int xx = left + jj * edgeWidth;
			for( int ii=0; ii<ChessBoardGridResult.NUM_ELEMENTS; ii++ )
			{
				int yy = upper + ( ChessBoardGridResult.NUM_ROWS - ii ) * edgeHeight;
				Point point = new Point( xx, yy );
				result.addVertex( point, jj, ii);
			}
		}

		return( result );
	}

	protected Pair<ComponentsStats, ComponentsStats> getEmptySquareLuminances( XmlElement xmlElement )
	{
		ComponentsStats whiteSquareComponentsStats = getComponentsStats( xmlElement, "white" );
		ComponentsStats blackSquareComponentsStats = getComponentsStats( xmlElement, "black" );

		return( new Pair<>( whiteSquareComponentsStats, blackSquareComponentsStats ) );
	}

	protected ComponentsStats getComponentsStats( XmlElement xmlElement, String childName )
	{
		ComponentsStats result = new ComponentsStats();
		result.init();

		XmlElement xe = getMandatoryChild( xmlElement, childName, childNotPresentErrorMessage("PatternSet", childName ) );

		addComponentStats( xe, "luminance", ComponentsStats.LUMINANCE, result );
		addComponentStats( xe, "red", ComponentsStats.RED, result );
		addComponentStats( xe, "green", ComponentsStats.GREEN, result );
		addComponentStats( xe, "blue", ComponentsStats.BLUE, result );

		return( result );
	}

	protected void addComponentStats( XmlElement xmlElement, String childName, int compIndex,
										ComponentsStats result )
	{
		XmlElement xe = getMandatoryChild( xmlElement, childName, childNotPresentErrorMessage("PatternSet", childName ) );
		
		long average = getLong( xe, "average", "PatternSet" );
		long standardDeviation = getLong( xe, "standarddeviation", "PatternSet" );

		result.setComponentStats( average, standardDeviation, compIndex);
	}

	protected void loadPatternType( XmlElement xmlElement,
									ChessFigurePatternSet ps )
	{
//		List<ChessFigurePattern> result = new ArrayList<>();

		XmlElement typeXe = getMandatoryChild( xmlElement, "type", childNotPresentErrorMessage("PatternSet", "type" ) );
		String type = typeXe.getText();

		XmlElement xe = xmlElement.getChild( "chesspatternimages" );
		XmlElementList xel = xe.getChildrenByName( "chesspatternimage" );
		if( xel != null )
		{
			for( XmlElement patternXe: xel.getCollection() )
			{
				ChessFigurePattern pattern = loadPattern( type, patternXe, ps );
				if( pattern != null )
					ps.add( pattern );
			}
		}

//		return( result );
	}

	protected Double loadMeanErrorThreshold( XmlElement parentXe )
	{
		Double result = null;
		XmlElement xe = parentXe.getChild( "meanerrorthreshold" );
		if( xe != null )
		{
			Double tmp = DoubleFunctions.instance().parseDouble( xe.getText() );
			if( tmp != null )
				result = tmp;
		}

		return( result );
	}

	protected ChessFigurePattern loadPattern( String type, XmlElement patternXe,
												ChessFigurePatternSet ps )
	{
		ChessFigurePattern result = new ChessFigurePattern();

		XmlElement nameXe = getMandatoryChild( patternXe, "name", childNotPresentErrorMessage("Pattern", "name" ) );
		String name = nameXe.getText();

		result.init(name, type, ps);

		Double meanErrorThreshold = loadMeanErrorThreshold( patternXe ); 
		result.setMeanErrorThreshold(meanErrorThreshold);

		Set<String> okFens = loadFens( getMandatoryChild( patternXe, "ok", childNotPresentErrorMessage("Pattern", "ok fens" ) ) );
		result.setFensOk( okFens );

		Set<String> nokFens = loadFens( getMandatoryChild( patternXe, "nok", childNotPresentErrorMessage("Pattern", "nok fens" ) ) );
		result.setFensNok( nokFens );

		return( result );
	}

	protected Set<String> loadFens( XmlElement fensXe )
	{
		Set<String> result = new HashSet<>();

		XmlElementList xel = fensXe.getChildrenByName( "fen" );
		if( xel != null )
		{
			for( XmlElement fenXe: xel.getCollection() )
			{
				result.add( fenXe.getText() );
			}
		}

		return( result );
	}

	@Override
	protected void registerInternationalizedStrings()
	{
		super.registerInternationalizedStrings();

		registerInternationalString(CONF_XML_WITHOUT_CHESSPATTERNSET, "Xml without chesspatternset" );
	}
}
