package com.example.sorms_app.data.utils

/**
 * Utility functions for role mapping and management
 * Đồng bộ với web app: mapRoleToAppRole function
 */
object RoleUtils {
    /**
     * Map backend role to app role
     * Đồng bộ với web: src/lib/auth-service.ts mapRoleToAppRole()
     */
    fun mapRoleToAppRole(role: String?): AppRole {
        if (role == null || role.isBlank()) return AppRole.USER
        
        val normalizedRole = role.trim().uppercase().let { r ->
            if (r.startsWith("ROLE_")) r.substring(5) else r
        }
        
        return when (normalizedRole) {
            "ADMIN_SYSTEM" -> AppRole.ADMIN
            "ADMINISTRATIVE" -> AppRole.OFFICE
            "STAFF" -> AppRole.STAFF
            "USER" -> AppRole.USER
            else -> AppRole.USER
        }
    }
    
    /**
     * Check if user has admin role
     */
    fun isAdmin(role: String?): Boolean {
        return mapRoleToAppRole(role) == AppRole.ADMIN
    }
    
    /**
     * Check if user has staff role
     */
    fun isStaff(role: String?): Boolean {
        return mapRoleToAppRole(role) == AppRole.STAFF
    }
    
    /**
     * Check if user has office role
     */
    fun isOffice(role: String?): Boolean {
        return mapRoleToAppRole(role) == AppRole.OFFICE
    }
}

/**
 * App role enum - đồng bộ với web app
 */
enum class AppRole {
    ADMIN,      // ADMIN_SYSTEM
    OFFICE,     // ADMINISTRATIVE
    STAFF,      // STAFF
    USER        // USER
}

