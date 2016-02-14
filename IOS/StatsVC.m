//
//  StatsVC.m
//  Golf
//
//  Created by Adam Wilson on 6/11/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "StatsVC.h"
#import "ProfileHeaderBar.h"
#import "AppDelegate.h"
#import "Golfer.h"
#import "Hole.h"
#import "Stroke.h"
#import "CoursesTVC.h"
#import "ScorecardVC.h"

@interface StatsVC ()
{
    AppDelegate *delegate;
    
    BOOL didAnimatePlotsGraph;
    BOOL didAnimateBarGraph;
    BOOL didAnimateCirclesGraph;
    
    NSMutableArray *plotGraphScores;
    NSMutableArray *barGraphPercents;
    NSMutableArray *circlesGraphPercents;
    NSString *averageScore;
    
    ProfileHeaderBar *profileBar;
    Golfer *profileGolfer;
    UIView *options;
    
    CoursesTVC *coursesTVC;
    UITableView *coursesTableView;
    
    int scoreForAllRounds;
    int numRounds;
    int totalPars;
    int totalBogies;
    int totalBirdies;
    int totalStrokes;
    float totalLeftFairways;
    float totalRightFairways;
    float totalCenterFairways;
    float totalFairwayHits;
    float totalLeftGreens;
    float totalRightGreens;
    float totalCenterGreens;
    float totalGreensHits;
    int roundOne;
    int roundTwo;
    int roundThree;
    int roundFour;
    int roundFive;
}
@end

@implementation StatsVC

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    delegate = [UIApplication sharedApplication].delegate;
    managedObjectContext = [delegate managedObjectContext];

    //create the profile bar
    profileBar = [ProfileHeaderBar sharedProfileBar];
    
    //swipe back to return to home screen
    UIScreenEdgePanGestureRecognizer *swipe = [[UIScreenEdgePanGestureRecognizer alloc] initWithTarget:self action:@selector(unwind:)];
    [swipe setEdges:UIRectEdgeLeft];
    [self.view addGestureRecognizer:swipe];

    //init values to singlefire animations
    didAnimatePlotsGraph = NO;
    didAnimateBarGraph = NO;
    didAnimateCirclesGraph = NO;
    
    //get values from database for labels
    [self getValuesForLabels];
    
    //animate text value for pie graph on load
    //its the first visible graph
    if (totalStrokes != 0){
        [pieWheel setBirdiePercent:((float)totalBirdies/(float)totalStrokes)];
        [pieWheel setParPercent:((float)totalPars/(float)totalStrokes)];
        [pieWheel setBogiePercent:((float)totalBogies/(float)totalStrokes)];
    }
    

    NSNumber *average;
    if (numRounds == 0){
        average = [NSNumber numberWithInt:0];;
    }else{
        average = [NSNumber numberWithInt:(scoreForAllRounds/numRounds)];
    }
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
        NSTimer *timer = [NSTimer scheduledTimerWithTimeInterval:.1 target:self selector:@selector(animateNumbersInLabel:) userInfo:@[averageScoreLabel,average.stringValue] repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSDefaultRunLoopMode];
        [[NSRunLoop currentRunLoop] run];
    });
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:YES];
    
    //unset profile bar items
    [profileBar setShouldShowBackButton:NO];
    [profileBar setShouldShowOptionsButton:NO];
    [profileBar removeFromSuperview];

    [profileBar.profileBackButton removeTarget:self action:@selector(unwind:) forControlEvents:UIControlEventTouchDown];
}

