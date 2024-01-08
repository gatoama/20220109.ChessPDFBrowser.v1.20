/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io;

import com.frojasg1.general.io.model.GenericIOModelContextBase;
import com.frojasg1.general.collection.ModifiedStatus;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	MMO - Model for output (to be writen to disk, it can accept more general types, to be able to translate)
	MMI - Model for input (it is the most particular, as it reads from disk exactly the type expected)
*/
public class DefaultGenericIO<MM extends ModifiedStatus>
	extends GenericIOBase<MM, GenericIOModelContextBase<MM>>
{

	public DefaultGenericIO( LinesAndModelTranslator<MM, MM> linesModelTranslator )
	{
		super( linesModelTranslator );
	}

	@Override
	public GenericIOModelContextBase<MM> createEmptyModelContext() {
		return( new GenericIOModelContextBase<MM>() );
	}
}
