#include "LexiAnalyzer.h"
Reserved::Reserved()
{
	for (int i = 0; i < 17; i++)
		idx[ary[i]] = i;//0-index
}
const string Reserved::ary[17] = { "void","var","int","float","string","begin","end","if","then","else","while","do","call","read","write","and","or" };


Symbol::Symbol()
{
	for (int i = ksta; i < ksta + 16; i++)
		kind[ary[i]] = i;
}
const string Symbol::ary[16] = { "{","}","(",")",";","==","=","<","<=",">",">=","<>","+","-","*","/" };

LexiAnalyzer::LexiAnalyzer(string in, string out)
	:fin(ifstream(in)), fout(ofstream(out))
{
	string line;
	while (getline(fin, line))
		fileBuffer.push_back(line);
}

void LexiAnalyzer::run()
{

}