#define GRAPH_OFFSET 25
-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];

    //setup profile button
    [profileBar setShouldShowOptionsButton:YES];
    [profileBar setShouldShowBackButton:YES];
    [self.view addSubview:profileBar];
    [self.view bringSubviewToFront:profileBar];
    [profileBar setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.view.frame.size.width, PROFILE_BAR_HEIGHT)];

    UIButton *scorecardButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [scorecardButton setTitle:@"Scorecards" forState:UIControlStateNormal];
    [scorecardButton addTarget:self action:@selector(showScorecardTableView) forControlEvents:UIControlEventTouchUpInside];
    [profileBar.optionsArray addObject:scorecardButton];

    [profileBar.profileBackButton addTarget:self action:@selector(unwind:) forControlEvents:UIControlEventTouchDown];

    [scrollView setContentSize:CGSizeMake(self.view.frame.size.width, 1700)];
    [scrollView setDelegate:self];
    
    if (!didAnimateBarGraph) {
        [leftFairwayBar setFrame:CGRectMake(leftFairwayBar.frame.origin.x, legend.frame.origin.y - leftFairwayBar.frame.size.height/2 - GRAPH_OFFSET ,leftFairwayBar.frame.size.width, leftFairwayBar.frame.size.height)];
        [rightFairwayBar setFrame:CGRectMake(rightFairwayBar.frame.origin.x, legend.frame.origin.y - rightFairwayBar.frame.size.height/2 - GRAPH_OFFSET ,rightFairwayBar.frame.size.width, rightFairwayBar.frame.size.height)];
        [centerFairwayBar setFrame:CGRectMake(centerFairwayBar.frame.origin.x, legend.frame.origin.y - centerFairwayBar.frame.size.height/2 - GRAPH_OFFSET ,centerFairwayBar.frame.size.width, centerFairwayBar.frame.size.height)];
    }
    
    if (!didAnimatePlotsGraph) {
        //add backgrounds to plots
        NSArray *plots = @[fifthPreviousRoundLabel,fourthPreviousRoundLabel,thirdPreviousRoundLabel,secondPreviousRoundLabel, firstPreviousRoundLabel];
        for (int i = 0; i < plots.count; i++){
            UILabel *plotLabel = (UILabel *)plots[i];
            //add plot view behind label
            //plot covers text, so add a new label on top
            UILabel *labelCopy = [[UILabel alloc] initWithFrame:plotLabel.bounds];
            [labelCopy setText:plotLabel.text];
            [labelCopy setTextColor:plotLabel.textColor];
            [labelCopy setFont:plotLabel.font];
            [labelCopy setTextAlignment:plotLabel.textAlignment];
            
            Plot *plotBG = [[Plot alloc] initWithFrame:plotLabel.bounds];
            [plotBG setParentView:plotsGraph];
            [plotBG setOpaque:NO];
            [plotLabel addSubview:plotBG];
            [plotLabel addSubview:labelCopy];
        }
    }
}

