//
//  Golfer.h
//  Golf
//
//  Created by Adam Wilson on 6/19/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Hole, Round;

@interface Golfer : NSManagedObject

@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSSet *holes;
@property (nonatomic, retain) Round *round;
@end

@interface Golfer (CoreDataGeneratedAccessors)

/*- (void)insertObject:(Hole *)value inHolesAtIndex:(NSUInteger)idx;
- (void)removeObjectFromHolesAtIndex:(NSUInteger)idx;
- (void)insertHoles:(NSArray *)value atIndexes:(NSIndexSet *)indexes;
- (void)removeHolesAtIndexes:(NSIndexSet *)indexes;
- (void)replaceObjectInHolesAtIndex:(NSUInteger)idx withObject:(Hole *)value;
- (void)replaceHolesAtIndexes:(NSIndexSet *)indexes withHoles:(NSArray *)values;
*/
- (void)addHolesObject:(Hole *)value;
- (void)removeHolesObject:(Hole *)value;
- (void)addHoles:(NSOrderedSet *)values;
- (void)removeHoles:(NSOrderedSet *)values;
@end
