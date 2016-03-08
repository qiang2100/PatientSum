package kex.pattern;

import java.util.ArrayList;

public class PIndex {
	
	private int siz ; // 位置数
	private int[] pos;  // 具体的每个位置
	
	public PIndex(int num)
	{
		siz=0;
		pos = new int[num];
	}
	
	public int getPos(int i)
	{
		return pos[i];
	}
	
	public void setPos(int i,int v)
	{
		pos[i]=v;
	}
	
	public int getSize()
	{
		return siz;
	}
	
	public void sizInc()
	{
		siz++;
	}
	
	public String ToString()
	{
		String res = "siz="+siz + " pos=";
		for(int i=0;i<pos.length; i++)
			res += pos[i] + ",";
		//res += pos.toString();
		return res;
	}

}
