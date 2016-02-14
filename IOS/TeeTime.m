//
//  TeeTime.m
//  Golf
//
//  Created by Adam Wilson on 12/29/15.
//  Copyright © 2015 Adam Wilson. All rights reserved.
//

#import "TeeTime.h"

@implementation TeeTime

-(instancetype)initForBedford{
    self = [super init];
    
    self.carts = 0;
    self.golfers = 1;
    return self;
}

-(void)submit{
    NSLog(@"Submit");
}

-(NSArray *)getTimes
{
    return [[NSArray alloc] init];
}
@end
