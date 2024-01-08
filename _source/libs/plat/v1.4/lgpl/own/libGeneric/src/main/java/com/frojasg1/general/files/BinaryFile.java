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
package com.frojasg1.general.files;

import java.nio.ByteBuffer;

/**
 *
 * @author Usuario
 */
public interface BinaryFile
{
	public String getFileName();
	
	public void openForReading( String fileName ) throws BinaryFileException;
	public void openForReadingAndWriting( String fileName, boolean truncateExisting ) throws BinaryFileException;
	public void openForReadingAndWriting_SYNC( String fileName, boolean truncateExisting ) throws BinaryFileException;
	public void openForReadingAndWriting_DSYNC( String fileName, boolean truncateExisting ) throws BinaryFileException;

	public int write( ByteBuffer bb ) throws BinaryFileException;
	public int write( ByteBuffer bb, long position ) throws BinaryFileException;

	void writeText( String text ) throws BinaryFileException;
	void writeLine( String text ) throws BinaryFileException;

	public int read( ByteBuffer bb ) throws BinaryFileException;
	public int read( ByteBuffer bb, long position ) throws BinaryFileException;

	public boolean isOpen();
	public void close() throws BinaryFileException;
}
