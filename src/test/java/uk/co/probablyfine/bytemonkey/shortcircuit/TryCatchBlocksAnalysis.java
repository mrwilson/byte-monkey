package uk.co.probablyfine.bytemonkey.shortcircuit;

import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TryCatchBlocksAnalysis {
    private static Logger logger = Logger.getLogger(TryCatchBlocksAnalysis.class);

    public static void main(String[] args) {
        staticAnalysis("example/src/main");
        tryCatchTestCoverageAnalysis("example");
     }

     public static void staticAnalysis(String srcPath) {
         Launcher spoon = new Launcher();

         spoon.addInputResource(srcPath);
         spoon.getEnvironment().setNoClasspath(true);
         spoon.buildModel();
         CtModel model = spoon.getModel();

         int classesNum = 0;
         int methodsNum = 0;
         int tryCatchNum = 0;

         for (CtType<?> s : model.getAllTypes()) {
             if (s.isClass()) {
                 classesNum ++;
//                logger.info("Analyze class: " + s.getSimpleName());
                 for (CtMethod<?> m : s.getMethods()) {
                     methodsNum ++;
                     int tryIndex = 0;
                     if (m.getBody() != null) {
                         List<CtTry> tryBlocks = m.getBody().getElements(new TypeFilter(CtTry.class));
                         if (!tryBlocks.isEmpty()) {
//                            logger.info("Analyze method : " + m.getSimpleName());
//                            logger.info("try-catch block number: " + tryBlocks.size());
                             for (CtTry tryBlock : tryBlocks) {
                                 int startPos = tryBlock.getBody().getPosition().getLine();
                                 for (CtCatch catchBlock : tryBlock.getCatchers()) {
                                     String exceptionType = catchBlock.getParameter().getType().getSimpleName();
                                     logger.info(String.format("try catch index %d @ %d (%s), %s @ %s", tryIndex, startPos, exceptionType, m.getSimpleName(), s.getSimpleName()));
                                     tryCatchNum++;
                                     tryIndex++;
                                 }
                             }
                         }
                     }
                 }
             }
         }

         logger.info("----Static Analysis Finished----");
         logger.info("Classes Number: " + classesNum);
         logger.info("Methods Number: " + methodsNum);
         logger.info("TryCatch Number: " + tryCatchNum);
     }

     public static void tryCatchTestCoverageAnalysis(String rootPath) {
         Process process = null;
         List<String> processList = new ArrayList<String>();
         Map<String, String> tcMap = new HashMap<String, String>();
         Map<String, List<String>> testCaseInfo = new HashMap<String, List<String>>();
         String testResult = null;
         int tryCatchExcutedTimes = 0;
         try {
             process = Runtime.getRuntime().exec("mvn test", null, new File(rootPath));
             BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
             String line = "";
             while ((line = input.readLine()) != null) {
                 if (line.startsWith("INFO ByteMonkey try catch index")) {
                     tcMap.put(line, line);
                     String testCaseLine = input.readLine();
                     processList.add(line);
                     processList.add(testCaseLine);
                     List<String> tryCatchLines = null;
                     if (!testCaseInfo.containsKey(testCaseLine)) {
                         tryCatchLines = new ArrayList<String>();
                         tryCatchLines.add(line);
                         testCaseInfo.put(testCaseLine, tryCatchLines);
                     } else {
                         tryCatchLines = testCaseInfo.get(testCaseLine);
                         if (!tryCatchLines.contains(line)) {
                             tryCatchLines.add(line);
                             testCaseInfo.put(testCaseLine, tryCatchLines);
                         }
                     }
                 } else if (line.startsWith("Tests run:") && !line.contains("Time elapsed")) {
                     testResult = line;
                 }
             }
             input.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
         tryCatchExcutedTimes = processList.size() / 2;

         testCaseInfo.forEach((caseInfo, tryCatchInfoList) -> {
             logger.info(tryCatchInfoList.size() + " : " + caseInfo);
             tryCatchInfoList.forEach((tcInfo -> logger.debug(tcInfo)));
             logger.debug("----");
         });

         logger.info("----Try-catch Coverage Analysis Finished----");
         logger.info(testResult);
         logger.info("try-catch executed times: " + tryCatchExcutedTimes);
         logger.info("number of the covered try-catch: " + tcMap.keySet().size());
         logger.info("number of relevant test cases: " + testCaseInfo.keySet().size());
         logger.info("covered try-catch info as follows:");
         tcMap.keySet().forEach(tc -> {logger.info(tc);});
     }
}