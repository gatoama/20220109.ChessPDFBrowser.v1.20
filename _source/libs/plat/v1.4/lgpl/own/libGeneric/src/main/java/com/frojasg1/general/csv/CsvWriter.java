
package com.frojasg1.general.csv;

import com.frojasg1.general.ExecutionFunctions;
import java.io.FileWriter;
import java.io.Writer;

/**
 *
 * @author fjavier.rojas
 */
public class CsvWriter {

	protected Writer _writer;

	protected String buildString( String separator, Object ... items )
	{
		StringBuilder sb = new StringBuilder();
		for( Object item: items )
			sb.append(item).append(separator);
		
		return( sb.toString() );
	}

	protected void writeln( String separator, Object ... items )
	{
		writeln( _writer, separator, items );
	}

	protected void writeln( Writer writer, String separator, Object ... items )
	{
		if( writer != null )
			ExecutionFunctions.instance().safeMethodExecution( () -> writer.write( buildString( separator, items ) + "\n" ) );
	}
	
	
/*
	protected void dump( Complex[] input, Complex[] result, int startIndex, int endIndex, int shiftIndex )
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> {
			String separator = ";";
			try( FileWriter writer = new FileWriter( "D:\\dump.csv" ) )
			{
				writeln( writer, separator, "Start index", "End index", "Shift index" );
				writeln( writer, separator, startIndex, endIndex, shiftIndex );
				writeln( writer, separator, "Index", "Input Value", "Output value" );
				for( int index = 0; index < input.length; index++ )
					writeln( writer, separator, index, input[index], result[index] );
			}
		} );
	}
*/

	protected Writer getWriter() {
		return _writer;
	}

	protected void setWriter(Writer _writer) {
		this._writer = _writer;
	}
}
