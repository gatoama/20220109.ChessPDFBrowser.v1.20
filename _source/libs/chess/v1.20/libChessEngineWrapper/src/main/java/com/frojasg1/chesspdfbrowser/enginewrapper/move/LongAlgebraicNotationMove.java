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
package com.frojasg1.chesspdfbrowser.enginewrapper.move;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.enginewrapper.constants.LibConstants;
import com.frojasg1.general.dialogs.filefilter.GenericFileFilter;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LongAlgebraicNotationMove implements InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "LongAlgebraicNotationMove.properties";
	protected static final String CONF_INVALID_LENGTH_OF_MOVE = "INVALID_LENGTH_OF_MOVE";
	protected static final String CONF_PROMOTION_PIECE_NOT_VALID = "PROMOTION_PIECE_NOT_VALID";
	protected static final String CONF_COORDINATE_BOUNDS_NOT_VALID = "COORDINATE_BOUNDS_NOT_VALID";

	protected static InternationalizedStringConfImp _internationalizedStringConf = null;

	protected Map<Integer, GenericFileFilter> _map;
	protected Map<Integer, Supplier<GenericFileFilter>> _mapOfBuilders;

	static
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	protected String _string = null;
	protected int _fromColumn = 0;
	protected int _fromRow = 0;
	protected int _toColumn = 0;
	protected int _toRow = 0;

	protected Character _promotionPieceCode = null;

	protected boolean _isEmpty = false;

	public void init( String string )
	{
		_string = string;

		parse( string );
	}

	public boolean isEmpty()
	{
		return( _isEmpty );
	}

	protected boolean moveStringIsEmpty( String string )
	{
		return( ( string == null ) || ( string.equals( "(none)" ) || ( string.equals( "NULL" ) ) ) );
	}

	protected void parse( String string )
	{
		_isEmpty = moveStringIsEmpty( string );

		if( ! isEmpty() )
		{
			if( (string.length() < 4) || (string.length() > 5 ) )
				throw( new RuntimeException( getInternationalString( CONF_INVALID_LENGTH_OF_MOVE ) ) );

			_fromColumn = calculateIndex( string.charAt(0), 'a' );
			_fromRow = calculateIndex( string.charAt(1), '1' );
			_toColumn = calculateIndex( string.charAt(2), 'a' );
			_toRow = calculateIndex( string.charAt(3), '1' );

			if( string.length() == 5 )
				_promotionPieceCode = string.charAt( 4 );

			checkValues();
		}
	}

	protected int calculateIndex( char ch, char initialChar )
	{
		return( (int) ( ch - initialChar + 1 ) );
	}

	protected void checkValues()
	{
		checkBoundaries( _fromColumn, 1, 8 );
		checkBoundaries( _fromRow, 1, 8 );
		checkBoundaries( _toColumn, 1, 8 );
		checkBoundaries( _toRow, 1, 8 );

		checkPromotion();
	}

	protected void checkPromotion()
	{
		if( ( _promotionPieceCode != null ) &&
			( 'q' != _promotionPieceCode ) &&
			( 'r' != _promotionPieceCode ) &&
			( 'b' != _promotionPieceCode ) &&
			( 'n' != _promotionPieceCode ) )
		{
			// TODO: translate
			throw( new RuntimeException( createCustomInternationalString( CONF_PROMOTION_PIECE_NOT_VALID,
																		_promotionPieceCode ) ) );
		}
	}

	protected void checkBoundaries( int value, int lowBound, int highBound )
	{
		if( ( value < lowBound ) || ( value > highBound ) )
			throw( new RuntimeException( createCustomInternationalString( CONF_COORDINATE_BOUNDS_NOT_VALID,
																			_string ) ) );
	}

	public String getString() {
		return _string;
	}

	public int getFromColumn() {
		return _fromColumn;
	}

	public int getFromRow() {
		return _fromRow;
	}

	public int getToColumn() {
		return _toColumn;
	}

	public int getToRow() {
		return _toRow;
	}

	public Character getPromotionPieceCode() {
		return _promotionPieceCode;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + Objects.hashCode(this._string);
		hash = 29 * hash + this._fromColumn;
		hash = 29 * hash + this._fromRow;
		hash = 29 * hash + this._toColumn;
		hash = 29 * hash + this._toRow;
		hash = 29 * hash + Objects.hashCode(this._promotionPieceCode);
		hash = 29 * hash + (this._isEmpty ? 1 : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final LongAlgebraicNotationMove other = (LongAlgebraicNotationMove) obj;
		if (this._fromColumn != other._fromColumn) {
			return false;
		}
		if (this._fromRow != other._fromRow) {
			return false;
		}
		if (this._toColumn != other._toColumn) {
			return false;
		}
		if (this._toRow != other._toRow) {
			return false;
		}
		if (this._isEmpty != other._isEmpty) {
			return false;
		}
		if (!Objects.equals(this._string, other._string)) {
			return false;
		}
		if (!Objects.equals(this._promotionPieceCode, other._promotionPieceCode)) {
			return false;
		}
		return true;
	}


	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	@Override
	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalStringStatic(CONF_INVALID_LENGTH_OF_MOVE, "Length of move string not valid" );
		registerInternationalStringStatic(CONF_PROMOTION_PIECE_NOT_VALID, "Promotion piece not valid: $1" );
		registerInternationalStringStatic(CONF_COORDINATE_BOUNDS_NOT_VALID, "Coordinate bounds not valid. Move string: $1" );
	}

	protected static void registerInternationalStringStatic(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}
}
