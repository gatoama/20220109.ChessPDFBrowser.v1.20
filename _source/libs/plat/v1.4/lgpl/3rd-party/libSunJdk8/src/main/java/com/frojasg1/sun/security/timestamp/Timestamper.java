package com.frojasg1.sun.security.timestamp;

import com.frojasg1.sun.security.timestamp.TSRequest;
import com.frojasg1.sun.security.timestamp.TSResponse;

import java.io.IOException;

public interface Timestamper {
   TSResponse generateTimestamp(TSRequest var1) throws IOException;
}
