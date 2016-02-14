//
//  TeeTimeVC.m
//  Golf
//
//  Created by Adam Wilson on 6/11/15.
//  Copyright (c) 2015 Adam Wilson. All rights reserved.
//

#import "TeeTimeVC.h"
#import "TeeTime.h"
#import "TeeTimeFactory.h"
#import "RoundSelectCell.h"
#import "CoursesTVC.h"

@interface TeeTimeVC ()
{
    ProfileHeaderBar *profileBar;

    UITextField *textField;
    NSMutableArray *allGolfers;
    
    NSArray *months;
    NSDate *currentMonth;
    int positionForCalendarCycle;
    
    UIButton *selectedDay;
    NSString *selectedMonth;
    NSString *selectedCourse;
    NSString *selectedHour;
    NSString *selectedMinute;
    NSString *selectedYear;
    
    TeeTime *teeTime;
    
    NSMutableArray *availableTimes;
    UIPickerView *pickerView;
    
    CoursesTVC *coursesTVC;
    UITableView *coursesTableView;
}
@end

@implementation TeeTimeVC

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    profileBar = [ProfileHeaderBar sharedProfileBar];
    
    //swipe back to return to home screen
    UIScreenEdgePanGestureRecognizer *swipe = [[UIScreenEdgePanGestureRecognizer alloc] initWithTarget:self action:@selector(unwind:)];
    [swipe setEdges:UIRectEdgeLeft];
    [self.view addGestureRecognizer:swipe];
    
    //tableview setup
    [golfersTableView setDataSource:self];
    [golfersTableView setDelegate:self];
    
    allGolfers = [[NSMutableArray alloc] initWithObjects:profileBar.profileNameLabel.text, nil];
    
    months = @[@"January",@"February",@"March",@"April",@"May",@"June",
               @"July",@"August",@"September",@"October",@"November",@"December"];
    availableTimes = [[NSMutableArray alloc]init];
    
    for (int i = 1; i < 24; i++) {
        int x = i;
        NSString *period = @"AM";
        for (int j = 0; j < 60; j=j+5) {
            if (i > 12) {
                x = i % 12;
                period = @"PM";
            }
            NSString *time = [NSString stringWithFormat:@"%02d:%02d %@", x, j, period];
            [availableTimes addObject:time];
        }
    }
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:YES];
    
    //unset profile bar items
    [profileBar.profileBackButton removeTarget:self action:@selector(unwind:) forControlEvents:UIControlEventTouchDown];
    [profileBar setShouldShowBackButton:NO];
}

-(void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:YES];
    //setup profile button
    [profileBar setShouldShowBackButton:YES];
    [profileBar.profileBackButton addTarget:self action:@selector(unwind:) forControlEvents:UIControlEventTouchDown];
    
    [profileBar setFrame:CGRectMake(0, 0, self.view.frame.size.width, PROFILE_BAR_HEIGHT)];
    [self.view addSubview:profileBar];
    [self.view bringSubviewToFront:profileBar];
    
    //set calendar
    currentMonth = [NSDate date];
    [self updateCalendarButtonsForDate:currentMonth];

    //setup calendar buttons. all calendar buttons have tag > 1
    for (NSObject *obj in self.view.subviews) {
        if ([obj isKindOfClass:[UIButton class]]) {
            UIButton *button = (UIButton *)obj;
            if (button.tag >= 1) {
                [button addTarget:self action:@selector(selectDayOfMonth:) forControlEvents:UIControlEventTouchDown];
            }
        }
    }
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    teeTime.golfers = (int)allGolfers.count;
    return allGolfers.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    RoundSelectCell *cell = [[RoundSelectCell alloc] init];
    cell = [tableView dequeueReusableCellWithIdentifier:@"TeeTimeCell" forIndexPath:indexPath];
    
    NSString *golferName = [allGolfers objectAtIndex:indexPath.row];
    [cell.cellLabel setText:golferName];
    
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //select to add golfer name
    [self enterGolferName];
    [golfersTableView deselectRowAtIndexPath:indexPath animated:NO];

}

