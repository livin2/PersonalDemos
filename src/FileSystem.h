#pragma once

#include <string>
#include <vector>
#include <queue>
#include <list>
#include <map>
#include <stack>
#include <iostream>
#include <iomanip>
#include <fstream>
using namespace std;

struct Block
{
	static const int BlockSize = 50;//50*2 B
	char D[BlockSize];
	void operator=(const Block& b);
	Block(){
		memset(D, 0, sizeof(D));
	}
	void clear() {
		memset(D, 0, sizeof(D));
	}
};

struct diNode								//设备文件索引节点
{
	int fileSize;							//块数量
	string owner;
	list<int> addr;
	diNode():fileSize(0),owner("#"){}
};
struct Menu
{
	map<string, int> fileMenu;				//name,diNode
	map<string, int> folder;				//name,folder
	void output(ostream &out,const string &pre, vector<Menu>& vMenu);
};
struct Dev
{
	vector<Menu> menuData;					//菜单数据
	vector<Block> DATA;						//数据区
	vector<diNode> FileNodes;				//索引节点

	//超级块------
	static const int AllocFailed = -1;
	int blockSize;							//硬盘块数
	int blockFree;							//空闲块数
	queue<int> emptyBlock;					//空闲块号队列
	int nodeSize;							//索引节点数
	int nodeFree;							//空闲索引节点数
	queue<int> emptyNode;					//空闲索引节点号表
	int rootMenui;							//目录项
	//------------

	

	Dev(int blockSize, int nodeSize);
	string devF;
	Dev(const string &devF);

	int BlockAlloc();						//分配块 返回块号
	void BlockFree(int i);					//释放块i
	int NodeAlloc();						//分配索引节点 返回索引节点号
	void NodeFree(int i);					//释放索引节点i

	int curMenui;							//当前目录
	stack<int> preMenui;					//上一级目录栈
	bool openFolder(const string& name);	//打开子目录
	bool closeFolder();						//关闭子目录

	bool newFile(const string& name,ostream &out, const string & curUser);		//新建文件
	bool deleteFile(const string & name, ostream& out);							//删除文件
	bool newFolder(const string& name, ostream& out);	//新建子目录
	bool deleteFolder(const string& name, ostream& out);//删除子目录 
	void backToRoot();

	void output(ostream &out);
	void DATAoutput(ostream &out);

	void CLOSE();

};


struct inode									//内存文件索引节点
{
	int fileSize;								//多少个块
	bool canWrite;								//只读/可写
	list<int> addr;								//块索引号表
	//int i_dev;								//设备号
	int indexOfdev;								//设备中的索引结点号
	string owner;
};
struct RAM
{
	static const int AllocFailed = -1;
	int blockSize;								//内存块数
	int blockFree;								//空闲块数
	queue<int> emptyBlock;						//空闲块号队列
	int nodeSize;								//索引节点数
	int nodeFree;								//空闲索引节点数
	queue<int> emptyNode;						//空闲索引节点号表

	map<string, int>fileOpen;					//内存索引节点表							

	RAM(int blockSize, int nodeSize);
	int BlockAlloc();							//分配块 返回块号
	void BlockFree(int i);						//释放块i
	int NodeAlloc();							//分配索引节点 返回索引节点号
	void NodeFree(int i);						//释放索引节点i

	void output(ostream &out);
	void DATAoutput(ostream &out);

	vector<Block> DATA;							//数据区
	vector<inode> FileNodes;					//内存索引节点
};

struct FileSystem
{
	Dev dev;
	RAM ram;
	string curUser;
	ostream *out;
	istream *in;
	const char writeEnd = '#';//esc

	FileSystem();
	FileSystem(const string &devF);

	bool writeBack(int ri);						//通过内存索引节点号将文件写回DEV
	bool open(const string &name);				//打开文件夹 or 为文件分配内存索引结点
	void close(const string &name);				//关闭文件 释放内存索引结点
	void close();								//关闭文件夹 
	void closeAll();							//关闭所有文件
	bool read(const string &name);				//从内存中打开的文件读出
	bool write(const string &name);				//往内存中打开的文件写入
	bool writeAll();							//写回所有文件

	bool newFile(const string& name);			//新建文件
	bool deleteFile(const string& name);		//删除文件
	bool newFolder(const string& name);			//新建子目录
	bool deleteFolder(const string& name);		//删除子目录

	bool checkUser(const string& name);
	void login();
	void run(ostream &dev, ostream &ram);
};