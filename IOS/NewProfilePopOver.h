//
//  NewProfilePopOver.h
//  Golf
//
//  Created by Adam Wilson on 7/12/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MainViewController.h"

@interface NewProfilePopOver : UIView <UITableViewDataSource, UITableViewDelegate, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UIAlertViewDelegate>
{
    NSManagedObjectContext *managedObjectContext;

}
@property (nonatomic) BOOL isCreatingNewProfile;
@property (weak, nonatomic) IBOutlet UITextField *profileNameNew;
@property (weak, nonatomic) IBOutlet UIImageView *profilePicNew;
@property (weak, nonatomic) IBOutlet UITableView *profilesTableView;
@property (weak, nonatomic) MainViewController *parentVC;

- (IBAction)openCamera:(id)sender;
- (IBAction)openAlbums:(id)sender;
- (IBAction)cancel:(id)sender;
- (IBAction)addNewProfile:(id)sender;
-(IBAction)textFieldReturn:(UITextField *)sender;
@end
