//
//  HoleVC.m
//  Golf
//
//  Created by Adam Wilson on 6/19/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "HoleVC.h"
#import "AppDelegate.h"
#import "StrokeLog.h"
#import "Hole.h"
#import "ScorecardVC.h"
#import "RoundSelectVC.h"
#import "RangeFinderVC.h"
#import "Stroke.h"
#import "ProfileHeaderBar.h"
#import <MediaPlayer/MediaPlayer.h>
#import "Advertisement.h"

@interface HoleVC ()
{
    NSArray *allHoles;
    NSDictionary *hole;
    Golfer *golfer;
    
    BOOL hasShownAd;

    ProfileHeaderBar *profileBar;
    RangeFinderVC *rangeFinder;
    MPMoviePlayerController *moviePlayer;
    StrokeLog *strokeLog;
    UIView *popover;
}
@end

@implementation HoleVC

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Do any additional setup after loading the view.
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    managedObjectContext = [delegate managedObjectContext];
    
    //create the profile bar
    profileBar = [ProfileHeaderBar sharedProfileBar];
    
    //swipe to cycle between holes
    UIScreenEdgePanGestureRecognizer *swipeLeft = [[UIScreenEdgePanGestureRecognizer alloc] initWithTarget:self action:@selector(cyclePreviousHole:)];
    [swipeLeft setEdges:UIRectEdgeLeft];
    [self.view addGestureRecognizer:swipeLeft];
    
    UIScreenEdgePanGestureRecognizer *swipeRight = [[UIScreenEdgePanGestureRecognizer alloc] initWithTarget:self action:@selector(cycleNextHole:)];
    [swipeRight setEdges:UIRectEdgeRight];
    [self.view addGestureRecognizer:swipeRight];
    
    //register for notification center
    NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
    [nc addObserver:self selector:@selector(saveScores) name:@"ScoreChanged" object:nil];
    [nc addObserver:self selector:@selector(saveData) name:@"DataChanged" object:nil];
    [nc addObserver:self selector:@selector(saveStroke) name:@"StrokeAdded" object:nil];
    
    hasShownAd = NO;
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:YES];
    
    //unset profile bar items
    [profileBar setShouldShowBackButton:NO];
    [profileBar.profileBackButton removeTarget:self action:@selector(quit) forControlEvents:UIControlEventTouchDown];
    [profileBar removeFromSuperview];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:YES];
    
    //set course name
    [courseNameLabel setText:self.currentCourseName];
    
    //get golfer
    if (golfer == nil) {
        for (Golfer *g in self.currentRound.golfers.allObjects) {
            if ([g.name isEqualToString:profileBar.profileNameLabel.text]) {
                golfer = g;
            }
        }
    }
}

-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    //setup profile button
    [self.view addSubview:profileBar];
    [profileBar setShouldShowBackButton:YES];
    [profileBar.profileBackButton addTarget:self action:@selector(quit) forControlEvents:UIControlEventTouchDown];
    [profileBar setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.view.frame.size.width, PROFILE_BAR_HEIGHT)];
    [self.view bringSubviewToFront:profileBar];
    
    //clear up orientation issue from programmatic switch to landscape for scorecard/movieplayer
    [rangeFinder shouldAutorotate];
    
    //get pointer to rangefinder
    if (rangeFinder == nil) {
        for (UIViewController *vc in self.childViewControllers) {
            if ([vc respondsToSelector:@selector(setHole:withDict:)]) {
                NSDictionary *mapDict = [NSDictionary dictionaryWithDictionary:[self.currentCourse objectForKey:@"MAP"]];
                
                //grab pointer to rangefinder
                rangeFinder = (RangeFinderVC *)vc;
                [rangeFinder setGolfer:golfer];
                [rangeFinder setMapOverlay:mapDict];
                break;
            }
        }
    }
    
    if (allHoles == nil) {
        //remove MAP dictionary for rangefinder
        NSMutableDictionary *dictionary = [NSMutableDictionary dictionaryWithDictionary:self.currentCourse];
        [dictionary removeObjectForKey:@"MAP"];
        
        //store sorted keys for course dictionary
        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"self" ascending:YES comparator:^(id obj1, id obj2) {
            
            if ([obj1 integerValue] > [obj2 integerValue]) {
                return (NSComparisonResult)NSOrderedDescending;
            }
            if ([obj1 integerValue] < [obj2 integerValue]) {
                return (NSComparisonResult)NSOrderedAscending;
            }
            return (NSComparisonResult)NSOrderedSame;
        }];
        allHoles = [[NSArray alloc] initWithArray:[dictionary.allKeys sortedArrayUsingDescriptors:@[sortDescriptor]]];
        
        //set first hole
        hole = [NSDictionary dictionaryWithDictionary:[self.currentCourse objectForKey:[allHoles firstObject]]];
        
        //update view
        [self updateHole];
    }
}

