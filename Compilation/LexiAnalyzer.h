#pragma once
#include <fstream>
#include <map>
#include <vector>
#include <string>
#include <cctype>
using namespace std;
struct Reserved
{
	static const int kind = 1;
	static const string ary[17];
	map<string, int> idx;
	Reserved();
};
struct Symbol
{
	static const int ksta = 7;
	static const string ary[16];
	map<string, int> kind;
	Symbol();
};
struct pos
{
	int x, l;
	pos() {}
	pos(int xx, int ll);
};
struct fileBuffer
{
	vector<string> src;
	pos cur, pre,end;//pos line,x
	static const char npos = -1;
	fileBuffer(ifstream& in);
	bool getNext(string& s,pos &p);
	bool getNextDigit(string & s, pos & p);
	bool getNextDecimal(string & s, pos & p);
	char getCur();
	bool reachEnd();
	void skipBlank();
	void NextCur();
};
struct LexiAnalyzer
{
	Symbol sym;
	Reserved resev;
	ifstream fin;
	ofstream fout;
	fileBuffer buf;
	LexiAnalyzer(string in, string out);
	void run();
};

