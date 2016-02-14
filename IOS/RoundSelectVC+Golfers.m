//
//  RoundSelectVC+Golfers.m
//  Golf
//
//  Created by Adam Wilson on 6/18/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "RoundSelectVC+Golfers.h"

@implementation RoundSelectVC (Golfers)
/*
-(NSMutableArray *)loadGolfers
{
    NSMutableArray *golfers = [[NSMutableArray alloc] init];
    
    if (self.isContinuing){
        //disable tableview
        [golfersTableView setUserInteractionEnabled:NO];
        
        //get current round
        Round *round = [self getCurrentRound];
        
        //add golfers over from round
        for (Golfer *golfer in [round valueForKey:@"golfers"]){
            [golfers addObject:golfer];
        }
    }else{
        //set user as initial golfer
        [golfersTableView setUserInteractionEnabled:YES];
        Golfer *me = [[Golfer alloc] initWithEntity:[NSEntityDescription entityForName:@"Golfer" inManagedObjectContext:managedObjectContext] insertIntoManagedObjectContext:managedObjectContext];
        [me setName:profileBar.profileNameLabel.text];
        [golfers addObject:me];
    }
    
    return golfers;
}

-(void)addGolfer:(NSString *)name
{
    //add golfer
    Golfer *golfer = [[Golfer alloc] initWithEntity:[NSEntityDescription entityForName:@"Golfer" inManagedObjectContext:managedObjectContext] insertIntoManagedObjectContext:managedObjectContext];
    [golfer setName:name];
    [self.allGolfers addObject:golfer];
}

-(void)removeGolfer:(Golfer *)golfer
{
    [managedObjectContext deleteObject:golfer];
    [self.allGolfers removeObject:golfer];
}

-(Round *)getCurrentRound
{
    Round *currentRound;
    
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription
                                   entityForName:@"Round" inManagedObjectContext:managedObjectContext];
    [fetchRequest setEntity:entity];
    
    NSError *error;
    NSArray *fetchedObjects = [managedObjectContext executeFetchRequest:fetchRequest error:&error];
    for (Round *round in fetchedObjects) {
        //check date to find current round
        if ([self daysWithinEraFromDate:[round valueForKey:@"date"] toDate:[NSDate date]] == 0) {
            currentRound = round;
        }
    }
    
    return currentRound;
}

//returns 0 is startDate == endDate
-(NSInteger)daysWithinEraFromDate:(NSDate *) startDate toDate:(NSDate *) endDate
{
    NSCalendar *calendar = [NSCalendar autoupdatingCurrentCalendar];
    NSInteger startDay = [calendar ordinalityOfUnit:NSCalendarUnitDay
                                             inUnit:NSCalendarUnitEra
                                            forDate:startDate];
    NSInteger endDay = [calendar ordinalityOfUnit:NSCalendarUnitDay
                                           inUnit:NSCalendarUnitEra
                                          forDate:endDate];
    return endDay - startDay;
}

*/


@end
