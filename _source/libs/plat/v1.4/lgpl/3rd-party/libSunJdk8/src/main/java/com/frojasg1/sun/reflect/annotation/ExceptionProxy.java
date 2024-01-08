package com.frojasg1.sun.reflect.annotation;

import java.io.Serializable;

public abstract class ExceptionProxy implements Serializable {
   public ExceptionProxy() {
   }

   protected abstract RuntimeException generateException();
}
