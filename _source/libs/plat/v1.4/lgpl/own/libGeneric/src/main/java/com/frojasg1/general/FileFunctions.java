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
package com.frojasg1.general;

import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.files.FileStoreHacks;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Usuario
 */
public class FileFunctions
{
/*
	This Class is used during the start of the application, when it is nothing initialized, and so it
	cannot make use of multilanguage.

	public static final String GLOBAL_CONF_FILE_NAME = "FileFunctions.properties";

	public static final String CONF_ERROR = "ERROR";
	public static final String CONF_DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY = "DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY";
	public static final String CONF_NEW_DIRECTORY_EXISTED_PREVIOUSLY = "NEW_DIRECTORY_EXISTED_PREVIOUSLY";
*/
	private static final Logger LOGGER = LoggerFactory.getLogger(FileFunctions.class);

	public static String  DIR_SEPARATOR = System.getProperty( "file.separator" );

	protected static FileFunctions _instance;

// https://stackoverflow.com/questions/10324228/regex-to-validate-a-filename/10324265
	protected Pattern VALID_FILENAME_PATTERN = Pattern.compile( "^[^\\*\\?&%\\s/\\\\:]+$" );

	protected Pattern GET_DISK_UNIT = Pattern.compile( "^([A-Za-z]:)\\\\" );

/*
	protected static InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																											GenericConstants.sa_PROPERTIES_PATH_IN_JAR );

	static
	{
		try
		{
			registerInternationalizedStrings();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
*/
	public static void changeInstance( FileFunctions inst )
	{
		_instance = inst;
	}

	public static FileFunctions instance()
	{
		if( _instance == null )
			_instance = new FileFunctions();
		return( _instance );
	}

	public FileFunctions()
	{
		_instance = this;
	}
/*
	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_ERROR, "Error" );
		registerInternationalString(CONF_DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY, "does not exist or it is not a directory." );
		registerInternationalString(CONF_NEW_DIRECTORY_EXISTED_PREVIOUSLY, "new directory existed previously" );
	}
*/
	public String getBaseName( String longFileName )
	{
		String result = null;

		if( longFileName != null )
		{
			File file = new File( longFileName );
			result = file.getName();
		}

		return( result );
	}

	public String getAbsoluteDirName( String longFileName )
	{
		String result = null;

		if( longFileName != null )
		{
			File file = new File( longFileName );
			File parent = file.getParentFile();

			if( parent != null )
				result = parent.getAbsolutePath();
		}

		return( result );
	}

	public String getAbsolutePathName( String longFileName )
	{
		String result = null;

		if( longFileName != null )
		{
			File file = new File( longFileName );
			result = file.getAbsolutePath();
		}

		return( result );
	}

	public String getDirName( String longFileName )
	{
		String result = null;

		if( longFileName != null )
		{
			File file = new File( longFileName );
			File parent = file.getParentFile();

			if( parent != null )
				result = parent.getPath();
		}

		return( result );
	}

	public String getExtension( File file )
    {
        String fileName = file.toString();
        return( getExtension( fileName ) );
    }

    public String getExtension( String fileName )
    {
        String extension = "";
        
        String longFileName = fileName;
        String shortFileName = null;
        int pos = longFileName.lastIndexOf( File.separator );
        while( (pos>0) && (pos==(longFileName.length()-1) ))
        {
            longFileName = longFileName.substring( 0, longFileName.length()-1 );
            pos = longFileName.lastIndexOf( File.separator );
        }

        if( (pos >= 0) && (pos<(longFileName.length()-1) ) )
        {
            shortFileName = longFileName.substring( pos + 1 );
        }
        else if( pos < 0 )
        {
            shortFileName = longFileName;
        }

        pos = shortFileName.lastIndexOf( "." );
        if( (pos>0) && (pos<(shortFileName.length()-1) ))
        {
            extension = shortFileName.substring( pos+1, shortFileName.length() );
        }

/*
        System.out.println( "File:" + fileName +
                            "shortFileName: " + shortFileName +
                            ", extension: " + extension +
                            ", separator: " + File.separator );
*/
        return( extension );
    }
	
