package uk.co.probablyfine.bytemonkey.shortcircuit;

import org.apache.log4j.Logger;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

public class TryCatchBlocksAnalysis {
    private static Logger logger = Logger.getLogger(TryCatchBlocksAnalysis.class);

    public static void main(String[] args) {
        Launcher spoon = new Launcher();

        spoon.addInputResource("example/src/main");
        spoon.getEnvironment().setNoClasspath(true);
        spoon.buildModel();
        CtModel model = spoon.getModel();

        int classesNum = 0;
        int methodsNum = 0;
        int tryCatchNum = 0;

        for (CtType<?> s : model.getAllTypes()) {
            if (s.isClass()) {
                classesNum ++;
                logger.info("Analyze class: " + s.getSimpleName());
                for (CtMethod<?> m : s.getMethods()) {
                    methodsNum ++;
                    if (m.getBody() != null) {
                        List<CtTry> tryBlock = m.getBody().getElements(new TypeFilter(CtTry.class));
                        if (!tryBlock.isEmpty()) {
                            tryCatchNum ++;
                            logger.info("Analyze method : " + m.getSimpleName());
                            logger.info("try-catch block number: " + tryBlock.size());
                        }
                    }
                }
            }
        }

        logger.info("----Analysis finished----");
        logger.info("Classes Number: " + classesNum);
        logger.info("Methods Number: " + methodsNum);
        logger.info("TryCatch Number: " + tryCatchNum);
     }
}