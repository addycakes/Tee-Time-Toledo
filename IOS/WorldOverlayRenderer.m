//
//  WorldOverlayRenderer.m
//  Golf
//
//  Created by Adam Wilson on 7/17/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "WorldOverlayRenderer.h"

@implementation WorldOverlayRenderer

-(void)drawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale inContext:(CGContextRef)context
{
    CGContextSetFillColorWithColor(context, [UIColor colorWithRed:.19 green:.24 blue:.13 alpha:1].CGColor);
    CGContextFillRect(context, [self rectForMapRect:mapRect]);
}

@end
