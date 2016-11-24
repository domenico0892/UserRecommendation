package org.rm3umf.sentiment;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class LIWCDictionaryBase implements LIWCDictionary {

    private Map<String, Set<LIWCCategory>> wordCategoriesMap;

    protected LIWCDictionaryBase(String language) {
        wordCategoriesMap = new HashMap<>();
        init(language);
    }

    @Override
    public Set<LIWCCategory> getCategories(String word) {
        Set<LIWCCategory> categories = wordCategoriesMap.get(word);

        if (categories == null) {
            if(word.length() < 2)
                return new HashSet<>();

            if(!word.endsWith("*")) {
                String shorter = word.substring(0, word.length()-1) + "*";
                return getCategories(shorter);

            } else {
                String shorter = word.substring(0, word.length()-2) + "*";
                return getCategories(shorter);
            }
        }

        return categories;
    }

    private void init(String lang) {

        Map<Integer, LIWCCategory> idCategoryMap = new HashMap<>();

        try {
//            String path = "resources/LIWC_categories_"+lang+".diz";
        	Path p = Paths.get("resources/LIWC_categories_"+lang+".diz");
        	List<String> lines = Files.readAllLines(p);
//            ClassLoader cl = LIWCDictionaryBase.class.getClassLoader();
//            InputStream is = cl.getResourceAsStream(path);
//            List<String> lines = IOUtils.readLines(new BufferedInputStream(is), Charsets.UTF_8);

            for(String line : lines) {
                String[] splitted = line.split(" ");
                int id = Integer.valueOf(splitted[0]);
                String categoryName = splitted[1];

                LIWCCategory category = new LIWCCategory(id, categoryName);
                idCategoryMap.put(id, category);
            }
            p = Paths.get("resources/LIWC_"+lang+".diz");
//            path = "resources/LIWC_"+lang+".diz";
//            is = cl.getResourceAsStream(path);
//            lines = IOUtils.readLines(new BufferedInputStream(is), Charsets.UTF_8);
            lines = Files.readAllLines(p);
            StringTokenizer tokenizer;
            for(String line : lines) {
                tokenizer = new StringTokenizer(line);

                if(tokenizer.hasMoreTokens()) {
                    String word = tokenizer.nextToken();

                    while(tokenizer.hasMoreTokens()) {
                        String cat = tokenizer.nextToken();
                        int idCat = Integer.valueOf(cat);

                        LIWCCategory category = idCategoryMap.get(idCat);

                        Set<LIWCCategory> categories = wordCategoriesMap.get(word);
                        if(categories == null) {
                            categories = new HashSet<>();
                        }
                        categories.add(category);

                        wordCategoriesMap.put(word, categories);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}