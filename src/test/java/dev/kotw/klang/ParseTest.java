package dev.kotw.klang;

import org.junit.Test;

import java.util.HashMap;

public class ParseTest {
    @Test
    public void testParse() {
        String klang =
                """
                key1 "value1"
                key2 (
                    key3 "value3"
                    key4 (
                        key5 "value5"
                        key6 (
                            key7 "value7"
                            key8 (
                                key9 "value9"
                                key10 "value10"
                            )
                        )
                    )
                )
                key3 "value3"
                key4 (
                    key5 "value5"
                    key6 (
                        key7 "value7"
                        key8 (
                            key9 "value9"
                            key10 "value10"
                        )
                    )
                )
                """;
        KlangParser parser = new KlangParser(klang);
        try {
            HashMap<String, Object> result = parser.parseKlang();
            System.out.println(result);
        } catch (KlangParser.KlangException e) {
            e.printStackTrace();
        }
    }

}
