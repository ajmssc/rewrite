/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.rewrite.ast.visitor

import com.netflix.rewrite.ast.*
import java.util.*

open class AstVisitor<R> {
    var default: (Tree?) -> R

    constructor(default: R) {
        this.default = { default }
    }

    constructor(default: (Tree?) -> R) {
        this.default = default
    }

    /**
     * Some sensible defaults for reduce (boolean OR, list concatenation, or else just the value of r1).
     * Override if your particular visitor needs to reduce values in a different way.
     */
    @Suppress("UNUSED_PARAMETER", "UNCHECKED_CAST")
    open fun reduce(r1: R, r2: R): R = when (r1) {
        is Boolean -> (r1 || r2 as Boolean) as R
        is Iterable<*> -> r1.plus(r2 as Iterable<*>) as R
        else -> r1 ?: r2
    }

    private val cursorStack = Stack<Tree>()

    fun cursor(): Cursor = Cursor(cursorStack.toList())

    fun visit(tree: Tree?): R =
            if (tree != null) {
                cursorStack.push(tree)
                val t = reduce(tree.accept(this), visitTree(tree))
                cursorStack.pop()
                t
            } else default(tree)

    private fun R.andThen(nodes: Iterable<Tree>?): R =
            if (nodes != null) reduce(this, visit(nodes)) else this

    private fun R.andThen(node: Tree?): R = if (node != null) reduce(this, visit(node)) else this

    fun visit(nodes: Iterable<Tree>?): R =
            nodes?.let {
                var r: R = default(null)
                var first = true
                for (node in nodes) {
                    r = if (first) visit(node) else r.andThen(node)
                    first = false
                }
                r
            } ?: default(null)

    open fun visitTree(t: Tree): R = default(t)

    open fun visitAnnotation(annotation: Tr.Annotation): R {
        visitTypeName(annotation.annotationType)
        return visit(annotation.annotationType)
                .andThen(annotation.args?.args)
    }

    open fun visitArrayAccess(arrayAccess: Tr.ArrayAccess): R =
            visit(arrayAccess.indexed)
                    .andThen(arrayAccess.dimension.index)

    open fun visitArrayType(arrayType: Tr.ArrayType): R {
        visitTypeName(arrayType.elementType)
        return visit(arrayType.elementType)
    }

    open fun visitAssert(assert: Tr.Assert): R =
            visit(assert.condition)

    open fun visitAssign(assign: Tr.Assign): R =
            visit(assign.variable)
                    .andThen(assign.assignment)

    open fun visitAssignOp(assign: Tr.AssignOp): R =
            visit(assign.variable)
                    .andThen(assign.assignment)

    open fun visitBinary(binary: Tr.Binary): R =
            visit(binary.left)
                    .andThen(binary.right)

    open fun visitBlock(block: Tr.Block<Tree>): R =
            visit(block.statements)

    open fun visitBreak(breakStatement: Tr.Break): R =
            visit(breakStatement.label)

    open fun visitCase(case: Tr.Case): R =
            visit(case.pattern)
                    .andThen(case.statements)

    open fun visitCatch(catch: Tr.Catch): R =
            visit(catch.param)
                    .andThen(catch.body)

    open fun visitClassDecl(classDecl: Tr.ClassDecl): R {
        visitTypeNameIfNonNull(classDecl.extends)
        classDecl.implements.forEach { visitTypeName(it) }
        return visit(classDecl.annotations)
                .andThen(classDecl.modifiers)
                .andThen(classDecl.name)
                .andThen(classDecl.typeParams?.params)
                .andThen(classDecl.extends)
                .andThen(classDecl.implements)
                .andThen(classDecl.body)
    }

    open fun visitCompilationUnit(cu: Tr.CompilationUnit): R = reduce(
            visit(cu.imports)
                    .andThen(cu.packageDecl)
                    .andThen(cu.classes),
            visitEnd()
    )

    open fun visitContinue(continueStatement: Tr.Continue): R =
            visit(continueStatement.label)

    open fun visitDoWhileLoop(doWhileLoop: Tr.DoWhileLoop): R =
            visit(doWhileLoop.condition)
                    .andThen(doWhileLoop.body)

    open fun visitEmpty(empty: Tr.Empty): R = default(empty)

    open fun visitEnd() = default(null)

    open fun visitEnumValue(enum: Tr.EnumValue): R =
            visit(enum.name)
                    .andThen(enum.initializer?.args)

    open fun visitEnumValueSet(enums: Tr.EnumValueSet): R =
            visit(enums.enums)

    open fun visitExpression(expr: Expression): R = default(null)

    open fun visitFieldAccess(field: Tr.FieldAccess): R =
            visit(field.target)
                    .andThen(field.name)

    open fun visitForLoop(forLoop: Tr.ForLoop) =
            visit(forLoop.control.init)
                    .andThen(forLoop.control.condition)
                    .andThen(forLoop.control.update)
                    .andThen(forLoop.body)

    open fun visitForEachLoop(forEachLoop: Tr.ForEachLoop): R =
            visit(forEachLoop.control.variable)
                    .andThen(forEachLoop.control.iterable)
                    .andThen(forEachLoop.body)

    open fun visitIdentifier(ident: Tr.Ident): R = default(ident)

    open fun visitIf(iff: Tr.If): R =
            visit(iff.ifCondition)
                    .andThen(iff.thenPart)
                    .andThen(iff.elsePart)

    open fun visitImport(import: Tr.Import): R =
            visit(import.qualid)

    open fun visitInstanceOf(instanceOf: Tr.InstanceOf): R =
            visit(instanceOf.expr)
                    .andThen(instanceOf.clazz)

