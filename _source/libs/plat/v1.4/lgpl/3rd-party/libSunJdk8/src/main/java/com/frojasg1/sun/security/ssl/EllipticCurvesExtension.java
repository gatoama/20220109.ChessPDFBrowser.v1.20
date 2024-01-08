package com.frojasg1.sun.security.ssl;

import java.io.IOException;
import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.SSLProtocolException;
import com.frojasg1.sun.security.action.GetPropertyAction;

final class EllipticCurvesExtension extends HelloExtension {
   private static final Debug debug = Debug.getInstance("ssl");
   private static final int ARBITRARY_PRIME = 65281;
   private static final int ARBITRARY_CHAR2 = 65282;
   private static final Map<String, Integer> oidToIdMap = new HashMap();
   private static final Map<Integer, String> idToOidMap = new HashMap();
   private static final Map<Integer, AlgorithmParameters> idToParams = new HashMap();
   private static final int[] supportedCurveIds;
   private final int[] curveIds;

   private static boolean isAvailableCurve(int var0) {
      String var1 = (String)idToOidMap.get(var0);
      if (var1 != null) {
         AlgorithmParameters var2 = null;

         try {
            var2 = JsseJce.getAlgorithmParameters("EC");
            var2.init(new ECGenParameterSpec(var1));
         } catch (Exception var4) {
            return false;
         }

         idToParams.put(var0, var2);
         return true;
      } else {
         return false;
      }
   }

   private EllipticCurvesExtension(int[] var1) {
      super(ExtensionType.EXT_ELLIPTIC_CURVES);
      this.curveIds = var1;
   }

   EllipticCurvesExtension(HandshakeInStream var1, int var2) throws IOException {
      super(ExtensionType.EXT_ELLIPTIC_CURVES);
      int var3 = var1.getInt16();
      if ((var2 & 1) == 0 && var3 + 2 == var2) {
         this.curveIds = new int[var3 >> 1];

         for(int var4 = 0; var4 < this.curveIds.length; ++var4) {
            this.curveIds[var4] = var1.getInt16();
         }

      } else {
         throw new SSLProtocolException("Invalid " + this.type + " extension");
      }
   }

   static int getActiveCurves(AlgorithmConstraints var0) {
      return getPreferredCurve(supportedCurveIds, var0);
   }

   static boolean hasActiveCurves(AlgorithmConstraints var0) {
      return getActiveCurves(var0) >= 0;
   }

