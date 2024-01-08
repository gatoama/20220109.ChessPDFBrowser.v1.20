package com.frojasg1.sun.security.ssl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.net.ssl.SSLProtocolException;

final class HandshakeStateManager {
   private LinkedList<HandshakeStateManager.HandshakeState> upcomingStates = new LinkedList();
   private LinkedList<HandshakeStateManager.HandshakeState> alternatives = new LinkedList();
   private static final boolean debugIsOn;
   private static final HashMap<Byte, String> handshakeTypes;

   HandshakeStateManager() {
   }

   boolean isEmpty() {
      return this.upcomingStates.isEmpty();
   }

   List<Byte> check(byte var1) throws SSLProtocolException {
      LinkedList var2 = new LinkedList();
      String var3 = "Handshake message sequence violation, " + var1;
      if (debugIsOn) {
         System.out.println("check handshake state: " + toString(var1));
      }

      if (this.upcomingStates.isEmpty()) {
         if (var1 != 0 && var1 != 1) {
            throw new SSLProtocolException("Handshake message sequence violation, " + var1);
         } else {
            return Collections.emptyList();
         }
      } else if (var1 == 0) {
         return Collections.emptyList();
      } else {
         Iterator var4 = this.upcomingStates.iterator();

         while(var4.hasNext()) {
            HandshakeStateManager.HandshakeState var5 = (HandshakeStateManager.HandshakeState)var4.next();
            if (var5.handshakeType == var1) {
               return var2;
            }

            if (!var5.isOptional) {
               Iterator var6 = this.alternatives.iterator();

               HandshakeStateManager.HandshakeState var7;
               do {
                  if (!var6.hasNext()) {
                     throw new SSLProtocolException(var3);
                  }

                  var7 = (HandshakeStateManager.HandshakeState)var6.next();
                  if (var7.handshakeType == var1) {
                     return var2;
                  }
               } while(var7.isOptional);

               throw new SSLProtocolException(var3);
            }

            var2.add(var5.handshakeType);
         }

         throw new SSLProtocolException("Handshake message sequence violation, " + var1);
      }
   }

