//
//  ScorecardVC.m
//  Golf
//
//  Created by Adam Wilson on 6/11/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "ScorecardVC.h"
#import "AppDelegate.h"
#import "Hole.h"

@interface ScorecardVC () //<UIGestureRecognizerDelegate>
{
    AppDelegate *delegate;
   
    NSArray *golfer1Holes;
    NSArray *golfer2Holes;
    NSArray *golfer3Holes;
    NSArray *golfer4Holes;

    NSArray *holeLabels;
    NSArray *parLabels;
    NSArray *handicapLabels;
    NSArray *golfer1Labels;
    NSArray *golfer2Labels;
    NSArray *golfer3Labels;
    NSArray *golfer4Labels;
    
    Round *currentRound;
    NSString *currentCourseName;
    Golfer *currentGolfer;
    NSDictionary *currentCourse;
    
    BOOL hasMoreToShow;
    BOOL is18HoleCourse;
    CGRect scorecardBodyRect;
    NSRange nineHoleRange;
    
    UITextField *textField;
    UILabel *selectedLabel;
}
@end

@implementation ScorecardVC

-(id)init{
    self = [super init];
    is18HoleCourse = YES;
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    delegate = [UIApplication sharedApplication].delegate;
    [delegate setShouldForceLandscape:YES];
    
    //swipe back to return to home screen
    UIScreenEdgePanGestureRecognizer *edgeSwipe = [[UIScreenEdgePanGestureRecognizer alloc] initWithTarget:self action:@selector(unwind:)];
    [edgeSwipe setEdges:UIRectEdgeLeft];
    [self.view addGestureRecognizer:edgeSwipe];
    
    //swipe to scroll cards
    UISwipeGestureRecognizer *swipe = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(cycleCards:)];
    //[swipe setDelegate:self];
    [swipe setDirection:UISwipeGestureRecognizerDirectionLeft];
    [self.view addGestureRecognizer:swipe];
    
    //register targets for golfer labels
    UITapGestureRecognizer *gr1 = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(enterGolferName:)];
    gr1.numberOfTapsRequired = 1;
    [g2Name addGestureRecognizer:gr1];
    UITapGestureRecognizer *gr2 = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(enterGolferName:)];
    gr2.numberOfTapsRequired = 1;
    [g3Name addGestureRecognizer:gr2];
    UITapGestureRecognizer *gr3 = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(enterGolferName:)];
    gr3.numberOfTapsRequired = 1;
    [g4Name addGestureRecognizer:gr3];
}

-(void)setRound:(Round *)round andGolfer:(Golfer *)golfer forCourse:(NSString *)courseName
{
    currentRound = round;
    currentGolfer = golfer;
    currentCourseName = courseName;
    currentCourse = [self courseDictForName:currentCourseName];
    
    if (round.continueCourse != NULL && ![round.continueCourse isEqualToString:@""]) {
        is18HoleCourse = NO;
        hasMoreToShow = YES;
    }
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:YES];
}

-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    scorecardBodyRect = scorecardBody.frame;
    [self setGolferLabels];
    
    //set golfer names
    [g1Name setText:currentGolfer.name];
}

