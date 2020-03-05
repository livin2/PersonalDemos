#include "FileSystem.h"

FileSystem::FileSystem()
	:ram(100,50),dev(500,200)
{
	out = &cout;
	in = &cin;
}

FileSystem::FileSystem(const string &devF)
	:ram(100, 50), dev(devF)
{
	out = &cout;
	in = &cin;
}

//FileSystem::FileSystem(const string &devFs, const string& ramFs)
//	:devF(NULL),ramF(NULL), ram(100, 50), dev(500, 200), devFs(devFs), ramFs(ramFs)
//{
//	out = &cout;
//	in = &cin;
//
//	int Dop = fopen_s(&devF, devFs.c_str(), "rb+");
//	int Rop = fopen_s(&ramF, ramFs.c_str(), "rb+");
//	if (!Dop)
//		 fread(&dev, sizeof(Dev), 1, devF);
//	else
//		Dop = fopen_s(&devF, devFs.c_str(), "wb+");
//
//	if (!Rop)
//		fread(&ram, sizeof(RAM), 1, ramF);
//	else
//		Rop = fopen_s(&ramF, ramFs.c_str(), "wb+");
//
//	fclose(devF);
//	fclose(ramF);
//
//	dev.curMenu = &dev.rootMenu;
//}

bool FileSystem::writeBack(int ri)
{
	inode &from = ram.FileNodes[ri];
	diNode &to = dev.FileNodes[from.indexOfdev];
	if (to.fileSize + dev.blockFree < from.fileSize)
	{
		*out << "Failed: dev no free block\n";
		return false;							//DEV空间不足
	}

	auto itf = from.addr.begin();
	auto itt = to.addr.begin();
	for ( ; itf != from.addr.end(); itf++,itt++)
	{
		if (itt!=to.addr.end())
		{
			dev.DATA[*itt] = ram.DATA[*itf];
		}
		else
		{
			int bkALL = dev.BlockAlloc();
			if (bkALL == dev.AllocFailed)
			{
				*out << "Failed: dev blockAlloc failed\n";
				return false;					//块分配失败
			}
			to.addr.push_back(bkALL);
			itt = to.addr.end(); itt--;

			dev.DATA[bkALL] = ram.DATA[*itf];

		}
	}

	//释放碎片
	auto delBegin = itt;
	while (itt!= to.addr.end())
	{
		dev.BlockFree(*itt);
		itt++;
	}
	to.addr.erase(delBegin, to.addr.end());
	to.fileSize = to.addr.size();
	return true;
}

bool FileSystem::open(const string & name)
{
	if (dev.openFolder(name))
	{
		*out << "opened\n";
		return true;
	}
	
	Menu *curMenu = &(dev.menuData[dev.curMenui]);
	auto itf = curMenu->fileMenu.find(name);
	if (itf == curMenu->fileMenu.end())
	{
		*out << "Failed: file cannot be found in current dir\n";
		return false;
	}

	int di = itf->second;
	diNode& diN = dev.FileNodes[di];

	auto oped = ram.fileOpen.find(name);		//文件已经在内存里打开
	if (oped != ram.fileOpen.end())
	{
		inode &ioped = ram.FileNodes[oped->second];
		ioped.canWrite = curUser == diN.owner;
		*out << "opened\n";
		return true;
	}

	if (diN.fileSize > ram.blockFree)
	{
		*out << "Failed: ram no free block\n";
		return false;							//空间不够
	}


	//inode * riNt;
	//int ri;
	//if (!ram.emptyNode.empty())
	//{
	//	int riTmp = ram.emptyNode.front();
	//	riNt = &(ram.FileNodes[riTmp]);
	//	ri = riTmp;
	//	ram.emptyNode.pop();
	//}
	//else
	//{
	//	riNt = new inode;
	//	ri = ram.FileNodes.size();			
	//}
	int ri = ram.NodeAlloc();				//新分配一个索引结点
	if (ri == ram.AllocFailed)
	{
		*out << "Failed: ram nodeAlloc failed\n";
		return false;
	}
	inode& riNt = ram.FileNodes[ri];
	riNt.fileSize = diN.fileSize;
	riNt.owner = diN.owner;
	riNt.canWrite = curUser == diN.owner;
	riNt.indexOfdev = di;
	ram.FileNodes.push_back(riNt);
	inode& riN = ram.FileNodes[ri];


	list<int>::iterator it = diN.addr.begin();
	for (; it != diN.addr.end(); it++)
	{
		int bkAll = ram.BlockAlloc();			//分配块
		if (bkAll == ram.AllocFailed)
		{
			ram.NodeFree(ri);
			*out << "Failed: ram bolckAlloc failed\n";
			return false;						//分配块失败
		}
		ram.DATA[bkAll] = dev.DATA[*it];		//拷贝块
		riN.addr.push_back(bkAll);				//链接块到索引节点
	}

	ram.fileOpen[name] = ri;
	*out << "opened\n";
	return true;
}

