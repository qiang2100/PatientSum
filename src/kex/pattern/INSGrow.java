package kex.pattern;

import kex.pattern.PIndex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kex.util.Counter;


public class INSGrow {
	private static final int MAXE=20000; // maximum number of events
	private static final int MAXN=8000; // maximum number of sequences
	private static final int MAXL=40000; // maximum total length
	
	private static final int SEP=-1; // 
	private static final int NA=-3;               
	
	private int n, m; // input data: n = # of sequence; m=total length of sequences
	private int[] s=new int[MAXN], l=new int[MAXN], db=new int[MAXL]; // s[i], start position of sequence i; 
	// l[i],length of sequence i; db ,database of all sequences.
	
	private ArrayList<Integer>[] pos = new ArrayList[MAXE]; //  index of event position: pos[i].get(j) = the jth position of event i,鑼呴垾婢熻锔癸拷闄囧繖閳ュ绌掕寘閳ユ緹璇ャ仮闊儍顬犫懇锟藉繖閳ュ绌撹寘閳ユ緹璇ワ腹锟介檱蹇欓垾瀛よ銇㈡枻鎷疯寘閳ユ緹璇ワ腹锟介檱蹇欓垾瀛わ拷
	
	private HashMap<Integer, PIndex>[] pst = new HashMap[MAXN]; // pst[i].get(j),positions of j in sequence i
	private int[] res = new int[MAXL]; // reversed index of pos[][]// res[i],i鐩查檰閿熺煫鈹撅拷楦ュ繖閳ユ拃銇ｏ腹锟借矾鑼呴垾婢熻顭娧�拷鑼呴敓閾扳敓锔癸拷璺洸闄嗛敓鐭斁锟介弗蹇欓垾鎾�仯锔癸拷璺寘閳ユ緹璇ワ腹锟介檱蹇欓垾瀛よ鈹撅拷楦ョ尗鑹╅垾姣灎鈶╋拷蹇欓垾瀛よ銉拷閿熺煫鈹撅拷楦ュ繖閳ユ拃銇ｏ腹锟借矾鑼呴垾婢熻锔癸拷闄囧繖閳ュ璁库斁锟介弗姘撻敓閾邦個顬ｂ檧锟借幗鍗㈠崲鑾介殕璺繖閳ュ璁库斁锟介弗蹇欓垾鎾�仯锔癸拷璺洸闄嗛敓鐭斁锟介弗蹇欓垾鎾�仯锔癸拷璺� 鑼呴垾婢熻锔癸拷闄囧繖閳ュ锟借寘閳ユ緹璇ワ腹锟介檱蹇欓垾瀛よ銉傃�拷
	private int[] ind = new int[MAXL]; // index of sequence number of each position, ind[i], i鐩查檰閿熺煫鈹撅拷楦ュ繖閳ユ拃銇ｏ腹锟借矾鑼呴垾婢熻锔癸拷闄囧繖閳ュ璁库斁锟介弗鐚墿閳ユ顬犫懇锟藉繖閳ュ璁库斁锟介弗蹇欓垾鎾�仯锔癸拷璺寘閳ユ緹璇ワ腹锟介檱蹇欓垾瀛よ鈹撅拷楦ュ繖閳ユ拃銇ｏ腹锟借矾鑼呴垾婢熻锔癸拷闄囧繖閳ュ锟�
	private int[] next = new int[MAXL]; // index of event position: next[i] = the next position of event db[i] in this sequence (or =-1 if no one left), i鐩查檰閿熺煫鈹撅拷楦ュ繖閳ユ拃銇ｏ腹锟借矾鑼呴垾婢熻锔癸拷闄囧繖閳ュ璁库斁锟介弗蹇欓垾鎾�仯锔癸拷璺寘閳ユ緹璇ワ腹锟介檱蹇欓垾瀛よ鈹撅拷楦ュ繖閳ユ拃銇ｏ腹锟借矾鑼呴垾婢熻銉嫹鑺﹁幗鍨勯垾鎳婏腹锟借矾鑼呴垾婢熻锔癸拷闄囧繖閳ュ璁裤仮绯曞亾鑼呴垾婢熻锔癸拷闄囧繖閳ュ璁裤仮鏂ゆ嫹鑼呴垾婢熻锔癸拷闄囧繖閳ュ锟�	
	private int nid; // number of different events,鑼呴垾婢熻锔癸拷闄囧繖閳ュ璁课差垽鎷疯寘閳ユ緹璇ワ腹锟介檱蹇欓垾瀛よ鈹撅拷楦ヨ寕椹撮檰
	
