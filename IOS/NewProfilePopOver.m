//
//  NewProfilePopOver.m
//  Golf
//
//  Created by Adam Wilson on 7/12/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "NewProfilePopOver.h"
#import "RoundSelectCell.h"
#import "Profile.h"
#import "AppDelegate.h"
#import "ProfileHeaderBar.h"

@interface NewProfilePopOver()
{
    NSMutableArray *allProfiles;
    UIImagePickerController *imagePicker;
    
    NSIndexPath *selectedIndexPath;
}

@end

@implementation NewProfilePopOver

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    managedObjectContext = [delegate managedObjectContext];

    // Drawing code
    self.layer.shadowColor = [[UIColor blackColor] CGColor];
    self.layer.shadowOffset =  CGSizeMake(-5.0, 10.0);
    self.layer.shadowRadius = 8.0;
    self.layer.shadowOpacity = 1;
    
    [self.profilesTableView setDataSource:self];
    [self.profilesTableView setDelegate:self];
    [self loadProfiles];
}

-(void)setIsCreatingNewProfile:(BOOL)isCreatingNewProfile
{
    _isCreatingNewProfile = isCreatingNewProfile;
    
    //if creating new profile show interface, else show tableview for switching/deleting
    if (!isCreatingNewProfile) {
        for (UIView *view in self.subviews) {
            [view setHidden:!view.isHidden];
        }
    }
}

- (IBAction)openCamera:(id)sender {
    
    //access camera to take pic
    imagePicker = [[UIImagePickerController alloc] init];
    [imagePicker setSourceType:UIImagePickerControllerSourceTypeCamera];
    [imagePicker setDelegate:self];
    [self.parentVC presentViewController:imagePicker animated:YES completion:nil];
}

- (IBAction)openAlbums:(id)sender {

    //access photo album for pic
    imagePicker = [[UIImagePickerController alloc] init];
    [imagePicker setSourceType:UIImagePickerControllerSourceTypePhotoLibrary];
    [imagePicker setDelegate:self];
    [self.parentVC presentViewController:imagePicker animated:YES completion:nil];
}

- (IBAction)cancel:(id)sender {
    [self removeFromSuperview];
}

- (IBAction)addNewProfile:(id)sender{
    
    if (![self.profileNameNew.text isEqualToString:@""]) {
        //add profile to database
        Profile *newProfile = [[Profile alloc] initWithEntity:[NSEntityDescription entityForName:@"Profile" inManagedObjectContext:managedObjectContext] insertIntoManagedObjectContext:managedObjectContext];
        
        [newProfile setName:self.profileNameNew.text];
        [newProfile setPic:UIImageJPEGRepresentation(self.profilePicNew.image, 100)];
        
        [self saveData];
        [[ProfileHeaderBar sharedProfileBar] changeProfile:newProfile];
        [self removeFromSuperview];
    }
}

-(void)removeProfile:(Profile *)p
{
    [managedObjectContext deleteObject:p];
    [allProfiles removeObject:p];
    [self saveData];
}

-(void)saveData
{
    //save data
    AppDelegate *delegate = [UIApplication sharedApplication].delegate;
    [delegate saveContext];
}

-(IBAction)textFieldReturn:(UITextField *)sender
{
    NSString *newProfileName = [sender.text stringByTrimmingCharactersInSet:
                            [NSCharacterSet whitespaceCharacterSet]];
    
    if (![newProfileName isEqualToString:@""]) {
        [self.profileNameNew setText:newProfileName];
    }
}

-(void)loadProfiles
{
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription
                                   entityForName:@"Profile" inManagedObjectContext:managedObjectContext];
    [fetchRequest setEntity:entity];
    
    NSError *error;
    NSArray *fetchedObjects = [managedObjectContext executeFetchRequest:fetchRequest error:&error];
    
    allProfiles = [[NSMutableArray alloc] init];
    for (Profile *p in fetchedObjects) {
        [allProfiles addObject:p];
    }
    
    [self.profilesTableView reloadData];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return allProfiles.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [[UITableViewCell alloc] init];
    
    //set profile cell
    [cell.textLabel setFont:[UIFont fontWithName:@"Arial-Bold" size:24]];
    [cell.textLabel setTextAlignment:NSTextAlignmentLeft];
    [cell.textLabel setText:[[allProfiles objectAtIndex:indexPath.row] name]];

    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //switch to new profile
    [[ProfileHeaderBar sharedProfileBar] changeProfile:[allProfiles objectAtIndex:indexPath.row]];
    [self removeFromSuperview];
}

-(BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

-(void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        
        //ask to confirm delete
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Delete?" message:@"Are you sure you want to delete this profile?" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Yes", nil];
        [alert show];
        
        selectedIndexPath = indexPath;
    }
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if ([[alertView buttonTitleAtIndex:buttonIndex] isEqualToString:@"Yes"]) {
        //remove golfer
        [self removeProfile:[allProfiles objectAtIndex:selectedIndexPath.row]];
        [self.profilesTableView deleteRowsAtIndexPaths:@[selectedIndexPath] withRowAnimation:UITableViewRowAnimationFade];
    }
    [self removeFromSuperview];

    [[[ProfileHeaderBar sharedProfileBar] profileHandicapLabel] setText:@"Handicap 0"];
    [[[ProfileHeaderBar sharedProfileBar] profileNameLabel] setText:@"Golfer"];
    [[[ProfileHeaderBar sharedProfileBar] profilePic] setImage:[UIImage imageNamed:@"golficon180.png"]];
}

-(void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    UIImage *image = [info objectForKey:UIImagePickerControllerOriginalImage];
    [self.profilePicNew setImage:image];
    [self.parentVC dismissViewControllerAnimated:NO completion:nil];
    imagePicker = nil;
}

@end
