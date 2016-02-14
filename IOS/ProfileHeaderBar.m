//
//  ProfileHeaderBar.m
//  Golf
//
//  Created by Adam Wilson on 7/12/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "ProfileHeaderBar.h"
#import "AppDelegate.h"


#define OPTIONS_HEIGHT 44
#define OPTIONS_WIDTH 152
#define OPTIONS_BUFFER 20
@interface ProfileHeaderBar() <UIGestureRecognizerDelegate>
{
    Profile *currentProfile;
    AppDelegate *delegate;
    UIView *options;
}
@end
@implementation ProfileHeaderBar

+(ProfileHeaderBar *)sharedProfileBar
{
    static ProfileHeaderBar *sharedProfileBar = nil;
    if (!sharedProfileBar) {
        sharedProfileBar = [[super allocWithZone:nil] init];
    }
    
    return sharedProfileBar;
}

+(id)allocWithZone:(NSZone *)zone
{
    return [self sharedProfileBar];
}

-(id)init
{
    self = [super init];
    if (self) {
        delegate = [UIApplication sharedApplication].delegate;
        NSManagedObjectContext *managedObjectContext = [delegate managedObjectContext];

        
        NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
        NSEntityDescription *entity = [NSEntityDescription
                                       entityForName:@"Profile" inManagedObjectContext:managedObjectContext];
        [fetchRequest setEntity:entity];
        
        NSError *error;
        NSArray *fetchedObjects = [managedObjectContext executeFetchRequest:fetchRequest error:&error];
        
        if (fetchedObjects.count == 0) {
            NSLog(@"No profiles");
            currentProfile.name = @"Golfer";
            [currentProfile setPic:UIImageJPEGRepresentation([UIImage imageNamed:@"golficon180.png"], 100)];
        }else{
            NSLog(@"%@", [fetchedObjects.lastObject name]);
            currentProfile = fetchedObjects.lastObject;
        }
        self.optionsArray = [[NSMutableArray alloc]init];
    }
    
    return self;
}

-(void)changeProfile:(Profile *)newProfile
{
    [self.profileNameLabel setText:newProfile.name];
    [self.profileHandicapLabel setText:[self getHandicap]];
    [self.profilePic setImage:[UIImage imageWithData:newProfile.pic]];
}

-(void)reloadBar
{
    [self.profileNameLabel setText:[currentProfile name]];
    [self.profileHandicapLabel setText:[self getHandicap]];
    UIImage *image = [UIImage imageWithData:[currentProfile pic]];
    [self.profilePic setImage:image];
}

-(void)setShouldShowBackButton:(BOOL)shouldShowBackButton{
    _shouldShowBackButton = shouldShowBackButton;
    [self.profileBackButton setHidden:!shouldShowBackButton];
}

-(void)setShouldShowOptionsButton:(BOOL)shouldShowOptionsButton{
    _shouldShowOptionsButton = shouldShowOptionsButton;
    
    if (shouldShowOptionsButton){
        [self.profileButton setHidden:NO];
        [self.profileButton addTarget:self action:@selector(showOptions) forControlEvents:UIControlEventTouchUpInside];
    }else{
        [self.profileButton setHidden:YES];
        [self.profileButton removeTarget:self action:@selector(showOptions) forControlEvents:UIControlEventTouchUpInside];
        [self.optionsArray removeAllObjects];
    }
    
    if (options != nil){
        [self dismissOptionsMenu];
    }
}

-(NSString *)getHandicap
{
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription
                                   entityForName:@"Round" inManagedObjectContext:[delegate managedObjectContext]];
    [fetchRequest setEntity:entity];
    
    NSError *error;
    NSArray *fetchedObjects = [delegate.managedObjectContext executeFetchRequest:fetchRequest error:&error];
    
    int roundsPlayed = 0;
    int positiveScore = 0;
    int numHolesPlayed = 0;
    
    for (Round *round in fetchedObjects){
        for (Golfer *golfer in round.golfers.allObjects) {
            if ([golfer.name isEqualToString:self.profileNameLabel.text]) {
                roundsPlayed++;
                
                int parTotal = 0;
                int scoreTotal = 0;
                for (Hole *hole in golfer.holes.allObjects) {
                    numHolesPlayed += 1;
                    parTotal += hole.par.intValue;
                    scoreTotal += hole.score.intValue;
                }
                positiveScore += (scoreTotal - parTotal);
            }
        }
    }
    
    NSLog(@"positive score: %d", positiveScore);
    NSLog(@"here rounds player: %d or %f", roundsPlayed, (round((numHolesPlayed / 18) * 2.0) / 2.0));
    
    roundsPlayed = round((numHolesPlayed / 18) * 2.0) / 2.0;
    
    if (roundsPlayed < 3) {
        return [NSString stringWithFormat:@"TBD"];
    }
    
    if (positiveScore < 0) {
        return @"Handicap 0";
    }
    
    //if ((positiveScore/roundsPlayed) > 18) {
    //    return @"Handicap 18";
    //}

    return [NSString stringWithFormat:@"Handicap %d",(positiveScore/roundsPlayed)];
}

