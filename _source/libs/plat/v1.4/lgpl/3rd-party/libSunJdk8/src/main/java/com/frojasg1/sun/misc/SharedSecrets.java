package com.frojasg1.sun.misc;

import com.frojasg1.sun.misc.JavaAWTAccess;
import com.frojasg1.sun.misc.JavaIOAccess;
import com.frojasg1.sun.misc.JavaIOFileDescriptorAccess;
import com.frojasg1.sun.misc.JavaLangAccess;
import com.frojasg1.sun.misc.JavaLangRefAccess;
import com.frojasg1.sun.misc.JavaNetAccess;
import com.frojasg1.sun.misc.JavaNetHttpCookieAccess;
import com.frojasg1.sun.misc.JavaNioAccess;
import com.frojasg1.sun.misc.JavaOISAccess;
import com.frojasg1.sun.misc.JavaObjectInputStreamAccess;
import com.frojasg1.sun.misc.JavaSecurityAccess;
import com.frojasg1.sun.misc.JavaSecurityProtectionDomainAccess;
import com.frojasg1.sun.misc.JavaUtilJarAccess;
import com.frojasg1.sun.misc.JavaUtilZipFileAccess;
import com.frojasg1.sun.misc.JavaxCryptoSealedObjectAccess;
import com.frojasg1.sun.misc.Unsafe;

import java.io.Console;
import java.io.FileDescriptor;
import java.io.ObjectInputStream;
import java.net.HttpCookie;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;
import javax.crypto.SealedObject;

public class SharedSecrets {
   private static final com.frojasg1.sun.misc.Unsafe unsafe = Unsafe.getUnsafe();
   private static com.frojasg1.sun.misc.JavaUtilJarAccess javaUtilJarAccess;
   private static com.frojasg1.sun.misc.JavaLangAccess javaLangAccess;
   private static com.frojasg1.sun.misc.JavaLangRefAccess javaLangRefAccess;
   private static com.frojasg1.sun.misc.JavaIOAccess javaIOAccess;
   private static com.frojasg1.sun.misc.JavaNetAccess javaNetAccess;
   private static com.frojasg1.sun.misc.JavaNetHttpCookieAccess javaNetHttpCookieAccess;
   private static com.frojasg1.sun.misc.JavaNioAccess javaNioAccess;
   private static com.frojasg1.sun.misc.JavaIOFileDescriptorAccess javaIOFileDescriptorAccess;
   private static com.frojasg1.sun.misc.JavaSecurityProtectionDomainAccess javaSecurityProtectionDomainAccess;
   private static com.frojasg1.sun.misc.JavaSecurityAccess javaSecurityAccess;
   private static com.frojasg1.sun.misc.JavaUtilZipFileAccess javaUtilZipFileAccess;
   private static com.frojasg1.sun.misc.JavaAWTAccess javaAWTAccess;
   private static com.frojasg1.sun.misc.JavaOISAccess javaOISAccess;
   private static com.frojasg1.sun.misc.JavaxCryptoSealedObjectAccess javaxCryptoSealedObjectAccess;
   private static com.frojasg1.sun.misc.JavaObjectInputStreamAccess javaObjectInputStreamAccess;

   public SharedSecrets() {
   }

   public static com.frojasg1.sun.misc.JavaUtilJarAccess javaUtilJarAccess() {
      if (javaUtilJarAccess == null) {
         unsafe.ensureClassInitialized(JarFile.class);
      }

      return javaUtilJarAccess;
   }

   public static void setJavaUtilJarAccess(JavaUtilJarAccess var0) {
      javaUtilJarAccess = var0;
   }

   public static void setJavaLangAccess(com.frojasg1.sun.misc.JavaLangAccess var0) {
      javaLangAccess = var0;
   }

   public static JavaLangAccess getJavaLangAccess() {
      return javaLangAccess;
   }

   public static void setJavaLangRefAccess(com.frojasg1.sun.misc.JavaLangRefAccess var0) {
      javaLangRefAccess = var0;
   }

   public static JavaLangRefAccess getJavaLangRefAccess() {
      return javaLangRefAccess;
   }

   public static void setJavaNetAccess(com.frojasg1.sun.misc.JavaNetAccess var0) {
      javaNetAccess = var0;
   }

   public static JavaNetAccess getJavaNetAccess() {
      return javaNetAccess;
   }

   public static void setJavaNetHttpCookieAccess(com.frojasg1.sun.misc.JavaNetHttpCookieAccess var0) {
      javaNetHttpCookieAccess = var0;
   }

