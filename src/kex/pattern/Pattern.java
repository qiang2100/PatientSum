package kex.pattern;

import java.util.ArrayList;

public class Pattern {
	private ArrayList <Integer> p; // pattern
	private ArrayList<Integer> s; // start positions of instances
	private ArrayList<Integer> t; // end positions of instances
	
	public Pattern()
	{
		p = new ArrayList<Integer>();
		s = new ArrayList<Integer>();
		t = new ArrayList<Integer>();
	}
	
	public int size()
	{
		return t.size();
	}
	
	public ArrayList<Integer> getP()
	{
		return p;
	}
	
	public ArrayList<Integer> getS()
	{
		return s;
	}
	
	public void copyP(Pattern pat)
	{
		ArrayList<Integer> p1=pat.getP();
		for (int i=0;i<p1.size();i++)
		{
			p.add(p1.get(i));
		}
	}
	
	public boolean containItem(int item)
	{
		if (p.contains(item)) 
			return true;
		else 
			return false;
	}
	
	public void copyS(ArrayList<Integer> posList)
	{
		for (int i=0;i<posList.size();i++)
		{
			s.add(posList.get(i));
		}
	}
	
	public void copyT(ArrayList<Integer> posList)
	{
		for (int i=0; i<posList.size();i++)
		{
			t.add(posList.get(i));
		}
	}
	
	public void addP(int i)
	{
		p.add(i);
	}
	
	public void addP(int i, int pos)
	{
		p.add(pos,i);
	}
	
	public void addS(int i)
	{
		s.add(i);
	}
	
	
	public void addT(int i)
	{
		t.add(i);
	}
	
	public ArrayList<Integer> getT()
	{
		return t;
	}
	
	public boolean hasRepetitiveItem()
	{
		int newItem = p.get(p.size()-1);
		for (int i=0;i<p.size()-1;i++)
		{
			int item = p.get(i);
			if (item==newItem) return true;
		}
		return false;
	}

	

}
