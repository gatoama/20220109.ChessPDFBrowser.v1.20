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
package com.frojasg1.general.args;

import com.frojasg1.general.number.IntegerFunctions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ArgsExtractorBase {
    public static final String DEBUG = "-debug";

    protected Map<String, String> _map = new HashMap<>();
	protected String[] _args = null;

    public ArgsExtractorBase() {
    }

	public String[] getArgs() {
		return _args;
	}

	protected String getKey(String arg) {
        String result = null;

        if (arg != null) {
            int pos = arg.indexOf("=");

            result = arg;
            if ((pos >= 0) && (pos < arg.length())) {
                result = arg.substring(0, pos);
            }
        }
        return (result);
    }

    public String getValue(String arg) {
        String result = null;

        if (arg != null) {
            int pos = arg.indexOf("=");

            result = null;
            if ((pos >= 0) && (pos < arg.length())) {
                result = arg.substring(pos + 1, arg.length());
            }
        }
        return (result);
    }

	protected boolean isPresentWithoutValue( String paramName )
	{
		boolean result = false;

        if (paramName != null)
		{
			boolean isPresent = _map.containsKey( paramName );
			if( isPresent )
				result = ( _map.get(paramName) == null );
        }

        return (result);
	}

	protected void insertArgInMap(String arg) {
        if (arg != null) {
            String key = getKey(arg);
            String value = getValue(arg);

            _map.put(key, value);
        }
    }

    protected void insertArgsInMap(String[] args) {
        _map.clear();

        for (int ii = 0; ii < args.length; ii++) {
            insertArgInMap(args[ii]);
        }
    }

    public void process(String[] args) {
		_args = args;
        insertArgsInMap(args);
    }

    protected Integer processInteger(String paramName, Integer def) {
        Integer result = def;

       if (paramName != null) {
            String value = _map.get(paramName);
           if (value != null) {
                result = IntegerFunctions.parseInt( value );
            }
        }

        return (result);
    }

    protected boolean processBoolean(String paramName, boolean def) {
        boolean result = def;

        if (paramName != null) {
            String value = _map.get(paramName);
            if (value != null) {
                result = value.equalsIgnoreCase("yes");
            }
        }

        return (result);
    }

    protected String processString(String paramName, String def) {
        String result = def;

        if (paramName != null) {
            String value = _map.get(paramName);

            if (value != null) {
                result = value;
            }
        }

        return (result);
    }

    protected Collection<String> processList(String paramName, String separatorRegex ) {
        Collection<String> result = new ArrayList<>();

        if (paramName != null) {
            String value = _map.get(paramName);
            if (value != null) {
                String[] elemArray = value.split(separatorRegex);

                for (int ii = 0; ii < elemArray.length; ii++) {
                    result.add(elemArray[ii]);
                }
            }
        }

        return (result);
    }

    protected Collection<Integer> translateStrColToIntCol( Collection<String> col )
    {
        Collection<Integer> result = null;

        if( col != null )
        {
            result = new ArrayList<>();
        
            Iterator<String> it = col.iterator();
            while( it.hasNext() )
            {
                Integer tmpInt = Integer.parseInt( it.next() );
                result.add(tmpInt);
            }
        }
        return( result );
    }
}
