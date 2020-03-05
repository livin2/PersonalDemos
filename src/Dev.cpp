#include "FileSystem.h"

Dev::Dev(int blockSize, int nodeSize)
	:blockSize(blockSize), DATA(blockSize), nodeSize(nodeSize), FileNodes(nodeSize), menuData(1)
{
	blockFree = blockSize;
	nodeFree = nodeSize;
	for (int i = 0; i < blockSize; i++)
		emptyBlock.push(i);

	for (int i = 0; i < nodeSize; i++)
		emptyNode.push(i);

	rootMenui = 0;
	curMenui = rootMenui;
	devF = "DEVF";
}



int Dev::BlockAlloc()
{
	if (!emptyBlock.empty())
	{
		int res = emptyBlock.front();
		emptyBlock.pop();
		blockFree--;
		return res;
	}
	return AllocFailed;
}

void Dev::BlockFree(int i)
{
	emptyBlock.push(i);
	blockFree++;
	return;
}

int Dev::NodeAlloc()
{
	if (!emptyNode.empty())
	{
		int ni = emptyNode.front();
		int blk = BlockAlloc();
		if (blk == AllocFailed)
			return AllocFailed;

		FileNodes[ni].addr.push_back(blk);
		FileNodes[ni].fileSize = 1;

		emptyNode.pop();
		nodeFree--;
		return ni;
	}
	return AllocFailed;
}

void Dev::NodeFree(int i)
{
	auto it = FileNodes[i].addr.begin();
	for (; it != FileNodes[i].addr.end(); it++)
		BlockFree(*it);

	FileNodes[i].addr.clear();
	emptyNode.push(i);
	nodeFree++;
	return;
}

bool Dev::newFile(const string & name,ostream& out, const string & curUser)
{
	Menu *curMenu = &menuData[curMenui];
	if (curMenu->fileMenu.find(name) != curMenu->fileMenu.end())
	{
		out << "Failed: file exist\n";
		return false;//重名
	}
	if (curMenu->folder.find(name) != curMenu->folder.end())
	{
		out << "Failed: folder exist\n";
		return false;//重名
	}

	int ni = NodeAlloc();
	if (ni == AllocFailed)
	{
		out << "Failed: dev nodeAlloc failed\n";
		return false;//满了
	}

	curMenu->fileMenu[name] = ni;
	FileNodes[ni].owner = curUser;
	out << "success\n";
	return true;
}

bool Dev::deleteFile(const string & name, ostream& out)
{
	Menu *curMenu = &menuData[curMenui];
	auto it = curMenu->fileMenu.find(name);
	if (it == curMenu->fileMenu.end())
	{
		out << "Failed: file cannot be found\n";
		return false;
	}

	NodeFree(it->second);
	curMenu->fileMenu.erase(it);
	return true;
}

bool Dev::newFolder(const string & name, ostream& out)
{
	Menu *curMenu = &menuData[curMenui];
	if (curMenu->folder.find(name) != curMenu->folder.end())
	{
		out << "Failed: folder exist\n";
		return false;//重名
	}
	if (curMenu->fileMenu.find(name) != curMenu->fileMenu.end())
	{
		out << "Failed: file exist\n";
		return false;//重名
	}

	menuData.push_back(Menu());
	curMenu = &menuData[curMenui];
	curMenu->folder[name] = menuData.size()-1;
	return true;
}

bool Dev::deleteFolder(const string & name, ostream& out)
{
	Menu *curMenu = &menuData[curMenui];
	auto it = curMenu->folder.find(name);
	if (it == curMenu->folder.end())
	{
		out << "Failed: folder cannot be found\n";
		return false;
	}
	
	for (auto j = curMenu->folder.begin(); j != curMenu->folder.end(); j++)
		deleteFolder(j->first, out);

	for (auto j = curMenu->fileMenu.begin(); j != curMenu->fileMenu.end(); j++)
		deleteFile(j->first, out);

	curMenu->folder.erase(it);
	return true;
}

void Dev::backToRoot()
{

	curMenui = rootMenui;
	while (!preMenui.empty())
		preMenui.pop();
}

void Dev::output(ostream & out)
{
	out.seekp(0);
	Menu *rootMenu = &menuData[rootMenui];
	out << "-----DEV INFO-----" << endl;
	out << "blockSize\t\t\t" << blockSize << endl;
	out << "blockFree\t\t\t" << blockFree << endl;

	out << "nodeSize\t\t\t" << nodeSize << endl;
	out << "nodeFree\t\t\t" << nodeFree << endl;

	out << endl;
	out << "dir" << endl;
	rootMenu->output(out, "", menuData);
	out << "idx-owner-block" << endl;
	for (int i = 0; i < FileNodes.size(); i++) {
		diNode& diN = FileNodes[i];
		if (diN.fileSize == 0)
			break;
		out << i << "\t" << diN.owner<<"\t";
		for (auto it = diN.addr.begin(); it != diN.addr.end(); it++){
			out << *it << " ";
		}
		out << endl;
	}

	out << endl;
	out << "data" << endl;
	DATAoutput(out);

}

