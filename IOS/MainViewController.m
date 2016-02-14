//
//  MainViewController.m
//  Golf
//
//  Created by Adam Wilson on 7/12/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "MainViewController.h"
#import "NewProfilePopOver.h"
#import "AppDelegate.h"

@interface MainViewController ()
{
    NewProfilePopOver *newProfilePopover;
    UIView *options;
    AppDelegate *delegate;
}
@end

@implementation MainViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    delegate = [UIApplication sharedApplication].delegate;
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:YES];
    //[self.profileBar.profileButton removeTarget:self action:@selector(changeOrCreateProfile) forControlEvents:UIControlEventTouchDown];
    [self.profileBar setShouldShowOptionsButton:NO];
    [self.profileBar removeFromSuperview];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
    NSLog(@"low memory");
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:YES];
    
    //create the profile bar
    if (self.profileBar == NULL) {
        self.profileBar = [[[NSBundle mainBundle] loadNibNamed:@"ProfileHeaderBar" owner:self options:nil] objectAtIndex:0];
        
        self.profileBar = [ProfileHeaderBar sharedProfileBar];
    }
    [self.profileBar reloadBar];
}

-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    
    //setup profile button
    [self.view addSubview:self.profileBar];
    [self.profileBar setShouldShowOptionsButton:YES];
    [self.view bringSubviewToFront:self.profileBar];
    [self.profileBar setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.view.frame.size.width, PROFILE_BAR_HEIGHT)];
    
    UIButton *createProtileButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [createProtileButton setTitle:@"New Profile" forState:UIControlStateNormal];
    [createProtileButton addTarget:self action:@selector(showProfileWindow:) forControlEvents:UIControlEventTouchUpInside];
    [self.profileBar.optionsArray addObject:createProtileButton];

    UIButton *switchProfileButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [switchProfileButton setTitle:@"Change Profile" forState:UIControlStateNormal];
    [switchProfileButton addTarget:self action:@selector(showProfileWindow:) forControlEvents:UIControlEventTouchUpInside];
    [self.profileBar.optionsArray addObject:switchProfileButton];
}

/*
#define OPTIONS_HEIGHT 44
#define OPTIONS_WIDTH 152
#define OPTIONS_BUFFER 20
-(void)changeOrCreateProfile
{
    if (options == nil) {
        options = [[UIView alloc] initWithFrame:CGRectMake(self.profileBar.frame.size.width-OPTIONS_WIDTH, self.profileBar.frame.size.height-(2*OPTIONS_HEIGHT), OPTIONS_WIDTH, (OPTIONS_HEIGHT*2)+OPTIONS_BUFFER)];
        [options setBackgroundColor:[UIColor colorWithRed:.93 green:.93 blue:.94 alpha:1]];
        //options.layer.shadowColor = [[UIColor blackColor] CGColor];
        //options.layer.shadowOffset =  CGSizeMake(-5.0, 10.0);
        //options.layer.shadowRadius = 8.0;
        //options.layer.shadowOpacity = 1;
        
        UIButton *createProtileButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [createProtileButton setTitle:@"New Profile" forState:UIControlStateNormal];
        [createProtileButton setFrame:CGRectMake(0, OPTIONS_HEIGHT+OPTIONS_BUFFER, OPTIONS_WIDTH, OPTIONS_HEIGHT)];
        [createProtileButton setFrame:CGRectMake(0, OPTIONS_BUFFER, OPTIONS_WIDTH, OPTIONS_HEIGHT)];
        
        [createProtileButton setTintColor:[UIColor blackColor]];
        [createProtileButton.titleLabel setTextAlignment:NSTextAlignmentRight];
        [createProtileButton setBackgroundColor:[UIColor colorWithRed:.93 green:.93 blue:.94 alpha:1]];
        [createProtileButton addTarget:self action:@selector(showProfileWindow:) forControlEvents:UIControlEventTouchUpInside];

        UIButton *switchProfileButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [switchProfileButton setTitle:@"Change Profile" forState:UIControlStateNormal];
        [switchProfileButton setFrame:CGRectMake(0, OPTIONS_HEIGHT+OPTIONS_BUFFER, OPTIONS_WIDTH, OPTIONS_HEIGHT)];
        [switchProfileButton setTintColor:[UIColor blackColor]];
        [switchProfileButton.titleLabel setTextAlignment:NSTextAlignmentRight];
        [switchProfileButton setBackgroundColor:[UIColor colorWithRed:.93 green:.93 blue:.94 alpha:1]];
        [switchProfileButton addTarget:self action:@selector(showProfileWindow:) forControlEvents:UIControlEventTouchUpInside];
        
        [options addSubview:createProtileButton];
        [options addSubview:switchProfileButton];

        [self.view addSubview:options];
        [self.view bringSubviewToFront:self.profileBar];
        
        [UIView animateWithDuration:.5 animations:^{
            [options setFrame:CGRectMake(self.profileBar.frame.size.width-OPTIONS_WIDTH, self.profileBar.frame.size.height - OPTIONS_BUFFER-3, OPTIONS_WIDTH, options.frame.size.height)];
        }];
    }else{
        //dismiss options window
        [UIView animateWithDuration:.5 animations:^{
            [options setFrame:CGRectMake(self.profileBar.frame.size.width-OPTIONS_WIDTH, self.profileBar.frame.size.height-options.frame.size.height-OPTIONS_BUFFER, OPTIONS_WIDTH, options.frame.size.height)];
        }completion:^(BOOL finished) {
            [options removeFromSuperview];
            options = nil;
        }];
    }
}
*/
#define PROFILE_WINDOW_WIDTH 200
#define PROFILE_WINDOW_HEIGHT 300
-(void)showProfileWindow:(UIButton *)sender
{
    //create the new profile popover
    newProfilePopover = [[[NSBundle mainBundle] loadNibNamed:@"NewProfilePopOver" owner:self options:nil] objectAtIndex:0];
    if ([sender.titleLabel.text isEqualToString:@"New Profile"]) {
        [newProfilePopover setIsCreatingNewProfile:YES];
    }else{
        [newProfilePopover setIsCreatingNewProfile:NO];
    }
    [newProfilePopover setFrame:CGRectMake(0, 0, PROFILE_WINDOW_WIDTH, PROFILE_WINDOW_HEIGHT)];
    [newProfilePopover setCenter:self.view.center];
    [newProfilePopover setParentVC:self];
    
    [self.view addSubview:newProfilePopover];
    [self.view bringSubviewToFront:newProfilePopover];
    
    //dismiss options window
    [self.profileBar dismissOptionsMenu];
}


-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    UITouch *t = [touches anyObject];
    CGPoint tap = [t locationInView:self.view];
    
    if  (newProfilePopover != nil){
        if  (!CGRectContainsPoint(newProfilePopover.frame, tap)){
            [newProfilePopover removeFromSuperview];
            newProfilePopover = nil;
        }
    }
}

- (NSUInteger)supportedInterfaceOrientations {
    
    if (delegate.shouldForceLandscape) {
        return UIInterfaceOrientationMaskLandscape;
    }
    return UIInterfaceOrientationMaskPortrait;
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