-(void)setGolferLabels
{
    
    //sort holes in golfer arrays
    NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"self" ascending:YES comparator:^(id obj1, id obj2) {
        
        if ([[(Hole *)obj1  number] intValue] > [[(Hole *)obj2 number] intValue]) {
            return (NSComparisonResult)NSOrderedDescending;
        }
        if ([[(Hole *)obj1  number] intValue] < [[(Hole *)obj2 number] intValue]) {
            return (NSComparisonResult)NSOrderedAscending;
        }
        return (NSComparisonResult)NSOrderedSame;
    }];
    
    int i = 0;
    for (Golfer *golfer in currentRound.golfers.allObjects) {
        if ([golfer.name isEqualToString:currentGolfer.name]) {
            if (is18HoleCourse) {
                //golfer1Holes = golfer.holes.allObjects;
                golfer1Holes = [golfer.holes.allObjects sortedArrayUsingDescriptors:@[sortDescriptor]];
            }else{
                NSMutableArray *holesForCurrentCourse = [[NSMutableArray alloc] init];
                for (Hole *h in golfer.holes.allObjects){
                    if ([h.course isEqualToString:currentCourseName]) {
                        [holesForCurrentCourse addObject:h];
                    }
                }
                golfer1Holes = [NSArray arrayWithArray:holesForCurrentCourse];
            }
        }else{
            if (i == 0) {
                [g2Name setText:golfer.name];
                [g2Name setUserInteractionEnabled:NO];
                if (is18HoleCourse) {
                    golfer2Holes  = [golfer.holes.allObjects sortedArrayUsingDescriptors:@[sortDescriptor]];
                }else{
                    NSMutableArray *holesForCurrentCourse = [[NSMutableArray alloc] init];
                    for (Hole *h in golfer.holes.allObjects){
                        if ([h.course isEqualToString:currentCourseName]) {
                            [holesForCurrentCourse addObject:h];
                        }
                    }
                    golfer2Holes = [NSArray arrayWithArray:holesForCurrentCourse];
                }
                
            }else if (i == 1) {
                [g3Name setText:golfer.name];
                [g3Name setUserInteractionEnabled:NO];
                if (is18HoleCourse) {
                    golfer3Holes = [golfer.holes.allObjects sortedArrayUsingDescriptors:@[sortDescriptor]];
                }else{
                    NSMutableArray *holesForCurrentCourse = [[NSMutableArray alloc] init];
                    for (Hole *h in golfer.holes.allObjects){
                        if ([h.course isEqualToString:currentCourseName]) {
                            [holesForCurrentCourse addObject:h];
                        }
                    }
                    golfer3Holes = [NSArray arrayWithArray:holesForCurrentCourse];
                }
            }else if (i == 2) {
                [g4Name setText:golfer.name];
                [g4Name setUserInteractionEnabled:NO];
                if (is18HoleCourse) {
                    golfer4Holes  = [golfer.holes.allObjects sortedArrayUsingDescriptors:@[sortDescriptor]];
                }else{
                    NSMutableArray *holesForCurrentCourse = [[NSMutableArray alloc] init];
                    for (Hole *h in golfer.holes.allObjects){
                        if ([h.course isEqualToString:currentCourseName]) {
                            [holesForCurrentCourse addObject:h];
                        }
                    }
                    golfer4Holes = [NSArray arrayWithArray:holesForCurrentCourse];
                }
            }
            i++;
        }
    }
    
    
    if (golfer1Holes.count > 9){
        hasMoreToShow = YES;
    }
    
    [self getLabelValues];
}

