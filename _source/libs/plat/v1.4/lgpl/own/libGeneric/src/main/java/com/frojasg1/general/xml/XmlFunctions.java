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
package com.frojasg1.general.xml;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.xml.impl.XmlElementImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class XmlFunctions
{
	protected static final XmlElementMatcher ATTRIBUTE_VALUE_LIST_STRING_MATCHER = ( xmlElement, toMatchWith ) -> { return( xmlElement.attributesMatch((String) toMatchWith)); };
	protected static final XmlElementMatcher ATTRIBUTE_VALUE_MAP_MATCHER = ( xmlElement, toMatchWith ) -> { return( xmlElement.attributesMatch((Map<String,String>) toMatchWith)); };

    protected static XmlFunctions _instance = null;

    public static void changeInstance(XmlFunctions instance) {

		_instance = instance;

	}

    public static XmlFunctions instance() {
        if (_instance == null) {
            _instance = new XmlFunctions();
        }

        return (_instance);
    }

	public XmlElement getXmlTree( Document doc )
	{
		return( getXmlTree( doc.getDocumentElement() ) );
	}

	protected XmlElement getXmlTree( Element elem )
	{
		XmlElement result = createXmlElement( elem );

		// if it is not pure text ...
		if( result.getText() == null )
		{
			NodeList nl = JavaXmlFunctions.instance().getAllChildElements(elem);
			for( int ii=0; ii<nl.getLength(); ii++ )
				result.addChild( getXmlTree( (Element) nl.item(ii) ) );
		}

		return( result );
	}

	public XmlElement createXmlElement( String name )
	{
		return( new XmlElementImpl( name ) );
	}

	protected String getPureText( Element elem )
	{
		String result = null;

		NodeList nl = elem.getChildNodes();
		if( ( nl.getLength() == 1 ) && ( nl.item(0).getNodeType() == Element.TEXT_NODE ) )
			result = elem.getTextContent();

		return( result );
	}

	protected XmlElement createXmlElement( Element elem )
	{
		XmlElement result = createXmlElement( elem.getTagName() );

		NamedNodeMap nnm = elem.getAttributes();
		for( int ii=0; ii<nnm.getLength(); ii++ )
		{
			Node intNode = nnm.item(ii);
			result.putAttribute( intNode.getNodeName(), intNode.getTextContent() );
		}

		result.setText( getPureText( elem ) );

		return( result );
	}
/*
	protected XmlElement getChild( XmlElement elem, LevelElementToLookFor letlf )
	{
		XmlElement result = null;

		if( ( elem != null ) && ( letlf != null ) )
		{
			result = elem.getChild( letlf.getTagName(), letlf.getIndex() );
		}

		return( result );
	}

	public XmlElement getChild( XmlElement rootElem, Collection<LevelElementToLookFor> col )
	{
		XmlElement result = rootElem;

		Iterator<LevelElementToLookFor> it = col.iterator();
		while( ( result != null ) && it.hasNext() )
		{
			LevelElementToLookFor elem = it.next();
			result = getChild( result, elem );
		}

		return( result );
	}

	public XmlElementList getListOfComposedLocation( XmlElement rootElem, Collection<LevelElementToLookFor> col, String lastTag )
	{
		XmlElementList result = null;
		XmlElement parent = getChild( rootElem, col );
		if( parent != null )
		{
			result = parent.getChildrenByName(lastTag);
		}

		return( result );
	}
*/
	// format of composedTag:    tag1<att1=val1,...,attN=valN>[ind]. ... .tagN<att1=val1,...,attN=valN>[ind]
	public XmlElementList getListOfComposedLocation( XmlElement rootElem, String composedTag )
	{
		XmlElementList result = null;

		if( composedTag != null )
		{
			String[] tags = splitComposedTagInTags( composedTag );
			XmlElement elem = getElemOfComposedLocation( rootElem, tags );

			if( elem != null )
			{
				result = elem.getSelfAndSibilingItems();

				if( tags.length > 0 )
				{
					SingleElementToLookFor elemData = new SingleElementToLookFor( tags[ tags.length -1 ] );
					for( int ii=0; ii<result.getSize(); ii++ )
					{
						if( !result.getElementAt(ii).attributesMatch( elemData.getAttValPairList() ) )
						{
							result.removeElement(ii);
							ii--;
						}
					}
				}
			}
		}

		return( result );
	}

	public XmlElementList createNewXmlElementList( XmlElementList xmlElemList )
	{
		XmlElementList result = null;

		try
		{
			result = xmlElemList.getClass().getConstructor( String.class ).newInstance( xmlElemList.getName() );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			result = null;
		}

		return( result );
	}

	public XmlElementList copy( XmlElementList xmlElemList )
	{
		XmlElementList result = null;
		if( xmlElemList != null )
		{
			result = createNewXmlElementList( xmlElemList );
			for( int ii=0; ii<xmlElemList.getSize(); ii++ )
				result.addElement( xmlElemList.getElementAt(ii) );
		}

		return( result );
	}

	protected String[] splitComposedTagInTags( String composedTag )
	{
		ArrayList<String> list = new ArrayList<>();

		if( composedTag != null )
		{
			int prev = 0;
			while( ( prev != -1 ) && ( prev < composedTag.length() ) )
			{
				int pos = prev;
				pos = StringFunctions.instance().indexOfAnyChar(composedTag, "<.", pos );

				if( pos > -1 )
				{
					switch( composedTag.charAt( pos ) )
					{
						case '.':
							list.add( composedTag.substring( prev, pos ) );
						break;

						case '<':
						{
							pos = composedTag.indexOf( '>', pos );

							if( pos > -1 )
							{
								int pos2 = StringFunctions.instance().indexOfAnyChar(composedTag, ".[", pos );
								if( pos2 > -1 )
								{
									switch( composedTag.charAt( pos2 ) )
									{
										case '[': pos = composedTag.indexOf( ']', pos2 ); break;
									}
								}
								else
									pos = pos2;
							}
						}
						break;
					}
				}

				if( ( pos == -1 ) || ( pos >= ( composedTag.length() - 1 ) ) )
					list.add( composedTag.substring( prev ) );

				if( pos != -1 )
					prev = pos + 1;
				else
					prev = -1;
			}
		}

		return( list.toArray( new String[ list.size() ] ) );
	}

	// format of composedTag:    tag1<att1=val1,...,attN=valN>[ind]. ... .tagN<att1=val1,...,attN=valN>[ind]
	public XmlElement getElemOfComposedLocation( XmlElement rootElem, String composedTag )
	{
		XmlElement result = null;
		if( composedTag != null )
		{
			String[] tags = splitComposedTagInTags( composedTag );
			result = getElemOfComposedLocation( rootElem, tags );
		}

		return( result );
	}

	// format of composedTag:    tag1<att1=val1,...,attN=valN>[ind]. ... .tagN<att1=val1,...,attN=valN>[ind]
	protected XmlElement getElemOfComposedLocation( XmlElement rootElem, String[] tags )
	{
		XmlElement result = null;
		if( tags != null )
		{
			result = rootElem;
			for( int ii=0; ( result != null ) && ii<tags.length; ii++ )
			{
				result = getSingleElem( result, tags[ii] );
			}
		}

		return( result );
	}

	public SingleElementToLookFor createSingleElementToLookFor( String singleLocation )
	{
		return( new SingleElementToLookFor( singleLocation ) );
	}

	public SingleElementToLookFor createSingleElementToLookFor( String name, int index, String attValPairList )
	{
		return( new SingleElementToLookFor( name, index, attValPairList ) );
	}

	public XmlElement getSingleElem( XmlElement elem, String singleLocation )
	{
		XmlElement result = null;

		if( StringFunctions.instance().isEmpty(singleLocation) )
			result = elem;
		else
		{
			if( elem != null )
			{
				SingleElementToLookFor elemData = createSingleElementToLookFor( singleLocation );

				result = getSingleElem( elem, elemData.getTagName(),
										elemData.getIndex(),
										elemData.getAttValPairList() );
			}
		}

		return( result );
	}

	protected XmlElement getSingleElemInternal( XmlElement elem, String tagName, int index, XmlElementMatcher matcher, Object toMatchWith )
	{
		XmlElement result = null;

		if( elem != null )
		{
			XmlElementList elList = elem.getChildrenByName( tagName );

			if( elList != null )
			{
				int count = 0;
				Iterator<XmlElement> it = elList.getCollection().iterator();
				while( ( result == null ) && it.hasNext() )
				{
					XmlElement item = it.next();
					if( matcher.matches( item, toMatchWith ) )
					{
						count++;
						if( count == index )
							result = item;
					}
				}
			}
		}

		return( result );
	}

	public XmlElement getSingleElem( XmlElement elem, String tagName, int index, String attValPairList )
	{
		return( getSingleElemInternal( elem, tagName, index, ATTRIBUTE_VALUE_LIST_STRING_MATCHER, attValPairList ) );
	}

	public XmlElement getSingleElem( XmlElement elem, String tagName, int index, Map<String,String> attValPairMap )
	{
		return( getSingleElemInternal( elem, tagName, index, ATTRIBUTE_VALUE_MAP_MATCHER, attValPairMap ) );
	}

	public Document createDocument( XmlElement xmlElement )
	{
		Document result = null;
		
		if( xmlElement != null )
		{
			result = ExecutionFunctions.instance().safeFunctionExecution( () -> JavaXmlFunctions.instance().createEmptyDocument() );

			if( result == null )
				throw( new RuntimeException( "Error when creating empty xml document" ) );

			Node rootNode = createEmptyElement( result, xmlElement );
			result.appendChild(rootNode);
			traslateXmlElementIntoElementOfDocument( result, rootNode, xmlElement );
		}

		return( result );
	}

	protected Element createEmptyElement( Document doc, XmlElement xmlElement )
	{
		Element result = null;
		if( xmlElement != null )
		{
			result = JavaXmlFunctions.instance().createEmptyElement( doc, xmlElement.getName() );
			String textContent = xmlElement.getText();
			if( textContent != null )
				result.setTextContent(textContent);

			for( String attribName: xmlElement.getAttributeNames() )
				result.setAttribute(attribName, xmlElement.getAttributeValue(attribName));
		}
		return( result );
	}

	protected void traslateXmlElementIntoElementOfDocument( Document result, Node current,
															XmlElement xmlElement )
	{
		if( ( result != null ) && ( current != null ) & (xmlElement != null ) )
		{
			for( XmlElement childXmlElement: xmlElement.getAllChildren() )
			{
				Element childElement = createEmptyElement( result, childXmlElement );
				traslateXmlElementIntoElementOfDocument( result, childElement, childXmlElement );
				current.appendChild(childElement);
			}
		}
	}

	@FunctionalInterface
	protected interface XmlElementMatcher
	{
		public boolean matches( XmlElement elem, Object toMatchWith );
	}
}