    public String cutOffExtension( String fileName )
    {
		String extension = getExtension( fileName );
		int lenExt = (extension.length() > 0 ? extension.length() + 1 : 0 );    // para el punto
		String outputFileName = fileName.substring(0, fileName.length()-lenExt );
//		System.out.println( "fileName: " + fileName + "without extension: " + outputFileName );

		return( outputFileName );
    }

	public String convertFolderSeparator( String compoundFolder )
	{
		String result = convertFolderSeparatorGen(compoundFolder, "\\/", DIR_SEPARATOR + DIR_SEPARATOR );
		return( result );
	}

	public String normalizeFolderSeparator( String compoundFolder )
	{
		String result = convertFolderSeparatorGen(compoundFolder, "\\/", "//" );
		return( result );
	}

	public String convertFolderSeparatorGen( String compoundFolder, String fromSeparators, String toSeparators )
	{
		String result = StringFunctions.instance().replaceSetOfChars(compoundFolder, fromSeparators, toSeparators );
		return( result );
	}

	public boolean isDirectory( String dirname )
	{
		boolean result = false;
		if( dirname != null )
		{
			File dir = new File( dirname );
			result = dir.exists() && dir.isDirectory();
		}

		return( result );
	}

	public long getFileLength( String fileName )
	{
		long result = -1;
		if( fileName != null )
		{
			File file = new File( fileName );
			result = file.length();
		}

		return( result );
	}

	public boolean isFile( String fileName )
	{
		boolean result = false;
		if( fileName != null )
		{
			File file = new File( fileName );
			result = file.exists() && file.isFile();
		}

		return( result );
	}

	public void copyFile( String sourceFileName, String destFileName ) throws IOException
	{
		boolean overwrite = true;
		copyFile( sourceFileName, destFileName, overwrite );
	}

/*
	// http://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
	public void copyFile( String sourceFileName, String destFileName, boolean overwrite ) throws IOException
	{
		File sourceFile = new File( sourceFileName );
		File destFile = new File( destFileName );
		
		if(!destFile.exists())
		{
			destFile.createNewFile();
		}
		else if( !overwrite )
		{
			return;
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally {
			if(source != null) {
				source.close();
			}
			if(destination != null) {
				destination.close();
			}
		}
	}
*/
	
	public boolean copyFile( String sourceFileName, String destFileName, boolean overwrite ) throws IOException
	{
		boolean result = false;

		File destFile = new File( destFileName );
		
		if(!destFile.exists())
		{
			destFile.createNewFile();
		}
		else if( !overwrite )
		{
			return( result );
		}

		FileInputStream source = null;

		try {
			source = new FileInputStream(sourceFileName);
			ResourceFunctions.instance().copyInputStreamToFile(source, destFileName);
			result = true;
		}
		finally {
			if(source != null) {
				source.close();
			}
		}

		return( result );
	}

	public void copyDirectoryRecursive( String oldDir, String newDir,
												boolean onlyForNonExistingNewDir,
												boolean overwrite ) throws IOException
	{
		File oDir = new File( oldDir );
		if( !oDir.exists() || !oDir.isDirectory() )
			throw( new RuntimeException( String.format( "%s: %s %s",
					"Error",
					oldDir,
					"does not exist or is not a directory"
														)
										)
				);
/*			throw( new RuntimeException( String.format( "%s: %s %s",
					getInternationalString( CONF_ERROR ),
					oldDir,
					getInternationalString( CONF_DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY )
														)
										)
				);
*/
		File nDir = new File( newDir );
		if( nDir.exists() && onlyForNonExistingNewDir )
			throw( new RuntimeException( String.format( "%s: %s",
					"Error",
					"new directory existed previously"
														)
										)
				);
/*			throw( new RuntimeException( String.format( "%s: %s",
					getInternationalString( CONF_ERROR ),
					getInternationalString( CONF_NEW_DIRECTORY_EXISTED_PREVIOUSLY )
														)
										)
				);
*/
		nDir.mkdirs();
		File files[] = oDir.listFiles();
		for( int ii=0; ii<files.length; ii++ )
		{
			if( files[ii].isFile() )
				copyFile( files[ii].getPath(), newDir + DIR_SEPARATOR + files[ii].getName(), overwrite );
			else if( files[ii].isDirectory() )
				copyDirectoryRecursive( files[ii].getPath(), newDir + DIR_SEPARATOR + files[ii].getName(),
										onlyForNonExistingNewDir, overwrite );
		}
	}

