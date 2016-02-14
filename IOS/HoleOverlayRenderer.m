//
//  HoleOverlayRenderer.m
//  Golf
//
//  Created by Adam Wilson on 7/17/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "HoleOverlayRenderer.h"

@implementation HoleOverlayRenderer


-(void)drawMapRect:(MKMapRect)mapRect zoomScale:(MKZoomScale)zoomScale inContext:(CGContextRef)context
{
    //overlay rect vertices    
    double overlayX = MKMapPointForCoordinate(self.overlayTopLeft).x;
    double overlayY = MKMapPointForCoordinate(self.overlayTopLeft).y;
    double overlayWidth = fabs(MKMapPointForCoordinate(self.overlayBottomLeft).x -  MKMapPointForCoordinate(self.overlayBottomRight).x);
    double overlayHeight = fabs(MKMapPointForCoordinate(self.overlayTopLeft).y -  MKMapPointForCoordinate(self.overlayBottomRight).y);
    MKMapRect overlayMapRect = MKMapRectMake(overlayX, overlayY, overlayWidth, overlayHeight);
    UIImage *holeOverlayImage = [UIImage imageNamed:self.imageFilename];
    CGImageRef holeRef = holeOverlayImage.CGImage;
    (void)holeOverlayImage;
    
    CGContextScaleCTM(context, 1.0, -1.0);
    CGContextTranslateCTM(context, 0.0, -overlayMapRect.size.height);
    CGContextDrawImage(context, [self rectForMapRect:overlayMapRect], holeRef);
}
@end
