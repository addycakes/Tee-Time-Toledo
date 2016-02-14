//
//  StatsVC.h
//  Golf
//
//  Created by Adam Wilson on 6/11/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PieWheelGraph.h"
#import "Circle.h"
#import "Bar.h"
#import "Plot.h"
#import "Round.h"

@interface StatsVC : UIViewController <UIScrollViewDelegate>
{
    __weak IBOutlet UIScrollView *scrollView;
    NSManagedObjectContext *managedObjectContext;

    //pieGraph
    __weak IBOutlet UIView *pieGraph;
    __weak IBOutlet PieWheelGraph *pieWheel;
    __weak IBOutlet UILabel *averageScoreLabel;
    
    //circlesGraph
    __weak IBOutlet UIView *circlesGraph;
    __weak IBOutlet Circle *rightGreenCircle;
    __weak IBOutlet Circle *centerGreenCircle;
    __weak IBOutlet Circle *leftGreenCircle;
    __weak IBOutlet UILabel *centerGreenLabel;
    __weak IBOutlet UILabel *rightGreenLabel;
    __weak IBOutlet UILabel *leftGreenLabel;
    
    //barGraph
    __weak IBOutlet UIView *barGraph;
    __weak IBOutlet Bar *rightFairwayBar;
    __weak IBOutlet Bar *centerFairwayBar;
    __weak IBOutlet Bar *leftFairwayBar;
    __weak IBOutlet UILabel *rightFairwayLabel;
    __weak IBOutlet UILabel *centerFairwayLabel;
    __weak IBOutlet UILabel *leftFairwayLabel;
    __weak IBOutlet UILabel *legend;
    
    //plotGraph
    __weak IBOutlet UIView *plotsGraph;
    __weak IBOutlet UILabel *fifthPreviousRoundLabel;
    __weak IBOutlet UILabel *fourthPreviousRoundLabel;
    __weak IBOutlet UILabel *thirdPreviousRoundLabel;
    __weak IBOutlet UILabel *secondPreviousRoundLabel;
    __weak IBOutlet UILabel *firstPreviousRoundLabel;
    
}

-(void)showScorecard:(Round *)selectedRound withCourse:(NSString *)course;

@end