	public void copyDirectoryRecursive( String oldDir, String newDir ) throws IOException
	{
		boolean onlyForNonExistingNewDir = true;
		boolean overwrite = false;
		copyDirectoryRecursive( oldDir, newDir,	onlyForNonExistingNewDir, overwrite );
	}

	public void eraseDirCompletely( String dir ) throws IOException
	{
		File oDir = new File( dir );
		if( !oDir.exists() || !oDir.isDirectory() )
			throw( new RuntimeException( String.format( "%s: %s %s",
					"Error",
					dir,
					"does not exist or is not a directory"
														)
										)
				);
/*			throw( new RuntimeException( String.format( "%s: %s %s",
					getInternationalString( CONF_ERROR ),
					dir,
					getInternationalString( CONF_DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY )
														)
										)
				);
*/
		File files[] = oDir.listFiles();
		for( int ii=0; ii<files.length; ii++ )
		{
			if( files[ii].isFile() )
			{
				  files[ii].delete();
			}
			else if( files[ii].isDirectory() )
			{
				eraseDirCompletely( files[ii].getPath() );
			}
		}
		
		oDir.delete();
	}
	
	public void eraseRecursive( String dir, String fileToErase ) throws IOException
	{
		File oDir = new File( dir );
		if( !oDir.exists() || !oDir.isDirectory() )
			throw( new RuntimeException( String.format( "%s: %s %s",
					"Error",
					dir,
					"does not exist or is not a directory"
														)
										)
				);
/*			throw( new RuntimeException( String.format( "%s: %s %s",
					getInternationalString( CONF_ERROR ),
					dir,
					getInternationalString( CONF_DOES_NOT_EXIST_OR_IS_NOT_A_DIRECTORY )
														)
										)
				);
*/
		File files[] = oDir.listFiles();
		for( int ii=0; ii<files.length; ii++ )
		{
			if( files[ii].isFile() && files[ii].getName().equals( fileToErase ) )
			{
				  files[ii].delete();
			}
			else if( files[ii].isDirectory() )
			{
				eraseRecursive( files[ii].getPath(), fileToErase );
			}
		}
	}

	public void eraseRecursiveFileRegex( String dir, String fileRegexToErase ) throws IOException
	{
		File oDir = new File( dir );
		if( !oDir.exists() || !oDir.isDirectory() )
			throw( new RuntimeException( "Error: " + dir + " does not exist or it is not a directory." ) );

		File files[] = oDir.listFiles();
		for( int ii=0; ii<files.length; ii++ )
		{
			if( files[ii].isFile() && files[ii].getName().matches( fileRegexToErase ) )
			{
				  files[ii].delete();
			}
			else if( files[ii].isDirectory() )
			{
				eraseRecursiveFileRegex( files[ii].getPath(), fileRegexToErase );
			}
		}
	}

	public String getTextContentsOfAFile( String fileName, String charsetName ) throws IOException
	{
		String result = null;

		File file = new File( fileName );
		if( file.exists() && file.isFile() )		// read the file as binary
		{
			byte[] fileData = new byte[(int) file.length()];

			DataInputStream dis = null;
			FileInputStream fis = null;
			try
			{
				fis = new FileInputStream( file );
				dis = new DataInputStream( fis );
				dis.readFully(fileData);
				dis.close();
			}
			catch( IOException ioe )
			{
				throw( ioe );
			}
			finally
			{
				if( dis != null )
					dis.close();
				else if( fis != null )
					fis.close();
			}

			result = new String( fileData, charsetName );	// convert binary data into String with a particular Charset
		}

		return( result );
	}

