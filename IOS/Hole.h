//
//  Hole.h
//  Golf
//
//  Created by Adam Wilson on 6/19/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Golfer, Stroke;

@interface Hole : NSManagedObject

@property (nonatomic, retain) NSString * course;
@property (nonatomic, retain) NSNumber * number;
@property (nonatomic, retain) NSNumber * par;
@property (nonatomic, retain) NSNumber * score;
@property (nonatomic, retain) NSNumber * putts;
@property (nonatomic, retain) NSString * fairwayHit;
@property (nonatomic, retain) NSString * greensHit;
@property (nonatomic, retain) Golfer *golfer;
@property (nonatomic, retain) NSSet *strokes;
@end

@interface Hole (CoreDataGeneratedAccessors)

- (void)addStrokesObject:(Stroke *)value;
- (void)removeStrokesObject:(Stroke *)value;
- (void)addStrokes:(NSSet *)values;
- (void)removeStrokes:(NSSet *)values;

@end
