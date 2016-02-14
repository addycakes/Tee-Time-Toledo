//
//  RangeFinderVC+Map.h
//  Golf
//
//  Created by Adam Wilson on 7/17/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "RangeFinderVC.h"
#import "HoleOverlayRenderer.h"
#import "WorldOverlayRenderer.h"

@interface RangeFinderVC (Map)
-(void)displayHoleOverlay:(NSDictionary *)holeDict;
-(void)cleanUpMap;
-(void)addMapPin:(UIGestureRecognizer *)sender;
@end
