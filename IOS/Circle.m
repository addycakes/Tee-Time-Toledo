//
//  Circle.m
//  Golf
//
//  Created by Adam Wilson on 7/4/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "Circle.h"
#import <QuartzCore/QuartzCore.h>

@interface Circle ()
{
    CGPoint center;
    float radius;
    
}
@end

@implementation Circle

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.

#define CIRCLE_RADIUS 60
#define ZOOM_SCALE .5
- (void)drawRect:(CGRect)rect
{
    CGRect bounds = [self bounds];
    //center point
    center.x = bounds.origin.x + bounds.size.width / 2.0;
    center.y = bounds.origin.y + bounds.size.height / 2.0;
    
    //radius
    radius = CIRCLE_RADIUS;
        
    //line properties
    UIBezierPath *circlePath = [UIBezierPath bezierPath];
    [circlePath addArcWithCenter:center radius:radius startAngle:0 endAngle:(2*M_PI) clockwise:1];
    
    self.pathLayer = [CAShapeLayer layer];
    self.pathLayer.frame = self.bounds;
    self.pathLayer.path = circlePath.CGPath;
    self.pathLayer.shadowColor = [[UIColor blackColor] CGColor];
    self.pathLayer.shadowOffset =  CGSizeMake(-5.0, 10.0);
    self.pathLayer.shadowRadius = 8.0;
    self.pathLayer.shadowOpacity = 1;
    self.pathLayer.lineWidth = 10.0f;
    self.pathLayer.lineJoin = kCALineJoinBevel;

    if  (self.tag == 0){
        self.pathLayer.strokeColor = [[UIColor colorWithRed:.14 green:.35 blue:.48 alpha:1] CGColor];
        self.pathLayer.fillColor = [[UIColor colorWithRed:.14 green:.35 blue:.48 alpha:1] CGColor];
        
    }else if (self.tag == 1){
        self.pathLayer.strokeColor = [[UIColor colorWithRed:.33 green:.47 blue:.25 alpha:1] CGColor];
        self.pathLayer.fillColor = [[UIColor colorWithRed:.33 green:.47 blue:.25 alpha:1] CGColor];

    }else{
        self.pathLayer.strokeColor = [[UIColor colorWithRed:.83 green:.65 blue:.16 alpha:1] CGColor];
        self.pathLayer.fillColor = [[UIColor colorWithRed:.83 green:.65 blue:.16 alpha:1] CGColor];

    }
    
    [self.layer addSublayer:self.pathLayer];
}

-(void)adjustSize:(float)amount
{
    float finalSize = 1 + (amount/100 - ZOOM_SCALE);
    
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath: @"transform.scale"];
    animation.fromValue = @(1);
    animation.toValue = @(finalSize);
    animation.duration = 1.0;
    //animation.timingFunction = [CAMediaTimingFunction functionWithName: kCAMediaTimingFunctionEaseInEaseOut];
    //animation.delegate = self;
    // Important: change the actual layer property before installing the animation.
    [self.layer setValue: animation.toValue forKeyPath: animation.keyPath];
    // Now install the explicit animation, overriding the implicit animation.
    [self.layer addAnimation: animation forKey: animation.keyPath];
}

@end
