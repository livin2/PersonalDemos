#include "SyntacAnalyzer.h"

SyntacAnalyzer::SyntacAnalyzer(string in, string out)
	:LeA(in, out), Tcot(1)
{
	tmp.p.x = 0;
	tmp.p.l = 0;
	TACs.push_back(TAC("", "", "", ""));
}

bool SyntacAnalyzer::Program()
{
	if (subProgram())
	{
		while (subProgram())
			;
		return true;
	}
	throwError("expect subProgram", tmp.p);
	return false;
}

bool SyntacAnalyzer::subProgram()
{
	if (!LeA.NextReserved(tmp))
		return false;
	else if (tmp.s != "procedure")
	{
		LeA.buf.retract();
		return false;
	}

	if (!LeA.NextId(tmp))
	{
		throwError("expect id", tmp.p);
		//LeA.buf.retract(); // re def
		return false;
	}

	if (!LeA.NextReserved(tmp))
	{
		throwError("expect end", tmp.p);
		return false;
	}
	else if (tmp.s != "begin")
	{
		LeA.buf.retract();
		return false;
	}

	if (!statementList())
	{
		throwError("expect statementList", tmp.p);
		return false;
	}

	if (!LeA.NextReserved(tmp))
	{
		throwError("expect end", tmp.p);
		return false;
	}
	else if (tmp.s != "end")
	{
		throwError("expect end", tmp.p);
		LeA.buf.retract();
		return false;
	}

	if (!LeA.NextSym(tmp))
	{
		throwError("expect .", tmp.p);
		return false;
	}
	else if (tmp.s != ".")
	{
		throwError("expect .", tmp.p);
		LeA.buf.retract();
		return false;
	}
	return true;
}


bool SyntacAnalyzer::statementList()
{
	if (!statement())
		return false;

	while (LeA.NextSym(tmp))
	{
		if (tmp.s != ";")
		{
			LeA.buf.retract();
			return true;
		}
		if (!statement())
		{
			throwError("expect statement", tmp.p);
			return false;
		}
	}
	return true;
}

bool SyntacAnalyzer::statement()
{
	if (assignStatement())
		return true;
	if (ifStatement())
		return true;
	if (callStatement())
		return true;
	if (compStatement())
		return true;
	if (whileStatement())
		return true;
	if (defStatement())
		return true;
	return true; //空
}

bool SyntacAnalyzer::defStatement()
{
	if (!LeA.NextReserved(tmp))
		return false;
	else if (tmp.s != "def")
	{
		LeA.buf.retract();
		return false;
	}

	if (!LeA.NextId(tmp))
	{
		throwError("expect id", tmp.p);
		//LeA.buf.retract(); // re def
		return false;
	}

	////没分号
	//while(LeA.NextSym(tmp))
	//{
	//	if (tmp.s != ",")
	//	{
	//		LeA.buf.retract(); // re sym
	//		return true;
	//	}
	//	if (!LeA.NextId(tmp))
	//	{
	//		throwError("expect id", tmp.p);
	//		return false;
	//	}
	//}
	//return true;

	//有分号
	while (LeA.NextSym(tmp))
	{
		if (tmp.s != ",")
		{
			if (tmp.s == ";")
				return true;
			else
			{
				throwError("expect ;", tmp.p);
				//LeA.buf.retract(); // re sym
				//LeA.buf.retract(); // re def
				return false;
			}
		}
		if (!LeA.NextId(tmp))
		{
			throwError("expect id", tmp.p);
			//LeA.buf.retract(); // re sym
			//LeA.buf.retract(); // re def
			return false;
		}
	}
	throwError("expect ;", tmp.p);
	return false;
}

bool SyntacAnalyzer::assignStatement()
{
	if (!LeA.NextId(tmp))
		return false;
	string res = tmp.s;

	if (!LeA.NextSym(tmp))
	{
		throwError("expect =", tmp.p);
		//LeA.buf.retract(); // re id
		return false;
	}
	else if (tmp.s != "=")
	{
		throwError("expect =", tmp.p);
		LeA.buf.retract();// re sym
		//LeA.buf.retract();// re id
		return false;
	}

	string arg;
	if (!expression(arg))
	{
		throwError("expect expression", tmp.p);
		//LeA.buf.retract();// re =
		//LeA.buf.retract();// re id
		return false;
	}
	TACs.push_back(TAC("=", arg, "", res));
	return true;
}