-(void)getValuesForLabels
{
    scoreForAllRounds = 0;
    numRounds = 0;
    
    totalPars = 0;
    totalBogies = 0;
    totalBirdies = 0;
    totalStrokes = 0;
    
    totalLeftFairways = 0;
    totalRightFairways = 0;
    totalCenterFairways = 0;
    totalFairwayHits = 0;
    
    totalLeftGreens = 0;
    totalRightGreens = 0;
    totalCenterGreens = 0;
    totalGreensHits = 0;
    
    roundOne = 0;
    roundTwo = 0;
    roundThree = 0;
    roundFour = 0;
    roundFive = 0;

    //look up database entries
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription
                                   entityForName:@"Round" inManagedObjectContext:managedObjectContext];
    [fetchRequest setEntity:entity];
    
    NSError *error;
    NSArray *fetchedObjects = [managedObjectContext executeFetchRequest:fetchRequest error:&error];
    NSSortDescriptor *dateDescriptor = [[NSSortDescriptor alloc] initWithKey:@"date" ascending:NO];
    NSArray *sortedRounds = [fetchedObjects sortedArrayUsingDescriptors:@[dateDescriptor]];
    
    int previousRound = 0;
    for (Round *round in sortedRounds) {
        for (Golfer *golfer in round.golfers.allObjects){
            if ([golfer.name isEqualToString:profileBar.profileNameLabel.text]) {
                profileGolfer = golfer;
                numRounds++;
                for (Hole *hole in golfer.holes.allObjects) {
                    //accumulate score
                    scoreForAllRounds += hole.score.intValue;
                    
                    //get 5 previous rounds scores
                    if (previousRound == 4) {
                        roundFive += hole.score.intValue;
                    }else  if (previousRound == 3) {
                        roundFour += hole.score.intValue;
                    }else  if (previousRound == 2) {
                        roundThree += hole.score.intValue;
                    }else  if (previousRound == 1) {
                        roundTwo += hole.score.intValue;
                    }else  if (previousRound == 0) {
                        roundOne += hole.score.intValue;
                    }
                    
                    //check for par, bogie, birdie
                    if (hole.score.intValue == hole.par.intValue) {
                        totalPars += 1;
                        totalStrokes++;
                    }else if (hole.score.intValue == hole.par.intValue + 1) {
                        totalBogies += 1;
                        totalStrokes++;
                    }else if (hole.score.intValue == hole.par.intValue - 1) {
                        totalBirdies += 1;
                        totalStrokes++;
                    }
                    
                    //accumulate greens hits
                    if ([hole.greensHit isEqualToString:@"Left"]) {
                        totalLeftGreens += 1;
                        totalGreensHits++;
                    }else if ([hole.greensHit isEqualToString:@"Right"]) {
                        totalRightGreens += 1;
                        totalGreensHits++;
                    }else if ([hole.greensHit isEqualToString:@"Center"]) {
                        totalCenterGreens += 1;
                        totalGreensHits++;
                    }
                    
                    //accumulate fairway hits
                    if ([hole.fairwayHit isEqualToString:@"Left"]) {
                        totalLeftFairways += 1;
                        totalFairwayHits++;
                    }else if ([hole.fairwayHit isEqualToString:@"Right"]) {
                        totalRightFairways += 1;
                        totalFairwayHits++;
                    }else if ([hole.fairwayHit isEqualToString:@"Center"]) {
                        totalCenterFairways += 1;
                        totalFairwayHits++;
                    }
                }
            }
        }
        previousRound++;
    }

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)unwind:(UIGestureRecognizer *)gr
{
    if (([gr state] == UIGestureRecognizerStateBegan)) {
        if  (coursesTVC != nil){
            //dismiss course table view controll
            [coursesTableView removeFromSuperview];
            coursesTVC = nil;
            coursesTableView = nil;
        }else{
            [self.presentingViewController dismissViewControllerAnimated:YES completion:nil];
        }
    }
}

-(void)scrollViewDidScroll:(UIScrollView *)sV
{

    CGRect visibleRect = CGRectMake(sV.contentOffset.x, sV.contentOffset.y, pieGraph.frame.size.width, pieGraph.frame.size.height);
    
    if(visibleRect.origin.y >= 200)
    {
        if (!didAnimateCirclesGraph) {
            [self animateCirclesGraph];
        }
    }
    
    if(visibleRect.origin.y >= 550)
    {
        if (!didAnimateBarGraph) {
            [self animateBarGraph];
            
        }
    }
    
    if(visibleRect.origin.y >= 900)
    {
        if (!didAnimatePlotsGraph) {
            [self animatePlotsGraph];
        }
    }
   
}