	public FileInputStream getFileInputStream( String fileName )
	{
		FileInputStream result = null;
		
		try
		{
			result = new FileInputStream( fileName );
		}
		catch( Exception ex )
		{ }
		
		return( result );
	}

	public FileOutputStream getFileOutputStream( String fileName )
	{
		FileOutputStream result = null;
		
		try
		{
			result = new FileOutputStream( fileName );
		}
		catch( Exception ex )
		{ }

		return( result );
	}

	public File getFileRoot_old( File file )
	{
		File result = null;

		File[] fileRoots = File.listRoots();
		
		String fileName = file.getAbsolutePath();
		for( int ii=0; ii<fileRoots.length; ii++ )
		{
			if( StringFunctions.instance().stringStartsWith( fileName,
															fileRoots[ii].getAbsolutePath() ) )
			{
				if( ( result == null ) ||
					( result.getAbsolutePath().length() < fileRoots[ii].getAbsolutePath().length() ) )
				{
					result = fileRoots[ii];
				}
			}
		}

		return( result );
	}

	public String getFileRoot( File file )
	{
		String result = null;

		Iterator<FileStore> it = FileSystems.getDefault().getFileStores().iterator();

		int ii=0;
		String fileName = file.getAbsolutePath();
		while( it.hasNext() )
		{
			FileStore fs = it.next();

			Path fsRoot = FileStoreHacks.getPath(fs);
			String fsRootName = fsRoot.toString();

//			System.out.println( "Root[" + ii + "] : " + fsRootName );
			if( StringFunctions.instance().stringStartsWith( fileName, fsRootName ) )
			{
				if( ( result == null ) ||
					( result.length() < fsRootName.length() ) )
				{
					result = fsRootName;
				}
			}
			ii++;
		}

		return( result );
	}

	public Properties loadPropertiesFromDisk( String fileName )
	{
		Properties result = null;

		if( isFile( fileName ) )
		{
			result = new Properties( );

			try( FileInputStream fis = getFileInputStream(fileName) )
			{
				result.load( fis );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
				result = null;
			}
		}

		return( result );
	}

	public void savePropertiesInFile( Properties prop, String fileName ) throws IOException
	{
		if( ( prop != null ) && ( fileName != null ) )
		{
			try( FileOutputStream fos = getFileOutputStream(fileName) )
			{
				prop.store( fos, null );
			}
		}
	}

	public boolean createFolder( String dirName )
	{
		boolean result = false;
		File dir = new File( dirName );
		if( ! dir.exists() )
		{
			result = dir.mkdirs();
		}

		return( result );
	}

	// https://stackoverflow.com/questions/21541455/java-7-how-to-check-if-the-os-is-posix-compliant
	public boolean isPosix()
	{
		return( FileSystems.getDefault().supportedFileAttributeViews().contains("posix") );
	}
	
	public String loadTextFileContent( String fileName )
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public String appendSuffixToFileNameBeforeLastExtension( String fileName, String suffix )
	{
		String result = null;
		if( fileName != null )
		{
			String fileNameWithoutExtension = FileFunctions.instance().cutOffExtension(fileName);
			String extension = FileFunctions.instance().getExtension(fileName);

			result = fileNameWithoutExtension + suffix;

			if( !StringFunctions.instance().isEmpty( extension ) )
				result += "." + extension;
		}

		return( result );
	}

	public String addSeparatorToDirName( String dirName )
	{
		String result = null;

		if( dirName != null )
		{
			result = dirName;
			if( ! StringFunctions.instance().isEmpty( dirName ) )
				result += File.separator;
		}

		return( result );
	}

	public boolean isValidFileName( String fileName )
	{
		return( !StringFunctions.instance().isEmpty(fileName) &&
				VALID_FILENAME_PATTERN.matcher( fileName ).matches() );
	}

