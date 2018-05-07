#include <iostream>
#include <string>
#include "SyntacAnalyzer.h"

int main()
{
	SyntacAnalyzer exp("text.txt", "out.txt");
	Token a;
	exp.LeA.outToF = false;
	//cout << "kind-id," << setw(12) << "value," << setw(9) << "line&row" << endl;
	if (exp.Program())
		cout << "Yes" << endl;
	return 0;
}