#define ONE_THIRD_HEIGHT 160
#define BAR_CEILING 140
#define BAR_SPACER 10
-(void)animateBarGraph
{
    didAnimateBarGraph = YES;

    NSArray *bars = @[leftFairwayBar, centerFairwayBar, rightFairwayBar];
    NSArray *barLabels = @[leftFairwayLabel, centerFairwayLabel, rightFairwayLabel];

    NSArray *percents = @[[NSNumber numberWithFloat:(totalLeftFairways/totalFairwayHits*100)],[NSNumber numberWithFloat:(totalCenterFairways/totalFairwayHits*100)],[NSNumber numberWithFloat:(totalRightFairways/totalFairwayHits*100)]];
    
    //animate text values for bars
    for (int i = 0; i < bars.count; i++) {
        Bar *bar = (Bar *)bars[i];
        UILabel *barLabel = (UILabel *)barLabels[i];
        NSNumber *percent = percents[i];
        
        //set label.text to 10 +/- the score in scoreHistory for animation
        int percentForLabel;
        if (percent.intValue - 10 < 0) {
            percentForLabel = (percent.intValue + 10);
        }else{
            percentForLabel = (percent.intValue - 10);
        }
        [barLabel setText:[NSString stringWithFormat:@"%d%%", percentForLabel]];

        //increment the text
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
            NSTimer *timer = [NSTimer scheduledTimerWithTimeInterval:.1 target:self selector:@selector(animateNumbersInLabel:) userInfo:@[barLabels[i],percent.stringValue] repeats:YES];
            [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSDefaultRunLoopMode];
            [[NSRunLoop currentRunLoop] run];
        });
        
        float delta = percent.intValue * 2.5 - ONE_THIRD_HEIGHT;
        
        //adjust labels and bars by delta
        [UILabel animateWithDuration:1 animations:^{
            [barLabel setFrame:CGRectMake(barLabel.frame.origin.x, barLabel.frame.origin.y - delta - GRAPH_OFFSET, barLabel.frame.size.width, barLabel.frame.size.height)];
        } completion:nil];
        
        [UIView animateWithDuration:1 animations:^{
            [bar setFrame:CGRectMake(bar.frame.origin.x, legend.frame.origin.y - bar.frame.size.height/2 - GRAPH_OFFSET ,bar.frame.size.width, bar.frame.size.height)];
        } completion:nil];
        
        [bar adjustSize:delta];
    }
}

-(void)animateCirclesGraph
{
    didAnimateCirclesGraph = YES;
    
    NSArray *circles = @[leftGreenCircle,centerGreenCircle,rightGreenCircle];
    NSArray *percents = @[[NSNumber numberWithFloat:totalLeftGreens/totalGreensHits*100],[NSNumber numberWithFloat:totalCenterGreens/totalGreensHits*100],[NSNumber numberWithFloat:totalRightGreens/totalGreensHits*100]];
    NSArray *circleLabels = @[leftGreenLabel, centerGreenLabel, rightGreenLabel];

    for (int i = 0; i < circles.count; i++){
        UILabel *circleLabel = (UILabel *)circleLabels[i];
        NSNumber *percent = (NSNumber *)percents[i];
        Circle *circle = (Circle *)circles[i];
        
        //set label.text to 10 +/- the score in scoreHistory for animation
        int scoreForLabel;
        if (percent.intValue - 10 < 0) {
            scoreForLabel = (percent.intValue + 10);
        }else{
            scoreForLabel = (percent.intValue - 10);
        }
        [circleLabel setText:[NSString stringWithFormat:@"%d%%", scoreForLabel]];
        
        //animate text value increasing
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
            NSTimer *timer = [NSTimer scheduledTimerWithTimeInterval:.1 target:self selector:@selector(animateNumbersInLabel:) userInfo:@[circleLabel, percent.stringValue] repeats:YES];
            [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSDefaultRunLoopMode];
            [[NSRunLoop currentRunLoop] run];
        });
        
        [circle adjustSize:percent.floatValue];
    }
}

