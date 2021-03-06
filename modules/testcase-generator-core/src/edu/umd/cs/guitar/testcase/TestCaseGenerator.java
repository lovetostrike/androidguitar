
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

package edu.umd.cs.guitar.testcase;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import edu.umd.cs.guitar.model.IO;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.testcase.plugin.GTestCaseGeneratorPlugin;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Main class of the Test Case Generator. Run the specified converter, with the
 * specifier parameters
 * 
 * @author <a href="mailto:charlie.biger@gmail.com"> Charlie BIGER </a>
 */
public class TestCaseGenerator
{

   public static void
   main(String[] args)
   {
      setupLog();

      /*
       * Manually search for the plugin argument, and let it parse the
       * arguments.
       */
      String pluginName = "";

      for (int i = 0; i < args.length; i++) {
         if (args[i].equals("-p") || args[i].equals("--plugin")) {
            if (i + 1 < args.length) {
               pluginName = args[i + 1];
               break;
            } else {
               // Testcase generator plugin name not specified
               System.out.println("Missing plugin argument -p");
               System.exit(0);
            }
         }
      }

      if (pluginName.equals("")) {
         System.out.println("Missing plugin argument -p");
         System.exit(0);
      }

      CmdLineParser parser =
         new CmdLineParser(new TestCaseGeneratorConfiguration());

      try {
         /*
          * By default the plugin is put in the same package
          * with GTestCaseGeneratorPlugin
          */
         if (!pluginName.contains(
                 GTestCaseGeneratorPlugin.class.getPackage().getName()))
         {
            pluginName = GTestCaseGeneratorPlugin.class.getPackage().getName()
                         + "." + pluginName;
         }

         Class<?> converterClass = Class.forName(pluginName);
         GTestCaseGeneratorPlugin generator =
            (GTestCaseGeneratorPlugin) converterClass.newInstance();

         TestCaseGeneratorConfiguration configuration =
            generator.getConfiguration();

         if (configuration == null) {
            System.exit(0);
         }

         parser = new CmdLineParser(configuration);
         parser.parseArgument(args);

         if (!configuration.isValid() ||
              (TestCaseGeneratorConfiguration.HELP)) {
            throw new CmdLineException("");
         }

         if (!generator.isValidArgs()) {
            throw new CmdLineException("Invalid plugin arguments");
         }

         GUITARLog.log.info("Plugin: " +
                            TestCaseGeneratorConfiguration.PLUGIN);

         // Reading EFG
         GUITARLog.log.info("Reading EFG");
         EFG efg = (EFG)
            IO.readObjFromFile(TestCaseGeneratorConfiguration.EFG_FILE,
                               EFG.class);

         // Reading ADDITIONAL_GRAPH (if specified)
         if (!TestCaseGeneratorConfiguration.ADDITIONAL_GRAPH_FILE.equals("")) {
            GUITARLog.log.info("Reading additional graph file");
            EFG efgx = (EFG)
               IO.readObjFromFile(
               TestCaseGeneratorConfiguration.ADDITIONAL_GRAPH_FILE, EFG.class);
            generator.efgx = efgx;
         }

         generator.generate(efg, TestCaseGeneratorConfiguration.OUTPUT_DIR,
               TestCaseGeneratorConfiguration.MAX_NUMBER,
               TestCaseGeneratorConfiguration.NO_DUPLICATE_EVENT,
               TestCaseGeneratorConfiguration.TREAT_TERMINAL_EVENT_SPECIALLY);
        }

        catch (CmdLineException e) {
            System.out.println("\n" + e.getMessage() + "\n\n" +
                            "Usage: java [JVM options] "
                            + TestCaseGenerator.class.getName()
                            + " [TC generator options] \n\n" +
            "where [TC generator options] is:");

            parser.printUsage(System.err);
            System.exit(0);
        }
        catch (ClassNotFoundException e)
        {
            GUITARLog.log.error("Plugin cannot be found. Please make sure "
                            + "that the plugin name is correct and the "
                            + "corresponding .jar file can be reached.");

            e.printStackTrace();
        }
        catch (InstantiationException e)
        {
            GUITARLog.log.error("Plugin is defined as an Abstract class, "
                            + "or an interface, or its constructor is not "
                            + "accessible without parameters.");

            GUITARLog.log.error("Please Report this bug");
        }
        catch (IllegalAccessException e)
        {
            GUITARLog.log.error("Plugin is not accessible");
        }

        printInfo();
    }

    /**
     * 
     */
    private static void
    printInfo()
    {
        GUITARLog.log.info("================================");
        GUITARLog.log.info("EFG File: " + TestCaseGeneratorConfiguration.EFG_FILE);
        GUITARLog.log.info("Plugin: " + TestCaseGeneratorConfiguration.PLUGIN);
        GUITARLog.log.info("Test cases #: " + TestCaseGeneratorConfiguration.MAX_NUMBER);

        GUITARLog.log.info("Output dir: " + TestCaseGeneratorConfiguration.OUTPUT_DIR);
        GUITARLog.log.info("================================");
    }

    /**
     * 
     */
    private static void
    setupLog()
    {
        System.setProperty(GUITARLog.LOGFILE_NAME_SYSTEM_PROPERTY, TestCaseGenerator.class.getSimpleName() + ".log");

        // try {
        // GUITARLog.log = Logger.getLogger(TestCaseGenerator.class
        // .getSimpleName());
        //
        // final File logFile = new File("TestCaseGenerator.log");
        // final String LOG_PATTERN = "%m%n";
        // final PatternLayout pl = new PatternLayout(LOG_PATTERN);
        //
        // final FileAppender rfp = new RollingFileAppender(pl, logFile
        // .getCanonicalPath(), true);
        //
        // final ConsoleAppender cp = new ConsoleAppender(pl);
        //
        // GUITARLog.log.addAppender(rfp);
        // GUITARLog.log.addAppender(cp);
        //
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }
}