	public List<String> readTextLinesOfFile( String fileName, Charset charset ) throws IOException
	{
		return( Files.readAllLines(new File(fileName).toPath(), charset ) );
	}

	public String addExtension( String fileName, String extension )
	{
		String result = fileName;
		if( ! StringFunctions.instance().isEmpty( result ) )
		{
			String existingExtension = FileFunctions.instance().getExtension( result );
			if( extension.compareToIgnoreCase( existingExtension ) != 0 )
				result = result + "." + extension;
		}

		return( result );
	}

	public boolean delete( String fileName )
	{
		boolean result = false;
		if( fileName != null )
		{
			File file = new File( fileName );
			result = file.delete();
		}

		return( result );
	}

	public boolean hasExtension( String fileName, String extension )
	{
		String fileExtension = getExtension( fileName );

		return( extension.equalsIgnoreCase( fileExtension ) );
	}
/*
	protected static void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	protected static String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}
*/

	protected String getDiskUnit( File file )
	{
		String result = null;
		if( file != null )
		{
			Matcher matcher = GET_DISK_UNIT.matcher( file.getAbsolutePath() );
			if( matcher.find() )
				result = matcher.group(1);
		}
		return( result );
	}

	public String getLongFileName( String longControlFileName, String targetFileName )
	{
		String result = null;
		try
		{
			File folder = new File(longControlFileName).getParentFile();
			String diskUnit = getDiskUnit(new File(targetFileName) );

			if( diskUnit != null )
				result = targetFileName;
			else if( targetFileName.startsWith( "/" ) || targetFileName.startsWith( "\\" ) )
			{
				diskUnit = getDiskUnit(folder);
				if( diskUnit != null )
					result = diskUnit + File.separator + targetFileName;
				else
					result = targetFileName;
			}
			else
			{
				result = folder.getAbsolutePath() + File.separator + targetFileName;
			}
		}
		catch( Exception ex )
		{
			LOGGER.warn( "Error getting long file name: controlFile: '{}', targetFile: '{}'",
				longControlFileName, targetFileName, ex );
		}

		return( result );
	}

	protected boolean match( Pattern pattern, String text )
	{
		return( ( pattern == null ) || pattern.matcher(text).find() );
	}

	public File[] filterFilesAtFolder( File dir, Pattern pattern )
	{
		return( dir.listFiles( f -> ( f.isFile() && match( pattern, f.getName()) ) ) );
	}

	public String formatFileSize( long sizeInBytes, Locale locale )
	{
		String result = null;
		String[] prefixes = new String[] { //"Yb", "Zb", 
											"EB", "PB", "TB", "GB", "MB", "kB", "B" };
		// yotta (10^24)
		// zetta (10^21)
		// exa (10^18)
		// peta (10^15)
		// tera (10^12)
		// giga (10^9)
		// mega (10^6)
		// kilo (10^3)

		result = "0 b";
		long init = 1000000000000000000L;
		for( long multiple = init, ii=0; ii<prefixes.length; ii++, multiple /= 1000 )
			if( sizeInBytes >= multiple )
			{
				result = String.format( locale, "%.02f %s",
					( (double) sizeInBytes ) / multiple,
					prefixes[(int)ii] );
				break;
			}

		return( result );
	}

	public String forceExtension( String fileName, String extension )
	{
		String result = fileName;
		if( ! hasExtension(fileName, extension) )
			result = cutOffExtension( fileName ) + "." + extension;

		return( result );
	}

	protected boolean equalsIgnoreCase( String str1, String str2 )
	{
		return( StringFunctions.instance().equalsIgnoreCase( str1, str2 ) );
	}

	public boolean isAnyExtension( String fileName, String ... extensions )
	{
		String extension = getExtension( fileName );

		return( Arrays.stream(extensions).anyMatch( ext -> equalsIgnoreCase( extension, ext ) ) );
	}

	public boolean isAnyExtension( File file, String ... extensions )
	{
		boolean result = false;
		if( file != null )
			result = isAnyExtension( file.getName(), extensions );

		return( result );
	}
}
