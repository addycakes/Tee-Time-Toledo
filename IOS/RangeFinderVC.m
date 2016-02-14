//
//  RangeFinderVC.m
//  Golf
//
//  Created by Adam Wilson on 6/11/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "RangeFinderVC.h"
#import "Round.h"
#import "AppDelegate.h"
#import "HoleVC.h"
#import "RangeFinderVC+Map.h"

@interface RangeFinderVC ()
{
    UITextField *textField;
    NSArray *clubsForPicker;
    AppDelegate *delegate;
}
@end

@implementation RangeFinderVC

- (void)viewDidLoad {
    [super viewDidLoad];

    //ask permission to use location services
    locationManager = [[CLLocationManager  alloc] init];
    [locationManager requestWhenInUseAuthorization];
    [locationManager startUpdatingLocation];
    
    //initiate
    [self.mapView setDelegate:self];
   
    //long press to drop pin
    UILongPressGestureRecognizer *longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(addMapPin:)];
    [longPress setMinimumPressDuration:.25];
    [self.mapView addGestureRecognizer:longPress];
    
    //add border to view
    UIImageView *border = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"rangefinderBorder.png"]];
    [border setFrame:self.view.frame];
    [self.mapView addSubview:border];
    //[self.view.layer setBorderWidth: 20];
    //[self.view.layer setBorderColor:[UIColor colorWithRed:0 green:0 blue:0 alpha:.07].CGColor];
}

-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    [self.view setNeedsLayout];
}

-(void)setShouldShowHistory:(BOOL)shouldShowHistory
{
    _shouldShowHistory = shouldShowHistory;
    
    if (shouldShowHistory){
        //display any stroke annotations for hole
        for (Stroke *s in allStrokesHistory) {
            // Create your coordinate
            CLLocationCoordinate2D pinCoordinate = CLLocationCoordinate2DMake(s.latitude.doubleValue, s.longitude.doubleValue);
            CustomPin *pin = [[CustomPin alloc] initWithType:@"History" andLocation:pinCoordinate];
            
            //Drop pin on map
            [self.mapView addAnnotation:pin];
        }
    }else{
        //remove all history pins
        for (MKAnnotationView *ann in self.mapView.annotations) {
            if ([ann isKindOfClass:[CustomPin class]]){
                CustomPin *historyPin = (CustomPin *) ann;
                for (Stroke *s in allStrokesHistory) {
                    if ((historyPin.coordinate.latitude == s.latitude.doubleValue) && (historyPin.coordinate.longitude == s.longitude.doubleValue)) {
                        [self.mapView removeAnnotation:historyPin];
                    }
                }
            }
        }
    }
}

-(void)setMapOverlay:(NSDictionary *)mapDict
{
    //load coordinates from plist
    NSArray *topLeftCoords = [[mapDict objectForKey:@"topLeftCoordinate"] componentsSeparatedByString:@","];
    NSArray *bottomLeftCoords = [[mapDict objectForKey:@"bottomLeftCoordinate"] componentsSeparatedByString:@","];
    NSArray *bottomRightCoords = [[mapDict objectForKey:@"bottomRightCoordinate"]componentsSeparatedByString:@","];
    NSArray *centerCoords = [[mapDict objectForKey:@"centerCoordinate"]
                             componentsSeparatedByString:@","];
    overLayImageUrl = [mapDict objectForKey:@"imageName"];
    
    //overlay rect vertices
    overlayTopLeft = CLLocationCoordinate2DMake([topLeftCoords[0] floatValue],[topLeftCoords[1] floatValue]);
    overlayBottomLeft = CLLocationCoordinate2DMake([bottomLeftCoords[0] floatValue],[bottomLeftCoords[1] floatValue]);
    overlayBottomRight = CLLocationCoordinate2DMake([bottomRightCoords[0] floatValue],[bottomRightCoords[1] floatValue]);
    CLLocationCoordinate2D overlayCenter = CLLocationCoordinate2DMake([centerCoords[0] floatValue],[centerCoords[1] floatValue]);

    double overlayX = MKMapPointForCoordinate(overlayTopLeft).x;
    double overlayY = MKMapPointForCoordinate(overlayTopLeft).y;
    double overlayWidth = fabs(MKMapPointForCoordinate(overlayBottomLeft).x -  MKMapPointForCoordinate(overlayBottomRight).x);
    double overlayHeight = fabs(MKMapPointForCoordinate(overlayTopLeft).y -  MKMapPointForCoordinate(overlayBottomLeft).y);
    MKMapRect overlayRect = MKMapRectMake(overlayX, overlayY, overlayWidth, overlayHeight);

    
    //add overlays
    worldOverlay = [[WorldOverlay alloc] init];
    [worldOverlay setCoordinate:self.mapView.centerCoordinate];
    [worldOverlay setBoundingMapRect:MKMapRectWorld];
    [self.mapView addOverlay:worldOverlay];
    
    holeOverlay = [[HoleOverlay alloc] init];
    [holeOverlay setCoordinate:overlayCenter];
    [holeOverlay setBoundingMapRect:overlayRect];
    [self.mapView addOverlay:holeOverlay];
}

