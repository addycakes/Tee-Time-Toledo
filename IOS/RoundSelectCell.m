//
//  RoundSelectCell.m
//  Golf
//
//  Created by Adam Wilson on 6/18/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "RoundSelectCell.h"

@interface RoundSelectCell()
{

}

@end
@implementation RoundSelectCell

- (void)awakeFromNib {
    // Initialization code

}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

#define IMAGE_HEIGHT 184
-(void)showCellInfo:(UITableView *)tableView indexPath:(NSIndexPath *) indexPath
{

    [UIView animateWithDuration:.5 animations:^{        
        [self.playButton setFrame:CGRectMake(self.playButton.frame.origin.x, self.playButton.frame.origin.y + IMAGE_HEIGHT, self.playButton.frame.size.width,self.playButton.frame.size.height)];

        [self.infoImage setFrame:CGRectMake(self.infoImage.frame.origin.x, self.infoImage.frame.origin.y + IMAGE_HEIGHT, self.infoImage.frame.size.width,self.infoImage.frame.size.height)];
        [tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationNone];
        [tableView scrollToRowAtIndexPath:indexPath atScrollPosition:UITableViewScrollPositionMiddle animated:NO];
    }completion:^(BOOL finished) {
        [self.infoImage layoutIfNeeded];
        [self.playButton layoutIfNeeded];
    }];
}

-(void)hideCellInfo:(UITableView *)tableView indexPath:(NSIndexPath *) indexPath
{

    [UIView animateWithDuration:.5 animations:^{
        [self.playButton setFrame:CGRectMake(self.playButton.frame.origin.x, self.playButton.frame.origin.y - IMAGE_HEIGHT, self.playButton.frame.size.width,self.playButton.frame.size.height)];

        [self.infoImage setFrame:CGRectMake(self.infoImage.frame.origin.x, self.infoImage.frame.origin.y - IMAGE_HEIGHT, self.infoImage.frame.size.width,self.infoImage.frame.size.height)];
        [tableView reloadRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationNone];
    }completion:^(BOOL finished) {
        [self.infoImage layoutIfNeeded];
        [self.playButton layoutIfNeeded];
    }];
}

@end
