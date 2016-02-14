//
//  Bar.m
//  Golf
//
//  Created by Adam Wilson on 7/4/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "Bar.h"
@interface Bar ()
{
    CAShapeLayer *pathLayer;
    CGPoint basePoint;
}
@end

@implementation Bar

- (void)drawRect:(CGRect)rect
{
    CGRect bounds = [self bounds];

    UIBezierPath *barPath = [UIBezierPath bezierPathWithRect:bounds];
    
    pathLayer = [CAShapeLayer layer];
    pathLayer.frame = self.bounds;
    pathLayer.path = barPath.CGPath;
    pathLayer.shadowColor = [[UIColor blackColor] CGColor];
    pathLayer.shadowOffset =  CGSizeMake(-5.0, 10.0);
    pathLayer.shadowRadius = 8.0;
    pathLayer.shadowOpacity = 1;
    pathLayer.anchorPoint = CGPointMake(0.5,1.0);
    
    if (self.tag == 0){
        pathLayer.strokeColor = [[UIColor colorWithRed:.14 green:.35 blue:.48 alpha:1] CGColor];
        pathLayer.fillColor = [[UIColor colorWithRed:.14 green:.35 blue:.48 alpha:1] CGColor];
        
    }else if (self.tag == 1){
        pathLayer.strokeColor = [[UIColor colorWithRed:.33 green:.47 blue:.25 alpha:1] CGColor];
        pathLayer.fillColor = [[UIColor colorWithRed:.33 green:.47 blue:.25 alpha:1] CGColor];

    }else{
        pathLayer.strokeColor = [[UIColor colorWithRed:.83 green:.65 blue:.16 alpha:1] CGColor];
        pathLayer.fillColor = [[UIColor colorWithRed:.83 green:.65 blue:.16 alpha:1] CGColor];
    }
    
    [self.layer addSublayer:pathLayer];
}

-(void)adjustSize:(float)amount
{
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath: @"transform.scale.y"];
    animation.fromValue = @(1);
    animation.toValue = @(1+(amount/160));
    animation.duration = 1.0;
    //animation.timingFunction = [CAMediaTimingFunction functionWithName: kCAMediaTimingFunctionEaseInEaseOut];
    //animation.delegate = self;
    // Important: change the actual layer property before installing the animation.
    [pathLayer setValue: animation.toValue forKeyPath: animation.keyPath];
    // Now install the explicit animation, overriding the implicit animation.
    [pathLayer addAnimation: animation forKey: animation.keyPath];
}
@end
