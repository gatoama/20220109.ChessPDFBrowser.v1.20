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
package com.frojasg1.general.xml.language;

import com.frojasg1.applications.common.configuration.application.ChangeLanguageClientInterface;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageServerInterface;
import com.frojasg1.general.language.file.LanguageFile;
import com.frojasg1.general.xml.JavaXmlFunctions;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.XmlFunctions;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LanguageXmlConfiguration implements ChangeLanguageClientInterface
{
	protected LanguageFile _languageFile = null;

	protected XmlElement _xmlElement = null;

	protected ChangeLanguageServerInterface _changeLanguageServer = null;

//	protected BaseApplicationConfigurationInterface _appConf = null;

/*
	public LanguageXmlConfiguration( BaseApplicationConfigurationInterface appConf,
							String singleXmlFileName,
							String languageFolderResourceName,
							String languageFolderDiskFileName )
	{
		_languageFile = createLanguageFile(singleXmlFileName,
											languageFolderResourceName,
											languageFolderDiskFileName );
	}
*/

	public void init( String singleXmlFileName,
						String languageFolderResourceName,
						String languageFolderDiskFileName )
	{
		_languageFile = createLanguageFile(singleXmlFileName,
											languageFolderResourceName,
											languageFolderDiskFileName );
	}

	protected LanguageFile createLanguageFile( String singleXmlFileName,
									String languageFolderResourceName,
									String languageFolderDiskFileName )
	{
		return( new LanguageFile( singleXmlFileName,
									languageFolderResourceName,
									languageFolderDiskFileName ) );
	}

	protected LanguageFile getLanguageFile()
	{
		return( _languageFile );
	}

	protected XmlElement loadDocument( String language ) throws IOException, ParserConfigurationException, SAXException
	{
		return( loadDocument( language, _languageFile.getSingleFileName() ) );
	}

	protected XmlElement loadDocumentCustomFileName( String longFileName ) throws IOException, ParserConfigurationException, SAXException
	{
		return( loadDocument( new FileInputStream( longFileName ) ) );
	}

	protected XmlElement loadDocument( String language, String singleFileName ) throws IOException, ParserConfigurationException, SAXException
	{
		return( loadDocument( _languageFile.getInputStream( language, singleFileName ) ) );
	}

	protected XmlElement loadDocument( InputStream is ) throws IOException, ParserConfigurationException, SAXException
	{
		XmlElement result = null;
		_xmlElement = null;
		try( InputStream is1 = is; )
		{
			Document doc = JavaXmlFunctions.instance().parseXml( is1 );
			result = XmlFunctions.instance().getXmlTree(doc);
		}
		_xmlElement = result;
		
		return( _xmlElement );
	}
/*
	public void init() throws IOException, ParserConfigurationException, SAXException
	{
//		registerToChangeLanguageAsObserver( _appConf );
		loadDocument( getLanguage() );
	}
*/

	protected void saveDocumentCustomFileName( XmlElement xmlElement, String fileName ) throws IOException, TransformerException
	{
		saveDocument( xmlElement, new FileWriter( fileName ) );
	}

	protected void saveDocument( XmlElement xmlElement, String language, String singleFileName ) throws IOException, TransformerException
	{
		saveDocument( xmlElement, _languageFile.getFileWriter( language, singleFileName ) );
	}

	protected void saveDocument( XmlElement xmlElement, String language ) throws IOException, TransformerException
	{
		saveDocument( xmlElement, _languageFile.getFileWriterOfLanguage( language ) );
	}

	protected void saveDocument( XmlElement xmlElement, Writer writer ) throws IOException, TransformerException
	{
		Document xmlDoc = XmlFunctions.instance().createDocument(xmlElement);

		JavaXmlFunctions.instance().writeDocument(xmlDoc, writer );
	}

	@Override
	public String getLanguage()
	{
		return( ( _changeLanguageServer == null ) ? null: _changeLanguageServer.getLanguage() );
	}

	@Override
	public void changeLanguage(String newLanguage) throws IOException, ParserConfigurationException, SAXException
	{
		loadDocument( newLanguage );
	}

	@Override
	public void unregisterFromChangeLanguageAsObserver()
	{
		if( _changeLanguageServer != null )
			_changeLanguageServer.unregisterChangeLanguageObserver(this);

		_changeLanguageServer = null;
	}

	@Override
	public void registerToChangeLanguageAsObserver(ChangeLanguageServerInterface conf)
	{
		unregisterFromChangeLanguageAsObserver();
		_changeLanguageServer = conf;
		if( _changeLanguageServer != null )
			_changeLanguageServer.registerChangeLanguageObserver(this);
	}

	public XmlElement getXmlElement()
	{
		return( _xmlElement );
	}

	public String getLongFileName( String language, String relativeFileName )
	{
		return( this.getLanguageFile().getLongFileName(language, relativeFileName) );
	}

	public String getLongFileName( String relativeFileName )
	{
		return( getLongFileName(null, relativeFileName) );
	}
}
