//
//  Advertisement.m
//  Golf
//
//  Created by Adam Wilson on 8/4/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "Advertisement.h"

@implementation Advertisement


// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
    self.layer.shadowColor = [[UIColor blackColor] CGColor];
    self.layer.shadowOffset =  CGSizeMake(-5.0, 10.0);
    self.layer.shadowRadius = 8.0;
    self.layer.shadowOpacity = 1;

}


- (IBAction)close:(UIButton *)sender
{
    [self removeFromSuperview];
}
@end
