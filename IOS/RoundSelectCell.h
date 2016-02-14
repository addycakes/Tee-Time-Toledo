//
//  RoundSelectCell.h
//  Golf
//
//  Created by Adam Wilson on 6/18/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RoundSelectCell : UITableViewCell
@property (nonatomic) BOOL hasInfo;
@property (weak, nonatomic) IBOutlet UILabel *cellLabel;
@property (weak, nonatomic) IBOutlet UIImageView *backgroundImage;
@property (weak, nonatomic) IBOutlet UIImageView *infoImage;
@property (weak, nonatomic) IBOutlet UIView *labelShadowBar;
@property (weak, nonatomic) IBOutlet UIButton *playButton;
@property (weak, nonatomic) IBOutlet UILabel *strokesLabel;
@property (weak, nonatomic) IBOutlet UILabel *scoreLabel;

-(void)showCellInfo:(UITableView *)tableView indexPath:(NSIndexPath *) indexPath;
-(void)hideCellInfo:(UITableView *)tableView indexPath:(NSIndexPath *) indexPath;
@end
