//
//  ProfileHeaderBar.h
//  Golf
//
//  Created by Adam Wilson on 7/12/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Profile.h"
#import "Round.h"
#import "Golfer.h"
#import "Hole.h"

@interface ProfileHeaderBar : UIView 
@property (weak, nonatomic) IBOutlet UIImageView *profilePic;
@property (weak, nonatomic) IBOutlet UILabel *profileNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *profileHandicapLabel;
@property (weak, nonatomic) IBOutlet UIButton *profileButton;
@property (weak, nonatomic) IBOutlet UIButton *profileBackButton;
@property (nonatomic) BOOL shouldShowBackButton;
@property (nonatomic) BOOL shouldShowOptionsButton;
@property (strong, nonatomic) NSMutableArray *optionsArray;

+(ProfileHeaderBar *)sharedProfileBar;
-(void)changeProfile:(Profile *)newProfile;
-(void)reloadBar;
-(void)dismissOptionsMenu;

#define PROFILE_BAR_HEIGHT 100
@end
