
package Summarization;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kex.pattern.INSGrow;
import kex.stemmers.MartinPorterStemmer;
import kex.stopwords.Stopwords;
import kex.stopwords.StopwordsEnglish;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

//Time: 2014/7/22
//Aim: multi-document summarization based pattern evolution


public class PTMSum_Whole {

	//private int MaxSent = 1000;
	ArrayList<Integer> label = new ArrayList<Integer>();
	//ArrayList<String> sData;
	private MartinPorterStemmer m_Stemmer = new MartinPorterStemmer();
	private Stopwords m_EnStopwords = new StopwordsEnglish();
	private HashMap<String, Integer> word2IdHash = new HashMap<String, Integer>(); //<word, word's id>
	private HashMap<Integer, String> id2WordHash = new HashMap<Integer, String>(); // <word's id, word>
	private int id = 0;
	
	private ArrayList<String> allSentences = new ArrayList<String> (); // store all the original sentences
	//private ArrayList<ArrayList<Integer>> sentId = new ArrayList<ArrayList<Integer>>();
	private SentSup ss[];
	
	//private ArrayList<ArrayList<Integer>> sentIDArr = new ArrayList<ArrayList<Integer>>();
	//private ArrayList<ArrayList<Integer>> sentIDSupArr = new ArrayList<ArrayList<Integer>>();
	
    //private ArrayList<Double> sentIndexId = new ArrayList<Double>(); // store the sentence's index. 
	
	private ArrayList<Integer> sentWordNum = new ArrayList<Integer>(); // store the number of each sentence's words
	
	private ArrayList<Integer> closedIdArr = new ArrayList<Integer>();
	
	private ArrayList<Integer> sentFile = new ArrayList<Integer>();
	
	private double alpa ;
	
	private double min_sup;
	
	private double belt = 0.1;
	
	private int numFile = 0;
	
	private double[][] sent2SentSis;
	
