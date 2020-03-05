#include "FileSystem.h"

RAM::RAM(int blockSize,int nodeSize)
	:blockSize(blockSize), DATA(blockSize), nodeSize(nodeSize), FileNodes(nodeSize)
{
	blockFree = blockSize;
	nodeFree = nodeSize;

	for (int i = 0; i < blockSize; i++)
		emptyBlock.push(i);

	for (int i = 0; i < nodeSize; i++)
		emptyNode.push(i);
}

int RAM::BlockAlloc()
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

void RAM::BlockFree(int i)
{
	emptyBlock.push(i);
	blockFree++;
	return;
}


int RAM::NodeAlloc()
{
	if (!emptyNode.empty())
	{
		int ni = emptyNode.front();

		//int blk = BlockAlloc();
		//if (blk == AllocFailed)
		//	return AllocFailed;
		//FileNodes[ni].addr.push_back(blk);
		FileNodes[ni].fileSize = 0;

		emptyNode.pop();
		nodeFree--;
		return ni;
	}
	return AllocFailed;
}

void RAM::NodeFree(int i)
{
	auto it = FileNodes[i].addr.begin();
	for (; it != FileNodes[i].addr.end(); it++)
		BlockFree(*it);
	emptyNode.push(i);
	nodeFree++;
	return;
}

void RAM::output(ostream & out)
{
	out.seekp(0);
	out << "-----RAM INFO-----" << endl;
	out << "blockSize\t\t\t" << blockSize << endl;
	out << "blockFree\t\t\t" << blockFree << endl;

	out << "nodeSize\t\t\t" << nodeSize << endl;
	out << "nodeFree\t\t\t" << nodeFree << endl;

	out << endl;
	out << "fileOpened" << endl;
	auto it = fileOpen.begin();
	for (; it!=fileOpen.end(); it++)
		out <<  "-" << it->first << "\t\t idx:" << it->second << endl;
	out << "idx-owner-block" << endl;
	for (int i = 0; i < FileNodes.size(); i++) {
		inode& riN = FileNodes[i];
		if (riN.fileSize == 0)
			break;
		out << i << "\t" << riN.owner << "\t";
		for (auto it = riN.addr.begin(); it != riN.addr.end(); it++) {
			out << *it << " ";
		}
		out << endl;
	}

	out << endl;
	out << "data" << endl;
	DATAoutput(out);
}

void RAM::DATAoutput(ostream & out)
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
