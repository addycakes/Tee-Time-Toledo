//
//  ScoreCell.h
//  Golf
//
//  Created by Adam Wilson on 6/19/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ScoreCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *nameLabel;
@property (weak, nonatomic) IBOutlet UILabel *scoreLabel;
@property (weak, nonatomic) IBOutlet UIStepper *scoreStepper;

- (IBAction)adjustScore:(UIStepper *)sender;
@end
