package org.rm3umf.sentiment;
import java.util.Set;

public interface LIWCDictionary {

    Set<LIWCCategory> getCategories(String word);

}