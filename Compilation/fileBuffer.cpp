#include "LexiAnalyzer.h"
fileBuffer::fileBuffer(ifstream & in)
	:cur(0, 0)
{
	string line;
	while (getline(in, line))
		src.push_back(line + "\n");
	int ln = (int)src.size() - 1;				//防止无符号溢出
	end = pos(ln, (int)src[ln].size() - 1);		//防止无符号溢出
}

bool fileBuffer::getNext(string &s, pos &p)
{
	if (reachEnd())
		return false;
	skipBlank();
	if (!isalpha(getCur()) && getCur() != '$')
		return false;
	string tmp = "";
	pos pcur = cur;
	pre.push(pcur);
	int &li = cur.l; int &xi = cur.x;
	for (; li < src.size(); li++)
	{
		for (; xi < src[li].size(); xi++)
		{
			if (isalpha(src[li][xi]) || isdigit(src[li][xi]))
				tmp += src[li][xi];
			else
			{
				s = tmp;
				p = pos(pcur.l, pcur.x);
				return true;
			}
		}
		xi = 0;
	}
	s = tmp;
	p = pos(pcur.l, pcur.x);
	return true;
}

bool fileBuffer::getNextDigit(string &s, pos &p)
{
	if (reachEnd())
		return false;
	//skipBlank();
	if (!isdigit(getCur()))
		return false;
	string tmp = "";
	pos pcur = cur;
	pre.push(pcur);

	int &li = cur.l; int &xi = cur.x;
	for (; li < src.size(); li++)
	{
		for (; xi < src[li].size(); xi++)
		{
			if (isdigit(src[li][xi]))
				tmp += src[li][xi];
			else if(digEnd(src[li][xi]))
			{
				s = tmp;
				p = pos(pcur.l, pcur.x);
				return true;
			}
			else
			{
				while (!digEnd(getCur()))
				{
					tmp += getCur();
					NextCur();
				}
				s = tmp;
				p = pos(pcur.l, pcur.x);
				return false;
			}
		}
		xi = 0;
	}
	s = tmp;
	p = pos(pcur.l, pcur.x);
	return true;
}

//bool fileBuffer::getNextDecimal(string &s, pos &p)
//{
//	string s1;
//	if (!getNextDigit(s1, p))
//		return false;
//	if (getCur() != '.')
//		return false;
//	NextCur();
//	string s2; pos p2;
//	if (!getNextDigit(s2, p2))
//		return false;
//	s = s1 + "." + s2;
//	//p = p;
//	return true;
//}
bool fileBuffer::getNextDecimal(string &s)
{
	if (getCur() != '.')
		return false;
	NextCur();
	if (digEnd(getCur()) && getCur() != '.')
	{
		s += ".";
		return true;
	}
	else if (isdigit(getCur()))
	{
		string s2; pos p2;
		if (!getNextDigit(s2, p2))
			return false;
		s = s + "." + s2;
		//p = p;
		return true;
	}
	else
	{
		string s2;
		while (!digEnd(getCur()))
		{
			s2 += getCur();
			NextCur();
		}
		s = s + "." + s2;
		return false;
	}
}
bool fileBuffer::getNextChar(string & s, pos & p)
{
	if (getCur() != '\'')
		return false;
	string tmp = "";
	pos pcur = cur;
	pre.push(pcur);

	tmp += getCur();

	NextCur();
	tmp += getCur();
	if (getCur() == '\'')
	{
		s = tmp;
		return true;
	}

	NextCur();
	if (getCur() != '\'')
	{
		s = tmp;
		return false;
	}
	else
	{
		tmp += getCur();
		NextCur();
		s = tmp;
		return true;
	}
}
bool fileBuffer::getNextStr(string & s, pos & p)
{
	if (getCur() != '\"')
		return false;
	string tmp = "";
	pos pcur = cur;
	pre.push(pcur);

	tmp += getCur();
	NextCur();

	int &li = cur.l; int &xi = cur.x;
	for (; li < src.size(); li++)
	{
		for (; xi < src[li].size(); xi++)
		{
			if (src[li][xi] != '\"')
				tmp += src[li][xi];
			else
			{
				tmp += src[li][xi];
				NextCur();
				s = tmp;
				p = pos(pcur.l, pcur.x);
				return true;
			}
		}
		xi = 0;
	}
	s = tmp;
	p = pos(pcur.l, pcur.x);
	return false;
}
bool fileBuffer::getNextSym(string & s, pos & p,int &idx)
{
	pos pcur = cur;
	pre.push(pcur);
	string tmp = "";
	char ch = getCur();
	if (ch == '=' || ch == '<' || ch == '>')
	{
		NextCur();
		tmp += ch;
		tmp += getCur();
		idx = sym.search(tmp);
		if (idx!= npos)
		{
			s = tmp;
			p = pcur;
			NextCur();
			return true;
		}
		retract();
	}
	tmp = ""; tmp += ch;
	idx = sym.search(tmp);
	if (idx != npos)
	{
		s = tmp;
		p = pcur;
		NextCur();
		return true;
	}
	NextCur();
	return false;
}
bool fileBuffer::skipComments()
{
	pos pcur = cur;
	if (getCur() != '/')
		return false;
	NextCur();

	if (getCur() == '/')
	{
		pre.push(pcur);
		cur.l++;
		cur.x = 0;
		return true;
	}
	if (getCur() != '*')
	{
		retract();
		return false;
	}
	NextCur();

	pre.push(pcur);
	int &li = cur.l; int &xi = cur.x;
	for (; li < src.size(); li++)
	{
		for (; xi < src[li].size(); xi++)
		{
			if (CommentEnd())
				return true;
		}
		xi = 0;
	}
	//cerr << "Error: expect CommentEnd" << endl;
	return false;
}
bool fileBuffer::CommentEnd()
{
	if (getCur() != '*')
		return false;
	NextCur();
	if (getCur() != '/') 
	{
		retract();
		return false;
	}
	else
	{
		NextCur();
		return true;
	}
}
void fileBuffer::retract()
{
	cur = pre.top();
	pre.pop();
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

bool fileBuffer::digEnd(char ch)
{
	string tmp = ""; tmp += ch;
	if (isblank(ch) || ch == '\n' || ch == '.' || sym.search(tmp) != -1||ch==-1)
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
			if (!isblank(src[li][xi]) && src[li][xi] != '\n')		//isblank并不会检查\n
				return;
		}
		xi = 0;
	}
}

void fileBuffer::NextCur()
{
	pre.push(cur);
	if (cur.x >= (int)src[cur.l].size() - 1)	//防止无符号溢出
	{
		cur.l++;
		cur.x = 0;
	}
	else
		cur.x++;
}