#define BOTTOM_SCORE 120
-(void)animatePlotsGraph
{
    didAnimatePlotsGraph = YES;
    
    NSArray *plots = @[fifthPreviousRoundLabel,fourthPreviousRoundLabel,thirdPreviousRoundLabel,secondPreviousRoundLabel, firstPreviousRoundLabel];
    //get actual scores from DB
    NSArray *scoreHistory = @[[NSNumber numberWithInt:roundFive],[NSNumber numberWithInt:roundFour],
                              [NSNumber numberWithInt:roundThree],[NSNumber numberWithInt:roundTwo], [NSNumber numberWithInt:roundOne]];
    
    int delta = 0;
    for (int i = 0; i < plots.count; i++){
        UILabel *plotLabel = (UILabel *)plots[i];
        NSNumber *finalScore = scoreHistory[i];
        
        //set label.text to 10 +/- the score in scoreHistory for animation
        int scoreForLabel;
        if (finalScore.intValue - 10 < 0) {
            scoreForLabel = (finalScore.intValue + 10);
        }else{
            scoreForLabel = (finalScore.intValue - 10);
        }
        [plotLabel setText:[NSString stringWithFormat:@"%d", scoreForLabel]];

        Plot *plotBG;
        //get plot view behind label
        UILabel *labelCopy;
        for (UIView *view in plotLabel.subviews) {
            if ([view isKindOfClass:[Plot class]]) {
                plotBG = (Plot *)view;
            }else if ([view isKindOfClass:[UILabel class]]){
                labelCopy = (UILabel *)view;
            }
        }
        
        //animate text value increasing
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
            NSTimer *timer = [NSTimer scheduledTimerWithTimeInterval:.1 target:self selector:@selector(animateNumbersInLabel:) userInfo:@[labelCopy, finalScore.stringValue] repeats:YES];
            [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSDefaultRunLoopMode];
            [[NSRunLoop currentRunLoop] run];
        });

        //use text value to calculate delta for animating frames along y axis
        int score = finalScore.intValue;
        delta = (BOTTOM_SCORE - score * 1.5);
        
        [UILabel animateWithDuration:1 animations:^{
            [plotLabel setFrame:CGRectMake(plotLabel.frame.origin.x, plotLabel.frame.origin.y+delta, plotLabel.frame.size.width, plotLabel.frame.size.height)];
        } completion:^(BOOL finished) {
            
            //connect dots
            if (i+1 < plots.count) {
                UILabel *nextPlotLabel = (UILabel *)plots[i+1];
                [plotBG connectTo:nextPlotLabel.center];
            }
        }];
    }
}

-(void)animateNumbersInLabel:(NSTimer *)timer
{
    //get score value from label; remove % that might be present in text
    NSArray *labelAndScore = (NSArray *)timer.userInfo;
    UILabel *label = (UILabel *)labelAndScore[0];
    NSString *finalScore = (NSString *)labelAndScore[1];
    
    NSMutableString *labelText = [NSMutableString stringWithString:label.text];
    BOOL hasPercentSign = NO;
    if ([labelText rangeOfString:@"%"].location != NSNotFound) {
        [labelText replaceOccurrencesOfString:@"%" withString:@"" options:NSCaseInsensitiveSearch range:NSRangeFromString([NSString stringWithFormat:@"%lu", (unsigned long)labelText.length])];
        hasPercentSign = YES;
    }
    
    //get value of label
    int value = [labelText intValue];
    if (value < finalScore.intValue) {
        value++;
    }else if (value > finalScore.intValue) {
        value--;
    }else{
        [timer invalidate];
    }
    
    if (hasPercentSign) {
        [label setText:[NSString stringWithFormat:@"%d%%", value]];
    }else{
        [label setText:[NSString stringWithFormat:@"%d", value]];
    }
}

-(void)showScorecardTableView
{
    //create tableview
    coursesTableView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStyleGrouped];
    
    //display course table view controller
    coursesTVC = [[CoursesTVC alloc] initWithParent:self];
    [coursesTVC setTableView:coursesTableView];
    [coursesTVC setGolfer:profileBar.profileNameLabel.text];
    
    [self.view addSubview:coursesTableView];
    [self.view bringSubviewToFront:coursesTableView];
    
    //dismiss options window
    [profileBar dismissOptionsMenu];

}

-(void)showScorecard:(Round *)selectedRound withCourse:(NSString *)course
{
    //dismiss course table view controll
    [coursesTableView removeFromSuperview];
    coursesTVC = nil;
    coursesTableView = nil;
        
    //initialize holeVC
    ScorecardVC *svc = [self.storyboard instantiateViewControllerWithIdentifier:@"ScoreCard"];
    svc.isRoundFinished = YES;
    [svc setRound:selectedRound andGolfer:profileGolfer forCourse:course];
    [self presentViewController:svc animated:YES completion:nil];
}

- (NSUInteger)supportedInterfaceOrientations {
    if (delegate.shouldForceLandscape) {
        return UIInterfaceOrientationMaskLandscape;
    }
    return UIInterfaceOrientationMaskPortrait;
}

@end
