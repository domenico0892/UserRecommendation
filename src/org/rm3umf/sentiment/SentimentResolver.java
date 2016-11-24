package org.rm3umf.sentiment;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// utilizzare solo per test veloci - per precision usare MLsentv1.5
public class SentimentResolver {

   // private static final Logger LOG = LoggerFactory.getLogger(SentimentResolver.class);

    private LIWCDictionary liwcDictionary;
    private String language;

    public SentimentResolver(String language) throws Exception {
        liwcDictionary = LIWCDictionaryFactory.create(language);
        this.language = language;

        if(liwcDictionary == null)
            throw new Exception("Missing LIWC dictionary for language ["+language+"]. Cannot get sentiment!");
    }

    public int getSentiment(String sentence) {

        if(liwcDictionary != null) {
            int res = 0;
            int contNeg = 0;

            List<String> tokenized = TweetTextTokenizer.tokenize(sentence);
            for(String token : tokenized) {
                Set<LIWCCategory> categories = liwcDictionary.getCategories(token);
                int sentiment = resolve(categories);

                if(sentiment == -5){
                    sentiment = 0;
                    contNeg = 4;
                }

                if(contNeg > 0){
                    res = res + (-1 * sentiment);
                    contNeg--;
                } else {
                    res = res+sentiment;
                    contNeg = 0;
                }
            }

            if(res > 0)
                return 1;
            if(res < 0)
                return -1;

            return 0;

        } else {
            System.err.println("Missing LIWC dictionary for language [{}]. Cannot get sentiment!");
            return 0;
        }
    }

    // TODO refactor?
    private int resolve(Set<LIWCCategory> categories) {
        Set<String> categoriesString = new HashSet<>();
        for(LIWCCategory c : categories) {
            categoriesString.add(c.getCategory());
        }

        if(categoriesString.contains("negemo") && categoriesString.contains("sentpos"))
            return 0;

        if(categoriesString.contains("negemo"))
            return -1;

        if(categoriesString.contains("posemo"))
            return 1;

        if(categoriesString.contains("negate"))
            return -5;

        return 0;
    }
    
    public String getLanguage() {
    	return this.language;
    }
    
    public static void main(String[] args) throws Exception {
    	SentimentResolver s= new SentimentResolver("en");
    	System.out.println("Sentiment: "+s.getSentiment("@cnn #rage i'm so angry for Trump's election."));
    	
    }

}