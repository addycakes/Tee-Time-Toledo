//
//  ScoreCell.m
//  Golf
//
//  Created by Adam Wilson on 6/19/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "ScoreCell.h"

@implementation ScoreCell

- (void)awakeFromNib {
    // Initialization code
    //[self.scoreLabel setText:[NSString stringWithFormat:@"%.f",self.scoreStepper.value]];
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (IBAction)adjustScore:(UIStepper *)sender
{
    //reset stepper if score is zero
    if ([self.scoreLabel.text isEqualToString:@"0"]) {
        [self.scoreStepper setValue:1];
    }
    [self.scoreLabel setText:[NSString stringWithFormat:@"%.f",self.scoreStepper.value]];

    //post notification that data has changed
    NSNotification *note = [NSNotification notificationWithName:@"ScoreChanged" object:self];
    [[NSNotificationCenter defaultCenter] postNotification:note];
}

@end
