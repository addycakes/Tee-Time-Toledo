//
//  Circle.h
//  Golf
//
//  Created by Adam Wilson on 7/4/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface Circle : UIView
@property (nonatomic, strong) CAShapeLayer *pathLayer;

-(void)adjustSize:(float)amount;
@end
