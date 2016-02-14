//
//  CustomPin.m
//  Golf
//
//  Created by Adam Wilson on 8/29/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "CustomPin.h"

@interface CustomPin ()
{
    UIImage *image;
}

@end

@implementation CustomPin

-(id)initWithType:(NSString *)type andLocation:(CLLocationCoordinate2D)location
{
    self = [super init];
    
    if (self) {
        self.coordinate = location;
        
        if ([type isEqualToString:@"History"]) {
            image = [UIImage imageNamed:@"historyPin"];
        }else{
            image = [UIImage imageNamed:@"distancePin"];
        }
    }
    return self;
}

-(MKAnnotationView *)annotationView
{
    MKAnnotationView *annView = [[MKAnnotationView alloc] initWithAnnotation:self reuseIdentifier:nil];
        annView.image = image;
    return annView;
}
@end
