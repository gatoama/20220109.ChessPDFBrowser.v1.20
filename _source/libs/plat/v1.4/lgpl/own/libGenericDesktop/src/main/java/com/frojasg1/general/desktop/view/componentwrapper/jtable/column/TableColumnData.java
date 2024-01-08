/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.column;

import com.frojasg1.general.codec.Pojo;
import com.frojasg1.general.codec.impl.SelfStringCodecBase;
import static com.frojasg1.general.number.IntegerFunctions.CONF_ERROR;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TableColumnData<RR> extends SelfStringCodecBase implements Pojo
{

	protected String _label;
	protected int _width = -1;
	protected boolean _isInitial = false;
	protected boolean _canBeRemoved = false;

	protected Function<String, String> _internationalStringFunction;

	protected BiFunction<Integer, RR, String> _cellValueGetter;

	protected Comparator<RR> _comparator;

	// default constructor. It is needed.
	public TableColumnData()
	{
		this( null, null, null );
	}

	public TableColumnData( Function<String, String> internationalStringFunction,
							BiFunction<Integer, RR, String> cellValueGetter,
							Comparator<RR> comparator)
	{
		_internationalStringFunction = internationalStringFunction;
		_cellValueGetter = cellValueGetter;
		_comparator = comparator;
	}

	public boolean isInitial() {
		return _isInitial;
	}

	public  TableColumnData<RR> setIsInitial(boolean _isInitial) {
		this._isInitial = _isInitial;

		return( this );
	}

	public boolean canBeRemoved() {
		return _canBeRemoved;
	}

	public  TableColumnData<RR> setCanBeRemoved(boolean value) {
		this._canBeRemoved = value;

		return( this );
	}

	public Function<String, String> getInternationalStringFunction() {
		return _internationalStringFunction;
	}

	public BiFunction<Integer, RR, String> getCellValueGetter() {
		return _cellValueGetter;
	}

	public String getCellValue( Integer index, RR record )
	{
		String result = null;
		try
		{
			result = getCellValueGetter().apply(index, record);
		}
		catch( Exception ex )
		{
			result = getInternationalString( CONF_ERROR );
		}
		
		return( result );
	}

	public String getLabel() {
		return _label;
	}

	public  TableColumnData<RR> setLabel(String _label) {
		this._label = _label;

		return( this );
	}

	public int getWidth() {
		return _width;
	}

	public  TableColumnData<RR> setWidth(int _width) {
		this._width = _width;

		return( this );
	}

	public Comparator<RR> getComparator() {
		return _comparator;
	}

	@Override
	public String toString() {
		return getInternationalString( getLabel() );
	}

	protected String getInternationalString( String label )
	{
		return( getInternationalStringFunction().apply( label ) );
	}
}