-(void)setHole:(Hole *)hole withDict:(NSDictionary *)holeDict
{
    currentHole = hole;
    currentHoleDict = holeDict;
    NSString *currentCourseName = [(HoleVC *)self.parentViewController currentCourseName];
    
    //get previous rounds on this course
    delegate = [UIApplication sharedApplication].delegate;
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription
                                   entityForName:@"Round" inManagedObjectContext:delegate.managedObjectContext];
    [fetchRequest setEntity:entity];
    NSError *error;
    NSArray *fetchedObjects = [delegate.managedObjectContext executeFetchRequest:fetchRequest error:&error];
    
    //grab all rounds for this course
    NSMutableArray *oldRounds = [[NSMutableArray alloc] init];
    for (Round *r in fetchedObjects) {
        if ([r.course isEqualToString:currentCourseName] || [r.continueCourse isEqualToString:currentCourseName]) {
            [oldRounds addObject:r];
        }
    }
    
    //keep only three most recent rounds
    NSSortDescriptor *dateDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"date" ascending:NO];
    NSMutableArray *threeRounds = [[NSMutableArray alloc] initWithArray:[oldRounds sortedArrayUsingDescriptors:@[dateDescriptor]]];
    
    //while (threeRounds.count > 3){
    //    [threeRounds removeObject:threeRounds.lastObject];
    //}
    
    Stroke *first = [[Stroke alloc] initWithEntity:[NSEntityDescription entityForName:@"Stroke" inManagedObjectContext:delegate.managedObjectContext] insertIntoManagedObjectContext:delegate.managedObjectContext];
    first.latitude = [NSNumber numberWithDouble:41.730681];
    first.longitude = [NSNumber numberWithDouble:-83.582807];
    Stroke *second =  [[Stroke alloc] initWithEntity:[NSEntityDescription entityForName:@"Stroke" inManagedObjectContext:delegate.managedObjectContext] insertIntoManagedObjectContext:delegate.managedObjectContext];
    second.latitude = [NSNumber numberWithDouble:41.731775];
    second.longitude = [NSNumber numberWithDouble:-83.582973];
    Stroke *third =  [[Stroke alloc] initWithEntity:[NSEntityDescription entityForName:@"Stroke" inManagedObjectContext:delegate.managedObjectContext] insertIntoManagedObjectContext:delegate.managedObjectContext];
    third.latitude = [NSNumber numberWithDouble:41.732476];
    third.longitude = [NSNumber numberWithDouble:-83.583553];
    

    //get the strokes for the current hole
    allStrokesHistory = [[NSMutableArray alloc]init];
    for (Round *r in threeRounds) {
        for (Golfer *g in r.golfers) {
            if ([g.name isEqualToString:self.golfer.name]) {
                for (Hole *h in g.holes) {
                    if ([h valueForKey:@"number"] == [hole valueForKey:@"number"]  && [[h valueForKey:@"course"]isEqualToString:currentCourseName]) {
                        [allStrokesHistory addObjectsFromArray:h.strokes.allObjects];
                        break;
                    }
                }
            }
        }
    }
    //test pins
    [allStrokesHistory addObject:first];
    [allStrokesHistory addObject:second];
    [allStrokesHistory addObject:third];
    [allStrokesHistory addObjectsFromArray:hole.strokes.allObjects];
    
    [self displayHoleOverlay:holeDict];
}

-(void)deleteHistoryStroke{
    [currentHole removeStrokesObject:selectedStroke];
    [self setShouldShowHistory:NO];
    [allStrokesHistory removeObject:selectedStroke];
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if ([[alertView buttonTitleAtIndex:buttonIndex] isEqualToString:@"Yes"]){
        NSNotification *note = [NSNotification notificationWithName:@"StrokeAdded" object:self];
        [[NSNotificationCenter defaultCenter] postNotification:note];
    }else if ([[alertView buttonTitleAtIndex:buttonIndex] isEqualToString:@"Delete"]){
        [self deleteHistoryStroke];
        //post notification that data has changed
        NSNotification *note = [NSNotification notificationWithName:@"DataChanged" object:self];
        [[NSNotificationCenter defaultCenter] postNotification:note];
    }
}

