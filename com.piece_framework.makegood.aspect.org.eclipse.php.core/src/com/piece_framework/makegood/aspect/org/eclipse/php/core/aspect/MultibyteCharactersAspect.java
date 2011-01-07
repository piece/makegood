/**
 * Copyright (c) 2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.org.eclipse.php.core.aspect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import com.piece_framework.makegood.aspect.Aspect;

/**
 * @since 1.2.0
 */
public class MultibyteCharactersAspect extends Aspect {
    private static final String JOINPOINT_PHPASTLEXER_ACCESS_CMAP_PHP53 = "PhpAstLexer#next_token [access CMAP php53]"; //$NON-NLS-1$
    private static final String JOINPOINT_PHPASTLEXER_ACCESS_CMAP_PHP5 = "PhpAstLexer#next_token [access CMAP php5]"; //$NON-NLS-1$
    private static final String JOINPOINT_PHPASTLEXER_ACCESS_CMAP_PHP4 = "PhpAstLexer#next_token [access CMAP php4]"; //$NON-NLS-1$
    private static final String JOINPOINT_PHPLEXER_ACCESS_CMAP_PHP53 = "PhpLexer#yylex [access CMAP php53]"; //$NON-NLS-1$
    private static final String JOINPOINT_PHPLEXER_ACCESS_CMAP_PHP5 = "PhpLexer#yylex [access CMAP php5]"; //$NON-NLS-1$
    private static final String JOINPOINT_PHPLEXER_ACCESS_CMAP_PHP4 = "PhpLexer#yylex [access CMAP php4]"; //$NON-NLS-1$
    private static final String[] JOINPOINTS = {
        JOINPOINT_PHPASTLEXER_ACCESS_CMAP_PHP53,
        JOINPOINT_PHPASTLEXER_ACCESS_CMAP_PHP5,
        JOINPOINT_PHPASTLEXER_ACCESS_CMAP_PHP4,
        JOINPOINT_PHPLEXER_ACCESS_CMAP_PHP53,
        JOINPOINT_PHPLEXER_ACCESS_CMAP_PHP5,
        JOINPOINT_PHPLEXER_ACCESS_CMAP_PHP4
    };
    private static final String WEAVINGCLASS_PHPASTLEXER_PHP53 =
        "org.eclipse.php.internal.core.ast.scanner.php53.PhpAstLexer"; //$NON-NLS-1$
    private static final String WEAVINGCLASS_PHPASTLEXER_PHP5 =
        "org.eclipse.php.internal.core.ast.scanner.php5.PhpAstLexer"; //$NON-NLS-1$
    private static final String WEAVINGCLASS_PHPASTLEXER_PHP4 =
        "org.eclipse.php.internal.core.ast.scanner.php4.PhpAstLexer"; //$NON-NLS-1$
    private static final String WEAVINGCLASS_PHPLEXER_PHP53 =
        "org.eclipse.php.internal.core.documentModel.parser.php53.PhpLexer"; //$NON-NLS-1$
    private static final String WEAVINGCLASS_PHPLEXER_PHP5 =
        "org.eclipse.php.internal.core.documentModel.parser.php5.PhpLexer"; //$NON-NLS-1$
    private static final String WEAVINGCLASS_PHPLEXER_PHP4 =
        "org.eclipse.php.internal.core.documentModel.parser.php4.PhpLexer"; //$NON-NLS-1$
    private static final String[] WEAVINGCLASSES = {
        WEAVINGCLASS_PHPASTLEXER_PHP53,
        WEAVINGCLASS_PHPASTLEXER_PHP5,
        WEAVINGCLASS_PHPASTLEXER_PHP4,
        WEAVINGCLASS_PHPLEXER_PHP53,
        WEAVINGCLASS_PHPLEXER_PHP5,
        WEAVINGCLASS_PHPLEXER_PHP4
    };
    private static final String PHPASTLEXER_FIELD_CMAP_PACKED_PHP53 =
"private static final String ZZ_CMAP_PACKED_MULTIBYTECHARACTERS = \"\\\11\\\0\\\1\\\12\\\1\\\11\\\2\\\0\\\1\\\13\\\22\\\0\\\1\\\12\\\1\\\57\\\1\\\15\\\1\\\100\\\1\\\14\\\1\\\63\\\1\\\64\\\1\\\77\\\1\\\51\\\1\\\52\\\1\\\61\\\1\\\4\\\1\\\67\\\1\\\45\\\1\\\2\\\1\\\62\\\1\\\5\\\11\\\1\\\1\\\47\\\1\\\21\\\1\\\60\\\1\\\56\\\1\\\46\\\1\\\73\\\1\\\74\\\1\\\35\\\1\\\7\\\1\\\30\\\1\\\24\\\1\\\3\\\1\\\25\\\1\\\42\\\1\\\36\\\1\\\22\\\1\\\53\\\1\\\41\\\1\\\40\\\1\\\44\\\1\\\27\\\1\\\31\\\1\\\43\\\1\\\55\\\1\\\33\\\1\\\32\\\1\\\23\\\1\\\26\\\1\\\50\\\1\\\37\\\1\\\6\\\1\\\34\\\1\\\10\\\1\\\70\\\1\\\16\\\1\\\71\\\1\\\66\\\1\\\54\\\1\\\17\\\1\\\35\\\1\\\7\\\1\\\30\\\1\\\24\\\1\\\3\\\1\\\25\\\1\\\42\\\1\\\36\\\1\\\22\\\1\\\53\\\1\\\41\\\1\\\40\\\1\\\44\\\1\\\27\\\1\\\31\\\1\\\43\\\1\\\55\\\1\\\33\\\1\\\76\\\1\\\23\\\1\\\26\\\1\\\50\\\1\\\37\\\1\\\6\\\1\\\34\\\1\\\10\\\1\\\20\\\1\\\65\\\1\\\75\\\1\\\72\\\uff81\\\10\";"; //$NON-NLS-1$
    private static final String PHPASTLEXER_METHOD_UNPACKCMAP_PHP53 = generateZZUnpackCMapMethod(188);
    private static final String PHPASTLEXER_FIELD_CMAP_PHP53 =
"private static final char [] ZZ_CMAP_MULTIBYTECHARACTERS = zzUnpackCMapMultibyteCharacters(ZZ_CMAP_PACKED_MULTIBYTECHARACTERS);"; //$NON-NLS-1$
    private static final String PHPLEXER_FIELD_CMAP_PACKED_PHP53 =
"private static final String ZZ_CMAP_PACKED_MULTIBYTECHARACTERS = \"\\\11\\\0\\\1\\\12\\\1\\\11\\\2\\\0\\\1\\\15\\\22\\\0\\\1\\\12\\\1\\\26\\\1\\\17\\\1\\\74\\\1\\\16\\\1\\\32\\\1\\\33\\\1\\\73\\\1\\\63\\\1\\\64\\\1\\\30\\\1\\\25\\\1\\\13\\\1\\\4\\\1\\\2\\\1\\\31\\\1\\\5\\\11\\\1\\\1\\\61\\\1\\\14\\\1\\\27\\\1\\\23\\\1\\\24\\\1\\\75\\\1\\\76\\\1\\\40\\\1\\\7\\\1\\\47\\\1\\\42\\\1\\\3\\\1\\\45\\\1\\\56\\\1\\\52\\\1\\\43\\\1\\\65\\\1\\\55\\\1\\\54\\\1\\\60\\\1\\\41\\\1\\\36\\\1\\\57\\\1\\\67\\\1\\\37\\\1\\\50\\\1\\\44\\\1\\\46\\\1\\\62\\\1\\\53\\\1\\\6\\\1\\\51\\\1\\\10\\\1\\\71\\\1\\\20\\\1\\\72\\\1\\\35\\\1\\\66\\\1\\\21\\\1\\\40\\\1\\\7\\\1\\\47\\\1\\\42\\\1\\\3\\\1\\\45\\\1\\\56\\\1\\\52\\\1\\\43\\\1\\\65\\\1\\\55\\\1\\\54\\\1\\\60\\\1\\\41\\\1\\\36\\\1\\\57\\\1\\\67\\\1\\\37\\\1\\\50\\\1\\\44\\\1\\\46\\\1\\\62\\\1\\\53\\\1\\\6\\\1\\\51\\\1\\\10\\\1\\\22\\\1\\\34\\\1\\\70\\\1\\\13\\\uff81\\\10\";"; //$NON-NLS-1$
    private static final String PHPLEXER_METHOD_UNPACKCMAP_PHP53 = PHPASTLEXER_METHOD_UNPACKCMAP_PHP53;
    private static final String PHPLEXER_FIELD_CMAP_PHP53 = PHPASTLEXER_FIELD_CMAP_PHP53;
    private static final String PHPASTLEXER_FIELD_CMAP_PACKED_PHP5 =
"private static final String ZZ_CMAP_PACKED_MULTIBYTECHARACTERS = \"\\\11\\\0\\\1\\\12\\\1\\\13\\\2\\\0\\\1\\\11\\\22\\\0\\\1\\\12\\\1\\\57\\\1\\\15\\\1\\\100\\\1\\\14\\\1\\\63\\\1\\\64\\\1\\\77\\\1\\\50\\\1\\\52\\\1\\\61\\\1\\\4\\\1\\\67\\\1\\\44\\\1\\\2\\\1\\\62\\\1\\\5\\\11\\\1\\\1\\\46\\\1\\\21\\\1\\\60\\\1\\\56\\\1\\\45\\\1\\\73\\\1\\\74\\\1\\\35\\\1\\\7\\\1\\\30\\\1\\\24\\\1\\\3\\\1\\\25\\\1\\\51\\\1\\\36\\\1\\\22\\\1\\\53\\\1\\\41\\\1\\\40\\\1\\\43\\\1\\\27\\\1\\\31\\\1\\\42\\\1\\\55\\\1\\\33\\\1\\\32\\\1\\\23\\\1\\\26\\\1\\\47\\\1\\\37\\\1\\\6\\\1\\\34\\\1\\\10\\\1\\\70\\\1\\\16\\\1\\\71\\\1\\\66\\\1\\\54\\\1\\\17\\\1\\\35\\\1\\\7\\\1\\\30\\\1\\\24\\\1\\\3\\\1\\\25\\\1\\\51\\\1\\\36\\\1\\\22\\\1\\\53\\\1\\\41\\\1\\\40\\\1\\\43\\\1\\\27\\\1\\\31\\\1\\\42\\\1\\\55\\\1\\\33\\\1\\\76\\\1\\\23\\\1\\\26\\\1\\\47\\\1\\\37\\\1\\\6\\\1\\\34\\\1\\\10\\\1\\\20\\\1\\\65\\\1\\\75\\\1\\\72\\\uff81\\\10\";"; //$NON-NLS-1$
    private static final String PHPASTLEXER_METHOD_UNPACKCMAP_PHP5 = PHPASTLEXER_METHOD_UNPACKCMAP_PHP53;
    private static final String PHPASTLEXER_FIELD_CMAP_PHP5 = PHPASTLEXER_FIELD_CMAP_PHP53;
    private static final String PHPLEXER_FIELD_CMAP_PACKED_PHP5 =
"private static final String ZZ_CMAP_PACKED_MULTIBYTECHARACTERS = \"\\\11\\\0\\\1\\\12\\\1\\\15\\\2\\\0\\\1\\\11\\\22\\\0\\\1\\\12\\\1\\\26\\\1\\\17\\\1\\\74\\\1\\\16\\\1\\\32\\\1\\\33\\\1\\\73\\\1\\\62\\\1\\\64\\\1\\\30\\\1\\\25\\\1\\\13\\\1\\\4\\\1\\\2\\\1\\\31\\\1\\\5\\\11\\\1\\\1\\\60\\\1\\\14\\\1\\\27\\\1\\\23\\\1\\\24\\\1\\\75\\\1\\\76\\\1\\\40\\\1\\\7\\\1\\\47\\\1\\\42\\\1\\\3\\\1\\\45\\\1\\\63\\\1\\\52\\\1\\\43\\\1\\\65\\\1\\\55\\\1\\\54\\\1\\\57\\\1\\\41\\\1\\\36\\\1\\\56\\\1\\\67\\\1\\\37\\\1\\\50\\\1\\\44\\\1\\\46\\\1\\\61\\\1\\\53\\\1\\\6\\\1\\\51\\\1\\\10\\\1\\\71\\\1\\\20\\\1\\\72\\\1\\\35\\\1\\\66\\\1\\\21\\\1\\\40\\\1\\\7\\\1\\\47\\\1\\\42\\\1\\\3\\\1\\\45\\\1\\\63\\\1\\\52\\\1\\\43\\\1\\\65\\\1\\\55\\\1\\\54\\\1\\\57\\\1\\\41\\\1\\\36\\\1\\\56\\\1\\\67\\\1\\\37\\\1\\\50\\\1\\\44\\\1\\\46\\\1\\\61\\\1\\\53\\\1\\\6\\\1\\\51\\\1\\\10\\\1\\\22\\\1\\\34\\\1\\\70\\\1\\\13\\\uff81\\\10\";"; //$NON-NLS-1$
    private static final String PHPLEXER_METHOD_UNPACKCMAP_PHP5 = PHPASTLEXER_METHOD_UNPACKCMAP_PHP53;
    private static final String PHPLEXER_FIELD_CMAP_PHP5 = PHPASTLEXER_FIELD_CMAP_PHP53;
    private static final String PHPASTLEXER_FIELD_CMAP_PACKED_PHP4 =
"private static final String ZZ_CMAP_PACKED_MULTIBYTECHARACTERS = \"\\\11\\\0\\\1\\\12\\\1\\\14\\\2\\\0\\\1\\\11\\\22\\\0\\\1\\\12\\\1\\\52\\\1\\\75\\\1\\\13\\\1\\\67\\\1\\\56\\\1\\\57\\\1\\\76\\\1\\\42\\\1\\\44\\\1\\\54\\\1\\\4\\\1\\\63\\\1\\\36\\\1\\\2\\\1\\\55\\\1\\\5\\\7\\\101\\\2\\\1\\\1\\\40\\\1\\\62\\\1\\\53\\\1\\\51\\\1\\\37\\\1\\\70\\\1\\\71\\\1\\\33\\\1\\\7\\\1\\\26\\\1\\\17\\\1\\\3\\\1\\\23\\\1\\\43\\\1\\\32\\\1\\\15\\\1\\\46\\\1\\\34\\\1\\\21\\\1\\\50\\\1\\\25\\\1\\\20\\\1\\\35\\\1\\\47\\\1\\\30\\\1\\\27\\\1\\\16\\\1\\\24\\\1\\\41\\\1\\\31\\\1\\\6\\\1\\\45\\\1\\\10\\\1\\\64\\\1\\\77\\\1\\\65\\\1\\\61\\\1\\\22\\\1\\\100\\\1\\\33\\\1\\\7\\\1\\\26\\\1\\\17\\\1\\\3\\\1\\\23\\\1\\\43\\\1\\\32\\\1\\\15\\\1\\\46\\\1\\\34\\\1\\\21\\\1\\\50\\\1\\\25\\\1\\\20\\\1\\\35\\\1\\\47\\\1\\\30\\\1\\\74\\\1\\\16\\\1\\\24\\\1\\\41\\\1\\\31\\\1\\\6\\\1\\\45\\\1\\\10\\\1\\\72\\\1\\\60\\\1\\\73\\\1\\\66\\\uff81\\\10\";"; //$NON-NLS-1$
    private static final String PHPASTLEXER_METHOD_UNPACKCMAP_PHP4 = generateZZUnpackCMapMethod(190);
    private static final String PHPASTLEXER_FIELD_CMAP_PHP4 = PHPASTLEXER_FIELD_CMAP_PHP53;
    private static final String PHPLEXER_FIELD_CMAP_PACKED_PHP4 =
"private static final String ZZ_CMAP_PACKED_MULTIBYTECHARACTERS = \"\\\11\\\0\\\1\\\12\\\1\\\20\\\2\\\0\\\1\\\11\\\22\\\0\\\1\\\12\\\1\\\24\\\1\\\73\\\1\\\70\\\1\\\67\\\1\\\30\\\1\\\31\\\1\\\17\\\1\\\57\\\1\\\61\\\1\\\26\\\1\\\23\\\1\\\13\\\1\\\4\\\1\\\2\\\1\\\27\\\1\\\5\\\7\\\76\\\2\\\1\\\1\\\55\\\1\\\14\\\1\\\25\\\1\\\21\\\1\\\22\\\1\\\71\\\1\\\72\\\1\\\36\\\1\\\7\\\1\\\45\\\1\\\40\\\1\\\3\\\1\\\43\\\1\\\60\\\1\\\52\\\1\\\41\\\1\\\63\\\1\\\53\\\1\\\46\\\1\\\65\\\1\\\37\\\1\\\34\\\1\\\54\\\1\\\64\\\1\\\35\\\1\\\50\\\1\\\42\\\1\\\44\\\1\\\56\\\1\\\51\\\1\\\6\\\1\\\62\\\1\\\10\\\1\\\16\\\1\\\74\\\1\\\16\\\1\\\33\\\1\\\47\\\1\\\75\\\1\\\36\\\1\\\7\\\1\\\45\\\1\\\40\\\1\\\3\\\1\\\43\\\1\\\60\\\1\\\52\\\1\\\41\\\1\\\63\\\1\\\53\\\1\\\46\\\1\\\65\\\1\\\37\\\1\\\34\\\1\\\54\\\1\\\64\\\1\\\35\\\1\\\50\\\1\\\42\\\1\\\44\\\1\\\56\\\1\\\51\\\1\\\6\\\1\\\62\\\1\\\10\\\1\\\66\\\1\\\32\\\1\\\15\\\1\\\13\\\uff81\\\10\";"; //$NON-NLS-1$
    private static final String PHPLEXER_METHOD_UNPACKCMAP_PHP4 = PHPASTLEXER_METHOD_UNPACKCMAP_PHP4;
    private static final String PHPLEXER_FIELD_CMAP_PHP4 = PHPASTLEXER_FIELD_CMAP_PHP53;

