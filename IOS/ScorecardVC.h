//
//  ScorecardVC.h
//  Golf
//
//  Created by Adam Wilson on 6/11/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Round.h"
#import "Golfer.h"

@interface ScorecardVC : UIViewController //<UIGestureRecognizerDelegate>
{
    __weak IBOutlet UILabel *g1Name;
    __weak IBOutlet UILabel *g2Name;
    __weak IBOutlet UILabel *g3Name;
    __weak IBOutlet UILabel *g4Name;
    __weak IBOutlet UIImageView *scorecardBody;
}
@property (nonatomic) BOOL isRoundFinished;
-(void)setRound:(Round *)round andGolfer:(Golfer *)golfer forCourse:(NSString *)courseName;

@end
