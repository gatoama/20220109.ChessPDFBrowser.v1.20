/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.io.model.list;

import com.frojasg1.general.io.LinesToModelTranslator;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */

/*
	RRO - Record for output ( it is more generic than RRI. To make it possible translation between Record implementations)
	RRI - Record for input ( it is derived from RRO. It is more particular, as we read exactly the type of file that produces the expected Record implementation type)
	MMI - Model for input (it is a list of Records of type RRI)
*/
public interface LinesIOListModelTranslator<RRO,
											RRI extends RRO, MMI extends IOListModelBase<RRI>>
	extends LinesToModelTranslator<MMI>
{

	public List<String> modelToLines( IOListModelBase<? extends RRO> model );
}
