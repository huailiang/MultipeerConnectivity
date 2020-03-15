//
//  OC_Multipeer.h
//  Unity-iPhone
//
//  Created by 彭怀亮 on 3/15/20.
//

#ifndef OC_Multipeer_h
#define OC_Multipeer_h


#import <Foundation/Foundation.h>
#import <MultipeerConnectivity/MultipeerConnectivity.h>
#import <Foundation/Foundation.h>

@interface OC_Multipeer : NSObject

- (void)createMC:(NSString *)name withType:(NSString*)type;

- (void)sendMsg:(NSString *)msg withMode:(MCSessionSendDataMode)mode;

@end

#endif /* OC_Multipeer_h */
