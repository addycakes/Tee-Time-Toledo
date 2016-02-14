//
//  RangeFinderVC+Map.m
//  Golf
//
//  Created by Adam Wilson on 7/17/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "RangeFinderVC+Map.h"

@implementation RangeFinderVC (Map)

-(void)displayHoleOverlay:(NSDictionary *)holeDict
{
    //for course plist info
    NSArray *topLeftCoords = [[holeDict objectForKey:@"topLeftCoordinate"] componentsSeparatedByString:@","];
    //NSArray *bottomLeftCoords = [[holeDict objectForKey:@"bottomLeftCoordinate"] componentsSeparatedByString:@","];
    NSArray *bottomRightCoords = [[holeDict objectForKey:@"bottomRightCoordinate"]componentsSeparatedByString:@","];
    NSArray *centerCoords = [[holeDict objectForKey:@"centerCoordinate"] componentsSeparatedByString:@","];
    NSInteger rotate = [[holeDict objectForKey:@"rotate"] integerValue];
    
    //overlay rect vertices
    CLLocationCoordinate2D mapTopLeft = CLLocationCoordinate2DMake([topLeftCoords[0] floatValue],[topLeftCoords[1] floatValue]);
    //CLLocationCoordinate2D mapBottomLeft = CLLocationCoordinate2DMake([bottomLeftCoords[0] floatValue],[bottomLeftCoords[1] floatValue]);
    CLLocationCoordinate2D mapBottomRight = CLLocationCoordinate2DMake([bottomRightCoords[0] floatValue],[bottomRightCoords[1] floatValue]);
    CLLocationCoordinate2D mapCenter = CLLocationCoordinate2DMake([centerCoords[0] floatValue],[centerCoords[1] floatValue]);
    
    //double overlayX = MKMapPointForCoordinate(overlayTopLeft).x;
    //double overlayY = MKMapPointForCoordinate(overlayTopLeft).y;
    //double overlayWidth = fabs(MKMapPointForCoordinate(overlayBottomLeft).x -  MKMapPointForCoordinate(overlayBottomRight).x);
    //double overlayHeight = fabs(MKMapPointForCoordinate(overlayTopLeft).y -  MKMapPointForCoordinate(overlayBottomRight).y);
    
    //MKMapRect overlayMapRect = MKMapRectMake(overlayX, overlayY, overlayWidth, overlayHeight);
    double scalingFactor = ABS( (cos(2 * M_PI * mapCenter.latitude / 360.0) ));
    MKCoordinateSpan span = MKCoordinateSpanMake(fabs(mapTopLeft.latitude - mapBottomRight.latitude), (fabs(mapTopLeft.longitude - mapBottomRight.longitude)*scalingFactor));
    MKCoordinateRegion holeRegion = MKCoordinateRegionMake(mapCenter, span);
    [self.mapView setRegion:holeRegion];
    region = holeRegion;
    
    //fix rotation for horizontal holes
    if (rotate == 270) {
        MKMapCamera *newCamera = [[self.mapView camera] copy];
        [newCamera setHeading:90.0]; // or newCamera.heading + 90.0 % 360.0
        [self.mapView setCamera:newCamera animated:NO];
    }else if (rotate == 180) {
        MKMapCamera *newCamera = [[self.mapView camera] copy];
        [newCamera setHeading:180.0]; // or newCamera.heading + 90.0 % 360.0
        [self.mapView setCamera:newCamera animated:NO];
    }else if (rotate == 90) {
        MKMapCamera *newCamera = [[self.mapView camera] copy];
        [newCamera setHeading:270.0]; // or newCamera.heading + 90.0 % 360.0
        [self.mapView setCamera:newCamera animated:NO];
    }
}

-(void)cleanUpMap
{
    for (NSDictionary *dict in mapPins) {
        [[dict objectForKey:@"Label"] removeFromSuperview];
        [self.mapView removeOverlay:[dict objectForKey:@"Line"]];
    }
    
    [self.mapView removeAnnotations:self.mapView.annotations];
    mapPins = nil;
}

-(void)addMapPin:(UIGestureRecognizer *)sender
{
    if (mapPins == NULL) {
        mapPins = [[NSMutableArray alloc] init];
    }
    
    //drop pin on touch location
    if ([sender state] == UIGestureRecognizerStateBegan) {
        CGPoint tap = [sender locationInView:self.mapView];
        
        // Create your coordinate
        CLLocationCoordinate2D pinCoordinate = [self.mapView convertPoint:tap toCoordinateFromView:self.mapView];
        CustomPin *pin = [[CustomPin alloc] initWithType:@"Distance" andLocation:pinCoordinate];
        
        //Drop pin on map
        [self.mapView addAnnotation:pin];
        
        //draw dotted line between user and pin
        [self drawDistanceToPin:pin];
    }
}

