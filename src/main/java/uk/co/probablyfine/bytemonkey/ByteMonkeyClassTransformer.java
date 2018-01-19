package uk.co.probablyfine.bytemonkey;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class ByteMonkeyClassTransformer implements ClassFileTransformer {

    private final AddChanceOfFailure addChanceOfFailure = new AddChanceOfFailure();

    private final OperationMode failureMode;
    private final AgentArguments arguments;
    private final FilterByClassAndMethodName filter;

    public ByteMonkeyClassTransformer(String args) {
        Map<String, String> configuration = argumentMap(args == null ? "" : args);

        long latency = Long.valueOf(configuration.getOrDefault("latency","100"));
        double activationRatio = Double.valueOf(configuration.getOrDefault("rate","1"));

        this.arguments = new AgentArguments(latency, activationRatio);
        this.failureMode = OperationMode.fromLowerCase(configuration.getOrDefault("mode", OperationMode.FAULT.name()));
        this.filter = new FilterByClassAndMethodName(configuration.getOrDefault("filter", ".*"));
    }

    private Map<String, String> argumentMap(String args) {
        return Arrays
            .stream(args.split(","))
            .map(line -> line.split(":"))
            .filter(line -> line.length == 2)
            .collect(Collectors.toMap(
                keyValue -> keyValue[0],
                keyValue -> keyValue[1])
            );
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classFileBuffer
    ) throws IllegalClassFormatException {
        return meddle(classFileBuffer);
    }

    private byte[] meddle(byte[] classFileBuffer) {
        ClassNode cn = new ClassNode();
        new ClassReader(classFileBuffer).accept(cn, 0);

        if (cn.name.startsWith("java/") || cn.name.startsWith("sun/") || cn.name.contains("$")) return classFileBuffer;

        switch (failureMode) {
	        case SCIRCUIT:
            case ANALYZETC:
	            cn.methods.stream()
	        	.filter(method -> !method.name.startsWith("<"))
	        	.filter(method -> filter.matches(cn.name, method.name))
	        	.filter(method -> method.tryCatchBlocks.size() > 0)
	        	.forEach(method -> {
	        		// inject an exception in each try-catch block
	        		// take the first exception type in catch block
	        		// TODO: for 1 try -> n catch, we should do n injections
	        		// TODO: these codes really need to be beautified
	        		LabelNode ln = method.tryCatchBlocks.get(0).start;
	        		int i = 0;
	        		for (TryCatchBlockNode tc : method.tryCatchBlocks) {
	        			if (ln == tc.start && i > 0) {
	        				// if two try-catch-block-nodes have the same "start", it indicates that it's one try block with multiple catch
	        				// so we should only inject one exception each time
	        				continue;
	        			}
		        		InsnList newInstructions = failureMode.generateByteCode(tc, arguments);
		        		method.maxStack += newInstructions.size();
		        		method.instructions.insert(tc.start, newInstructions);
		        		ln = tc.start;
		        		i++;
	        		}
// some beautiful ones for reference
//	        		method.tryCatchBlocks.forEach(tryCatchNode -> {
//	        			System.out.println(tryCatchNode.type);
//		        		InsnList newInstructions = failureMode.generateByteCode(tryCatchNode, arguments);
//		        		method.maxStack += newInstructions.size();
//		        		method.instructions.insert(tryCatchNode.start, newInstructions);
//	        		});
	        	});
	        	break;
	        default:
	          cn.methods.stream()
	            .filter(method -> !method.name.startsWith("<"))
	            .filter(method -> filter.matches(cn.name, method.name))
	            .forEach(method -> {
	                createNewInstructions(method).ifPresent(newInstructions -> {
	                    method.maxStack += newInstructions.size();
	                    method.instructions.insertBefore(
	                        method.instructions.getFirst(),
	                        newInstructions
	                    );
	                });
	            });
	        	break;
        }

        final ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }

    private Optional<InsnList> createNewInstructions(MethodNode method) {
        InsnList newInstructions = failureMode.generateByteCode(method, arguments);

        return ofNullable(
            addChanceOfFailure.apply(newInstructions, arguments.chanceOfFailure())
        );
    }

}