-(void)updateHole
{
    //add hole to golfers data or retreive if hole already exists
    BOOL isNew = YES;
    for (Hole *h in [golfer valueForKey:@"holes"]) {
        //check if hole is in data base
        if ([[h valueForKey:@"number"]isEqual:[NSNumber numberWithInteger:[[hole valueForKey:@"Number"] integerValue]]] && [[h valueForKey:@"course"]isEqualToString:self.currentCourseName]) {
            isNew = NO;
        
            //update rangefinder
            [rangeFinder setHole:h withDict:hole];

            break;
        }
    }
    
    if (isNew) {
        Hole *h = [[Hole alloc] initWithEntity:[NSEntityDescription entityForName:@"Hole" inManagedObjectContext:managedObjectContext] insertIntoManagedObjectContext:managedObjectContext];

        //set static values
        [h setValue:self.currentCourseName forKey:@"course"];
        [h setValue:[NSNumber numberWithInt:[[hole objectForKey:@"Par"] intValue]] forKey:@"par"];
        [h setValue:[NSNumber numberWithInteger:[[hole valueForKey:@"Number"] integerValue]] forKey:@"number"];
        [h setValue:[NSNumber numberWithInt:0] forKey:@"score"];
        [h setValue:[NSNumber numberWithInt:0] forKey:@"putts"];

        //add hole to golfers array
        [golfer addHolesObject:h];
        
        //save
        [self saveData];
        
        //update rangefinder
        [rangeFinder setHole:h withDict:hole];
    }
    
    //update labels
    [holeNumberLable setText:[NSString stringWithFormat:@"Hole %@",[hole objectForKey:@"Number"]]];
    [distanceLabel setText:[NSString stringWithFormat:@"%@ yrds",[hole objectForKey:@"Distance"]]];
    [parLabel setText:[NSString stringWithFormat:@"Par %@",[hole objectForKey:@"Par"]]];
}

-(void)saveData
{    
    //save data
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    [delegate saveContext];
}

-(void)saveScores
{
    for (Hole *h in [golfer valueForKey:@"holes"]) {
        //check for current hole
        if ([[h valueForKey:@"number"]isEqual:[NSNumber numberWithInteger:[[hole valueForKey:@"Number"] integerValue]]] && [[h valueForKey:@"course"]isEqualToString:self.currentCourseName]) {
            
            //get score from stroke log
            [h setValue:[NSNumber numberWithInt:strokeLog.score] forKey:@"score"];
            [h setValue:[NSNumber numberWithInt:strokeLog.putts] forKey:@"putts"];
            [h setValue:strokeLog.greenHit forKey:@"greensHit"];
            [h setValue:strokeLog.fairwayHit forKey:@"fairwayHit"];
        }
    }
    
    [self saveData];
}

-(void)saveStroke
{
    for (Hole *h in [golfer valueForKey:@"holes"]) {
        //check for current hole
        if ([[h valueForKey:@"number"]isEqual:[NSNumber numberWithInteger:[[hole valueForKey:@"Number"] integerValue]]] && [[h valueForKey:@"course"]isEqualToString:self.currentCourseName]) {
            
            Stroke *stroke = [[Stroke alloc] initWithEntity:[NSEntityDescription entityForName:@"Stroke" inManagedObjectContext:managedObjectContext] insertIntoManagedObjectContext:managedObjectContext];
            
            [stroke setValue:[NSNumber numberWithDouble:rangeFinder.userLocation.latitude] forKey:@"latitude"];
            [stroke setValue:[NSNumber numberWithDouble:rangeFinder.userLocation.longitude] forKey:@"longitude"];
            //[stroke setValue:[rangeFinder.golferStrokeAnnotation objectForKey:@"club"] forKey:@"club"];

            [h addStrokesObject:stroke];
            [self saveData];

            [rangeFinder setHole:h withDict:hole];
            [rangeFinder setShouldShowHistory:YES];
            break;
        }
    }
    
}


