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
package com.frojasg1.general.document.formatter.xml;

import com.frojasg1.general.document.formatter.ExternalTextFormatter;
import com.frojasg1.general.document.formatted.FormatForText;
import com.frojasg1.general.document.formatted.FormattedString;
import com.frojasg1.general.document.formatted.text.FormattedText;
import com.frojasg1.general.xml.XmlElement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ComposedXmlFormatterBase extends XmlOutputFormatterBase
{
	protected Collection<XmlLocationFormatterData> _formattersCol = null;
	protected Map<String, ExternalTextFormatter> _textFormaterForStyleMap = null;

	@Override
	public FormattedText formatXml(String typeOfFormat, XmlElement elem)
	{
		FormattedText result =  createEmptyFormattedText();

		if( (elem != null) && ( _formattersCol != null ) )
		{
			result.add( getOwnFormattedText( typeOfFormat ) );

			Iterator<XmlLocationFormatterData> it = _formattersCol.iterator();
			while( it.hasNext() )
			{
				XmlLocationFormatterData item = it.next();

				XmlElement tmpElem = elem.getChildFromComposedLocation( item.getComposedLocation() );
				if( tmpElem != null )
				{
//					FormattedText resultElem = item.getXmlFormatter().formatXml(tmpElem);
					FormattedText resultElem = formatChild( typeOfFormat, tmpElem, item.getXmlFormatter() );

					addElemToResult( typeOfFormat, result, resultElem );
//					resultElem = refactor( resultElem );

//					result.add( resultElem );

////					String composedLocation = item.getComposedLocation();
////					result.append( getFormattedStringAfterPartialFormat( composedLocation ) );
				}
			}
		}

		return( result );
	}

	protected void init()
	{
		_formattersCol = createCollectionOfXmlLocationFormatterData();
	}

	protected abstract Map<String, String> getTranslationMapForParagraphFormatter();
	protected abstract Map<String, ExternalTextFormatter> getTextFormatterForStyleMap();
	protected abstract Collection<XmlLocationFormatterData> fillCollectionOfFormatters();
	protected abstract FormattedString getFormattedStringAfterPartialFormat( String composedLocation );

	protected abstract FormattedText getOwnFormattedText( String typeOfFormat );

	protected abstract FormattedText formatChild( String typeOfFormat, XmlElement elem, XmlOutputFormatter formatter );

	protected abstract void addElemToResult( String typeOfFormat, FormattedText result, FormattedText childFt );

/*
	protected SingleXmlFormatterBase createParagraphFormatter()
	{
		SingleParagraphFormatterImp result = new SingleParagraphFormatterImp();
		result.init();

		result.setTranslationStyleMap( getTranslationMapForParagraphFormatter() );

		return( result );
	}
*/

	protected FormattedText refactor( FormattedText inputFt )
	{
		FormattedString fsResult = null;

		if( (_textFormaterForStyleMap!=null) && ( inputFt != null ) &&
			( inputFt.getFormattedString() != null ) )
		{
			Collection<FormatForText> outputFormats = createCollectionOfFormatForText();

			Iterator<FormatForText> it = inputFt.getFormattedString().getFormatCol().iterator();
//			int offset = 0;
			while( it.hasNext() )
			{
				FormatForText elem = it.next();

				ExternalTextFormatter textFormatter = _textFormaterForStyleMap.get( elem.getStyleName() );
				if( textFormatter != null )
				{
					Collection<FormatForText> tmpfftCol = textFormatter.formatText( elem.getText() );

					int offset = elem.getStart();
					addAllFormatsForText( outputFormats, tmpfftCol, offset );
				}
				else
				{
					outputFormats.add( elem );
				}

//				offset += elem.getLength();
			}

			fsResult = createFormattedString(inputFt.getFormattedString().getString(), outputFormats );

//			if( result == null )
//				result = createEmptyFormattedString();

			inputFt.setFormattedString(fsResult);
		}

		return( inputFt );
	}

	protected void addAllFormatsForText( Collection<FormatForText> col, Collection<FormatForText> colToAdd, int offset )
	{
		if( colToAdd != null )
		{
			Iterator< FormatForText > it = colToAdd.iterator();
			while( it.hasNext() )
			{
				FormatForText item = it.next();

				addFormatForText( col, item, offset );
			}
		}
	}

	protected void addFormatForText( Collection<FormatForText> col, FormatForText elem, int offset )
	{
		if( ( col != null ) && ( elem != null ) )
		{
			String translatedStyle = translateStyleName( elem.getStyleName() );
			if( elem.getStyleName() != translatedStyle )
				elem = createFormatForText( elem.getText(), elem.getStart() + offset, translatedStyle );

			col.add( elem );
		}
	}

}
