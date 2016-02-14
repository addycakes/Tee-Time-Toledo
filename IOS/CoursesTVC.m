//
//  CoursesTVC.m
//  Golf
//
//  Created by Adam Wilson on 7/29/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "CoursesTVC.h"
#import "RoundSelectCell.h"
#import "RoundSelectVC.h"
#import "StatsVC.h"
#import "TeeTimeVC.h"
#import "AppDelegate.h"
#import "Hole.h"
#import "Golfer.h"

#define COURSE_FILE @"Courses.plist"
#define CELL_HEIGHT 185.0f

@interface CoursesTVC ()
{
    NSDictionary *allCourses;
    NSDictionary *allRounds;
    NSMutableArray *sortedRounds;
    
    BOOL isCellSelected;
    UIViewController *parentVC;
    NSIndexPath *selectedIndexPath;
    
    BOOL shouldShowForStats;
    BOOL shouldShowForTeeTime;
}
@end

@implementation CoursesTVC

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:YES];
}

-(id)initWithParent:(UIViewController *)parent
{
    self = [super init];
    parentVC = parent;
    
    shouldShowForStats = [parentVC respondsToSelector:@selector(showScorecard:withCourse:)];
    shouldShowForTeeTime = [parentVC respondsToSelector:@selector(confirmTeeTime:)];
    
    if (shouldShowForStats) {
        allRounds = [self loadAllRounds];
    }
    allCourses = [self loadAllCourses];
    
    self.selectedCourse = [[NSString alloc] init];
    self.selectedCourse = @"";

    return self;
}

-(CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (isCellSelected) {
        if ([[allCourses.allKeys objectAtIndex:indexPath.row] isEqualToString:self.selectedCourse]) {
            return CELL_HEIGHT * 2;
        }
    }
    return CELL_HEIGHT;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    if (shouldShowForStats) {
        return sortedRounds.count;
    }
    return allCourses.allKeys.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    RoundSelectCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Course"];
    
    if (!cell) {
        [tableView registerNib:[UINib nibWithNibName:@"RoundSelectCell" bundle:nil] forCellReuseIdentifier:@"Course"];
        cell = [tableView dequeueReusableCellWithIdentifier:@"Course"];
    }
    
    if (shouldShowForStats) {
        NSString *roundName = [sortedRounds objectAtIndex:indexPath.row];
        NSRange nameRange = [roundName rangeOfString:@"/"];
        
        NSString *cellLabelText = [roundName substringToIndex:nameRange.location + 11];
        NSString *courseName =[roundName substringToIndex:nameRange.location];
        [cell.backgroundImage setImage:[UIImage imageNamed:[NSString stringWithFormat:@"%@CellBG.png", courseName]]];
        [cell.scoreLabel setHidden:NO];
        [cell.strokesLabel setHidden:NO];

        NSDictionary *values = [self scoreAndStrokesForRound:roundName];
        [cell.scoreLabel setText:[values objectForKey:@"Score"]];
        [cell.strokesLabel setText:[values objectForKey:@"Strokes"]];
        [cell.cellLabel setText:[NSString stringWithFormat:@"%@",cellLabelText]];
    }else{
        NSString *courseName = [allCourses.allKeys objectAtIndex:indexPath.row];
        [cell.backgroundImage setImage:[UIImage imageNamed:[NSString stringWithFormat:@"%@CellBG.png", courseName]]];
        
        [cell.cellLabel setText:[NSString stringWithFormat:@"%@",courseName]];
        [cell.scoreLabel setHidden:YES];
        [cell.strokesLabel setHidden:YES];
    }

    return cell;
}


