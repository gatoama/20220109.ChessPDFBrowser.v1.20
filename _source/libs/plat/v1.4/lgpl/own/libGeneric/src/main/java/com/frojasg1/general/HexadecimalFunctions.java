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

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;

public class HexadecimalFunctions
{
	public static final String GLOBAL_CONF_FILE_NAME = "HexadecimalFunctions.properties";

	public static final String CONF_HEXADECIMAL_DIGIT_OUT_OF_RANGE = "HEXADECIMAL_DIGIT_OUT_OF_RANGE";
	public static final String CONF_ODD_NUMBER_OF_HEXADECIMAL_DIGITS = "ODD_NUMBER_OF_HEXADECIMAL_DIGITS";
	public static final String CONF_BUFFER_NOT_EQUAL_TO_4_IN_INT_FUNCTION = "BUFFER_NOT_EQUAL_TO_4_IN_FUNCTION";
	public static final String CONF_BUFFER_NOT_EQUAL_TO_8_IN_LONG_FUNCTION = "BUFFER_NOT_EQUAL_TO_8_IN_LONG_FUNCTION";

	protected byte[] a_countOnes;

	protected static HexadecimalFunctions _instance;


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

		instance().fillCountOnes();
	}

	public static void changeInstance( HexadecimalFunctions inst )
	{
		_instance = inst;
	}

	public static HexadecimalFunctions instance()
	{
		if( _instance == null )
			_instance = new HexadecimalFunctions();
		return( _instance );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_HEXADECIMAL_DIGIT_OUT_OF_RANGE, "Hexadecimal digit out of range" );
		registerInternationalString(CONF_ODD_NUMBER_OF_HEXADECIMAL_DIGITS, "Odd number of hexadecimal digits" );
		registerInternationalString(CONF_BUFFER_NOT_EQUAL_TO_4_IN_INT_FUNCTION, "Buffer length not equal to 4 in M_getIntegerFromBuffer function" );
		registerInternationalString(CONF_BUFFER_NOT_EQUAL_TO_8_IN_LONG_FUNCTION, "Buffer length not equal to 8 in M_getLongFromBuffer function" );
	}

	protected void fillCountOnes()
	{
		a_countOnes = new byte[256];
		for( int ii=0; ii<256; ii++ )
		{
			byte count = 0;
			int wei = 0x80;
			for( int jj=0; jj<8; jj++ )
			{
				if( (ii & wei)>0 )	count++;
				wei = wei >> 1;
			}
			a_countOnes[ii]=count;
		}
	}

	public int M_convertHexDigitInValue( char hexDigit ) throws GeneralException
	{
		int result = Character.digit( hexDigit, 16 );
		if( result == -1 )
			throw( new GeneralException( getInternationalString( CONF_HEXADECIMAL_DIGIT_OUT_OF_RANGE ) ) );
		return( result );
	}

	public byte[] M_convertHexadecimalStringToByteArray( String hexStr ) throws GeneralException
	{
		byte[] result = null;
		if( hexStr.length() % 2 != 0 )
			throw( new GeneralException( getInternationalString( CONF_ODD_NUMBER_OF_HEXADECIMAL_DIGITS ) ) );

		int numberOfBytes = hexStr.length()/2;
		result = new byte[numberOfBytes];
		int jj=0;
		for( int ii=0; ii<numberOfBytes; ii++ )
		{
			result[ii] = ( new Integer( M_convertHexDigitInValue( hexStr.charAt( jj ) ) * 16 +
										M_convertHexDigitInValue( hexStr.charAt( jj + 1) ) ) ).byteValue();
			jj=jj+2;
		}

		return( result );
	}
	
	public String M_convertByteArrayToHexadecimalString( byte[] buffer )
	{
		StringBuilder result = new StringBuilder( buffer.length * 2 );
		int jj=0;
		for( int ii=0; ii<buffer.length; ii++ )
		{
			String hexOfByte = "0" + Integer.toHexString( buffer[ii] );
			String hexOfByte_of_two_chars = hexOfByte.substring( hexOfByte.length()-2, hexOfByte.length());
			result.replace( jj, jj+2, hexOfByte_of_two_chars );
			jj=jj+2;
		}

		return( new String( result ) );
	}
	
	public int M_countOnes( byte value )
	{
		int index = value + (value<0?256:0);
		return( a_countOnes[index] );
	}
	
	public int M_countOnes( byte[] ba, int pos )
	{
		int result = 0;
		for( int ii=pos; ii<ba.length; ii++ )
		{
			int index = ba[ii] + (ba[ii]<0?256:0);
			result = result + a_countOnes[index];
		}
		return( result );
	}
	
	public int M_getIntegerFromBuffer( byte [] buffer ) throws GeneralException
	{
		if( (buffer == null) || (buffer.length != 4) )
			throw( new GeneralException( getInternationalString( CONF_BUFFER_NOT_EQUAL_TO_4_IN_INT_FUNCTION ) ) );

		int tmp0 = buffer[0] & 0xFF;
		int tmp1 = buffer[1] & 0xFF;
		int tmp2 = buffer[2] & 0xFF;
		int tmp3 = buffer[3] & 0xFF;

		int result = ( tmp0 | (tmp1<<8) | (tmp2<<16) | (tmp3<<24) );
		return( result );
	}

	public byte[] M_getBufferFromInteger( int value )
	{
		byte[] result = new byte[4];
		result[0] = ( new Integer( value & 0xFF ) ).byteValue();
		result[1] = ( new Integer( (value>>>8) & 0xFF ) ).byteValue();
		result[2] = ( new Integer( (value>>>16) & 0xFF ) ).byteValue();
		result[3] = ( new Integer( (value>>>24) & 0xFF ) ).byteValue();
		return( result );
	}

	public long M_getLongFromBuffer( byte[] buffer ) throws GeneralException
	{
		if( (buffer == null) || (buffer.length != 8) )
			throw( new GeneralException( getInternationalString( CONF_BUFFER_NOT_EQUAL_TO_8_IN_LONG_FUNCTION ) ) );

		long tmp0 = buffer[0] & 0xFF;
		long tmp1 = buffer[1] & 0xFF;
		long tmp2 = buffer[2] & 0xFF;
		long tmp3 = buffer[3] & 0xFF;
		long tmp4 = buffer[4] & 0xFF;
		long tmp5 = buffer[5] & 0xFF;
		long tmp6 = buffer[6] & 0xFF;
		long tmp7 = buffer[7] & 0xFF;

		long result = ( tmp0 | (tmp1<<8) | (tmp2<<16) | (tmp3<<24) |
						(tmp4<<32) | (tmp5<<40) | (tmp6<<48) | (tmp7<<56) );

		return( result );
	}
	
	public byte[] M_getBufferFromLong( long value )
	{
		byte[] result = new byte[8];
		result[0] = ( new Long( value & 0xFF ) ).byteValue();
		result[1] = ( new Long( (value>>>8) & 0xFF ) ).byteValue();
		result[2] = ( new Long( (value>>>16) & 0xFF ) ).byteValue();
		result[3] = ( new Long( (value>>>24) & 0xFF ) ).byteValue();
		result[4] = ( new Long( (value>>>32) & 0xFF ) ).byteValue();
		result[5] = ( new Long( (value>>>40) & 0xFF ) ).byteValue();
		result[6] = ( new Long( (value>>>48) & 0xFF ) ).byteValue();
		result[7] = ( new Long( (value>>>56) & 0xFF ) ).byteValue();
		return( result );
	}

	public String M_getLogFromBuffer( byte[] buffer )
	{
		StringBuilder log = new StringBuilder();

		int numBytesLogged = 0;
		StringBuilder lineCodes = new StringBuilder();
		StringBuilder lineAsciis = new StringBuilder();
		while( (buffer != null) && (numBytesLogged < buffer.length) )
		{
			String offsetStr = String.format( "%06d: ", numBytesLogged );
			lineCodes.setLength(0);
			lineAsciis.setLength(0);
			for( int ii=0; ii<16; ii++ )
			{
				String code = "   ";
				String ascii = ".";
				if(numBytesLogged < buffer.length)
				{
					code = String.format( "%02X ", buffer[numBytesLogged] );
					if( (buffer[numBytesLogged] >= 32) &&
						(buffer[numBytesLogged] <127 ) )
					{
						ascii = String.valueOf( (char) buffer[numBytesLogged] );
					}
				}
				lineCodes.append(code);
				lineAsciis.append(ascii);
				numBytesLogged++;
			}
//			String line = offsetStr + lineCodes + lineAsciis + "\n";
			log.append(offsetStr).append(lineCodes).append(lineAsciis).append("\n");
		}
		return( log.toString() );
	}

	public int[] M_getHistogram( byte[] buffer )
	{
		int[] result = new int[256];
		
		for( int ii=0; ii<256; ii++ ) result[ii]=0;
		
		for( int ii=0; ii<buffer.length; ii++ )
		{
			result[ (buffer[ii]>=0?buffer[ii]:buffer[ii]+256) ]++;
		}
		
		return( result );
	}

	public String M_IntArrayToString( int[] array )
	{
		String result = "";
		for( int ii=0; ii<array.length; ii++ )
		{
			result = result + String.format( "int[%d]=%d\n", ii, array[ii] );
		}
		return( result );
	}
	
	public byte[] M_joinByteArrays( byte[] array1, byte[] array2 )
	{
		byte[] result = null;

		if( array1 == null )		result = array2;
		else if( array2 == null )	result = array1;
		else
		{
			result = new byte[array1.length + array2.length ];
			
			System.arraycopy(array1, 0, result, 0, array1.length );
			System.arraycopy(array2, 0, result, array1.length, array2.length );
		}
		return( result );
	}

	
	public static void main( String[] args )
	{
		byte[] buffer = new byte[256];
		for( int ii=0; ii<256; ii++ ) buffer[ii]=( new Integer(ii) ).byteValue();
		String hexString = instance().M_convertByteArrayToHexadecimalString( buffer );
		System.out.println( hexString );
		System.out.println( "longitud: " + hexString.length() );
	}

	protected static void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	protected static String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}
}
