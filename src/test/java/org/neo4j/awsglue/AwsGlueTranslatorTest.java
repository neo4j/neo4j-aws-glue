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
