/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io;

import com.frojasg1.general.io.model.GenericIOModelContext;
import com.frojasg1.general.collection.ModifiedStatus;

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
public interface GenericIO<MM extends ModifiedStatus, MC extends GenericIOModelContext<MM>>
	extends GenericInputIO<MM, MC>, GenericOutputIO<MM, MC> {
	
}
