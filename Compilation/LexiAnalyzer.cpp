#include "LexiAnalyzer.h"
LexiAnalyzer::LexiAnalyzer(string in, string out)
	:fin(ifstream(in)), fout(ofstream(out)), buf(fin), outToF(false), canContinue(true)//按声明次序初始化
{

}



void LexiAnalyzer::run()
{
	if (outToF)
		fout << "kind-id," << setw(12) << "value," << setw(9) << "line&row" << endl;
	else
		cout << "kind-id," << setw(12) << "value," << setw(9) << "line&row" << endl;
	while (unitAnaly())
		;
	//test();
}
bool LexiAnalyzer::test()
{
	string str = "";
	pos p(-1, -1);
	buf.skipBlank();
	char fir = buf.getCur();
	if (fir == buf.npos)
		return false;
	int idx = buf.npos;
	if (buf.getNextSym(str, p, idx))
		output(str, p, idx, 0);

	if (buf.getNext(str, p))
	{
		int idx = resev.search(str);
		if (idx != -1)
			output(str, p, 1, idx);
		else if (IDfier.find(str) != IDfier.end())
			output(str, p, 2, IDfier[str]);
		else
		{
			idx = IDfier.size() + 1;
			IDfier[str] = idx;
			output(str, p, 2, idx);
		}
	}
}
bool LexiAnalyzer::NextReserved(Token &res)
{
	if (!buf.skipComments())
		return false;
	string str = "";
	pos p(-1, -1);
	buf.skipBlank();
	char fir = buf.getCur();
	if (fir == buf.npos)
		return false;
	if (isalpha(fir) || fir == '_')
	{
		if (buf.getNext(str, p))
		{
			int idx = resev.search(str);
			if (idx != -1)
			{
				res.set(str, p, 1, idx);
				return true;
			}
			else
			{
				buf.retract();
				return false;
			}
		}
		else
		{
			throwError(str, p);
			buf.canContinue = false;
		}
	}
	return false;
}
bool LexiAnalyzer::NextId(Token & res)
{
	if (!buf.skipComments())
		return false;
	string str = "";
	pos p(-1, -1);
	buf.skipBlank();
	char fir = buf.getCur();
	if (fir == buf.npos)
		return false;
	if (isalpha(fir) || fir == '_')
	{
		if (buf.getNext(str, p))
		{
			int idx = resev.search(str);
			if (idx == -1)
			{
				if (str.size() > 20)
				{
					throwError(str, p, "invaild ID");
					canContinue = false;
					return false;
				}

				if (IDfier.find(str) != IDfier.end())
					res.set(str, p, 2, IDfier[str]);
				else
				{
					idx = IDfier.size() + 1;
					IDfier[str] = idx;
					res.set(str, p, 2, idx);
				}
				return true;
			}
			else
			{
				buf.retract();
				return false;
			}
		}
		else
		{
			throwError(str, p);
			buf.canContinue = false;
		}
	}
	return false;
}
bool LexiAnalyzer::NextDig(Token & res)
{
	if (!buf.skipComments())
		return false;
	string str = "";
	pos p(-1, -1);
	buf.skipBlank();
	char fir = buf.getCur();
	if (fir == buf.npos)
	{
		canContinue = false;
		return false;
	}
	if (isdigit(fir))
	{
		if (fir == '0')
		{
			throwError(str, p, "invaild digit");
			canContinue = false;
			return false;
		}
		//buf.skipBlank();
		if (buf.getNextDigit(str, p))
		{
			res.set(str, p, 3, 0);
			return true;
		}
		else
		{
			throwError(str, p, "invaild digit");
			canContinue = false;
			return false;
		}
	}
	//buf.retract();
	return false;
}
bool LexiAnalyzer::NextSym(Token & res)
{
	if (!buf.skipComments())
		return false;
	string str = "";
	pos p(-1, -1);
	buf.skipBlank();
	char fir = buf.getCur();
	if (fir == buf.npos)
	{
		canContinue = false;
		return false;
	}

	int idx = buf.npos;
	if (buf.getNextSym(str, p, idx))
	{
		res.set(str, p, idx, 0);
		return true;
	}
	else
	{
		buf.retract();
		return false;
	}
}
bool LexiAnalyzer::unitAnaly()
{
	string str = "";
	pos p(-1, -1);
	buf.skipBlank();
	char fir = buf.getCur();
	if (fir == buf.npos)
		return false;
	if (isalpha(fir) || fir == '$')
	{
		if (buf.getNext(str, p))
		{
			int idx = resev.search(str);
			if (idx != -1)
				output(str, p, 1, idx);
			else if (IDfier.find(str) != IDfier.end())
				output(str, p, 2, IDfier[str]);
			else
			{
				idx = IDfier.size() + 1;
				IDfier[str] = idx;
				output(str, p, 2, idx);
			}
		}
		else
			throwError(str, p);
	}
	else if (isdigit(fir))
	{
		//buf.skipBlank();
		if (buf.getNextDigit(str, p))
			output(str, p, 4, 0);
		else
			throwError(str, p, "invaild digit");
	}
	else if (fir == '/')
	{
		int idx = buf.npos;
		if (buf.skipComments())
			return true;
		else if (buf.getNextSym(str, p, idx))
			output(str, p, idx, 0);
		else
			throwError(str, p, "Error: expect CommentEnd");
	}
	else
	{
		int idx = buf.npos;
		if (buf.getNextSym(str, p, idx))
			output(str, p, idx, 0);
		else
			throwError(str, p, "unknow Symbol");
	}
	return true;
}

void LexiAnalyzer::output(string & s, pos & p, int kind, int idx)
{
	if (outToF)
	{
		fout << "(" << setw(2) << kind << " ," << setw(2) << idx << ","
			<< setw(10) << s << " ," << setw(3) << p.l << ","
			<< setw(3) << p.x << ")" << endl;
	}
	else
	{
		cout << "(" << setw(2) << kind << " ," << setw(2) << idx << ","
			<< setw(10) << s << " ," << setw(3) << p.l << ","
			<< setw(3) << p.x << ")" << endl;
	}
}

void LexiAnalyzer::throwError(string & s, pos & p)
{
	//cerr <<"Error"<< "s" << " " << p.l << "," << p.x << endl;
	if (outToF)
		fout << "Error " << s << " " << p.l << "," << p.x << endl;
	else
		cout << "Error " << s << " " << p.l << "," << p.x << endl;
}

void LexiAnalyzer::throwError(string & s, pos & p, const string & msg)
{
	//cerr<<"Error msg: " << msg << endl;
	throwError(s, p);
	if (outToF)
		fout << "Error msg: " << msg << endl;
	else
		cout << "Error msg: " << msg << endl;
}

void Token::set(string & ss, pos & pp, int kkind, int iidx)
{
	s = ss;
	p.x = pp.x;
	p.l = pp.l;
	kind = kkind;
	idx = iidx;
}

void Token::output()
{
	cout << "Error " << s << " (" << p.l << "," << p.x <<")"<< endl;
	/*cout << "(" << setw(2) << kind << " ," << setw(2) << idx << ","
		<< setw(10) << s << " ," << setw(3) << p.l << ","
		<< setw(3) << p.x << ")" << endl;*/
}
