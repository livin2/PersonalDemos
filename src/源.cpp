#include "FileSystem.h"

int main()
{
	fstream devInfo("devInfo.txt",ios::out);		//devInfo记录了dev设备的信息
	fstream ramInfo("ramInfo.txt", ios::out);		//ramInfo记录了内存的信息

	//FileSystem exp;				//默认构造函数 不会保存dev的状态到dev文件
	FileSystem exp("DEVF");			//"DEVF" 是dev文件的文件名 若没有则会新建 
	exp.curUser = "user";			//不能用记事本等工具直接打开DEVF文件 记事本会以某种编码打开 打乱DEVF的信息
	exp.run(devInfo, ramInfo);

	//exp.newFolder("a1");
	//exp.newFile("abx.ch");
	//exp.newFolder("b1");
	//exp.open("a1");
	//exp.newFile("wdf.tw");
	//exp.newFile("rgr.tw");
	//exp.newFolder("a2");
	//exp.open("a2");
	//exp.newFile("jpi.jp");
	//exp.newFile("xug.jp");
	//exp.close();
	//exp.close();
	//exp.open("b1");
	//exp.newFolder("a2");
	////
	//exp.dev.backToRoot();
	//exp.open("abx.ch");
	//exp.write("abx.ch");
	
	//exp.newFile("abx.ch");
	//exp.run(devInfo, ramInfo);
	
	//exp.dev.CLOSE(fs);

	//exp.newFile("wqer.ch");
	//exp.open("wqer.ch");
	//exp.write("wqer.ch");
	//exp.close("wqer.ch");

	//exp.read("wqer.ch");
	//exp.dev.output(dev);
	return 0;
}