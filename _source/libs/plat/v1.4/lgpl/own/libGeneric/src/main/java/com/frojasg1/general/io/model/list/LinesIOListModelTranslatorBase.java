/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io.model.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.frojasg1.general.io.LinesAndModelTranslator;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	RRO - Record for output ( it is more generic than RRI. To make it possible translation between Record implementations)
	RRI - Record for input ( it is derived from RRO. It is more particular, as we read exactly the type of file that produces the expected Record implementation type)
	MMI - Model for input (it is a list of Records of type RRI)
*/
public abstract class LinesIOListModelTranslatorBase<RRO,
													RRI extends RRO, MMI extends IOListModelBase<RRI>>
	implements LinesIOListModelTranslator<RRO, RRI, MMI>
{
	protected LinesAndModelTranslator<RRO, RRI> _linesToRecordTranslator;

	public LinesIOListModelTranslatorBase( LinesAndModelTranslator<RRO, RRI> linesToRecordTranslator )
	{
		_linesToRecordTranslator = linesToRecordTranslator;
	}

	public LinesAndModelTranslator<RRO, RRI> getLinesToRecordTranslator() {
		return _linesToRecordTranslator;
	}

	public void setLinesToRecordTranslator(LinesAndModelTranslator<RRO, RRI> _linesToRecordTranslator) {
		this._linesToRecordTranslator = _linesToRecordTranslator;
	}

	protected Analyser createAnalyser( List<String> lines )
	{
		return( new Analyser(lines) );
	}

	@Override
	public MMI linesToModel(List<String> lines) {
		MMI result = createEmptyModel();
		
		Analyser ana = new Analyser( lines );
		ana.next();
		readHeader( ana, result );

		while( ana.getLine() != null )
		{
			RRI record = readRecord( ana, result );
			if( record != null )
				result.add( record );
		}

		readFooter( ana, result );

		result.setHasBeenModified(false);

		return( result );
	}

	protected abstract MMI createEmptyModel();

	protected abstract void readHeader( Analyser ana, MMI result );
	protected abstract void readFooter( Analyser ana, MMI result );

	protected RRI readRecord( Analyser ana, MMI result )
	{
		RRI record = null;

		while( ( ana.getLine() != null ) && ! isStartOfRecord(ana) )
			ana.next();

		if( isStartOfRecord(ana) )
		{
			ana.addCurrentLineToRecord();
			ana.next();

			while( ( ana.getLine() != null ) && ! isStartOfRecord(ana) )
			{
				ana.addCurrentLineToRecord();
				ana.next();
			}

			List<String> recordLinesList = ana.getAndClearCurrentRecordLines();
			if( ! recordLinesList.isEmpty() )
				record = linesToRecord( recordLinesList );
		}

		return( record );
	}

	protected abstract boolean isStartOfRecord( Analyser ana );

	protected RRI linesToRecord( List<String> lines )
	{
		return( getLinesToRecordTranslator().linesToModel( lines ) );
	}

	@Override
	public List<String> modelToLines( IOListModelBase<? extends RRO> model ) {
		List<String> result = new ArrayList<>();

		if( model != null )
		{
			addHeader( result );
			for( RRO record: model.getList() )
			{
				if( isValid(record) )
					result.addAll( recordToLines(record) );
			}

			addFooter( result );
		}

		return( result );
	}

	protected abstract boolean isValid( RRO record );

	protected abstract void addHeader( List<String> result );
	protected abstract void addFooter( List<String> result );

	protected List<String> recordToLines( RRO record )
	{
		return( getLinesToRecordTranslator().modelToLines(record) );
	}

	protected static class Analyser
	{
		protected List<String> _lines;
		protected Iterator<String> _iterator;

		protected String _line;

		protected List<String> _currentRecordLines = new ArrayList<>();

		public Analyser(List<String> lines) {
			this._lines = lines;
			_iterator = _lines.iterator();
		}

		public String getLine()
		{
			return( _line );
		}

		public String next()
		{
			if( _iterator.hasNext() )
				_line = _iterator.next();
			else
				_line = null;

			return( _line );
		}

		protected List<String> getCurrentRecordLines()
		{
			return( _currentRecordLines );
		}

		public void addCurrentLineToRecord()
		{
			getCurrentRecordLines().add( getLine() );
		}

		public List<String> getAndClearCurrentRecordLines()
		{
			List<String> result = new ArrayList<>( getCurrentRecordLines() );
			getCurrentRecordLines().clear();

			return( result );
		}
	}
}
