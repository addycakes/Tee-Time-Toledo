//
//  Stroke.h
//  Golf
//
//  Created by Adam Wilson on 6/19/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Hole;

@interface Stroke : NSManagedObject

@property (nonatomic, retain) NSNumber * latitude;
@property (nonatomic, retain) NSNumber * longitude;
//@property (nonatomic, retain) NSString * club;

@property (nonatomic, retain) Hole *hole;

@end