   static EllipticCurvesExtension createExtension(AlgorithmConstraints var0) {
      ArrayList var1 = new ArrayList(supportedCurveIds.length);
      int[] var2 = supportedCurveIds;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         if (var0.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), "EC", (AlgorithmParameters)idToParams.get(var5))) {
            var1.add(var5);
         }
      }

      if (var1.isEmpty()) {
         return null;
      } else {
         var2 = new int[var1.size()];
         var3 = 0;

         Integer var7;
         for(Iterator var6 = var1.iterator(); var6.hasNext(); var2[var3++] = var7) {
            var7 = (Integer)var6.next();
         }

         return new EllipticCurvesExtension(var2);
      }
   }

   int getPreferredCurve(AlgorithmConstraints var1) {
      return getPreferredCurve(this.curveIds, var1);
   }

   private static int getPreferredCurve(int[] var0, AlgorithmConstraints var1) {
      int[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         if (isSupported(var5) && var1.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), "EC", (AlgorithmParameters)idToParams.get(var5))) {
            return var5;
         }
      }

      return -1;
   }

   boolean contains(int var1) {
      int[] var2 = this.curveIds;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         if (var1 == var5) {
            return true;
         }
      }

      return false;
   }

   int length() {
      return 6 + (this.curveIds.length << 1);
   }

   void send(HandshakeOutStream var1) throws IOException {
      var1.putInt16(this.type.id);
      int var2 = this.curveIds.length << 1;
      var1.putInt16(var2 + 2);
      var1.putInt16(var2);
      int[] var3 = this.curveIds;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var3[var5];
         var1.putInt16(var6);
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Extension " + this.type + ", curve names: {");
      boolean var2 = true;
      int[] var3 = this.curveIds;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var3[var5];
         if (var2) {
            var2 = false;
         } else {
            var1.append(", ");
         }

         String var7 = getCurveName(var6);
         if (var7 != null) {
            var1.append(var7);
         } else if (var6 == 65281) {
            var1.append("arbitrary_explicit_prime_curves");
         } else if (var6 == 65282) {
            var1.append("arbitrary_explicit_char2_curves");
         } else {
            var1.append("unknown curve " + var6);
         }
      }

      var1.append("}");
      return var1.toString();
   }

   static boolean isSupported(int var0) {
      int[] var1 = supportedCurveIds;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1[var3];
         if (var0 == var4) {
            return true;
         }
      }

      return false;
   }

   static int getCurveIndex(ECParameterSpec var0) {
      String var1 = JsseJce.getNamedCurveOid(var0);
      if (var1 == null) {
         return -1;
      } else {
         Integer var2 = (Integer)oidToIdMap.get(var1);
         return var2 == null ? -1 : var2;
      }
   }

   static String getCurveOid(int var0) {
      return (String)idToOidMap.get(var0);
   }

   static ECGenParameterSpec getECGenParamSpec(int var0) {
      AlgorithmParameters var1 = (AlgorithmParameters)idToParams.get(var0);

      try {
         return (ECGenParameterSpec)var1.getParameterSpec(ECGenParameterSpec.class);
      } catch (InvalidParameterSpecException var4) {
         String var3 = getCurveOid(var0);
         return new ECGenParameterSpec(var3);
      }
   }

   private static String getCurveName(int var0) {
      EllipticCurvesExtension.NamedEllipticCurve[] var1 = EllipticCurvesExtension.NamedEllipticCurve.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EllipticCurvesExtension.NamedEllipticCurve var4 = var1[var3];
         if (var4.id == var0) {
            return var4.name;
         }
      }

      return null;
   }

   static {
      boolean var0 = SunJSSE.isFIPS();
      EllipticCurvesExtension.NamedEllipticCurve var1 = EllipticCurvesExtension.NamedEllipticCurve.getCurve("secp256r1", false);
      String var2 = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.tls.namedGroups"));
      if (var2 != null && var2.length() != 0 && var2.length() > 1 && var2.charAt(0) == '"' && var2.charAt(var2.length() - 1) == '"') {
         var2 = var2.substring(1, var2.length() - 1);
      }

      ArrayList var3;
      int var6;
      int var7;
      if (var2 != null && var2.length() != 0) {
         String[] var10 = var2.split(",");
         var3 = new ArrayList(var10.length);
         String[] var11 = var10;
         var6 = var10.length;

         for(var7 = 0; var7 < var6; ++var7) {
            String var15 = var11[var7];
            var15 = var15.trim();
            if (!var15.isEmpty()) {
               EllipticCurvesExtension.NamedEllipticCurve var9 = EllipticCurvesExtension.NamedEllipticCurve.getCurve(var15, var0);
               if (var9 != null && isAvailableCurve(var9.id)) {
                  var3.add(var9.id);
               }
            }
         }

         if (var3.isEmpty() && JsseJce.isEcAvailable()) {
            throw new IllegalArgumentException("System property jdk.tls.namedGroups(" + var2 + ") contains no supported elliptic curves");
         }
      } else {
         int[] var4;
         if (var0) {
            var4 = new int[]{23, 24, 25, 9, 10, 11, 12, 13, 14};
         } else {
            var4 = new int[]{23, 24, 25, 9, 10, 11, 12, 13, 14, 22};
         }

         var3 = new ArrayList(var4.length);
         int[] var5 = var4;
         var6 = var4.length;

         for(var7 = 0; var7 < var6; ++var7) {
            int var8 = var5[var7];
            if (isAvailableCurve(var8)) {
               var3.add(var8);
            }
         }
      }

      if (debug != null && var3.isEmpty()) {
         debug.println("Initialized [jdk.tls.namedGroups|default] list contains no available elliptic curves. " + (var2 != null ? "(" + var2 + ")" : "[Default]"));
      }

      supportedCurveIds = new int[var3.size()];
      int var12 = 0;

      Integer var14;
      for(Iterator var13 = var3.iterator(); var13.hasNext(); supportedCurveIds[var12++] = var14) {
         var14 = (Integer)var13.next();
      }

   }

   private static enum NamedEllipticCurve {
      T163_K1(1, "sect163k1", "1.3.132.0.1", true),
      T163_R1(2, "sect163r1", "1.3.132.0.2", false),
      T163_R2(3, "sect163r2", "1.3.132.0.15", true),
      T193_R1(4, "sect193r1", "1.3.132.0.24", false),
      T193_R2(5, "sect193r2", "1.3.132.0.25", false),
      T233_K1(6, "sect233k1", "1.3.132.0.26", true),
      T233_R1(7, "sect233r1", "1.3.132.0.27", true),
      T239_K1(8, "sect239k1", "1.3.132.0.3", false),
      T283_K1(9, "sect283k1", "1.3.132.0.16", true),
      T283_R1(10, "sect283r1", "1.3.132.0.17", true),
      T409_K1(11, "sect409k1", "1.3.132.0.36", true),
      T409_R1(12, "sect409r1", "1.3.132.0.37", true),
      T571_K1(13, "sect571k1", "1.3.132.0.38", true),
      T571_R1(14, "sect571r1", "1.3.132.0.39", true),
      P160_K1(15, "secp160k1", "1.3.132.0.9", false),
      P160_R1(16, "secp160r1", "1.3.132.0.8", false),
      P160_R2(17, "secp160r2", "1.3.132.0.30", false),
      P192_K1(18, "secp192k1", "1.3.132.0.31", false),
      P192_R1(19, "secp192r1", "1.2.840.10045.3.1.1", true),
      P224_K1(20, "secp224k1", "1.3.132.0.32", false),
      P224_R1(21, "secp224r1", "1.3.132.0.33", true),
      P256_K1(22, "secp256k1", "1.3.132.0.10", false),
      P256_R1(23, "secp256r1", "1.2.840.10045.3.1.7", true),
      P384_R1(24, "secp384r1", "1.3.132.0.34", true),
      P521_R1(25, "secp521r1", "1.3.132.0.35", true);

      int id;
      String name;
      String oid;
      boolean isFips;

      private NamedEllipticCurve(int var3, String var4, String var5, boolean var6) {
         this.id = var3;
         this.name = var4;
         this.oid = var5;
         this.isFips = var6;
         if (EllipticCurvesExtension.oidToIdMap.put(var5, var3) != null || EllipticCurvesExtension.idToOidMap.put(var3, var5) != null) {
            throw new RuntimeException("Duplicate named elliptic curve definition: " + var4);
         }
      }

      static EllipticCurvesExtension.NamedEllipticCurve getCurve(String var0, boolean var1) {
         EllipticCurvesExtension.NamedEllipticCurve[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EllipticCurvesExtension.NamedEllipticCurve var5 = var2[var4];
            if (var5.name.equals(var0) && (!var1 || var5.isFips)) {
               return var5;
            }
         }

         return null;
      }
   }
}