bool SyntacAnalyzer::ifStatement()
{
	if (!LeA.NextReserved(tmp))
		return false;
	else if (tmp.s != "if")
	{
		LeA.buf.retract();
		return false;
	}

	if (!LeA.NextSym(tmp))
	{
		throwError("expect (", tmp.p);
		return false;
	}
	else if (tmp.s != "(")
	{
		LeA.buf.retract();
		throwError("expect (", tmp.p);
		return false;
	}
	int T = -1, F = -1, andOr = noFlag;
	if (!boolExpression(T, F, andOr))
	{
		throwError("expect boolExpression", tmp.p);
		return false;
	}

	if (!LeA.NextSym(tmp))
	{
		throwError("expect )", tmp.p);
		return false;
	}
	else if (tmp.s != ")")
	{
		LeA.buf.retract();
		throwError("expect )", tmp.p);
		return false;
	}

	TFfill(T, TACs.size());
	if (!statement())
	{
		throwError("expect statement", tmp.p);
		return false;
	}

	if (!LeA.NextReserved(tmp)) 
	{
		TFfill(F, TACs.size());//无else 下一句是F出口
		return true;
	}
	else if (tmp.s != "else")
	{
		LeA.buf.retract();
		TFfill(F, TACs.size());//无else 下一句是F出口
		return true;
	}
	//有else 应在if后面放个无条件跳出到END出口
	int END = TACs.size();
	TACs.push_back(TAC("jmp", "null", "null", TAC::waitForFill,true));
	//

	TFfill(F, TACs.size());//有else 下一句是F出口
	if (!statement())
	{
		throwError("expect statement", tmp.p);
		return false;
	}

	
	TFfill(END, TACs.size());//END 出口回填
	return true;
}

bool SyntacAnalyzer::whileStatement()
{
	if (!LeA.NextReserved(tmp))
		return false;
	else if (tmp.s != "while")
	{
		LeA.buf.retract();
		return false;
	}

	if (!LeA.NextSym(tmp))
	{
		throwError("expect (", tmp.p);
		return false;
	}
	else if (tmp.s != "(")
	{
		LeA.buf.retract();
		throwError("expect (", tmp.p);
		return false;
	}

	int T = -1, F = -1, andOr = noFlag;
	int STA = TACs.size();//下一句是while TAC开头
	if (!boolExpression(T,F,andOr))
	{
		throwError("expect boolExpression", tmp.p);
		return false;
	}
	if (!LeA.NextSym(tmp))
	{
		throwError("expect )", tmp.p);
		return false;
	}
	else if (tmp.s != ")")
	{
		LeA.buf.retract();
		throwError("expect )", tmp.p);
		return false;
	}

	TFfill(T, TACs.size());
	if (!statement())
	{
		throwError("expect statement", tmp.p);
		return false;
	}
	TACs.push_back(TAC("jmp", "null", "null", STA,true));//while 循环

	TFfill(F, TACs.size());//出口
	return true;
}

bool SyntacAnalyzer::callStatement()
{
	if (!LeA.NextReserved(tmp))
		return false;
	else if (tmp.s != "call")
	{
		LeA.buf.retract();
		return false;
	}
	if (!LeA.NextId(tmp))
	{
		throwError("expect id", tmp.p);
		return false;
	}
	return true;
}

bool SyntacAnalyzer::compStatement()
{
	if (!LeA.NextReserved(tmp))
		return false;
	else if (tmp.s != "begin")
	{
		LeA.buf.retract();
		return false;
	}

	if (!statementList())
	{
		throwError("expect statementList", tmp.p);
		return false;
	}

	if (!LeA.NextReserved(tmp))
	{
		throwError("expect end", tmp.p);
		return false;
	}
	else if (tmp.s != "end")
	{
		throwError("expect end", tmp.p);
		LeA.buf.retract();
		return false;
	}
	return true;
}

