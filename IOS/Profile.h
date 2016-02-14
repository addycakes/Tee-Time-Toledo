//
//  Profile.h
//  Golf
//
//  Created by Adam Wilson on 7/17/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <CoreData/CoreData.h>

@interface Profile : NSManagedObject
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSData * pic;

@end
