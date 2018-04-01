#include <iostream>
#include <string>
#include "LexiAnalyzer.h"

int main()
{
	LexiAnalyzer exp("text.txt", "out.txt");
	exp.outToF = true;
	exp.run();
	return 0;
}
