package pam.mobile.usecase3fp.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.SupabaseClient

object SupabaseClientInstance {
    private const val SUPABASE_URL = "https://hglfbypvduopgddmwpgr.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhnbGZieXB2ZHVvcGdkZG13cGdyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzM0NzQ1MjksImV4cCI6MjA0OTA1MDUyOX0.sUxBIDXuVjuRHJlCxOW30w_H9Th5q0y"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Postgrest)
        }
    }
}