   void update(HandshakeMessage var1, boolean var2) throws SSLProtocolException {
      byte var3 = (byte)var1.messageType();
      String var4 = "Handshake message sequence violation, " + var3;
      if (debugIsOn) {
         System.out.println("update handshake state: " + toString(var3));
      }

      HandshakeStateManager.HandshakeState var7;
      label147: {
         boolean var5 = false;
         HandshakeStateManager.HandshakeState var9;
         switch(var3) {
         case 0:
            if (!this.upcomingStates.isEmpty()) {
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_CLIENT_HELLO);
            }
            break label147;
         case 1:
            if (!this.upcomingStates.isEmpty()) {
               HandshakeStateManager.HandshakeState var6 = (HandshakeStateManager.HandshakeState)this.upcomingStates.pop();
               if (var6 != HandshakeStateManager.HandshakeState.HS_CLIENT_HELLO) {
                  throw new SSLProtocolException(var4);
               }
            }

            HandshakeMessage.ClientHello var12 = (HandshakeMessage.ClientHello)var1;
            this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_SERVER_HELLO);
            break label147;
         case 2:
            if (this.upcomingStates.isEmpty()) {
               throw new SSLProtocolException(var4);
            }

            var7 = (HandshakeStateManager.HandshakeState)this.upcomingStates.pop();
            HandshakeStateManager.HandshakeState var8 = null;
            if (!this.alternatives.isEmpty()) {
               var8 = (HandshakeStateManager.HandshakeState)this.alternatives.pop();
            }

            if (var7 != HandshakeStateManager.HandshakeState.HS_SERVER_HELLO && var8 != HandshakeStateManager.HandshakeState.HS_SERVER_HELLO) {
               throw new SSLProtocolException(var4);
            }

            HandshakeMessage.ServerHello var14 = (HandshakeMessage.ServerHello)var1;
            HelloExtensions var15 = var14.extensions;
            if (var2) {
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_SERVER_CHANGE_CIPHER_SPEC);
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_SERVER_FINISHED);
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_CLIENT_CHANGE_CIPHER_SPEC);
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_CLIENT_FINISHED);
            } else {
               CipherSuite.KeyExchange var16 = var14.cipherSuite.keyExchange;
               if (var16 != CipherSuite.KeyExchange.K_KRB5 && var16 != CipherSuite.KeyExchange.K_KRB5_EXPORT && var16 != CipherSuite.KeyExchange.K_DH_ANON && var16 != CipherSuite.KeyExchange.K_ECDH_ANON) {
                  this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_SERVER_CERTIFICATE);
               }

               if (var16 == CipherSuite.KeyExchange.K_RSA_EXPORT || var16 == CipherSuite.KeyExchange.K_DHE_RSA || var16 == CipherSuite.KeyExchange.K_DHE_DSS || var16 == CipherSuite.KeyExchange.K_DH_ANON || var16 == CipherSuite.KeyExchange.K_ECDHE_RSA || var16 == CipherSuite.KeyExchange.K_ECDHE_ECDSA || var16 == CipherSuite.KeyExchange.K_ECDH_ANON) {
                  this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_SERVER_KEY_EXCHANGE);
               }

               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_CERTIFICATE_REQUEST);
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_SERVER_HELLO_DONE);
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_CLIENT_CERTIFICATE);
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_CLIENT_KEY_EXCHANGE);
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_CERTIFICATE_VERIFY);
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_CLIENT_CHANGE_CIPHER_SPEC);
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_CLIENT_FINISHED);
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_SERVER_CHANGE_CIPHER_SPEC);
               this.upcomingStates.add(HandshakeStateManager.HandshakeState.HS_SERVER_FINISHED);
            }
            break label147;
         case 11:
            while(!this.upcomingStates.isEmpty()) {
               var9 = (HandshakeStateManager.HandshakeState)this.upcomingStates.pop();
               if (var9.handshakeType == var3) {
                  var5 = true;
                  if (var9 != HandshakeStateManager.HandshakeState.HS_CLIENT_CERTIFICATE && var9 != HandshakeStateManager.HandshakeState.HS_SERVER_CERTIFICATE) {
                     throw new SSLProtocolException(var4);
                  }

                  boolean var10 = false;
                  if (!this.upcomingStates.isEmpty()) {
                     HandshakeStateManager.HandshakeState var11 = (HandshakeStateManager.HandshakeState)this.upcomingStates.getFirst();
                     if (var11 == HandshakeStateManager.HandshakeState.HS_CLIENT_KEY_EXCHANGE) {
                        var10 = true;
                     }
                  }

                  if (var10) {
                     if (var9 != HandshakeStateManager.HandshakeState.HS_CLIENT_CERTIFICATE) {
                        throw new SSLProtocolException(var4);
                     }
                  } else if (var9 != HandshakeStateManager.HandshakeState.HS_SERVER_CERTIFICATE) {
                     throw new SSLProtocolException(var4);
                  }
                  break;
               }

               if (!var9.isOptional) {
                  throw new SSLProtocolException(var4);
               }
            }

            if (!var5) {
               throw new SSLProtocolException(var4);
            }
            break label147;
         }

         while(!this.upcomingStates.isEmpty()) {
            var9 = (HandshakeStateManager.HandshakeState)this.upcomingStates.pop();
            if (var9.handshakeType == var3) {
               var5 = true;
               break;
            }

            if (!var9.isOptional) {
               throw new SSLProtocolException(var4);
            }
         }

         if (!var5) {
            throw new SSLProtocolException(var4);
         }
      }

      if (debugIsOn) {
         Iterator var13 = this.upcomingStates.iterator();

         while(var13.hasNext()) {
            var7 = (HandshakeStateManager.HandshakeState)var13.next();
            System.out.println("upcoming handshake states: " + var7);
         }

         var13 = this.alternatives.iterator();

         while(var13.hasNext()) {
            var7 = (HandshakeStateManager.HandshakeState)var13.next();
            System.out.println("upcoming handshake alternative state: " + var7);
         }
      }

   }

   void changeCipherSpec(boolean var1, boolean var2) throws SSLProtocolException {
      if (debugIsOn) {
         System.out.println("update handshake state: change_cipher_spec");
      }

      String var3 = "ChangeCipherSpec message sequence violation";
      HandshakeStateManager.HandshakeState var4;
      if ((!var2 || !var1) && (var2 || var1)) {
         var4 = HandshakeStateManager.HandshakeState.HS_CLIENT_CHANGE_CIPHER_SPEC;
      } else {
         var4 = HandshakeStateManager.HandshakeState.HS_SERVER_CHANGE_CIPHER_SPEC;
      }

      boolean var5 = false;

      while(!this.upcomingStates.isEmpty()) {
         HandshakeStateManager.HandshakeState var6 = (HandshakeStateManager.HandshakeState)this.upcomingStates.pop();
         if (var6 == var4) {
            var5 = true;
            break;
         }

         if (!var6.isOptional) {
            throw new SSLProtocolException(var3);
         }
      }

      if (!var5) {
         throw new SSLProtocolException(var3);
      } else {
         if (debugIsOn) {
            Iterator var8 = this.upcomingStates.iterator();

            HandshakeStateManager.HandshakeState var7;
            while(var8.hasNext()) {
               var7 = (HandshakeStateManager.HandshakeState)var8.next();
               System.out.println("upcoming handshake states: " + var7);
            }

            var8 = this.alternatives.iterator();

            while(var8.hasNext()) {
               var7 = (HandshakeStateManager.HandshakeState)var8.next();
               System.out.println("upcoming handshake alternative state: " + var7);
            }
         }

      }
   }

   private static String toString(byte var0) {
      String var1 = (String)handshakeTypes.get(var0);
      if (var1 == null) {
         var1 = "unknown";
      }

      return var1 + "[" + var0 + "]";
   }

   static {
      debugIsOn = Handshaker.debug != null && Debug.isOn("handshake") && Debug.isOn("verbose");
      handshakeTypes = new HashMap(8);
      handshakeTypes.put((byte)0, "hello_request");
      handshakeTypes.put((byte)1, "client_hello");
      handshakeTypes.put((byte)2, "server_hello");
      handshakeTypes.put((byte)11, "certificate");
      handshakeTypes.put((byte)12, "server_key_exchange");
      handshakeTypes.put((byte)14, "server_hello_done");
      handshakeTypes.put((byte)15, "certificate_verify");
      handshakeTypes.put((byte)16, "client_key_exchange");
      handshakeTypes.put((byte)20, "finished");
   }

   static enum HandshakeState {
      HS_HELLO_REQUEST("hello_request", (byte)0),
      HS_CLIENT_HELLO("client_hello", (byte)1),
      HS_SERVER_HELLO("server_hello", (byte)2),
      HS_SERVER_CERTIFICATE("server certificate", (byte)11),
      HS_SERVER_KEY_EXCHANGE("server_key_exchange", (byte)12, true),
      HS_CERTIFICATE_REQUEST("certificate_request", (byte)13, true),
      HS_SERVER_HELLO_DONE("server_hello_done", (byte)14),
      HS_CLIENT_CERTIFICATE("client certificate", (byte)11, true),
      HS_CLIENT_KEY_EXCHANGE("client_key_exchange", (byte)16),
      HS_CERTIFICATE_VERIFY("certificate_verify", (byte)15, true),
      HS_CLIENT_CHANGE_CIPHER_SPEC("client change_cipher_spec", (byte)-1),
      HS_CLIENT_FINISHED("client finished", (byte)20),
      HS_SERVER_CHANGE_CIPHER_SPEC("server change_cipher_spec", (byte)-1),
      HS_SERVER_FINISHED("server finished", (byte)20);

      final String description;
      final byte handshakeType;
      final boolean isOptional;

      private HandshakeState(String var3, byte var4) {
         this.description = var3;
         this.handshakeType = var4;
         this.isOptional = false;
      }

      private HandshakeState(String var3, byte var4, boolean var5) {
         this.description = var3;
         this.handshakeType = var4;
         this.isOptional = var5;
      }

      public String toString() {
         return this.description + "[" + this.handshakeType + "]" + (this.isOptional ? "(optional)" : "");
      }
   }
}
