//
//  Plot.m
//  Golf
//
//  Created by Adam Wilson on 7/4/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "Plot.h"

#define PLOT_CIRCLE_RADIUS 10
@implementation Plot


// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
    //center point
    CGPoint center;
    center.x = rect.origin.x + rect.size.width / 2.0;
    center.y = rect.origin.y + rect.size.height / 2.0;
    
    //line properties
    UIBezierPath *circlePath = [UIBezierPath bezierPath];
    [circlePath addArcWithCenter:center radius:PLOT_CIRCLE_RADIUS startAngle:0 endAngle:(2*M_PI) clockwise:1];
    
    CAShapeLayer *pathLayer = [CAShapeLayer layer];
    pathLayer.frame = rect;
    pathLayer.path = circlePath.CGPath;
    pathLayer.shadowColor = [[UIColor blackColor] CGColor];
    pathLayer.shadowOffset =  CGSizeMake(-5.0, 10.0);
    pathLayer.shadowRadius = 8.0;
    pathLayer.shadowOpacity = 1;
    pathLayer.lineWidth = 10.0f;
    pathLayer.lineJoin = kCALineJoinBevel;
    pathLayer.strokeColor = [[UIColor colorWithRed:.14 green:.35 blue:.48 alpha:1] CGColor];
    pathLayer.fillColor = [[UIColor colorWithRed:.14 green:.35 blue:.48 alpha:1] CGColor];
    pathLayer.fillMode = kCAFillModeForwards;
    pathLayer.zPosition = 1;

    [self.layer addSublayer:pathLayer];
}

// An empty implementation adversely affects performance during animation.
- (void)connectTo:(CGPoint )nextPlot
{
    // Drawing code
    UIBezierPath *linePath = [UIBezierPath bezierPath];
    [linePath moveToPoint:self.center];
    [linePath addLineToPoint:[self convertPoint:nextPlot fromView:self.parentView]];
    
    CAShapeLayer *pathLayer = [CAShapeLayer layer];
    pathLayer.frame = self.bounds;
    pathLayer.path = linePath.CGPath;
    pathLayer.lineWidth = 5.0f;
    pathLayer.lineJoin = kCALineJoinBevel;
    pathLayer.strokeColor = [[UIColor grayColor] CGColor];
    pathLayer.fillColor = nil;
    pathLayer.lineDashPattern = @[@10,@7];
    pathLayer.lineDashPhase = 3.0f;
    pathLayer.fillMode = kCAFillModeForwards;
    
    [self.layer addSublayer:pathLayer];
    
    CABasicAnimation *dashLineAnimation = [CABasicAnimation animationWithKeyPath:@"strokeEnd"];
    dashLineAnimation.duration = 1;
    dashLineAnimation.fromValue = [NSNumber numberWithFloat:0.0f];
    dashLineAnimation.toValue = [NSNumber numberWithFloat:1.0f];
    [pathLayer addAnimation:dashLineAnimation forKey:@"strokeEnd"];

}
@end