-(void)enterGolferName
{
    textField = [[UITextField alloc] initWithFrame:CGRectMake(0, 0, golfersTableView.frame.size.width, 50)];
    [textField setCenter:self.view.center];
    [textField addTarget:self action:@selector(textFieldReturn:) forControlEvents:UIControlEventEditingDidEndOnExit];
    [textField setBackgroundColor:[UIColor lightGrayColor]];
    [textField setPlaceholder:@"Enter name..."];
    [textField becomeFirstResponder];
    
    [self.view addSubview:textField];
    [self.view bringSubviewToFront:textField];
    
    //disable tableview
    [golfersTableView setUserInteractionEnabled:NO];
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    UITouch *t = [touches anyObject];
    
    CGPoint tap = [t locationInView:self.view];
    if ([textField isFirstResponder]) {
        if (!CGRectContainsPoint(textField.frame, tap)) {
            [textField setText:@""];
            [self textFieldReturn:textField];
        }
    }
}

-(void)textFieldReturn:(UITextField *)sender
{
    //enable tableview
    [golfersTableView setUserInteractionEnabled:YES];
    
    NSString *golferName = [sender.text stringByTrimmingCharactersInSet:
                            [NSCharacterSet whitespaceCharacterSet]];
    
    if (![golferName isEqualToString:@""]) {
        [allGolfers addObject:golferName];
        NSIndexPath *path = [NSIndexPath indexPathForRow:allGolfers.count-1 inSection:0];
        [golfersTableView insertRowsAtIndexPaths:@[path] withRowAnimation:UITableViewRowAnimationAutomatic];
    }
    
    [sender resignFirstResponder];
    [sender removeFromSuperview];
}