-(void)showOptions
{
    if (options == nil) {
        options = [[UIView alloc] initWithFrame:CGRectMake(self.frame.size.width-OPTIONS_WIDTH, self.frame.size.height-((1+self.optionsArray.count)*OPTIONS_HEIGHT), OPTIONS_WIDTH, (OPTIONS_HEIGHT*self.optionsArray.count)+OPTIONS_BUFFER)];
        [options setBackgroundColor:[UIColor colorWithRed:.93 green:.93 blue:.94 alpha:1]];
        [options setUserInteractionEnabled:YES];
        
        //add room for drop down
        [self setFrame:CGRectMake(self.frame.origin.x, self.frame.origin.y,
                                  self.frame.size.width, PROFILE_BAR_HEIGHT + options.frame.size.height)];
       
        int i = 0;
        for (UIButton *button in self.optionsArray){
            [button setFrame:CGRectMake(0, OPTIONS_BUFFER+(OPTIONS_HEIGHT*i), OPTIONS_WIDTH, OPTIONS_HEIGHT)];
            [button setTintColor:[UIColor blackColor]];
            [button.titleLabel setTextAlignment:NSTextAlignmentRight];
            [button setBackgroundColor:[UIColor colorWithRed:.93 green:.93 blue:.94 alpha:1]];
            [options addSubview:button];
            i++;
        }
        [self addSubview:options];
        [self sendSubviewToBack:options];
        
        [UIView animateWithDuration:.5 animations:^{
            [options setFrame:CGRectMake(self.frame.size.width-OPTIONS_WIDTH, PROFILE_BAR_HEIGHT - OPTIONS_BUFFER-3, OPTIONS_WIDTH, options.frame.size.height)];
        }];
    }else{
        //dismiss options window
        [self dismissOptionsMenu];
    }
}

-(void)dismissOptionsMenu
{
    //dismiss options window
    [UIView animateWithDuration:.5 animations:^{
        [options setFrame:CGRectMake(self.frame.size.width-OPTIONS_WIDTH, PROFILE_BAR_HEIGHT-options.frame.size.height-OPTIONS_BUFFER, OPTIONS_WIDTH, options.frame.size.height)];
    }completion:^(BOOL finished) {
        [options removeFromSuperview];
        options = nil;
    }];
    
    //remove extra room for drop down
    [self setFrame:CGRectMake(self.frame.origin.x, self.frame.origin.y,
                              self.frame.size.width, PROFILE_BAR_HEIGHT)];
}

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
    //self.layer.shadowColor = [[UIColor blackColor] CGColor];
    //self.layer.shadowOffset =  CGSizeMake(-5.0, 10.0);
    //self.layer.shadowRadius = 8.0;
    //self.layer.shadowOpacity = 1;
}

//test data
/*
 NSLog(@"test");
 for (int i = 0; i < 3; i++){
 Round *testRound = [NSEntityDescription insertNewObjectForEntityForName:@"Round" inManagedObjectContext:delegate.managedObjectContext];
 [testRound setValue:@"Buckeye" forKey:@"course"];
 [testRound setValue:[[NSDate date] dateByAddingTimeInterval:6000 * i] forKey:@"date"];
 
 //add golfers
 //[newRound addGolfers:[NSSet setWithArray:self.allGolfers]];
 Golfer *golfer = [[Golfer alloc] initWithEntity:[NSEntityDescription entityForName:@"Golfer" inManagedObjectContext:delegate.managedObjectContext] insertIntoManagedObjectContext:delegate.managedObjectContext];
 [golfer setName:currentProfile.name];
 [testRound addGolfersObject:golfer];
 
 int x = 10;
 if (i == 2) {
 x = 19;
 }
 for (int j = 1; j < x; j++) {
 Hole *hole = [[Hole alloc] initWithEntity:[NSEntityDescription entityForName:@"Hole" inManagedObjectContext:delegate.managedObjectContext] insertIntoManagedObjectContext:delegate.managedObjectContext];
 
 int par = arc4random() % 3;
 int score = arc4random() % 5;
 int stroke = arc4random() % 3;
 
 int parValue;
 int scoreValue;
 NSString *strokeValue;
 
 
 //set static values
 if (par == 0) {
 parValue = 3;
 }else if (par == 1){
 parValue = 4;
 }else if (par == 2){
 parValue = 5;
 }
 
 if (score == 0) {
 scoreValue = parValue;
 }else if (score == 1){
 scoreValue = parValue + 1;
 }else if (score == 2){
 scoreValue = parValue + 1;
 }else if (score == 3){
 scoreValue = parValue + 1;
 }else if (score == 4){
 scoreValue = parValue + 1;
 }
 
 if (stroke == 0) {
 strokeValue = @"Left";
 }else if (stroke == 1){
 strokeValue = @"Center";
 }else if (stroke == 2){
 strokeValue = @"Right";
 }
 
 [hole setValue:@"Buckeye" forKey:@"course"];
 [hole setValue:[NSNumber numberWithInt:parValue] forKey:@"par"];
 [hole setValue:[NSNumber numberWithInteger:j] forKey:@"number"];
 [hole setValue:[NSNumber numberWithInt:scoreValue] forKey:@"score"];
 [hole setValue:[NSNumber numberWithInt:0] forKey:@"putts"];
 [hole setValue:strokeValue forKey:@"fairwayHit"];
 [hole setValue:strokeValue forKey:@"greensHit"];
 
 //add hole to golfers array
 [golfer addHolesObject:hole];
 }
 
 //save data
 delegate = [UIApplication sharedApplication].delegate;
 [delegate saveContext];
 
 }*/

@end
