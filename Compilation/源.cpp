#include <iostream>
#include <string>
#include "LexiAnalyzer.h"

int main()
{
	//LexiAnalyzer exp("text.txt", "out.txt");
	ifstream in("text.txt");
	fileBuffer exp(in);
	string s; pos p;
	while (exp.getNext(s,p))
	{
		cout << s <<" "<<p.l<<" "<<p.x<< endl;
	}
	cout << endl;
	return 0;
}