void FileSystem::close(const string & name)
{
	auto it = ram.fileOpen.find(name);
	if (it == ram.fileOpen.end())
	{
		*out << "closed\n";
		return;
	}

	bool wb = writeBack(it->second);
	if (!wb)
		*out << "warning:dev no more space\n";

	ram.NodeFree(it->second);
	ram.fileOpen.erase(it);
	*out << "closed\n";
	return;
}

void FileSystem::close()
{
	dev.closeFolder();
	*out << "closed\n";
}

void FileSystem::closeAll()
{

	auto it = ram.fileOpen.begin();
	bool wb = true;
	for (; it != ram.fileOpen.end(); it++)
	{
		if (!wb)
			continue;
		else
		{
			wb = writeBack(it->second);
			if(!wb)
				*out << "warning:dev no more space\n";
		}

		ram.NodeFree(it->second);
		
	}
	ram.fileOpen.clear();
	*out << "closed\n";
}

bool FileSystem::read(const string &name)
{
	Menu *curMenu = &(dev.menuData[dev.curMenui]);
	if (ram.fileOpen.find(name) == ram.fileOpen.end())
	{
		if (curMenu->fileMenu.find(name) == curMenu->fileMenu.end())
		{
			*out << "Failed: file cannot be found\n";
			return false;
		}
		else
		{
			if (!open(name))
				return false;
		}
	}

	inode& riN = ram.FileNodes[ram.fileOpen[name]];

	auto it = riN.addr.begin();
	for ( ; it != riN.addr.end(); it++)
	{
		Block &ths = ram.DATA[*it];
		//*out <<"Block:"<<*it<<endl;
		for(size_t i=0;i<Block::BlockSize;i++)
			*out<<ths.D[i];				//
		//*out << endl;
	}
	*out << endl;
	return true;
}

bool FileSystem::write(const string & name)
{
	auto it = ram.fileOpen.find(name);
	if (it == ram.fileOpen.end())
	{
		*out << "Failed: file cannot be found in RAM\n";
		return false;
	}

	inode &thsf = ram.FileNodes[it->second];
	if (!thsf.canWrite && thsf.owner!=curUser)
	{
		*out << "Failed: no premisson\n";
		return false;
	}

	auto bk = thsf.addr.begin();
	int cot = 0;char ch;
	while (in->get(ch)&&ch!=writeEnd)
	{
		if (cot == Block::BlockSize)
		{	bk++; cot = 0;}

		if (bk == thsf.addr.end())				//分配新的block结点
		{
			int ri = ram.BlockAlloc();
			if (ri == RAM::AllocFailed)
			{
				*out << "Failed: ram bolckAlloc failed\n";
				return false;					//内存满了
			}

			thsf.addr.push_back(ri);
			thsf.fileSize++;
			bk = thsf.addr.end();
			bk--;
		}
		if (cot == 0)
			ram.DATA[*bk].clear();
		ram.DATA[*bk].D[cot] = ch;
		cot++;
	}

	bk++;
	if (bk !=  thsf.addr.end())
	{
		//释放碎片
		auto delBegin = bk;
		while (bk != thsf.addr.end())
		{
			dev.BlockFree(*bk);
			bk++;
		}
		thsf.addr.erase(delBegin, thsf.addr.end());
		thsf.fileSize = thsf.addr.size();

	}
	return true;
}

bool FileSystem::writeAll()
{
	auto it = ram.fileOpen.begin();
	bool res = true;
	for (; it != ram.fileOpen.end(); it++)
	{
		res = writeBack(it->second);
		if (!res)
			return false;
	}
	return true;
}

bool FileSystem::newFile(const string & name)
{
	return dev.newFile(name,*out,curUser);
}

bool FileSystem::deleteFile(const string & name)
{
	return dev.deleteFile(name,*out);
}

