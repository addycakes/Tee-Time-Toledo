//
//  PieWheelGraph.m
//  Golf
//
//  Created by Adam Wilson on 7/3/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "PieWheelGraph.h"
#import <QuartzCore/QuartzCore.h>

@interface PieWheelGraph ()
{
    CGPoint center;
    float radius;
    
    BOOL isAnimationDone;
}

@end
@implementation PieWheelGraph


// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    
    self.birdiePercent = (360 * self.birdiePercent) * .017453;
    self.bogiePercent = (360 * self.bogiePercent) * .017453;
    self.parPercent = (360 * self.parPercent) * .017453;
    
    if (self.birdiePercent == 0 && self.bogiePercent == 0 && self.parPercent == 0) {
        self.birdiePercent = (360 * .333333) * .017453;;
        self.bogiePercent = (360 * .333333) * .017453;;
        self.parPercent = (360 * .333333) * .017453;
    }
    
    CGRect bounds = [self bounds];
    //center point
    center.x = bounds.origin.x + bounds.size.width / 2.0;
    center.y = bounds.origin.y + bounds.size.height / 2.0;
    
    //radius
    radius = (bounds.size.width / 2.0)-30;
    
    //CGContextAddArc(ctx,
    [self setBackgroundColor:[UIColor whiteColor]];
    
    //line properties
    UIBezierPath *birdiePath = [UIBezierPath bezierPath];
    [birdiePath addArcWithCenter:center radius:radius startAngle:(3*M_PI/2) endAngle:(self.birdiePercent - M_PI/2) clockwise:1];
    
    CAShapeLayer *pathLayer = [CAShapeLayer layer];
    pathLayer.frame = self.bounds;
    pathLayer.fillColor = nil;
    pathLayer.lineWidth = 70.0f;
    pathLayer.shadowColor = [[UIColor blackColor] CGColor];
    pathLayer.shadowOffset =  CGSizeMake(-5.0, 10.0);
    pathLayer.shadowRadius = 8.0;
    pathLayer.shadowOpacity = 1;
    pathLayer.lineJoin = kCALineJoinBevel;
    pathLayer.zPosition = 1;
    pathLayer.path = birdiePath.CGPath;
    pathLayer.strokeColor = [[UIColor colorWithRed:.14 green:.35 blue:.48 alpha:1] CGColor];
    [self.layer addSublayer:pathLayer];

    CABasicAnimation *birdieAnimation = [CABasicAnimation animationWithKeyPath:@"strokeEnd"];
    birdieAnimation.duration = 1;
    birdieAnimation.fromValue = [NSNumber numberWithFloat:0.0f];
    birdieAnimation.toValue = [NSNumber numberWithFloat:1.0f];
    [birdieAnimation setDelegate:self];
    [pathLayer addAnimation:birdieAnimation forKey:@"strokeEnd"];
    
    isAnimationDone = YES;
}

-(void)animationDidStop:(CAAnimation *)anim finished:(BOOL)flag
{
    CAShapeLayer *pathLayer = [CAShapeLayer layer];
    pathLayer.frame = self.bounds;
    pathLayer.fillColor = nil;
    pathLayer.lineWidth = 70.0f;
    pathLayer.shadowColor = [[UIColor blackColor] CGColor];
    pathLayer.shadowOffset =  CGSizeMake(-5.0, 10.0);
    pathLayer.shadowRadius = 8.0;
    pathLayer.shadowOpacity = .75;
    pathLayer.lineJoin = kCALineJoinBevel;
    
    if (isAnimationDone) {
        //line properties
        UIBezierPath *parPath = [UIBezierPath bezierPath];
        [parPath addArcWithCenter:center radius:radius startAngle:(self.birdiePercent - M_PI/2) endAngle:(self.parPercent + self.birdiePercent - M_PI/2) clockwise:1];
        
        pathLayer.zPosition = 2;
        pathLayer.path = parPath.CGPath;
        pathLayer.strokeColor = [[UIColor colorWithRed:.33 green:.47 blue:.25 alpha:1] CGColor];
        [self.layer addSublayer:pathLayer];
        
        CABasicAnimation *parAnimation = [CABasicAnimation animationWithKeyPath:@"strokeEnd"];
        parAnimation.duration = .75;
        parAnimation.fromValue = [NSNumber numberWithFloat:0.0f];
        parAnimation.toValue = [NSNumber numberWithFloat:1.0f];
        [parAnimation setDelegate:self];
        [pathLayer addAnimation:parAnimation forKey:@"strokeEnd"];
        
        isAnimationDone = NO;
    }else{
        //line properties
        UIBezierPath *bogeyPath = [UIBezierPath bezierPath];
        [bogeyPath addArcWithCenter:center radius:radius startAngle:(self.parPercent + self.birdiePercent - M_PI/2)  endAngle:(self.parPercent + self.birdiePercent + self.bogiePercent - M_PI/2) clockwise:1];
        
        pathLayer.zPosition = 1;
        pathLayer.path = bogeyPath.CGPath;
        pathLayer.strokeColor = [[UIColor colorWithRed:.83 green:.65 blue:.16 alpha:1] CGColor];
        [self.layer addSublayer:pathLayer];

        CABasicAnimation *bogeyAnimation = [CABasicAnimation animationWithKeyPath:@"strokeEnd"];
        bogeyAnimation.duration = 1;
        bogeyAnimation.fromValue = [NSNumber numberWithFloat:0.0f];
        bogeyAnimation.toValue = [NSNumber numberWithFloat:1.0f];
        [pathLayer addAnimation:bogeyAnimation forKey:@"strokeEnd"];
    }
    

}


@end