	private int[] et = new int[MAXE]; // et[i] = the ith event
	private HashMap <Integer, Integer> id; // index of event ID, 鑼呴垾婢熻锔癸拷闄囧繖閳ュ璁库斁锟介弗蹇欓垾鎾�仯锔癸拷璺寘閳ユ緹璇ワ腹锟介檱蹇欓垾瀛よ鈹撅拷楦ヨ幗纰岃伣d鑼呴垾婢熻锔癸拷闄囧繖閳ュ璁库斁锟介弗蹇欓垾鎾�仯锔癸拷璺�
	
	private double minsup; // minimum support
	
	private int tot; // total number of pattern
	
	private HashMap<ArrayList, Integer> patHash;// =new HashMap<ArrayList,Integer>();
	
	private HashMap<ArrayList, ArrayList> patSentIndexHash;
	
	//private ArrayList<Pattern> cloPat = new ArrayList<Pattern>();
	public INSGrow()
	{
		
	}
	
	private void show(Pattern pat)
	{
		int i;
		ArrayList<Integer> p=pat.getP();
		for(i=0;i<p.size();i++)
		{
			if (i==0) 
				System.out.print("(");
			//System.out.print(et[p.get(i)]);
			System.out.print(p.get(i));
			if (i<p.size()-1)
				System.out.print(" ");
			else
				System.out.print(")");
		}
		for(int j=0; j<pat.getT().size(); j++)
		{
			System.out.print(ind[pat.getT().get(j)] + " ");
		}
		System.out.println();
	}
	
	private void addPattern(Pattern pat)
	{
		
		ArrayList pList = new ArrayList();
		ArrayList<Integer> p=pat.getP();
		if(p.size()<2)
			return;
		for (int i=0;i<p.size();i++)
		{
			pList.add(et[p.get(i)]); //id
			//pList.add(id.get(p.get(i)));
		}
		patHash.put(pList, pat.getT().size());
		
		ArrayList<Integer> sentIndexList = new ArrayList();
		for(int j=0; j<pat.getT().size(); j++)
		{
			sentIndexList.add(ind[pat.getT().get(j)] );
		}
		patSentIndexHash.put(pList, sentIndexList);
	}
	
	public HashMap getPatHash()
	{
		return patHash;
	}
	
	public HashMap getPatSentIndexHash()
	{
		return patSentIndexHash;
	}
	
	
	private int last_position(ArrayList<Integer> p, int j)
	{
		int bck=0, frt = p.size()-1, mid;
		j--;
		while(frt-bck>1)
		{
			mid = (frt+bck)/2;
			if (j==p.get(mid))
				return j;
			else if (j<p.get(mid))
				frt=mid;
			else
				bck=mid;
		}
		if (j<p.get(bck))
			return -1;
		else if (j==p.get(bck))
			return j;
		else if (j<p.get(frt))
			return p.get(bck);
		else if (j==p.get(frt))
			return j;
		else 
			return p.get(frt);

	}
	
	private int next_position(PIndex pin, int x, int bck)
	{
		int frt=pin.getSize()-1, mid;
		
		if(bck>frt)
			return -1;
		if(x<=pin.getPos(bck))
			return pin.getPos(bck);
		if (x>pin.getPos(frt))
			return -1;
		while(frt-bck>1)
		{
			mid=(frt+bck)/2;
			if (x==pin.getPos(mid))
				return x;
			else if (x<pin.getPos(mid))
				frt=mid;
			else
				bck=mid;
		}
		if (x<=pin.getPos(bck))
			return pin.getPos(bck);
		else if (x<=pin.getPos(frt))
			return pin.getPos(frt);
		else 
			return -1;

	}
	
