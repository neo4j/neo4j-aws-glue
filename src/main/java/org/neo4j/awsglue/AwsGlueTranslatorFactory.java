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
