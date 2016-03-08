package Summarization;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Summarization.SentSup;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import kex.pattern.INSGrow;
import kex.stemmers.MartinPorterStemmer;
import kex.stopwords.Stopwords;
import kex.stopwords.StopwordsEnglish;

public class SumBasic {

	
	ArrayList<Integer> label = new ArrayList<Integer>();
	ArrayList<String> sData;
	ArrayList<ArrayList<Integer>> data;
	ArrayList<Integer> sensLen = new ArrayList<Integer>();
	ArrayList<Double> wordWei = new ArrayList<Double>();
	
	int wholeLen = 0;
	//private ArrayList<Integer> sentWordNum = new ArrayList<Integer>(); // store the number of each sentence's words
	
	private HashMap<String, Integer> word2IdHash = new HashMap<String, Integer>(); //<word, word's id>
	private MartinPorterStemmer m_Stemmer = new MartinPorterStemmer();
	
	private Stopwords m_EnStopwords = new StopwordsEnglish();

	MaxentTagger tagger = new MaxentTagger("C:/Users/qjp/Dropbox/Patient Summarization/PatientSum/stanford-postagger-2014-01-04/models/english-left3words-distsim.tagger");
	
	Pattern p = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
	
	//String allText = "";
	
	public SumBasic()
	{
		
	}
	
	
	public void readText( String path)
	 {
	    	String csvFile = path;
	    	BufferedReader br = null;
			String line = "";
			String cvsSplitBy = " ";
			data = new ArrayList<ArrayList<Integer>>();
			sData = new ArrayList<String>();
			BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
			
			int id = 0;

			try {
		 
				br = new BufferedReader(new FileReader(csvFile));
			
				HashSet<Integer> allSet = new HashSet<Integer>();
				while ((line = br.readLine()) != null) 
				{
					if(line.length()<20)
						continue;
					
					boolean isDoc = false;
					
					if(line.substring(0, 1).equals("I"))
						isDoc = false;
					else if(line.substring(0, 1).equals("P"))
						isDoc = true;
					else
					{
						isDoc = false;
						System.out.println("don't belong to anynoe!" + line);
					}
					
					
					//if(isDoc)
					//System.out.println(line);
					line = line.substring(2, line.length());
					iterator.setText(line);
					int start = iterator.first();
					for (int end = iterator.next();end != BreakIterator.DONE;start = end, end = iterator.next()) 
					{
					  //System.out.println(line.substring(start,end));
						String sourceSent = line.substring(start,end);
					  
						String tSentence = tagger.tagString(sourceSent);
						
						//System.out.println(tSentence);
						String str[] = tSentence.split("[_ ]");
						
						ArrayList<String> sentWords = new ArrayList<String>();
						for(int i=0; i<str.length; i=i+2)
						{
							String word = str[i];
							
					    	Matcher m = p.matcher(word); // only save these strings only contains characters
					    	if( !m.find()  && word.length()>=3 )
					    	{
					    		String wordLow = word.toLowerCase();
					    		
					    		if(!m_EnStopwords.isStopword(wordLow))
					    		{
					    			String token = m_Stemmer.stemString(wordLow);
					    			sentWords.add(token);
					    		}
					    	}
						}
						
						if(sentWords.size()>=5)
						{
							wholeLen += sentWords.size();
							//System.out.println(sourceSent);
							
							//sentWordNum.add(sentWords.size());
							
							if(isDoc)
								label.add(1);
							else
								label.add(0);
							
							ArrayList<Integer> sentId = new ArrayList<Integer>();
							
							for(int j=0; j<sentWords.size(); j++)
							{
								if (word2IdHash.get(sentWords.get(j))==null)
								  {
									 // allText += String.valueOf(id) + " ";
									  word2IdHash.put(sentWords.get(j), id);
									  sentId.add(id);
									  wordWei.add(1.0);
									   id++;
								    } else
								    {
								    	int wid=(Integer)word2IdHash.get(sentWords.get(j));
								    	wordWei.set(wid, wordWei.get(wid)+1);
								    	if(!sentId.contains(wid))
								    		sentId.add(wid);
								    	//allText += String.valueOf(wid) + " ";
								     }
							}
							
							if(isDoc)
							{
								sData.add(sourceSent);
								data.add(sentId);
								sensLen.add(str.length/2);
							}
								
							//allText += "-1 ";
							//allText += Character.toString('\n');
							
						}
					
						
					
					//sData.add(sample);
					}
					
				}
			
				//System.out.println("the number of differn words in data: " + allSet.size());
				br.close();
		 
			} catch(Exception e)
			{
				e.printStackTrace();
			}
	    }
	
	
	public void test()
	{
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		String source = "This is a test. This is a T.L.A. test. Now with a Dr. in it. I like it? Can you call 'me'?  ";
		iterator.setText(source);
		int start = iterator.first();
		for (int end = iterator.next();
		    end != BreakIterator.DONE;
		    start = end, end = iterator.next()) {
		  System.out.println(source.substring(start,end));
		}
	}
	
	public double computScore(int index)
	{
		double score = 0.0;
		for(int i=0; i<data.get(index).size(); i++)
			score += wordWei.get(data.get(index).get(i));
		
		score /= sensLen.get(index);
		
		return score;
	}
	
	public void updateWordWei(ArrayList<Integer> chooseId)
	{
		for(int i=0; i<chooseId.size(); i++)
		{
			int ind = chooseId.get(i);
			wordWei.set(ind, wordWei.get(ind)*wordWei.get(ind));
		}
	}
	
	public String computSummary()
	{
		String summary = "";
		
		ArrayList<Integer> sumId = new ArrayList<Integer>();
		//int len = 0;
		
		
		int sentNum = 0;
		while(sentNum<=15)
		{

			double flag = -1111;
			int id = -1;
			double score = 0;
			for(int i=0; i<data.size(); i++)
			{
				if(!sumId.contains(i))
				{
					score = computScore(i);
					
					if(score > flag)
					{
						flag = score;
						id = i;
					}
				}
			}
			
			sumId.add(id);
			
			sentNum++;
			System.out.println("score " + flag);
			
			updateWordWei(data.get(id));
			//System.out.println(ss[index].idArr.toString());
			//IPEvolving2(id);
		}
		//System.out.println("-------------------");
		//print();
		
		for(int i=0; i<sumId.size(); i++)
		{
			summary += sData.get(sumId.get(i));
			System.out.println( sData.get(sumId.get(i)));
		}
		
		sumId.clear();
		return summary;
		
	}
	
	
	
	public void mainFun(String path)
	{
		try
		{
			readText(path);
			
			for(int i=0; i<wordWei.size(); i++)
				wordWei.set(i, wordWei.get(i)/wholeLen);
			
			computSummary();
		//test();
		
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		SumBasic elt = new SumBasic();
		
		//String lungTextPath = "C:/Users/Administrator/Dropbox/Patient Summarization/Patient/Data/LungText.txt";
		
		String GenomicTesting = "C:/Users/qjp/Dropbox/Patient Summarization/Patient/GenomicTesting.txt";
		elt.mainFun(GenomicTesting);
		
	}

}