	public void expand1(Pattern pat)
	{
		int i,j,k;
		Pattern npat;
		if((double)pat.size()/n<minsup)
		{
			return;
		}
		for (i=0;i<nid;i++)
		{
			npat = new Pattern();
			npat.copyP(pat);
			npat.addP(i);
			k=0;
			for (j=0;j<pos[i].size()&& k<pat.size();j++)
			{
				while(ind[pat.getS().get(k)]<ind[pos[i].get(j)])
				{
					k++;
					if(k==pat.size())
						break;
				}
				if (k==pat.size())
					break;
				if (ind[pat.getS().get(k)]==ind[pos[i].get(j)]&& pat.getT().get(k)<pos[i].get(j))
				{
					npat.addS(pat.getS().get(k));
					npat.addT(pos[i].get(j));
					k++;
				}
			}
			expand1(npat);
		}
	}
	
	private int check(Pattern pat)
	{
		int i, j ,k, l, x, cnt=0;
		int newitm, preind, nxtpos=0;
		ArrayList<Integer> t, nt=new ArrayList<Integer>(); 
		ArrayList<Integer> npat=new ArrayList<Integer>();
		ArrayList<Integer> nnpat = new ArrayList<Integer>();
		ArrayList<Integer> itm=new ArrayList<Integer>();
		HashMap<Integer, Counter> pp = new HashMap<Integer, Counter>(); // the number of sequences that item occurs shared with pat.
		PIndex newpos;
		int nclosed=0, prune=0;
		
		t=pat.getS();
		
		for(i=0;i<t.size();)
		{
			k=ind[t.get(i)];
			cnt++;  // the number of sequences that pat occurs
			Iterator it=pst[k].keySet().iterator();
			while(it.hasNext())
			{
				int temp = (Integer)it.next();
				if(pp.containsKey(temp))  
				{
					pp.get(temp).increment();
				}
				else
					pp.put(temp, new Counter(1));
			}
			for(j=i;j<t.size()&&ind[t.get(j)]==k;j++);
				i=j;
		}
		Iterator itpp = pp.keySet().iterator();
		while (itpp.hasNext())
		{
			int key = (Integer)itpp.next();
			if(pp.get(key).value()==cnt)
				itm.add(key);
		}
		
		for(x=0;x<pat.getP().size();x++)
		{
			Iterator itp=itm.iterator();
			while(itp.hasNext())
			{
				newitm=(Integer)itp.next();
				for(i=0;i<t.size();)
				{
					k=ind[t.get(i)];
					newpos=pst[k].get(newitm);
					preind=0;
					for(j=i;j<t.size()&&ind[t.get(j)]==k;j++)
					{
						if(x>0)
							nxtpos=next_position(newpos,t.get(j)+1,preind);
						else
							nxtpos=next_position(newpos,-1,preind);
						if(nxtpos==-1)
							break;
						preind=res[nxtpos]+1;
						npat.add(nxtpos);
					}
					if(nxtpos==-1)
						break;
					for(j=i;j<t.size()&&ind[t.get(j)]==k;j++);
						i=j;
				}
				if(i<t.size())// the frequency of newitm in the sequences that pat occurs is less than pat.getS().size
				{
					npat.clear();
					continue;
				}
				if (newitm==pat.getP().get(x))
				{
					nt=(ArrayList)npat.clone();
					npat.clear();
					continue; // 蹇欓垾濂ヮ灐鈹撅拷楦ュ繖閳ユ拃銇ｏ腹锟借矾itp.hasNext()
				}
				for (l=x;l<pat.getP().size();l++) // <p0鐩查檰閿熺煫鈹撅拷楦ュ繖閳ユ拃銇ｏ腹锟借矾
				{
					nnpat.clear(); // 
					for (i=0;i<npat.size();)
					{
						k=ind[npat.get(i)];
						newpos=pst[k].get(pat.getP().get(l));
						preind=0;
						for (j=i;j<npat.size()&&ind[npat.get(j)]==k;j++)
						{
							nxtpos=next_position(newpos,npat.get(j)+1,preind);
							if (nxtpos==-1)
								break;
							preind=res[nxtpos]+1;
							nnpat.add(nxtpos);
						}
						if (nxtpos==-1)
							break;
						for (j=i;j<npat.size()&&ind[npat.get(j)]==k;j++);
							i=j;
					}
					if (i<npat.size())
						break;
					npat=(ArrayList)nnpat.clone();// 鐩茬閳ь優銇㈤【鍠⑩斁锟介弗蹇欓垾鎾�仯锔癸拷璺繖閳ь剨鎷�
				}
				if (l==pat.getP().size())
				{
					nclosed=1;
					for (i=0;i<npat.size();i++)
						if (npat.get(i)>pat.getT().get(i))
							break;
					if (i==npat.size())
					{
						prune=1;
						return 2;
					}
				}
				npat.clear();
			}
			t=nt;
		}
		
		
		t.clear();
		nt.clear();
		npat.clear();
		nnpat.clear();
		itm.clear();
		pp.clear();
		return nclosed;
	}
	