    open fun visitLabel(label: Tr.Label): R =
            visit(label.label)
                    .andThen(label.statement)

    open fun visitLambda(lambda: Tr.Lambda): R =
            visit(lambda.paramSet.params)
                    .andThen(lambda.body)

    open fun visitLiteral(literal: Tr.Literal): R = default(literal)

    open fun visitMemberReference(memberRef: Tr.MemberReference): R =
            visit(memberRef.containing)
                    .andThen(memberRef.reference)

    open fun visitMethod(method: Tr.MethodDecl): R {
        visitTypeNameIfNonNull(method.returnTypeExpr)
        method.throws?.exceptions?.forEach { visitTypeName(it) }
        return visit(method.annotations)
                .andThen(method.modifiers)
                .andThen(method.typeParameters?.params)
                .andThen(method.returnTypeExpr)
                .andThen(method.name)
                .andThen(method.params.params)
                .andThen(method.throws?.exceptions)
                .andThen(method.body)
                .andThen(method.defaultValue)
    }

    open fun visitMethodInvocation(meth: Tr.MethodInvocation): R {
        if (meth.select is NameTree && meth.type?.hasFlags(Flag.Static) ?: false)
            visitTypeName(meth.select)
        meth.typeParameters?.params?.forEach { visitTypeName(it) }

        return visit(meth.select)
                .andThen(meth.typeParameters?.params)
                .andThen(meth.name)
                .andThen(meth.args.args)
    }

    open fun visitMultiCatch(multiCatch: Tr.MultiCatch): R {
        multiCatch.alternatives.forEach { visitTypeName(it) }
        return visit(multiCatch.alternatives)
    }

    open fun visitMultiVariable(multiVariable: Tr.VariableDecls): R {
        if(multiVariable.typeExpr !is Tr.MultiCatch) {
            visitTypeNameIfNonNull(multiVariable.typeExpr)
        }

        return visit(multiVariable.annotations)
                .andThen(multiVariable.modifiers)
                .andThen(multiVariable.typeExpr)
                .andThen(multiVariable.vars)
    }

    open fun visitNewArray(newArray: Tr.NewArray): R {
        visitTypeNameIfNonNull(newArray.typeExpr)
        return visit(newArray.typeExpr)
                .andThen(newArray.dimensions.map { it.size })
                .andThen(newArray.initializer?.elements)
    }

    open fun visitNewClass(newClass: Tr.NewClass): R {
        visitTypeName(newClass.clazz)
        return visit(newClass.clazz)
                .andThen(newClass.args.args)
                .andThen(newClass.classBody)
    }

    open fun visitPackage(pkg: Tr.Package): R =
            visit(pkg.expr)

    open fun visitParameterizedType(type: Tr.ParameterizedType): R {
        visitTypeName(type.clazz)
        type.typeArguments?.args?.filterIsInstance<NameTree>()?.forEach { visitTypeName(it) }
        return visit(type.clazz)
                .andThen(type.typeArguments?.args)
    }

    open fun <T: Tree> visitParentheses(parens: Tr.Parentheses<T>): R =
            visit(parens.tree)

    open fun visitPrimitive(primitive: Tr.Primitive): R =
            default(primitive)

    open fun visitReturn(retrn: Tr.Return): R =
            visit(retrn.expr)

    open fun visitSwitch(switch: Tr.Switch): R =
            visit(switch.selector)
                    .andThen(switch.cases)

    open fun visitSynchronized(synch: Tr.Synchronized): R =
            visit(synch.lock)
                    .andThen(synch.body)

    open fun visitTernary(ternary: Tr.Ternary): R =
            visit(ternary.condition)
                    .andThen(ternary.truePart)
                    .andThen(ternary.falsePart)

    open fun visitThrow(thrown: Tr.Throw): R = visit(thrown.exception)

    open fun visitTry(tryable: Tr.Try): R =
            visit(tryable.resources?.decls)
                    .andThen(tryable.body)
                    .andThen(tryable.catches)
                    .andThen(tryable.finally?.block)

    open fun visitTypeCast(typeCast: Tr.TypeCast): R {
        visitTypeName(typeCast.clazz.tree)
        return visit(typeCast.clazz)
                .andThen(typeCast.expr)
    }

    open fun visitTypeParameter(typeParameter: Tr.TypeParameter): R {
        typeParameter.bounds?.types?.forEach { visitTypeName(it) }
        return visit(typeParameter.annotations)
                .andThen(typeParameter.name)
                .andThen(typeParameter.bounds?.types)
    }

    open fun visitTypeParameters(typeParameters: Tr.TypeParameters): R =
            visit(typeParameters.params)

    private fun visitTypeNameIfNonNull(name: NameTree?): R =
            name?.let { visitTypeName(it) } ?: default(null)

    open fun visitTypeName(name: NameTree): R = default(null)

    open fun visitUnary(unary: Tr.Unary): R = visit(unary.expr)

    open fun visitUnparsedSource(unparsed: Tr.UnparsedSource): R =
            default(null)

    open fun visitVariable(variable: Tr.VariableDecls.NamedVar): R =
            visit(variable.name)
                    .andThen(variable.dimensionsAfterName)
                    .andThen(variable.initializer)

    open fun visitWhileLoop(whileLoop: Tr.WhileLoop): R =
            visit(whileLoop.condition)
                    .andThen(whileLoop.body)

    open fun visitWildcard(wildcard: Tr.Wildcard): R {
        visitTypeNameIfNonNull(wildcard.boundedType)
        return visit(wildcard.bound)
                .andThen(wildcard.boundedType)
    }
}