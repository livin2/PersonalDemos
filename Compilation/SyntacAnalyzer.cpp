#include "SyntacAnalyzer.h"

SyntacAnalyzer::SyntacAnalyzer(string in, string out)
	:LeA(in,out)
{
	tmp.p.x = 0;
	tmp.p.l = 0;
}

bool SyntacAnalyzer::Program()
{
	if (subProgram())
	{
		while (subProgram())
			;
		return true;
	}
	throwError("expect subProgram",tmp.p);
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
	else if (tmp.s!=".")
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

	if (!expression())
	{
		throwError("expect expression", tmp.p);
		//LeA.buf.retract();// re =
		//LeA.buf.retract();// re id
		return false;
	}
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
	if (!boolExpression())
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

	if (!statement())
	{
		throwError("expect statement", tmp.p);
		return false;
	}

	if (!LeA.NextReserved(tmp))
		return true;
	else if (tmp.s != "else")
	{
		LeA.buf.retract();
		return true;
	}

	if (!statement())
	{
		throwError("expect statement", tmp.p);
		return false;
	}

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
	if (!boolExpression())
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

	if (!statement())
	{
		throwError("expect statement", tmp.p);
		return false;
	}
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
		}else if(tmp.s != ")")
		{
			LeA.buf.retract();
			throwError("expect )", tmp.p);
			return false;
		}
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
	cout << "Error " << msg << " (" << p.l << "," << p.x <<")" << endl;
}
