//
//  OC_Multipeer.m
//  Unity-iPhone
//
//  Created by 彭怀亮 on 3/15/20.
//

#import "OC_Multipeer.h"

NewRoleJoin roleJoinHandler;
RecvMessage recvMessageHandler;
OnCharQuit charQuitHandler;

#pragma mark oc interface

@interface OC_Multipeer
()<MCSessionDelegate,MCNearbyServiceBrowserDelegate,MCBrowserViewControllerDelegate>

/**
 *  表示为一个用户
 */
@property (nonatomic,strong)MCPeerID * peerID;
/**
 *  启用和管理Multipeer连接会话中的所有人之间的沟通。 通过Sesion，给别人发送数据。类似于Scoket
 */
@property (nonatomic,strong)MCSession * session;
/**
 *  可以接收，并处理用户请求连接的响应。没有回调，会弹出默认的提示框，并处理连接。
 */
@property (nonatomic,strong)MCAdvertiserAssistant * advertiser;
/**
 *  用于搜索附近的用户，并可以对搜索到的用户发出邀请加入某个会话中。
 */
@property (nonatomic,strong)MCNearbyServiceBrowser * brower;
/**
 *  附近用户列表
 */
@property (nonatomic,strong)MCBrowserViewController * browserViewController;
/**
 *  存储连接
 */
@property (nonatomic,strong)NSMutableArray * sessionArray;

/**
 * 连接类型
 */
@property (nonatomic, nullable) NSString* connectType;

@end

#pragma mark oc implements

@implementation OC_Multipeer

/**
 *  连接设置
 */
- (void)createMC:(NSString *)name withType:(NSString*)type
{
    _connectType = type;
    NSLog(@"connect with name: %@ with type: %@", name, type);
    
    //用户
    _peerID = [[MCPeerID alloc] initWithDisplayName:name];
    //为用户建立连接
    _session = [[MCSession alloc] initWithPeer:_peerID];
    _session.delegate = self;
    //设置广播服务(发送方)
    _advertiser = [[MCAdvertiserAssistant alloc] initWithServiceType:type discoveryInfo:nil session:_session];
    //开始广播
    [_advertiser start];
    //设置发现服务(接收方)
    _brower = [[MCNearbyServiceBrowser alloc]initWithPeer:_peerID serviceType:type];
    //设置代理
    _brower.delegate = self;
    [_brower startBrowsingForPeers];
}

- (void)sendMsg:(NSString *)msg withMode:(MCSessionSendDataMode)mode
{
     [_session sendData:[msg dataUsingEncoding:NSUTF8StringEncoding] toPeers:_session.connectedPeers withMode:mode error:nil];
}

-(void)quit
{
    if (_session!=nil) {
        [_session disconnect];
        _session = nil;
    }
    if (_brower!=nil) {
        [_brower stopBrowsingForPeers];
        _brower=nil;
    }
    if (_sessionArray !=nil) {
        [_sessionArray removeAllObjects];
    }
}

/**
 *  发现附近用户
 *
 *  @param browser 搜索附近用户
 *  @param peerID  附近用户
 *  @param info    详情
 */
- (void)browser:(MCNearbyServiceBrowser *)browser foundPeer:(MCPeerID *)peerID withDiscoveryInfo:(NSDictionary *)info{
    NSLog(@"发现附近用户%@",peerID.displayName);
    roleJoinHandler([peerID.displayName UTF8String]);
    if (_browserViewController == nil) {
        _browserViewController = [[MCBrowserViewController alloc]initWithServiceType:_connectType session:_session];
        _browserViewController.delegate = self;
        /**
         *  跳转发现界面
         */
        [UnityGetGLViewController() presentViewController:_browserViewController animated:YES completion:nil];
    }
}
/**
 *  附近某个用户消失了
 *
 *  @param browser 搜索附近用户
 *  @param peerID  用户
 */
- (void)browser:(MCNearbyServiceBrowser *)browser lostPeer:(MCPeerID *)peerID{
    NSLog(@"附近用户%@离开了",peerID.displayName);
    const char* name = [peerID.displayName UTF8String];
    charQuitHandler(name);
}


#pragma mark BrowserViewController附近用户列表视图相关代理方法
/**
 *  选取相应用户
 *
 *  @param browserViewController 用户列表
 */
- (void)browserViewControllerDidFinish:(MCBrowserViewController *)browserViewController{
    [UnityGetGLViewController() dismissViewControllerAnimated:YES completion:nil];
    _browserViewController = nil;
    //关闭广播服务，停止其他人发现
    [_advertiser stop];
}
/**
 *  用户列表关闭
 *
 *  @param browserViewController 用户列表
 */
- (void)browserViewControllerWasCancelled:(MCBrowserViewController *)browserViewController{
    [UnityGetGLViewController() dismissViewControllerAnimated:YES completion:nil];
    _browserViewController = nil;
    [_advertiser stop];
}


#pragma mark MCSession代理方法
/**
 *  当检测到连接状态发生改变后进行存储
 *
 *  @param session MC流
 *  @param peerID  用户
 *  @param state   连接状态
 */
- (void)session:(MCSession *)session peer:(MCPeerID *)peerID didChangeState:(MCSessionState)state{
    //判断如果连接
    if (state == MCSessionStateConnected) {
        //保存这个连接
        if (![_sessionArray containsObject:session]) {
            //如果不存在 保存
            [_sessionArray addObject:session];
        }
    }
}
/**
 *  接收到消息
 *
 *  @param session MC流
 *  @param data    传入的二进制数据
 *  @param peerID  用户
 */
- (void)session:(MCSession *)session didReceiveData:(NSData *)data fromPeer:(MCPeerID *)peerID{
    NSString * message = [NSString stringWithFormat:@"%@:%@",peerID.displayName,[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
    dispatch_async(dispatch_get_main_queue(), ^{
        NSLog(@"recv: %@", message);
        const char* msg = [message UTF8String];
        recvMessageHandler(msg);
    });
}
/**
 *  接收数据流
 *
 *  @param session    MC流
 *  @param stream     数据流
 *  @param streamName 数据流名称（标示）
 *  @param peerID     用户
 */
- (void)session:(MCSession *)session didReceiveStream:(NSInputStream *)stream withName:(NSString *)streamName fromPeer:(MCPeerID *)peerID{
    
}
/**
 *  开始接收资源
 *
 *  @param session      MC流
 *  @param resourceName 资源名称
 *  @param peerID       用户
 *  @param progress     进度
 */
- (void)session:(MCSession *)session didStartReceivingResourceWithName:(NSString *)resourceName fromPeer:(MCPeerID *)peerID withProgress:(NSProgress *)progress{

}
/**
 *  资源接收结束
 *
 *  @param session      MC流
 *  @param resourceName 资源名称
 *  @param peerID       用户
 *  @param localURL     本地资源
 *  @param error        报错信息
 */
- (void)session:(MCSession *)session didFinishReceivingResourceWithName:(NSString *)resourceName fromPeer:(MCPeerID *)peerID atURL:(NSURL *)localURL withError:(NSError *)error{
}

@end


