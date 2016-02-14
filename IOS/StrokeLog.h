//
//  StrokeLog.h
//  Golf
//
//  Created by Adam Wilson on 8/6/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Hole.h"

@interface StrokeLog : UIView
{
    __weak IBOutlet UILabel *holeLabel;
    __weak IBOutlet UIButton *scoreButton;
    __weak IBOutlet UIButton *puttsButton;
    __weak IBOutlet UIButton *leftFairwayButton;
    __weak IBOutlet UIButton *centerFairwayButton;
    __weak IBOutlet UIButton *rightFairwayButton;
    __weak IBOutlet UIButton *leftGreenButton;
    __weak IBOutlet UIButton *centerGreenButton;
    __weak IBOutlet UIButton *rightGreenButton;
}
@property(nonatomic) BOOL isPar3;
@property (nonatomic) int score;
@property (nonatomic) int putts;
@property (nonatomic, weak) NSString *fairwayHit;
@property (nonatomic, weak) NSString *greenHit;

-(void)setValuesForHole:(Hole *)hole;
- (IBAction)setSelected:(UIButton *)sender;
- (IBAction)increment:(UIButton *)sender;
@end
