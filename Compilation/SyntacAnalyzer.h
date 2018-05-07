#pragma once
#include "LexiAnalyzer.h"
struct SyntacAnalyzer
{
	LexiAnalyzer LeA;
	Token tmp;
	SyntacAnalyzer(string in, string out);
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
	bool term();
	bool factor();
	bool boolExpression();
	bool relaExpression();
	bool relaSymbol();
	void throwError(const string &msg, pos & p);
};