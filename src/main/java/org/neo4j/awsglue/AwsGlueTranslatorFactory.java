/*
 * Copyright (c) "Neo4j"
 * Neo4j Sweden AB [https://neo4j.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.awsglue;

import java.util.Map;
import org.neo4j.jdbc.translator.spi.Translator;
import org.neo4j.jdbc.translator.spi.TranslatorFactory;

/**
 * @author Gerrit Meier
 */
public class AwsGlueTranslatorFactory implements TranslatorFactory {

    // if we ever need some debugging in AWS Glue, this is the place
    /*
    static {
        var loggers = new Logger[]{
            Logger.getLogger("org.neo4j.jdbc.statement.SQL"),
            Logger.getLogger("org.neo4j.jdbc.statement"),
            Logger.getLogger("org.neo4j.jdbc.translator"),
            Logger.getLogger("org.neo4j.jdbc.connection"),
            Logger.getLogger("org.neo4j.jdbc.prepared-statement"),
            Logger.getLogger("org.neo4j.jdbc")
        };

        ConsoleHandler handler = new ConsoleHandler();
        // PUBLISH this level
        handler.setLevel(Level.FINEST);
        for (Logger logger : loggers) {
            logger.setLevel(Level.FINEST);
            logger.addHandler(handler);
        }
    }
    */

    public AwsGlueTranslatorFactory() {}

    @Override
    public Translator create(Map<String, ?> properties) {
        return new AwsGlueTranslator(properties);
    }
}
