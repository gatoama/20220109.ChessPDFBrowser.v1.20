package com.frojasg1.sun.reflect.generics.visitor;

import com.frojasg1.sun.reflect.generics.tree.ClassSignature;
import com.frojasg1.sun.reflect.generics.tree.MethodTypeSignature;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public interface Visitor<T> extends TypeTreeVisitor<T> {
   void visitClassSignature(ClassSignature var1);

   void visitMethodTypeSignature(MethodTypeSignature var1);
}
