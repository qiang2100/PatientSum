package kex.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

public class ExtraText {

	public String splitText(String text)
	{
		String[] sentences = text.split("\n");
		boolean isHead = false;
		boolean isText = false;
		int countHead = 0;
		String s = "";
		for(int j=0; j<sentences.length; j++)
		{
			
			if(sentences[j].equals("<HEAD>") || sentences[j].equals("<HL>") || sentences[j].equals("<HEADLINE>"))
			{
				isHead = true;
				countHead++;
				continue;
			}
			else if(sentences[j].equals("</HEAD>") || sentences[j].equals("</HL>") || sentences[j].equals("</HEADLINE>"))
			{
				isHead = false;
				continue;
			}else if(sentences[j].equals("<TEXT>"))
			{
				isText = true;
				continue;
			}else if(sentences[j].equals("</TEXT>"))
			{
				isText = false;
				break;
			}
			if(isHead && (countHead==1) && sentences[j].length()>4)
			{
				
				//String sen = extraHead(sentences[j]);
				//System.out.println(sentences[j]);
				//System.out.println(sen);
				//allHeads.add(sen);
				//buff.append(process(sen,diffDocSet,false));
			}
			
			if(isText)
			{
				sentences[j].replaceAll("<P>", " ");
				sentences[j].replaceAll("</P>", " ");
				//if(sentences[j].contains("<P>") ||sentences[j].contains("</P>"))
					//continue;
				//else
				s += sentences[j] + " ";
			}
				
				//System.out.println(sen);
			
			
		}
		
		return s;
		
	}
	
	public String readTextFromFile(String fileName)
	{
		StringBuffer txtStr = new StringBuffer();
		try 
		{
			File txt = new File(fileName);
			InputStreamReader is = new InputStreamReader(new FileInputStream(txt),"UTF-8");
			int c;
			while ((c = is.read()) != -1) 
			{
				
				txtStr.append((char)c);
			}
			is.close();
			
			
		
		} catch (Exception e) 
		{
			System.err.println("Can't find document.");
		}
		return splitText(txtStr.toString());

	}
	
	
	public void mainFun(String inputDir,String outputDir) throws Exception 
	{
		if (inputDir==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		
		File srcFile = new File(inputDir);		
		String []dirList = srcFile.list();
		
		for(int i=0; i<dirList.length; i++)
		{
			String path = inputDir + dirList[i];
			File twoDir = new File(path);
			
			String []twoDirList = twoDir.list();
			
			String writeRoot = "DUC2002"+"\\"+dirList[i]+ "\\";
			File writeFile = new File(writeRoot);		
			if(!writeFile.exists())
			{		 
				writeFile.mkdirs();		
			} 
			
			for(int j=0; j<twoDirList.length; j++)
			{
				File readFile = new File(inputDir + "\\" + dirList[i]+ "\\"+twoDirList[j]);
				
				String text = readTextFromFile(readFile.getPath());
				
				System.out.println(text);
				
				String newName = twoDirList[j];
				
				File newFile = new File(writeFile,newName);
				if(!newFile.exists())
				{
					newFile.createNewFile();
				}
				BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));  
				writer.write(text);
				writer.close();
			}
			
			
			//clm.test();
			//System.out.println(path);
		/*	String sum = clm.clm(path);
		
			String newName = dirList[i]+"_BasedWord_"+".spl";
			String writeRoot = "DUC2002"+"\\"+dirList[i]+ "\\peers\\";
			
			
			File newFile = new File(writeFile,newName);
			if(!newFile.exists())
			{
				newFile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));  
			writer.write(sum);
			writer.close();*/
			//System.out.println(sum);
			
			//new Thread().sleep(1000);
		}
		//System.out.println("the number of sentences :" + allSentences.size());
		//System.out.println("the number of sentences :" + lenOfAllSentences.size());
	}
	
	public static void main(String[] args) {
		
		
		String dirName = "F:/Êý¾Ý¼¯/DUC2002/DUC2002_Summarization_Documents/docs/";
		
		ExtraText rd = new ExtraText();
		
		try
		{
			rd.mainFun(dirName, "F:/program/TextMining/DUC2002");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
