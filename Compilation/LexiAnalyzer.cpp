#include "LexiAnalyzer.h"
Reserved::Reserved()
{
	for (int i = 0; i < 17; i++)
		idx[ary[i]] = i;//0-index
}
const string Reserved::ary[17] = { "void","var","int","float","string","begin","end","if","then","else","while","do","call","read","write","and","or" };


Symbol::Symbol()
{
	for (int i = ksta; i < ksta + 16; i++)
		kind[ary[i]] = i;
}
const string Symbol::ary[16] = { "{","}","(",")",";","==","=","<","<=",">",">=","<>","+","-","*","/" };

LexiAnalyzer::LexiAnalyzer(string in, string out)
	:fin(ifstream(in)), fout(ofstream(out)),buf(fin)//按声明次序初始化
{
}

void LexiAnalyzer::run()
{

}

fileBuffer::fileBuffer(ifstream & in)
	:cur(0,0),pre(0,0)
{
	string line;
	while (getline(in, line))
		src.push_back(line+"\n");
	int ln = src.size()-1;
	end = pos(ln,src[ln].size()-1);
}

bool fileBuffer::getNext(string &s,pos &p)
{
	if (reachEnd())
		return false;
	skipBlank();
	if (!isalpha(getCur()) && getCur()!='$')
		return false;
	string tmp="";
	pre = cur;
	int &li = cur.l;int &xi = cur.x;
	for (; li < src.size(); li++)
	{
		for (; xi < src[li].size(); xi++)
		{
			if (isalpha(src[li][xi])||isdigit(src[li][xi]))
				tmp += src[li][xi];
			else
			{
				s = tmp;
				p = pos(pre.x, pre.l);
				return true;
			}
		}
		xi = 0;
	}
	s = tmp; 
	p = pos(pre.x, pre.l);
	return true;
}

bool fileBuffer::getNextDigit(string &s, pos &p)
{
	if (reachEnd())
		return false;
	skipBlank();
	if (!isdigit(getCur()))
		return false;
	string tmp = "";
	pre = cur;
	int &li = cur.l; int &xi = cur.x;
	for (; li < src.size(); li++)
	{
		for (; xi < src[li].size(); xi++)
		{
			if (isdigit(src[li][xi]))
				tmp += src[li][xi];
			else
			{
				s = tmp;
				p = pos(pre.x, pre.l);
				return true;
			}
		}
		xi = 0;
	}
	s = tmp;
	p = pos(pre.x, pre.l);
	return true;
}
bool fileBuffer::getNextDecimal(string &s, pos &p)
{
	string s1;
	if (!getNextDigit(s1, p))
		return false;
	if (getCur()!='.')
		return false;
	NextCur();
	string s2; pos p2;
	if (!getNextDigit(s2, p2))
		return false;
	s = s1 + "." + s2;
	//p = p;
	return true;
}

char fileBuffer::getCur()
{
	if (reachEnd())
		return npos;
	return src[cur.l][cur.x];
}

bool fileBuffer::reachEnd()
{
	if (cur.l > end.l || (cur.l == end.l&&cur.x >= end.x))
		return true;
	return false;
}

void fileBuffer::skipBlank()
{
	int &li = cur.l;
	int &xi = cur.x;
	for (; li < src.size(); li++)
	{
		for (; xi < src[li].size(); xi++)
		{
			if (!isblank(src[li][xi])&& src[li][xi]!='\n')
				return;
		}
		xi = 0;
	}
}

void fileBuffer::NextCur()
{
	cur.l++;
	cur.x++;
}

pos::pos(int xx, int ll)
	:x(xx), l(ll)
{
}
