//
//  TeeTime.h
//  Golf
//
//  Created by Adam Wilson on 12/29/15.
//  Copyright © 2015 Adam Wilson. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface TeeTime : NSObject
@property (nonatomic) BOOL isReady;
@property (nonatomic, strong) NSString *selectedMinute;
@property (nonatomic, strong) NSString *selectedHour;
@property (nonatomic, strong) NSString *selectedDay;
@property (nonatomic, strong) NSString *selectedMonth;
@property (nonatomic, strong) NSString *selectedYear;
@property (nonatomic) int carts;
@property (nonatomic) int golfers;


-(instancetype)initForBedford;
-(NSArray *)getTimes;
-(void)submit;

@end
