//
//  TeeTimeFactory.h
//  Golf
//
//  Created by Adam Wilson on 12/29/15.
//  Copyright © 2015 Adam Wilson. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TeeTime.h"

@interface TeeTimeFactory : NSObject

-(TeeTime *)createTeeTime:(NSString *)courseName;
@end