-(BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

-(void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    //only be able to remove created golfers, not the initial
    if (indexPath.row != 0) {
        if (editingStyle == UITableViewCellEditingStyleDelete) {
            //remove golfer
            //[self removeGolfer:[allGolfers objectAtIndex:indexPath.row]];
            [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
        }
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)unwind:(UIGestureRecognizer *)gr
{
    if (([gr state] == UIGestureRecognizerStateBegan)) {
        if  (coursesTVC != nil){
            //dismiss course table view controll
            [coursesTableView removeFromSuperview];
            coursesTVC = nil;
            coursesTableView = nil;
        }else{
            [self.presentingViewController dismissViewControllerAnimated:YES completion:nil];
        }
    }
}

- (IBAction)nextMonth:(UIButton *)sender {
    
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *offsetComponents = [[NSDateComponents alloc] init];
    [offsetComponents setMonth:1]; // note that I'm setting it to -1
    NSDate *nextMonth = [calendar dateByAddingComponents:offsetComponents toDate:currentMonth options:0];
    
    currentMonth = nextMonth;
    [self updateCalendarButtonsForDate:nextMonth];
}

- (IBAction)previousMonth:(UIButton *)sender {
    
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *offsetComponents = [[NSDateComponents alloc] init];
    [offsetComponents setMonth:-1];
    NSDate *previousMonth = [calendar dateByAddingComponents:offsetComponents toDate:currentMonth options:0];
    currentMonth = previousMonth;
    [self updateCalendarButtonsForDate:previousMonth];
}


#define DAYS_IN_WEEK 7
#define CALENDAR_BUTTONS_COUNT 37
-(void)updateCalendarButtonsForDate:(NSDate *)date
{
    NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
    dateFormat.locale = [[NSLocale alloc] initWithLocaleIdentifier:@"en_US"];
    
    //get current year
    [dateFormat setDateFormat:@"yyyy"];
    selectedYear = [dateFormat stringFromDate:date];

    //set month label
    [dateFormat setDateFormat:@"MM"];
    selectedMonth = [months objectAtIndex:[[dateFormat stringFromDate:date] intValue]-1];
    [monthLabel setText:selectedMonth];

    //calc what day of the week the first falls on
    [dateFormat setDateFormat:@"EEEE"];
    NSString *dayOfWeek = [dateFormat stringFromDate:date];
    [dateFormat setDateFormat:@"dd"];
    
    //0 is the day before first of month since dates start at 1
    // looping 7 times returns correct day of week for this situation
    int daysAwayFromFirst = [[dateFormat stringFromDate:date] intValue] % DAYS_IN_WEEK;
    if  (daysAwayFromFirst == 0){
        daysAwayFromFirst = 7;
    }
    
    NSString *startingDay = @"";
    for (int i = 1; i < (daysAwayFromFirst); i++) {
        if ([dayOfWeek isEqualToString:@"Monday"]){
            dayOfWeek = @"Sunday";
        }else if ([dayOfWeek isEqualToString:@"Sunday"]){
            dayOfWeek = @"Saturday";
        }else if ([dayOfWeek isEqualToString:@"Saturday"]){
            dayOfWeek = @"Friday";
        }else if ([dayOfWeek isEqualToString:@"Friday"]){
            dayOfWeek = @"Thursday";
        }else if ([dayOfWeek isEqualToString:@"Thursday"]) {
            dayOfWeek = @"Wednesday";
        }else if ([dayOfWeek isEqualToString:@"Wednesday"]) {
            dayOfWeek = @"Tuesday";
        }else if ([dayOfWeek isEqualToString:@"Tuesday"]) {
            dayOfWeek = @"Monday";
        }
    }
    startingDay = dayOfWeek;
    
    //set first button for calendar
    int startingButtonNumber = 1;
    if ([startingDay isEqualToString:@"Monday"]) {
        NSLog(@"M");
        startingButtonNumber = 2;   //MONDAY
    }else if ([startingDay isEqualToString:@"Tuesday"]) {
        NSLog(@"T");
        startingButtonNumber = 3;   //TUESDAY
    }else if ([startingDay isEqualToString:@"Wednesday"]) {
        NSLog(@"W");
        startingButtonNumber = 4;   //WEDNESDAY
    }else if ([startingDay isEqualToString:@"Thursday"]) {
        NSLog(@"Th");
        startingButtonNumber = 5;   //THURSDAY
    }else if ([startingDay isEqualToString:@"Friday"]) {
        NSLog(@"F");
        startingButtonNumber = 6;   //FRIDAY
    }else if ([startingDay isEqualToString:@"Saturday"]) {
        NSLog(@"Sa");
        startingButtonNumber = 7;   //SATURDAY
    }else if ([startingDay isEqualToString:@"Sunday"]) {
        NSLog(@"Su");
        startingButtonNumber = 1;   //SUNDAY
    }
    
    //set max days for month
    [dateFormat setDateFormat:@"MM"];
    int maxDate;
    switch ([[dateFormat stringFromDate:date] intValue]-1) {
        case 3:
        case 5:
        case 8:
        case 10:
            maxDate = 30;
            break;
        case 1:
            maxDate = 28;
            break;
        default:
            maxDate = 31;
            break;
    }
    
    //setup calendar buttons. all calendar buttons have tag > 1
    int numberForButton = 1;
    int position = startingButtonNumber;
    for (NSObject *obj in self.view.subviews) {
        if ([obj isKindOfClass:[UIButton class]]) {
            UIButton *button = (UIButton *)obj;
            if (button.tag >= 1) {
                
                //set unused buttons to empty string
                if (button.tag < startingButtonNumber ||
                    button.tag > maxDate + startingButtonNumber-1) {
                    [button setTitle:@"" forState:UIControlStateNormal];
                    [button setHidden:YES];
                
                //set correct date to button label
                }else if (button.tag == position) {
                    [button setTitle:[NSString stringWithFormat:@"%d", numberForButton] forState:UIControlStateNormal];
                    position++;
                    numberForButton++;
                    [button setHidden:NO];
                }
            }
        }
    }
}

- (IBAction)confirmTeeTime:(UIButton *)sender
{
    if (teeTime.isReady){
        //continue or end
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Schedule" message:@"Schedule Tee Time?" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Yes", nil];
        [alert show];
    }
}


-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if ([[alertView buttonTitleAtIndex:buttonIndex] isEqualToString:@"Yes"]) {
        //submit
        [teeTime submit];
        
        //schedule reminder
        [self scheduleReminder];
        [successImage setHidden:NO];
    }
}

-(void)scheduleReminder
{
    //cancel any notifications already made
    [[UIApplication sharedApplication] cancelAllLocalNotifications];
    
    NSCalendar *calendar = [NSCalendar autoupdatingCurrentCalendar];
    NSDateComponents *dateComps = [[NSDateComponents alloc] init];
    
    NSLog(@"selected %d/%lu/%d %d:%d",selectedYear.intValue, (unsigned long)[months indexOfObject:selectedMonth],selectedDay.titleLabel.text.intValue,selectedHour.intValue,selectedMinute.intValue);
    //set date items
    [dateComps setDay:selectedDay.titleLabel.text.intValue];
    [dateComps setMonth:([months indexOfObject:selectedMonth]+1)];
    [dateComps setYear:selectedYear.intValue];
    [dateComps setHour:selectedHour.intValue];
    [dateComps setMinute:selectedMinute.intValue];
    NSDate *itemDate = [calendar dateFromComponents:dateComps];
    
    NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
    dateFormat.locale = [[NSLocale alloc] initWithLocaleIdentifier:@"en_US"];
    [dateFormat setDateFormat:@"YYYY/MM/dd HH:mm"];

    UILocalNotification *localNotif = [[UILocalNotification alloc] init];
    localNotif.fireDate = [itemDate dateByAddingTimeInterval:-(5*60)];  //alert 5 minutes before
    localNotif.timeZone = [NSTimeZone defaultTimeZone];
    localNotif.alertBody = [NSString stringWithFormat:@"Tee Time on %@ \n in 5 minutes", selectedCourse];
    localNotif.alertTitle = NSLocalizedString(@"Reminder", nil);
    localNotif.soundName = UILocalNotificationDefaultSoundName;
    localNotif.applicationIconBadgeNumber = 1;
    
    NSDictionary *infoDict = [NSDictionary dictionaryWithObjects:@[allGolfers, selectedCourse]  forKeys:@[@"golfers",@"course"]];
    localNotif.userInfo = infoDict;
    
    [[UIApplication sharedApplication] scheduleLocalNotification:localNotif];
    
    /*if ([localNotif.fireDate compare:[NSDate date]] == NSOrderedDescending) {
        localNotif.timeZone = [NSTimeZone defaultTimeZone];
        localNotif.alertBody = [NSString stringWithFormat:@"Tee Time on %@ \n in 5 minutes", selectedCourse];
        localNotif.alertTitle = NSLocalizedString(@"Reminder", nil);
        localNotif.soundName = UILocalNotificationDefaultSoundName;
        localNotif.applicationIconBadgeNumber = 1;
        
        NSDictionary *infoDict = [NSDictionary dictionaryWithObjects:@[allGolfers, selectedCourse]  forKeys:@[@"golfers",@"course"]];
        localNotif.userInfo = infoDict;
        
        [[UIApplication sharedApplication] scheduleLocalNotification:localNotif];
    }else{
        NSLog(@"wont fire");
    }*/
}

-(void)selectDayOfMonth:(UIButton *)sender
{
    if (![selectedDay isEqual:sender]) {
        
        if (selectedDay != nil) {
            [selectedDay setBackgroundColor:[UIColor colorWithRed:.92 green:.93 blue:.93 alpha:1]];
            [selectedDay setSelected:NO];
        }

        [sender setBackgroundColor:[UIColor blackColor]];
        selectedDay = sender;
        [sender setSelected:YES];
    }else{
        [sender setBackgroundColor:[UIColor colorWithRed:.92 green:.93 blue:.93 alpha:1]];
        selectedDay = nil;
        [sender setSelected:NO];
    }
    //NSLog(@"day: %@", sender.titleLabel.text);
}

#define MAX_CART_AMOUNT 4
- (IBAction)changeCartAmount:(UIButton *)sender {
    
    int cartAmount = sender.titleLabel.text.intValue;
    
    if (cartAmount < MAX_CART_AMOUNT) {
        cartAmount++;
    }else{
        cartAmount = 0;
    }
    
    teeTime.carts = cartAmount;
    
    [sender setTitle:[NSString stringWithFormat:@"%d",cartAmount] forState:UIControlStateNormal];
}

- (IBAction)setTime:(id)sender
{
    if (teeTime != nil){
        pickerView = [[UIPickerView alloc] initWithFrame:CGRectMake(self.view.frame.origin.x
                                                                    , self.view.frame.size.height, self.view.frame.size.width, self.view.frame.size.height/2)];
        [pickerView setBackgroundColor:[UIColor whiteColor]];
        [pickerView setDataSource:self];
        [pickerView setDelegate:self];
        UITapGestureRecognizer* gestureRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(pickerViewTapGestureRecognized:)];
        [gestureRecognizer setDelegate:self];
        [pickerView addGestureRecognizer:gestureRecognizer];

        [self.view addSubview:pickerView];
        
        [UIView animateWithDuration:1 animations:^{
            [pickerView setFrame:CGRectMake(self.view.frame.origin.x
                                           , self.view.frame.size.height - pickerView.frame.size.height, self.view.frame.size.width, self.view.frame.size.height/2)];
        }];
    }
}

