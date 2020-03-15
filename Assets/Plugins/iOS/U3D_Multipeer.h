//u3d_mulipeer.h

#ifndef __u3d_mulpeer__
#define __u3d_mulpeer__

#ifdef __cplusplus             //告诉编译器，这部分代码按C语言的格式进行编译，而不是C++的
extern "C"{
#endif

#include "OC_Multipeer.h"



OC_Multipeer *peer;

extern NewRoleJoin roleJoinHandler;

extern RecvMessage recvMessageHandler;

extern OnCharQuit charQuitHandler;

extern void InitWith(char* name, char* type, NewRoleJoin roleHandler, RecvMessage recvHandler, OnCharQuit quitHandler);

extern void Broadcast(char* msg);

extern void QuitConnect();


#ifdef __cplusplus
}
#endif


#endif
