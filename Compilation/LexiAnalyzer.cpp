#include "LexiAnalyzer.h"
LexiAnalyzer::LexiAnalyzer(string in, string out)
	:fin(ifstream(in)), fout(ofstream(out)), buf(fin), outToF(false)//按声明次序初始化
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