-(NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

-(NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return availableTimes.count;
}


-(NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return [availableTimes objectAtIndex:row];
}

-(BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer{
    // return
    return true;
}

- (void)pickerViewTapGestureRecognized:(UITapGestureRecognizer*)gestureRecognizer
{
    CGPoint touchPoint = [gestureRecognizer locationInView:self.view];
    
    CGRect frame = pickerView.frame;
    CGRect selectorFrame = CGRectInset( frame, 0.0, pickerView.bounds.size.height * 0.85 / 2.0 );
    
    if( CGRectContainsPoint( selectorFrame, touchPoint) )
    {
        NSMutableString *time = (NSMutableString *)[self pickerView:pickerView titleForRow:[pickerView selectedRowInComponent:0] forComponent:0];
        
        [timeButton setTitle:time forState:UIControlStateNormal];
        
        NSRange timeRange = [time rangeOfString:@":"];
        timeRange.location += 1;
        timeRange.length = 2;
        selectedMinute = [time substringWithRange:timeRange];
        timeRange.location = 0;
        selectedHour = [time substringWithRange:timeRange];
        [teeTime setSelectedHour:selectedHour];
        [teeTime setSelectedMinute:selectedMinute];
        [teeTime setIsReady:YES];
        
        [UIView animateWithDuration:1 animations:^{
            [pickerView setFrame:CGRectMake(self.view.frame.origin.x
                                            , self.view.frame.size.height, self.view.frame.size.width, self.view.frame.size.height/2)];
        }completion:^(BOOL finished) {
            [pickerView removeFromSuperview];
            pickerView = nil;
        }];
    }
}

- (IBAction)displayCourses:(id)sender
{
    if (selectedDay.isSelected) {
        //create tableview
        coursesTableView = [[UITableView alloc] initWithFrame:self.view.frame style:UITableViewStyleGrouped];
        
        //display course table view controller
        coursesTVC = [[CoursesTVC alloc] initWithParent:self];
        [coursesTVC setTableView:coursesTableView];
        
        [self.view addSubview:coursesTableView];
        [self.view bringSubviewToFront:coursesTableView];
    }
}

-(void)setCourse:(NSString *)courseName
{
    //dismiss course table view controll
    [coursesTableView removeFromSuperview];
    coursesTVC = nil;
    coursesTableView = nil;
    
    //set selected course
    selectedCourse = courseName;
    [coursesButton setTitle:courseName forState:UIControlStateNormal];
    
    TeeTimeFactory *factory = [[TeeTimeFactory alloc] init];
    teeTime = [factory createTeeTime:selectedCourse];
    [teeTime setSelectedDay:selectedDay.titleLabel.text];
    [teeTime setSelectedMonth:selectedMonth];
    [teeTime setSelectedYear:selectedYear];
    //availableTimes = [teeTime getTimes];
}

- (NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

/*
- (IBAction)cycleHour:(UIButton *)sender {
    
    int hour = sender.titleLabel.text.intValue;
    
    if (hour < 12) {
        hour++;
    }else{
        hour = 1;
    }
    
    [sender setTitle:[NSString stringWithFormat:@"%d",hour] forState:UIControlStateNormal];

}

- (IBAction)cycleMinute:(UIButton *)sender {
    
    int minute = sender.titleLabel.text.intValue;
    
    if (minute < 55) {
        minute += 5;
    }else{
        minute = 0;
    }
    
    [sender setTitle:[NSString stringWithFormat:@"%d",minute] forState:UIControlStateNormal];
}

- (IBAction)togglePeriod:(UIButton *)sender {
    
    if ([sender.titleLabel.text isEqualToString:@"AM"]) {
        [sender setTitle:@"PM" forState:UIControlStateNormal];
    }else{
        [sender setTitle:@"AM" forState:UIControlStateNormal];
    }
    
}
*/
@end