   public static JavaNetHttpCookieAccess getJavaNetHttpCookieAccess() {
      if (javaNetHttpCookieAccess == null) {
         unsafe.ensureClassInitialized(HttpCookie.class);
      }

      return javaNetHttpCookieAccess;
   }

   public static void setJavaNioAccess(com.frojasg1.sun.misc.JavaNioAccess var0) {
      javaNioAccess = var0;
   }

   public static JavaNioAccess getJavaNioAccess() {
      if (javaNioAccess == null) {
         unsafe.ensureClassInitialized(ByteOrder.class);
      }

      return javaNioAccess;
   }

   public static void setJavaIOAccess(com.frojasg1.sun.misc.JavaIOAccess var0) {
      javaIOAccess = var0;
   }

   public static JavaIOAccess getJavaIOAccess() {
      if (javaIOAccess == null) {
         unsafe.ensureClassInitialized(Console.class);
      }

      return javaIOAccess;
   }

   public static void setJavaIOFileDescriptorAccess(com.frojasg1.sun.misc.JavaIOFileDescriptorAccess var0) {
      javaIOFileDescriptorAccess = var0;
   }

   public static JavaIOFileDescriptorAccess getJavaIOFileDescriptorAccess() {
      if (javaIOFileDescriptorAccess == null) {
         unsafe.ensureClassInitialized(FileDescriptor.class);
      }

      return javaIOFileDescriptorAccess;
   }

   public static void setJavaOISAccess(com.frojasg1.sun.misc.JavaOISAccess var0) {
      javaOISAccess = var0;
   }

   public static JavaOISAccess getJavaOISAccess() {
      if (javaOISAccess == null) {
         unsafe.ensureClassInitialized(ObjectInputStream.class);
      }

      return javaOISAccess;
   }

   public static void setJavaSecurityProtectionDomainAccess(com.frojasg1.sun.misc.JavaSecurityProtectionDomainAccess var0) {
      javaSecurityProtectionDomainAccess = var0;
   }

   public static JavaSecurityProtectionDomainAccess getJavaSecurityProtectionDomainAccess() {
      if (javaSecurityProtectionDomainAccess == null) {
         unsafe.ensureClassInitialized(ProtectionDomain.class);
      }

      return javaSecurityProtectionDomainAccess;
   }

   public static void setJavaSecurityAccess(com.frojasg1.sun.misc.JavaSecurityAccess var0) {
      javaSecurityAccess = var0;
   }

   public static JavaSecurityAccess getJavaSecurityAccess() {
      if (javaSecurityAccess == null) {
         unsafe.ensureClassInitialized(AccessController.class);
      }

      return javaSecurityAccess;
   }

   public static com.frojasg1.sun.misc.JavaUtilZipFileAccess getJavaUtilZipFileAccess() {
      if (javaUtilZipFileAccess == null) {
         unsafe.ensureClassInitialized(ZipFile.class);
      }

      return javaUtilZipFileAccess;
   }

   public static void setJavaUtilZipFileAccess(JavaUtilZipFileAccess var0) {
      javaUtilZipFileAccess = var0;
   }

   public static void setJavaAWTAccess(com.frojasg1.sun.misc.JavaAWTAccess var0) {
      javaAWTAccess = var0;
   }

   public static JavaAWTAccess getJavaAWTAccess() {
      return javaAWTAccess == null ? null : javaAWTAccess;
   }

   public static com.frojasg1.sun.misc.JavaObjectInputStreamAccess getJavaObjectInputStreamAccess() {
      if (javaObjectInputStreamAccess == null) {
         unsafe.ensureClassInitialized(ObjectInputStream.class);
      }

      return javaObjectInputStreamAccess;
   }

   public static void setJavaObjectInputStreamAccess(JavaObjectInputStreamAccess var0) {
      javaObjectInputStreamAccess = var0;
   }

   public static void setJavaxCryptoSealedObjectAccess(com.frojasg1.sun.misc.JavaxCryptoSealedObjectAccess var0) {
      javaxCryptoSealedObjectAccess = var0;
   }

   public static JavaxCryptoSealedObjectAccess getJavaxCryptoSealedObjectAccess() {
      if (javaxCryptoSealedObjectAccess == null) {
         unsafe.ensureClassInitialized(SealedObject.class);
      }

      return javaxCryptoSealedObjectAccess;
   }
}
