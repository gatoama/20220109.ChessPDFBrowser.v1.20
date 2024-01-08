/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io.model.list;

import com.frojasg1.general.collection.impl.ThreadSafeGenListWrapper;
import com.frojasg1.general.io.*;
import com.frojasg1.general.io.model.GenericIOModelContext;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	RRO - Record for ouptut. It can be a parent of RRI, to let write any implementation
	RRI - Record for input. The exact type.
	MMI - Model for input (it is the most particular, as it reads from disk exactly the type expected)
	MCI - Model Context for input
*/
public interface LinesIOListModelGenericIO<RRO,
											RRI extends RRO, MMI extends ThreadSafeGenListWrapper<RRI>, MCI extends GenericIOModelContext<MMI>>
	extends GenericInputIO<MMI, MCI>, LinesIOListModelGenericOutputIO<RRO>
{
}
