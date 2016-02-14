//
//  TeeTimeFactory.m
//  Golf
//
//  Created by Adam Wilson on 12/29/15.
//  Copyright © 2015 Adam Wilson. All rights reserved.
//

#import "TeeTimeFactory.h"

@implementation TeeTimeFactory


-(TeeTime *)createTeeTime:(NSString *)courseName
{
    TeeTime *newTeeTime;
    
    if ([courseName isEqualToString:@"Buckeye"] ||
        [courseName isEqualToString:@"Wolverine"] ||
        [courseName isEqualToString:@"Irish"]) {
        newTeeTime = [[TeeTime alloc] initForBedford];
    }
    
    return newTeeTime;
}
@end
