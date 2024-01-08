/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io;

import com.frojasg1.general.StreamFunctions;
import com.frojasg1.general.io.model.GenericIOModelContext;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.structures.Pair;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.frojasg1.general.collection.ModifiedStatus;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	MM - Model
	MC - Model Context
*/
public abstract class GenericIOBaseParent<MM extends ModifiedStatus, MC extends GenericIOModelContext<MM>>
	implements GenericInputIO<MM, MC>
{
	protected LinesToModelTranslator<MM> _linesToModelTranslator;

	public GenericIOBaseParent( LinesToModelTranslator<MM> linesToModelTranslator )
	{
		_linesToModelTranslator = linesToModelTranslator;
	}

	public LinesToModelTranslator<MM> getLinesToModelTranslator() {
		return _linesToModelTranslator;
	}

	public void setLinesToModelTranslator(LinesToModelTranslator<MM> _linesToModelTranslator) {
		this._linesToModelTranslator = _linesToModelTranslator;
	}

	@Override
	public MC read(String fileName) throws IOException {
		return read( fileName, new FileInputStream(fileName) );
	}

	protected Pair<String, List<String>> readLines( InputStream is )
	{
		Pair<String, String> charsetNameAndText =
			StreamFunctions.instance().readCharsetNameAndTextFromInputStreamException( is );
		String[] lines = StringFunctions.instance().getLines( charsetNameAndText.getValue() );
		List<String> linesList = null;
		if( lines != null )
			linesList = Arrays.stream(lines).collect( Collectors.toList() );

		Pair<String, List<String>> result = new Pair<>(charsetNameAndText.getKey(), linesList);

		return( result );
	}

	@Override
	public MC read(InputStream is) throws IOException {
		return( read( null, is ) );
	}

	public MC read(String fileName, InputStream is) throws IOException {
		Pair<String, List<String>> charsetAndText = readLines( is );
		List<String> lines = charsetAndText.getValue();

		String charsetName = charsetAndText.getKey();
		MC result = createModelContext( fileName, charsetName, lines );

		return( result );
	}

	public abstract MC createEmptyModelContext();

	protected MM createModel( List<String> lines )
	{
		MM result = null;
		LinesToModelTranslator<MM> translator = getLinesToModelTranslator();
		if( translator != null )
			result = translator.linesToModel(lines);

		return( result );
	}

	protected MC createModelContext( String fileName, String charsetName, List<String>lines )
	{
		MC result = createEmptyModelContext();
		result.setCharsetName(charsetName);
		result.setFileName( fileName );

		MM model = createModel(lines);
		result.setModel(model);

		return( result );
	}
}
