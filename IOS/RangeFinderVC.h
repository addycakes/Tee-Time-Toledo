//
//  RangeFinderVC.h
//  Golf
//
//  Created by Adam Wilson on 6/11/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import "Hole.h"
#import "Golfer.h"
#import "HoleOverlay.h"
#import "WorldOverlay.h"
#import "Stroke.h"
#import "CustomPin.h"

@interface RangeFinderVC : UIViewController <MKMapViewDelegate, /*UIPickerViewDataSource, UIPickerViewDelegate,*/ UIGestureRecognizerDelegate, UIAlertViewDelegate>
{
    //__weak IBOutlet UIButton *saveStroke;
    //__weak IBOutlet UIPickerView *clubSelector;
    //__weak IBOutlet UISegmentedControl *fairwayHitSelector;
    //__weak IBOutlet UISegmentedControl *greensHitSelector;
    
    Hole *currentHole;
    NSDictionary *currentHoleDict;
    NSMutableArray *allStrokesHistory;
    MKCoordinateRegion region;

    CLLocationManager *locationManager;
    
    NSMutableArray *mapPins;
    Stroke *selectedStroke;
    
    HoleOverlay *holeOverlay;
    MKOverlayView *holeOverlayView;
    NSString *overLayImageUrl;
    CLLocationCoordinate2D overlayTopLeft;
    CLLocationCoordinate2D overlayBottomLeft;
    CLLocationCoordinate2D overlayBottomRight;
    
    //MKCoordinateRegion overlayRegion;

    WorldOverlay *worldOverlay;
    MKOverlayView *worldOverlayView;
    
    
}
@property (weak, nonatomic) IBOutlet MKMapView *mapView;
@property (nonatomic) CLLocationCoordinate2D userLocation;
@property (nonatomic) BOOL shouldShowHistory;
@property (nonatomic, weak) Golfer *golfer;

-(void)setMapOverlay:(NSDictionary *)mapDict;
-(void)setHole:(Hole *)hole withDict:(NSDictionary *)holeDict;
-(void)deleteHistoryStroke;

//@property (strong, nonatomic) NSMutableDictionary *golferStrokeAnnotation;
/*
-(void)setStrokeHistoryForHole:(Hole *)hole;
-(IBAction)addStroke:(UIButton *)sender;
-(IBAction)setHitLocation:(UISegmentedControl *)sender;
- (void)toggleHistory:(BOOL)shouldShow;
*/

@end