-(void)getLabelValues
{

    //main values arrays
    NSSortDescriptor *ascendingSort = [[NSSortDescriptor alloc] initWithKey:@"tag" ascending:YES];
    NSMutableArray *sortedHolesArray = [[NSMutableArray alloc] init];
    NSMutableArray *sortedParsArray = [[NSMutableArray alloc] init];
    NSMutableArray *sortedHandicapsArray = [[NSMutableArray alloc] init];
    NSMutableArray *sortedScoresArray = [[NSMutableArray alloc] init];

    //secondary values arrays for other golfers scores
    NSMutableArray *sortedG2Array = [[NSMutableArray alloc] init];
    NSMutableArray *sortedG3Array = [[NSMutableArray alloc] init];
    NSMutableArray *sortedG4Array = [[NSMutableArray alloc] init];
    
    //grab all labels and store into arrays
    for (id view in self.view.subviews) {
        if ([view isKindOfClass:[UILabel class]]) {
            if (![view isEqual:g1Name] ||
                ![view isEqual:g2Name] ||
                ![view isEqual:g3Name] ||
                ![view isEqual:g4Name]) {
                
                UILabel *cell = (UILabel *)view;
                
                if (cell.tag < 10) {
                    [sortedHolesArray addObject:cell];
                }else if (cell.tag < 20 && cell.tag >= 10) {
                    [sortedParsArray addObject:cell];
                }else if (cell.tag < 30 && cell.tag >= 20) {
                    [sortedScoresArray addObject:cell];
                }else if (cell.tag < 40 && cell.tag >= 30) {
                    if (![g2Name.text isEqualToString:@""]) {
                        [sortedG2Array addObject:cell];
                        UITapGestureRecognizer *gr = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(incrementScore:)];
                        gr.numberOfTapsRequired = 1;
                        [cell addGestureRecognizer:gr];
                        if (!self.isRoundFinished) {
                            [cell setUserInteractionEnabled:YES];
                        }
                    }
                }else if (cell.tag < 50 && cell.tag >= 40) {
                    [sortedHandicapsArray addObject:cell];
                }else if (cell.tag < 60 && cell.tag >= 50) {
                    if (![g3Name.text isEqualToString:@""]) {
                        [sortedG3Array addObject:cell];
                        UITapGestureRecognizer *gr = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(incrementScore:)];
                        gr.numberOfTapsRequired = 1;
                        [cell addGestureRecognizer:gr];
                        if (!self.isRoundFinished) {
                            [cell setUserInteractionEnabled:YES];
                        }                    }
                }else if (cell.tag < 70 && cell.tag >= 60){
                    if (![g4Name.text isEqualToString:@""]) {
                        [sortedG4Array addObject:cell];
                        UITapGestureRecognizer *gr = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(incrementScore:)];
                        gr.numberOfTapsRequired = 1;
                        [cell addGestureRecognizer:gr];
                        if (!self.isRoundFinished) {
                            [cell setUserInteractionEnabled:YES];
                        }
                    }
                }
            }
        }
    }
    
    holeLabels = [sortedHolesArray sortedArrayUsingDescriptors:[NSArray arrayWithObject:ascendingSort]];
    parLabels = [sortedParsArray sortedArrayUsingDescriptors:[NSArray arrayWithObject:ascendingSort]];
    handicapLabels = [sortedHandicapsArray sortedArrayUsingDescriptors:[NSArray arrayWithObject:ascendingSort]];
    golfer1Labels = [sortedScoresArray sortedArrayUsingDescriptors:[NSArray arrayWithObject:ascendingSort]];
    golfer2Labels = [sortedG2Array sortedArrayUsingDescriptors:[NSArray arrayWithObject:ascendingSort]];
    golfer3Labels = [sortedG3Array sortedArrayUsingDescriptors:[NSArray arrayWithObject:ascendingSort]];
    golfer4Labels = [sortedG4Array sortedArrayUsingDescriptors:[NSArray arrayWithObject:ascendingSort]];

    //set range
    nineHoleRange.location = 0;
    nineHoleRange.length = 9;
    
    //store sorted keys for course dictionary
    NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"self" ascending:YES comparator:^(id obj1, id obj2) {
        
        if ([obj1 integerValue] > [obj2 integerValue]) {
            return (NSComparisonResult)NSOrderedDescending;
        }
        if ([obj1 integerValue] < [obj2 integerValue]) {
            return (NSComparisonResult)NSOrderedAscending;
        }
        return (NSComparisonResult)NSOrderedSame;
    }];
    [self setPreloadLabels:[[currentCourse.allKeys sortedArrayUsingDescriptors:@[sortDescriptor]] subarrayWithRange:nineHoleRange]];
    
    //if (golfer1Holes.count > 9) {
    //    [self setScores:[golfer1Holes subarrayWithRange:nineHoleRange] forLabels:golfer1Labels];
    //    [self setScores:[golfer2Holes subarrayWithRange:nineHoleRange] forLabels:golfer2Labels];
    //    [self setScores:[golfer3Holes subarrayWithRange:nineHoleRange] forLabels:golfer3Labels];
    //    [self setScores:[golfer4Holes subarrayWithRange:nineHoleRange] forLabels:golfer4Labels];
    //}else{
        [self setScores:golfer1Holes forLabels:golfer1Labels];
        [self setScores:golfer2Holes forLabels:golfer2Labels];
        [self setScores:golfer3Holes forLabels:golfer3Labels];
        [self setScores:golfer4Holes forLabels:golfer4Labels];
    //}
}

