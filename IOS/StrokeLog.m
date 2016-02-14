//
//  StrokeLog.m
//  Golf
//
//  Created by Adam Wilson on 8/6/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "StrokeLog.h"

@interface StrokeLog()
{
    BOOL isFairwayHitSet;
    BOOL isGreenHitSet;
    BOOL isHitSet;
}
@end

@implementation StrokeLog


// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
    self.layer.shadowColor = [[UIColor blackColor] CGColor];
    self.layer.shadowOffset =  CGSizeMake(-5.0, 10.0);
    self.layer.shadowRadius = 8.0;
    self.layer.shadowOpacity = 1;
    
    if (self.isPar3) {
        [leftFairwayButton setEnabled:NO];
        [rightFairwayButton setEnabled:NO];
        [centerFairwayButton setEnabled:NO];
    }/*else{
        [leftGreenButton setEnabled:NO];
        [rightGreenButton setEnabled:NO];
        [centerGreenButton setEnabled:NO];
    }*/
    
    //isFairwayHitSet = NO;
    //isGreenHitSet = NO;
    isHitSet = NO;
    //self.greenHit = @"";
    //self.fairwayHit = @"";
}

//fairway buttons have tag == 1
//greens buttons have tag == 2
- (IBAction)setSelected:(UIButton *)sender {
    //if selected, deselect
    if (sender.isSelected) {
        [sender setSelected:NO];
        
        //set values
        if ([sender isEqual:leftGreenButton] ||
            [sender isEqual:rightGreenButton] ||
            [sender isEqual:centerGreenButton]) {
            //isGreenHitSet = NO;
            self.greenHit = @"";
        }
        if ([sender isEqual:leftFairwayButton] ||
            [sender isEqual:rightFairwayButton] ||
            [sender isEqual:centerFairwayButton]) {
            //isFairwayHitSet = NO;
            self.fairwayHit = @"";
        }

        isHitSet = NO;
    }else{
        //only set if hit isn't yet set
        if (!isHitSet) {
            if ([sender isEqual:leftGreenButton] ||
                [sender isEqual:rightGreenButton] ||
                [sender isEqual:centerGreenButton]) {
                //if (!isGreenHitSet) {
                [sender setSelected:YES];
                //isGreenHitSet = YES;
                isHitSet = YES;
                if ([sender isEqual:leftGreenButton]) {
                    self.greenHit = @"Left";
                }else if ([sender isEqual:rightGreenButton]) {
                    self.greenHit = @"Right";
                }else if ([sender isEqual:centerGreenButton]) {
                    self.greenHit = @"Center";
                }
                //}
            }
            if ([sender isEqual:leftFairwayButton] ||
                [sender isEqual:rightFairwayButton] ||
                [sender isEqual:centerFairwayButton]) {
                //if (!isFairwayHitSet) {
                [sender setSelected:YES];
                isHitSet = YES;
                //    isFairwayHitSet = YES;
                    
                if ([sender isEqual:leftFairwayButton]) {
                    self.fairwayHit = @"Left";
                }else if ([sender isEqual:rightFairwayButton]) {
                    self.fairwayHit = @"Right";
                }else if ([sender isEqual:centerFairwayButton]) {
                    self.fairwayHit = @"Center";
                }
                //}
            }
        }
    }
    
    NSNotification *note = [NSNotification notificationWithName:@"ScoreChanged" object:nil];
    [[NSNotificationCenter defaultCenter] postNotification:note];
}

#define MAX_SCORE 8
- (IBAction)increment:(UIButton *)sender {
    int value = sender.titleLabel.text.intValue;
    
    if (value < MAX_SCORE) {
        value++;
    }else{
        value = 0;
    }
    
    [sender setTitle:[NSString stringWithFormat:@"%d",value] forState:UIControlStateNormal];
    
    if ([sender isEqual:scoreButton]) {
        self.score = value;
    }else{
        self.putts = value;
    }
        
    NSNotification *note = [NSNotification notificationWithName:@"ScoreChanged" object:nil];
    [[NSNotificationCenter defaultCenter] postNotification:note];
}

-(void)setValuesForHole:(Hole *)hole
{
    [holeLabel setText:[NSString stringWithFormat:@"Hole #%@",hole.number]];
    [scoreButton setTitle:[NSString stringWithFormat:@"%@",hole.score] forState:UIControlStateNormal];
    [puttsButton setTitle:[NSString stringWithFormat:@"%@",hole.putts] forState:UIControlStateNormal];
    
    self.score = hole.score.intValue;
    self.putts = hole.putts.intValue;
    
    //par threes enable greens hit, four and fives enable both
    if (!self.isPar3) {
        if ([hole.fairwayHit isEqualToString:@"Left"]) {
            [leftFairwayButton setSelected:YES];
            self.fairwayHit = @"Left";
        }else if ([hole.fairwayHit isEqualToString:@"Right"]) {
            [rightFairwayButton setSelected:YES];
            self.fairwayHit = @"Right";
        }else if ([hole.fairwayHit isEqualToString:@"Center"]) {
            [centerFairwayButton setSelected:YES];
            self.fairwayHit = @"Center";
        }
    }
    
    if ([hole.greensHit isEqualToString:@"Left"]) {
        [leftGreenButton setSelected:YES];
        self.greenHit = @"Left";
    }else if ([hole.greensHit isEqualToString:@"Right"]) {
        [rightGreenButton setSelected:YES];
        self.greenHit = @"Right";
    }else if ([hole.greensHit isEqualToString:@"Center"]) {
        [centerGreenButton setSelected:YES];
        self.greenHit = @"Center";
    }
}
@end
