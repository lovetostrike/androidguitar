/*
 *  Copyright (c) 2009-@year@. The  GUITAR group  at the University of
 *  Maryland. Names of owners of this group may be obtained by sending
 *  an e-mail to atif@cs.umd.edu
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files
 *  (the "Software"), to deal in the Software without restriction,
 *  including without limitation  the rights to use, copy, modify, merge,
 *  publish,  distribute, sublicense, and/or sell copies of the Software,
 *  and to  permit persons  to whom  the Software  is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY,  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO  EVENT SHALL THE  AUTHORS OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR  OTHER LIABILITY,  WHETHER IN AN  ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package edu.umd.cs.guitar.graph.converter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.model.data.GUIStructure;
import edu.umd.cs.guitar.model.data.RowType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.model.wrapper.GUIStructureWrapper;
import edu.umd.cs.guitar.model.wrapper.GUITypeWrapper;

/**
 * @author <a href="mailto:baonn@cs.umd.edu"> Bao N. Nguyen </a>
 */
public class EFG2DL 
{
   // Static data
   private static final int MAX_WINDOW_TITLE_LENGTH = 10;
   private static final String SPLITER = " ";

   // Statistics
   private static int numNodes = 0;
   private static int numEdges = 0;

   /**
    * @param args
    */
   public static void main(String[] args) {
      if (args.length < 2) {
         System.out.println("Usage:" + EFG2DL.class.getName()
               + "<efg file> <dl file> [gui file]  ");
         System.exit(1);
      }

      String guiFile;
      String efgFile;
      String dlFile;

      efgFile = args[0];
      dlFile = args[1];

      if (args.length > 2) {
         guiFile = args[2];
         convert(guiFile, efgFile, dlFile);

      } else {
         convert(null, efgFile, dlFile);
      }

      System.out.println("Nodes: " + numNodes);
      System.out.println("Edges: " + numEdges);
   }

   public static void convert(String guiFile, String efgFile, String dlFile) {
      EFG efg = (EFG) IO.readObjFromFile(efgFile, EFG.class);
      StringBuffer result; 
      if (guiFile != null) {
         GUIStructure gui = (GUIStructure) IO.readObjFromFile(guiFile,
               GUIStructure.class);
         result = toCVS(gui, efg);
      }else {
         result = toCVS(efg);
      }

      try {
         // Create file
         FileWriter fstream = new FileWriter(dlFile);
         BufferedWriter out = new BufferedWriter(fstream);

         out.write(result.toString());

         // Close the output stream
         out.close();
      } catch (Exception e) {
         // Catch exception if any
         System.err.println("Error: " + e.getMessage());
      }
   }

   public static StringBuffer 
   toCVS(EFG efg) 
   {
      StringBuffer result = new StringBuffer();

      List<EventType> lEvents = efg.getEvents().getEvent();
      String header = "DL N=" + lEvents.size();
      header += ("\n");
      header += "format = fullmatrix";
      header += ("\n");
      result.append(header);

      // Set up node
      String label = "labels:\n";

      for (EventType event : efg.getEvents().getEvent()) {
         String eventID = event.getEventId();
         label += (eventID+",");
      }
      if (label.charAt(label.length() - 1) == ',') {
         label = label.substring(0, label.length() - 1);
      }
      result.append(label);
      result.append("\n");

      // Matrix
      result.append("Data:\n");
      List<RowType> lRows = efg.getEventGraph().getRow();

      for (int row = 0; row < lRows.size(); row++) {
         List<Integer> lE = lRows.get(row).getE();
         String line = "";
         // line += event.getEventId();

         for (int col = 0; col < lE.size(); col++) {
            line = line + SPLITER + lE.get(col);
            if (lE.get(col) > 0) {
               numEdges++;
            }
         }
         numNodes++;
         result.append(line);
         result.append("\n");
      }

      return result;
   }

   public static StringBuffer 
   toCVS(GUIStructure gui, EFG efg) 
   {
      GUIStructureWrapper wGUI = new GUIStructureWrapper(gui);
      wGUI.parseData();

      StringBuffer result = new StringBuffer();

      List<EventType> lEvents = efg.getEvents().getEvent();
      String header = "DL N=" + lEvents.size();
      header += ("\n");
      header += "format = fullmatrix";
      header += ("\n");
      result.append(header);

      // Set up node
      String label = "labels:\n";

      for (EventType event : efg.getEvents().getEvent()) {
         String widgetID = event.getWidgetId();

         ComponentTypeWrapper component = wGUI.getComponentFromID(widgetID);

         String widgetTitle = " ";
         String windowTitle = "";
         if (component != null) {
            widgetTitle = component
                  .getFirstValueByName(GUITARConstants.TITLE_TAG_NAME);
            GUITypeWrapper window = component.getWindow();
            if (window != null) {
               windowTitle = component.getWindow().getTitle();
            }

         }

         if (windowTitle.length() > MAX_WINDOW_TITLE_LENGTH) {
            windowTitle = windowTitle.substring(0, MAX_WINDOW_TITLE_LENGTH)
                  + "...";
         }

         widgetTitle = widgetTitle.replaceFirst(",", ":");
         windowTitle = windowTitle.replaceFirst(",", ":");

         // Set up label
         label += (windowTitle + " - " + widgetTitle + ",");
      }
      if (label.charAt(label.length() - 1) == ',') {
         label = label.substring(0, label.length() - 1);
      }
      result.append(label);
      result.append("\n");

      // Matrix
      result.append("Data:\n");
      List<RowType> lRows = efg.getEventGraph().getRow();

      for (int row = 0; row < lRows.size(); row++) {
         List<Integer> lE = lRows.get(row).getE();
         String line = "";

         for (int col = 0; col < lE.size(); col++) {
            line = line + SPLITER + lE.get(col);
            if (lE.get(col) > 0) {
               numEdges++;
            }
         }
         numNodes++;
         result.append(line);
         result.append("\n");
      }

      return result;
   }
}