-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    RoundSelectCell *cell = (RoundSelectCell *)[tableView cellForRowAtIndexPath:indexPath];
    
    if (shouldShowForStats) {
        StatsVC *statsVC = (StatsVC *)parentVC;
        //set selected course
        NSString *formatedName =[cell.cellLabel.text substringToIndex:[cell.cellLabel.text rangeOfString:@"/"].location];
        [statsVC showScorecard:[allRounds objectForKey:[sortedRounds objectAtIndex:indexPath.row]] withCourse:formatedName];
    }else{
        //if clicked expanded cell, call parent selector method
        if ([[allCourses.allKeys objectAtIndex:indexPath.row] isEqualToString:self.selectedCourse]) {
            if (shouldShowForTeeTime) {
                TeeTimeVC *teeTimeVC = (TeeTimeVC *)parentVC;
                //set selected course
                [teeTimeVC setCourse:cell.cellLabel.text];
            }else {
                RoundSelectVC *roundSelectVC = (RoundSelectVC *)parentVC;
                [roundSelectVC startRound];
            }
            isCellSelected = NO;
            //hide info image layer
            [cell hideCellInfo:self.tableView indexPath:indexPath];
        }else{
            self.selectedCourse = [allCourses.allKeys objectAtIndex:indexPath.row];
            //display info image layer
            isCellSelected = YES;
            [cell showCellInfo:self.tableView indexPath:indexPath];
        }
    }
}

-(BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    //can only edit if coming from stats vc
    if (shouldShowForStats) {
        return YES;
    }
    return NO;
}

-(void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        
        //ask to confirm delete
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Delete?" message:@"Are you sure you want to delete this round?" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Yes", nil];
        [alert show];
        
        selectedIndexPath = indexPath;
    }
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if ([[alertView buttonTitleAtIndex:buttonIndex] isEqualToString:@"Yes"]) {
        //remove golfer
        AppDelegate *delegate = [[UIApplication sharedApplication] delegate];
        [delegate.managedObjectContext deleteObject:[allRounds objectForKey:[sortedRounds objectAtIndex:selectedIndexPath.row]]];
        [delegate saveContext];

        [sortedRounds removeObjectAtIndex:selectedIndexPath.row];
        [self.tableView deleteRowsAtIndexPaths:@[selectedIndexPath] withRowAnimation:UITableViewRowAnimationFade];
    }
}

//get all courses from plist
-(NSDictionary *)loadAllCourses
{
    NSString *filePathBundle = [[NSBundle mainBundle] pathForResource:@"Courses" ofType:@"plist"];
    NSDictionary *plist = [[NSMutableDictionary alloc] initWithContentsOfFile:filePathBundle];
    
    return plist;
}

-(NSDictionary *)loadAllRounds
{
    AppDelegate *delegate = [[UIApplication sharedApplication] delegate];
    
    //look up database entries
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription
                                   entityForName:@"Round" inManagedObjectContext:[delegate managedObjectContext]];
    [fetchRequest setEntity:entity];
    
    NSError *error;
    NSArray *fetchedObjects = [[delegate managedObjectContext] executeFetchRequest:fetchRequest error:&error];
    NSSortDescriptor *dateDescriptor = [[NSSortDescriptor alloc] initWithKey:@"date" ascending:NO];
    NSArray *rounds = [fetchedObjects sortedArrayUsingDescriptors:@[dateDescriptor]];
    
    sortedRounds = [[NSMutableArray alloc] init];
    NSMutableDictionary *roundsDict = [[NSMutableDictionary alloc] init];
    for (Round *round in rounds) {
        //get date string
        NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
        [dateFormat setDateFormat:@"MM-dd-yyyy-HH-mm"];
        NSString *date = [dateFormat stringFromDate:round.date];
        NSString *key = [NSString stringWithFormat:@"%@/%@",round.course, date];
        
        [roundsDict setObject:round forKey:key];
        [sortedRounds addObject:key];
    }
    
    return (NSDictionary *)roundsDict;
}

-(NSDictionary *)courseHoles
{
    return [allCourses objectForKey:self.selectedCourse];
}

-(NSDictionary *)scoreAndStrokesForRound:(NSString *)roundKey
{
    int strokes = 0;
    int coursePar = 0;
    
    Round *round = [allRounds objectForKey:roundKey];
    
    for (Golfer *g in round.golfers.allObjects) {
        if ([g.name isEqualToString:self.golfer]) {
            for (Hole *h in g.holes.allObjects) {
                strokes += h.score.intValue;
                coursePar += h.par.intValue;
            }
        }
    }
    
    NSString *score;
    if (coursePar < strokes) {
        score = [NSString stringWithFormat:@"+%d", (strokes-coursePar)];
    }else{
        score = [NSString stringWithFormat:@"%d", (strokes-coursePar)];
    }
    
    return [NSDictionary dictionaryWithObjectsAndKeys:score, @"Score", [NSString stringWithFormat:@"%d",strokes], @"Strokes", nil];
}

@end
