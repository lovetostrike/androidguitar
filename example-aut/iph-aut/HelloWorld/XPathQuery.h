//
//  XPathQuery.h
//  FuelFinder
//
//  Created by Matt Gallagher on 4/08/08.
//  Copyright 2008 Matt Gallagher. All rights reserved.
//
//  Permission is given to use this source code file, free of charge, in any
//  project, commercial or otherwise, entirely at your risk, with the condition
//  that any redistribution (in part or whole) of source code must retain
//  this copyright and permission notice. Attribution in compiled projects is
//  appreciated but not required.
//
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NSArray *PerformHTMLXPathQuery(NSData *document, NSString *query);
NSArray *PerformHTMLXPathQueryWithEncoding(NSData *document, NSString *query,NSString *encoding);
NSArray *PerformXMLXPathQuery(NSData *document, NSString *query);
NSArray *PerformXMLXPathQueryWithEncoding(NSData *document, NSString *query,NSString *encoding);