-(void)cycleNextHole:(UIGestureRecognizer *)gr
{
    //get index of current hole
    int index = [[hole valueForKey:@"Number"] intValue] - 1;
    
    //increment
    index++;

    //set hole
    if ([gr state] == UIGestureRecognizerStateBegan) {
        if (index < allHoles.count) {
            hole = [self.currentCourse objectForKey:[allHoles objectAtIndex:index]];
        }else{
            hole = [self.currentCourse objectForKey:[allHoles objectAtIndex:0]];
        }
        [self updateHole];
    }
    
    //show ad once
    if (index == 5) {
        if (!hasShownAd) {
            Advertisement *ad = [[[NSBundle mainBundle] loadNibNamed:@"Advertisement" owner:self options:nil] objectAtIndex:0];
            [ad setFrame:CGRectMake(0, 0, 3*self.view.frame.size.width/4, 3*self.view.frame.size.height/4)];
            [ad setCenter:self.view.center];
            [self.view addSubview:ad];
            hasShownAd = YES;
        }
    }
}

-(void)cyclePreviousHole:(UIGestureRecognizer *)gr
{
    //get index of current hole
    int index = [[hole valueForKey:@"Number"] intValue]-1;
    
    //increment
    index--;

    if ([gr state] == UIGestureRecognizerStateBegan) {
        //set hole
        if (index >= 0) {
            hole = [self.currentCourse objectForKey:[allHoles objectAtIndex:index]];
        }else{
            hole = [self.currentCourse objectForKey:[allHoles objectAtIndex:allHoles.count-1]];
        }
        [self updateHole];
    }
}

