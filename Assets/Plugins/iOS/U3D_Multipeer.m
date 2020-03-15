/// Nultipeer.m

#import "U3D_Multipeer.h"


#pragma mark c implements


bool check_error()
{
    if (peer== nil || peer == NULL) {
        printf("not initial multi peer");
        return false;
    }
    return true;
}

void InitWith(char* name, char* type)
{
    printf("%s. type: %s",name, type);
    NSString* n_name = [[NSString alloc] initWithUTF8String:name];
    NSString* n_type = [[NSString alloc] initWithUTF8String:type];
    NSLog(@"name %@, type: %@", n_name, n_type);
    if (peer== nil || peer == NULL) {
        peer = [[OC_Multipeer alloc] init];
    }
    [peer createMC:n_name withType:n_type];
}

void Broadcast(char* msg)
{
    if (check_error()) {
        printf("send %s",msg);
        NSString * n_msg = [[NSString alloc] initWithUTF8String:msg];
        [peer sendMsg:n_msg withMode:MCSessionSendDataUnreliable];
    }
}