bool SyntacAnalyzer::expression()
{
	if (term())
	{
		//Token tmp;
		while (LeA.NextSym(tmp))
		{
			if (tmp.s != "+" && tmp.s != "-")
			{
				LeA.buf.retract();
				return true;
			}
			if (!term())
			{
				throwError("expect term", tmp.p);
				return false;
			}
		}
		return true;
	}
	return false;
}

bool SyntacAnalyzer::expression(string &T)
{
	string arg;
	if (term(arg))
	{
		//Token tmp;
		while (LeA.NextSym(tmp))
		{
			if (tmp.s != "+" && tmp.s != "-")
			{
				LeA.buf.retract();
				T = arg;
				return true;
			}
			string op = tmp.s;

			string arg2;
			if (!term(arg2))
			{
				throwError("expect term", tmp.p);
				return false;
			}
			else
			{
				string res = "T" + to_string(Tcot++);
				TACs.push_back(TAC(op, arg, arg2, res));
				arg = res;
			}
		}
		T = arg;
		return true;
	}
	return false;
}

bool SyntacAnalyzer::term()
{
	if (factor())
	{
		//Token tmp;
		while (LeA.NextSym(tmp))
		{
			if (tmp.s != "*" && tmp.s != "/")
			{
				LeA.buf.retract();
				return true;
			}
			if (!factor())
			{
				throwError("expect factor", tmp.p);
				return false;
			}
		}
		return true;
	}
	return false;
}

bool SyntacAnalyzer::term(string &T)
{
	string arg;
	if (factor(arg))
	{
		//Token tmp;
		while (LeA.NextSym(tmp))
		{
			if (tmp.s != "*" && tmp.s != "/")
			{
				LeA.buf.retract();
				T = arg;
				return true;
			}
			string op = tmp.s;

			string arg2;
			if (!factor(arg2))
			{
				throwError("expect factor", tmp.p);
				return false;
			}
			else
			{
				string res = "T" + to_string(Tcot++);
				TACs.push_back(TAC(op, arg, arg2, res));
				arg = res;
			}
		}
		T = arg;
		return true;
	}
	return false;
}

bool SyntacAnalyzer::factor()
{
	//Token tmp;
	if (LeA.NextDig(tmp))
		return true;
	if (LeA.NextId(tmp))
		return true;
	if (LeA.NextSym(tmp))
	{
		if (tmp.s != "(")
		{
			LeA.buf.retract();
			return false;
		}
		if (!expression())
		{
			throwError("expect expression", tmp.p);
			return false;
		}
		if (!LeA.NextSym(tmp))
		{
			throwError("expect )", tmp.p);
			return false;
		}
		else if (tmp.s != ")")
		{
			LeA.buf.retract();
			throwError("expect )", tmp.p);
			return false;
		}
		return true;
	}
	return false;
}

bool SyntacAnalyzer::factor(string & T)
{
	//Token tmp;
	if (LeA.NextDig(tmp))
	{
		T = tmp.s;
		return true;
	}
	if (LeA.NextId(tmp))
	{
		T = tmp.s;
		return true;
	}
	//
	string Texpre;
	if (LeA.NextSym(tmp))
	{
		if (tmp.s != "(")
		{
			LeA.buf.retract();
			return false;
		}
		if (!expression(Texpre))
		{
			throwError("expect expression", tmp.p);
			return false;
		}
		if (!LeA.NextSym(tmp))
		{
			throwError("expect )", tmp.p);
			return false;
		}
		else if (tmp.s != ")")
		{
			LeA.buf.retract();
			throwError("expect )", tmp.p);
			return false;
		}
		T = Texpre;
		return true;
	}
	return false;
}

bool SyntacAnalyzer::boolExpression()
{
	if (!relaExpression())
		return false;
	while (LeA.NextReserved(tmp))
	{
		if (tmp.s != "and" && tmp.s != "or")
		{
			LeA.buf.retract();
			return true;
		}
		if (!boolExpression())
		{
			throwError("expect boolExpression after and/or", tmp.p);
			return false;
		}
	}
	return true;
}

