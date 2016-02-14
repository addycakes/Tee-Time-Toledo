//
//  RoundSelectVC.h
//  Golf
//
//  Created by Adam Wilson on 6/18/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ProfileHeaderBar.h"

@interface RoundSelectVC : UIViewController //<UITableViewDataSource, UITableViewDelegate>
{
    __weak IBOutlet UITableView *coursesTableView;
    //__weak IBOutlet UITableView *golfersTableView;
    
    NSManagedObjectContext *managedObjectContext;
    ProfileHeaderBar *profileBar;
}
@property (nonatomic) BOOL isContinuing;
@property (nonatomic) NSArray *teetimeGolfers;
@property (nonatomic) NSString *teetimeCourse;

-(void)continueRound:(Round *)currentRound;
-(void)startRound;
@end
