package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.Tree;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public interface TypeTree extends Tree {
   void accept(TypeTreeVisitor<?> var1);
}