bool SyntacAnalyzer::boolExpression(int &T, int &F, int &andOr)
{
	if (!relaExpression(T, F, andOr))
		return false;
	while (LeA.NextReserved(tmp))
	{
		if (tmp.s == "and")
			andOr = andFlag;
		else if (tmp.s == "or")
			andOr = orFlag;
		else
		{
			LeA.buf.retract();
			andOr = noFlag;
			return true;
		}

		if (!boolExpression(T, F, andOr))
		{
			throwError("expect boolExpression after and/or", tmp.p);
			return false;
		}
	}
	return true;
}

bool SyntacAnalyzer::relaExpression()
{
	if (!expression())
		return false;

	if (!relaSymbol())
	{
		throwError("expect relaSymbol", tmp.p);
		return false;
	}

	if (!expression())
	{
		throwError("expect expression", tmp.p);
		return false;
	}

	return true;
}

bool SyntacAnalyzer::relaExpression(int & T, int & F, int &andOr)
{
	string arg1, arg2, op;
	if (!expression(arg1))
		return false;

	if (!relaSymbol())
	{
		throwError("expect relaSymbol", tmp.p);
		return false;
	}
	op = tmp.s;

	if (!expression(arg2))
	{
		throwError("expect expression", tmp.p);
		return false;
	}

	int thsT = TACs.size();
	int thsF = thsT + 1;
	TACs.push_back(TAC("j" + op, arg1, arg2, TAC::waitForFill));
	TACs.push_back(TAC("jmp", "null", "null", TAC::waitForFill));
	if (andOr == andFlag)
	{
		TFfill(T, thsT);
		if (F != -1)
			TACs[thsF].jmpRes = F;
	}
	else if (andOr == orFlag)
	{
		TFfill(F, thsT);
		if (T != -1)
			TACs[thsT].jmpRes = T;
	}
	T = thsT; F = thsF;
	return true;
}

bool SyntacAnalyzer::relaSymbol()
{
	//Token tmp;
	if (LeA.NextSym(tmp))
	{
		if (tmp.kind >= Symbol::relaSta && tmp.kind <= Symbol::relaEnd)
			return true;
	}
	return false;
}

void SyntacAnalyzer::throwError(const string & msg, pos & p)
{
	cout << "Error " << msg << " (" << p.l << "," << p.x << ")" << endl;
}

void SyntacAnalyzer::TACoutput()
{
	for (int i = 1; i < TACs.size(); i++)
	{
		cout << setw(3) << i << ", "
			<< setw(3) << TACs[i].op << ", "
			<< setw(5) << TACs[i].arg1 << ", "
			<< setw(5) << TACs[i].arg2 << ", ";
		if (TACs[i].jmpRes == -1)
			cout << setw(3) << TACs[i].res << endl;
		else
			cout << setw(3) << TACs[i].jmpRes << endl;
	}

}

void SyntacAnalyzer::TACoutput(fstream & fout)
{
	for (int i = 1; i < TACs.size(); i++)
	{
		fout << setw(3) << i << ", "
			<< setw(3) << TACs[i].op << ", "
			<< setw(5) << TACs[i].arg1 << ", "
			<< setw(5) << TACs[i].arg2 << ", ";
		if (TACs[i].jmpRes == -1)
			fout << setw(3) << TACs[i].res << endl;
		else
			fout << setw(3) << TACs[i].jmpRes << endl;
	}
}

void SyntacAnalyzer::TFfill(int TF, int toFill)
{
	if (TF != -1)
	{
		if (TACs[TF].jmpRes != TAC::waitForFill)
			TFfill(TACs[TF].jmpRes, toFill);
		TACs[TF].jmpRes = toFill;
	}
	return;
}

void SyntacAnalyzer::outMerge()
{
	for (int i = 1; i < TACs.size(); i++)
	{
		int jr = TACs[i].jmpRes;
		TACs[i].jmpRes = outMerge(jr);
	}
}