bool FileSystem::newFolder(const string & name)
{
	return dev.newFolder(name,*out);
}

bool FileSystem::deleteFolder(const string & name)
{
	return dev.deleteFolder(name,*out);
}

bool FileSystem::checkUser(const string & name)
{
	Menu *curMenu = &(dev.menuData[dev.curMenui]);
	auto it = curMenu->fileMenu.find(name);
	if (it == curMenu->fileMenu.end())
	{
		*out << "Failed: file cannot be found\n";
		return false;
	}

	*out << dev.FileNodes[it->second].owner<<endl;
	return true;
}

void FileSystem::login()
{
	*out << "user name:";
	*in >> curUser;
}

//void FileSystem::saveToFile()
//{
//	/*rewind(devF);
//	rewind(ramF);*/
//	//fclose(devF);
//	//fclose(ramF);
//	int Dop = fopen_s(&devF, devFs.c_str(), "wb+");
//	int Rop = fopen_s(&ramF, ramFs.c_str(), "wb+");
//	fwrite(&dev, sizeof(Dev), 1, devF);
//	fwrite(&ram, sizeof(RAM), 1, ramF);
//}

void FileSystem::run(ostream &devO,ostream &ramO)
{
	*out << ">:login" << endl;
	login();

	string cmd,str;
	while (true)
	{
		*out << ">";
		*in >> cmd;
		if (cmd[0] != ':') {
			*out << "type ':help' to get help" << endl;
			continue;
		}
		if (cmd == ":q")
		{
			writeAll();
			while (!dev.preMenui.empty())
				dev.preMenui.pop();
			dev.output(devO);
			ram.output(ramO);
			dev.CLOSE();
			return;
		}
		else if (cmd == ":login")
			login();
		else if (cmd == ":help")
		{
			*out << ":dir\t:q\t:\\" << endl;
			*out << ":open\t:close\t:back" << endl;
			*out << ":read\t:write\t:login" << endl;
			*out << ":newFile\t:deleteFile\t:check" << endl;
			*out << ":newFolder\t:deleteFolder" << endl;
			*out << ":devInfo\t:ramInfo" << endl;
		}
		else if (cmd ==":devInfo")
		{
			dev.output(devO);
			*out << "devInfo is saved to file" << endl;
		}
		else if (cmd == ":ramInfo")
		{
			ram.output(ramO);
			*out << "ramInfo is saved to file" << endl;
		}
		else if (cmd == ":dir")
		{
			Menu *curMenu = &(dev.menuData[dev.curMenui]);
			curMenu->output(*out, "\t", dev.menuData);
		}
		else if (cmd == ":open")
		{
			*out << "file/folder:";
			*in >> str;
			open(str);
		}
		else if (cmd == ":close")
		{
			*out << "file:";
			*in >> str;
			close(str);
		}
		else if (cmd == ":read")
		{
			*out << "file:";
			*in >> str;
			read(str);
		}
		else if (cmd == ":write")
		{
			*out << "file:";
			*in >> str;
			write(str);
		}
		else if (cmd == ":back")
		{
			close();
		}
		else if (cmd == ":/")
		{
			dev.backToRoot();
		}
		else if (cmd == ":newFile")
		{
			*out << "file:";
			*in >> str;
			newFile(str);
		}
		else if (cmd == ":check")
		{
			*out << "file:";
			*in >> str;
			checkUser(str);
		}
		else if (cmd == ":deleteFile")
		{
			*out << "file:";
			*in >> str;
			deleteFile(str);
		}
		else if (cmd == ":newFolder")
		{
			*out << "folder:";
			*in >> str;
			newFolder(str);
		}
		else if (cmd == ":deleteFolder")
		{
			*out << "folder:";
			*in >> str;
			deleteFolder(str);
		}

	}
}


void Block::operator=(const Block & b)
{
	for (int i = 0; i < BlockSize; i++)
		D[i] = b.D[i];
}

void Menu::output(ostream & out,const string &pre, vector<Menu>& vMenu)
{
	auto it = fileMenu.begin();
	for (; it != fileMenu.end(); it++) {
		out << pre << "-" << it->first << "\t\t idx:" << it->second << endl;
	}
	auto itF = folder.begin();
	for (; itF != folder.end(); itF++)
	{
		out << pre << "-" << itF->first << endl;
		vMenu[itF->second].output(out,pre+"\t",vMenu);
	}
	
}