#define CONVERT_TO_YARDS 1.09
-(void)drawDistanceToPin:(CustomPin *)pin
{
    //get coordinates for points
    CLLocationCoordinate2D *coords = malloc(sizeof(CLLocationCoordinate2D) * 4);
    coords[0] = self.mapView.userLocation.coordinate;
    coords[1] = pin.coordinate;
    
    //add line between
    MKPolyline *straightShot = [MKPolyline polylineWithCoordinates:coords count:2];
    [self.mapView addOverlay:straightShot];
    
    //calc distance
    CLLocation *pinLoc = [[CLLocation alloc] initWithLatitude:pin.coordinate.latitude longitude:pin.coordinate.longitude];
    CLLocationDistance dist = [self.mapView.userLocation.location distanceFromLocation:pinLoc] * CONVERT_TO_YARDS;
    
    //add distance label
    UILabel *distanceLabel = [[UILabel alloc] initWithFrame:CGRectMake([self.mapView convertCoordinate:pin.coordinate toPointToView:self.mapView].x - 100, [self.mapView convertCoordinate:pin.coordinate toPointToView:self.mapView].y, 200, 20)];
    [distanceLabel setTextColor:[UIColor yellowColor]];
    [distanceLabel setText:[NSString stringWithFormat:@"%.0f yards", dist]];
    
    [self.mapView addSubview:distanceLabel];
    
    //add map annotations to dict
    NSMutableDictionary *mapDict = [[NSMutableDictionary alloc] init];
    [mapDict setObject:pin forKey:@"Pin"];
    [mapDict setObject:straightShot forKey:@"Line"];
    [mapDict setObject:distanceLabel forKey:@"Label"];
    
    //store in array
    [mapPins addObject:mapDict];
}

-(MKOverlayRenderer *)mapView:(MKMapView *)mapView rendererForOverlay:(id<MKOverlay>)overlay
{
    if ([overlay isKindOfClass:[HoleOverlay class]]) {
        HoleOverlayRenderer *holeView = [[HoleOverlayRenderer alloc] initWithOverlay:overlay];
        [holeView setImageFilename:overLayImageUrl];
        [holeView setOverlayBottomLeft: overlayBottomLeft];
        [holeView setOverlayBottomRight:overlayBottomRight];
        [holeView setOverlayTopLeft:overlayTopLeft];
        return holeView;
    }else if ([overlay isKindOfClass:[WorldOverlay class]]) {
        WorldOverlayRenderer *bgView = [[WorldOverlayRenderer alloc] initWithOverlay:overlay];
        return bgView;
    }else{
        MKPolylineRenderer *polylineRender = [[MKPolylineRenderer alloc] initWithPolyline:overlay];
        polylineRender.strokeColor = [UIColor yellowColor];
        polylineRender.lineWidth = 5.0;
        polylineRender.alpha = .5;
        
        return polylineRender;
    }
    return nil;
}

//remove pin if tapped
-(void)mapView:(MKMapView *)mapView didSelectAnnotationView:(MKAnnotationView *)view
{
    //check if the user location button was selected or a map pin annotation view
    CGPoint golfer = [self.mapView convertCoordinate:self.mapView.userLocation.coordinate toPointToView:self.mapView];
    
    //if the user is selected, add stroke annotation for current user location
    if (CGRectContainsPoint(view.frame, golfer)) {
        //continue or end
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Save history" message:@"Save this stroke location?" delegate:self cancelButtonTitle:@"No" otherButtonTitles:@"Yes", nil];
        [alert show];
    }else{
        //if the pin is a distance marker
        //find the pin selected and remove it and it's line and label
        for (NSMutableDictionary *dict in mapPins) {
            CGPoint p = [self.mapView convertCoordinate:[[dict objectForKey:@"Pin"] coordinate] toPointToView:self.mapView];
            if (CGRectContainsPoint(view.frame, p)) {
                [self.mapView removeAnnotations:@[[dict objectForKey:@"Pin"]]];
                [[dict objectForKey:@"Label"] removeFromSuperview];
                [self.mapView removeOverlay:[dict objectForKey:@"Line"]];
                [mapPins removeObject:dict];
                return;
            }
        }
        //if the pin is a history marker, ask to remove
        for (Stroke *stroke in allStrokesHistory) {
            CLLocationCoordinate2D coord = CLLocationCoordinate2DMake(stroke.latitude.floatValue, stroke.longitude.floatValue);
            CGPoint p = [self.mapView convertCoordinate:coord toPointToView:self.mapView];
            if (CGRectContainsPoint(view.frame, p)) {
                //show alert for delete
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Delete Pin" message:@"Delete this stroke location?" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Delete", nil];
                [alert show];
                selectedStroke = stroke;
                return;
            }
        }
    }
}

-(MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id<MKAnnotation>)annotation
{
    if ([annotation isKindOfClass:[CustomPin class]]){
        CustomPin *customPin = (CustomPin *)annotation;
        
        MKAnnotationView *annotationView = [mapView dequeueReusableAnnotationViewWithIdentifier:@"CustomPin"];
        
        if (annotationView == nil) {
            annotationView = customPin.annotationView;
        }else{
            annotationView.annotation = annotation;
        }
        
        return annotationView;
    }else{
        return nil;
    }
}

-(void)mapView:(MKMapView *)mapView didUpdateUserLocation:(MKUserLocation *)userLocation
{
    self.userLocation = userLocation.coordinate;
}

@end
