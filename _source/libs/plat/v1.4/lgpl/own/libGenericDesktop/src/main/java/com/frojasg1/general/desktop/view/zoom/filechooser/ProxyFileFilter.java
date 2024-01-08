/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.zoom.filechooser;

import com.frojasg1.general.desktop.generic.dialogs.impl.StaticDesktopDialogsWrapper;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ProxyFileFilter extends FileFilter
{
		protected FileFilter originalFilter;
		public ProxyFileFilter( FileFilter filter )
		{
			this.originalFilter = filter;
		}

		@Override
		public boolean accept(File f)
		{
			return( originalFilter.accept(f) );
		}

		@Override
		public String getDescription()
		{
			return( originalFilter.getDescription() );
		}

		@Override
		public String toString()
		{
			return getDescription();
		}

		protected FileFilter getAncestor( Object fileFilter )
		{
			FileFilter result = null;
			if( fileFilter instanceof FileFilter )
			{
				result = (FileFilter) fileFilter;
				while( result instanceof ProxyFileFilter )
					result = ( (ProxyFileFilter) result ).originalFilter;
			}

			return( result );
		}

		@Override
		public boolean equals( Object that )
		{
			return getAncestor(this).equals( getAncestor(that) );
		}
}