	public void expand2(Pattern pat)
	{
		int i,j,k;
		HashMap<Integer, Pattern> npat = new HashMap<Integer, Pattern>();
		int newitm, preind,nxtpos;
		PIndex newpos;
		
		if ((double)pat.size()/n<minsup || pat.hasRepetitiveItem())
		{
			return;
		}
		
//		show(pat);
		
		tot++;
		
		for(i=0;i<pat.size();)
		{
			k=ind[pat.getS().get(i)];
			Iterator it = pst[k].keySet().iterator();
			while(it.hasNext())
			{
				newitm = (Integer)it.next();
				newpos = pst[k].get(newitm);
				preind=0;
				for(j=i; j<pat.size()&&ind[pat.getS().get(j)]==k;j++)
				{
					nxtpos=next_position(newpos, pat.getT().get(j)+1, preind);
					if(nxtpos==-1)
						break;
					preind = res[nxtpos]+1;
					if(!npat.containsKey(newitm))
					{
						Pattern tpat = new Pattern();
						tpat.copyP(pat);
						tpat.addP(newitm);
						npat.put(newitm, tpat);
					}
					Pattern tpat = npat.get(newitm);
					tpat.addS(pat.getS().get(j));
					tpat.addT(nxtpos);
				}
			}
			for(j=i;j<pat.size()&&ind[pat.getS().get(j)]==k;j++);
				i=j;
		}
		Iterator itpat = npat.keySet().iterator();
		while(itpat.hasNext())
		{
			Integer itm= (Integer)itpat.next();
			Pattern tpat = npat.get(itm);
			expand2(tpat);
		}
	}
	
	public void expand3(Pattern pat)
	{
		int i,j,k,temp,flag;
		HashMap<Integer, Pattern> npat = new HashMap<Integer, Pattern>();
		int newitm,preind,nxtpos;
		PIndex newpos;
		
		if(pat.size()<minsup)
		//if((double)pat.size()/n<minsup)  //||pat.hasRepetitiveItem()
		{
			return;
		}
		flag=0;
		temp=check(pat);
		
		if(temp==2)
		{
			return;
		}
		else if (temp==0)
		{
			flag=1;
			tot++;
		}
		
		for(i=0;i<pat.size();)
		{
			k=ind[pat.getS().get(i)];
			Iterator it = pst[k].keySet().iterator();
			while (it.hasNext())
			{
				newitm = (Integer)it.next();
				newpos = pst[k].get(newitm);
				preind = 0;
				for(j=i;j<pat.size()&&ind[pat.getS().get(j)]==k;j++)
				{
					nxtpos = next_position(newpos, pat.getT().get(j)+1,preind);
					if(nxtpos==-1)
						break;
					preind=res[nxtpos]+1;
					if(!npat.containsKey(newitm))
					{
						Pattern tpat = new Pattern();
						tpat.copyP(pat);
						tpat.addP(newitm);
						npat.put(newitm, tpat);
					}
					Pattern tpat = npat.get(newitm);
					tpat.addS(pat.getS().get(j));
					tpat.addT(nxtpos);
				}
			}
			for (j=i; j<pat.size()&&ind[pat.getS().get(j)]==k;j++);
				i=j;
		}
		temp=0;
		Iterator itpat = npat.keySet().iterator();
		while(itpat.hasNext())
		{
			
			Integer itm= (Integer)itpat.next();
			Pattern tpat = npat.get(itm);
			if (tpat.size()==pat.size())
				temp=1;
			expand3(tpat);
		}
		if(flag!=0)
		{
			if(temp!=0)
			{
				tot--;
				//addPattern(pat);
				//show(pat);
			}
			else
			{
				//show(pat);
				addPattern(pat);
			}
		}
		
	}
	
