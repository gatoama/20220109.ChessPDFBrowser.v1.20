package com.frojasg1.sun.reflect.generics.scope;

import java.lang.reflect.TypeVariable;

public interface Scope {
   TypeVariable<?> lookup(String var1);
}
