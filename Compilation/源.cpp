#include <iostream>
#include <string>
#include "SyntacAnalyzer.h"

int main()
{
	SyntacAnalyzer exp("text.txt", "out.txt");
	Token a;
	exp.LeA.outToF = false;
	fstream toout("out.txt");
	//cout << "kind-id," << setw(12) << "value," << setw(9) << "line&row" << endl;
	//string s;
	//int T = -1, F = -1, andOr = SyntacAnalyzer::noFlag;
	//exp.boolExpression(T,F,andOr);
	//exp.ifStatement();
	
	if (exp.Program())
		cout << "Yes" << endl;
	exp.outMerge();
	exp.TACoutput();
	//exp.TACoutput(toout);
	return 0;
}