int SyntacAnalyzer::outMerge(int jp)
{
	if (jp>0&&jp<TACs.size()&&TACs[jp].unlimjmp)
	{
		jp = TACs[jp].jmpRes;
		return TACs[jp].jmpRes = outMerge(jp);
	}
	return jp;
}

TAC::TAC(string op, string arg1, string arg2, string res)
	:op(op), arg1(arg1), arg2(arg2), res(res), jmpRes(-1), unlimjmp(false)
{
}

TAC::TAC(string op, string arg1, string arg2, int jmpRes)
	: op(op), arg1(arg1), arg2(arg2), jmpRes(jmpRes), unlimjmp(false)
{
}

TAC::TAC(string op, string arg1, string arg2, int jmpRes, bool unlimjmp)
	: op(op), arg1(arg1), arg2(arg2), jmpRes(jmpRes), unlimjmp(unlimjmp)
{
}

//bool SyntacAnalyzer::ifStatement()
//{
//	if (!LeA.NextReserved(tmp))
//		return false;
//	else if (tmp.s != "if")
//	{
//		LeA.buf.retract();
//		return false;
//	}
//
//	if (!LeA.NextSym(tmp))
//	{
//		throwError("expect (", tmp.p);
//		return false;
//	}
//	else if (tmp.s != "(")
//	{
//		LeA.buf.retract();
//		throwError("expect (", tmp.p);
//		return false;
//	}
//	if (!boolExpression())
//	{
//		throwError("expect boolExpression", tmp.p);
//		return false;
//	}
//	if (!LeA.NextSym(tmp))
//	{
//		throwError("expect )", tmp.p);
//		return false;
//	}
//	else if (tmp.s != ")")
//	{
//		LeA.buf.retract();
//		throwError("expect )", tmp.p);
//		return false;
//	}
//
//	if (!statement())
//	{
//		throwError("expect statement", tmp.p);
//		return false;
//	}
//
//	if (!LeA.NextReserved(tmp))
//		return true;
//	else if (tmp.s != "else")
//	{
//		LeA.buf.retract();
//		return true;
//	}
//
//	if (!statement())
//	{
//		throwError("expect statement", tmp.p);
//		return false;
//	}
//
//	return true;
//}

//bool SyntacAnalyzer::assignStatement()
//{
//	if (!LeA.NextId(tmp))
//		return false;
//
//	if (!LeA.NextSym(tmp))
//	{
//		throwError("expect =", tmp.p);
//		//LeA.buf.retract(); // re id
//		return false;
//	}
//	else if (tmp.s != "=")
//	{
//		throwError("expect =", tmp.p);
//		LeA.buf.retract();// re sym
//						  //LeA.buf.retract();// re id
//		return false;
//	}
//
//	if (!expression())
//	{
//		throwError("expect expression", tmp.p);
//		//LeA.buf.retract();// re =
//		//LeA.buf.retract();// re id
//		return false;
//	}
//	return true;
//}

//bool SyntacAnalyzer::whileStatement()
//{
//	if (!LeA.NextReserved(tmp))
//		return false;
//	else if (tmp.s != "while")
//	{
//		LeA.buf.retract();
//		return false;
//	}
//
//	if (!LeA.NextSym(tmp))
//	{
//		throwError("expect (", tmp.p);
//		return false;
//	}
//	else if (tmp.s != "(")
//	{
//		LeA.buf.retract();
//		throwError("expect (", tmp.p);
//		return false;
//	}
//	if (!boolExpression())
//	{
//		throwError("expect boolExpression", tmp.p);
//		return false;
//	}
//	if (!LeA.NextSym(tmp))
//	{
//		throwError("expect )", tmp.p);
//		return false;
//	}
//	else if (tmp.s != ")")
//	{
//		LeA.buf.retract();
//		throwError("expect )", tmp.p);
//		return false;
//	}
//
//	if (!statement())
//	{
//		throwError("expect statement", tmp.p);
//		return false;
//	}
//	return true;
//}