#pragma once
#include <fstream>
#include <map>
#include <vector>
#include <string>
#include <cctype>
#include <stack>
#include <iostream>//
#include <iomanip>
#include <algorithm>
using namespace std;
struct Reserved
{
	static const int kind = 1;
	static const string ary[10];
	map<string, int> idx;
	Reserved();
	int search(string str);
};
struct Symbol
{
	static const int ksta = 7;
	static const string ary[17];
	map<string, int> kind;
	int search(const string &str);
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
	Symbol sym;
	vector<string> src;
	pos cur, end;//pos line,x
	stack<pos> pre;
	static const char npos = -1;
	fileBuffer(ifstream& in);
	bool reachEnd();
	bool digEnd(char ch);
	bool getNext(string& s, pos &p);
	bool getNextDigit(string & s, pos & p);
	//bool getNextDecimal(string & s);
	//bool getNextChar(string & s, pos & p);
	//bool getNextStr(string & s, pos & p);
	bool getNextSym(string & s, pos & p,int &idx);
	bool skipComments();	// "/*asdsaf*/9"   true->cur��9 false-> cur��/ || ע��δ��� 
	bool CommentEnd();		// "asdsaf*/9"   true->cur��9 false->cur��* 
	void retract();
	char getCur();
	void NextCur();
	void skipBlank();
};
struct LexiAnalyzer
{
	Reserved resev;
	ifstream fin;
	ofstream fout;
	fileBuffer buf;
	map<string, int> IDfier;
	map<string, int> Chars;
	map<string, int> Strs;
	bool outToF;
	LexiAnalyzer(string in, string out);
	void run();
	bool unitAnaly();
	bool test();
	
	void output(string &s, pos &p, int kind, int idx);
	void throwError(string &s, pos &p);
	void throwError(string &s, pos &p,const string &msg);
};