	public void search()
	{
		int i;
		Pattern pat;
		
		for (i=0;i<nid;i++)
			//if ((double)pos[i].size()/n>=minsup)
			if(pos[i].size()>=minsup)
			{
				pat = new Pattern();
				pat.addP(i);
				pat.copyS(pos[i]);
				pat.copyT(pos[i]);
				expand3(pat);
			}
		
		/*for(int j=0; j<et.length; j++)
		{
			System.out.println(j + ": "+ et[j]);
		}*/
	}
	
	private int getid(int x)
	{
		if (!id.containsKey(x))
		{
			et[nid]=x;
			id.put(x, nid);
			nid++;
			return nid-1;
		}
		else 
			return id.get(x);
	}
	
	public void input(String filename, double min_sup)
	{
		int i, temp;
		HashMap<Integer, Counter> deg = new HashMap<Integer, Counter>();
		initialize(); // initialize, reset all the global variables
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			String aLine = reader.readLine();
			
			while (aLine!=null)
			{
				StringTokenizer st = new StringTokenizer(aLine);
				while (st.hasMoreTokens())
			 	{
					temp = Integer.parseInt(st.nextToken()); 
					if (temp==-1)
						break;
					pos[getid(temp)].add(m);
					if (!deg.containsKey(getid(temp)))
						deg.put(getid(temp), new Counter(1));
					else
					{
						Counter c=deg.get(getid(temp));
						c.increment(); // the frequency of an item in the sequence  
					}
					ind[m]=n-1; // index of sequence number of position m
					db[m++]=getid(temp); // event id of position m
					l[n-1]++; // the length of sequence n-1
				}
				Iterator ideg = deg.keySet().iterator();
				while (ideg.hasNext())
				{
					int itm = (Integer)ideg.next();
					PIndex pin = new PIndex(deg.get(itm).value());
					pst[n-1].put(itm, pin); // store all positions of itm in sequence n-1 
				}
				deg.clear();
				
				for(i=s[n-1];i<s[n-1]+l[n-1];i++)
				{
					pst[n-1].get(db[i]).setPos(pst[n-1].get(db[i]).getSize(), i);
					res[i]=pst[n-1].get(db[i]).getSize();
					pst[n-1].get(db[i]).sizInc(); // siz++; 
				}
				db[m++]=SEP; //  the separated symbol between sequences
				s[n++]=m; // the start position of the new sequence

				aLine = reader.readLine();
			}
			n--;
			minsup=min_sup;
			
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Error: file not found in the directory");
			System.exit(-1);   //exit
			
		}
		catch(IOException e)
		{
			System.err.println("Error: problem in stream IO");
			System.exit(-1);
			
		}
		
		
	}
	
	public void inputStr(String str, int sup)
	{
		int i, temp;
		HashMap<Integer, Counter> deg = new HashMap<Integer, Counter>(); // deg[i]= the frequency of event i in the current sequence
		initialize(); // initialize, reset all the global variables
		
		StringTokenizer tok = new StringTokenizer(str, "\n");
		
		while (tok.hasMoreTokens())
		{
			String aLine = tok.nextToken();
			StringTokenizer st = new StringTokenizer(aLine);
			while (st.hasMoreTokens())
		 	{
				temp = Integer.parseInt(st.nextToken()); 
				if (temp==-1)
					break;
				pos[getid(temp)].add(m);
				if (!deg.containsKey(getid(temp)))
					deg.put(getid(temp), new Counter(1));
				else
				{
					Counter c=deg.get(getid(temp));
					c.increment(); // the frequency of an item in the sequence  
				}
				ind[m]=n-1; // index of sequence number of position m
				db[m++]=getid(temp); // event id of position m
				l[n-1]++; // the length of sequence n-1
			}
			Iterator ideg = deg.keySet().iterator();
			while (ideg.hasNext())
			{
				int itm = (Integer)ideg.next();
				PIndex pin = new PIndex(deg.get(itm).value());
				pst[n-1].put(itm, pin); // store all positions of itm in sequence n-1 
			}
			deg.clear();
			
			for(i=s[n-1];i<s[n-1]+l[n-1];i++)
			{
				pst[n-1].get(db[i]).setPos(pst[n-1].get(db[i]).getSize(), i);
				res[i]=pst[n-1].get(db[i]).getSize();
				pst[n-1].get(db[i]).sizInc(); // siz++;
			}
			db[m++]=SEP; //  the separated symbol between sequences
			s[n++]=m; // the start position of the new sequence

		}
		n--;
		minsup=sup;

	}
	
	private void initialize()
	{
		tot=0;
		n=1;
		m=0;
		nid=0;
		id=new HashMap<Integer,Integer>();
		patHash = new HashMap <ArrayList, Integer>();
		patSentIndexHash = new HashMap<ArrayList, ArrayList>();
		java.util.Arrays.fill(l, 0);
		java.util.Arrays.fill(s, 0);
		java.util.Arrays.fill(et, 0);
		java.util.Arrays.fill(res, 0);
		java.util.Arrays.fill(ind, 0);
		java.util.Arrays.fill(db, 0);
		
		for (int i=0;i<MAXE;i++)
		{
			pos[i]= new ArrayList<Integer>();
		}
		
		for (int i=0;i<MAXN;i++)
			pst[i]= new HashMap<Integer, PIndex>();
	}
	
	private void build_next_index()
	{
		int i;
		int[] pt = new int[MAXE];
		java.util.Arrays.fill(pt, -1);
		java.util.Arrays.fill(next, -1);
		
		for (i=0;i<m;i++)
		{
			if(db[i]==SEP)
			{
				java.util.Arrays.fill(pt, -1);
			}
			else
			{
				if(pt[db[i]]!=-1)
					next[pt[db[i]]]=i;
				pt[db[i]]=i; // the priori position of db[i] in the sequence
			}
		}
	}
	
	public void printHashMap()
	{
		Iterator iter = patSentIndexHash.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			
			ArrayList<Integer> it = (ArrayList)entry.getKey();
			
			//if(it.size()<  5)
				//if(sup<min1)
					//continue;
			
			for(int ii=0; ii<it.size(); ii++)
				System.out.print(it.get(ii)+" ");
			System.out.print("->");
			ArrayList<Integer> value = (ArrayList)entry.getValue();
			for(int ii=0; ii<value.size(); ii++)
				System.out.print(value.get(ii)+" ");
			System.out.println("");
			
		}
	}
	
	public void printHash()
	{
		Iterator iter = patHash.entrySet().iterator();
		int i=0;
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			
			ArrayList<Integer> it = (ArrayList)entry.getKey();
			
			
			
			for(int ii=0; ii<it.size(); ii++)
				System.out.print(it.get(ii)+" ");
			System.out.print("->");
			System.out.print(entry.getValue()+" ");
			if((++i)%10==0)
				System.out.println("");
			
		}
	}
	
	public static void main(String[] args)
	{
		//String filename = "testdocs/data_JBoss2.txt";
		String filename = "test.txt";
		//String filename = "testdocs/en/train/test_postProcess.txt";
		System.out.println("Reading and indexing...");
		
		INSGrow ins = new INSGrow();
		ins.input(filename,2);
		
//		ins.build_next_index();
		
		Date start = new Date();
		System.out.println("Starting searching...");
		ins.search();
		ins.printHash();
		Date end = new Date();
		long span = end.getTime() - start.getTime();
		System.out.println("End with "+ ins.tot + " patterns in " + span + " ms");
		//System.out.println(ins.)
		
	}
	
	

}
