//
//  HoleVC.h
//  Golf
//
//  Created by Adam Wilson on 6/19/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Round.h"
#import "Golfer.h"
@interface HoleVC : UIViewController </*UITableViewDataSource, UITableViewDelegate,*/ UIAlertViewDelegate>
{
    __weak IBOutlet UILabel *courseNameLabel;
    __weak IBOutlet UILabel *holeNumberLable;
    __weak IBOutlet UILabel *distanceLabel;
    __weak IBOutlet UILabel *parLabel;
    
    NSManagedObjectContext *managedObjectContext;
}
@property (nonatomic, strong) Round *currentRound;
@property (nonatomic, strong) NSDictionary *currentCourse;
@property (nonatomic, strong) NSString *currentCourseName;

- (IBAction)logStroke:(UIButton *)sender;
- (IBAction)toggleHistory:(UIButton *)sender;
- (IBAction)showHoleMedia:(UIButton *)sender;

@end
