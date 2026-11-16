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

import java.sql.DatabaseMetaData;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.neo4j.jdbc.translator.spi.Translator;

/**
 * @author Gerrit Meier
 */
public class AwsGlueTranslator implements Translator {

    private static final String CONDITION_REGEX = "WHERE 1\\s?=\\s?0\\s?";
    private static final Pattern CONDITION_PATTERN = Pattern.compile(CONDITION_REGEX);
    private static final Logger LOG = Logger.getLogger("AwsGlueTranslatorFactory");
    private final Map<String, ?> config;

    public AwsGlueTranslator(Map<String, ?> config) {
        this.config = config;
    }

    @Override
    public int getOrder() {
        // the spark cleaner has lowest precedence - 5 and this should run afterwards
        return Translator.LOWEST_PRECEDENCE - 4;
    }

    @Override
    public String translate(String statement, DatabaseMetaData optionalDatabaseMetaData) {
        if (statement == null || statement.isEmpty()) {
            LOG.fine("statement empty");
            return statement;
        }
        var matcher = CONDITION_PATTERN.matcher(statement);
        if (matcher.find() && matcher.end() == statement.length()) {
            LOG.fine("rewriting " + statement);
            String rewrittenQuery = statement.replaceAll(CONDITION_REGEX, "LIMIT 1");
            LOG.fine("to " + rewrittenQuery);
            return rewrittenQuery;
        }
        LOG.fine("not rewriting " + statement);
        return statement;
    }
}
