#import "RNKlarnaView.h"
#import <KlarnaCheckoutSDK/KlarnaCheckout.h>

@interface RNKlarnaView ()

@property (nonatomic, retain) KCOKlarnaCheckout *checkout;
@property (nonatomic, readonly) NSURL *returnURL;
@property (nonatomic, readonly) UIViewController *parentViewController;

@end

@implementation RNKlarnaView

- (NSURL *)returnURL {
    NSDictionary* infoDict = [[NSBundle mainBundle] infoDictionary];
    NSString* returnString = [infoDict objectForKey:@"ReturnURLKlarna"];
    return [NSURL URLWithString:returnString];
}

- (UIViewController *)parentViewController {
    UIResponder *responder = self;
    while (responder && ![responder isKindOfClass:[UIViewController class]]) {
        responder = [responder nextResponder];
    }
    return (UIViewController *)responder;
}

- (void)setSnippet:(NSString *)snippet {
    _snippet = [snippet copy];
    [self updateSnippet];
}

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleNotification:)
                                                     name:KCOSignalNotification
                                                   object:nil];
    }
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    UIViewController *parentViewController = self.parentViewController;
    if (self.parentViewController && !self.checkout) {
        KCOKlarnaCheckout *checkout = [[KCOKlarnaCheckout alloc] initWithViewController:self.parentViewController
                                                                              returnURL:self.returnURL];
        [checkout setSnippet:self.snippet];
        UIViewController<KCOCheckoutViewControllerProtocol> *checkoutViewController = checkout.checkoutViewController;
        [self.parentViewController addChildViewController:checkoutViewController];
        checkoutViewController.view.frame = self.bounds;
        checkoutViewController.view.translatesAutoresizingMaskIntoConstraints = true;
        checkoutViewController.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        [self addSubview:checkoutViewController.view];
        [checkoutViewController didMoveToParentViewController:parentViewController];
        self.checkout = checkout;
    }
}

- (void)willMoveToSuperview:(UIView *)newSuperview {
    if (newSuperview != nil)
        return;
    [self.checkout.checkoutViewController willMoveToParentViewController:nil];
    [self.checkout.checkoutViewController.view removeFromSuperview];
    [self.checkout.checkoutViewController removeFromParentViewController];
    [self.checkout destroy];
    self.checkout = nil;
}

- (void)updateSnippet {
    if ([self.snippet isEqualToString:@"error"]) {
        [self.checkout setSnippet:@""];
        [self.checkout.checkoutViewController dismissViewControllerAnimated:YES completion:nil];
    } else {
        [self.checkout setSnippet:self.snippet];
    }
}

- (void)onCheckoutComplete:(NSDictionary *)event {
    if(_onComplete) {
        _onComplete(event);
    }
}

- (void)handleNotification:(NSNotification *)notification {
    NSString *name = notification.userInfo[KCOSignalNameKey];
    NSDictionary *data = notification.userInfo[KCOSignalDataKey];
    NSDictionary *completeEvent = @{@"type": @"onComplete",
                                    @"data": data,
                                    @"signalType": name};
    [self onCheckoutComplete: completeEvent];
}

@end
