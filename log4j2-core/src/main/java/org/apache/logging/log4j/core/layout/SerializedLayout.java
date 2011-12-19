/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.layout;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttr;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Transform;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.LineNumberReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 */
@Plugin(name="SerializedLayout",type="Core",elementType="layout",printObject=true)
public class SerializedLayout extends LayoutBase<LogEvent> {

    private static byte[] header;

    static {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.close();
            header = baos.toByteArray();
        } catch (Exception ex) {
            logger.error("Unable to generate Object stream header", ex);
        }
    }

    private SerializedLayout() {
    }

    /**
     * Formats a {@link org.apache.logging.log4j.core.LogEvent} in conformance with the log4j.dtd.
     */
    public byte[] format(final LogEvent event) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new PrivateObjectOutputStream(baos);
            oos.writeObject(event);
        } catch (IOException ioe) {
            logger.error("Serialization of Logging Event failed.", ioe);
        }
        return baos.toByteArray();
    }

    /**
     * Returns the LogEvent.
     * @param event The Logging Event.
     * @return The LogEvent.
     */
    public LogEvent formatAs(final LogEvent event) {
        return event;
    }

    /**
     * Create a SerializedLayout.
     * @return A SerializedLayout.
     */
    @PluginFactory
    public static SerializedLayout createLayout() {

        return new SerializedLayout();
    }

    @Override
    public byte[] getHeader() {
        return header;
    }

    /**
     * The stream header will be written in the Manager so skip it here.
     */
    private class PrivateObjectOutputStream extends ObjectOutputStream {

        public PrivateObjectOutputStream() throws IOException {
        }

        public PrivateObjectOutputStream(OutputStream os) throws IOException {
            super(os);
        }

        @Override
        protected void writeStreamHeader() {
        }
    }
}
