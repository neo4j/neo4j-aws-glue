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
