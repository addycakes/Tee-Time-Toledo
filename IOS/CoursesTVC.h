//
//  CoursesTVC.h
//  Golf
//
//  Created by Adam Wilson on 7/29/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CoursesTVC : UITableViewController <UIAlertViewDelegate>
@property (nonatomic) NSString *selectedCourse;
@property (nonatomic, weak) NSString *golfer;

-(NSDictionary *)courseHoles;
-(instancetype)initWithParent:(UIViewController *)parent;
@end
