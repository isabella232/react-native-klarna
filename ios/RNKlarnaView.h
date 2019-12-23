#import <UIKit/UIKit.h>
#import <React/RCTComponent.h>

@interface RNKlarnaView: UIView

@property (nonatomic, copy) RCTBubblingEventBlock onComplete;
@property (nonatomic, copy) NSString *snippet;

@end