-(void)setScores:(NSArray *)values forLabels:(NSArray *)labels
{
    int scoreTotal = 0;
    for (Hole *hole in values) {
        
        NSNumber *labelNumber;
        if (is18HoleCourse) {
            //front 9
            if (nineHoleRange.location == 0) {
                if (hole.number.intValue < 10) {
                    labelNumber = [NSNumber numberWithInt:hole.number.intValue-1];
                }
            }else{
                //back 9
                if (hole.number.intValue > 9) {
                    if  (hole.number.intValue == 18){
                        labelNumber = [NSNumber numberWithInt:8];
                    }else{
                        labelNumber = [NSNumber numberWithInt:(hole.number.intValue % 9) - 1];
                    }
                }
            }
        }else{
            labelNumber = [NSNumber numberWithInt:hole.number.intValue-1];
        }
        
        UILabel *parLabel = [parLabels objectAtIndex:labelNumber.integerValue];
        UILabel *scoreLabel = [labels objectAtIndex:labelNumber.integerValue];
        
        //pars are green and basic
        //bogies are yellow with an empty square
        //birdies are blue with an empty circle
        //eagles are blue with a filled circle
        //double bogies are yellow with a filled square
        if (hole.score.intValue == parLabel.text.intValue) {
            [scoreLabel setTextColor:[UIColor colorWithRed:.33 green:.47 blue:.25 alpha:1]];
        }else if (hole.score.intValue == parLabel.text.intValue + 1) {
            UIView *square = [[UIView alloc] initWithFrame:CGRectMake(0,0, scoreLabel.frame.size.width*1.25, scoreLabel.frame.size.width*1.25)];
            [square setCenter:scoreLabel.center];
            [square setTag:1000];
            square.backgroundColor = [UIColor clearColor];
            square.layer.borderWidth = 3;
            square.layer.borderColor = [UIColor colorWithRed:.83 green:.65 blue:.16 alpha:1].CGColor;
            [scoreLabel setTextColor:[UIColor colorWithRed:.83 green:.65 blue:.16 alpha:1]];
            [self.view addSubview:square];
            [self.view bringSubviewToFront:square];
            [self.view bringSubviewToFront:scoreLabel];
        }else if (hole.score.intValue == parLabel.text.intValue - 1) {
            UIView *circle = [[UIView alloc] initWithFrame:CGRectMake(0,0, scoreLabel.frame.size.width*1.25, scoreLabel.frame.size.width*1.25)];
            [circle setCenter:scoreLabel.center];
            [circle setTag:1000];
            circle.layer.cornerRadius = circle.frame.size.width/2;
            circle.backgroundColor = [UIColor clearColor];
            circle.layer.borderWidth = 3;
            circle.layer.borderColor = [UIColor colorWithRed:.14 green:.35 blue:.48 alpha:1].CGColor;
            [scoreLabel setTextColor:[UIColor colorWithRed:.14 green:.35 blue:.48 alpha:1]];
            [self.view addSubview:circle];
            [self.view bringSubviewToFront:circle];
            [self.view bringSubviewToFront:scoreLabel];
        }else if (hole.score.intValue == parLabel.text.intValue - 2) {
            UIView *circle = [[UIView alloc] initWithFrame:CGRectMake(0,0, scoreLabel.frame.size.width*1.25, scoreLabel.frame.size.width*1.25)];
            [circle setCenter:scoreLabel.center];
            [circle setTag:1000];
            circle.layer.cornerRadius = circle.frame.size.width/2;
            circle.backgroundColor = [UIColor colorWithRed:.14 green:.35 blue:.48 alpha:1];
            [scoreLabel setTextColor:[UIColor colorWithRed:.93 green:.93 blue:.94 alpha:1]];
            [self.view addSubview:circle];
            [self.view bringSubviewToFront:circle];
            [self.view bringSubviewToFront:scoreLabel];
        }else if (hole.score.intValue == parLabel.text.intValue + 2) {
            UIView *square = [[UIView alloc] initWithFrame:CGRectMake(0,0, scoreLabel.frame.size.width*1.25, scoreLabel.frame.size.width*1.25)];
            [square setCenter:scoreLabel.center];
            [square setTag:1000];
            square.backgroundColor = [UIColor colorWithRed:.83 green:.65 blue:.16 alpha:1];
            [scoreLabel setTextColor:[UIColor colorWithRed:.93 green:.93 blue:.94 alpha:1]];
            [self.view addSubview:square];
            [self.view bringSubviewToFront:square];
            [self.view bringSubviewToFront:scoreLabel];
        }

        
        [scoreLabel setText:hole.score.stringValue];
        scoreTotal += hole.score.intValue;
    }
    
    UILabel *scoreTotalLabel = [labels lastObject];
    [scoreTotalLabel setText:[NSString stringWithFormat:@"%d",scoreTotal]];
}

