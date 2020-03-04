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
	static const int ksta = 4;
	static const string ary[18];
	map<string, int> kind;
	int search(const string &str);
	Symbol();
	static const int relaSta = 10;
	static const int relaEnd = 15;
};
struct pos
{
	int x, l;
	pos() {}
	pos(int xx, int ll);
};
struct Token
{
	string s; 
	pos p; 
	int kind,idx;
	Token() {}
	Token(string & s, pos & p, int kind, int idx)
		:s(s), p(p), kind(kind), idx(idx) {}
	void set(string & ss, pos & pp, int kkind, int iidx);
	void output();
};
struct fileBuffer
{
	Symbol sym;
	vector<string> src;
	pos cur, end;//pos line,x
	stack<pos> pre;
	static const char npos = -1;
	bool canContinue; //注释未封闭 非法符号
	fileBuffer(ifstream& in);
	bool reachEnd();
	bool digEnd(char ch);
	bool getNext(string& s, pos &p);
	bool getNextDigit(string & s, pos & p);
	//bool getNextDecimal(string & s);
	//bool getNextChar(string & s, pos & p);
	//bool getNextStr(string & s, pos & p);
	bool getNextSym(string & s, pos & p,int &idx);
	bool skipComments();	// "/*asdsaf*/9"   true->cur在9 false-> cur在/ || 注释未封闭 
	bool CommentEnd();		// "asdsaf*/9"   true->cur在9 false->cur在* 
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
	bool canContinue;
	LexiAnalyzer(string in, string out);
	bool NextReserved(Token &res);	//1
	bool NextId(Token &res);		//2
	bool NextDig(Token &res);		//3
	bool NextSym(Token &res);
	void run();
	bool unitAnaly();
	bool test();
	
	void output(string &s, pos &p, int kind, int idx);
	void throwError(string &s, pos &p);
	void throwError(string &s, pos &p,const string &msg);
};

