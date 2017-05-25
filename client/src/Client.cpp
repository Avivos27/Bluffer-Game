#include <stdlib.h>
#include <boost/locale.hpp>
#include <boost/thread.hpp>
#include "ConnectionHandler.h"


void Read(ConnectionHandler* handler){
	while(true){
		std::string message;
		if (!handler->getLine(message)) {
			boost::this_thread::sleep(boost::posix_time::milliseconds(500));
			continue;
		}

		int len=message.length();
		// A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
		// we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
		message.resize(len-1);
		std::cout << "" << message << std::endl ;
		if (message == "SYSMSG QUIT ACCEPTED") {
			std::cout << "Exiting...\n" << std::endl;
			break;
		}
	}

}

void Write(ConnectionHandler* handler){
	 while (true) {
	        const short bufsize = 1024;
	        char buf[bufsize];
	        std::cin.getline(buf, bufsize);
	        std::string line(buf);
	        if (line=="exit")
	        	break;
	        if (!handler->sendLine(line)) {
	            std::cout << "Disconnected. Exiting...\n" << std::endl;
	            break;
	        }
	        // connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
	        //std::cout << "Sent " << len+1 << " bytes to server" << std::endl;
	 }
}

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    unsigned short port = atoi(argv[2]);

    ConnectionHandler* connectionHandler = new ConnectionHandler(host, port);
    if (!connectionHandler->connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    boost::thread writeThread(Write, connectionHandler);
    boost::thread readThread(Read, connectionHandler);
    writeThread.join();
    readThread.join();
    writeThread.detach();
    readThread.detach();
    delete connectionHandler;
    return 0;
}