-(void)setPreloadLabels:(NSArray *)values
{
    //store sorted keys for course dictionary
    int parTotal = 0;
    for (NSString *holeKey in values) {
        NSDictionary *hole = [currentCourse objectForKey:holeKey];
        
        NSInteger labelNumber;
        if ([[hole valueForKey:@"Number"] integerValue] > 9) {
            labelNumber = ([[hole valueForKey:@"Number"] integerValue] - 1) % 9;
        }else{
            labelNumber = [[hole valueForKey:@"Number"] integerValue] - 1;
        }
        
        UILabel *holeLabel = [holeLabels objectAtIndex:labelNumber];
        UILabel *parLabel = [parLabels objectAtIndex:labelNumber];
        UILabel *handicapLabel = [handicapLabels objectAtIndex:labelNumber];
        
        [holeLabel setText:[hole valueForKey:@"Number"]];
        [parLabel setText:[hole valueForKey:@"Par"]];
        [handicapLabel setText:[hole valueForKey:@"handicap"]];
        
        parTotal += [[hole valueForKey:@"Par"] intValue];
    }
    
    UILabel *parTotalLabel = [parLabels lastObject];
    [parTotalLabel setText:[NSString stringWithFormat:@"%d",parTotal]];
}

-(void)incrementScore:(UITapGestureRecognizer *)gr
{
    //grab label that was tapped
    selectedLabel = (UILabel *)[gr view];
    
    //check to cycle values
    if ([selectedLabel.text isEqualToString:@""] || [selectedLabel.text isEqualToString:@"8"]) {
        [selectedLabel setText:@"0"];
    }
    
    //increment score
    int score = selectedLabel.text.intValue;
    score++;
    [selectedLabel setText:[NSString stringWithFormat:@"%d",score]];
    
    //get hole number
    int holeNumber;

    NSString *golferName;
    if ([golfer2Labels containsObject:selectedLabel]) {
        golferName = g2Name.text;
        holeNumber = (int)[golfer2Labels indexOfObject:selectedLabel]+1;
    }else if ([golfer3Labels containsObject:selectedLabel]) {
        golferName = g3Name.text;
        holeNumber = (int)[golfer3Labels indexOfObject:selectedLabel]+1;
    }else if ([golfer4Labels containsObject:selectedLabel]){
        golferName = g4Name.text;
        holeNumber = (int)[golfer4Labels indexOfObject:selectedLabel]+1;
    }

    [self saveScore:score forGolfer:golferName andHoleNumber:(int)holeNumber];
    selectedLabel = nil;
}

