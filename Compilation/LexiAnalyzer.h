#pragma once
#include <fstream>
#include <map>
#include <vector>
#include <string>
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
struct LexiAnalyzer
{
	ifstream fin;
	ofstream fout;
	Symbol sym;
	Reserved resev;
	vector<string> fileBuffer;
	LexiAnalyzer(string in, string out);
	void run();
};