-(BOOL)shouldAutorotate
{
    //reset the orientation
    [self.mapView setFrame:self.view.frame];

    MKMapCamera *newCamera = [[self.mapView camera] copy];
    CLLocationDirection dir = newCamera.heading;
    
    [self.mapView setRegion:MKCoordinateRegionForMapRect(MKMapRectWorld)];
    [newCamera setHeading:90.0]; // or newCamera.heading + 90.0 % 360.0
    [self.mapView setCamera:newCamera animated:NO];
    [newCamera setHeading:dir]; // or newCamera.heading + 90.0 % 360.0
    [self.mapView setCamera:newCamera animated:NO];
    
    [self.mapView setRegion:region];
    [self displayHoleOverlay:currentHoleDict];
    
    return NO;
}
/*
-(IBAction)setHitLocation:(UISegmentedControl *)sender
{
    NSString *hitLocation;
    
    //left, center, right
    if (sender.selectedSegmentIndex == 0) {
        hitLocation = @"Left";
    }else if (sender.selectedSegmentIndex == 1) {
        hitLocation = @"Center";
    }else if (sender.selectedSegmentIndex == 2) {
        hitLocation = @"Right";
    }
    
    //add to dictionary for database
    if ([sender isEqual:fairwayHitSelector]) {
        [self.golferStrokeAnnotation setObject:hitLocation forKey:@"fairwayHit"];
    }else if ([sender isEqual:greensHitSelector]){
        [self.golferStrokeAnnotation setObject:hitLocation forKey:@"greensHit"];
    }
}

- (void)toggleHistory:(BOOL)shouldShow
{
}


#pragma CLUB SELECTOR METHODS

-(NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

-(NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return clubsForPicker.count;
}


-(NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return [clubsForPicker objectAtIndex:row];
}

- (NSAttributedString *)pickerView:(UIPickerView *)pickerView attributedTitleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    NSString *title = [clubsForPicker objectAtIndex:row];
    NSAttributedString *attString;
    if ([clubsForPicker[row] isEqualToString:[self.golferStrokeAnnotation objectForKey:@"club"]]) {
        attString = [[NSAttributedString alloc] initWithString:title attributes:@{NSForegroundColorAttributeName:[UIColor colorWithRed:.14 green:.35 blue:.48 alpha:1]}];
    }else{
        attString = [[NSAttributedString alloc] initWithString:title attributes:@{NSForegroundColorAttributeName:[UIColor colorWithRed:.83 green:.65 blue:.16 alpha:1]}];
    }
    
    return attString;
    
}

-(BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer{
    // return
    return true;
}

- (void)pickerViewTapGestureRecognized:(UITapGestureRecognizer*)gestureRecognizer
{
    CGPoint touchPoint = [gestureRecognizer locationInView:self.view];
    
    CGRect frame = clubSelector.frame;
    CGRect selectorFrame = CGRectInset( frame, 0.0, clubSelector.bounds.size.height * 0.85 / 2.0 );
    
    if( CGRectContainsPoint( selectorFrame, touchPoint) )
    {
        [self.golferStrokeAnnotation setObject:[self pickerView:clubSelector titleForRow:[clubSelector selectedRowInComponent:0] forComponent:0] forKey:@"club"];
        [clubSelector reloadAllComponents];
        [clubSelector selectRow:[clubSelector selectedRowInComponent:0] inComponent:0 animated:NO];
    }
}
 
-(IBAction)addStroke:(UIButton *)sender
{
    //set location of stroke
    [self.golferStrokeAnnotation setObject:[NSNumber numberWithDouble:(double)self.mapView.userLocation.coordinate.latitude] forKey:@"latitude"];
    [self.golferStrokeAnnotation setObject:[NSNumber numberWithDouble:(double)self.mapView.userLocation.coordinate.longitude] forKey:@"longitude"];
    
    //post notification that data has changed
    NSNotification *note = [NSNotification notificationWithName:@"StrokeAdded" object:self];
    [[NSNotificationCenter defaultCenter] postNotification:note];
    
    [self toggleHud];
}


-(void)toggleHud
{
    for (UIView *view in self.view.subviews) {
        if (view.tag == 1) {
            view.hidden = !view.isHidden;
        }
    }
}
*/
@end