-(void)saveScore:(int)score forGolfer:(NSString *)golfer andHoleNumber:(int)number
{
    //add golfer
    BOOL isNew = YES;
    for (Golfer *g in currentRound.golfers.allObjects) {
        if ([g.name isEqualToString:golfer]) {
            for (Hole *h in g.holes.allObjects) {
                //check for hole
                if ([[h valueForKey:@"number"] intValue] == number && [[h valueForKey:@"course"]isEqualToString:currentCourseName]) {
                    //get score from stroke log
                    [h setValue:[NSNumber numberWithInt:score] forKey:@"score"];
                    isNew = NO;
                    break;
                }
            }
            if (isNew) {
                Hole *hole = [[Hole alloc] initWithEntity:[NSEntityDescription entityForName:@"Hole" inManagedObjectContext:delegate.managedObjectContext] insertIntoManagedObjectContext:delegate.managedObjectContext];
                [hole setCourse:currentCourseName];
                [hole setScore:[NSNumber numberWithInt:score]];
                [hole setNumber:[NSNumber numberWithInt:number]];
                [g addHolesObject:hole];

            }
            [delegate saveContext];
            break;
        }
    }
    

    
}

-(IBAction)enterGolferName:(UITapGestureRecognizer *)gr
{
    //grab label that was tapped
    selectedLabel = (UILabel *)[gr view];
    
    //display text field
    textField = [[UITextField alloc] initWithFrame:CGRectMake(0,0,
                                                              2*self.view.frame.size.width/3,
                                                              50)];
    CGPoint center = self.view.center;
    center.y -= 50;
    [textField setCenter:center];

    [textField addTarget:self action:@selector(textFieldReturn:) forControlEvents:UIControlEventEditingDidEndOnExit];
    [textField setBackgroundColor:[UIColor lightGrayColor]];
    [textField setPlaceholder:@"Enter name..."];
    [textField becomeFirstResponder];
    
    [self.view addSubview:textField];
    [self.view bringSubviewToFront:textField];
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    UITouch *t = [touches anyObject];
    
    CGPoint tap = [t locationInView:self.view];
    if ([textField isFirstResponder]) {
        if (!CGRectContainsPoint(textField.frame, tap)) {
            [textField setText:@""];
            [self textFieldReturn:textField];
            [textField removeFromSuperview];
            textField = nil;
            selectedLabel = nil;
        }
    }
}

-(void)textFieldReturn:(UITextField *)sender
{
    NSString *golferName = [sender.text stringByTrimmingCharactersInSet:
                            [NSCharacterSet whitespaceCharacterSet]];
    
    if (![golferName isEqualToString:@""]) {
        [self addGolfer:golferName];
        [selectedLabel setText:golferName];
        selectedLabel = nil;
    }
    
    [textField removeFromSuperview];
    textField = nil;
}

-(void)addGolfer:(NSString *)name
{
    //add golfer
    Golfer *golfer = [[Golfer alloc] initWithEntity:[NSEntityDescription entityForName:@"Golfer" inManagedObjectContext:delegate.managedObjectContext] insertIntoManagedObjectContext:delegate.managedObjectContext];
    [golfer setName:name];
    [currentRound addGolfersObject:golfer];
    [delegate saveContext];
    
    [self getLabelValues];
}

