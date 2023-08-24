/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.example.log4j.perf;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext.ContextStack;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.util.StringMap;

import com.example.log4j.Main;

/**
 * Compares Log4j2 with Logback PatternLayout performance.
 */
// ============================== HOW TO RUN THIS TEST: ====================================
//
// single thread:
// java -jar log4j-perf/target/benchmarks.jar ".*PatternLayoutComparison.*" -f 1 -wi 10 -i 10 -tu ns -bm sample
//
// Usage help:
// java -jar log4j-perf/target/benchmarks.jar -help
//
@State(Scope.Thread)
public class MyBenchmark {

    final static String STR = "AB!(%087936DZYXQWEIOP$#^~-=/><nb"; // length=32
    final static LogEvent LOG4J2EVENT = createLog4j2Event();
    private static final Charset CHARSET_DEFAULT = Charset.defaultCharset();


    private static LogEvent createLog4j2Event() {
        final Marker marker = null;
        final String fqcn = "com.mycom.myproject.mypackage.MyClass";
        final Level level = Level.DEBUG;
        final Message message = new SimpleMessage(STR);

        final Exception inner = new Exception(Main.MSG);
        final Exception outer = new Exception(Main.MSG, inner);

        final Throwable t = outer;
        final StringMap mdc = null;
        final ContextStack ndc = null;
        final String threadName = null;
        final StackTraceElement location = null;
        final long timestamp = 12345678;

        return Log4jLogEvent.newBuilder() //
                .setLoggerName("name(ignored)") //
                .setMarker(marker) //
                .setLoggerFqcn(fqcn) //
                .setLevel(level) //
                .setMessage(message) //
                .setThrown(t) //
                .setContextData(mdc) //
                .setContextStack(ndc) //
                .setThreadName(threadName) //
                .setSource(location) //
                .setTimeMillis(timestamp) //
                .build();
    }


    private static PatternLayout pl(String pat) {
        return PatternLayout.createLayout(pat, null,
            null, null, CHARSET_DEFAULT, false, true, null, null);
    }


    @Setup
    public void setUp() throws IOException {
        //System.out.println(LOG4J2_PATTERN_LAYOUT_i.toSerializable(LOG4J2EVENT));
        //System.out.println(LOG4J2_PATTERN_LAYOUT_ii.toSerializable(LOG4J2EVENT));
        //System.out.println(LOG4J2_PATTERN_LAYOUT_iii.toSerializable(LOG4J2EVENT));
    }



    private static final PatternLayout pat1 = pl("%d %5p [%t] %c{1} %X{transactionId} - %m%n");
    @Benchmark
    public byte[] noEnc_noEx() {
        return pat1.toByteArray(LOG4J2EVENT);
    }


    private static final PatternLayout pat2 = pl("%d %5p [%t] %c{1} %X{transactionId} - %m%ex%n");
    @Benchmark
    public byte[] noEnc_Ex() {
        return pat2.toByteArray(LOG4J2EVENT);
    }


    private static final PatternLayout pat3 = pl("%d %5p [%t] %c{1} %X{transactionId} - %enc{%m}{HTML}%n");
    @Benchmark
    public byte[] baseEnc_noEx() {
        return pat3.toByteArray(LOG4J2EVENT);
    }


    private static final PatternLayout pat4 = pl("%d %5p [%t] %c{1} %X{transactionId} - %enc{%m%ex}{HTML}%n");
    @Benchmark
    public byte[] baseEnc_Ex() {
        return pat4.toByteArray(LOG4J2EVENT);
    }


    private static final PatternLayout pat5 = pl("%d %5p [%t] %c{1} %X{transactionId} - %myenc{%m}{HTML}%n");
    @Benchmark
    public byte[] myEnc_HTML_noEx() {
        return pat5.toByteArray(LOG4J2EVENT);
    }


    private static final PatternLayout pat6 = pl("%d %5p [%t] %c{1} %X{transactionId} - %myenc{%m}{URL}%n");
    @Benchmark
    public byte[] myEnc_URL_noEx() {
        return pat6.toByteArray(LOG4J2EVENT);
    }


    private static final PatternLayout pat7 = pl("%d %5p [%t] %c{1} %X{transactionId} - %myenc{%m%ex}{HTML}%n");
    @Benchmark
    public byte[] myEnc_HTML_Ex() {
        return pat7.toByteArray(LOG4J2EVENT);
    }

    private static final PatternLayout pat8 = pl("%d %5p [%t] %c{1} %X{transactionId} - %myenc{%m%ex}{URL}%n");
    @Benchmark
    public byte[] myEnc_URL_Ex() {
        return pat8.toByteArray(LOG4J2EVENT);
    }

    private static final PatternLayout pat9 = pl("%d %5p [%t] %c{1} %X{transactionId} - %myencalt{%m}{URL}%n");
    @Benchmark
    public byte[] altEnc_URL_noEx() {
        return pat9.toByteArray(LOG4J2EVENT);
    }

    private static final PatternLayout pat10 = pl("%d %5p [%t] %c{1} %X{transactionId} - %myencalt{%m%ex}{URL}%n");
    @Benchmark
    public byte[] altEnc_URL_Ex() {
        return pat10.toByteArray(LOG4J2EVENT);
    }


}
