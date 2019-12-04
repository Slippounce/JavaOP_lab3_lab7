

import javassist.*;
import nsu.fit.javaperf.TransactionProcessor;

import java.io.IOException;

public class Lab7 {
    public static void main(String[] args) {
        try {
            TransactionProcessor.main(null);

            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get("nsu.fit.javaperf.TransactionProcessor");
            assert ctClass != null;      //???
            addTiming(ctClass);
            ctClass.writeFile();
            TransactionProcessor.main(null);
        } catch (NotFoundException | CannotCompileException | IOException e) {
            e.printStackTrace( );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTiming(CtClass ctClass) throws NotFoundException, CannotCompileException {
            String mname = "processTransaction";
            String nName = mname + "$impl";
            CtMethod mold = ctClass.getDeclaredMethod(mname);
            mold.setName(nName);
            CtMethod mnew = CtNewMethod.copy(mold, mname, ctClass, null);
            String type = mold.getReturnType().getName();
            StringBuffer body = new StringBuffer();
            body.append("{\nlong start = System.currentTimeMillis();\n");
            if (!"void".equals(type)) {
                body.append(type + " result = ");
            }
            body.append(nName + "($$);\n");

            body.append("System.out.println(\"Call to method " + mname +
                    " took \" +\n (System.currentTimeMillis()-start) + " +
                    "\" ms.\");\n");
            if (!"void".equals(type)) {
                body.append("return result;\n");
            }
            body.append("}");

            mnew.setBody(body.toString());
            ctClass.addMethod(mnew);

            //  print the generated code block just to show what was done
            System.out.println("Interceptor method body:");
            System.out.println(body.toString());
    }
}