-(void)quit
{
    UIAlertView *alert;
    
    if (golfer.holes.count < 10) {
        //continue or end
         alert = [[UIAlertView alloc] initWithTitle:@"Finish?" message:@"Continue to next course?" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Continue", @"Quit", nil];
    }else{
        //just end
        alert = [[UIAlertView alloc] initWithTitle:@"Quit" message:@"Are you sure you want to quit?" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Quit", nil];
    }
    
    [alert show];
}


-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if ([[alertView buttonTitleAtIndex:buttonIndex] isEqualToString:@"Quit"]) {
        
        //delete round if no data saved
        [self checkToDelete];
        [self.presentingViewController dismissViewControllerAnimated:YES completion:nil];
        [self.presentingViewController.presentingViewController dismissViewControllerAnimated:YES completion:nil];
    }else if ([[alertView buttonTitleAtIndex:buttonIndex] isEqualToString:@"Continue"]){
        RoundSelectVC *rsvc = (RoundSelectVC *)self.presentingViewController;
        [rsvc setIsContinuing:YES];
        [rsvc continueRound:self.currentRound];
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}

- (IBAction)logStroke:(UIButton *)sender {
    
    if (popover != nil) {
        [self closePopover];
    }else{

        strokeLog = [[[NSBundle mainBundle] loadNibNamed:@"StrokeLog" owner:self options:nil] objectAtIndex:0];
        [strokeLog setFrame:CGRectMake(0, 0, self.view.frame.size.width - 16, self.view.frame.size.height/2)];
        [strokeLog setCenter:self.view.center];
        
        if ([[hole objectForKey:@"Par"] integerValue] == 3) {
            [strokeLog setIsPar3:YES];
        }else{
            [strokeLog setIsPar3:NO];
        }
        
        popover = (UIView *)strokeLog;
        [self.view addSubview:strokeLog];

        //check for data to display if not new hole
        for (Hole *h in [golfer valueForKey:@"holes"]) {
            //check for current hole
            if ([[h valueForKey:@"number"]isEqual:[NSNumber numberWithInteger:[[hole valueForKey:@"Number"] integerValue]]] && [[h valueForKey:@"course"]isEqualToString:self.currentCourseName]) {
                [strokeLog setValuesForHole:h];
                break;
            }
        }
    }
}

-(void)closePopover
{
    if (strokeLog != nil) {
        strokeLog = nil;
    }
    if (moviePlayer != nil) {
        moviePlayer = nil;
    }

    [popover removeFromSuperview];
    popover = nil;
}

- (IBAction)toggleHistory:(UIButton *)sender {
    [rangeFinder setShouldShowHistory:!rangeFinder.shouldShowHistory];
}

- (IBAction)showHoleMedia:(UIButton *)sender {
    
    if (popover != nil){
        [self closePopover];
    }else{
        //set popover
        popover = [[UIView alloc] initWithFrame:CGRectMake(0, 0, self.view.frame.size.width - 50, self.view.frame.size.height/3)];
        [popover setBackgroundColor:[UIColor colorWithRed:.94 green:.95 blue:.95 alpha:1]];
        popover.layer.shadowColor = [[UIColor blackColor] CGColor];
        popover.layer.shadowOffset =  CGSizeMake(-5.0, 10.0);
        popover.layer.shadowRadius = 8.0;
        popover.layer.shadowOpacity = 1;
        
        NSString *videoName = [self.currentCourseName stringByAppendingString:[hole objectForKey:@"Number"]];
        NSString *filePath = [[NSBundle mainBundle] pathForResource:videoName ofType:@"mp4"];
        NSURL *urlPath = [NSURL fileURLWithPath:filePath];
        
        moviePlayer = [[MPMoviePlayerController alloc] initWithContentURL:urlPath];
        [moviePlayer setScalingMode:MPMovieScalingModeAspectFit];
        [moviePlayer setMovieSourceType:MPMovieSourceTypeFile];
        [moviePlayer setControlStyle:MPMovieControlStyleEmbedded];
        [moviePlayer setFullscreen:NO];
        [moviePlayer setShouldAutoplay:NO];
        [moviePlayer prepareToPlay];
        [moviePlayer.view setFrame:CGRectMake(0, 0, popover.frame.size.width-8, popover.frame.size.height-8)];
        [moviePlayer.view setCenter:popover.center];
        
        [popover setCenter:self.view.center];
        [self.view addSubview:popover];
        [popover addSubview:moviePlayer.view];
        
        [moviePlayer play];
    }
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    UITouch *t = [touches anyObject];
    CGPoint tap = [t locationInView:self.view];
    
    if (!moviePlayer.isFullscreen) {
        if  (!CGRectContainsPoint(popover.frame, tap)){
            if (popover != nil) {
                [self closePopover];
            }
        }
    }
}

-(void)checkToDelete
{
    BOOL delete = YES;
    
    for (Hole *h in [golfer valueForKey:@"holes"]) {
        //check for current hole
        if ([[h valueForKey:@"score"] integerValue] > 0) {
            delete = NO;
            break;
        }
    }
    
    if ([[golfer valueForKey:@"holes"]count] < 9){
        delete = YES;
    }
    
    if (delete) {
        AppDelegate *delegate = [UIApplication sharedApplication].delegate;
        [delegate.managedObjectContext deleteObject:self.currentRound];
        [delegate saveContext];
    }
    
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([segue.identifier isEqualToString:@"ShowScorecard"]) {
        //initialize holeVC
        ScorecardVC *svc = [segue destinationViewController];
        svc.isRoundFinished = NO;
        [svc setRound:self.currentRound andGolfer:golfer forCourse:self.currentCourseName];
    }
}

/*
-(BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)orientation {
    if (moviePlayer.isFullscreen) {
        return UIInterfaceOrientationLandscapeLeft;
    }
    return UIInterfaceOrientationPortrait;
}

-(BOOL)shouldAutorotate
{
    return YES;
}

 - (NSUInteger)supportedInterfaceOrientations {
 if (moviePlayer.isFullscreen) {
 return UIInterfaceOrientationMaskLandscape;
 }
 return UIInterfaceOrientationMaskPortrait;
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
 
 -(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
 {
 return golfers.count;
 }
 
 -(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
 {
 ScoreCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Score" forIndexPath:indexPath];
 Golfer *golfer = [golfers objectAtIndex:indexPath.row];
 
 for (Hole *h in [golfer valueForKey:@"holes"]) {
 //check for current hole
 if ([[h valueForKey:@"number"]isEqual:[NSNumber numberWithInteger:[[hole valueForKey:@"Number"] integerValue]]]) {
 [cell.scoreLabel setText:[NSString stringWithFormat:@"%@", h.score]];
 }
 }
 [cell.nameLabel setText:[golfer name]];
 
 return cell;
 }
 
 -(void)loadGolfers
 {
 //get current round
 Round *round = [self getCurrentRound];
 
 //init array
 golfers = [[NSMutableArray alloc] init];
 
 //add golfers over from round
 for (Golfer *golfer in [round valueForKey:@"golfers"]){
 [golfers addObject:golfer];
 }
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
 */
@end
