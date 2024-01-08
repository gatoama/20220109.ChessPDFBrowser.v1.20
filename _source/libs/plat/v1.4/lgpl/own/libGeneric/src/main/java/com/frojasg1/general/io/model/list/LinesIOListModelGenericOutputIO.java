/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io.model.list;

import com.frojasg1.general.io.*;
import com.frojasg1.general.io.model.GenericIOModelContext;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	MMO - Model for output (to be writen to disk, it can accept more general types, to be able to translate)
	MCO - Model Context for output
	MMI - Model for input (it is the most particular, as it reads from disk exactly the type expected)
	MCI - Model Context for input
*/
public interface LinesIOListModelGenericOutputIO<RRO> {

	public void write( GenericIOModelContext<? extends IOListModelBase<? extends RRO>> modelContext, String fileName ) throws IOException;
	public boolean erase( String fileName );

	public void write( GenericIOModelContext<? extends IOListModelBase<? extends RRO>> modelContext, OutputStream os ) throws IOException;
}
