//
//  RoundSelectVC.m
//  Golf
//
//  Created by Adam Wilson on 6/18/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "RoundSelectVC.h"
#import "RoundSelectVC+Golfers.h"
#import "RoundSelectCell.h"
#import "HoleVC.h"
#import "Golfer.h"
#import "CoursesTVC.h"

@interface RoundSelectVC ()
{
    UITextField *textField;
    CoursesTVC *coursesTVC;
    Round *round;
    AppDelegate *delegate;
}
@end

@implementation RoundSelectVC

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.isContinuing = NO;
    //self.selectedCourse = @"";
    delegate = [UIApplication sharedApplication].delegate;
    managedObjectContext = [delegate managedObjectContext];
    
    profileBar = [ProfileHeaderBar sharedProfileBar];
   
    //swipe back to return to home screen
    UIScreenEdgePanGestureRecognizer *swipe = [[UIScreenEdgePanGestureRecognizer alloc] initWithTarget:self action:@selector(unwind)];
    [swipe setEdges:UIRectEdgeLeft];
    [self.view addGestureRecognizer:swipe];

    //setup tableviews
    //coursesTVC = [[CoursesTVC alloc] initWithParent:self];
    //[coursesTVC setTableView:coursesTableView];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:YES];
    [self.view addSubview:profileBar];
    
    coursesTVC = [[CoursesTVC alloc] initWithParent:self];
    [coursesTVC setTableView:coursesTableView];
}

-(void)viewDidAppear:(BOOL)animated
{
    [super viewWillAppear:YES];
    //setup profile button
    [profileBar setShouldShowBackButton:YES];
    [profileBar.profileBackButton addTarget:self action:@selector(unwind) forControlEvents:UIControlEventTouchDown];
    
    [profileBar setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.view.frame.size.width, PROFILE_BAR_HEIGHT)];
    [self.view bringSubviewToFront:profileBar];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:YES];
    
    [profileBar.profileBackButton removeTarget:self action:@selector(unwind) forControlEvents:UIControlEventTouchDown];
    [profileBar setShouldShowBackButton:NO];
}

- (void)startRound
{
    if  (![coursesTVC.selectedCourse isEqualToString:@""]){
        //add round to data model if not continuing or continuing on same course
        if (!self.isContinuing || [coursesTVC.selectedCourse isEqualToString:round.course]) {
            [self createRound];
        }else{
            round.continueCourse = coursesTVC.selectedCourse;
        }
        
        [self performSegueWithIdentifier:@"ShowHole" sender:self];
    }
}

-(void)continueRound:(Round *)currentRound
{
    round = currentRound;
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([segue.identifier isEqualToString:@"ShowHole"]) {
        //initialize holeVC
        HoleVC *hvc = [segue destinationViewController];
        [hvc setCurrentCourse:[coursesTVC courseHoles]];
        [hvc setCurrentCourseName:coursesTVC.selectedCourse];
        [hvc setCurrentRound:round];
    }
}

-(void)createRound
{
    //add new round to data model
    round = [NSEntityDescription insertNewObjectForEntityForName:@"Round" inManagedObjectContext:managedObjectContext];
    [round setValue:coursesTVC.selectedCourse forKey:@"course"];
    [round setValue:[NSDate date] forKey:@"date"];
    
    //add golfers
    Golfer *golfer = [[Golfer alloc] initWithEntity:[NSEntityDescription entityForName:@"Golfer" inManagedObjectContext:managedObjectContext] insertIntoManagedObjectContext:managedObjectContext];
    [golfer setName:profileBar.profileNameLabel.text];
    [round addGolfersObject:golfer];

    //save data
    delegate = [UIApplication sharedApplication].delegate;
    [delegate saveContext];
}

-(void)unwind
{
    [self.presentingViewController dismissViewControllerAnimated:YES completion:nil];
}

- (NSUInteger)supportedInterfaceOrientations {
    if (delegate.shouldForceLandscape) {
        return UIInterfaceOrientationMaskLandscape;
    }
    return UIInterfaceOrientationMaskPortrait;
}

@end
