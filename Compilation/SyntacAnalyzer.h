#pragma once
#include "LexiAnalyzer.h"
#include <queue>
struct TAC
{
	static const int waitForFill = 0;
	static const int noJmp = -1;
	string op,arg1,arg2,res;
	int jmpRes;
	bool unlimjmp;
	TAC() {}
	TAC(string op, string arg1, string arg2, string res);
	TAC(string op, string arg1, string arg2, int jmpRes);
	TAC(string op, string arg1, string arg2, int jmpRes,bool unlimjmp);
};
struct SyntacAnalyzer
{
	LexiAnalyzer LeA;
	Token tmp;
	int Tcot;
	SyntacAnalyzer(string in, string out);
	static const int andFlag = 1,orFlag = 2,noFlag = 0;
	vector<TAC> TACs;
	bool Program();
	bool subProgram();
	bool statementList();
	bool statement();
	bool defStatement();  //一定有一个；很怪
	bool assignStatement();
	bool ifStatement();
	bool whileStatement();
	bool callStatement();
	bool compStatement();
	bool expression();
	bool expression(string &T);
	bool term();
	bool term(string &T);
	bool factor();
	bool factor(string &T);
	bool boolExpression();
	bool boolExpression(int &T, int &F,int &andOr);
	bool relaExpression();
	bool relaExpression(int &T,int &F, int &andOr);
	bool relaSymbol();
	void throwError(const string &msg, pos & p);
	void TACoutput();
	void TACoutput(fstream &fout);
	void TFfill(int TF, int toFill);
	void outMerge();
	int outMerge(int jp);
};