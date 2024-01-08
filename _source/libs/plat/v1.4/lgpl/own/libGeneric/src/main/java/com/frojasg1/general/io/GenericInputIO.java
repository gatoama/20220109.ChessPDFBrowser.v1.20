/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io;

import com.frojasg1.general.io.model.GenericIOModelContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.frojasg1.general.collection.ModifiedStatus;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	MC - Model context
*/
public interface GenericInputIO<MM extends ModifiedStatus, MC extends GenericIOModelContext<MM>>
{
	public MC read( String fileName ) throws IOException;

	public MC read( InputStream is ) throws IOException;
}
