#include "LexiAnalyzer.h"
Reserved::Reserved()
{
	for (int i = 0; i < 17; i++)
		idx[ary[i]] = i;//0-index
}
int Reserved::search(const string & str)
{
	if (idx.find(str) != idx.end())
		return idx[str];
	return -1;
}
const string Reserved::ary[17] = { "void","var","int","float","string","begin","end","if","then","else","while","do","call","read","write","and","or" };


int Symbol::search(const string & str)
{
	if (kind.find(str) != kind.end())
		return kind[str];
	return -1;
}

Symbol::Symbol()
{
	for (int i = 0; i <16; i++)
		kind[ary[i]] = i+ ksta;
}
const string Symbol::ary[16] = { "{","}","(",")",";","==","=","<","<=",">",">=","<>","+","-","*","/" };

pos::pos(int ll, int xx)
	:x(xx), l(ll)
{
}