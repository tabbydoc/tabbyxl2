/*
 * Copyright 2018-19 Alexey O. Shigarov (shigarov@icc.ru) and Vasiliy V. Khristyuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.icc.td.tabbyxl.crl2j;

import com.squareup.javapoet.JavaFile;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.lang.StringUtils;

import ru.icc.td.tabbyxl.crl2j.compiler.CharSequenceCompiler;
import ru.icc.td.tabbyxl.crl2j.compiler.CharSequenceCompilerException;
import ru.icc.td.tabbyxl.crl2j.parsing.CRLLexer;
import ru.icc.td.tabbyxl.crl2j.parsing.CRLParser;
import ru.icc.td.tabbyxl.crl2j.parsing.TreeUtils;
import ru.icc.td.tabbyxl.model.CTable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public final class CRL2JEngine {

    private static final String filledLine = StringUtils.repeat("=", 100);
    private static final CharSequenceCompiler compiler;
    public static final String PACKAGE_NAME_BY_DEFAULT = "generated";

    static {
        compiler = new CharSequenceCompiler(ClassLoader.getSystemClassLoader(), null);
    }

    private List<JavaFile> javaFiles = new ArrayList<>();
    private SortedSet<TableConsumer> tableConsumers = new TreeSet<>();

    private String packageName;

    public CRL2JEngine() {
        this(PACKAGE_NAME_BY_DEFAULT);
    }

    public CRL2JEngine(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    private Tree parse(File crlFile) throws IOException, RecognitionException {

        ANTLRFileStream fileStream = new ANTLRFileStream(crlFile.getPath());

        // Tokenize CRL

        CRLLexer lexer = new CRLLexer(fileStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);

        // Parse CRL

        CRLParser parser = new CRLParser(tokenStream);

        return (Tree) parser.crl().getTree();
    }

    private void generateJavaFiles(Tree ast) {

        // Analyze and interpret AST to create a Ruleset model

        Ruleset ruleset = new Ruleset(ast);

        // Generate Java source code from the Ruleset model

        CodeGenerator codeGenerator = new CodeGenerator(getPackageName(), ruleset);
        javaFiles.addAll(codeGenerator.generateJavaFiles());
    }

    private void createTableConsumers(List<JavaFile> javaFiles)
            throws CharSequenceCompilerException, IllegalAccessException, InstantiationException {

        for (JavaFile javaFile : javaFiles) {
            String className = javaFile.packageName + '.' + javaFile.typeSpec.name;
            Class<TableConsumer>[] prototype = new Class[]{TableConsumer.class};
            String sourceCode = javaFile.toString();
            Class<TableConsumer> clazz = compiler.compile(className, sourceCode, null, prototype);

            TableConsumer instance = clazz.newInstance();
            tableConsumers.add(instance);
        }

    }

    public void loadRules(File crlFile) throws IOException, RecognitionException {

        // Parse the CRL ruleset to AST

        Tree ast = parse(crlFile);

        if (false) {
            System.out.println("This AST was produced from the ruleset");
            System.out.println(TreeUtils.trace(ast));
            System.out.println(filledLine);
            System.out.println();
        }

        // Interpret AST to create Java files

        generateJavaFiles(ast);

        if (true) {
            System.out.println("This Java source code was generated from the ruleset");
            System.out.println(filledLine);
            System.out.println();

            for (JavaFile javaFile : javaFiles) {
                System.out.println(javaFile.toString());
                System.out.println(filledLine);
                System.out.println();
            }
        }

        // Compile Java files to Java classes and create their instances

        try {
            createTableConsumers(javaFiles);
        } catch (CharSequenceCompilerException e) {
            System.err.println("The generated java files could not be compiled");
            e.printStackTrace();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    public List<JavaFile> getJavaFiles() {
        return javaFiles;
    }

    public void processTable(CTable table) {
        tableConsumers.forEach(tc -> tc.accept(table));
    }
}
