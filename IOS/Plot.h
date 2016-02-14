//
//  Plot.h
//  Golf
//
//  Created by Adam Wilson on 7/4/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "StatsVC.h"

@interface Plot : UIView
@property (nonatomic, strong) UIView *parentView;

-(void)connectTo:(CGPoint )nextPlot;
@end
