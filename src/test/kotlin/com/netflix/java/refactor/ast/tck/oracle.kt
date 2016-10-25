package com.netflix.java.refactor.ast.tck

import com.netflix.java.refactor.ast.*
import com.netflix.java.refactor.parse.OracleJdkParser

class OracleParserCompilationUnitTest: CompilationUnitTest(OracleJdkParser())
class OracleParserImportTest: ImportTest(OracleJdkParser())
class OracleParserNewClassTest: NewClassTest(OracleJdkParser())
class OracleParserClassDeclTest: ClassDeclTest(OracleJdkParser())
class OracleParserLiteralTest: LiteralTest(OracleJdkParser())
class OracleParserIdentTest: IdentTest(OracleJdkParser())
class OracleParserFieldAccessTest: FieldAccessTest(OracleJdkParser())
class OracleParserMethodDeclTest: MethodDeclTest(OracleJdkParser())
class OracleParserPrimitiveTest: PrimitiveTest(OracleJdkParser())
class OracleParserBlockTest : BlockTest(OracleJdkParser())
class OracleParserMethodInvocationTest: MethodInvocationTest(OracleJdkParser())
class OracleParserBinaryTest : BinaryTest(OracleJdkParser())
class OracleParserUnaryTest: UnaryTest(OracleJdkParser())
class OracleParserForLoopTest: ForLoopTest(OracleJdkParser())
class OracleParserForEachLoopTest: ForEachLoopTest(OracleJdkParser())
class OracleParserIfTest: IfTest(OracleJdkParser())
class OracleParserTernaryTest: TernaryTest(OracleJdkParser())
class OracleParserWhileLoopTest: WhileLoopTest(OracleJdkParser())
class OracleParserDoWhileLoopTest: DoWhileLoopTest(OracleJdkParser())
class OracleParserBreakTest: BreakTest(OracleJdkParser())
class OracleParserContinueTest: ContinueTest(OracleJdkParser())
class OracleParserLabelTest: LabelTest(OracleJdkParser())
class OracleParserVariableDeclTest: VariableDeclTest(OracleJdkParser())
class OracleParserReturnTest: ReturnTest(OracleJdkParser())
class OracleParserSwitchTest: SwitchTest(OracleJdkParser())
class OracleParserAssignTest: AssignTest(OracleJdkParser())
class OracleParserThrowTest: ThrowTest(OracleJdkParser())
class OracleParserTryTest: TryTest(OracleJdkParser())
class OracleParserSynchronizedTest: SynchronizedTest(OracleJdkParser())
class OracleParserEmptyTest: EmptyTest(OracleJdkParser())
class OracleParserParenthesesTest: ParenthesesTest(OracleJdkParser())
class OracleParserAssignOpTest: AssignOpTest(OracleJdkParser())
class OracleParserInstanceOfTest: InstanceOfTest(OracleJdkParser())
class OracleParserNewArrayTest: NewArrayTest(OracleJdkParser())
class OracleParserArrayAccessTest: ArrayAccessTest(OracleJdkParser())
class OracleParserLambdaTest: LambdaTest(OracleJdkParser())
class OracleCyclicTypeTest: CyclicTypeTest(OracleJdkParser())
class OracleAnnotationTest: AnnotationTest(OracleJdkParser())
class OracleEnumTest: EnumTest(OracleJdkParser())