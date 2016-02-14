//
//  CustomPin.h
//  Golf
//
//  Created by Adam Wilson on 8/29/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <MapKit/MapKit.h>

@interface CustomPin : NSObject <MKAnnotation>
@property (nonatomic) CLLocationCoordinate2D coordinate;

-(id)initWithType:(NSString *)type andLocation:(CLLocationCoordinate2D)location;
-(MKAnnotationView *)annotationView;
@end