    public MultibyteCharactersAspect() {
        super();
    }

    @Override
    protected void doWeave() throws NotFoundException, CannotCompileException {
        doWeavePHP53();
        doWeavePHP5();
        doWeavePHP4();
    }

    @Override
    protected String[] joinPoints() {
        return JOINPOINTS;
    }

    @Override
    protected String[] weavingClasses() {
        return WEAVINGCLASSES;
    }

    private void doWeavePHP53() throws NotFoundException, CannotCompileException {
        CtClass phpAstLexerClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPASTLEXER_PHP53);
        phpAstLexerClass.addField(CtField.make(PHPASTLEXER_FIELD_CMAP_PACKED_PHP53, phpAstLexerClass));
        phpAstLexerClass.addMethod(CtNewMethod.make(PHPASTLEXER_METHOD_UNPACKCMAP_PHP53, phpAstLexerClass));
        phpAstLexerClass.addField(CtField.make(PHPASTLEXER_FIELD_CMAP_PHP53, phpAstLexerClass));
        phpAstLexerClass.getDeclaredMethod("next_token").instrument( //$NON-NLS-1$
            new ExprEditor() {
                public void edit(FieldAccess fieldAccess) throws CannotCompileException {
                    if ("ZZ_CMAP".equals(fieldAccess.getFieldName())) { //$NON-NLS-1$
                        fieldAccess.replace("$_ = ZZ_CMAP_MULTIBYTECHARACTERS;"); //$NON-NLS-1$
                        markJoinPointAsPassed(JOINPOINT_PHPASTLEXER_ACCESS_CMAP_PHP53);
                    }
                }
            }
        );
        markClassAsWoven(phpAstLexerClass);

