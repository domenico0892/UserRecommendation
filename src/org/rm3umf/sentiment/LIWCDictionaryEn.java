package org.rm3umf.sentiment;
public class LIWCDictionaryEn extends LIWCDictionaryBase {

    private static LIWCDictionaryEn INSTANCE;

    static {
        INSTANCE = new LIWCDictionaryEn();
    }

    private LIWCDictionaryEn() {
        super("en");
    }

    public static LIWCDictionaryEn getInstance() {
        return LIWCDictionaryEn.INSTANCE;
    }

}