#include "LexiAnalyzer.h"
Reserved::Reserved()
{
	for (int i = 0; i < 10; i++)
		idx[ary[i]] = i;//0-index
}
int Reserved::search(string str)
{
	transform(str.begin(),str.end(), str.begin(), ::tolower);
	if (idx.find(str) != idx.end())
		return idx[str];
	return -1;
}
const string Reserved::ary[10] = { "procedure","def","if","else","while","call","begin","end","and","or" };


int Symbol::search(const string & str)
{
	if (kind.find(str) != kind.end())
		return kind[str];
	return -1;
}

Symbol::Symbol()
{
	for (int i = 0; i <18; i++)
		kind[ary[i]] = i+ ksta;
}
const string Symbol::ary[18] = { "{","}","(",")",";","=","==","<","<=",">",">=","<>","+","-","*","/","," ,"."};

pos::pos(int ll, int xx)
	:x(xx), l(ll)
{
}