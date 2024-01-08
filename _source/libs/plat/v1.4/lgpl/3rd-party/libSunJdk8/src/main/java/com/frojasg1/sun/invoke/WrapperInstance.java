package com.frojasg1.sun.invoke;

import java.lang.invoke.MethodHandle;

public interface WrapperInstance {
   MethodHandle getWrapperInstanceTarget();

   Class<?> getWrapperInstanceType();
}
