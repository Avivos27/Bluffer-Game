CC = g++
INC_DIR = include
INC_LIB = /home/hellero/boost_1_60_0/stage/lib/
CFLAGS  = -Wall -I$(INC_DIR)
LDFLAGS = -L$(INC_LIB) -lboost_system -lboost_locale -lboost_thread

# All Targets
all: client

# Tool invocations
client: bin/ConnectionHandler.o bin/Client.o 
	@echo 'Building Main Program'
	@echo 'Invoking: C++ Linker'
	$(CC) -o bin/client bin/ConnectionHandler.o bin/Client.o $(LDFLAGS) 
	@echo 'Finished building Program'

# Depends on the source and header files
bin/%.o: src/%.cpp
	@echo '----------------Building ' $@ ' From ' $<
	$(CC) $(CFLAGS) -c  -o $@ $<
	@echo '----------------Finished Building ' $@
	@echo
	@echo
	@echo


#Clean the build directory
clean: 
	rm -f bin/*
