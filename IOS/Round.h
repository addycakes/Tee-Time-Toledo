//
//  Round.h
//  Golf
//
//  Created by Adam Wilson on 6/19/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Golfer;

@interface Round : NSManagedObject

@property (nonatomic, retain) NSString * course;
@property (nonatomic, retain) NSString * continueCourse;
@property (nonatomic, retain) NSDate * date;
@property (nonatomic, retain) NSSet *golfers;
@end

@interface Round (CoreDataGeneratedAccessors)

- (void)addGolfersObject:(Golfer *)value;
- (void)removeGolfersObject:(Golfer *)value;
- (void)addGolfers:(NSSet *)values;
- (void)removeGolfers:(NSSet *)values;

@end
