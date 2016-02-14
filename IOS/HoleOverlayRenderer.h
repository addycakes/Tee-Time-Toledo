//
//  HoleOverlayRenderer.h
//  Golf
//
//  Created by Adam Wilson on 7/17/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <MapKit/MapKit.h>

@interface HoleOverlayRenderer : MKOverlayRenderer
@property (nonatomic) CLLocationCoordinate2D overlayTopLeft;
@property (nonatomic) CLLocationCoordinate2D overlayBottomLeft;
@property (nonatomic) CLLocationCoordinate2D overlayBottomRight;
@property (nonatomic, weak) NSString *imageFilename;
@end
