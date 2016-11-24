package org.rm3umf.sentiment;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TweetTextTokenizer {
	
	

//    private static final Logger LOG = LoggerFactory.getLogger(TweetTextTokenizer.class);

    private static final String PUNCT_ALL = "[!\"%&'+,<=>?\\[\\]^`{|}~():«»“”]";
    
    private static final String[] smiles = new String[] {":)"};

    /**
     * Tokenize the text removing also the hashtags # and mentions @
     *
     * @param text
     * @return
     */
    public static List<String> tokenize(String text) {

        // remove emojis and not UTF-8 chars
        text = cleanText(text);

        Set<String> set = new TreeSet<>();

        StringTokenizer tokenizer = new StringTokenizer(text);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = token.trim();

            // if it's a smile add to the set
            String smile = fromString(token);
            if(smile != null) {
                set.add(token);

            } else if(containsSmile(token)) {
                handleSmileToken(set, token);

            } else {
                // clean urls
                token = token.replace("http://", " ").replace("https://", " ").trim();

                // remove saxon gennitive, apostrophes and normal punctuation
                token = token.replaceAll("'s", " ").replaceAll(PUNCT_ALL, " ").trim();

                // replace occurences of two or more dots (not one because it could be an url
                token = token.replaceAll("\\.{2,}", " ").trim();

                // removing starting and endings dots
                if(token.startsWith("."))
                    token = token.replaceFirst(".", "");
                if(token.endsWith("."))
                    token = token.substring(0, token.length()-1);

                /*
                 * Remove dashes from the token.
                 * Do not remove dashes from words with inner dots to avoid possible urls.
                 *
                 * OK: NAPOLI-JUVE
                 * NO: google.com/rock-it
                 *
                 */
                if(!token.contains(".")) {
                    token = token.replaceAll("-", " ").trim();
                }

                String[] tokens = token.split("\\s+");
                if(tokens.length > 1) {
                    StringTokenizer tokenizer2 = new StringTokenizer(token);
                    while(tokenizer2.hasMoreTokens()) {
                        set.addAll(tokenize(tokenizer2.nextToken()));
                    }
                } else {
                    if(!token.equals(""))
                        set.add(token);
                }
            }
        }

        return reorderTokens(set, text);
    }
    
    private static String fromString (String token) {
    	for (String s : smiles) {
    		if (token.equals(s))
    			return s;
    	}
    	return null;
    }
    
    private static List<String> reorderTokens(Set<String> tokens, String originalSentence) {

        // order the tokens by size to avoid the smallest to "overlap" the indexOf
        List<String> sizeOrderedStrings = new ArrayList<>(tokens);
        Collections.sort(sizeOrderedStrings, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.length() - s2.length();
            }
        });

        TreeMap<Integer, String> tokenMap = new TreeMap<>();
        for(String token : sizeOrderedStrings) {
            int index = originalSentence.indexOf(token, 0);
            while(index != -1) {
                tokenMap.put(index, token);
                index = originalSentence.indexOf(token, (index+1));
            }
        }

        List<String> orderedTokens = new ArrayList<>();

        int lastIndex = 0;
        for(int key : tokenMap.keySet()) {
            if(key < lastIndex)
                continue;

            String value = tokenMap.get(key);
            lastIndex = key + value.length();

            orderedTokens.add(value);
        }

        return orderedTokens;
    }

    private static boolean containsSmile(String token) {
        boolean contains = false;
        for(String smile : smiles) {
            if(token.contains(smile)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private static Set<String> handleSmileToken(Set<String> set, String token) {
        for(String smile : smiles) {
            if(token.contains(smile)) {
                token = token.replace(smile, " "+smile+" ");
            }
        }

        token = token.trim();
        set.addAll(tokenize(token));

        return set;
    }

    /**
     * remove not UTF-8 chars (emojis and not printable)
     *
     * @param text
     * @return
     */
    private static String cleanText(String text) {
        try {
            byte[] utf8Bytes = text.getBytes("UTF-8");
            text = new String(utf8Bytes, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            System.err.println("Error while cleaning socialUpdate text");
        }
        Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\xFF]",
                Pattern.UNICODE_CASE | Pattern.CANON_EQ | Pattern.CASE_INSENSITIVE);
        Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(text);

        text = unicodeOutlierMatcher.replaceAll(" ");
        return text;
    }

}