        CtClass phpLexerClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPLEXER_PHP53);
        phpLexerClass.addField(CtField.make(PHPLEXER_FIELD_CMAP_PACKED_PHP53, phpLexerClass));
        phpLexerClass.addMethod(CtNewMethod.make(PHPLEXER_METHOD_UNPACKCMAP_PHP53, phpLexerClass));
        phpLexerClass.addField(CtField.make(PHPLEXER_FIELD_CMAP_PHP53, phpLexerClass));
        phpLexerClass.getDeclaredMethod("yylex").instrument( //$NON-NLS-1$
            new ExprEditor() {
                public void edit(FieldAccess fieldAccess) throws CannotCompileException {
                    if ("ZZ_CMAP".equals(fieldAccess.getFieldName())) { //$NON-NLS-1$
                        fieldAccess.replace("$_ = ZZ_CMAP_MULTIBYTECHARACTERS;"); //$NON-NLS-1$
                        markJoinPointAsPassed(JOINPOINT_PHPLEXER_ACCESS_CMAP_PHP53);
                    }
                }
            }
        );
        markClassAsWoven(phpLexerClass);
    }

    private void doWeavePHP5() throws NotFoundException, CannotCompileException {
        CtClass phpAstLexerClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPASTLEXER_PHP5);
        phpAstLexerClass.addField(CtField.make(PHPASTLEXER_FIELD_CMAP_PACKED_PHP5, phpAstLexerClass));
        phpAstLexerClass.addMethod(CtNewMethod.make(PHPASTLEXER_METHOD_UNPACKCMAP_PHP5, phpAstLexerClass));
        phpAstLexerClass.addField(CtField.make(PHPASTLEXER_FIELD_CMAP_PHP5, phpAstLexerClass));
        phpAstLexerClass.getDeclaredMethod("next_token").instrument( //$NON-NLS-1$
            new ExprEditor() {
                public void edit(FieldAccess fieldAccess) throws CannotCompileException {
                    if ("ZZ_CMAP".equals(fieldAccess.getFieldName())) { //$NON-NLS-1$
                        fieldAccess.replace("$_ = ZZ_CMAP_MULTIBYTECHARACTERS;"); //$NON-NLS-1$
                        markJoinPointAsPassed(JOINPOINT_PHPASTLEXER_ACCESS_CMAP_PHP5);
                    }
                }
            }
        );
        markClassAsWoven(phpAstLexerClass);

        CtClass phpLexerClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPLEXER_PHP5);
        phpLexerClass.addField(CtField.make(PHPLEXER_FIELD_CMAP_PACKED_PHP5, phpLexerClass));
        phpLexerClass.addMethod(CtNewMethod.make(PHPLEXER_METHOD_UNPACKCMAP_PHP5, phpLexerClass));
        phpLexerClass.addField(CtField.make(PHPLEXER_FIELD_CMAP_PHP5, phpLexerClass));
        phpLexerClass.getDeclaredMethod("yylex").instrument( //$NON-NLS-1$
            new ExprEditor() {
                public void edit(FieldAccess fieldAccess) throws CannotCompileException {
                    if ("ZZ_CMAP".equals(fieldAccess.getFieldName())) { //$NON-NLS-1$
                        fieldAccess.replace("$_ = ZZ_CMAP_MULTIBYTECHARACTERS;"); //$NON-NLS-1$
                        markJoinPointAsPassed(JOINPOINT_PHPLEXER_ACCESS_CMAP_PHP5);
                    }
                }
            }
        );
        markClassAsWoven(phpLexerClass);
    }

    private void doWeavePHP4() throws NotFoundException, CannotCompileException {
        CtClass phpAstLexerClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPASTLEXER_PHP4);
        phpAstLexerClass.addField(CtField.make(PHPASTLEXER_FIELD_CMAP_PACKED_PHP4, phpAstLexerClass));
        phpAstLexerClass.addMethod(CtNewMethod.make(PHPASTLEXER_METHOD_UNPACKCMAP_PHP4, phpAstLexerClass));
        phpAstLexerClass.addField(CtField.make(PHPASTLEXER_FIELD_CMAP_PHP4, phpAstLexerClass));
        phpAstLexerClass.getDeclaredMethod("next_token").instrument( //$NON-NLS-1$
            new ExprEditor() {
                public void edit(FieldAccess fieldAccess) throws CannotCompileException {
                    if ("ZZ_CMAP".equals(fieldAccess.getFieldName())) { //$NON-NLS-1$
                        fieldAccess.replace("$_ = ZZ_CMAP_MULTIBYTECHARACTERS;"); //$NON-NLS-1$
                        markJoinPointAsPassed(JOINPOINT_PHPASTLEXER_ACCESS_CMAP_PHP4);
                    }
                }
            }
        );
        markClassAsWoven(phpAstLexerClass);

        CtClass phpLexerClass = ClassPool.getDefault().get(WEAVINGCLASS_PHPLEXER_PHP4);
        phpLexerClass.addField(CtField.make(PHPLEXER_FIELD_CMAP_PACKED_PHP4, phpLexerClass));
        phpLexerClass.addMethod(CtNewMethod.make(PHPLEXER_METHOD_UNPACKCMAP_PHP4, phpLexerClass));
        phpLexerClass.addField(CtField.make(PHPLEXER_FIELD_CMAP_PHP4, phpLexerClass));
        phpLexerClass.getDeclaredMethod("yylex").instrument( //$NON-NLS-1$
            new ExprEditor() {
                public void edit(FieldAccess fieldAccess) throws CannotCompileException {
                    if ("ZZ_CMAP".equals(fieldAccess.getFieldName())) { //$NON-NLS-1$
                        fieldAccess.replace("$_ = ZZ_CMAP_MULTIBYTECHARACTERS;"); //$NON-NLS-1$
                        markJoinPointAsPassed(JOINPOINT_PHPLEXER_ACCESS_CMAP_PHP4);
                    }
                }
            }
        );
        markClassAsWoven(phpLexerClass);
    }

    private static String generateZZUnpackCMapMethod(int loopCount)
    {
        return
"private static char [] zzUnpackCMapMultibyteCharacters(String packed) {" + //$NON-NLS-1$
"    char [] map = new char[0x10000];" + //$NON-NLS-1$
"    int i = 0;" + //$NON-NLS-1$
"    int j = 0;" + //$NON-NLS-1$
"    while (i < " + loopCount + ") {" + //$NON-NLS-1$ //$NON-NLS-2$
"        int  count = packed.charAt(i++);" + //$NON-NLS-1$
"        char value = packed.charAt(i++);" + //$NON-NLS-1$
"        do map[j++] = value; while (--count > 0);" + //$NON-NLS-1$
"    }" + //$NON-NLS-1$
"    return map;" + //$NON-NLS-1$
"}"; //$NON-NLS-1$
    }
}
