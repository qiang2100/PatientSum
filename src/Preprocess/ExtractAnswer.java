package Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class ExtractAnswer {

	public String extractText(String fileName, MaxentTagger tagger) throws Exception
	{
		String buff = "";
		
		String csvFile = fileName;
    	BufferedReader br = null;
		String line = "";
		//data = new ArrayList<ArrayList<Integer>>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		

		try {
	 
			br = new BufferedReader(new FileReader(csvFile));
		
			boolean isAnswer = false;
			//HashSet<Integer> allSet = new HashSet<Integer>();
			while ((line = br.readLine()) != null) 
			{
				if(line.length()<20)
					continue;
				
				if(line.contains("<begin>Genomic Testing<begin>"))
				{
					isAnswer = true;
					continue;
				}
				if(!isAnswer)
					continue;
				
				if(line.equals("<end>Genomic Testing<end>"))
				{
					break;
				}
				
				System.out.println(line);
				buff += line;
				buff += Character.toString('\n');
				
			}
			
			//System.out.println("the number of differn words in data: " + allSet.size());
			br.close();
	 
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
		 return buff;
	}
	
	
	public void extract(String filePath, MaxentTagger tagger) throws Exception
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
			
			System.out.println(path);
			buff += extractText(path, tagger);
		}
		
		FileWriter fw = new FileWriter("C:/Users/qjp/Dropbox/Patient Summarization/Patient/GenomicTesting.txt");
		
		BufferedWriter bw = new BufferedWriter(fw);	
		bw.write(buff);
		bw.close();
		fw.close();
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try
		{
		
			ExtractAnswer ea = new ExtractAnswer();
		
		
			MaxentTagger tagger = new MaxentTagger("C:/Users/qjp/Dropbox/Patient Summarization/PatientSum/stanford-postagger-2014-01-04/models/english-left3words-distsim.tagger");
		
		//clm.test();
		
			ea.extract("C:/Users/qjp/Dropbox/Patient Summarization/Patient/Data2",tagger);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
