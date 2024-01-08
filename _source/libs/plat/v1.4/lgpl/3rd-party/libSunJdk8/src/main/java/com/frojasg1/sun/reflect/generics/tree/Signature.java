package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.FormalTypeParameter;
import com.frojasg1.sun.reflect.generics.tree.Tree;

public interface Signature extends Tree {
   FormalTypeParameter[] getFormalTypeParameters();
}
