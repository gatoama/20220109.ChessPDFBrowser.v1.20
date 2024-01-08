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

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.GenericConstants;
import java.lang.reflect.Field;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
// try https://stackoverflow.com/questions/15656139/get-partition-and-volume-information
public class FileStoreHacks
{
	public static final String GLOBAL_CONF_FILE_NAME = "FileStoreHacks.properties";

	public static final String CONF_FILE_FIELD_NOT_FOUND = "FILE_FIELD_NOT_FOUND";
	public static final String CONF_DENIED_ACCESS = "DENIED_ACCESS";


	protected static InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																											GenericConstants.sa_PROPERTIES_PATH_IN_JAR );

	/**
     * Stores the known hacks.
     */
    private static final Map<Class<? extends FileStore>, Hacks> hacksMap;
    static {
//        ImmutableMap.Builder<Class<? extends FileStore>, Hacks> builder =
//            ImmutableMap.builder();
		try
		{
			registerInternationalizedStrings();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		hacksMap = new HashMap<>();
        try {
            Class<? extends FileStore> fileStoreClass =
                Class.forName("sun.nio.fs.WindowsFileStore")
                    .asSubclass(FileStore.class);
            hacksMap.put(fileStoreClass, new WindowsFileStoreHacks(fileStoreClass));
        } catch (ClassNotFoundException e) {
            // Probably not running on Windows.
        }

		
		UnixFileStoreHacks ufsh = null;
        try {
            Class<? extends FileStore> fileStoreClass =
                Class.forName("sun.nio.fs.UnixFileStore")
                    .asSubclass(FileStore.class);
			
			ufsh = new UnixFileStoreHacks(fileStoreClass);

            hacksMap.put(fileStoreClass, new UnixFileStoreHacks(fileStoreClass));
        } catch (ClassNotFoundException e) {
            // Probably not running on UNIX.
        }


        try {
            Class<? extends FileStore> fileStoreClass =
                Class.forName("sun.nio.fs.LinuxFileStore")
                    .asSubclass(FileStore.class);

			if( ufsh != null )
				hacksMap.put( fileStoreClass, ufsh );
        } catch (ClassNotFoundException e) {
            // Probably not running on UNIX.
        }

//        hacksMap = builder.build();
    }

    private FileStoreHacks() {
    }

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_FILE_FIELD_NOT_FOUND, "file field not found" );
		registerInternationalString(CONF_DENIED_ACCESS, "Denied access" );
	}

    /**
     * Gets the path from a file store. For some reason, NIO2 only has a method
     * to go in the other direction.
     *
     * @param store the file store.
     * @return the path.
     */
    public static Path getPath(FileStore store) {
        Hacks hacks = hacksMap.get(store.getClass());
        if (hacks == null) {
            return null;
        } else {
            return hacks.getPath(store);
        }
    }

    private static interface Hacks {
        Path getPath(FileStore store);
    }

    private static class WindowsFileStoreHacks implements Hacks {
        private final Field field;

        public WindowsFileStoreHacks(Class<?> fileStoreClass) {
            try {
                field = fileStoreClass.getDeclaredField("root");
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException( getInternationalString( CONF_FILE_FIELD_NOT_FOUND ), e);
            }
        }

		@Override
        public Path getPath(FileStore store) {
            try {
                String root = (String) field.get(store);
                return FileSystems.getDefault().getPath(root);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException( getInternationalString( CONF_DENIED_ACCESS ), e);
            }
        }
    }
/*
    private static class UnixFileStoreHacks implements Hacks {
        private final Field field;

        private UnixFileStoreHacks(Class<?> fileStoreClass) {
            try {
                field = fileStoreClass.getDeclaredField("file");
                field.setAccessible(true);

				
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException("file field not found", e);
            }
        }

        @Override
        public Path getPath(FileStore store) {
            try {
                return (Path) field.get(store);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Denied access", e);
            }
        }
    }
*/
    private static class UnixFileStoreHacks implements Hacks {
        private Field fieldOfUnixFileStoreClass;
        private Field fieldOfUnixMountEntryClass;
		private Class<?> unixMountEntryClass;

        private UnixFileStoreHacks(Class<?> fileStoreClass) {
            try {
                fieldOfUnixFileStoreClass = fileStoreClass.getDeclaredField("entry");
                fieldOfUnixFileStoreClass.setAccessible(true);

				unixMountEntryClass = Class.forName("sun.nio.fs.UnixMountEntry");
				fieldOfUnixMountEntryClass = unixMountEntryClass.getDeclaredField( "dir" );
                fieldOfUnixMountEntryClass.setAccessible(true);
				
            } catch (NoSuchFieldException e) {
				e.printStackTrace();
                throw new IllegalStateException( getInternationalString( CONF_FILE_FIELD_NOT_FOUND ), e);
            } catch (ClassNotFoundException ex) {
				Logger.getLogger(FileStoreHacks.class.getName()).log(Level.SEVERE, null, ex);
			}
        }

        @Override
        public Path getPath(FileStore store) {
            try {
                Object unixMountEntry = fieldOfUnixFileStoreClass.get(store);

				byte[] dir = (byte[]) fieldOfUnixMountEntryClass.get( unixMountEntryClass.cast(unixMountEntry) );
				String dirName = new String( dir );
				Path result = FileSystems.getDefault().getPath(dirName);

				return( result );
			} catch (IllegalAccessException e) {
                throw new IllegalStateException( getInternationalString( CONF_DENIED_ACCESS ), e);
            }
        }
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