-(void)cycleCards:(UIGestureRecognizer *)gr
{
    if  ([gr state] == UIGestureRecognizerStateEnded){
        if (hasMoreToShow) {
            //clear labels
            for (id view in self.view.subviews) {
                if ([view isKindOfClass:[UILabel class]]) {
                    if ([view isEqual:g1Name] ||
                        [view isEqual:g2Name] ||
                        [view isEqual:g3Name] ||
                        [view isEqual:g4Name]) {
                        continue;
                    }else{
                        UILabel *cell = (UILabel *)view;
                        [cell setText:@""];
                    }
                }else if ([view isKindOfClass:[UIView class]]){
                    if ([(UIView *)view tag] == 1000) {
                        [view removeFromSuperview];
                    }
                }
            }
            
            //animate scorecard
            [UIImageView animateWithDuration:1 animations:^{
                [scorecardBody setFrame:CGRectMake(self.view.frame.origin.x - 500, self.view.frame.origin.y, scorecardBody.frame.size.width, scorecardBody.frame.size.height)];
            }completion:^(BOOL finished) {
                [scorecardBody setCenter:CGPointMake(self.view.center.x+500, self.view.center.y)];
                [UIImageView animateWithDuration:1 animations:^{
                    [scorecardBody setFrame:scorecardBodyRect];
                }completion:^(BOOL finished) {
                    
                    //load new scores
                    if (is18HoleCourse) {
                        NSInteger newLocation = nineHoleRange.location + 9;
                        NSInteger newLength = golfer1Holes.count - 9;
                        
                        if (newLocation >= golfer1Holes.count || newLength >= golfer1Holes.count) {
                            newLocation = 0;
                            newLength = 9;
                        }
                        
                        nineHoleRange.location = newLocation;
                        nineHoleRange.length = newLength;
                        
                        [self setScores:golfer1Holes forLabels:golfer1Labels];
                        [self setScores:golfer2Holes forLabels:golfer2Labels];
                        [self setScores:golfer3Holes forLabels:golfer3Labels];
                        [self setScores:golfer4Holes forLabels:golfer4Labels];
                        
                        //store sorted keys for course dictionary
                        NSSortDescriptor *sortDescriptor = [NSSortDescriptor sortDescriptorWithKey:@"self" ascending:YES comparator:^(id obj1, id obj2) {
                            
                            if ([obj1 integerValue] > [obj2 integerValue]) {
                                return (NSComparisonResult)NSOrderedDescending;
                            }
                            if ([obj1 integerValue] < [obj2 integerValue]) {
                                return (NSComparisonResult)NSOrderedAscending;
                            }
                            return (NSComparisonResult)NSOrderedSame;
                        }];
                        [self setPreloadLabels:[[currentCourse.allKeys sortedArrayUsingDescriptors:@[sortDescriptor]] subarrayWithRange:nineHoleRange]];
                    }else{
                        if ([currentCourseName isEqualToString:currentRound.course]) {
                            currentCourseName = currentRound.continueCourse;
                        }else{
                            currentCourseName = currentRound.course;
                        }
                        currentCourse = [self courseDictForName:currentCourseName];
                        [self setGolferLabels];
                    }
                    
                }];
            }];
        }
    }
}

//get all courses from plist
-(NSDictionary *)courseDictForName:(NSString *)courseName
{
    NSString *filePathBundle = [[NSBundle mainBundle] pathForResource:@"Courses" ofType:@"plist"];
    NSDictionary *plist = [[NSMutableDictionary alloc] initWithContentsOfFile:filePathBundle];
    
    NSMutableDictionary *course = [plist objectForKey:courseName];
    if ([course objectForKey:@"MAP"] != nil) {
        [course removeObjectForKey:@"MAP"];
    }
    
    return (NSDictionary *)course;
}


- (NSUInteger)supportedInterfaceOrientations {
    if  ([self isBeingDismissed]){
        return UIInterfaceOrientationMaskPortrait;
    }
    return UIInterfaceOrientationMaskLandscape;
}

-(BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)orientation {
    return UIInterfaceOrientationLandscapeLeft;
}

-(void)unwind:(UIGestureRecognizer *)gr
{
    if ([gr state] == UIGestureRecognizerStateBegan) {
        [delegate setShouldForceLandscape:NO];
        [self.presentingViewController dismissViewControllerAnimated:YES completion:nil];
    }
}
/*

-(BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer{
    // return
    return true;
}
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
