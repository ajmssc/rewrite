package com.netflix.java.refactor.search

import com.netflix.java.refactor.ast.*

data class Field(val name: String, val type: String)

class FindFields(val clazz: String, val includeInherited: Boolean) : AstVisitor<List<Field>>(emptyList()) {

    override fun visitCompilationUnit(cu: Tr.CompilationUnit): List<Field> {
        super.visitCompilationUnit(cu)
        return cu.typeDecls.flatMap { superFields(it.type.asClass()) }
    }

    private fun superFields(type: Type.Class?, inHierarchy: Boolean = false): List<Field> {
        if(type == null)
            return emptyList()
        
        if (type.supertype == null)
            return emptyList()

        val fields = type.members
                .filter { !inHierarchy || !it.hasFlags(Type.Var.Flags.Private) }
                // FIXME will not match on arrays of this type, as they would be Type.Array
                .filter { it.type is Type.Class }
                .map { Field(it.name, it.type!!.asClass()!!.fullyQualifiedName) }
        
        return fields + (if (includeInherited) superFields(type.supertype, true) else emptyList())
    }
}