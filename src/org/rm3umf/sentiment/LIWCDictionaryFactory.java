package org.rm3umf.sentiment;
public class LIWCDictionaryFactory {

    public static LIWCDictionary create(String language) {
        LIWCDictionary liwcDictionary = null;
        switch (language) {
//            case "it" : {
//                liwcDictionary = LIWCDictionary.getInstance();
//                break;
//            }
            case "en" : {
                liwcDictionary = LIWCDictionaryEn.getInstance();
                break;
            }
//            case "es" : {
//                liwcDictionary = LIWCDictionaryEs.getInstance();
//                break;
//            }
        }

        return liwcDictionary;
    }

}