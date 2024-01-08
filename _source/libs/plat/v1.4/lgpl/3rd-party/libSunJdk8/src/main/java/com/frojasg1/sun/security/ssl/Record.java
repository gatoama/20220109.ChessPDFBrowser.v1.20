package com.frojasg1.sun.security.ssl;

interface Record {
   byte ct_change_cipher_spec = 20;
   byte ct_alert = 21;
   byte ct_handshake = 22;
   byte ct_application_data = 23;
   int headerSize = 5;
   int maxExpansion = 1024;
   int trailerSize = 20;
   int maxDataSize = 16384;
   int maxPadding = 256;
   int maxIVLength = 256;
   int headerPlusMaxIVSize = 261;
   int maxRecordSize = 16921;
   boolean enableCBCProtection = Debug.getBooleanProperty("jsse.enableCBCProtection", true);
   int maxDataSizeMinusOneByteRecord = 15846;
   int maxLargeRecordSize = 33305;
   int maxAlertRecordSize = 539;
   int OVERFLOW_OF_INT08 = 256;
   int OVERFLOW_OF_INT16 = 65536;
   int OVERFLOW_OF_INT24 = 16777216;
}