	Pattern p = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE);
	
	//private int rt = 2;
	//private double coeffIndex=0.3; // coefficient of the sentences' index
	
	public PTMSum_Whole()
	{
		alpa = 0.4;
		min_sup = 0.02;
		/*for(int i=0; i<MaxSent; i++)
		{
			ss[i] = new SentSup();
		}*/
	}
	
	public PTMSum_Whole(double alpa, double min_sup)
	{
		this.alpa = alpa;
		this.min_sup = min_sup;
	}
	
	public PTMSum_Whole(double alpa, double min_sup, double belt)
	{
		this.alpa = alpa;
		
		this.min_sup = min_sup;
		this.belt = belt;
		//this.rt = rt;
	}
	
	
	public String valueFromList(ArrayList<Integer> list)
	{
		String res = "";
		for(int i=0; i<list.size(); i++)
		{
			res += list.get(i) + " ";
		}
		return res;
	}
	
	HashMap<ArrayList,ArrayList> getClos(String buff) throws Exception 
	{
		String writeName = "multiDocu.txt";
	     BufferedWriter writer = new BufferedWriter(new FileWriter(new File(writeName)));   
	     writer.write(buff);
		 writer.close();
		
		INSGrow ins = new INSGrow();
		//ins.input(writeName,(double)numFile*min_sup/allSentences.size());
		ins.input(writeName, min_sup);
		
       ins.search();
		
		HashMap<ArrayList, ArrayList> patSentIndexHash = ins.getPatSentIndexHash();
		
		return patSentIndexHash;
	}
	
	
 
	public String extractText(String fileName, MaxentTagger tagger, int numFile) throws FileNotFoundException
	{
		String buff = "";
		
		String csvFile = fileName;
    	BufferedReader br = null;
		String line = "";
		//data = new ArrayList<ArrayList<Integer>>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		

		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
		
			HashSet<Integer> allSet = new HashSet<Integer>();
			while ((line = br.readLine()) != null) 
			{
				if(line.length()<20)
					continue;
				
				boolean isDoc = true;
				
				if(line.substring(0, 2).contains("I:"))
					isDoc = false;
				
				line = line.substring(2,line.length());
				//if(isDoc)
				//System.out.println(line);
				
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
				    	if(  !m.find()  && word.length()>=3 )
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
						//System.out.println(sourceSent);
						allSentences.add(sourceSent);
						sentWordNum.add(sentWords.size());
						
						if(isDoc)
							label.add(1);
						else
							label.add(0);
						
						for(int j=0; j<sentWords.size(); j++)
						{
							if (word2IdHash.get(sentWords.get(j))==null)
							  {
								  buff += String.valueOf(id) + " ";
								  word2IdHash.put(sentWords.get(j), id);
								   id++;
							    } else
							    {
							    	int wid=(Integer)word2IdHash.get(sentWords.get(j));
							    	buff += String.valueOf(wid) + " ";
							     }
						}
						buff += "-1 ";
						buff += Character.toString('\n');
						
					}
				
				
				//sData.add(sample);
				}
				
			}
			if(numFile==0)
				sentFile.add( allSentences.size()-1);
			else
			{
				sentFile.add(sentFile.get(numFile-1)+allSentences.size());
			}
			 
		
			//System.out.println("the number of differn words in data: " + allSet.size());
			br.close();
	 
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
		 return buff;
	}
	
	
	public void clm(String filePath, MaxentTagger tagger) throws Exception
	{
		if (filePath==null)
		{
			System.out.println("you have not specify the file path.");
			return ;
		}
		
		File srcFile = new File(filePath);		
		String []fileList = srcFile.list();
		
		String buff = "";
		
		for(int i=0; i<fileList.length; i++)
		{
			String path = filePath + "/" + fileList[i];
			
			
			buff += extractText(path, tagger, i);
		}
		
		numFile = fileList.length;
		//sentFile = new int[numFile];
		//numFile = fileList.length;
		//System.out.println(buff);
		
		sent2SentSis = new double[allSentences.size()][allSentences.size()];
		
		HashMap<ArrayList, ArrayList> patHash = getClos(buff);
		//System.out.println(patHash.size());
		
		//printHashMap(patHash);
		
		Iterator iter = patHash.entrySet().iterator();
		ss = new SentSup[allSentences.size()];
		for(int i=0; i<ss.length; i++)
			ss[i] = new SentSup();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			ArrayList<Integer> keyI = (ArrayList)entry.getKey();
			ArrayList<Integer> sentI = (ArrayList)entry.getValue();
		
			if(keyI.size()< 2)
				continue;
			//System.out.println(sI.toString());
			
			for(int i=0; i<keyI.size(); i++)
			{
				if(!closedIdArr.contains(keyI.get(i)))
					closedIdArr.add(keyI.get(i));
			}
			//int allFile = spanFile(sentI);
			double patternWei = (double)sentI.size();//((double)allFile/numFile);
			computSent2Sent(sentI, keyI.size()*patternWei);
			for(int ii=0; ii<sentI.size(); ii++)
			{
				ss[sentI.get(ii)].add(keyI, patternWei);
			}
		}
		
		//for(int i=0; i<ss.length; i++)
		//{
		//	ss[i].print();
		//}
		patHash.clear();
		
		 //idSup = new double[id];
	}
	
	public int getFileIndex(int id)
	{
		for(int i=0; i<sentFile.size(); i++)
			if(id<=sentFile.get(i))
				return i;
		return -1;
	}
	
	public int spanFile(ArrayList<Integer> sentArr)
	{
		ArrayList<Integer> fileId =  new ArrayList<Integer>();
		for(int i=0; i<sentArr.size(); i++)
		{
			int id = getFileIndex(sentArr.get(i));
			if(id == -1)
				System.out.println("wrong!");
			if(!fileId.contains(id))
				fileId.add(id);
		}
		return fileId.size();
			
	}
	
	public void computSent2Sent(ArrayList<Integer> sentArr, double wei)
	{
		int sup = sentArr.size();
		
		for(int i=0; i<sentArr.size(); i++)
		{
			for(int j=i+1; j<sentArr.size(); j++)
			{
				sent2SentSis[sentArr.get(i)][sentArr.get(j)] += wei;
				sent2SentSis[sentArr.get(j)][sentArr.get(i)] += wei;
			}
		}
	}
	
	public void printHashMap(HashMap<ArrayList,ArrayList> patHash)
	{
		Iterator iter = patHash.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			
			ArrayList<Integer> it = (ArrayList)entry.getKey();
			
			//if(it.size()<  5)
				//if(sup<min1)
				//	continue;
			
			for(int ii=0; ii<it.size(); ii++)
				System.out.print(id2WordHash.get(it.get(ii))+" ");
			System.out.print("->");
			ArrayList<Integer> value = (ArrayList)entry.getValue();
			for(int ii=0; ii<value.size(); ii++)
				System.out.print(value.get(ii)+" ");
			System.out.println("");
			
		}
	}
	
	
	
	public double computScoreBySelect(int id, ArrayList<Integer> selectArr)
	{
		double score = 0;
		for(int i=0; i<selectArr.size(); i++)
		{
			double score2 = sent2SentSis[id][selectArr.get(i)];
			if(score2>score)
				score = score2;
		}
		
		return score;
	}
	
	public int computLeftWords(ArrayList<Integer> sentArr)
	{
		int num = 0;
		for(int i=0; i<sentArr.size(); i++)
		{
			if(closedIdArr.contains(sentArr.get(i)))
				num++;
		}
		
		return num;
	}
	
	public void removeClosedWord(ArrayList<Integer> sentArr)
	{
		for(int i=0; i<closedIdArr.size(); i++)
		{
			if(sentArr.contains(closedIdArr.get(i)))
			{
				closedIdArr.remove(i);
				i--;
			}
		}
	}
	
	public String computSummary()
	{
		String summary = "";
		
		ArrayList<Integer> sumId = new ArrayList<Integer>();
		int len = 0;
		
		
		//DecimalFormat df = new DecimalFormat("#.00");
		//System.out.println(sentNumAfter);
		//print();
		
		//ArrayList<Integer> leftClosedArr = new ArrayList<Integer>();
		
		int sentNum = 0;
		while(sentNum<=9)
		{

			double flag = -1111;
			int id = -1;
			double score = 0;
			for(int i=0; i<ss.length; i++)
			{
				if(label.get(i)==0)
				   continue;
				
				if(allSentences.get(i).length()>200)
					continue;
				
				if(!sumId.contains(i))
				{
					score = ss[i].subSum();
					double scoreSelect = (double) computScoreBySelect(i,sumId);
					
					score = score*alpa - (1-alpa)*scoreSelect;
					//score *= (double)(computLeftWords(ss[i].idArr)+1)/allSentences.get(i).getBytes().length;//sentWordNum.get(i);
					//double wordWeight = (double)ss[i].idArr.size()/sentWordNum.get(i) + belt;
					//score = score  /sentWordNum.get(i);
					//score = score  * sentIndexId.get(i)/allSentences.get(i).getBytes().length;
					//score = (score+belt)* sentIndexId.get(i)/(allSentences.get(i).getBytes().length+belt);
					//score /= allSentences.get(i).getBytes().length;
					//score = score * wordWeight;
					//score *=  sentIndexId.get(i);//Math.pow(Math.E, -sentIndexId.get(i)/(6*6));
					//score *= sentIndexId.get(i);
					//System.out.println(i+" "+ score);
					if(score > flag)
					{
						flag = score;
						id = i;
					}
				}
			}
			
			sumId.add(id);
			
			removeClosedWord(ss[id].idArr);
			//len += allSentences.get(id).getBytes().length;
			sentNum++;
			System.out.println("score " + flag);
			//System.out.println(ss[index].idArr.toString());
			//IPEvolving2(id);
		}
		//System.out.println("-------------------");
		//print();
		
		for(int i=0; i<sumId.size(); i++)
		{
			summary += allSentences.get(sumId.get(i));
			System.out.println( allSentences.get(sumId.get(i)));
		}
		
		sumId.clear();
		return summary;
		
	}
	
	public void print()
	{
		for(int i=0; i<ss.length; i++)
		{
			ss[i].print();
		}
	}
	
	public void clear()
	{
		word2IdHash.clear();
		id2WordHash.clear();
		
		//id2Freq.clear();
		allSentences.clear();
		//sentId.clear();
		
		for(int i=0; i<ss.length; i++)
			ss[i].clear();
		//m_Stemmer.
		
		ss = null;
		
		//sentIndexId.clear();
		sentWordNum.clear();
		m_Stemmer = null;
		m_EnStopwords = null;
	}
	
	public void getSentIndex()
	{
		String s = "disappointed by the economic confusion within the new Russian government of Prime Minister Yevgeny Primakov";
		
		for(int i=0; i<allSentences.size(); i++)
			if(allSentences.get(i).contains(s))
				System.out.println("the index of the sentence is " + i);
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try
		{
			MaxentTagger tagger = new MaxentTagger("C:/Users/qjp/Dropbox/Patient Summarization/PatientSum/stanford-postagger-2014-01-04/models/english-left3words-distsim.tagger");
			PTMSum_Whole ts = new PTMSum_Whole(0.5,3);
			//clm.test();
			
			ts.clm("C:/Users/qjp/Dropbox/Patient Summarization/Patient/Data3",tagger);
			//ts.clm("E:/test",tagger);
			//ts.clm("E:/test/",tagger);
			ts.computSummary();
			//System.out.println(ts.computSummary());
			
			//ts.getSentIndex();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
