package Summarization;

import java.util.ArrayList;

public class SentSup {
	
	ArrayList<Integer> idArr = new ArrayList<Integer>();
	ArrayList<Double> idSupArr= new ArrayList<Double>();
	
	ArrayList<Integer> idSoureArr;// = new ArrayList<Integer>();
	ArrayList<Integer> idSoureSupArr;//= new ArrayList<Double>();
	
	public SentSup()
	{
		
	}
	
	public SentSup(ArrayList<Integer> idFile, ArrayList<Integer> idSup)
	{
		idSoureArr = idFile;
		idSoureSupArr = idSup;
	}
	
	public void add(ArrayList<Integer> arr, double sup)
	{
		for(int i=0; i<arr.size(); i++)
		{
			int id = arr.get(i);
			if(!idArr.contains(id))
			{
				idArr.add(id);
				//idSupArr.add(1.0);
				//idSupArr.add((double)sup*arr.size());
				idSupArr.add((double)sup);
			}else
			{
				int index = idArr.indexOf(id);
				idSupArr.set(index,idSupArr.get(index)+sup);
				//idSupArr.set(index,idSupArr.get(index)+(double)sup*arr.size());
				//idSupArr.set(index,idSupArr.get(index)+(double)sup);
			}
		}
	}
	
	public void print()
	{
		//if(idArr.size() != idSupArr.size())
			//System.out.println("wrong!!!!");
		for(int i=0; i<idArr.size(); i++)
		{
			System.out.print(idArr.get(i)+":"+ idSupArr.get(i)+ " ");
		}
		System.out.println();
	}
	
	public boolean isNull()
	{
		if(idArr.isEmpty())
			return true;
		else
			return false;
	}
	
	public void normal()
	{
		double sum = 0;
		
		for(int i=0; i<idSupArr.size(); i++)
			sum += idSupArr.get(i);
		
		for(int i=0; i<idSupArr.size(); i++)
		{
			idSupArr.set(i, idSupArr.get(i)/sum);
		}
	}
	
	public double subSum()
	{
		double res = 0;
		
		for(int i=0; i<idSupArr.size(); i++)
			res += idSupArr.get(i);
		
		return res;
	}
	
	public double subSum2()
	{
		double res = 0;
		
		for(int i=0; i<idSupArr.size(); i++)
		{
			int index = idSoureArr.indexOf(idArr.get(i));
			res += idSupArr.get(i)*idSoureSupArr.get(index);
		}
		
		return res;
	}
	
	public void supZero()
	{
		for(int i=0; i<idSupArr.size(); i++)
			idSupArr.set(i, 0.0);
	}
	public void clear()
	{
		idArr.clear();
		idSupArr.clear();
	}

}
