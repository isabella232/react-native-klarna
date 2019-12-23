#import "RNKlarna.h"
#import "RNKlarnaView.h"

@implementation RNKlarna

RCT_EXPORT_MODULE()

RCT_EXPORT_VIEW_PROPERTY(onComplete, RCTBubblingEventBlock);
RCT_EXPORT_VIEW_PROPERTY(snippet, NSString *);

- (UIView *)view {
    return [[RNKlarnaView alloc] initWithFrame:CGRectZero];
}

@end
