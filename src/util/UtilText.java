package util;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.*;


import org.knallgrau.utils.textcat.TextCategorizer;


import twitter4j.internal.logging.Logger;







public class UtilText {
	
	
	private static Logger logger = Logger.getLogger(UtilText.class);
	
	public static final String FILE_STOPWORD="./preprocessing/stopwords";
	
	
	
	private static String expStop=" ";
	//private static String symbolToRemove="[\\:|\\;\\?\\.\\)\\(\\-,!\\*]";
	private static String symbolToRemove="[\\:|\\;\\?\\)\\(\\-,!\\*]";
	
	//voglio togliere i link e le citazioni
	private static String twitterStopWords="(@[^ ]+|http(s?)://[\\w|\\.|/]+|#|RT )";
	
	private TextCategorizer catLanguage;
	
	
	private static UtilText instance;
	
	public  static synchronized UtilText getInstance(){
		if(instance==null){
			instance=new UtilText();	
		}
		return instance;
	}
	
	public  UtilText(){
		
		this.catLanguage=new TextCategorizer();
		
		expStop="[ ]+((a";
		
		//Leggi stopword
		try{
			
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream(FILE_STOPWORD);
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String stopword;
			  //Read File Line By Line
			  while ((stopword = br.readLine()) != null)   {
				  expStop=expStop+"|"+stopword;	
				 
			  }
			  //Close the input stream
			  in.close();
			    }catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
			  }
			   
		expStop=expStop+")+ )+";
	}
	
	/**
	 * Rimuovi le stop word da un testo passato come input
	 * @param input - stringa di input
	 * @param output - stringa ripulita dalle stopword
	 */
	
	public static String removeStopWord(String input){
	//	System.out.println(expStop);
		String expReg=expStop;
		Pattern stopWords = Pattern.compile(expReg, Pattern.CASE_INSENSITIVE);
		Matcher matcher = stopWords.matcher(input);
		String clean = matcher.replaceAll(" ");
		
//		Pattern removerTwitter = Pattern.compile(twitterStopWords, Pattern.CASE_INSENSITIVE);
//		matcher = removerTwitter.matcher(input);
//		clean = matcher.replaceAll(" ");
		
		Pattern symbols = Pattern.compile(symbolToRemove, Pattern.CASE_INSENSITIVE);
		matcher = symbols.matcher(clean);
		clean = matcher.replaceAll(" ");
		
		//remove multiple white space
		Pattern whiteSpaceRemover = Pattern.compile("[ ]+", Pattern.CASE_INSENSITIVE);
		matcher = whiteSpaceRemover.matcher(clean);
		clean = matcher.replaceAll(" ");
		return clean;

		
	}
	public String removeACapo(String input){
		String expReg=expStop;
		Pattern stopWords = Pattern.compile("[\n]", Pattern.CASE_INSENSITIVE);
		Matcher matcher = stopWords.matcher(input);
		String clean = matcher.replaceAll(" ");
		
		//remove multiple white space
		Pattern whiteSpaceRemover = Pattern.compile("[ ]+", Pattern.CASE_INSENSITIVE);
		matcher = whiteSpaceRemover.matcher(clean);
		clean = matcher.replaceAll(" ");
		return clean;
		
	}
	public boolean isEnglish(String text){
    	//per memorizzazione nel DB degli utenti inglesi
    	   	//verifico se il twets � in inglese
        	String language=catLanguage.categorize(text);
        	return language.equals("english");
        	
	}
	
public static Set<String> extractLinks(String text)  {
		
		
		//String regex = "\\s+https?://(www)?\\.?[\\w]+\\.[a-z]{2,3}\\s";
		String regex ="http(s?)://[\\w|\\.|/]+";
			
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);

		Set<String> listaLink=new HashSet<String>();
		while (matcher.find()){
			//setto name trasformandolo in minuscolo
			String url=matcher.group();
			logger.debug("estratto url:"+url);
			listaLink.add(url); 
		}
		return listaLink;	
	}
	
	public static void main (String[] args) {
		
		UtilText ut=new UtilText();
		
		String input="RT @cnnbrk: 796 dead in #Haiti #cholera outbreak. http://on.cnn.com/9fWGSl @cnnbrk:and,she,a,an,the http://gd/hrfuD!!!!!!!!!!https://prova.it Air cargo packages examined overseas had explosive material, Obama says \n"+
		"@tuskoblenz Martin Demichelis w�re doch was? ;-) #tusko \n"+
		"@rheinzeitung Viel Erfolg mit der neuen #rzneu :-) \n"+
		"@AndroidPIT Seltsam, da� sich fast nur M�nner aufregen? @Wishu_Kaiser \n"+
		"Guten Appetit! :-) (@QueenRania) \n"+
		"@rheinzeitung Guuuuuuuut-kannte ich noch nicht! @frauenfuss #elmi \n"+
		"@frauenfuss Das forderst du als Frau? ;-) Er wurde doch auf Betreiben der SWR-Frauenbeauftragten geschasst. :-( \n"+
		"@frauenfuss Elmi-das waren noch Zeiten. :-))) \n"+
		"@frauenfuss Und was hast du bei SWR3 gewonnen? :-) \n"+
		"@rheinzeitung Hoffentlich arbeiten bei #BilfingerBerger nicht zu viele Ausl�nder/Migranten. Sonst muss #Koch sofort einschreiten! ;-) \n"+
		"@VanLynden Denn empfiehlst du aber viele Twitterer gleichzeitig oder? :-) \n"+
		"Warum brauche ich unbedingt jetzt neue Winterreifen? *seufz* Nur Nonames gibt es in H�lle und F�lle... \n"+
		"@MarcoVau: Na, Arbeitnehmergesocks... Freut ihr euch �ber das lange Wochenende? \n"+
		"@tuskoblenz @rheinzeitung Wenn man die Aufrufe der Vorschl�ge addiert, kommt eine sch�ne Zahl zusammen. :-) \n"+
		"@cnnbrk: CNNMoney.com: #Nissan recalling 747,000 vehicles in U.S. \n"+
     	"@silbermund: ? Greenpeace! \n"+
		"egal gegen wir nun weiter kommen! :-) @tuskoblenz \n"+
		"@VanLynden hmmm *schmatz* \n"+
		"Wann ist das Wenden auf der Autobahn erlaubt? - Wenn man das Schild sieht: Willkommen im Saarland - *fg* \n"+
		"@j0joey0y @echt Gibt es eine Verteilerliste? :-) \n"+
		"@RZMoJane Eher peinlich. :-( \n"+
		"@silbermund Wie geht das??? and and she she a the and \n";
		
		Set<String>  output=ut.extractLinks(input);
		
		for(String s:output)
		System.out.println(s);
		
		System.out.println(removeStopWord(input));
		
	}
	
	

}