void Dev::DATAoutput(ostream & out)
{
	for (int i = 0; i < DATA.size(); i++)
	{
		out << setw(3) << i << " ";
		for (int j = 0; j < Block::BlockSize; j++)
		{
			char ch = DATA[i].D[j] ? DATA[i].D[j] : '_';
			if (ch == '\t')
				out << "\\t";
			else if (ch == '\n')
				out << "\\n";
			else
				out << ch;
		}
		out << endl;
	}
}

void Dev::CLOSE()
{
	fstream fs(devF);
	//block
	fs << blockSize << " " << blockFree << endl;
	while (!emptyBlock.empty())
	{
		fs << emptyBlock.front() << " ";
		emptyBlock.pop();
	}
	fs << endl;
	//node
	fs << nodeSize << " " << nodeFree << endl;
	while (!emptyNode.empty())
	{
		fs << emptyNode.front() << " ";
		emptyNode.pop();
	}
	fs << endl;

	//Menu
	fs << menuData.size() << endl;
	for (int i = 0; i < menuData.size(); i++)
	{
		fs << menuData[i].fileMenu.size() << endl;
		for (auto j = menuData[i].fileMenu.begin(); j != menuData[i].fileMenu.end(); j++)
			fs << j->first << " "<<j->second << endl;

		fs << menuData[i].folder.size() << endl;
		for (auto j = menuData[i].folder.begin(); j != menuData[i].folder.end(); j++)
			fs << j->first << " " << j->second << endl;
	}

	//FileNodes
	fs << FileNodes.size() << endl;
	for (int i = 0; i < FileNodes.size(); i++)
	{
		fs << FileNodes[i].fileSize << " " << FileNodes[i].owner << endl;
		for (auto j = FileNodes[i].addr.begin(); j != FileNodes[i].addr.end(); j++)
			fs << *j << " ";
		fs << endl;
	}

	//DATA
	fs << DATA.size() << endl;
	for (int i = 0; i < DATA.size(); i++)
	{
		for (int j = 0; j < Block::BlockSize; j++)
		{
			if (DATA[i].D[j]!=0)
				fs << DATA[i].D[j];
			else
				fs << '$';
			
		}
	}


}
Dev::Dev(const string &devF) :devF(devF)
{
	fstream fs(devF);
	if (!fs)
	{
		fs.open(devF, fstream::out);
		fs.close();
		this->Dev::Dev(500, 200);
		return;
	}
	//block
	fs >> blockSize >> blockFree;
	for (int i = 0; i < blockFree; i++)
	{
		int tmp;
		fs >> tmp;
		emptyBlock.push(tmp);
	}

	//node
	fs >> nodeSize >> nodeFree;
	for (int i = 0; i < nodeFree; i++)
	{
		int tmp;
		fs >> tmp;
		emptyNode.push(tmp);
	}

	//Menu
	int menuSize;
	fs >> menuSize;
	menuData = vector<Menu>(menuSize);
	for (int i = 0; i < menuSize; i++)
	{
		int fileSize, folderSize,second;
		string first;
		fs >> fileSize;
		for (int j = 0; j < fileSize; j++)
		{
			fs >> first >> second;
			menuData[i].fileMenu[first] = second;
		}

		fs >> folderSize;
		for (int j = 0; j < folderSize; j++)
		{
			fs >> first >> second;
			menuData[i].folder[first] = second;
		}
	}

	//FileNodes
	int FileNodesSize;
	fs >> FileNodesSize;
	FileNodes = vector<diNode>(FileNodesSize);
	for (int i = 0; i < FileNodesSize; i++)
	{
		int fileSize; string owner;
		fs >> fileSize >> owner;
		FileNodes[i].fileSize = fileSize;
		FileNodes[i].owner = owner;
		for (int j = 0; j < fileSize; j++)
		{
			int btmp; fs >> btmp;
			FileNodes[i].addr.push_back(btmp);
		}
	}

	//DATA
	int DATASize;
	fs >> DATASize;
	fs.get();//get \n
	DATA = vector<Block>(DATASize);
	for (int i = 0; i < DATASize; i++)
	{
		for (int j = 0; j < Block::BlockSize; j++)
		{
			fs.get(DATA[i].D[j]);
			if (DATA[i].D[j] == '$')
				DATA[i].D[j] = 0;
		}
		//fs.get();//\n
	}

	

	rootMenui = 0;
	curMenui = 0;
}

bool Dev::openFolder(const string & name)
{
	Menu *curMenu = &menuData[curMenui];
	auto it = curMenu->folder.find(name);
	if (it == curMenu->folder.end())
		return false;

	preMenui.push(curMenui);
	curMenui = it->second;
	return true;
}

bool Dev::closeFolder()
{
	if (!preMenui.empty())
	{
		curMenui = preMenui.top();
		preMenui.pop();
		return true;
	}
	curMenui = rootMenui;
	return true;
}
