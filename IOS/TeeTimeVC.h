//
//  TeeTimeVC.h
//  Golf
//
//  Created by Adam Wilson on 6/11/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ProfileHeaderBar.h"

@interface TeeTimeVC : UIViewController <UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate,UIPickerViewDataSource, UIPickerViewDelegate,UIGestureRecognizerDelegate>
{
    __weak IBOutlet UITableView *golfersTableView;
    __weak IBOutlet UILabel *monthLabel;
    __weak IBOutlet UIImageView *openTeeTimeImage;
    __weak IBOutlet UIButton *coursesButton;
    __weak IBOutlet UIButton *timeButton;
    __weak IBOutlet UIImageView *successImage;
}

- (IBAction)cycleHour:(UIButton *)sender;
- (IBAction)cycleMinute:(UIButton *)sender;
- (IBAction)togglePeriod:(UIButton *)sender;

- (IBAction)selectDayOfMonth:(UIButton *)sender;

- (IBAction)nextMonth:(UIButton *)sender;
- (IBAction)previousMonth:(UIButton *)sender;

- (IBAction)confirmTeeTime:(UIButton *)sender;
- (IBAction)changeCartAmount:(UIButton *)sender;
- (IBAction)setTime:(id)sender;

- (IBAction)displayCourses:(id)sender;
-(void)setCourse:(NSString *)courseName;
@end
