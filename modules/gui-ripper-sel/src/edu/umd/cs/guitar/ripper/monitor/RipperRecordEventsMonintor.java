/*
 * Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland.
 * Names of owners of this group may be obtained by sending an e-mail to
 * atif@cs.umd.edu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.umd.cs.guitar.ripper.monitor;

import org.apache.log4j.Logger;

import edu.umd.cs.guitar.ripper.GRipperMonitor;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;

import edu.umd.cs.guitar.ripper.Ripper;

import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Provide a delay after each ripping step
 *
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao Nguyen </a>
 *
 */
public class RipperRecordEventsMonintor extends GRipperStepMonitor {

  Logger LOGGER = Logger.getLogger(RipperRecordEventsMonintor.class);

  /**
   * Delay after each step
   */
  @Override
  public void afterRippingComponent(GComponent component, GWindow window) {


    GRipperMonitor ripperMonitor = this.ripper.getMonitor();
    if (ripperMonitor.isExpandable(component, window)) {
      LOGGER.debug("*** Start recording state ***");
    }

  }

}
