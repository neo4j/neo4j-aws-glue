package org.neo4j.awsglue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * @author Gerrit Meier
 */
class AwsGlueTranslatorTest {

    private AwsGlueTranslator translator = new AwsGlueTranslator(Map.of());

    @ParameterizedTest
    @CsvSource({
        "1=0", "1 =0", "1= 0", "1 = 0",
        "1=0 ", "1 =0 ", "1= 0 ", "1 = 0 "
    })
    void shouldTranslatePattern(String pattern) {
        assertEquals("LIMIT 1", translator.translate("WHERE " + pattern));
    }

    @ParameterizedTest
    @CsvSource({
        "1=0", "1 =0", "1= 0", "1 = 0",
        "1=0 ", "1 =0 ", "1= 0 ", "1 = 0 "
    })
    void shouldNotTranslatePatternWithoutWhere(String pattern) {
        assertEquals(pattern, translator.translate(pattern));
    }

    @Test
    void shouldNotMatchPatternWithinAStatement() {
        var testStatement = "SOME SQL WHERE 1 = 0 RETURNS";
        assertEquals(testStatement, translator.translate(testStatement));
    }
}
