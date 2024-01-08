/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.column;

import com.frojasg1.general.comparators.ComparatorOfAttributes;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.swing.JTable;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class RecordToTableCellStringsBase<RR> {

	protected Function<String, String> _internationalStringFunction;
	protected JTable _table;

	protected Map<String, TableColumnData<RR>> _map;

	public RecordToTableCellStringsBase( JTable table, Function<String, String> internationalStringFunction )
	{
		_table = table;
		_internationalStringFunction = internationalStringFunction;
	}

	public Map<String, TableColumnData<RR>> fillColumnDataMap(Map<String, TableColumnData<RR>> map)
	{
		_map = fillColumnDataMapChild( map );

		return( _map );
	}

	public JTable getTable() {
		return _table;
	}

	public abstract Map<String, TableColumnData<RR>> fillColumnDataMapChild(Map<String, TableColumnData<RR>> result);

	public boolean matches( RR record, String filter, List<TableColumnData<RR>> columnDataList )
	{
		return( defaultMatches( record, filter, columnDataList ) );
	}

	protected boolean defaultMatches( RR record, String filter,
									List<TableColumnData<RR>> columnDataList )
	{
		boolean result = false;
		for( TableColumnData<RR> tcd: columnDataList )
		{
			result = defaultMatches( record, tcd, filter );
			if( result )
				break;
		}

		return( result );
	}

	protected boolean defaultMatches( RR record, TableColumnData<RR> tcd, String filter )
	{
		boolean result = false;

		if( record != null )
			result = defaultMatches( getCellValue( record, tcd ), filter );

		return( result );
	}

	protected String getCellValue( RR record, TableColumnData<RR> tcd )
	{
		String result = null;
		if( ( record != null ) && ( tcd != null ) )
			result = tcd.getCellValue(-1, record);

		return( result );
	}
/*
	protected String getCellValue( RR record, BiFunction<Integer, RR, String> getter )
	{
		String result = null;
		if( ( record != null ) && ( getter != null ) )
			result = getter.apply(-1, record);

		return( result );
	}
*/
	protected boolean defaultMatches( String value, String filter )
	{
		boolean result = false;
		if( ( value != null ) && ( filter != null ) )
			result = toLowerCase( value ).contains( toLowerCase( filter ) );

		return( result );
	}

	protected String toLowerCase( String str )
	{
		String result = null;
		if( str != null )
			result = str.toLowerCase( getOutputLocale() );

		return( result );
	}


	// every getter, produces a class:   C extends Comparable<C>
	protected Comparator<RR> createGenericComparator( Function<RR, ?> ... getters )
	{
		ComparatorOfAttributes<RR> result = new ComparatorOfAttributes<>();
		for( Function getter: getters )
			result.add(getter);

		return( result );
	}

	protected TableColumnData<RR> createTableColumnData( String label,
													BiFunction<Integer, RR, String> cellValueGetter,
													Comparator<RR> comparator )
	{
		TableColumnData<RR> result = new TableColumnData<>(_internationalStringFunction,
															cellValueGetter,
															comparator);
		result.setLabel(label);

		return( result );
	}

	protected String getInternationalString( String label )
	{
		String result = label;
		if( ( _internationalStringFunction != null ) && ( label != null ) )
			result = _internationalStringFunction.apply( label );

		return( result );
	}

	protected void put( Map<String, TableColumnData<RR>> map, String label,
						BiFunction<Integer, RR, String> cellValueGetter,
						boolean isInitial, boolean canBeRemoved,
						Comparator<RR> ascendingComparator )
	{
		map.put(label,
			createTableColumnData(label, cellValueGetter, ascendingComparator)
			.setIsInitial(isInitial)
			.setCanBeRemoved(canBeRemoved)
		);
	}

	public Locale getOutputLocale()
	{
		return( FrameworkComponentFunctions.instance().getOutputLocale( getTable() ) );
	}
}
